package model.user;

import model.context.ContextID;
import model.context.spatial.ISpatialContextView;
import model.context.spatial.Music;
import model.context.spatial.SpatialContext;
import model.context.spatial.SpatialContextType;
import model.notification.INotificationView;
import model.notification.Notification;

import java.util.Map;
import java.util.UUID;

/**
 * Ein Interface, welche der View Zugriff auf Parameter des am Client angemeldeten Benutzers zur Verfügung stellt.
 */
public interface IInternUserView extends IUserView {

    /**
     * Gibt die aktuelle Welt des internen Benutzers zurück.
     * @return Die aktuelle Welt.
     */
    ISpatialContextView getCurrentWorld();

    /**
     * Gibt den aktuellen Raum des internen Benutzers zurück.
     * @return Der aktuelle Raum.
     */
    ISpatialContextView getCurrentRoom();

    /**
     * Gibt die Benachrichtigungen des Benutzers im globalen Kontext zurück.
     * @return Die globalen Benachrichtigungen des Benutzers.
     * @see Notification
     * @see model.context.Context
     */
    Map<UUID, INotificationView> getGlobalNotifications();

    /**
     * Gibt die Benachrichtigungen des Benutzers in der aktuellen Welt zurück.
     * @return Die lokalen Benachrichtigungen des Benutzers.
     * @see Notification
     * @see SpatialContext
     * @see SpatialContextType#WORLD
     */
    Map<UUID, INotificationView> getWorldNotifications();

    /**
     * Gibt die abzuspielende Musik zurück.
     * @return Abzuspielende Musik.
     */
    Music getMusic();

    /**
     * Gibt zurück, ob sich der Benutzer in einem privaten Raum aufält.
     * @return true, wenn der Benutzer in einem privaten Raum ist, sonst false.
     */
    boolean isInPrivateRoom();

    /**
     * Gibt die Kontexte zurück, mit denen der Benutzer gerade interagieren kann.
     * @return Kontexte, mit denen der Benutzer gerade interagieren kann.
     */
    Map<ContextID, ISpatialContextView> getCurrentInteractables();
}