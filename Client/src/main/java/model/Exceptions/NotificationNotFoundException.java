package model.Exceptions;

import java.util.UUID;

public class NotificationNotFoundException extends Exception{
    private UUID notificationId;

    public NotificationNotFoundException(String errorMessage, UUID notificationId) {
        super(errorMessage);
        this.notificationId = notificationId;
    }

    public UUID getNotificationId() {
        return notificationId;
    }
}
