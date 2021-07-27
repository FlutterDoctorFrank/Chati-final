package model.User;

/**
 * Eine Enumeration, die eine Durchführung einer administrativen Aktion auf einem Benutzer repräsentiert
 */
public enum AdministrativeAction {
    INVITE_FRIEND,
    REMOVE_FRIEND,
    IGNORE_USER,
    UNIGNORE_USER,
    MUTE_USER,
    UNMUTE_USER,
    BAN_USER,
    UNBAN_USER,
    REPORT_USER,
    TELEPORT_TO_USER,
    ROOM_INVITE,
    ROOM_KICK,
    ASSIGN_MODERATOR,
    WITHDRAW_MODERATOR,
    ASSIGN_ADMINISTRATOR,
    WITHDRAW_ADMINISTRATOR,
}
