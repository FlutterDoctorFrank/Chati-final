package model.exception;

import org.jetbrains.annotations.NotNull;

public class EncryptionException extends Exception {

    public EncryptionException(@NotNull final String message, @NotNull final Throwable cause) {
        super(message, cause);
    }
}
