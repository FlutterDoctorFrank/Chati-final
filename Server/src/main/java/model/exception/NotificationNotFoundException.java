package model.exception;

import model.user.User;
import org.jetbrains.annotations.NotNull;
import java.util.UUID;

public class NotificationNotFoundException extends Exception {

    private final User user;
    private final UUID notificationId;

    public NotificationNotFoundException(@NotNull final String errorMessage, @NotNull final User user,
                                         @NotNull final UUID notificationId) {
        super(errorMessage);
        this.user = user;
        this.notificationId = notificationId;
    }

    public NotificationNotFoundException(@NotNull final String errorMessage, @NotNull final User user,
                                         @NotNull final UUID notificationId, @NotNull final Throwable cause) {
        super(errorMessage, cause);
        this.user = user;
        this.notificationId = notificationId;
    }

    public @NotNull User getUser() {
        return user;
    }

    public @NotNull UUID getNotificationId() {
        return notificationId;
    }
}
