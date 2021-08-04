package model.notification;

import model.context.Context;
import model.MessageBundle;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Eine Klasse, welche eine Benachrichtigung repräsentiert.
 */
public class Notification implements INotificationView{

    /** Die eindeutige ID der Benachrichtigung. */
    private final UUID notificationId;

    /** Kontext, in dem der Benutzer diese Benachrichtigung besitzt. */
    private final Context context;

    /** Übersetzbare Nachricht der Benachrichtigung zusammen mit ihren Argumenten. */
    private final MessageBundle messageBundle;

    /** Zeitpunkt, an dem die Benachrichtigung generiert wurde. */
    private final LocalDateTime timestamp;

    /** Die Information, ob diese Benachrichtigung als Anfrage dargestellt werden soll. */
    private final boolean isRequest;

    public Notification(UUID notificationId, Context context, MessageBundle messageBundle, LocalDateTime timestamp,
                        boolean isRequest) {
        this.notificationId = notificationId;
        this.context = context;
        this.messageBundle = messageBundle;
        this.timestamp = timestamp;
        this.isRequest = isRequest;
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
    public boolean isRequest() {
        return isRequest;
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
