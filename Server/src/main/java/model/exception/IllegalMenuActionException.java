package model.exception;

public class IllegalMenuActionException extends Exception {
    private final String clientMessageKey;

    public IllegalMenuActionException(String errorMessage, String clientMessageKey) {
        super(errorMessage);
        this.clientMessageKey = clientMessageKey;
    }

    public IllegalMenuActionException(String errorMessage, String clientMessageKey, Throwable cause) {
        super(errorMessage, cause);
        this.clientMessageKey = clientMessageKey;
    }

    public String getClientMessageKey() {
        return clientMessageKey;
    }
}
