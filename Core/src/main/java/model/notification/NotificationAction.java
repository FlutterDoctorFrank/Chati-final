package model.notification;

/**
 * Eine Enumeration für die verschiedenen Verwaltungsmöglichkeiten einer Benachrichtigung.
 */
public enum NotificationAction {

    /**
     * Führt dazu, dass eine Benachrichtigung als gelesen markiert wird.
     */
    READ,

    /**
     * Führt dazu, dass eine Benachrichtigungs-Anfrage akzeptiert wird.
     */
    ACCEPT,

    /**
     * Führt dazu, dass eine Benachrichtigungs-Anfrage abgelehnt wird.
     */
    DECLINE,

    /**
     * Führt dazu, dass eine Benachrichtigung gelöscht wird.
     */
    DELETE
}
