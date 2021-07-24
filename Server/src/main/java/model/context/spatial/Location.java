package model.context.spatial;

public class Location implements ILocation {
    private SpatialContext room;
    private int posX;
    private int posY;

    public Location(SpatialContext room, int posX, int posY) {
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
    public SpatialContext getRoom() {
        return room;
    }

    @Override
    public SpatialContext getArea() {
        return room.getArea(posX, posY);
    }

    public void setPosition(int posX, int posY) {
        this.posX = posX;
        this.posY = posY;
    }

    public int distance(Location location) {
        return (int) Math.sqrt(Math.pow((posX - location.posX), 2) + Math.pow((posY - location.posY), 2));
    }
}
