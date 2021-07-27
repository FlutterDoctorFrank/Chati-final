package model.Exceptions;

import java.util.UUID;

public class UserNotFoundException extends Exception {
    private UUID userID;
    private String username;

    public UserNotFoundException(String errorMessage, UUID userID) {
        super(errorMessage);
        this.userID = userID;
    }

    public UUID getUserID() {
        return userID;
    }

    public String getUsername() {
        return username;
    }
}