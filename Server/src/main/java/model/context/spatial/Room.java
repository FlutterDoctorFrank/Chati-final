package model.context.spatial;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import controller.network.ClientSender.SendAction;
import model.MessageBundle;
import model.communication.message.TextMessage;
import model.context.Context;
import model.role.Role;
import model.user.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
     * @param parent Übergeordneter Kontext.
     * @param world Übergeordnete Welt.
     * @param map Karte des Raums.
     */
    protected Room(@NotNull final String roomName, @NotNull final Context parent, @Nullable final World world,
                   @NotNull final SpatialMap map) {
        super(roomName, parent, world, null, null, null);
        this.map = map;
        this.isPrivate = false;
        this.password = null;

        if (Gdx.app == null) {
            throw new IllegalStateException("LibGDX environment is not available");
        }

        Gdx.app.postRunnable(this::build);
    }

    /**
     * Erzeugt eine Instanz eines (privaten) Raums.
     * @param roomName Name des Raums.
     * @param parent Übergeordneter Kontext.
     * @param world Übergeordnete Welt.
     * @param map Karte des Raums.
     * @param password Passwort des Raums.
     */
    public Room(@NotNull final String roomName, @NotNull final Context parent, @Nullable final World world,
                @NotNull final SpatialMap map, @NotNull final String password) {
        this(roomName, parent, world, map);
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
    public boolean isLegal(final int posX, final int posY) {
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
        if (!this.contains(user)) {
            user.send(SendAction.CONTEXT_JOIN, this);
            user.teleport(this.spawnLocation);

            super.addUser(user);

            this.containedUsers.values().stream()
                    .filter(avatar -> !avatar.equals(user))
                    .forEach(avatar -> user.send(SendAction.AVATAR_MOVE, avatar));

            if (this.isPrivate) {
                final TextMessage info = new TextMessage(new MessageBundle("key"));

                this.containedUsers.values().stream()
                        .filter(receiver -> !receiver.equals(user))
                        .forEach(receiver -> receiver.send(SendAction.MESSAGE, info));
            }
        }
    }

    @Override
    public void removeUser(@NotNull final User user) {
        if (this.contains(user)) {
            super.removeUser(user);

            this.containedUsers.values().forEach(receiver -> receiver.send(SendAction.AVATAR_REMOVE, user));

            if (this.isPrivate && user.hasRole(this, Role.ROOM_OWNER)) {
                user.removeRole(this, Role.ROOM_OWNER);

                if (this.containedUsers.isEmpty()) {
                    this.world.removePrivateRoom(this);
                    this.world.removeChild(this);

                    /*
                     * Sende an alle Benutzer, die gerade das Menü einer Rezeption geöffnet haben, die neue Liste
                     * aller privaten Räume.
                     */
                    this.world.getUsers().values().stream()
                            .filter(receiver -> receiver.getCurrentMenu() == Menu.ROOM_RECEPTION_MENU)
                            .forEach(receiver -> receiver.send(SendAction.CONTEXT_LIST, this.world));
                } else {
                    this.containedUsers.values().stream().findAny().get().addRole(this, Role.ROOM_OWNER);
                }
            }
        }
    }

    /**
     * Setzt die Parameter dieses Raums, die in der Datenstruktur der Karte enthalten sind.
     * @see MapUtils
     */
    private void build() {
        TiledMap tiledMap = new TmxMapLoader().load(map.getPath());
        this.communicationRegion = MapUtils.getCommunicationRegion(tiledMap.getProperties());
        this.communicationRegion.setArea(this);
        this.communicationMedia = MapUtils.getCommunicationMedia(tiledMap.getProperties());
        this.expanse = new Expanse(new Location(this, 0, 0), MapUtils.getWidth(tiledMap) , MapUtils.getHeight(tiledMap));
        System.out.println(MapUtils.getWidth(tiledMap));
        System.out.println(MapUtils.getHeight(tiledMap));
        //this.collisionMap = MapUtils.getCollisionMap(tiledMap);
        this.spawnLocation = new Location(this, MapUtils.getSpawnPosX(tiledMap), MapUtils.getSpawnPosY(tiledMap));
        MapUtils.buildChildTree(this, tiledMap);
    }
}