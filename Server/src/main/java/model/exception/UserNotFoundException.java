package model.exception;

import org.jetbrains.annotations.NotNull;
import java.util.UUID;

public class UserNotFoundException extends Exception {

    private UUID userID;
    private String username;

    public UserNotFoundException(@NotNull final String errorMessage, @NotNull final UUID userID) {
        super(errorMessage);
        this.userID = userID;
    }

    public UserNotFoundException(@NotNull final String errorMessage, @NotNull final String username) {
        super(errorMessage);
        this.username = username;
    }

    public UserNotFoundException(@NotNull final String errorMessage, @NotNull final UUID userID,
                                 @NotNull final String username) {
        super(errorMessage);
        this.userID = userID;
        this.username = username;
    }

    public UserNotFoundException(@NotNull final String errorMessage, @NotNull final UUID userID,
                                 @NotNull final Throwable cause) {
        super(errorMessage, cause);
        this.userID = userID;
    }

    public UserNotFoundException(@NotNull final String errorMessage, @NotNull final String username,
                                 @NotNull final Throwable cause) {
        super(errorMessage, cause);
        this.username = username;
    }

    public UserNotFoundException(@NotNull final String errorMessage, @NotNull final UUID userID,
                                 @NotNull final String username, @NotNull final Throwable cause) {
        super(errorMessage, cause);
        this.userID = userID;
        this.username = username;
    }

    public @NotNull UUID getUserID() {
        return userID;
    }

    public @NotNull String getUsername() {
        return username;
    }
}