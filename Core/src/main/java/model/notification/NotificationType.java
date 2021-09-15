package model.notification;

import org.jetbrains.annotations.NotNull;

/**
 * Eine Enumeration, welche den Typ einer Benachrichtigung spezifiziert.
 */
public enum NotificationType {

    /**
     * Repräsentiert eine einfache Benachrichtigung.
     * */
    INFORMATION("notification.type.information"),

    /**
     * Repräsentiert eine Freundschaftsanfrage.
     * */
    FRIEND_REQUEST("notification.type.friend-request"),

    /**
     * Repräsentiert eine Anfrage zum Beitritt in einen privaten Raum.
     * */
    ROOM_REQUEST("notification.type.room-request"),

    /**
     * Repräsentiert eine Einladung in einen privaten Raum.
     * */
    ROOM_INVITATION("notification.type.room-invite"),

    /**
     * Repräsentiert eine Anfrage zum Erhalt der Rolle des Bereichsberechtigten.
     * */
    AREA_MANAGING_REQUEST("notification.type.area-manage");

    private final String messageKey;

    NotificationType(@NotNull final String messageKey) {
        this.messageKey = messageKey;
    }

    public @NotNull String getMessageKey() {
        return messageKey;
    }
}