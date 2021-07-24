package model.exception;

public class IllegalWorldActionException extends Exception {
    private final String clientMessageKey;

    public IllegalWorldActionException(String errorMessage, String clientMessageKey) {
        super(errorMessage);
        this.clientMessageKey = clientMessageKey;
    }

    public IllegalWorldActionException(String errorMessage, String clientMessageKey, Throwable cause) {
        super(errorMessage, cause);
        this.clientMessageKey = clientMessageKey;
    }

    public String getClientMessageKey() {
        return clientMessageKey;
    }
}
