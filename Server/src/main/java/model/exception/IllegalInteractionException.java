package model.exception;

import model.context.spatial.objects.IInteractable;
import model.user.IUser;
import org.jetbrains.annotations.NotNull;

public class IllegalInteractionException extends Exception {

    private final IUser user;
    private IInteractable interactable;

    public IllegalInteractionException(@NotNull final String errorMessage, @NotNull final IUser user) {
        super(errorMessage);
        this.user = user;
    }

    public IllegalInteractionException(@NotNull final String errorMessage, @NotNull final IUser user,
                                       @NotNull final Throwable cause) {
        super(errorMessage, cause);
        this.user = user;
    }

    public IllegalInteractionException(@NotNull final String errorMessage, @NotNull final IUser user,
                                       @NotNull final IInteractable interactable) {
        super(errorMessage);
        this.user = user;
        this.interactable = interactable;
    }

    public IllegalInteractionException(@NotNull final String errorMessage, @NotNull final IUser user,
                                       @NotNull final IInteractable interactable, @NotNull final Throwable cause) {
        super(errorMessage, cause);
        this.user = user;
        this.interactable = interactable;
    }

    public @NotNull IUser getUser() {
        return user;
    }

    public @NotNull IInteractable getInteractable() {
        return interactable;
    }
}
