package model.role;

import java.util.EnumSet;
import java.util.Set;

public enum Role {
    OWNER(
            EnumSet.of(Permission.CONTACT_USER, Permission.CONTACT_CONTEXT, Permission.TELEPORT_TO_USER,
                    Permission.ENTER_PRIVATE_ROOM, Permission.MUTE, Permission.BAN_MODERATOR, Permission.ASSIGN_AREA_MANAGER,
                    Permission.ASSIGN_MODERATOR, Permission.ASSIGN_ADMINISTRATOR, Permission.MANAGE_WORLDS)
    ),
    ADMINISTRATOR(
            EnumSet.of(Permission.CONTACT_USER, Permission.CONTACT_CONTEXT, Permission.TELEPORT_TO_USER,
                    Permission.ENTER_PRIVATE_ROOM, Permission.MUTE, Permission.BAN_MODERATOR, Permission.ASSIGN_AREA_MANAGER,
                    Permission.ASSIGN_MODERATOR, Permission.MANAGE_WORLDS)
    ),
    MODERATOR(
            EnumSet.of(Permission.CONTACT_USER, Permission.CONTACT_CONTEXT, Permission.TELEPORT_TO_USER,
                    Permission.ENTER_PRIVATE_ROOM, Permission.MUTE, Permission.BAN_USER, Permission.ASSIGN_AREA_MANAGER)
    ),
    ROOM_OWNER(
            EnumSet.of(Permission.CONTACT_USER, Permission.CONTACT_CONTEXT, Permission.MANAGE_PRIVATE_ROOM, Permission.MUTE)
    ),
    AREA_MANAGER(
            EnumSet.of(Permission.CONTACT_USER, Permission.MUTE)
    );

    private final Set<Permission> permissions;

    private Role(Set<Permission> permissions) {
        this.permissions = permissions;
    }

    public boolean hasPermission(Permission permission) {
        return permissions.contains(permission);
    }
}
