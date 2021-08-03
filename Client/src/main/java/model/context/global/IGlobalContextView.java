package model.context.global;

import model.context.IContextView;
import model.context.spatial.ISpatialContextView;

/**
 * Eine Schnittstelle, welche der View Zugriff auf Parameter des globalen Kontexts ermöglicht.
 */
public interface IGlobalContextView extends IContextView {

    /**
     * Gibt die aktuelle Welt des internen Benutzers zurück.
     * @return die aktuelle Welt.
     */
    ISpatialContextView getCurrentWold();

    /**
     * Gibt den aktuellen Raum des internen Benutzers zurück.
     * @return der aktuelle Raum.
     */
    ISpatialContextView getCurrentRoom();
}
