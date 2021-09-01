package model.user;

import model.context.ContextID;
import model.context.spatial.ISpatialContextView;
import model.context.spatial.ContextMusic;
import model.context.spatial.SpatialContext;
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
     */
    Map<UUID, INotificationView> getWorldNotifications();

    /**
     * Gibt die Information zurück, ob eine neue Benachrichtigung erhalten wurde und setzt diese Information zurück.
     * @return true, wenn eine neue Benachrichtigung erhalten wurde, sonst false.
     */
    boolean receivedNewNotification();

    /**
     * Gibt die abzuspielende Musik zurück.
     * @return Abzuspielende Musik.
     */
    ContextMusic getMusic();

    /**
     * Gibt zurück, ob der Benutzer gerade Sprachnachrichten versenden darf.
     * @return true, wenn er Sprachnachrichten versenden darf, sonst false.
     */
    boolean canTalk();

    /**
     * Gibt die Kontexte zurück, mit denen der Benutzer gerade interagieren kann.
     * @return Kontexte, mit denen der Benutzer gerade interagieren kann.
     */
    Map<ContextID, ISpatialContextView> getCurrentInteractables();
}