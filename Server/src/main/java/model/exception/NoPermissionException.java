package model.exception;

import model.role.Permission;
import model.user.User;
import org.jetbrains.annotations.NotNull;

public class NoPermissionException extends Exception {

    private final User user;
    private final Permission permission;

    public NoPermissionException(@NotNull final String errorMessage, @NotNull final User user,
                                 @NotNull final Permission permission) {
        super(errorMessage);
        this.user = user;
        this.permission = permission;
    }

    public NoPermissionException(@NotNull final String errorMessage, @NotNull final User user,
                                 @NotNull final Permission permission, @NotNull final Throwable cause) {
        super(errorMessage, cause);
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
