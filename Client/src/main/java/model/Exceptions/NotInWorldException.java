package model.Exceptions;

public class NotInWorldException extends Exception {
    public NotInWorldException(String errorMessage) {
        super(errorMessage);

    }

    public NotInWorldException(String errorMessage, Throwable cause) {
        super(errorMessage, cause);
    }
}