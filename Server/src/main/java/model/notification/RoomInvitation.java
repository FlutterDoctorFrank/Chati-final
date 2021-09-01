package model.notification;

import controller.network.ClientSender.SendAction;
import model.MessageBundle;
import model.communication.message.TextMessage;
import model.context.spatial.Room;
import model.exception.IllegalNotificationActionException;
import model.role.Permission;
import model.user.User;
import org.jetbrains.annotations.NotNull;
import java.util.Objects;

/**
 * Eine Klasse, welche Einladungen in einen privaten Raum repräsentiert.
 */
public class RoomInvitation extends Notification {

    /** Der einladende Benutzer. */
    private final User invitingUser;

    /** Der Raum, für den diese Einladung ausgestellt wurde. */
    private final Room invitedRoom;

    /**
     * Erzeugt eine neue Instanz einer Raumeinladung mit einer vom einladenden Benutzer beigefügten Nachricht.
     * @param owner Der Eigentümer dieser Benachrichtigung.
     * @param userMessage Die vom einladenden Benutzer beigefügte Nachricht.
     * @param invitingUser Der einladende Benutzer.
     * @param invitedRoom Der Raum, für den diese Einladung ausgestellt wurde.
     */
    public RoomInvitation(@NotNull final User owner, @NotNull final String userMessage,
                          @NotNull final User invitingUser, @NotNull final Room invitedRoom) {
        super(NotificationType.ROOM_INVITATION, owner, Objects.requireNonNull(invitingUser.getWorld()),
                new MessageBundle("request.room-invite.notification", invitingUser.getUsername(),
                        invitedRoom.getContextName(), userMessage));
        this.invitingUser = invitingUser;
        this.invitedRoom = invitedRoom;
    }

    /**
     * Erzeugt eine neue Instanz einer Raumeinladung ohne eine vom einladenden Benutzer beigefügten Nachricht.
     * @param owner Der Eigentümer dieser Benachrichtigung.
     * @param invitingUser Der einladende Benutzer.
     * @param invitedRoom Der Raum, für den diese Einladung ausgestellt wurde.
     */
    public RoomInvitation(@NotNull final User owner, @NotNull final User invitingUser,
                          @NotNull final Room invitedRoom) {
        super(NotificationType.ROOM_INVITATION, owner, Objects.requireNonNull(invitingUser.getWorld()),
                new MessageBundle("request.room-invite.notification", invitingUser.getUsername(),
                        invitedRoom.getContextName()));
        this.invitingUser = invitingUser;
        this.invitedRoom = invitedRoom;
    }

    @Override
    public void accept() throws IllegalNotificationActionException {
        super.accept();

        if (owner.getWorld() == null) {
            throw new IllegalStateException("Owners world is not available");
        }

        // Überprüfe, ob der Raum, in den eingeladen werden soll, noch existiert.
        if (!owner.getWorld().containsPrivateRoom(invitedRoom)) {
            TextMessage infoMessage = new TextMessage("request.room-invite.not-possible", invitedRoom.getContextName());
            owner.send(SendAction.MESSAGE, infoMessage);
            return;
        }
        // Überprüfe, ob der einladende Benutzer noch Rauminhaber ist.
        if (!invitingUser.hasPermission(invitedRoom, Permission.MANAGE_PRIVATE_ROOM)) {
            TextMessage infoMessage = new TextMessage("request.room-invite.not-valid", invitingUser.getUsername(), invitedRoom.getContextName());
            owner.send(SendAction.MESSAGE, infoMessage);
            return;
        }
        // Teleportiere den Benutzer auf die Startposition des eingeladenen Raums.
        owner.teleport(invitedRoom.getSpawnLocation());
    }

    @Override
    public void decline() throws IllegalNotificationActionException {
        super.decline();

        // Keine Auswirkungen.
        if (owner.getWorld() == null) {
            throw new IllegalStateException("Owners world is not available");
        }

        // Benachrichtige den anfragenden Benutzer über die abgelehnte Raumeinladung
        Notification declineNotification = new Notification(invitingUser, owner.getWorld(),
                new MessageBundle("request.room-invite.declined", owner.getUsername()));
        invitingUser.addNotification(declineNotification);
    }
}