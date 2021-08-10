package model.context.spatial;

import controller.network.ClientSender;
import model.context.ContextID;
import model.context.global.GlobalContext;
import model.role.Permission;
import model.user.User;
import org.jetbrains.annotations.NotNull;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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
        addChild(privateRoom);
        // Hier noch an alle clienten neue Liste senden...
    }

    /**
     * Entfernt einen privaten Raum aus dem Kontext.
     * @param privateRoom Zu entfernender privater Raum.
     */
    public void removePrivateRoom(Room privateRoom) {
        privateRooms.remove(privateRoom.getContextId());
        removeChild(privateRoom);
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

    @Override
    public Map<ContextID, Room> getPrivateRooms() {
        return privateRooms;
    }

    @Override
    public void addUser(@NotNull final User user) {
        // Überprüfung, ob sich der Benutzer bereits in der Welt befindet.
        if (user.equals(this.containedUsers.get(user.getUserId()))) {
            return;
        }

        if (!this.equals(user.getWorld())) {
            user.setWorld(this);
        }

        // Sende die entsprechenden Pakete an den Benutzer und an andere Benutzer.
        user.getClientSender().send(ClientSender.SendAction.WORLD_ACTION, this);

        super.addUser(user);

        user.getWorldRoles().values().forEach(contextRole -> {
            user.getClientSender().send(ClientSender.SendAction.CONTEXT_ROLE, contextRole);
        });
        user.getWorldNotifications().values().forEach(notification -> {
            user.getClientSender().send(ClientSender.SendAction.NOTIFICATION, notification);
        });

        final Map<UUID, User> usersToSend = new HashMap<>(this.containedUsers);

        if (user.hasPermission(this, Permission.BAN_USER) || user.hasPermission(this, Permission.BAN_MODERATOR)) {
            usersToSend.putAll(this.bannedUsers);
        }

        usersToSend.remove(user.getUserId());
        usersToSend.values().forEach(containedUser -> {
            containedUser.getClientSender().send(ClientSender.SendAction.USER_INFO, user);
            user.getClientSender().send(ClientSender.SendAction.USER_INFO, containedUser);
        });
    }

    @Override
    public void removeUser(@NotNull final User user) {
        // Überprüfung, ob sich der zu entfernende Benutzer überhaupt in der Welt befindet.
        if (this.containedUsers.containsKey(user.getUserId())) {
            // Temporäre Behebung des 'User is already in a world' Fehler
            if (this.equals(user.getWorld())) {
                user.setWorld(null);
            }

            super.removeUser(user);

            // Sende die entsprechenden Pakete an den Benutzer und an andere Benutzer.
            this.containedUsers.values().forEach(containedUser -> {
                containedUser.getClientSender().send(ClientSender.SendAction.USER_INFO, user);
            });

            if (user.isOnline()) {
                user.getClientSender().send(ClientSender.SendAction.WORLD_ACTION, this);
            }
        }
    }

    @Override
    public World getWorld() {
        return this;
    }
}