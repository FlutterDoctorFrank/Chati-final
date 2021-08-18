package model.notification;

import controller.network.ClientSender.SendAction;
import model.MessageBundle;
import model.communication.message.TextMessage;
import model.context.spatial.Room;
import model.role.Permission;
import model.user.User;
import model.user.account.UserAccountManager;
import org.jetbrains.annotations.NotNull;
import java.util.Objects;

/**
 * Eine Klasse, welche Anfragen zum Beitritt in einen privaten Raum repräsentiert.
 */
public class RoomRequest extends Notification {

    /** Benutzer, der die Raumanfrage stellt. */
    private final User requestingUser;

    /** Der angefragte Raum. */
    private final Room requestedRoom;

    /**
     * Erzeugt eine neue Instanz der Raumanfrage.
     * @param owner Eigentümer dieser Benachrichtigung.
     * @param userMessage Die vom anfragenden Benutzer beigefügte Nachricht.
     * @param requestingUser Benutzer, der die Raumanfrage stellt.
     * @param requestedRoom Der angefragte Raum.
     */
    public RoomRequest(@NotNull final User owner, @NotNull final String userMessage,
                       @NotNull final User requestingUser, @NotNull final Room requestedRoom) {
        super(NotificationType.ROOM_REQUEST, owner, Objects.requireNonNull(requestingUser.getWorld()),
                new MessageBundle("roomRequestKey", requestingUser.getUsername(), requestedRoom.getContextName(), userMessage));
        this.requestingUser = requestingUser;
        this.requestedRoom = requestedRoom;
    }

    @Override
    public void accept() {
        if (owner.getWorld() == null) {
            throw new IllegalStateException("Owners world is not available");
        }

        // Überprüfe, ob der anfragende Benutzer noch existiert.
        if (!UserAccountManager.getInstance().isRegistered(requestingUser.getUserId())) {
            MessageBundle messageBundle = new MessageBundle("Der anfragende Benutzer existiert nicht mehr.");
            TextMessage infoMessage = new TextMessage(messageBundle);
            owner.send(SendAction.MESSAGE, infoMessage);
            return;
        }
        // Überprüfe, ob der angefragte private Raum noch existiert und der Eigentümer dieser Benachrichtigung noch
        // Raumbesitzer ist.
        if (!owner.getWorld().containsPrivateRoom(requestedRoom) || !owner.hasPermission(requestedRoom,
                Permission.MANAGE_PRIVATE_ROOM)) {
            MessageBundle messageBundle = new MessageBundle("Du hast nicht die nötige Berechtigung für den angefragten Raum.");
            TextMessage infoMessage = new TextMessage(messageBundle);
            owner.send(SendAction.MESSAGE, infoMessage);
            return;
        }
        // Lade den anfragenden Benutzer in den Raum ein.
        RoomInvitation roomInvitation = new RoomInvitation(requestingUser, owner, requestedRoom);
        requestingUser.addNotification(roomInvitation);
    }

    @Override
    public void decline() {
        if (owner.getWorld() == null) {
            throw new IllegalStateException("Owners world is not available");
        }

        // Überprüfe, ob der anfragende Benutzer noch existiert.
        if (!UserAccountManager.getInstance().isRegistered(requestingUser.getUserId())) {
            MessageBundle messageBundle = new MessageBundle("Der anfragende Benutzer existiert nicht mehr.");
            TextMessage infoMessage = new TextMessage(messageBundle);
            owner.send(SendAction.MESSAGE, infoMessage);
            return;
        }
        // Überprüfe, ob der angefragte private Raum noch existiert und der Eigentümer dieser Benachrichtigung noch
        // Raumbesitzer ist.
        if (!owner.getWorld().containsPrivateRoom(requestedRoom) || !owner.hasPermission(requestedRoom,
                Permission.MANAGE_PRIVATE_ROOM)) {
            MessageBundle messageBundle = new MessageBundle("Du hast nicht die nötige Berechtigung für den angefragten Raum.");
            TextMessage infoMessage = new TextMessage(messageBundle);
            owner.send(SendAction.MESSAGE, infoMessage);
            return;
        }
        // Benachrichtige den anfragenden Benutzer über die abgelehnte Raumanfrage
        Notification declineNotification = new Notification(requestingUser, owner.getWorld(),
                new MessageBundle("messageKey"));
        requestingUser.addNotification(declineNotification);
    }
}