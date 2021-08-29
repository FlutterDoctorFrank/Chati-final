package model.user;

import model.context.spatial.ILocationView;
import model.context.spatial.Location;
import model.context.spatial.SpatialContext;
import model.role.Permission;
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
     * Gibt zurück, ob der intern angemeldete Benutzer diesen Benutzer in einen Raum einladen, oder ihn aus diesem
     * entfernen kann.
     * @return true, wenn eine Einladung in den Raum, oder eine Entfernung aus dem Raum möglich ist.
     */
    boolean canManageInRoom();

    /**
     * Gibt zurück, ob sich der intern angemeldete Benutzer zu diesem Benutzer teleportieren kann.
     * @return true, wenn eine Teleportation möglich ist, sonst false.
     */
    boolean canTeleportTo();

    /**
     * Gibt zurück, ob der intern angemeldete Benutzer diesen Benutzer melden kann.
     * @return true, wenn der Benutzer gemeldet werden kann, sonst false.
     */
    boolean canReport();

    /**
     * Gibt zurück, ob der intern angemeldete Benutzer diesen Benutzer stummschalten, oder die Stummschaltung aufheben
     * kann.
     * @return true, wenn dieser Benutzer stummgeschaltet, oder die Stummschaltung aufgehoben werden kann, sonst false.
     */
    boolean canMute();

    /**
     * Gibt zurück, ob der intern angemeldete Benutzer diesen Benutzer sperren oder entsperren kann.
     * @return true, wenn dieser Benutzer gesperrt oder entsperrt werden kann, sonst false
     */
    boolean canBan();

    /**
     * Gibt zurück, ob der intern angemeldete Benutzer diesem Benutzer die Rolle des Moderators erteilen oder entziehen
     * kann.
     * @return true, wenn diesem Benutzer die Rolle des Moderators erteilt oder entzogen werden kann.
     */
    boolean canAssignModerator();

    /**
     * Gibt zurück, ob der intern angemeldete Benutzer mit diesem Benutzer kommunizieren kann.
     * @return true, wenn eine Kommunikation möglich ist, sonst false.
     */
    boolean canCommunicateWith();

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
    ILocationView getLocation();

    /**
     * Gibt die Information zurück, ob der Benutzer zu seiner aktuellen Position teleportiert, oder mit einer
     * Laufanimation bewegt werden soll.
     * @return true, wenn der Benutzer teleportiert werden soll, sonst false.
     */
    boolean isTeleporting();

    /**
     * Gibt die Information zurück, ob sich dieser Benutzer schnell fortbewegt.
     * @return true, wenn der Benutzer sich schnell fortbewegt, sonst false.
     */
    boolean isSprinting();

    /**
     * Gibt zurück, ob der Benutzer eine Rolle im innersten Kontext oder einem übergeordneten Kontext besitzt,
     * in dem sich der intern angemeldete Benutzer befindet.
     * @param role Zu überprüfende Rolle.
     * @return true, wenn der Benutzer die Rolle besitzt, sonst false.
     */
    boolean hasRole(Role role);

    /**
     * Gibt zurück, ob der Benutzer eine Berechtigung im innersten Kontext oder einem übergeordneten Kontext besitzt,
     * in dem sich der intern angemeldete Benutzer befindet.
     * @param permission Zu überprüfende Berechtigung.
     * @return true, wenn der Benutzer die Berechtigung besitzt, sonst false.
     */
    boolean hasPermission(Permission permission);


    /**
     * Gibt die übergeordnetste Rolle des Benutzers zurück, die er im innersten Kontext oder einem übergeordneten
     * Kontext besitzt, in dem sich der intern angemeldete Benutzer befindet. Die Rollen sind wie folgt hierarchisch
     * gegliedert: {@link Role#OWNER}, {@link Role#ADMINISTRATOR}, {@link Role#MODERATOR}, {@link Role#ROOM_OWNER},
     * {@link Role#AREA_MANAGER}. Diese Gliederung wird für Anzeigezwecke genutzt und spiegelt keine tatsächliche
     * Hierarchie der Rollen wider, da nicht jede übergeordnete Rolle alle Berechtigungen aller untergeordneter
     * Rollen enthalten.
     * @return Übergeordnetste Rolle des Benutzers.
     */
    Role getHighestRole();
}