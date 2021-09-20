package model.exception;

import model.user.IUser;
import org.jetbrains.annotations.NotNull;
import java.util.UUID;

public class NotificationNotFoundException extends Exception {

    private final IUser user;
    private final UUID notificationId;

    public NotificationNotFoundException(@NotNull final String errorMessage, @NotNull final IUser user,
                                         @NotNull final UUID notificationId) {
        super(errorMessage);
        this.user = user;
        this.notificationId = notificationId;
    }

    public NotificationNotFoundException(@NotNull final String errorMessage, @NotNull final IUser user,
                                         @NotNull final UUID notificationId, @NotNull final Throwable cause) {
        super(errorMessage, cause);
        this.user = user;
        this.notificationId = notificationId;
    }

    public @NotNull IUser getUser() {
        return user;
    }

    public @NotNull UUID getNotificationId() {
        return notificationId;
    }
}
