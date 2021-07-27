package model.Context.Spatial;

import model.Context.IContextView;
import model.context.ContextID;
import java.util.Map;

/**
 * Eine Schnittstelle, welche der View Zugriff auf Parameter eines räumlichen Kontexts
 * ermöglicht.
 */
public interface ISpatialContextView extends IContextView {
    /**
     * Gibt zurück, ob eine Bewegung innerhalb des räumlichen Kontexts möglich ist.
     * @return truewenn eine Bewegung möglich ist, sonst false.
     */
    public boolean canMoveIn();

    /**
     * Gibt zurück, ob mit dem räumlichen Kontext interagiert werden kann.
     * @return truewenn mit dem Kontext interagiert werden kann, sonst false.
     */
    public boolean canInteractWith();

    /**
     * Gibt die privaten Räume des räumlichen Kontexts zurück, falls dieser Kontext eine
     * Welt repräsentiert.
     * @return Die privaten Räume, oder null.
     * @see SpatialContext
     * @see SpatialContextType#ROOM
     */
    public Map<ContextID, ISpatialContextView> getPrivateRooms();

    /**
     * Gibt die hinterlegte Karte des räumlichen Kontexts zurück.
     * @return Die Karte des Kontextes.
     */
    public SpatialMap getMap();


}
