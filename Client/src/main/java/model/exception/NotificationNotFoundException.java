package model.exception;

import org.jetbrains.annotations.NotNull;
import java.util.UUID;

public class NotificationNotFoundException extends Exception{

    private final UUID notificationId;

    public NotificationNotFoundException(@NotNull final String errorMessage, @NotNull final UUID notificationId) {
        super(errorMessage);
        this.notificationId = notificationId;
    }

    public @NotNull UUID getNotificationId() {
        return notificationId;
    }
}
