package model.context.spatial;

/**
 * Repräsentiert die räumliche Ausdehnung eines Kontextes.
 */
public class Expanse {

    /** Position der unteren linken Ecke des Kontextes. */
    private final Location bottomLeft;

    /** Breite des Kontextes. */
    private final int width;

    /** Höhe des Kontextes. */
    private final int height;

    /**
     * Erzeugt eine neue Instanz der räumlichen Ausdehnung eines Kontextes.
     * @param bottomLeft Position der unteren linken Ecke des Kontextes.
     * @param width Breite des Kontextes.
     * @param height Höhe des Kontextes.
     */
    public Expanse(Location bottomLeft, int width, int height) {
        this.bottomLeft = bottomLeft;
        this.width = width;
        this.height = height;
    }

    /**
     * Überprüft, ob sich gegebene Koordinaten in der räumlichen Ausdehnung eines Kontextes befinden.
     * @param posX Zu überprüfende X-Koordinate.
     * @param posY Zu überprüfende Y-Koordinate.
     * @return true, wenn sich die Koordinaten in der Ausdehnung befinden, sonst false.
     */
    public boolean isIn(int posX, int posY) {
        return isAround(posX, posY, 0);
    }

    /**
     * Überprüft, ob gegebene Koordinate eine maximale Distanz zur räumlichen Ausdehnung eines Kontextes haben.
     * @param posX Zu überprüfende X-Koordinate.
     * @param posY Zu überprüfende Y-Koordinate.
     * @param distance Maximale Distanz.
     * @return true, wenn die Koordinaten die maximale Distanz zur Ausdehnung haben, sonst false.
     */
    public boolean isAround(int posX, int posY, int distance) {
        return bottomLeft.getPosX() - distance <= posX && posX <= bottomLeft.getPosX() + distance + width
            && bottomLeft.getPosY() - distance <= posY && posY <= bottomLeft.getPosY() + distance + height;
    }

    /**
     * Gibt die Position der linken unteren Ecke des Kontextes zurück.
     * @return Position der linken unteren Ecke des Kontextes.
     */
    public Location getBottomLeft() {
        return bottomLeft;
    }
}
