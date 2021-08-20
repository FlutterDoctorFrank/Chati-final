package model.context.spatial;

import model.user.UserManager;

import java.util.Objects;

/**
 * Eine Klasse, welche Positionen repräsentiert.
 */
public class Location implements ILocationView {

    /** X-Koordinate der Position. */
    private final float posX;

    /** Y-Koordinate der Position. */
    private final float posY;

    /**
     * Erzeugt eine neue Instanz einer Position.
     * @param posX Die X-Koordinate der Position.
     * @param posY Die Y-Koordinate der Position.
     */
    public Location(float posX, float posY) {
        this.posX = posX;
        this.posY = posY;
    }

    @Override
    public SpatialContext getArea() {
        return UserManager.getInstance().getInternUser().getCurrentRoom().getArea(posX, posY);
    }

    @Override
    public float getPosX() {
        return posX;
    }

    @Override
    public float getPosY() {
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
}