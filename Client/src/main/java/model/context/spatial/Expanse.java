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

    public Expanse(final float posX, final float posY, final float width, final float height) {
        this(new Location(posX, posY), width, height);
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
     * Überprüft, ob die gegebene Koordinate eine maximale Distanz zur räumlichen Ausdehnung eines Kontextes hat.
     * @param posX Zu überprüfende X-Koordinate.
     * @param posY Zu überprüfende Y-Koordinate.
     * @param distance Maximale Distanz.
     * @return true, wenn die Koordinaten die maximale Distanz zur Ausdehnung haben, sonst false.
     */
    public boolean isAround(final float posX, final float posY, final float distance) {
        return isPosXInRange(posX, distance) && isPosYInRange(posY, distance);
    }

    /**
     * Überprüft, ob die gegebene Koordinate in der gegebenen Richtung eine maximale Distanz zur räumlichen
     * Ausdehnung eines Kontextes hat.
     * @param direction Zu überprüfende Richtung.
     * @param posX Zu überprüfende X-Koordinate.
     * @param posY Zu überprüfende Y-Koordinate.
     * @param distance Maximale Distanz.
     * @return true, wenn die Koordinaten die maximale Distanz zur Ausdehnung haben, sonst false.
     */
    public boolean isAround(@NotNull final Direction direction, final float posX, final float posY, final float distance) {
        if (isIn(posX, posY)) {
            return true;
        }

        switch (direction) {
            case UP:
                return bottomLeft.getPosY() - distance <= posY && posY <= bottomLeft.getPosY()
                        && isPosXInRange(posX, distance);

            case RIGHT:
                return bottomLeft.getPosX() - distance <= posX && posX <= bottomLeft.getPosX()
                        && isPosYInRange(posY, distance);

            case DOWN:
                return bottomLeft.getPosY() + height + distance >= posY && posY >= bottomLeft.getPosY() + height
                        && isPosXInRange(posX, distance);

            case LEFT:
                return bottomLeft.getPosX() + width + distance >= posX && posX >= bottomLeft.getPosX() + width
                        && isPosYInRange(posY, distance);
        }

        return false;
    }

    private boolean isPosXInRange(final float posX, final float distance) {
        return bottomLeft.getPosX() - distance <= posX && posX <= bottomLeft.getPosX() + width + distance;
    }

    private boolean isPosYInRange(final float posY, final float distance) {
        return bottomLeft.getPosY() - distance <= posY && posY <= bottomLeft.getPosY() + height + distance;
    }

    /**
     * Gibt den Mittelpunkt der Ausdehnung zurück.
     * @return Mittelpunkt der Ausdehnung.
     */
    public @NotNull Location getCenter() {
        return new Location(bottomLeft.getPosX() + width / 2, bottomLeft.getPosY() + height / 2);
    }
}
