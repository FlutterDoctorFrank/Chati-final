package model.context.spatial;

import controller.network.ClientSender.SendAction;
import model.communication.CommunicationMedium;
import model.context.Context;
import model.context.ContextID;
import model.context.global.GlobalContext;
import model.role.Permission;
import model.user.User;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class World extends Area implements IWorld {

    /** Bezeichnung eines öffentlichen Raums. */
    private static final String PUBLIC_ROOM_NAME = "Public";

    /** Untergeordneter öffentlicher Raum. */
    private final Room publicRoom;

    /** Untergeordnete private Räume. */
    private final Map<ContextID, Room> privateRooms;

    /**
     * Erzeugt eine Instanz einer Welt.
     * @param worldName Name der Welt.
     * @param map Karte der Welt.
     */
    public World(@NotNull final String worldName, @NotNull final SpatialMap map) {
        super(worldName, GlobalContext.getInstance(), null, null, null, null);
        this.publicRoom = new Room(PUBLIC_ROOM_NAME, this, map);
        addChild(publicRoom);
        this.privateRooms = new HashMap<>();
    }

    /**
     * Fügt dem Kontext einen privaten Raum hinzu.
     * @param privateRoom Hinzuzufügender privater Raum.
     */
    public void addPrivateRoom(@NotNull final Room privateRoom) {
        privateRooms.put(privateRoom.getContextId(), privateRoom);
        addChild(privateRoom);

        this.sendRoomList();
    }

    /**
     * Entfernt einen privaten Raum aus dem Kontext.
     * @param privateRoom Zu entfernender privater Raum.
     */
    public void removePrivateRoom(@NotNull final Room privateRoom) {
        privateRooms.remove(privateRoom.getContextId());
        removeChild(privateRoom);

        this.sendRoomList();
    }

    /**
     * Überprüft, ob dieser Kontext einen privaten Raum enthält.
     * @param privateRoom Zu überprüfender privater Raum.
     * @return true, wenn der Kontext den privaten Raum enthält, sonst false.
     */
    public boolean containsPrivateRoom(@NotNull final Room privateRoom) {
        return privateRooms.containsKey(privateRoom.getContextId());
    }

    @Override
    public @NotNull Room getPublicRoom() {
        return publicRoom;
    }

    @Override
    public @NotNull Map<ContextID, Room> getPrivateRooms() {
        return Collections.unmodifiableMap(privateRooms);
    }

    @Override
    public void addUser(@NotNull final User user) {
        if (user.getWorld() != null && !user.getWorld().equals(this)) {
            throw new IllegalStateException("User is already in an another World.");
        }

        if (!contains(user)) {
            user.send(SendAction.WORLD_ACTION, this);

            super.addUser(user);

            user.getWorldRoles().values().forEach(role -> user.send(SendAction.CONTEXT_ROLE, role));
            user.getWorldNotifications().values().forEach(notification -> user.send(SendAction.NOTIFICATION, notification));

            containedUsers.values().stream()
                    .filter(receiver -> !receiver.equals(user))
                    .forEach(receiver -> {
                        receiver.send(SendAction.USER_INFO, user);
                        user.send(SendAction.USER_INFO, receiver);
                    });

            if (user.hasPermission(this, Permission.BAN_USER) || user.hasPermission(this, Permission.BAN_MODERATOR)) {
                bannedUsers.values().forEach(banned -> user.send(SendAction.USER_INFO, banned));
            }
        }
    }

    @Override
    public void removeUser(@NotNull final User user) {
        if (this.contains(user)) {
            user.send(SendAction.WORLD_ACTION, this);
            super.removeUser(user);
            containedUsers.values().forEach(receiver -> receiver.send(SendAction.USER_INFO, user));
        }
    }

    @Override
    public @NotNull World getWorld() {
        return this;
    }

    @Override
    public @NotNull Map<UUID, User> getCommunicableUsers(@NotNull User communicatingUser) {
        return Collections.emptyMap();
    }

    @Override
    public boolean canCommunicateWith(@NotNull CommunicationMedium medium) {
        return false;
    }

    public void sendRoomList() {
        containedUsers.values().stream()
                .filter(user -> user.getCurrentMenu() == Menu.ROOM_RECEPTION_MENU)
                .forEach(user -> user.send(SendAction.CONTEXT_LIST, this));
    }
}