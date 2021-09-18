package model.context.spatial;

import controller.network.ClientSender.SendAction;
import model.communication.CommunicationMedium;
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
    public World(@NotNull final String worldName, @NotNull final ContextMap map) {
        super(worldName, GlobalContext.getInstance(), null, null, null, null);
        this.publicRoom = new Room(PUBLIC_ROOM_NAME, this, map);
        this.publicRoom.build();
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
            user.setWorld(this);
            user.send(SendAction.WORLD_ACTION, this);

            super.addUser(user);

            // Zuerst die Benutzerinformationen senden und danach die Positionen der Avatare.
            containedUsers.values().stream()
                    .filter(receiver -> !receiver.equals(user))
                    .forEach(receiver -> {
                        // Versenden der Benutzerinformationen.
                        user.send(SendAction.USER_INFO, receiver);
                        receiver.send(SendAction.USER_INFO, user);
                        // Versenden der globalen und weltbezogenen Rollen.
                        user.send(SendAction.CONTEXT_ROLE, receiver.getGlobalRoles());
                        user.send(SendAction.CONTEXT_ROLE, receiver.getWorldRoles());
                        receiver.send(SendAction.CONTEXT_ROLE, user.getGlobalRoles());
                        receiver.send(SendAction.CONTEXT_ROLE, user.getWorldRoles());
                    });

            if (user.hasPermission(this, Permission.BAN_USER) || user.hasPermission(this, Permission.BAN_MODERATOR)) {
                bannedUsers.values().forEach(banned -> user.send(SendAction.USER_INFO, banned));
            }

            user.teleport(publicRoom.getSpawnLocation());
            user.getWorldNotifications().values().forEach(notification -> user.send(SendAction.NOTIFICATION, notification));
        }
    }

    @Override
    public void removeUser(@NotNull final User user) {
        if (this.contains(user)) {
            super.removeUser(user);
            user.setWorld(null);
            user.send(SendAction.WORLD_ACTION, this);
            containedUsers.values().forEach(receiver -> receiver.send(SendAction.USER_INFO, user));
        }
    }

    @Override
    public @NotNull World getWorld() {
        return this;
    }

    @Override
    public @NotNull Map<UUID, User> getCommunicableUsers(@NotNull final User communicatingUser) {
        return Collections.emptyMap();
    }

    @Override
    public boolean canCommunicateWith(@NotNull final CommunicationMedium medium) {
        return false;
    }

    public void sendRoomList() {
        containedUsers.values().stream()
                .filter(user -> user.getCurrentMenu() == ContextMenu.ROOM_RECEPTION_MENU)
                .forEach(user -> user.send(SendAction.CONTEXT_LIST, this));
    }
}