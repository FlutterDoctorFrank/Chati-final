package model.Context.Spatial;

import model.Context.IContextView;
import model.context.spatial.SpatialMap;

/**
 * Eine Schnittstelle, welche der View Zugriff auf Parameter eines räumlichen Kontexts
 * ermöglicht.
 */
public interface ISpatialContextView extends IContextView {

    /**
     * Gibt die hinterlegte Karte des räumlichen Kontexts zurück.
     * @return Die Karte des Kontextes.
     */
    SpatialMap getMap();
}
