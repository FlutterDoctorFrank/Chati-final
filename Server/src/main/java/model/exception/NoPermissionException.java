package model.exception;

import model.role.Permission;
import model.user.User;
import org.jetbrains.annotations.NotNull;

public class NoPermissionException extends FeedbackException {

    private final User user;
    private final Permission permission;

    public NoPermissionException(@NotNull final String message, @NotNull final String key,
                                 @NotNull final User user, @NotNull final Permission permission) {
        super(message, key, permission);
        this.user = user;
        this.permission = permission;
    }

    public @NotNull User getUser() {
        return user;
    }

    public @NotNull Permission getPermission() {
        return permission;
    }
}
