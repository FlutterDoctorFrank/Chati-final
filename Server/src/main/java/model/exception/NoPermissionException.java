package model.exception;

import model.role.Permission;
import model.user.User;

public class NoPermissionException extends Exception {
    private final User user;
    private final Permission permission;

    public NoPermissionException(String errorMessage, User user, Permission permission) {
        super(errorMessage);
        this.user = user;
        this.permission = permission;
    }

    public NoPermissionException(String errorMessage, User user, Permission permission, Throwable cause) {
        super(errorMessage, cause);
        this.user = user;
        this.permission = permission;
    }

    public User getUser() {
        return user;
    }

    public Permission getPermission() {
        return permission;
    }
}
