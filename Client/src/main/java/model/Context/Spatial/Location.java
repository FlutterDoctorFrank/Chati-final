package model.Context.Spatial;

import model.Context.Global.GlobalContext;

/**
 * Eine Klasse, welche Positionen repräsentiert.
 */
public class Location implements ILocationView{
    private int posX;
    private int posY;

    public Location(int posX, int posY) {
        this.posX = posX;
        this.posY = posY;
    }

    @Override
    public int getPosX() {
        return posX;
    }

    @Override
    public int getPosY() {
        return posY;
    }

    @Override
    public SpatialContext getArea() {
        return GlobalContext.getInstance().getRoom().getArea(posX, posY);
    }
}
