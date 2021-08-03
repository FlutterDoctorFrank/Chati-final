package model.exception;

import model.context.spatial.objects.Interactable;
import model.user.User;

public class IllegalInteractionException extends Exception {
    private final User user;
    private Interactable interactable;

    public IllegalInteractionException(String errorMessage, User user) {
        super(errorMessage);
        this.user = user;
    }

    public IllegalInteractionException(String errorMessage, User user, Throwable cause) {
        super(errorMessage, cause);
        this.user = user;
    }

    public IllegalInteractionException(String errorMessage, User user, Interactable interactable) {
        super(errorMessage);
        this.user = user;
        this.interactable = interactable;
    }

    public IllegalInteractionException(String errorMessage, User user, Interactable interactable, Throwable cause) {
        super(errorMessage, cause);
        this.user = user;
        this.interactable = interactable;
    }

    public User getUser() {
        return user;
    }

    public Interactable getInteractable() {
        return interactable;
    }
}
