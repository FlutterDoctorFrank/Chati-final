package model.notification;

import model.MessageBundle;
import model.context.IContext;
import org.jetbrains.annotations.NotNull;
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
    @NotNull UUID getNotificationId();

    /**
     * Gibt den Kontext, in dem die Benachrichtigung gesendet wurde, zurück.
     * @return Kontext der Benachrichtigung.
     */
    @NotNull IContext getContext();

    /**
     * Gibt die Nachricht der Benachrichtigung zurück.
     * @return Nachricht der Benachrichtigung.
     */
    @NotNull MessageBundle getMessageBundle();

    /**
     * Gibt den Zeitpunkt zurück, an dem die Benachrichtigung erstellt wurde.
     * @return Zeitstempel der Benachrichtigung.
     */
    @NotNull LocalDateTime getTimestamp();

    /**
     * Gibt zurück, um welche Art von Benachrichtigung es sich handelt.
     * @return Art der Benachrichtigung.
     */
    @NotNull NotificationType getNotificationType();

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