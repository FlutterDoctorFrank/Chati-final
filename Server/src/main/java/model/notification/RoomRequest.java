package model.notification;

import controller.network.ClientSender;
import model.MessageBundle;
import model.communication.message.TextMessage;
import model.context.spatial.SpatialContext;
import model.role.Permission;
import model.user.User;
import model.user.account.UserAccountManager;

public class RoomRequest extends Notification {
    private final User requestingUser;
    private final SpatialContext requestedRoom;

    public RoomRequest(User owner, String userMessage, User requestingUser, SpatialContext requestedRoom) {
        super(owner, requestingUser.getWorld(), new MessageBundle("roomRequestKey", new Object[]{userMessage}));
        this.requestingUser = requestingUser;
        this.requestedRoom = requestedRoom;
    }

    @Override
    public void accept() {
        // Check if requesting user still exists.
        if (!UserAccountManager.getInstance().isRegistered(requestingUser.getUserId())) {
            MessageBundle messageBundle = new MessageBundle("Der anfragende Benutzer existiert nicht mehr.");
            TextMessage infoMessage = new TextMessage(messageBundle);
            owner.getClientSender().send(ClientSender.SendAction.MESSAGE, infoMessage);
            delete();
            return;
        }
        // Check if user of this notification is still roomowner of the requested room.
        if (!owner.getWorld().containsPrivateRoom(requestedRoom) || !owner.hasPermission(requestedRoom, Permission.MANAGE_PRIVATE_ROOM)) {
            MessageBundle messageBundle = new MessageBundle("Du hast nicht die nötige Berechtigung für den angefragten Raum.");
            TextMessage infoMessage = new TextMessage(messageBundle);
            owner.getClientSender().send(ClientSender.SendAction.MESSAGE, infoMessage);
            delete();
            return;
        }
        RoomInvitation roomInvitation = new RoomInvitation(requestingUser, null, owner, requestedRoom);
        requestingUser.addNotification(roomInvitation);
        delete();
    }

    @Override
    public void decline() {
        delete();
    }

    @Override
    public boolean isRequest() {
        return true;
    }
}
