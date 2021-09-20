package model.exception;

import model.user.AdministrativeAction;
import model.user.IUser;
import org.jetbrains.annotations.NotNull;

public class IllegalAdministrativeActionException extends Exception {

    private final IUser performer;
    private final IUser target;
    private final AdministrativeAction action;

    public IllegalAdministrativeActionException(@NotNull final String errorMessage, @NotNull final IUser performer,
                                                @NotNull final IUser target, @NotNull final AdministrativeAction action) {
        super(errorMessage);
        this.performer = performer;
        this.target = target;
        this.action = action;
    }

    public IllegalAdministrativeActionException(@NotNull final String errorMessage, @NotNull final IUser performer,
                                                @NotNull final IUser target, @NotNull final AdministrativeAction action,
                                                @NotNull final Throwable cause) {
        super(errorMessage, cause);
        this.performer = performer;
        this.target = target;
        this.action = action;
    }

    public @NotNull IUser getPerformer() {
        return performer;
    }

    public @NotNull IUser getTarget() {
        return target;
    }

    public @NotNull AdministrativeAction getAdministrativeAction() {
        return action;
    }
}
