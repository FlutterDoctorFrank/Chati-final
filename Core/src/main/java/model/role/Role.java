package model.role;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

/**
 * Eine Enumeration, welche Rollen repräsentiert. Eine Rolle besteht aus mehreren {@link Permission Berechtigungen}.
 */
public enum Role {

    /**
     * Repräsentiert die Rolle eines Besitzers.
     */
    OWNER(Permission.CONTACT_USER, Permission.CONTACT_CONTEXT, Permission.SHARE_SCREEN, Permission.SEE_INVISIBLE_USERS,
            Permission.TELEPORT_TO_USER, Permission.ENTER_PRIVATE_ROOM, Permission.MUTE, Permission.BAN_MODERATOR,
            Permission.BAN_USER, Permission.ASSIGN_AREA_MANAGER, Permission.ASSIGN_MODERATOR,
            Permission.ASSIGN_ADMINISTRATOR, Permission.MANAGE_WORLDS, Permission.MANAGE_BOTS),

    /**
     * Repräsentiert die Rolle eines Administrators.
     */
    ADMINISTRATOR(Permission.CONTACT_USER, Permission.CONTACT_CONTEXT, Permission.SHARE_SCREEN,
            Permission.SEE_INVISIBLE_USERS, Permission.TELEPORT_TO_USER, Permission.ENTER_PRIVATE_ROOM, Permission.MUTE,
            Permission.BAN_MODERATOR, Permission.BAN_USER, Permission.ASSIGN_AREA_MANAGER, Permission.ASSIGN_MODERATOR,
            Permission.MANAGE_WORLDS, Permission.MANAGE_BOTS),

    /**
     * Repräsentiert die Rolle eines Moderators.
     */
    MODERATOR(Permission.CONTACT_USER, Permission.CONTACT_CONTEXT, Permission.SHARE_SCREEN,
            Permission.SEE_INVISIBLE_USERS, Permission.TELEPORT_TO_USER, Permission.ENTER_PRIVATE_ROOM, Permission.MUTE,
            Permission.BAN_USER, Permission.ASSIGN_AREA_MANAGER),

    /**
     * Repräsentiert die Rolle eines Rauminhabers.
     */
    ROOM_OWNER(Permission.CONTACT_USER, Permission.CONTACT_CONTEXT, Permission.SHARE_SCREEN,
            Permission.MANAGE_PRIVATE_ROOM, Permission.MUTE),

    /**
     * Repräsentiert die Rolle eines Bereichsberechtigten.
     */
    AREA_MANAGER(Permission.CONTACT_USER, Permission.CONTACT_CONTEXT, Permission.SHARE_SCREEN, Permission.MUTE),

    /**
     * Repräsentiert einen Bot.
     */
    BOT(Permission.CONTACT_USER, Permission.CONTACT_CONTEXT, Permission.MUTE, Permission.BAN_USER, Permission.BAN_MODERATOR);

    private final Set<Permission> permissions;

    Role(@NotNull final Permission... permissions) {
        this.permissions = Collections.unmodifiableSet(EnumSet.copyOf(Arrays.asList(permissions)));
    }

    /**
     * Gibt zurück, ob diese Rolle die angegebene Berechtigung besitzt.
     * @param permission Die zu überprüfende Berechtigung.
     * @return true, wenn die Rolle die Berechtigung enthält, ansonsten false.
     */
    public boolean hasPermission(@NotNull final Permission permission) {
        return this.permissions.contains(permission);
    }
}
