package model.Notification;

import model.MessageBundle;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Eine Schnittstelle, welche der View Zugriff auf Parameter einer Benachrichtigung ermöglicht.
 */
public interface INotificationView {
    /**
     * Gibt die ID der Benachrichtigung zurück.
     * @return ID der Benachrichtigung.
     */
    public UUID getNotificationId();

    /**
     * Gibt die Nachricht der Benachrichtigung zurück.
     * @return Nachricht der Benachrichtigung.
     */
    public MessageBundle getMessageBundle();

    /**
     * Gibt den Zeitpunkt zurück, an dem die Benachrichtigung erhalten wurde.
     * @return Zeitstempel der Benachrichtigung.
     */
    public LocalDateTime getTimestamp();

    /**
     * Gibt zurück, ob die Benachrichtigung eine Anfrage repräsentiert.
     * @return true wenn die Benachrichtigung eine Anfrage ist, sonst false.
     */
    public boolean isRequest();
}
