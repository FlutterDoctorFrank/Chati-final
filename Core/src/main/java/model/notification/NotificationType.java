package model.notification;

/**
 * Eine Enumeration, welche den Typ einer Benachrichtigung spezifiziert.
 */
public enum NotificationType {

    /** Repräsentiert eine einfache Benachrichtigung. */
    NOTIFICATION("Information"),

    /** Repräsentiert eine Freundschaftsanfrage. */
    FRIEND_REQUEST("Freundschaftsanfrage"),

    /** Repräsentiert eine Anfrage zum Beitritt in einen privaten Raum. */
    ROOM_REQUEST("Raumanfrage"),

    /** Repräsentiert eine Einladung in einen privaten Raum. */
    ROOM_INVITATION("Raumeinladung"),

    /** Repräsentiert eine Anfrage zum Erhalt der Rolle des Bereichsberechtigten. */
    AREA_MANAGING_REQUEST("Bereichsanfrage");

    private final String name;

    NotificationType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}