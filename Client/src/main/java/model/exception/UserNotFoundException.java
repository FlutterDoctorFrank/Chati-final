package model.exception;

import java.util.UUID;

public class UserNotFoundException extends Exception {
    private UUID userID;

    public UserNotFoundException(String errorMessage, UUID userID) {
        super(errorMessage);
        this.userID = userID;
    }

    public UUID getUserID() {
        return userID;
    }
}