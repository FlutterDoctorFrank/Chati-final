package model.context.spatial;

import controller.network.ClientSender;
import model.context.ContextID;
import model.context.global.GlobalContext;
import model.exception.ContextNotFoundException;
import model.user.User;

import java.util.HashMap;
import java.util.Map;

public class World extends Room implements IWorld {

    /** Untergeordnete private Räume. */
    private final Map<ContextID, Room> privateRooms;

    /**
     * Erzeugt eine Instanz einer Welt.
     * @param worldName Name der Welt.
     * @param map Karte der Welt.
     */
    public World(String worldName, SpatialMap map) {
        super(worldName, GlobalContext.getInstance(), null, map);
        this.privateRooms = new HashMap<>();
    }

    /**
     * Fügt dem Kontext einen privaten Raum hinzu.
     * @param privateRoom Hinzuzufügender privater Raum.
     */
    public void addPrivateRoom(Room privateRoom) {
        privateRooms.put(privateRoom.getContextId(), privateRoom);
        children.put(privateRoom.getContextId(), privateRoom);
        // Hier noch an alle clienten neue Liste senden...
    }

    /**
     * Entfernt einen privaten Raum aus dem Kontext.
     * @param privateRoom Zu entfernender privater Raum.
     */
    public void removePrivateRoom(Room privateRoom) {
        privateRooms.remove(privateRoom.getContextId());
        children.remove(privateRoom.getContextId());
        // Hier noch alle clienten neue Liste senden...
    }

    /**
     * Überprüft, ob dieser Kontext einen privaten Raum enthält.
     * @param privateRoom Zu überprüfender privater Raum.
     * @return true, wenn der Kontext den privaten Raum enthält, sonst false.
     */
    public boolean containsPrivateRoom(Room privateRoom) {
        return privateRooms.containsKey(privateRoom.getContextId());
    }

    /**
     * Gibt einen privaten Raum zu übergebener ID zurück.
     * @param roomId ID des privaten Raums.
     * @return Privater Raum.
     */
    public Room getPrivateRoom(ContextID roomId) throws ContextNotFoundException {
        Room privateRoom = privateRooms.get(roomId);
        if (privateRoom == null) {
            throw new ContextNotFoundException("key", roomId);
        }
        return privateRoom;
    }

    @Override
    public Map<ContextID, Room> getPrivateRooms() {
        return privateRooms;
    }

    @Override
    public void addUser(User user) {
        // Sende die entsprechenden Pakete an den Benutzer und an andere Benutzer.
        user.getClientSender().send(ClientSender.SendAction.WORLD_ACTION, this);
        super.addUser(user);

        user.getWorldRoles().values().forEach(contextRole -> {
            user.getClientSender().send(ClientSender.SendAction.CONTEXT_ROLE, contextRole);
        });
        user.getWorldNotifications().values().forEach(notification -> {
            user.getClientSender().send(ClientSender.SendAction.NOTIFICATION, notification);
        });
        getUsers().values().forEach(containedUser -> {
            if (!user.equals(containedUser)) {
                containedUser.getClientSender().send(ClientSender.SendAction.USER_INFO, user);
                user.getClientSender().send(ClientSender.SendAction.USER_INFO, containedUser);
            }
        });
        getBannedUsers().values().forEach(bannedUser -> {
            user.getClientSender().send(ClientSender.SendAction.USER_INFO, bannedUser);
        });
    }

    @Override
    public void removeUser(User user) {
        super.removeUser(user);
        // Sende die entsprechenden Pakete an den Benutzer und an andere Benutzer.
        getUsers().values().forEach(containedUser -> {
            containedUser.getClientSender().send(ClientSender.SendAction.USER_INFO, user);
        });
        user.getClientSender().send(ClientSender.SendAction.WORLD_ACTION, this);
    }

    @Override
    public World getWorld() {
        return this;
    }
}