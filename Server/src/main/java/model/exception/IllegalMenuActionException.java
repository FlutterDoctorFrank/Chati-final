package model.exception;

import org.jetbrains.annotations.NotNull;

public class IllegalMenuActionException extends FeedbackException {

    public IllegalMenuActionException(@NotNull final String message, @NotNull final String key,
                                      @NotNull final Object... arguments) {
        super(message, key, arguments);
    }

    public IllegalMenuActionException(@NotNull final String message, @NotNull final Throwable cause,
                                      @NotNull final String key, @NotNull final Object... arguments) {
        super(message, cause, key, arguments);
    }
}
