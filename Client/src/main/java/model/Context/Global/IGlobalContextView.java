package model.Context.Global;

import model.Context.IContextView;
import model.Context.Spatial.ISpatialContextView;
import model.context.ContextID;

import java.util.Map;

/**
 * Eine Schnittstelle, welche der View Zugriff auf Parameter des globalen Kontexts ermöglicht.
 */
public interface IGlobalContextView extends IContextView {
    /**
     * Gibt die auf dem Server existierenden Welten zurück.
     * @return die existierenden Welten.
     */
    public Map<ContextID, ISpatialContextView> getWorlds();

    /**
     * Gibt die aktuelle Welt des internen Benutzers zurück.
     * @return die aktuelle Welt.
     */
    public ISpatialContextView getCurrentWold();

    /**
     * Gibt den aktuellen Raum des internen Benutzers zurück.
     * @return der aktuelle Raum.
     */
    public ISpatialContextView getCurrentRoom();
}
