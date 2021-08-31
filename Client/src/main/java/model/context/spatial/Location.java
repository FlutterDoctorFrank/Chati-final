package model.context.spatial;

import model.user.UserManager;

import java.util.Objects;

/**
 * Eine Klasse, welche Positionen repräsentiert.
 */
public class Location implements ILocationView {

    /** X-Koordinate der Position. */
    private float posX;

    /** Y-Koordinate der Position. */
    private float posY;

    /** Richtung, in die die Position gerichtet ist. */
    private Direction direction;

    /**
     * Erzeugt eine neue Instanz einer Position.
     * @param posX Die X-Koordinate der Position.
     * @param posY Die Y-Koordinate der Position.
     */
    public Location(float posX, float posY) {
        this.posX = posX;
        this.posY = posY;
        this.direction = Direction.DOWN;
    }

    /**
     * Erzeugt eine neue Instanz einer Position.
     * @param posX Die X-Koordinate der Position.
     * @param posY Die Y-Koordinate der Position.
     * @param direction Richtung, in der die Position gerichtet ist.
     */
    public Location(float posX, float posY, Direction direction) {
        this.posX = posX;
        this.posY = posY;
        this.direction = direction;
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
    public Direction getDirection() {
        return direction;
    }

    @Override
    public SpatialContext getArea() {
        return UserManager.getInstance().getInternUser().getCurrentRoom().getArea(posX, posY);
    }

    /**
     * Setzt die Koordinaten dieser Position.
     * @param posX Neue X-Koordinate.
     * @param posY Neue Y-Koordinate.
     */
    public void setCoordinates(float posX, float posY) {
        this.posX = posX;
        this.posY = posY;
    }

    /**
     * Setzt die Richtung dieser Position.
     * @param direction Neue Richtung.
     */
    public void setDirection(Direction direction) {
        this.direction = direction;
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