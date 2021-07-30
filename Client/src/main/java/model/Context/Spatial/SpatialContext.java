package model.Context.Spatial;

import model.Context.Context;
import model.context.ContextID;
import model.context.spatial.SpatialContextType;
import model.context.spatial.SpatialMap;
import view.Screens.IModelObserver;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

/**
 * Eine Klasse, welche einen räumlichen Kontext der Anwendung repräsentiert.
 */
public class SpatialContext extends Context implements ISpatialContextView{

    private SpatialMap map;
    private Map<ContextID, SpatialContext> rooms;
    private SpatialContextType spatialContextType;
    private boolean moveable;
    private boolean interactable;
    private SpatialContext[][] areaMap;
    private Expanse expanse;


    public SpatialContext(String contextName, Context parent, ContextID contextId) {
        super(contextName, parent, contextId);
    }


    @Override
    public boolean canMoveIn() {
        return moveable;
    }

    @Override
    public boolean canInteractWith() {
        return interactable;
    }

    @Override
    public Map<ContextID, ISpatialContextView> getPrivateRooms() {
        return rooms.entrySet().stream().collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Override
    public SpatialMap getMap() {
        return map;
    }


    /**
     * ermittelt anhand der X-und Y- Koordinate den innersten Kontext einer Stelle
     * @param posX X-Koordinate
     * @param posY Y-Koordinate
     * @return innersten Kontext
     */
    public SpatialContext getArea(int posX, int posY) {
        try {
            return children.entrySet().stream().filter(entry -> entry.getValue().getExpanse().isIn(posX, posY))
                    .findFirst().orElseThrow().getValue().getArea(posX, posY);
        } catch (NoSuchElementException e) {
            return this;
        }
    }

    public Expanse getExpanse() {
        return expanse;
    }
    public void setRooms(Map<ContextID, SpatialContext> rooms) {
        this.rooms = rooms;
    }
    public void setMap(SpatialMap map) {
        this.map = map;
    }
    public Map<ContextID, SpatialContext> getRooms() {
        return rooms;
    }
    public SpatialContext[][] getAreaMap() {
        return areaMap;
    }
}
