package model.exception;

import model.context.spatial.SpatialContext;
import model.user.User;

public class IllegalInteractionException extends Exception {
    private final User user;
    private SpatialContext interactable;

    public IllegalInteractionException(String errorMessage, User user) {
        super(errorMessage);
        this.user = user;
    }

    public IllegalInteractionException(String errorMessage, User user, Throwable cause) {
        super(errorMessage, cause);
        this.user = user;
    }

    public IllegalInteractionException(String errorMessage, User user, SpatialContext interactable) {
        super(errorMessage);
        this.user = user;
        this.interactable = interactable;
    }

    public IllegalInteractionException(String errorMessage, User user, SpatialContext interactable, Throwable cause) {
        super(errorMessage, cause);
        this.user = user;
        this.interactable = interactable;
    }

    public User getUser() {
        return user;
    }

    public SpatialContext getInteractable() {
        return interactable;
    }
}
