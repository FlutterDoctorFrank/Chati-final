package model.notification;

import controller.network.ClientSender;
import model.MessageBundle;
import model.communication.message.TextMessage;
import model.context.spatial.SpatialContext;
import model.role.Permission;
import model.user.User;
import model.user.account.UserAccountManager;

/**
 * Eine Klasse, welche Anfragen zum Beitritt in einen privaten Raum repräsentiert.
 */
public class RoomRequest extends Notification {

    /** Benutzer, der die Raumanfrage stellt. */
    private final User requestingUser;

    /** Der angefragte Raum. */
    private final SpatialContext requestedRoom;

    /**
     * Erzeugt eine neue Instanz der Raumanfrage.
     * @param owner Eigentümer dieser Benachrichtigung.
     * @param userMessage Die vom anfragenden Benutzer beigefügte Nachricht.
     * @param requestingUser Benutzer, der die Raumanfrage stellt.
     * @param requestedRoom Der angefragte Raum.
     */
    public RoomRequest(User owner, String userMessage, User requestingUser, SpatialContext requestedRoom) {
        super(NotificationType.ROOM_REQUEST, owner, requestingUser.getWorld(),
                new MessageBundle("roomRequestKey", requestingUser.getUsername(), requestedRoom.getContextName(), userMessage));
        this.requestingUser = requestingUser;
        this.requestedRoom = requestedRoom;
    }

    @Override
    public void accept() {
        // Überprüfe, ob der anfragende Benutzer noch existiert.
        if (!UserAccountManager.getInstance().isRegistered(requestingUser.getUserId())) {
            MessageBundle messageBundle = new MessageBundle("Der anfragende Benutzer existiert nicht mehr.");
            TextMessage infoMessage = new TextMessage(messageBundle);
            owner.getClientSender().send(ClientSender.SendAction.MESSAGE, infoMessage);
            return;
        }
        // Überprüfe, ob der angefragte private Raum noch existiert und der Eigentümer dieser Benachrichtigung noch
        // Raumbesitzer ist.
        if (!owner.getWorld().containsPrivateRoom(requestedRoom) || !owner.hasPermission(requestedRoom, Permission.MANAGE_PRIVATE_ROOM)) {
            MessageBundle messageBundle = new MessageBundle("Du hast nicht die nötige Berechtigung für den angefragten Raum.");
            TextMessage infoMessage = new TextMessage(messageBundle);
            owner.getClientSender().send(ClientSender.SendAction.MESSAGE, infoMessage);
            return;
        }
        // Lade den anfragenden Benutzer in den Raum ein.
        RoomInvitation roomInvitation = new RoomInvitation(requestingUser, owner, requestedRoom);
        requestingUser.addNotification(roomInvitation);
    }

    @Override
    public void decline() {
        // Überprüfe, ob der anfragende Benutzer noch existiert.
        if (!UserAccountManager.getInstance().isRegistered(requestingUser.getUserId())) {
            MessageBundle messageBundle = new MessageBundle("Der anfragende Benutzer existiert nicht mehr.");
            TextMessage infoMessage = new TextMessage(messageBundle);
            owner.getClientSender().send(ClientSender.SendAction.MESSAGE, infoMessage);
            return;
        }
        // Überprüfe, ob der angefragte private Raum noch existiert und der Eigentümer dieser Benachrichtigung noch
        // Raumbesitzer ist.
        if (!owner.getWorld().containsPrivateRoom(requestedRoom) || !owner.hasPermission(requestedRoom, Permission.MANAGE_PRIVATE_ROOM)) {
            MessageBundle messageBundle = new MessageBundle("Du hast nicht die nötige Berechtigung für den angefragten Raum.");
            TextMessage infoMessage = new TextMessage(messageBundle);
            owner.getClientSender().send(ClientSender.SendAction.MESSAGE, infoMessage);
            return;
        }
        // Benachrichtige den anfragenden Benutzer über die abgelehnte Raumanfrage
        Notification declineNotification = new Notification(requestingUser, owner.getWorld(), new MessageBundle("messageKey"));
        requestingUser.addNotification(declineNotification);
    }

    @Override
    public boolean isRequest() {
        return true;
    }
}
