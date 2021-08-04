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
        return bottomLeft.getPosX() <= posX && posX <= bottomLeft.getPosX() + width
                && bottomLeft.getPosY() <= posY && posY <= bottomLeft.getPosY() + height;
    }
}
