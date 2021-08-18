package model.exception;

import org.jetbrains.annotations.NotNull;

public class IllegalAccountActionException extends Exception {

    private final String clientMessageKey;

    public IllegalAccountActionException(@NotNull final String errorMessage, @NotNull final String clientMessageKey) {
        super(errorMessage);
        this.clientMessageKey = clientMessageKey;
    }

    public IllegalAccountActionException(@NotNull final String errorMessage, @NotNull final String clientMessageKey,
                                         @NotNull final Throwable cause) {
        super(errorMessage, cause);
        this.clientMessageKey = clientMessageKey;
    }

    public @NotNull String getClientMessageKey() {
        return clientMessageKey;
    }
}
