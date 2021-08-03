package model.exception;

public class NotInWorldException extends Exception {
    public NotInWorldException(String errorMessage) {
        super(errorMessage);

    }

    public NotInWorldException(String errorMessage, Throwable cause) {
        super(errorMessage, cause);
    }
}