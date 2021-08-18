package model.context.spatial;

import controller.network.ClientSender.SendAction;
import model.context.ContextID;
import model.context.global.GlobalContext;
import model.role.Permission;
import model.user.User;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class World extends Room implements IWorld {

    /** Untergeordnete private Räume. */
    private final Map<ContextID, Room> privateRooms;
    private final Map<UUID, User> worldUsers;

    /**
     * Erzeugt eine Instanz einer Welt.
     * @param worldName Name der Welt.
     * @param map Karte der Welt.
     */
    public World(@NotNull final String worldName, @NotNull final SpatialMap map) {
        super(worldName, GlobalContext.getInstance(), null, map);
        this.privateRooms = new HashMap<>();
        this.worldUsers = new HashMap<>();
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
    public @NotNull Map<ContextID, Room> getPrivateRooms() {
        return Collections.unmodifiableMap(privateRooms);
    }

    public @NotNull Map<UUID, User> getWorldUsers() {
        return Collections.unmodifiableMap(worldUsers);
    }

    @Override
    public void addUser(@NotNull final User user) {
        if (user.getWorld() != null && !user.getWorld().equals(this)) {
            throw new IllegalStateException("User is already in an another World.");
        }

        if (!this.contains(user)) {
            user.send(SendAction.WORLD_ACTION, this);

            super.addUser(user);

            user.getWorldRoles().values().forEach(role -> user.send(SendAction.CONTEXT_ROLE, role));
            user.getWorldNotifications().values().forEach(notification -> user.send(SendAction.NOTIFICATION, notification));

            this.worldUsers.put(user.getUserId(), user);
            this.worldUsers.values().stream()
                    .filter(receiver -> !receiver.equals(user))
                    .forEach(receiver -> {
                        receiver.send(SendAction.USER_INFO, user);
                        user.send(SendAction.USER_INFO, receiver);
                    });

            if (user.hasPermission(this, Permission.BAN_USER) || user.hasPermission(this, Permission.BAN_MODERATOR)) {
                this.bannedUsers.values().forEach(banned -> user.send(SendAction.USER_INFO, banned));
            }
        }
    }

    @Override
    public void removeUser(@NotNull final User user) {
        if (this.contains(user)) {
            /*
             * Entferne den Benutzer nur aus der Welt, wenn der Benutzer auch die Welt verlassen hat, ansonsten
             * wechselt der Benutzer nur den aktuellen Raum innerhalb der Welt.
             */
            if (user.getWorld() == null || !user.getWorld().equals(this)) {
                user.send(SendAction.WORLD_ACTION, this);

                super.removeUser(user);

                this.worldUsers.remove(user.getUserId());
                this.worldUsers.values().forEach(receiver -> receiver.send(SendAction.USER_INFO, user));
            }
        }
    }

    @Override
    public @NotNull World getWorld() {
        return this;
    }

    public void sendRoomList() {
        this.worldUsers.values().stream()
                .filter(user -> user.getCurrentMenu() == Menu.ROOM_RECEPTION_MENU)
                .forEach(user -> user.send(SendAction.CONTEXT_LIST, this));
    }
}