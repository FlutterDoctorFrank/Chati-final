package model.context.spatial;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import controller.network.ClientSender.SendAction;
import model.communication.message.TextMessage;
import model.role.Role;
import model.user.User;
import org.jetbrains.annotations.NotNull;
import java.util.BitSet;
import java.util.NoSuchElementException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class Room extends Area implements IRoom {

    /** Karte dieses Raums. */
    private final ContextMap map;

    /** Enthält Information über erlaubte Positionen auf der Karte. */
    private BitSet collisionsMap;

    /** Anfangsposition auf der Karte. */
    private Location spawnLocation;

    /** Information, ob dieser Kontext privat, oder öffentlich zugänglich ist. */
    private boolean isPrivate;

    /** Passwort des Kontextes, im Falle eines privaten Kontextes. */
    private String password;

    /**
     * Erzeugt eine Instanz eines (öffentlichen) Raums.
     * @param roomName Name des Raums.
     * @param world Übergeordnete Welt.
     * @param map Karte des Raums.
     */
    protected Room(@NotNull final String roomName, @NotNull final World world,
                   @NotNull final ContextMap map) {
        super(roomName, world, world, null, null, null);
        this.map = map;
        this.isPrivate = false;
        this.password = null;
    }

    /**
     * Erzeugt eine Instanz eines (privaten) Raums.
     * @param roomName Name des Raums.
     * @param world Übergeordnete Welt.
     * @param map Karte des Raums.
     * @param password Passwort des Raums.
     */
    public Room(@NotNull final String roomName, @NotNull final World world,
                @NotNull final ContextMap map, @NotNull final String password) {
        this(roomName, world, map);
        this.isPrivate = true;
        this.password = password;
    }

    @Override
    public @NotNull ContextMap getMap() {
        return map;
    }

    @Override
    public boolean isPrivate() {
        return isPrivate;
    }

    /**
     * Überprüft, ob ein übergebenes Passwort mit dem Passwort dieses Kontextes übereinstimmt.
     * @param password Zu überprüfendes Passwort.
     * @return true, wenn das Passwort übereinstimmt oder false, wenn dieser Kontext kein Passwort hat oder es nicht
     * übereinstimmt.
     */
    public boolean checkPassword(@NotNull final String password) {
        return this.password != null && this.password.equals(password);
    }

    /**
     * Gibt zurück, ob die Position auf der Karte dieses Kontextes an den übergebenen Koordinaten erlaubt ist.
     * @param posX X-Koordinate.
     * @param posY Y-Koordinate.
     * @return true, wenn die Position erlaubt ist, sonst false.
     */
    public boolean isLegal(final float posX, final float posY) {
        if (0 <= posX && posX <= this.expanse.getWidth() && 0 <= posY && posY <= this.expanse.getHeight()) {
            return !this.collisionsMap.get(Math.round(posY) * this.expanse.getWidth() + Math.round(posX));
        }
        return false;
    }

    /**
     * Gibt die Anfangsposition auf der Karte dieses Kontextes zurück.
     * @return Anfangsposition auf der Karte dieses Kontextes.
     */
    public @NotNull Location getSpawnLocation() {
        return spawnLocation;
    }

    @Override
    public void addUser(@NotNull final User user) {
        if (!contains(user)) {
            world.addUser(user);

            user.send(SendAction.CONTEXT_JOIN, this);
            super.addUser(user);
            user.updateUserInfo(true);
            user.updateCommunicableUsers();
            user.getRoomRoles().values().forEach(role -> user.send(SendAction.CONTEXT_ROLE, role));

            // Sende die Positionen der anderen Benutzer an den beitretenden Benutzer.
            containedUsers.values().stream().filter(other -> !other.equals(user)).forEach(other -> {
                user.send(SendAction.AVATAR_SPAWN, other);
                // Versenden der Raumrollen.
                other.getRoomRoles().values().forEach(role -> user.send(SendAction.CONTEXT_ROLE, role));
                user.getRoomRoles().values().forEach(role -> other.send(SendAction.CONTEXT_ROLE, role));
            });

            if (isPrivate) {
                final TextMessage info = new TextMessage("context.room.joined", user.getUsername());
                containedUsers.values().stream()
                        .filter(receiver -> !receiver.equals(user))
                        .forEach(receiver -> receiver.send(SendAction.MESSAGE, info));
            }
        }
    }

    @Override
    public void removeUser(@NotNull final User user) {
        if (contains(user)) {
            super.removeUser(user);
            user.setMovable(true);
            user.setCurrentInteractable(null);

            containedUsers.values().forEach(receiver -> receiver.send(SendAction.AVATAR_REMOVE, user));

            if (isPrivate) {
                if (!containedUsers.isEmpty()) {
                    final TextMessage info = new TextMessage("context.room.left", user.getUsername());

                    containedUsers.values().forEach(receiver -> receiver.send(SendAction.MESSAGE, info));

                    if (user.hasRole(this, Role.ROOM_OWNER)) {
                        user.removeRole(this, Role.ROOM_OWNER);

                        try {
                            containedUsers.values().stream().findAny().orElseThrow().addRole(this, Role.ROOM_OWNER);
                        } catch (NoSuchElementException ex) {
                            throw new IllegalStateException("Unable to find new room owner", ex);
                        }
                    }
                } else {
                    world.removePrivateRoom(this);
                }
            }
        }
    }

    /**
     * Setzt die Parameter dieses Raums, die in der Datenstruktur der Karte enthalten sind.
     * @see MapUtils
     */
    public void build() {
        if (Gdx.app == null) {
            throw new IllegalStateException("LibGDX environment is not available");
        }

        FutureTask<?> buildTask = new FutureTask<>(() -> {
            TiledMap tiledMap = new TmxMapLoader().load(map.getPath());

            expanse = MapUtils.createMapExpanse(Room.this, tiledMap);
            spawnLocation = MapUtils.parseLocation(Room.this, tiledMap.getProperties());
            communicationRegion = MapUtils.parseCommunication(tiledMap.getProperties());
            communicationRegion.setArea(Room.this);
            communicationMedia = MapUtils.parseMedia(tiledMap.getProperties());
            collisionsMap = MapUtils.createCollisionMap(Room.this, tiledMap.getLayers().get("Collisions"));
            MapUtils.createContexts(Room.this, tiledMap.getLayers().get("Contexts"));
        }, null);
        Gdx.app.postRunnable(buildTask);
        try {
            buildTask.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}