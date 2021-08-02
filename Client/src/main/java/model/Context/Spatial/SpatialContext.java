package model.Context.Spatial;

import model.Communication.CommunicationRegion;
import model.Context.Context;
import model.communication.CommunicationMedium;
import model.context.spatial.SpatialMap;

import java.util.NoSuchElementException;
import java.util.Set;

/**
 * Eine Klasse, welche einen räumlichen Kontext der Anwendung repräsentiert.
 */
public class SpatialContext extends Context implements ISpatialContextView{

    private SpatialMap map;
    private final Expanse expanse;

    public SpatialContext(String contextName, Context parent, CommunicationRegion communicationRegion,
                          Set<CommunicationMedium> communicationMedia, Expanse expanse) {
        super(contextName, parent, communicationRegion, communicationMedia);
        this.expanse = expanse;
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
            return children.values().stream().filter(child -> child.getExpanse().isIn(posX, posY))
                    .findFirst().orElseThrow().getArea(posX, posY);
        } catch (NoSuchElementException e) {
            return this;
        }
    }

    public Expanse getExpanse() {
        return expanse;
    }
    public void setMap(SpatialMap map) {
        this.map = map;
    }
}
