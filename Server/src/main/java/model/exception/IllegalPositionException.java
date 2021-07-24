package model.exception;

import model.user.User;

public class IllegalPositionException extends Exception {
    private final User user;
    private final int posX;
    private final int posY;

    public IllegalPositionException(String errorMessage, User user, int posX, int posY) {
        super(errorMessage);
        this.user = user;
        this.posX = posX;
        this.posY = posY;
    }

    public IllegalPositionException(String errorMessage, User user, int posX, int posY, Throwable cause) {
        super(errorMessage, cause);
        this.user = user;
        this.posX = posX;
        this.posY = posY;
    }

    public User getUser() {
        return user;
    }

    public int getPosX() {
        return posX;
    }

    public int getPosY() {
        return posY;
    }
}
