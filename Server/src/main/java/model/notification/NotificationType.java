package model.notification;

/**
 * Eine Enumeration, welche den Typ einer Benachrichtigung spezifiziert.
 */
public enum NotificationType {

    /** Repräsentiert eine einfache Benachrichtigung. */
    NOTIFICATION,

    /** Repräsentiert eine Freundschaftsanfrage. */
    FRIEND_REQUEST,

    /** Repräsentiert eine Anfrage zum Beitritt in einen privaten Raum. */
    ROOM_REQUEST,

    /** Repräsentiert eine Einladung in einen privaten Raum. */
    ROOM_INVITATION,

    /** Repräsentiert eine Anfrage zum Erhalt der Rolle des Bereichsberechtigten. */
    AREA_MANAGING_REQUEST
}