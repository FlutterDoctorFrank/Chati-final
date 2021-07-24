package model.notification;

import model.MessageBundle;
import model.communication.message.MessageType;
import model.communication.message.TextMessage;
import model.context.Context;
import model.context.spatial.SpatialContext;
import model.user.User;

public class RoomInvitation extends Notification {

    public RoomInvitation(User owner, Context owningContext, String userMessage, User requestingUser, SpatialContext requestedContext) {
        super(owner, owningContext, new MessageBundle("roomInvitationKey", new Object[]{userMessage}), requestingUser, requestedContext);
    }

    @Override
    public void accept() {
        // Check if invited room still exists.
        if (!requestingUser.getWorld().containsPrivateRoom(getRequestedContext())) {
            MessageBundle messageBundle = new MessageBundle("Der private Raum existiert nicht mehr.");
            TextMessage infoMessage = new TextMessage(messageBundle);
            delete();
            return;
        }
        owner.teleport(requestedContext.getSpawnLocation());
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
