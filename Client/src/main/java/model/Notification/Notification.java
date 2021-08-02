package model.Notification;

import model.Context.Context;
import model.MessageBundle;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Eine Klasse, welche eine Benachrichtigung repräsentiert.
 */
public class Notification implements INotificationView{
    private final UUID notificationId;
    private final LocalDateTime timestamp;
    private final boolean isRequest;
    private final MessageBundle messageBundle;
    private final Context context;

    public Notification(UUID notificationId, LocalDateTime timestamp, boolean isRequest, MessageBundle messageBundle, Context context) {
        this.notificationId = notificationId;
        this.timestamp = timestamp;
        this.isRequest = isRequest;
        this.messageBundle = messageBundle;
        this.context = context;
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

    public Context getContext() {
        return context;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Notification that = (Notification) object;
        return Objects.equals(notificationId, that.notificationId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(notificationId);
    }
}
