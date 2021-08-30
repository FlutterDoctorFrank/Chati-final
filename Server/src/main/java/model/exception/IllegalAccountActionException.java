package model.exception;

import org.jetbrains.annotations.NotNull;

public class IllegalAccountActionException extends FeedbackException {

    public IllegalAccountActionException(@NotNull final String message, @NotNull final String key,
                                         @NotNull final Object... arguments) {
        super(message, key, arguments);
    }

    public IllegalAccountActionException(@NotNull final String message, @NotNull final Throwable cause,
                                         @NotNull final String key, @NotNull final Object... arguments) {
        super(message, cause, key, arguments);
    }
}
