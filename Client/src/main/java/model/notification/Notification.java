package model.notification;

import model.context.Context;
import model.MessageBundle;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Eine Klasse, welche eine Benachrichtigung repräsentiert.
 */
public class Notification implements INotificationView {

    /** Die eindeutige ID der Benachrichtigung. */
    private final UUID notificationId;

    /** Kontext, in dem der Benutzer diese Benachrichtigung besitzt. */
    private final Context context;

    /** Übersetzbare Nachricht der Benachrichtigung zusammen mit ihren Argumenten. */
    private final MessageBundle messageBundle;

    /** Zeitpunkt, an dem die Benachrichtigung generiert wurde. */
    private final LocalDateTime timestamp;

    /** Die Information, um welche Art von Benachrichtigung es sich handelt. */
    private final NotificationType type;

    /** Information, ob diese Benachrichtigung vom Empfänger bereits geöffnet wurde. */
    private boolean isRead;

    /** Information, ob diese Benachrichtigung angenommen wurde. */
    private boolean isAccepted;

    /** Information, ob diese Benachrichtigung abgelehnt wurde. */
    private boolean isDeclined;

    public Notification(UUID notificationId, Context context, MessageBundle messageBundle, LocalDateTime timestamp,
                        NotificationType type, boolean isRead, boolean isAccepted, boolean isDeclined) {
        this.notificationId = notificationId;
        this.context = context;
        this.messageBundle = messageBundle;
        this.timestamp = timestamp;
        this.type = type;
        this.isRead = isRead;
        this.isAccepted = isAccepted;
        this.isDeclined = isDeclined;
    }

    @Override
    public UUID getNotificationId() {
        return notificationId;
    }

    @Override
    public MessageBundle getMessageBundle() {
        return messageBundle;
    }

    @Override
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    @Override
    public NotificationType getType() {
        return type;
    }

    @Override
    public boolean isRead() {
        return isRead;
    }

    @Override
    public boolean isAccepted() {
        return isAccepted;
    }

    @Override
    public boolean isDeclined() {
        return isDeclined;
    }

    /**
     * Lässt die Benachrichtigung als gelesen anzeigen.
     */
    public void setRead() {
        this.isRead = true;
    }

    /**
     * Lässt die Benachrichtigung als angenommen anzeigen.
     */
    public void setAccepted() {
        this.isAccepted = true;
    }

    /**
     * Lässt die Benachrichtigung als abgelehnt anzeigen.
     */
    public void setDeclined() {
        this.isDeclined = true;
    }

    /**
     * Gibt den Kontext zurück, in dem der Benutzer diese Benachrichtigung besitzt.
     * @return Kontext, in dem der Benutzer diese Benachrichtigung besitzt.
     */
    public Context getContext() {
        return context;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Notification that = (Notification) o;
        return Objects.equals(notificationId, that.notificationId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(notificationId);
    }
}
