package model.context.spatial;

import model.user.InternUser;
import model.user.UserManager;
import org.jetbrains.annotations.NotNull;
import java.util.Objects;

/**
 * Eine Klasse, welche Positionen repräsentiert.
 */
public class Location implements ILocationView {

    /** X-Koordinate der Position. */
    private float posX;

    /** Y-Koordinate der Position. */
    private float posY;

    /** Richtung der Position */
    private Direction direction;

    /**
     * Erzeugt eine neue Instanz einer Position.
     * @param posX Die X-Koordinate der Position.
     * @param posY Die Y-Koordinate der Position.
     */
    public Location(final float posX, final float posY) {
        this.posX = posX;
        this.posY = posY;
        this.direction = Direction.UP;
    }

    /**
     * Erzeugt eine neue Instanz einer Position.
     * @param posX Die X-Koordinate der Position.
     * @param posY Die Y-Koordinate der Position.
     * @param direction Richtung, in der die Position gerichtet ist.
     */
    public Location(final float posX, final float posY, @NotNull final Direction direction) {
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
    public @NotNull Direction getDirection() {
        return direction;
    }

    @Override
    public @NotNull SpatialContext getArea() {
        InternUser intern = UserManager.getInstance().getInternUser();
        if (intern.getCurrentRoom() == null) {
            throw new IllegalStateException("User is not in a room");
        }
        return intern.getCurrentRoom().getArea(posX, posY);
    }

    /**
     * Setzt die Koordinaten dieser Position.
     * @param posX Neue X-Koordinate.
     * @param posY Neue Y-Koordinate.
     */
    public void setCoordinates(final float posX, final float posY) {
        this.posX = posX;
        this.posY = posY;
    }

    /**
     * Setzt die Richtung dieser Position.
     * @param direction Neue Richtung.
     */
    public void setDirection(@NotNull final Direction direction) {
        this.direction = direction;
    }

    /**
     * Ermittelt die Distanz zu einer anderen Position.
     * @param location Zu überprüfende andere Position.
     * @return Distanz zur anderen Position.
     */
    public int distance(@NotNull final Location location) {
        return (int) Math.sqrt(Math.pow((posX - location.posX), 2) + Math.pow((posY - location.posY), 2));
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