package model.notification;

import model.MessageBundle;
import model.context.IContext;
import model.context.spatial.ISpatialContext;
import model.user.User;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Eine Schnittstelle, welche dem Controller Methoden zur Verwaltung von Benachrichtigungen bereitstellt. Wird von
 * {@link Notification} implementiert.
 */
public interface INotification {

    /**
     * Gibt die ID der Benachrichtigung zurück.
     * @return ID der Benachrichtigung.
     */
    public UUID getNotificationId();

    /**
     * Gibt den Kontext, in dem die Benachrichtigung gesendet wurde, zurück.
     * @return Kontext der Benachrichtigung.
     */
    public IContext getContext();

    /**
     * Gibt die Nachricht der Benachrichtigung zurück.
     * @return Nachricht der Benachrichtigung.
     */
    public MessageBundle getMessageBundle();

    /**
     * Gibt den Zeitpunkt zurück, an dem die Benachrichtigung erstellt wurde.
     * @return Zeitstempel der Benachrichtigung.
     */
    public LocalDateTime getTimestamp();
}
