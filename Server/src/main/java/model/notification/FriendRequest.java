package model.notification;

import controller.network.ClientSender;
import model.MessageBundle;
import model.communication.message.TextMessage;
import model.context.global.GlobalContext;
import model.user.User;
import model.user.account.UserAccountManager;

public class FriendRequest extends Notification {
    private final User requestingUser;

    public FriendRequest(User owner, String userMessage, User requestingUser) {
        super(owner, GlobalContext.getInstance(), new MessageBundle("friendRequestKey", new Object[]{userMessage}));
        this.requestingUser = requestingUser;
    }

    @Override
    public void accept() {
        // Check if requesting user still exists.
        if (!UserAccountManager.getInstance().isRegistered(requestingUser.getUserID())) {
            MessageBundle messageBundle = new MessageBundle("Der anfragende Benutzer existiert nicht mehr");
            TextMessage infoMessage = new TextMessage(messageBundle);
            owner.getClientSender().send(ClientSender.SendAction.MESSAGE, infoMessage);
            delete();
            return;
        }
        // Check if users are already friends.
        if (owner.isFriend(requestingUser) || requestingUser.isFriend(owner)) {
            MessageBundle messageBundle = new MessageBundle("Du bist bereits mit diesem Benutzer befreundet.");
            TextMessage infoMessage = new TextMessage(messageBundle);
            owner.getClientSender().send(ClientSender.SendAction.MESSAGE, infoMessage);
            delete();
            return;
        }
        // Check if requesting user ignores owner of this notification.
        if (requestingUser.isIgnoring(owner)) {
            MessageBundle messageBundle = new MessageBundle("Eine Freundschaft mit diesem Benutzer ist nicht möglich.");
            TextMessage infoMessage = new TextMessage(messageBundle);
            owner.getClientSender().send(ClientSender.SendAction.MESSAGE, infoMessage);
            delete();
            return;
        }
        // check if owner of this notification ignores requesting user.
        if (owner.isIgnoring(requestingUser)) {
            owner.unignoreUser(requestingUser);
        }
        owner.addFriend(requestingUser);
        requestingUser.addFriend(owner);
        Notification friendNotification = new Notification(requestingUser, GlobalContext.getInstance(), new MessageBundle("messageKey"));
        requestingUser.addNotification(friendNotification);
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