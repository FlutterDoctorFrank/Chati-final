package model.context.spatial;

import org.jetbrains.annotations.NotNull;

/**
 * Repräsentiert die räumliche Ausdehnung eines Kontextes.
 */
public class Expanse {

    /** Position der unteren linken Ecke des Kontextes. */
    private final Location bottomLeft;

    /** Breite des Kontextes. */
    private final float width;

    /** Höhe des Kontextes. */
    private final float height;

    /**
     * Erzeugt eine neue Instanz der räumlichen Ausdehnung eines Kontextes.
     * @param bottomLeft Position der unteren linken Ecke des Kontextes.
     * @param width Breite des Kontextes.
     * @param height Höhe des Kontextes.
     */
    public Expanse(@NotNull final Location bottomLeft, final float width, final float height) {
        this.bottomLeft = bottomLeft;
        this.width = width;
        this.height = height;
    }

    /**
     * Erzeugt eine neue Instanz der räumlichen Ausdehnung eines Kontextes.
     * @param room Der Raum des Kontextes.
     * @param posX Die X Position der unteren linken Ecke des Kontexts.
     * @param posY Die Y Position der unteren linken Ecke des Kontexts.
     * @param width Breite des Kontextes.
     * @param height Höhe des Kontextes.
     */
    public Expanse(@NotNull final Room room, final float posX, final float posY, final float width, final float height) {
        this.bottomLeft = new Location(room, Direction.UP, posX, posY);
        this.width = width;
        this.height = height;
    }

    /**
     * Überprüft, ob sich gegebene Koordinaten in der räumlichen Ausdehnung eines Kontextes befinden.
     * @param posX Zu überprüfende X-Koordinate.
     * @param posY Zu überprüfende Y-Koordinate.
     * @return true, wenn sich die Koordinaten in der Ausdehnung befinden, sonst false.
     */
    public boolean isIn(final float posX, final float posY) {
        return isAround(posX, posY, 0);
    }

    /**
     * Überprüft, ob gegebene Koordinate eine maximale Distanz zur räumlichen Ausdehnung eines Kontextes haben.
     * @param posX Zu überprüfende X-Koordinate.
     * @param posY Zu überprüfende Y-Koordinate.
     * @param distance Maximale Distanz.
     * @return true, wenn die Koordinaten die maximale Distanz zur Ausdehnung haben, sonst false.
     */
    public boolean isAround(final float posX, final float posY, final float distance) {
        return bottomLeft.getPosX() - distance <= posX && posX <= bottomLeft.getPosX() + distance + width
            && bottomLeft.getPosY() - distance <= posY && posY <= bottomLeft.getPosY() + distance + height;
    }

    /**
     * Gibt die Position der linken unteren Ecke des Kontextes zurück.
     * @return Position der linken unteren Ecke des Kontextes.
     */
    public @NotNull Location getBottomLeft() {
        return bottomLeft;
    }

    public int getWidth() {
        return Math.round(this.width);
    }

    public int getHeight() {
        return Math.round(this.height);
    }
}