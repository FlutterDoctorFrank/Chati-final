package model.context.spatial;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import controller.network.ClientSender.SendAction;
import model.MessageBundle;
import model.communication.message.TextMessage;
import model.role.Role;
import model.user.User;
import org.jetbrains.annotations.NotNull;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class Room extends Area implements IRoom {

    /** Karte dieses Raums. */
    private final SpatialMap map;

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
                   @NotNull final SpatialMap map) {
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
                @NotNull final SpatialMap map, @NotNull final String password) {
        this(roomName, world, map);
        this.isPrivate = true;
        this.password = password;
    }

    @Override
    public @NotNull SpatialMap getMap() {
        return map;
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

    /**
     * Gibt zurück, ob dieser Kontext privat ist.
     * @return true, wenn der Kontext privat ist, sonst false.
     */
    public boolean isPrivate() {
        return isPrivate;
    }

    @Override
    public void addUser(@NotNull final User user) {
        if (!contains(user)) {
            user.send(SendAction.CONTEXT_JOIN, this);
            // Sende die Positionen der anderen Benutzer an den beitretenden Benutzer.
            containedUsers.values().forEach(other -> user.send(SendAction.AVATAR_SPAWN, other));

            super.addUser(user);
            user.teleport(spawnLocation);

            if (isPrivate) {
                final TextMessage info = new TextMessage(new MessageBundle("key"));

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

                if (containedUsers.isEmpty()) {
                    world.removePrivateRoom(this);

                    /*
                     * Sende an alle Benutzer, die gerade das Menü einer Rezeption geöffnet haben, die neue Liste
                     * aller privaten Räume.
                     */
                    world.getUsers().values().stream()
                            .filter(receiver -> receiver.getCurrentMenu() == Menu.ROOM_RECEPTION_MENU)
                            .forEach(receiver -> receiver.send(SendAction.CONTEXT_LIST, this.world));
                } else {
                    containedUsers.values().stream().findAny().get().addRole(this, Role.ROOM_OWNER);
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