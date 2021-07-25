package model.role;

/**
 * Eine Enumeration, welche Berechtigungen repräsentiert. Eine Berechtigung bestimmt, welche Handlungen ein Benutzer
 * durchführen darf.
 * Berechtigungen sind als {@link Role Rollen} zusammengefasst.
 */
public enum Permission {

    /**
     * Erlaubt das Senden von Nachrichten an, oder das Erhalten von Nachrichten von Benutzern mit denen gemäß der
     * Kommunikationsform und des Kommunikationsmedium nicht kommuniziert werden kann.
     */
    CONTACT_USER,

    /**
     * Erlaubt das Senden von Nachrichten an alle Benutzer innerhalb eines Kontextes, in dem sich der Anwender befindet.
     */
    CONTACT_CONTEXT,

    /**
     * Erlaubt das Teleportieren zu Benutzern, die sich nicht in der Freundesliste befinden.
     * Befindet sich der Benutzer, zu dem sich teleportiert werden soll, in einem privaten Raum, ist zusätzlich die
     * Berechtigung {@link Permission#ENTER_PRIVATE_ROOM} erforderlich.
     */
    TELEPORT_TO_USER,

    /**
     * Erlaubt das Einladen von Benutzern in einen privaten Raum, das Empfangen von Anfragen zum Beitritt in einen
     * privaten Raum, sowie das Entfernen von Benutzern aus einem privaten Raum.
     */
    MANAGE_PRIVATE_ROOM,

    /**
     * Erlaubt das Betreten von privaten Räumen ohne die Kenntnis eines Passworts und ohne vorherige Einladung in den
     * privaten Raum.
     */
    ENTER_PRIVATE_ROOM,

    /**
     * Erlaubt das Stummschalten, sowie die Aufhebung von Stummschaltungen bei Benutzern, die nicht selbst diese
     * Berechtigung haben.
     */
    MUTE,

    /**
     * Erlaubt das Sperren und Entsperren von Benutzern, sowie das Empfangen von Benachrichtigungen über gemeldete und
     * gesperrte Benutzer, sofern diese Benutzer nicht diese Berechtigung oder die Berechtigung
     * {@link Permission#BAN_MODERATOR} besitzen.
     */
    BAN_USER,

    /**
     * Erlaubt das Sperren und Entsperren von Benutzern, sowie das Empfangen von Benachrichtigungen über gemeldete
     * und gesperrte Benutzer, sofern diese Benutzer nicht diese Berechtigung besitzen.
     */
    BAN_MODERATOR,

    /**
     * Erlaubt das Erhalten von Anfragen zur Vergabe der Rolle des Bereichsberechtigten.
     */
    ASSIGN_AREA_MANAGER,

    /**
     * Erlaubt das Vergeben und Entziehen der Rolle des Moderators.
     */
    ASSIGN_MODERATOR,

    /**
     * Erlaubt das Vergeben und Entziehen der Rolle des Administrators.
     */
    ASSIGN_ADMINISTRATOR,

    /**
     * Erlaubt das Erstellen und Löschen von Welten.
     */
    MANAGE_WORLDS
}
