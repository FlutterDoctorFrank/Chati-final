package model.notification;

import model.MessageBundle;
import model.context.Context;
import model.exception.IllegalNotificationActionException;
import model.user.User;
import org.jetbrains.annotations.NotNull;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Eine Klasse, welche Benachrichtigungen repräsentiert.
 */
public class Notification implements INotification {

    /** Wird zur eindeutigen Identifikation einer Benachrichtigung verwendet. */
    private final UUID notificationId;

    /** Typ der Benachrichtigung. Wird zur Hinterlegen von Benachrichtigungen in die Datenbank benötigt. */
    private final NotificationType notificationType;

    /** Der Eigentümer dieser Benachrichtigung. */
    protected final User owner;

    /** Der Kontext, in dem der Eigentümer diese Benachrichtigung besitzt. */
    private final Context owningContext;

    /** Die übersetzbare Nachricht der Benachrichtigung zusammen mit deren Argumenten. */
    private final MessageBundle messageBundle;

    /** Der Zeitstempel der Benachrichtigung. */
    private final LocalDateTime timestamp;

    /** Information, ob diese Benachrichtigung vom Empfänger bereits geöffnet wurde. */
    private boolean isRead;

    /** Information, ob diese Benachrichtigung angenommen wurde. */
    protected boolean isAccepted;

    /** Information, ob diese Benachrichtigung abgelehnt wurde. */
    protected boolean isDeclined;

    /**
     * Erzeugt eine neue Instanz einer Benachrichtigung.
     * @param owner Der Eigentümer dieser Benachrichtigung.
     * @param owningContext Der Kontext, in dem der Eigentümer diese Benachrichtigung besitzt.
     * @param messageBundle Die übersetzbare Nachricht der Benachrichtigung zusammen mit deren Argumenten.
     */
    public Notification(@NotNull final User owner, @NotNull final Context owningContext,
                        @NotNull final MessageBundle messageBundle) {
        this.notificationId = UUID.randomUUID();
        this.notificationType = NotificationType.INFORMATION;
        this.owner = owner;
        this.owningContext = owningContext;
        this.messageBundle = messageBundle;
        this.timestamp = LocalDateTime.now();
        this.isRead = false;
        this.isAccepted = false;
        this.isDeclined = false;
    }

    /**
     * Erzeugt eine neue Instanz einer Benachrichtigung. Wird zur Erzeugung von Objekten erbender Klassen verwendet.
     * @param type Typ der Benachrichtigung.
     * @param owner Der Eigentümer dieser Benachrichtigung.
     * @param owningContext Der Kontext, in dem der Eigentümer diese Benachrichtigung besitzt.
     * @param messageBundle Die übersetzbare Nachricht der Benachrichtigung zusammen mit deren Argumenten.
     */
    protected Notification(@NotNull final NotificationType type, @NotNull final User owner,
                           @NotNull final Context owningContext, @NotNull final MessageBundle messageBundle) {
        this(UUID.randomUUID(), type, owningContext, owner, LocalDateTime.now(), messageBundle, false, false, false);
    }

    public Notification(@NotNull final UUID notificationId, @NotNull final NotificationType type,
                        @NotNull final Context context, @NotNull final User owner,
                        @NotNull final LocalDateTime timestamp, @NotNull final MessageBundle messageBundle,
                        final boolean read, final boolean accepted, final boolean declined) {
        this.notificationId = notificationId;
        this.notificationType = type;
        this.owner = owner;
        this.owningContext = context;
        this.timestamp = timestamp;
        this.messageBundle = messageBundle;
        this.isRead = read;
        this.isAccepted = accepted;
        this.isDeclined = declined;
    }

    @Override
    public @NotNull UUID getNotificationId() {
        return notificationId;
    }

    @Override
    public @NotNull Context getContext() {
        return owningContext;
    }

    @Override
    public @NotNull MessageBundle getMessageBundle() {
        return messageBundle;
    }

    @Override
    public @NotNull LocalDateTime getTimestamp() {
        return timestamp;
    }

    @Override
    public @NotNull NotificationType getNotificationType() {
        return notificationType;
    }

    @Override
    public boolean isRead() {
        return isRead;
    }

    @Override
    public boolean isAccepted() {
        return isAccepted;
    }

    @Override
    public boolean isDeclined() {
        return isDeclined;
    }

    /**
     * Wird aufgerufen, wenn der Empfänger diese Benachrichtigung gelesen hat.
     */
    public void read() {
        this.isRead = true;
    }

    /**
     * Akzeptiere die Anfrage dieser Benachrichtigung. Eine Instanz dieser Klasse ist niemals eine Anfrage. Lediglich
     * Instanzen erbender Unterklassen können Anfragen sein.
     * @throws IllegalNotificationActionException wenn diese Benachrichtigung keine Anfrage ist.
     */
    public void accept() throws IllegalNotificationActionException {

        // Überprüfe, ob diese Benachrichtigung angenommen werden kann.
        if (notificationType == NotificationType.INFORMATION) {
            throw new IllegalNotificationActionException("This notification is not a request.", owner, this, true);
        }

        // Überprüfe, ob diese Benachrichtigung bereits angenommen oder abgelehnt wurde.
        if (isAccepted || isDeclined) {
            throw new IllegalNotificationActionException("This notification has already been managed.", owner, this, true);
        }

        this.isRead = true;
        this.isAccepted = true;
    }

    /**
     * Lehne die Anfrage dieser Benachrichtigung ab. Eine Instanz dieser Klasse ist niemals eine Anfrage. Lediglich
     * Instanzen erbender Unterklassen können Anfragen sein.
     * @throws IllegalNotificationActionException wenn diese Benachrichtigung keine Anfrage ist.
     */
    public void decline() throws IllegalNotificationActionException {

        // Überprüfe, ob diese Benachrichtigung abgelehnt werden kann.
        if (notificationType == NotificationType.INFORMATION) {
            throw new IllegalNotificationActionException("This notification is not a request.", owner, this, true);
        }

        // Überprüfe, ob diese Benachrichtigung bereits angenommen oder abgelehnt wurde.
        if (isAccepted || isDeclined) {
            throw new IllegalNotificationActionException("This notification has already been managed.", owner, this, true);
        }

        this.isRead = true;
        this.isDeclined = true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Notification that = (Notification) o;
        return notificationId.equals(that.notificationId) && owner.equals(that.owner);
    }

    @Override
    public int hashCode() {
        return Objects.hash(notificationId, owner);
    }
}
