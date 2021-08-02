package model.Context.Spatial;

/**
 * diese Klasse repräsentiert eine 2-dimensionale Ausdehnung an einer Position
 */
public class Expanse {
    /**
     * linke, untere Ecke der Ausdehnung
     */
    private final Location bottomLeft;
    /**
     * Breite der Ausdehnung
     */
    private final int width;
    /**
     * Höhe der Ausdehnung
     */
    private final int height;

    public Expanse(Location bottomLeft, int width, int height) {
        this.bottomLeft = bottomLeft;
        this.width = width;
        this.height = height;
    }

    /**
     * prüft, ob ein Punkt innerhalb der Ausdehnung ist
     * @param posX X-Koordinate des Punktes
     * @param posY Y-Koordinate des Punktes
     * @return true, wenn der Punkt innerhalb der Ausdehnung ist, sonst false
     */
    public boolean isIn(int posX, int posY) {
        return bottomLeft.getPosX() <= posX && posX <= bottomLeft.getPosX() + width
                && bottomLeft.getPosY() <= posY && posY <= bottomLeft.getPosY() + height;
    }
}
