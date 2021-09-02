package model.notification;

import model.MessageBundle;
import org.jetbrains.annotations.NotNull;
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
    @NotNull UUID getNotificationId();

    /**
     * Gibt die Nachricht der Benachrichtigung zurück.
     * @return Nachricht der Benachrichtigung.
     */
    @NotNull MessageBundle getMessageBundle();

    /**
     * Gibt den Zeitpunkt zurück, an dem die Benachrichtigung erhalten wurde.
     * @return Zeitstempel der Benachrichtigung.
     */
    @NotNull LocalDateTime getTimestamp();

    /**
     * Gibt die Art der Benachrichtigung zurück.
     * @return Art der Benachrichtigung.
     */
    @NotNull NotificationType getType();

    /**
     * Information, ob diese Benachrichtigung vom Empfänger bereits gelesen wurde.
     * @return true, wenn diese Benachrichtigung bereits gelesen wurde, sonst false.
     */
    boolean isRead();

    /**
     * Information, ob diese Benachrichtigung vom Empfänger angenommen wurde.
     * @return true, wenn diese Benachrichtigung angenommen wurde.
     */
    boolean isAccepted();

    /**
     * Information, ob diese Benachrichtigung vom Empfänger abgelehnt wurde.
     * @return true, wenn diese Benachrichtigung abgelehnt wurde.
     */
    boolean isDeclined();
}