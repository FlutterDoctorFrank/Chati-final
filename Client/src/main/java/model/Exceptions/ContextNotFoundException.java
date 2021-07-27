package model.Exceptions;

import model.context.ContextID;

public class ContextNotFoundException extends Exception {
    private final ContextID contextID;

    public ContextNotFoundException(String errorMessage, ContextID contextID) {
        super(errorMessage);
        this.contextID = contextID;
    }

    public ContextNotFoundException(String errorMessage, ContextID contextID, Throwable cause) {
        super(errorMessage, cause);
        this.contextID = contextID;
    }

    public ContextID getContextID() {
        return contextID;
    }
}