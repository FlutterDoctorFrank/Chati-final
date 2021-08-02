package model.context.spatial;

import controller.network.ClientSender;
import model.MessageBundle;
import model.communication.message.TextMessage;
import model.context.Context;
import model.role.Role;
import model.user.User;

public class Room extends Area implements IRoom {

    /** Karte dieses räumlichen Kontextes. */
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
     * Erzeugt eine Instanz eines Kontextes.
     *
     * @param roomName            Name des Kontextes.
     * @param parent              Übergeordneter Kontext.
     */
    protected Room(String roomName, Context parent, World world, SpatialMap map) {
        super(roomName, parent, world, null, null, null);
        this.map = map;
        this.isPrivate = false;
        this.password = null;
        RoomBuilder.build(this);
    }

    /**
     * Erzeugt eine Instanz eines Kontextes.
     *
     * @param roomName            Name des Kontextes.
     * @param parent              Übergeordneter Kontext.
     */
    public Room(String roomName, Context parent, World world, SpatialMap map, String password) {
        super(roomName, parent, world, null, null, null);
        this.map = map;
        this.isPrivate = true;
        this.password = password;
        RoomBuilder.build(this);
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
        if (password == null) {
            return false;
        }
        return this.password.equals(password);
    }

    /**
     * Gibt zurück, ob die Position auf der Karte dieses Kontextes an den übergebenen Koordinaten erlaubt ist.
     * @param posX X-Koordinate.
     * @param posY Y-Koordinate.
     * @return true, wenn die Position erlaubt ist, sonst false.
     */
    public boolean isLegal(int posX, int posY) {
        return collisionMap[posX][posY];
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
        // Wenn ein neuer Raum betreten wird, sende Positionsinformationen.
        containedUsers.values().forEach(containedUser -> {
            containedUser.getClientSender().send(ClientSender.SendAction.AVATAR_MOVE, user);
            user.getClientSender().send(ClientSender.SendAction.AVATAR_MOVE, containedUser);
        });
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
        if (isPrivate && user.hasRole(this, Role.ROOM_OWNER)) {
            user.removeRole(this, Role.ROOM_OWNER);
            if (containedUsers.isEmpty()) {
                world.removePrivateRoom(this);
            } else {
                containedUsers.values().stream().findAny().get().addRole(this, Role.ROOM_OWNER);
            }
        }
    }

    public void setCollisionMap(boolean[][] collisionMap) {
        this.collisionMap = collisionMap;
    }

    public void setSpawnLocation(Location spawnLocation) {
        this.spawnLocation = spawnLocation;
    }
}
