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
    private UUID notificationId;
    private LocalDateTime timestamp;
    private boolean isRequest;
    private MessageBundle messageBundle;
    private Context context;

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
