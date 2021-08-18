package model.exception;

import model.user.AdministrativeAction;
import model.user.User;
import org.jetbrains.annotations.NotNull;

public class IllegalAdministrativeActionException extends Exception {

    private final User performer;
    private final User target;
    private final AdministrativeAction action;

    public IllegalAdministrativeActionException(@NotNull final String errorMessage, @NotNull final User performer,
                                                @NotNull final User target, @NotNull final AdministrativeAction action) {
        super(errorMessage);
        this.performer = performer;
        this.target = target;
        this.action = action;
    }

    public IllegalAdministrativeActionException(@NotNull final String errorMessage, @NotNull final User performer,
                                                @NotNull final User target, @NotNull final AdministrativeAction action,
                                                @NotNull final Throwable cause) {
        super(errorMessage, cause);
        this.performer = performer;
        this.target = target;
        this.action = action;
    }

    public @NotNull User getPerformer() {
        return performer;
    }

    public @NotNull User getTarget() {
        return target;
    }

    public @NotNull AdministrativeAction getAdministrativeAction() {
        return action;
    }
}
