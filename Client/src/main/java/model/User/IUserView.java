package model.User;

import model.Context.Global.GlobalContext;
import model.Context.Spatial.ILocationView;
import model.Context.Spatial.Location;
import model.Context.Spatial.SpatialContext;
import model.Notification.INotificationView;
import model.Notification.Notification;
import model.role.Role;
import model.user.Avatar;
import model.user.Status;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Eine Schnittstelle, welche der View Zugriff auf Parameter eines Benutzers zur Verfügung
 * stellt.
 */
public interface IUserView {
    /**
     * Gibt die ID des Benutzers zurück.
     * @return ID des Benutzer
     */
    public UUID getUserId();

    /**
     * Gibt den aktuellen Benutzernamen des Benutzers zurück.
     * @return Benutzername des Benutzers
     */
    public String getUsername();

    /**
     * Gibt den aktuellen Status des Benutzers zurück.
     * @return Status des Benutzers
     */
    public Status getStatus();

    /**
     * Gibt den aktuell ausgewählten Avatar des Benutzers zurück.
     * @return Avatar des Benutzers
     */
    public Avatar getAvatar();

    /**
     * Gibt zurück, ob der Benutzer als Freund markiert ist.
     * @return  true, wenn der Benutzer als Freund markiert ist, sonst false.
     */
    public boolean isFriend();

    /**
     * Gibt zurück, ob der Benutzer als ignoriert markiert ist.
     * @return true, wenn der Benutzer als ignoriert markiert ist, sonst false.
     */
    public boolean isIgnored();

    /**
     * Gibt zurück, ob der Benutzer im aktuellen räumlichen Kontext des internen Benutzers als gemeldet markiert ist.
     * @return true, wenn der Benutzer im aktuellen räumlichen Kontext als gemeldet
     * markiert ist, sonst false.
     */
    public boolean isReported();

    /**
     * Gibt zurück, ob der Benutzer im aktuellen räumlichen Kontext des internen Benutzers als stummgeschaltet markiert ist.
     * @return true, wenn der Benutzer im aktuellen Kontext als stummgeschaltet markiert ist, sonst false.
     */
    public boolean isMuted();

    /**
     * Gibt zurück, ob der Benutzer im aktuellen räumlichen Kontext des internen Benutzers als gesperrt markiert ist
     * @return true, wenn der Benutzer im aktuellen Kontext als gesperrt markiert ist,
     * sonst false.
     */
    public boolean isBanned();

    /**
     * Gibt zurück, ob sich der Benutzer in der aktuellen Welt des internen Benutzers
     * befindet.
     * @return true, wenn der Benutzer in derselben Welt ist, sonst false.
     * @see SpatialContext
     * @see SpatialContextType#WORLD
     */
    public boolean isInCurrentWorld();

    /**
     * Gibt zurück, ob sich der Benutzer im aktuellen Raum des internen Benutzers be-
     * findet.
     * @return true, wenn der Benutzer im selben Raum ist, sonst false.
     * @see SpatialContext
     * @see SpatialContextType#ROOM
     */
    public boolean isInCurrentRoom();

    /**
     * Gibt die aktuelle Position des Benutzers innerhalb des aktuellen Raumes zurück.
     * @return die Position des Benutzers.
     * @see Location
     */
    public ILocationView getCurrentLocation();

    /**
     * Gibt die Rollen des Benutzers im globalen Kontext zurück.
     * @return die Rollen des Benutzers.
     * @see GlobalContext
     */
    public Set<Role> getGlobalRoles();

    /**
     * Gibt die Rollen des Benutzers in der aktuellen Welt des internen Benutzers zurück.
     * @return die Rollen des Benutzers in der aktuellen Welt.
     * @see SpatialContext
     * @see SpatialContextType#WORLD
     */
    public Set<Role> getWorldRoles();

    /**
     * Gibt die Benachrichtigungen des Benutzers im globalen Kontext zurück.
     * @return die globalen Benachrichtigungen des Benutzers.
     * @see Notification
     * @see GlobalContext
     */
    public Map<UUID, INotificationView> getGlobalNotifications();


    /**
     * Gibt die Benachrichtigungen des Benutzers in der aktuellen Welt zurück.
     * @return die lokalen Benachrichtigungen des Benutzers.
     * @see Notification
     * @see SpatialContext
     * @see SpatialContextType#WORLD
     */
    public Map<UUID, INotificationView> getWorldNotifications();
}
