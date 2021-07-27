package model.Context.Global;

import model.Context.Music;
import model.Context.Spatial.SpatialContext;
import model.Context.Spatial.SpatialContextType;
import model.Context.Spatial.SpatialMap;
import model.Exceptions.ContextNotFoundException;
import model.context.ContextID;

import java.util.Map;

/**
 * Eine Schnittstelle, welche dem Controller Methoden zum Zugriff auf die dem Client
 * bekannten Kontexte ermöglicht.
 */
public interface IGlobalContextController {
    /**
     * Aktualisiert die Liste aller Welten.
     * @param worlds: Ein HashSet mit der Menge der IDs aller Welten, sowie dem
     * zugehörigen Namen der Welten.
     * @see SpatialContext
     * @see SpatialContextType#WORLD
     */
    public void updateWorlds(Map<ContextID, String> worlds);

    /**
     * Aktualisiert die Liste aller privaten Räume in einer Welt.
     * @param worldId: ID der Welt, in der die Liste der privaten Räume aktualisiert
     * werden soll.
     * @param privateRooms: Ein HashSet mit der Menge der IDs aller privaten Räume, sowie dem zugehörigen Namen des Raums.
     * @throws ContextNotFoundException: wenn keine Welt mit der ID existiert.
     * @see SpatialContext
     * @see SpatialContextType#ROOM
     */
    public void updateRooms(ContextID worldId, Map<ContextID,String> privateRooms) throws ContextNotFoundException;

    /**
     * Setzt die Welt, in dem sich der interne Benutzer befindet.
     * @param worldId: ID der Welt.
     * @throws ContextNotFoundException: wenn keine Welt mit der ID existiert.
     * @see SpatialContext
     * @see SpatialContextType#WORLD
     */
    public void setWorld(ContextID worldId) throws ContextNotFoundException;

    /**
     * Setzt den Raum, in dem sich der interne Benutzer befindet und die Karte, die
     * angezeigt werden soll.
     * @param roomId: ID des Raums.
     * @param map: Karte, die angezeigt werden soll.
     * @throws ContextNotFoundException: wenn kein Raum mit der ID existiert.
     * @see SpatialContext
     * @see SpatialContextType#ROOM
     */
    public void setRoom(ContextID roomId, SpatialMap map) throws ContextNotFoundException;

    /**
     * Setzt die Musik, die in einem Kontext abgespielt werden soll.
     * @param spatialId: ID des räumlichen Kontextes, in dem die Musik abgespielt
     * werden soll.
     * @param music: Abzuspielende Musik.
     * @throws ContextNotFoundException: falls dem Client kein Kontext mit der
     * ID bekannt ist.
     * @see SpatialContext
     */
    public void setMusic(ContextID spatialId, Music music) throws ContextNotFoundException;
}
