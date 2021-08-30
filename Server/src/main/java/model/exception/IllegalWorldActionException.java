package model.exception;

import org.jetbrains.annotations.NotNull;

public class IllegalWorldActionException extends FeedbackException {

    public IllegalWorldActionException(@NotNull final String message, @NotNull final String key,
                                       @NotNull final Object... arguments) {
        super(message, key, arguments);
    }
}
