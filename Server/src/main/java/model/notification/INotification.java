package model.notification;

import model.MessageBundle;
import model.context.IContext;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Ein Interface, welche dem Controller Methoden zur Verwaltung von Benachrichtigungen bereitstellt. Wird von
 * {@link Notification} implementiert.
 */
public interface INotification {

    /**
     * Gibt die ID der Benachrichtigung zurück.
     * @return ID der Benachrichtigung.
     */
    UUID getNotificationId();

    /**
     * Gibt den Kontext, in dem die Benachrichtigung gesendet wurde, zurück.
     * @return Kontext der Benachrichtigung.
     */
    IContext getContext();

    /**
     * Gibt die Nachricht der Benachrichtigung zurück.
     * @return Nachricht der Benachrichtigung.
     */
    MessageBundle getMessageBundle();

    /**
     * Gibt den Zeitpunkt zurück, an dem die Benachrichtigung erstellt wurde.
     * @return Zeitstempel der Benachrichtigung.
     */
    LocalDateTime getTimestamp();

    /**
     * Gibt zurück, um welche Art von Benachrichtigung es sich handelt.
     * @return Art der Benachrichtigung.
     */
    NotificationType getNotificationType();
}