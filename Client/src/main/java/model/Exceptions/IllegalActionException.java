package model.Exceptions;

public class IllegalActionException extends Exception {
    public IllegalActionException(String errorMessage) {
        super(errorMessage);
    }

    public IllegalActionException(String errorMessage, Throwable cause) {
        super(errorMessage, cause);
    }
}
