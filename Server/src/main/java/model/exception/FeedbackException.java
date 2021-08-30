package model.exception;

import model.MessageBundle;
import org.jetbrains.annotations.NotNull;

public abstract class FeedbackException extends Exception {

    private final MessageBundle bundle;

    protected FeedbackException(@NotNull final String key, @NotNull final Object... arguments) {
        this.bundle = new MessageBundle(key, arguments);
    }

    protected FeedbackException(@NotNull final String message, @NotNull final String key,
                                @NotNull final Object... arguments) {
        super(message);
        this.bundle = new MessageBundle(key, arguments);
    }

    protected FeedbackException(@NotNull final String message, @NotNull final Throwable cause,
                                @NotNull final String key, @NotNull final Object... arguments) {
        super(message, cause);
        this.bundle = new MessageBundle(key, arguments);
    }

    public @NotNull MessageBundle getMessageBundle() {
        return this.bundle;
    }
}
