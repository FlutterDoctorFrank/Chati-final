package model.notification;

import controller.network.ClientSender.SendAction;
import model.MessageBundle;
import model.communication.message.TextMessage;
import model.context.spatial.Room;
import model.exception.IllegalNotificationActionException;
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
                new MessageBundle("request.room-request.notification", requestingUser.getUsername(),
                        requestedRoom.getContextName(), userMessage));
        this.requestingUser = requestingUser;
        this.requestedRoom = requestedRoom;
    }

    @Override
    public void accept() throws IllegalNotificationActionException {
        super.accept();

        if (owner.getWorld() == null) {
            throw new IllegalStateException("Owners world is not available");
        }

        // Überprüfe, ob der anfragende Benutzer noch existiert.
        if (!UserAccountManager.getInstance().isRegistered(requestingUser.getUserId())) {
            TextMessage infoMessage = new TextMessage("request.room-request.user-not-found", requestingUser.getUsername());
            owner.send(SendAction.MESSAGE, infoMessage);
            return;
        }
        // Überprüfe, ob der angefragte private Raum noch existiert und der Eigentümer dieser Benachrichtigung noch
        // Raumbesitzer ist.
        if (!owner.getWorld().containsPrivateRoom(requestedRoom) || !owner.hasPermission(requestedRoom,
                Permission.MANAGE_PRIVATE_ROOM)) {
            TextMessage infoMessage = new TextMessage("request.room-request.not-permitted", requestedRoom.getContextName(),
                    Permission.MANAGE_PRIVATE_ROOM);
            owner.send(SendAction.MESSAGE, infoMessage);
            return;
        }
        // Lade den anfragenden Benutzer in den Raum ein.
        RoomInvitation roomInvitation = new RoomInvitation(requestingUser, owner, requestedRoom);
        requestingUser.addNotification(roomInvitation);
    }

    @Override
    public void decline() throws IllegalNotificationActionException {
        super.decline();

        if (owner.getWorld() == null) {
            throw new IllegalStateException("Owners world is not available");
        }

        // Überprüfe, ob der anfragende Benutzer noch existiert.
        if (!UserAccountManager.getInstance().isRegistered(requestingUser.getUserId())) {
            TextMessage infoMessage = new TextMessage("request.room-request.user-not-found", requestingUser.getUsername());
            owner.send(SendAction.MESSAGE, infoMessage);
            return;
        }
        // Überprüfe, ob der angefragte private Raum noch existiert und der Eigentümer dieser Benachrichtigung noch
        // Raumbesitzer ist.
        if (!owner.getWorld().containsPrivateRoom(requestedRoom) || !owner.hasPermission(requestedRoom,
                Permission.MANAGE_PRIVATE_ROOM)) {
            TextMessage infoMessage = new TextMessage("request.room-request.not-permitted", requestedRoom.getContextName(),
                    Permission.MANAGE_PRIVATE_ROOM);
            owner.send(SendAction.MESSAGE, infoMessage);
            return;
        }
        // Benachrichtige den anfragenden Benutzer über die abgelehnte Raumanfrage
        Notification declineNotification = new Notification(requestingUser, owner.getWorld(),
                new MessageBundle("request.room-request.declined", owner.getUsername()));
        requestingUser.addNotification(declineNotification);
    }
}