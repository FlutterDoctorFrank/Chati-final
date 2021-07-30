package model.context.spatial;

import model.context.ContextID;
import model.context.IContext;

import java.util.Map;

/**
 * Ein Interface, welche dem Controller Methoden zur Verwaltung von räumlichen Kontexten bereitstellt. Wird von
 * {@link SpatialContext} implementiert.
 */
public interface ISpatialContext extends IContext {

    /**
     * Gibt den Typ des räumlichen Kontextes zurück.
     * @return Typ des räumlichen Kontextes.
     */
    SpatialContextType getSpatialContextType();

    /**
     * Gibt die Menge aller enthaltenen privaten Räume dieses Kontextes zurück.
     * @return Menge aller privaten Räume.
     */
    Map<ContextID, SpatialContext> getPrivateRooms();

    /**
     * Gibt die Karte des räumlichen Kontextes zurück, wenn dieser eine besitzt, sonst null.
     * @return Karte des räumlichen Kontextes.
     */
    SpatialMap getMap();
}
