package model.exception;

import model.user.IUser;
import org.jetbrains.annotations.NotNull;

public class IllegalPositionException extends Exception {

    private final IUser user;
    private final float posX;
    private final float posY;

    public IllegalPositionException(@NotNull final String errorMessage, @NotNull final IUser user,
                                    final float posX, final float posY) {
        super(errorMessage);
        this.user = user;
        this.posX = posX;
        this.posY = posY;
    }

    public IllegalPositionException(@NotNull final String errorMessage, @NotNull final IUser user,
                                    final float posX, final float posY, @NotNull final Throwable cause) {
        super(errorMessage, cause);
        this.user = user;
        this.posX = posX;
        this.posY = posY;
    }

    public @NotNull IUser getUser() {
        return user;
    }

    public float getPosX() {
        return posX;
    }

    public float getPosY() {
        return posY;
    }
}
