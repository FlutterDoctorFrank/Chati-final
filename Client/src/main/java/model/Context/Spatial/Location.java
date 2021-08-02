package model.Context.Spatial;

import model.Context.Global.GlobalContext;

import java.util.Objects;

/**
 * Eine Klasse, welche Positionen repräsentiert.
 */
public class Location implements ILocationView{
    private final int posX;
    private final int posY;

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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Location location = (Location) o;
        return posX == location.posX && posY == location.posY;
    }

    @Override
    public int hashCode() {
        return Objects.hash(posX, posY);
    }

    @Override
    public SpatialContext getArea() {
        return GlobalContext.getInstance().getRoom().getArea(posX, posY);
    }
}
