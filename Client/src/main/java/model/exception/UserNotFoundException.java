package model.exception;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.UUID;

public class UserNotFoundException extends Exception {

    private UUID userID;
    private String username;

    public UserNotFoundException(@NotNull final String errorMessage, @NotNull final UUID userID) {
        super(errorMessage);
        this.userID = userID;
    }

    public UserNotFoundException(@NotNull final String errorMessage, @NotNull final String username,
                                 @NotNull final Throwable cause) {
        super(errorMessage, cause);
        this.username = username;
    }

    public @Nullable UUID getUserID() {
        return userID;
    }

    public @Nullable String getUsername() {
        return username;
    }
}