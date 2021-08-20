package model.exception;

import model.user.User;
import org.jetbrains.annotations.NotNull;

public class IllegalPositionException extends Exception {
    private final User user;
    private final float posX;
    private final float posY;

    public IllegalPositionException(@NotNull final String errorMessage, @NotNull final User user,
                                    final float posX, final float posY) {
        super(errorMessage);
        this.user = user;
        this.posX = posX;
        this.posY = posY;
    }

    public IllegalPositionException(@NotNull final String errorMessage, @NotNull final User user,
                                    final float posX, final float posY, @NotNull final Throwable cause) {
        super(errorMessage, cause);
        this.user = user;
        this.posX = posX;
        this.posY = posY;
    }

    public @NotNull User getUser() {
        return user;
    }

    public float getPosX() {
        return posX;
    }

    public float getPosY() {
        return posY;
    }
}
