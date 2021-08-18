package model.exception;

import model.context.spatial.objects.Interactable;
import model.user.User;
import org.jetbrains.annotations.NotNull;

public class IllegalInteractionException extends Exception {

    private final User user;
    private Interactable interactable;

    public IllegalInteractionException(@NotNull final String errorMessage, @NotNull final User user) {
        super(errorMessage);
        this.user = user;
    }

    public IllegalInteractionException(@NotNull final String errorMessage, @NotNull final User user,
                                       @NotNull final Throwable cause) {
        super(errorMessage, cause);
        this.user = user;
    }

    public IllegalInteractionException(@NotNull final String errorMessage, @NotNull final User user,
                                       @NotNull final Interactable interactable) {
        super(errorMessage);
        this.user = user;
        this.interactable = interactable;
    }

    public IllegalInteractionException(@NotNull final String errorMessage, @NotNull final User user,
                                       @NotNull final Interactable interactable, @NotNull final Throwable cause) {
        super(errorMessage, cause);
        this.user = user;
        this.interactable = interactable;
    }

    public @NotNull User getUser() {
        return user;
    }

    public @NotNull Interactable getInteractable() {
        return interactable;
    }
}
