package model.exception;

import model.notification.Notification;
import model.user.User;

public class IllegalNotificationActionException extends Exception {
    private final User user;
    private final Notification notification;
    private final boolean accept;

    public IllegalNotificationActionException(String errorMessage, User user, Notification notification, boolean accept) {
        super(errorMessage);
        this.user = user;
        this.notification = notification;
        this.accept = accept;
    }

    public IllegalNotificationActionException(String errorMessage, User user, Notification notification, boolean accept, Throwable cause) {
        super(errorMessage, cause);
        this.user = user;
        this.notification = notification;
        this.accept = accept;
    }

    public User getUser() {
        return user;
    }

    public Notification getNotification() {
        return notification;
    }

    public boolean isAccepted() {
        return accept;
    }
}
