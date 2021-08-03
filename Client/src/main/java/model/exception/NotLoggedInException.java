package model.exception;


public class NotLoggedInException extends Exception {
    public NotLoggedInException() {
        super("User is not logged in");
    }

    public NotLoggedInException(Throwable cause) {
        super("User is not logged in", cause);
    }
}
