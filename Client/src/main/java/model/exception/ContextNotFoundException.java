package model.exception;

import model.context.ContextID;
import org.jetbrains.annotations.NotNull;

public class ContextNotFoundException extends Exception {
    
    private final ContextID contextID;

    public ContextNotFoundException(@NotNull final String errorMessage, @NotNull final ContextID contextID) {
        super(errorMessage);
        this.contextID = contextID;
    }

    public ContextNotFoundException(@NotNull final String errorMessage, @NotNull final ContextID contextID,
                                    @NotNull final Throwable cause) {
        super(errorMessage, cause);
        this.contextID = contextID;
    }

    public @NotNull ContextID getContextID() {
        return contextID;
    }
}