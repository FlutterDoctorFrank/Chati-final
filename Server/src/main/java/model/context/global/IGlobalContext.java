package model.context.global;

import model.context.ContextID;
import model.context.IContext;
import model.context.spatial.SpatialMap;
import model.context.spatial.SpatialContext;
import model.exception.ContextNotFoundException;
import model.exception.IllegalWorldActionException;
import model.exception.NoPermissionException;
import model.exception.UserNotFoundException;

import java.util.Map;
import java.util.UUID;

/**
 * Ein Interface, welche dem Controller Methoden zur Verwaltung vom globalen Kontext
 * bereitstellt.
 */
public interface IGlobalContext extends IContext {

    /**
     * Erzeugt eine Welt im Namen eines Benutzers, sofern dieser die nötige Berechtigung dafür besitzt.
     * @param performerId ID des Benutzers, der die Welt erstellen soll.
     * @param map Karte mit der die Welt erzeugt werden soll.
     * @param worldname Name mit der die Welt erzeugt werden soll.
     * @throws UserNotFoundException wenn kein Benutzer mit der ID existiert.
     * @throws NoPermissionException wenn der ausführende Benutzer nicht die nötige Berechtigung besitzt.
     * @throws IllegalWorldActionException wenn der Name der Welt bereits existiert oder nicht das richtige Format hat.
     * @see model.context.spatial.ISpatialContext
     * @see model.role.Permission#MANAGE_WORLDS
     */
    void createWorld(UUID performerId, String worldname, SpatialMap map) throws UserNotFoundException, NoPermissionException, IllegalWorldActionException;

    /**
     * Entfernt eine Welt im Namen eines Benutzers, sofern dieser die nötige Berechtigung dafür besitzt.
     * @param performerId ID des Benutzers, der die Welt entfernen soll.
     * @param worldId ID der Welt, die entfernt werden soll.
     * @throws UserNotFoundException wenn kein Benutzer mit der ID existiert.
     * @throws ContextNotFoundException wenn keine Welt mit der ID existiert.
     * @throws NoPermissionException wenn der Benutzer nicht die nötige Berechtigung besitzt.
     * @see model.context.spatial.ISpatialContext
     * @see model.role.Permission#MANAGE_WORLDS
     */
    void removeWorld(UUID performerId, ContextID worldId) throws UserNotFoundException, NoPermissionException, ContextNotFoundException;

    /**
     * Gibt die Menge aller Welten zurück.
     * @return Menge aller Welten.
     */
    Map<ContextID, SpatialContext> getWorlds();
}
