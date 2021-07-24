package model.exception;

import model.user.AdministrativeAction;
import model.user.User;

public class IllegalAdministrativeActionException extends Exception {
    private final User performer;
    private final User target;
    private final AdministrativeAction action;

    public IllegalAdministrativeActionException(String errorMessage, User performer, User target,  AdministrativeAction action) {
        super(errorMessage);
        this.performer = performer;
        this.target = target;
        this.action = action;
    }

    public IllegalAdministrativeActionException(String errorMessage, User performer, User target, AdministrativeAction action, Throwable cause) {
        super(errorMessage, cause);
        this.performer = performer;
        this.target = target;
        this.action = action;
    }

    public User getPerformer() {
        return performer;
    }

    public User getTarget() {
        return target;
    }

    public AdministrativeAction getAdministrativeAction() {
        return action;
    }
}
