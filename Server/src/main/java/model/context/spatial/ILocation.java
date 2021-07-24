package model.context.spatial;

public interface ILocation {
    public int getPosX();
    public int getPosY();
    public ISpatialContext getRoom();
    public ISpatialContext getArea();
}
