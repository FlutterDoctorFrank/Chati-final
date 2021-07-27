package model.Context.Spatial;

import model.Context.Spatial.Location;

public class Expanse {
    private Location bottomLeft;
    private int width;
    private int height;

    public Expanse(Location bottomLeft, int width, int height) {
        this.bottomLeft = bottomLeft;
        this.width = width;
        this.height = height;
    }

    public boolean isIn(int posX, int posY) {
        return bottomLeft.getPosX() <= posX && posX <= bottomLeft.getPosX() + width
                && bottomLeft.getPosY() <= posY && posY <= bottomLeft.getPosY() + height;
    }
}
