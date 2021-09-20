package model.exception;

import model.notification.INotification;
import model.user.IUser;
import org.jetbrains.annotations.NotNull;

public class IllegalNotificationActionException extends Exception {

    private final IUser user;
    private final INotification notification;
    private final boolean accept;

    public IllegalNotificationActionException(@NotNull final String errorMessage, @NotNull final IUser user,
                                              @NotNull final INotification notification, final boolean accept) {
        super(errorMessage);
        this.user = user;
        this.notification = notification;
        this.accept = accept;
    }

    public IllegalNotificationActionException(@NotNull final String errorMessage, @NotNull final IUser user,
                                              @NotNull final INotification notification, final boolean accept,
                                              @NotNull final Throwable cause) {
        super(errorMessage, cause);
        this.user = user;
        this.notification = notification;
        this.accept = accept;
    }

    public @NotNull IUser getUser() {
        return user;
    }

    public @NotNull INotification getNotification() {
        return notification;
    }

    public boolean isAccepted() {
        return accept;
    }
}
