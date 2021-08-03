package model.context.global;

import model.context.spatial.SpatialContext;
import model.exception.ContextNotFoundException;
import model.context.ContextID;
import model.context.spatial.Music;
import model.context.spatial.SpatialContextType;
import model.context.spatial.SpatialMap;

/**
 * Eine Schnittstelle, welche dem Controller Methoden zum Zugriff auf die dem Client
 * bekannten Kontexte ermöglicht.
 */
public interface IGlobalContextController {

    /**
     * Erstellt die Welt, in der sich ein Benutzer befindet
     * @param worldName: Name der Welt.
     * @see SpatialContext
     * @see SpatialContextType#WORLD
     */
    void setWorld(String worldName) throws ContextNotFoundException;

    /**
     * Erstellt den Raum, in dem sich der interne Benutzer befindet und initialisiert den kompletten Kontextbaum
     * dieses Raums. Ist die roomId die Id der aktuellen Welt, so ist diese der Wurzelkontext; ansonsten ist der
     * aktuelle Raum der Wurzelkontext.
     * @param roomName: Name des Raums.
     * @param map: Karte, über der der KOntextbaum erstellt wird.
     * @see SpatialContext
     * @see SpatialContextType#ROOM
     */
    void setRoom(String roomName, SpatialMap map) throws ContextNotFoundException;

    /**
     * Setzt die Musik, die in einem Kontext abgespielt werden soll.
     * @param spatialId: ID des räumlichen Kontextes, in dem die Musik abgespielt
     * werden soll.
     * @param music: Abzuspielende Musik.
     * @throws ContextNotFoundException: falls dem Client kein Kontext mit der
     * ID bekannt ist.
     * @see SpatialContext
     */
    void setMusic(ContextID spatialId, Music music) throws ContextNotFoundException;
}
