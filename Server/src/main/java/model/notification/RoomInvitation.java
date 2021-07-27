package model.notification;

import controller.network.ClientSender;
import model.MessageBundle;
import model.communication.message.TextMessage;
import model.context.Context;
import model.context.spatial.SpatialContext;
import model.user.User;

public class RoomInvitation extends Notification {
    private final User invitingUser;
    private final SpatialContext invitedRoom;

    public RoomInvitation(User owner, String userMessage, User invitingUser, SpatialContext invitedRoom) {
        super(owner, invitingUser.getWorld(), new MessageBundle("roomInvitationKey", new Object[]{userMessage}));
        this.invitingUser = invitingUser;
        this.invitedRoom = invitedRoom;
    }

    @Override
    public void accept() {
        // Check if invited room exists.
        if (!owner.getWorld().containsPrivateRoom(invitedRoom)) {
            MessageBundle messageBundle = new MessageBundle("Der private Raum existiert nicht mehr.");
            TextMessage infoMessage = new TextMessage(messageBundle);
            owner.getClientSender().send(ClientSender.SendAction.MESSAGE, infoMessage);
            delete();
            return;
        }
        owner.teleport(invitedRoom.getSpawnLocation());
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
