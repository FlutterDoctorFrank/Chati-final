package model.context.spatial;

import model.context.IContextView;

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
