package model.context.spatial;

/**
 * Eine Klasse, welche Positionen repräsentiert.
 */
public class Location implements ILocation {

    /** Der Raum, auf den sich diese Position bezieht. */
    private Room room;

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
    public Location(Room room, int posX, int posY) {
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
    public Room getRoom() {
        return room;
    }

    @Override
    public Area getArea() {
        return room.getArea(posX, posY);
    }

    /**
     * Setzt die Position auf die übergebenen Koordinaten.
     * @param posX Zu setzende X-Koordinate.
     * @param posY Zu setzende Y-Koordinate.
     */
    public void setPosition(int posX, int posY) {
        this.posX = posX;
        this.posY = posY;
    }

    /**
     * Ermittelt die Distanz zu einer anderen Position.
     * @param location Zu überprüfende andere Position.
     * @return Distanz zur anderen Position, oder -1 wenn die Positionen sich nicht auf den selben Raum beziehen.
     */
    public int distance(Location location) {
        if (!room.equals(location.getRoom())) {
            return -1;
        }
        return (int) Math.sqrt(Math.pow((posX - location.posX), 2) + Math.pow((posY - location.posY), 2));
    }
}
