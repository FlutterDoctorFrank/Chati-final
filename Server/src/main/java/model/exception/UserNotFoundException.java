package model.exception;

import java.util.UUID;

public class UserNotFoundException extends Exception {
    private UUID userID;
    private String username;

    public UserNotFoundException(String errorMessage, UUID userID) {
        super(errorMessage);
        this.userID = userID;
    }

    public UserNotFoundException(String errorMessage, String username) {
        super(errorMessage);
        this.username = username;
    }

    public UserNotFoundException(String errorMessage, UUID userID, String username) {
        super(errorMessage);
        this.userID = userID;
        this.username = username;
    }

    public UserNotFoundException(String errorMessage, UUID userID, Throwable cause) {
        super(errorMessage, cause);
        this.userID = userID;
    }

    public UserNotFoundException(String errorMessage, String username, Throwable cause) {
        super(errorMessage, cause);
        this.username = username;
    }

    public UserNotFoundException(String errorMessage, UUID userID, String username, Throwable cause) {
        super(errorMessage, cause);
        this.userID = userID;
        this.username = username;
    }

    public UUID getUserID() {
        return userID;
    }

    public String getUsername() {
        return username;
    }
}