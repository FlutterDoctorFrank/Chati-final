package model.notification;

import model.MessageBundle;
import model.context.Context;
import model.exception.IllegalNotificationActionException;
import model.user.User;

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

    /**
     * Erzeugt eine neue Instanz einer Benachrichtigung.
     * @param owner Der Eigentümer dieser Benachrichtigung.
     * @param owningContext Der Kontext, in dem der Eigentümer diese Benachrichtigung besitzt.
     * @param messageBundle Die übersetzbare Nachricht der Benachrichtigung zusammen mit deren Argumenten.
     */
    public Notification(User owner, Context owningContext, MessageBundle messageBundle) {
        this.notificationId = UUID.randomUUID();
        this.notificationType = NotificationType.NOTIFICATION;
        this.owner = owner;
        this.owningContext = owningContext;
        this.messageBundle = messageBundle;
        this.timestamp = LocalDateTime.now();
    }

    /**
     * Erzeugt eine neue Instanz einer Benachrichtigung. Wird zur Erzeugung von Objekten erbender Klassen verwendet.
     * @param type Typ der Benachrichtigung.
     * @param owner Der Eigentümer dieser Benachrichtigung.
     * @param owningContext Der Kontext, in dem der Eigentümer diese Benachrichtigung besitzt.
     * @param messageBundle Die übersetzbare Nachricht der Benachrichtigung zusammen mit deren Argumenten.
     */
    protected Notification(NotificationType type, User owner, Context owningContext, MessageBundle messageBundle) {
        this.notificationId = UUID.randomUUID();
        this.notificationType = type;
        this.owner = owner;
        this.owningContext = owningContext;
        this.messageBundle = messageBundle;
        this.timestamp = LocalDateTime.now();
    }

    @Override
    public UUID getNotificationId() {
        return notificationId;
    }

    @Override
    public Context getContext() {
        return owningContext;
    }

    @Override
    public MessageBundle getMessageBundle() {
        return messageBundle;
    }

    @Override
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    /**
     * Akzeptiere die Anfrage dieser Benachrichtigung. Eine Instanz dieser Klasse ist niemals eine Anfrage. Lediglich
     * Instanzen erbender Unterklassen können Anfragen sein.
     * @throws IllegalNotificationActionException wenn diese Benachrichtigung keine Anfrage ist.
     */
    public void accept() throws IllegalNotificationActionException {
        throw new IllegalNotificationActionException("This notification is not a request.", owner, this, true);
    }

    /**
     * Lehne die Anfrage dieser Benachrichtigung ab. Eine Instanz dieser Klasse ist niemals eine Anfrage. Lediglich
     * Instanzen erbender Unterklassen können Anfragen sein.
     * @throws IllegalNotificationActionException wenn diese Benachrichtigung keine Anfrage ist.
     */
    public void decline() throws IllegalNotificationActionException {
        throw new IllegalNotificationActionException("This notification is not a request.", owner, this, false);
    }

    @Override
    public NotificationType getNotificationType() {
        return notificationType;
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
