package model.exception;

import org.jetbrains.annotations.NotNull;

public class IllegalActionException extends Exception {

    public IllegalActionException(@NotNull final String errorMessage) {
        super(errorMessage);
    }

    public IllegalActionException(@NotNull final String errorMessage, @NotNull final Throwable cause) {
        super(errorMessage, cause);
    }
}
