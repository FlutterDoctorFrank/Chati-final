package model.context.spatial;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import controller.network.ClientSender;
import model.MessageBundle;
import model.communication.message.TextMessage;
import model.context.Context;
import model.role.Role;
import model.user.User;

import java.util.Set;

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
    protected Room(String roomName, Context parent, World world, SpatialMap map) {
        super(roomName, parent, world, null, null, null);
        this.map = map;
        this.isPrivate = false;
        this.password = null;
        create();
    }

    /**
     * Erzeugt eine Instanz eines (privaten) Raums.
     * @param roomName Name des Raums.
     * @param parent Übergeordneter Kontext.
     * @param world Übergeordnete Welt.
     * @param map Karte des Raums.
     * @param password Passwort des Raums.
     */
    public Room(String roomName, Context parent, World world, SpatialMap map, String password) {
        super(roomName, parent, world, null, null, null);
        this.map = map;
        this.isPrivate = true;
        this.password = password;
        create();
    }

    @Override
    public SpatialMap getMap() {
        return map;
    }

    /**
     * Überprüft, ob ein übergebenes Passwort mit dem Passwort dieses Kontextes übereinstimmt.
     * @param password Zu überprüfendes Passwort.
     * @return true, wenn das Passwort übereinstimmt oder false, wenn dieser Kontext kein Passwort hat oder es nicht
     * übereinstimmt.
     */
    public boolean checkPassword(String password) {
        return !(this.password == null) && this.password.equals(password);
    }

    /**
     * Gibt zurück, ob die Position auf der Karte dieses Kontextes an den übergebenen Koordinaten erlaubt ist.
     * @param posX X-Koordinate.
     * @param posY Y-Koordinate.
     * @return true, wenn die Position erlaubt ist, sonst false.
     */
    public boolean isLegal(int posX, int posY) {
        return !collisionMap[posX][posY];
    }

    /**
     * Gibt die Anfangsposition auf der Karte dieses Kontextes zurück.
     * @return Anfangsposition auf der Karte dieses Kontextes.
     */
    public Location getSpawnLocation() {
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
    public void addUser(User user) {
        // Wenn ein neuer Raum betreten wird, sende die entsprechenden Pakete.
        user.getClientSender().send(ClientSender.SendAction.CONTEXT_JOIN, this);
        containedUsers.values().forEach(containedUser -> {
            user.getClientSender().send(ClientSender.SendAction.AVATAR_MOVE, containedUser);
        });
        // Wenn der betrene Raum privat ist, informiere alle anderen Benutzer in dem Raum darüber.
        if (isPrivate) {
            TextMessage infoMessage = new TextMessage(new MessageBundle("key"));
            containedUsers.values().forEach(containedUser -> {
                containedUser.getClientSender().send(ClientSender.SendAction.MESSAGE, infoMessage);
            });
        }
        super.addUser(user);
    }

    @Override
    public void removeUser(User user) {
        super.removeUser(user);
        containedUsers.values().forEach(containedUser -> {
            containedUser.getClientSender().send(ClientSender.SendAction.AVATAR_REMOVE, user);
        });
        // Wenn dieser Raum privat ist und der zu entfernende Benutzer der Raumbesitzer, entziehe ihm die Rolle.
        if (isPrivate && user.hasRole(this, Role.ROOM_OWNER)) {
            user.removeRole(this, Role.ROOM_OWNER);
            // Wenn der private Raum nach seinem Verlassen leer ist, entferne den privaten Raum, sonst übergebe die
            // Rolle jemand anderem in dem Raum.
            if (containedUsers.isEmpty()) {
                world.removePrivateRoom(this);
            } else {
                containedUsers.values().stream().findAny().get().addRole(this, Role.ROOM_OWNER);
            }
        }
    }

    /**
     * Setzt die Parameter dieses Raums, die in der Datenstruktur der Karte enthalten sind.
     * @see MapUtils
     */
    private void create() {
        TiledMap tiledMap = new TmxMapLoader().load(map.getPath());
        this.communicationRegion = MapUtils.getCommunicationRegion(tiledMap.getProperties());
        this.communicationMedia = MapUtils.getCommunicationMedia(tiledMap.getProperties());
        this.expanse = new Expanse(new Location(this, 0, 0), MapUtils.getWidth(tiledMap), MapUtils.getHeight(tiledMap));
        this.collisionMap = MapUtils.getCollisionMap(tiledMap);
        this.spawnLocation = new Location(this, MapUtils.getSpawnPosX(tiledMap), MapUtils.getSpawnPosY(tiledMap));
        MapUtils.buildChildTree(this, tiledMap);
    }
}