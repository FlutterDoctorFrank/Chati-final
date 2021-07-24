package model.exception;

import model.user.User;

import java.util.UUID;

public class NotificationNotFoundException extends Exception {
    private final User user;
    private final UUID notificationID;

    public NotificationNotFoundException(String errorMessage, User user, UUID notificationID) {
        super(errorMessage);
        this.user = user;
        this.notificationID = notificationID;
    }

    public NotificationNotFoundException(String errorMessage, User user, UUID notificationID, Throwable cause) {
        super(errorMessage, cause);
        this.user = user;
        this.notificationID = notificationID;
    }

    public User getUser() {
        return user;
    }

    public UUID getContextID() {
        return notificationID;
    }
}
