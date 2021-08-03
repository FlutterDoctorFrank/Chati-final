package model.notification;

import controller.network.ClientSender;
import model.MessageBundle;
import model.communication.message.TextMessage;
import model.context.spatial.Area;
import model.role.Permission;
import model.user.User;
import model.user.account.UserAccountManager;

import java.time.LocalDateTime;

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
    public AreaManagingRequest(User owner, User requestingUser, Area requestedArea, LocalDateTime from, LocalDateTime to) {
        super(NotificationType.AREA_MANAGING_REQUEST, owner, requestingUser.getWorld(),
                new MessageBundle("key", requestingUser.getUsername(), requestedArea.getContextName(), from, to));
        this.requestingUser = requestingUser;
        this.requestedArea = requestedArea;
        this.from = from;
        this.to = to;
    }

    @Override
    public void accept() {
        // Überprüfe, ob der Besitzer dieser Benachrichtigung noch die nötige Berechtigung besitzt.
        if (!owner.hasPermission(requestedArea, Permission.ASSIGN_AREA_MANAGER)) {
            MessageBundle messageBundle = new MessageBundle("Du besitzt nicht die nötige Berechtigung zum Annehmen dieser Anfrage.");
            TextMessage infoMessage = new TextMessage(messageBundle);
            owner.getClientSender().send(ClientSender.SendAction.MESSAGE, infoMessage);
        }
        // Überprüfe, ob der anfragende Benutzer noch existiert.
        if (!UserAccountManager.getInstance().isRegistered(requestingUser.getUserId())) {
            MessageBundle messageBundle = new MessageBundle("Der anfragende Benutzer existiert nicht mehr");
            TextMessage infoMessage = new TextMessage(messageBundle);
            owner.getClientSender().send(ClientSender.SendAction.MESSAGE, infoMessage);
            return;
        }
        // Überprüfe, ob der anfragende Benutzer diesen Bereich bereits zu einem Zeitpunkt reserviert.
        if (requestedArea.isReservedBy(requestingUser)) {
            MessageBundle messageBundle = new MessageBundle("Der anfragende Benutzer hat diesen Bereich bereits reserviert.");
            TextMessage infoMessage = new TextMessage(messageBundle);
            owner.getClientSender().send(ClientSender.SendAction.MESSAGE, infoMessage);
            return;
        }
        // Überprüfe, ob dieser Bereich zu dem angefragten Zeitraum bereits reserviert wird.
        if (requestedArea.isReservedAt(from, to)) {
            MessageBundle messageBundle = new MessageBundle("Der angefragte Bereich ist bereits reserviert.");
            TextMessage infoMessage = new TextMessage(messageBundle);
            owner.getClientSender().send(ClientSender.SendAction.MESSAGE, infoMessage);
            return;
        }
        // Füge die Reservierung hinzu.
        requestedArea.addReservation(requestingUser, from, to);
        // Benachrichte den Benutzer über die erfolgreiche Reservierung.
        MessageBundle messageBundle = new MessageBundle("key", owner, requestedArea, from, to);
        Notification acceptNotification = new Notification(requestingUser, owner.getWorld(), messageBundle);
        requestingUser.addNotification(acceptNotification);
    }

    @Override
    public void decline() {
        // Überprüfe, ob der Besitzer dieser Benachrichtigung noch die nötige Berechtigung besitzt.
        if (!owner.hasPermission(requestedArea, Permission.ASSIGN_AREA_MANAGER)) {
            MessageBundle messageBundle = new MessageBundle("Du besitzt nicht die nötige Berechtigung zum Ablehnen dieser Anfrage.");
            TextMessage infoMessage = new TextMessage(messageBundle);
            owner.getClientSender().send(ClientSender.SendAction.MESSAGE, infoMessage);
        }
        // Überprüfe, ob der anfragende Benutzer noch existiert.
        if (!UserAccountManager.getInstance().isRegistered(requestingUser.getUserId())) {
            MessageBundle messageBundle = new MessageBundle("Der anfragende Benutzer existiert nicht mehr");
            TextMessage infoMessage = new TextMessage(messageBundle);
            owner.getClientSender().send(ClientSender.SendAction.MESSAGE, infoMessage);
            return;
        }
        // Überprüfe, ob der anfragende Benutzer diesen Bereich bereits zu diesem Zeitpunkt reserviert.
        if (requestedArea.isReservedAtBy(requestingUser, from, to)) {
            MessageBundle messageBundle = new MessageBundle("Der anfragende Benutzer hat diesen Bereich bereits reserviert.");
            TextMessage infoMessage = new TextMessage(messageBundle);
            owner.getClientSender().send(ClientSender.SendAction.MESSAGE, infoMessage);
            return;
        }
        // Benachrichtige den Benutzer über die abgelehnte Anfrage.
        Notification declineNotification = new Notification(requestingUser, owner.getWorld(), new MessageBundle("key"));
        requestingUser.addNotification(declineNotification);
    }
}