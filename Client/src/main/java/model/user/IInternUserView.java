package model.user;

import model.context.Context;
import model.context.ContextID;
import model.context.spatial.ISpatialContextView;
import model.context.spatial.ContextMusic;
import model.context.spatial.SpatialContext;
import model.notification.INotificationView;
import model.notification.Notification;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
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
    @Nullable ISpatialContextView getCurrentWorld();

    /**
     * Gibt den aktuellen Raum des internen Benutzers zurück.
     * @return Der aktuelle Raum.
     */
    @Nullable ISpatialContextView getCurrentRoom();

    /**
     * Gibt die Benachrichtigungen des Benutzers im globalen Kontext zurück.
     * @return Die globalen Benachrichtigungen des Benutzers.
     * @see Notification
     * @see Context
     */
    @NotNull Map<UUID, INotificationView> getGlobalNotifications();

    /**
     * Gibt die Benachrichtigungen des Benutzers in der aktuellen Welt zurück.
     * @return Die lokalen Benachrichtigungen des Benutzers.
     * @see Notification
     * @see SpatialContext
     */
    @NotNull Map<UUID, INotificationView> getWorldNotifications();

    /**
     * Gibt die abzuspielende Musik zurück.
     * @return Abzuspielende Musik.
     */
    @Nullable ContextMusic getMusic();

    /**
     * Gibt zurück, ob der Benutzer gerade Sprachnachrichten versenden darf.
     * @return true, wenn er Sprachnachrichten versenden darf, sonst false.
     */
    boolean canTalk();

    /**
     * Gibt die Kontexte zurück, mit denen der Benutzer gerade interagieren kann.
     * @return Kontexte, mit denen der Benutzer gerade interagieren kann.
     */
    @NotNull Map<ContextID, ISpatialContextView> getCurrentInteractables();
}