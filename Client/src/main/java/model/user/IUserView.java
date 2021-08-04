package model.user;

import model.context.spatial.ILocationView;
import model.context.spatial.Location;
import model.context.spatial.SpatialContext;
import model.context.spatial.SpatialContextType;
import model.role.Role;

import java.util.Set;
import java.util.UUID;

/**
 * Ein Interface, welche der View Zugriff auf Parameter eines Benutzers zur Verfügung stellt.
 */
public interface IUserView {

    /**
     * Gibt die ID des Benutzers zurück.
     * @return ID des Benutzer
     */
    UUID getUserId();

    /**
     * Gibt den aktuellen Benutzernamen des Benutzers zurück.
     * @return Benutzername des Benutzers
     */
    String getUsername();

    /**
     * Gibt den aktuellen Status des Benutzers zurück.
     * @return Status des Benutzers
     */
    Status getStatus();

    /**
     * Gibt den aktuell ausgewählten Avatar des Benutzers zurück.
     * @return Avatar des Benutzers
     */
    Avatar getAvatar();

    /**
     * Gibt zurück, ob der Benutzer als Freund markiert ist.
     * @return true, wenn der Benutzer als Freund markiert ist, sonst false.
     */
    boolean isFriend();

    /**
     * Gibt zurück, ob der Benutzer als ignoriert markiert ist.
     * @return true, wenn der Benutzer als ignoriert markiert ist, sonst false.
     */
    boolean isIgnored();

    /**
     * Gibt zurück, ob sich der intern angemeldete Benutzer zu diesem Benutzer teleportieren kann.
     * @return true, wenn eine Teleportation möglich ist, sonst false.
     */
    boolean canTeleportTo();

    /**
     * Gibt zurück, ob der Benutzer im aktuellen räumlichen Kontext des internen Benutzers als gemeldet markiert ist.
     * @return true, wenn der Benutzer im aktuellen räumlichen Kontext als gemeldet markiert ist, sonst false.
     */
    boolean isReported();

    /**
     * Gibt zurück, ob der Benutzer im aktuellen räumlichen Kontext des internen Benutzers als stummgeschaltet markiert
     * ist.
     * @return true, wenn der Benutzer im aktuellen Kontext als stummgeschaltet markiert ist, sonst false.
     */
    boolean isMuted();

    /**
     * Gibt zurück, ob der Benutzer im aktuellen räumlichen Kontext des internen Benutzers als gesperrt markiert ist.
     * @return true, wenn der Benutzer im aktuellen Kontext als gesperrt markiert ist, sonst false.
     */
    boolean isBanned();

    /**
     * Gibt zurück, ob sich der Benutzer in der aktuellen Welt des internen Benutzers befindet.
     * @return true, wenn der Benutzer in derselben Welt ist, sonst false.
     * @see SpatialContext
     */
    boolean isInCurrentWorld();

    /**
     * Gibt zurück, ob sich der Benutzer im aktuellen Raum des internen Benutzers befindet.
     * @return true, wenn der Benutzer im selben Raum ist, sonst false.
     * @see SpatialContext
     */
    boolean isInCurrentRoom();

    /**
     * Gibt die aktuelle Position des Benutzers innerhalb des aktuellen Raumes zurück.
     * @return Die Position des Benutzers.
     * @see Location
     */
    ILocationView getCurrentLocation();

    /**
     * Gibt die Rollen des Benutzers im globalen Kontext zurück.
     * @return die Rollen des Benutzers.
     */
    Set<Role> getGlobalRoles();

    /**
     * Gibt die Rollen des Benutzers in der aktuellen Welt und allen untergeordneten Kontexten dieser Welt zurück.
     * @return die Rollen des Benutzers in der aktuellen Welt.
     * @see SpatialContext
     */
    Set<Role> getWorldRoles();
}