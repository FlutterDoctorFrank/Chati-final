package model.notification;

import model.MessageBundle;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Ein Interface, welche der View Zugriff auf Parameter einer Benachrichtigung ermöglicht.
 */
public interface INotificationView {

    /**
     * Gibt die ID der Benachrichtigung zurück.
     * @return ID der Benachrichtigung.
     */
    UUID getNotificationId();

    /**
     * Gibt die Nachricht der Benachrichtigung zurück.
     * @return Nachricht der Benachrichtigung.
     */
    MessageBundle getMessageBundle();

    /**
     * Gibt den Zeitpunkt zurück, an dem die Benachrichtigung erhalten wurde.
     * @return Zeitstempel der Benachrichtigung.
     */
    LocalDateTime getTimestamp();

    /**
     * Gibt die Art der Benachrichtigung zurück.
     * @return Art der Benachrichtigung.
     */
    NotificationType getType();
}