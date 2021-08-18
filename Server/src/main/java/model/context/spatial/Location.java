package model.context.spatial;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.Objects;

/**
 * Eine Klasse, welche Positionen repräsentiert.
 */
public class Location implements ILocation {

    /** Der Raum, auf den sich diese Position bezieht. */
    private final Room room;

    /** X-Koordinate der Position. */
    private int posX;

    /** Y-Koordinate der Position. */
    private int posY;

    /**
     * Erzeugt eine neue Instanz einer Position.
     * @param room Der Raum auf den sich diese Position bezieht.
     * @param posX Die X-Koordinate der Position.
     * @param posY Die Y-Koordinate der Position.
     */
    public Location(@NotNull final Room room, final int posX, final int posY) {
        this.room = room;
        setPosition(posX, posY);
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
    public @NotNull Room getRoom() {
        return room;
    }

    @Override
    public @NotNull Area getArea() {
        return room.getArea(posX, posY);
    }

    /**
     * Setzt die Position auf die übergebenen Koordinaten.
     * @param posX Zu setzende X-Koordinate.
     * @param posY Zu setzende Y-Koordinate.
     */
    public void setPosition(final int posX, final int posY) {
        this.posX = posX;
        this.posY = posY;
    }

    /**
     * Ermittelt die Distanz zu einer anderen Position.
     * @param location Zu überprüfende andere Position.
     * @return Distanz zur anderen Position.
     */

    public int distance(@NotNull final Location location) {
        // Sollte niemals eintreffen.
        if (!room.equals(location.getRoom())) {
            throw new IllegalArgumentException("Location is in a different room.");
        }
        return (int) Math.sqrt(Math.pow((posX - location.posX), 2) + Math.pow((posY - location.posY), 2));
    }

    @Override
    public boolean equals(@Nullable final Object object) {
        if (this == object) {
            return true;
        }

        if (object == null || getClass() != object.getClass()) {
            return false;
        }

        final Location location = (Location) object;
        return posX == location.posX && posY == location.posY && room.equals(location.room);
    }

    @Override
    public int hashCode() {
        return Objects.hash(room, posX, posY);
    }
}