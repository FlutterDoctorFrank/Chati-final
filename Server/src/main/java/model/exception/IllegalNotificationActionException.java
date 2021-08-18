package model.exception;

import model.notification.Notification;
import model.user.User;
import org.jetbrains.annotations.NotNull;

public class IllegalNotificationActionException extends Exception {

    private final User user;
    private final Notification notification;
    private final boolean accept;

    public IllegalNotificationActionException(@NotNull final String errorMessage, @NotNull final User user,
                                              @NotNull final Notification notification, final boolean accept) {
        super(errorMessage);
        this.user = user;
        this.notification = notification;
        this.accept = accept;
    }

    public IllegalNotificationActionException(@NotNull final String errorMessage, @NotNull final User user,
                                              @NotNull final Notification notification, final boolean accept,
                                              @NotNull final Throwable cause) {
        super(errorMessage, cause);
        this.user = user;
        this.notification = notification;
        this.accept = accept;
    }

    public @NotNull User getUser() {
        return user;
    }

    public @NotNull Notification getNotification() {
        return notification;
    }

    public boolean isAccepted() {
        return accept;
    }
}
