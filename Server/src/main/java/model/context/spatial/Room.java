package model.context.spatial;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import controller.network.ClientSender.SendAction;
import model.communication.message.TextMessage;
import model.role.Role;
import model.user.User;
import org.jetbrains.annotations.NotNull;
import java.util.NoSuchElementException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class Room extends Area implements IRoom {

    /** Karte dieses Raums. */
    private final ContextMap map;

    /** Enthält Information über erlaubte Positionen auf der Karte. */
    private boolean[][] collisionMap;

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
        return !(this.password == null) && this.password.equals(password);
    }

    /**
     * Gibt zurück, ob die Position auf der Karte dieses Kontextes an den übergebenen Koordinaten erlaubt ist.
     * @param posX X-Koordinate.
     * @param posY Y-Koordinate.
     * @return true, wenn die Position erlaubt ist, sonst false.
     */
    public boolean isLegal(final float posX, final float posY) {
        return true; // !collisionMap[posX][posY];
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
            user.send(SendAction.CONTEXT_JOIN, this);
            super.addUser(user);
            user.teleport(spawnLocation);

            user.updateUserInfo(true);
            user.getRoomRoles().values().forEach(role -> user.send(SendAction.CONTEXT_ROLE, role));

            // Sende die Positionen der anderen Benutzer an den beitretenden Benutzer.
            containedUsers.values().stream().filter(other -> !other.equals(user)).forEach(other -> {
                user.send(SendAction.AVATAR_SPAWN, other);
                // Versenden der Raumrollen.
                user.getRoomRoles().values().forEach(role -> other.send(SendAction.CONTEXT_ROLE, role));
                other.getRoomRoles().values().forEach(role -> user.send(SendAction.CONTEXT_ROLE, role));
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
            user.teleport(null);

            containedUsers.values().forEach(receiver -> receiver.send(SendAction.AVATAR_REMOVE, user));

            if (isPrivate && user.hasRole(this, Role.ROOM_OWNER)) {
                user.removeRole(this, Role.ROOM_OWNER);

                if (!containedUsers.isEmpty()) {
                    final TextMessage info = new TextMessage("context.room.left", user.getUsername());

                    containedUsers.values().forEach(receiver -> receiver.send(SendAction.MESSAGE, info));

                    try {
                        containedUsers.values().stream().findAny().orElseThrow().addRole(this, Role.ROOM_OWNER);
                    } catch (NoSuchElementException ex) {
                        throw new IllegalStateException("Unable to find new room owner", ex);
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
            communicationRegion = MapUtils.getCommunicationRegion(tiledMap.getProperties());
            communicationRegion.setArea(Room.this);
            communicationMedia = MapUtils.getCommunicationMedia(tiledMap.getProperties());
            expanse = new Expanse(new Location(Room.this, 0, 0), MapUtils.getWidth(tiledMap) , MapUtils.getHeight(tiledMap));
            //this.collisionMap = MapUtils.getCollisionMap(tiledMap);
            spawnLocation = new Location(Room.this, MapUtils.getSpawnPosX(tiledMap), MapUtils.getSpawnPosY(tiledMap));
            MapUtils.buildChildTree(Room.this, tiledMap);
        }, null);
        Gdx.app.postRunnable(buildTask);
        try {
            buildTask.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}