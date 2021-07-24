package model.notification;

import model.MessageBundle;
import model.communication.message.MessageType;
import model.communication.message.TextMessage;
import model.context.Context;
import model.context.spatial.SpatialContext;
import model.role.Permission;
import model.user.User;
import model.user.account.UserAccountManager;

public class RoomRequest extends Notification {

    public RoomRequest(User owner, Context owningContext, String userMessage, User requestingUser, SpatialContext requestedContext) {
        super(owner, owningContext, new MessageBundle("roomRequestKey", new Object[]{userMessage}), requestingUser, requestedContext);
    }

    @Override
    public void accept() {
        // Check if requesting user still exists.
        if (!UserAccountManager.getInstance().isRegistered(requestingUser.getUserID())) {
            MessageBundle messageBundle = new MessageBundle("Der anfragende Benutzer existiert nicht mehr.");
            TextMessage infoMessage = new TextMessage(messageBundle);
            delete();
            return;
        }
        // Check if owner of this notification is still roomowner of the requested room.
        if (!owner.getWorld().containsPrivateRoom(requestedContext) || !owner.hasPermission(requestedContext, Permission.MANAGE_PRIVATE_ROOM)) {
            MessageBundle messageBundle = new MessageBundle("Du hast nicht die nötige Berechtigung für den angefragten Raum.");
            TextMessage infoMessage = new TextMessage(messageBundle);
            delete();
            return;
        }
        RoomInvitation roomInvitation = new RoomInvitation(requestingUser, owner.getWorld(), null, owner, requestedContext);
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
