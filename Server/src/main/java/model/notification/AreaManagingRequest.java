package model.notification;

import controller.network.ClientSender.SendAction;
import model.MessageBundle;
import model.communication.message.TextMessage;
import model.context.spatial.Area;
import model.role.Permission;
import model.user.User;
import model.user.account.UserAccountManager;
import org.jetbrains.annotations.NotNull;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Eine Klasse, welche Anfragen zum Erhalt der Rolle des Bereichsberechtigten repräsentiert.
 */
public class AreaManagingRequest extends Notification {

    /** Benutzer, der die Anfrage zum Erhalt der Rolle des Bereichsberechtigten stellt. */
    private final User requestingUser;

    /** Bereich, in dem die Rolle des Bereichsberechtigten angefragt wurde. */
    private final Area requestedArea;

    /** Zeitpunkt, ab dem der Benutzer die Rolle des Bereichsberechtigten haben soll. */
    private final LocalDateTime from;

    /** Zeitpunkt, bis zu dem der Benutzer die Rolle des Bereichsberechtigten haben soll. */
    private final LocalDateTime to;

    /**
     * Erzeugt eine neue Instanz der Anfrage zum Erhalt der Rolle des Bereichsberechtigten.
     * @param owner Der Eigentümer dieser Benachrichtigung.
     * @param requestingUser Benutzer, der die Anfrage zum Erhalt der Rolle des Bereichsberechtigten stellt.
     * @param requestedArea Bereich, für den die Rolle des Bereichsberechtigten angefragt wurde.
     * @param from Zeitpunkt, ab dem der Benutzer die Rolle des Bereichsberechtigten haben soll.
     * @param to Zeitpunkt, bis zu dem der Benutzer die Rolle des Bereichsberechtigten haben soll.
     */
    public AreaManagingRequest(@NotNull final User owner, @NotNull final User requestingUser,
                               @NotNull final Area requestedArea, @NotNull final LocalDateTime from,
                               @NotNull final LocalDateTime to) {
        super(NotificationType.AREA_MANAGING_REQUEST, owner, Objects.requireNonNull(requestingUser.getWorld()),
                new MessageBundle("request.area-manage.notification", requestingUser.getUsername(),
                        requestedArea.getContextName(), from, to));
        this.requestingUser = requestingUser;
        this.requestedArea = requestedArea;
        this.from = from;
        this.to = to;
    }

    @Override
    public void accept() {
        // Überprüfe, ob der Besitzer dieser Benachrichtigung noch die nötige Berechtigung besitzt.
        if (!owner.hasPermission(requestedArea, Permission.ASSIGN_AREA_MANAGER)) {
            TextMessage infoMessage = new TextMessage("request.area-manage.not-permitted", requestedArea.getContextName(),
                    Permission.ASSIGN_AREA_MANAGER);
            owner.send(SendAction.MESSAGE, infoMessage);
        }
        // Überprüfe, ob der anfragende Benutzer noch existiert.
        if (!UserAccountManager.getInstance().isRegistered(requestingUser.getUserId())) {
            TextMessage infoMessage = new TextMessage("request.area-manage.user-not-found", requestingUser.getUsername());
            owner.send(SendAction.MESSAGE, infoMessage);
            return;
        }
        // Überprüfe, ob der anfragende Benutzer diesen Bereich bereits zu einem Zeitpunkt reserviert.
        if (requestedArea.isReservedBy(requestingUser)) {
            TextMessage infoMessage = new TextMessage("request.area-manage.already-reserved", requestedArea.getContextName());
            owner.send(SendAction.MESSAGE, infoMessage);
            return;
        }
        // Überprüfe, ob dieser Bereich zu dem angefragten Zeitraum bereits reserviert wird.
        if (requestedArea.isReservedAt(from, to)) {
            TextMessage infoMessage = new TextMessage("request.area-manage.already-assigned", requestedArea.getContextName());
            owner.send(SendAction.MESSAGE, infoMessage);
            return;
        }

        if (owner.getWorld() == null) {
            throw new IllegalStateException("Owners world is not available");
        }

        // Füge die Reservierung hinzu.
        requestedArea.addReservation(requestingUser, from, to);
        // Benachrichtige den Benutzer über die erfolgreiche Reservierung.
        MessageBundle messageBundle = new MessageBundle("request.area-manage.accepted", owner.getUsername(),
                requestedArea.getContextName(), from, to);
        Notification acceptNotification = new Notification(requestingUser, owner.getWorld(), messageBundle);
        requestingUser.addNotification(acceptNotification);
    }

    @Override
    public void decline() {
        // Überprüfe, ob der Besitzer dieser Benachrichtigung noch die nötige Berechtigung besitzt.
        if (!owner.hasPermission(requestedArea, Permission.ASSIGN_AREA_MANAGER)) {
            TextMessage infoMessage = new TextMessage("request.area-manage.not-permitted", requestedArea.getContextName(),
                    Permission.ASSIGN_AREA_MANAGER);
            owner.send(SendAction.MESSAGE, infoMessage);
        }
        // Überprüfe, ob der anfragende Benutzer noch existiert.
        if (!UserAccountManager.getInstance().isRegistered(requestingUser.getUserId())) {
            TextMessage infoMessage = new TextMessage("request.area-manage.user-not-found", requestingUser.getUsername());
            owner.send(SendAction.MESSAGE, infoMessage);
            return;
        }
        // Überprüfe, ob der anfragende Benutzer diesen Bereich bereits zu diesem Zeitpunkt reserviert.
        /*
        if (requestedArea.isReservedAtBy(requestingUser, from, to)) {
            TextMessage infoMessage = new TextMessage("request.area-manage.already-reserved", requestedArea.getContextName());
            owner.send(SendAction.MESSAGE, infoMessage);
            return;
        }
         */

        if (owner.getWorld() == null) {
            throw new IllegalStateException("Owners world is not available");
        }

        // Benachrichtige den Benutzer über die abgelehnte Anfrage.
        MessageBundle messageBundle = new MessageBundle("request.area-manage.declined", owner.getUsername(),
                requestedArea.getContextName(), from, to);
        Notification declineNotification = new Notification(requestingUser, owner.getWorld(), messageBundle);
        requestingUser.addNotification(declineNotification);
    }
}