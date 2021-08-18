package model.exception;

import model.user.User;
import org.jetbrains.annotations.NotNull;

public class IllegalPositionException extends Exception {
    private final User user;
    private final int posX;
    private final int posY;

    public IllegalPositionException(@NotNull final String errorMessage, @NotNull final User user,
                                    final int posX, final int posY) {
        super(errorMessage);
        this.user = user;
        this.posX = posX;
        this.posY = posY;
    }

    public IllegalPositionException(@NotNull final String errorMessage, @NotNull final User user,
                                    final int posX, final int posY, @NotNull final Throwable cause) {
        super(errorMessage, cause);
        this.user = user;
        this.posX = posX;
        this.posY = posY;
    }

    public @NotNull User getUser() {
        return user;
    }

    public int getPosX() {
        return posX;
    }

    public int getPosY() {
        return posY;
    }
}
