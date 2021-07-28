package model.notification;

import controller.network.ClientSender;
import model.MessageBundle;
import model.communication.message.TextMessage;
import model.context.spatial.SpatialContext;
import model.user.User;
import model.user.account.UserAccountManager;

import java.time.LocalDateTime;

public class AreaManagingRequest extends Notification {
    private final User requestingUser;
    private final SpatialContext requestedArea;
    private final LocalDateTime from;
    private final LocalDateTime to;

    public AreaManagingRequest(User owner, User requestingUser, SpatialContext requestedArea, LocalDateTime from, LocalDateTime to) {
        super(owner, requestingUser.getWorld(), new MessageBundle("key", requestingUser.getUsername(), requestedArea.getContextName(), from, to));
        this.requestingUser = requestingUser;
        this.requestedArea = requestedArea;
        this.from = from;
        this.to = to;
    }

    @Override
    public void accept() {
        // Check if requesting user still exists.
        if (!UserAccountManager.getInstance().isRegistered(requestingUser.getUserId())) {
            MessageBundle messageBundle = new MessageBundle("Der anfragende Benutzer existiert nicht mehr");
            TextMessage infoMessage = new TextMessage(messageBundle);
            owner.getClientSender().send(ClientSender.SendAction.MESSAGE, infoMessage);
            delete();
            return;
        }
        // Check if requested area is already reserved by that user
        if (requestedArea.isReservedBy(requestingUser)) {
            MessageBundle messageBundle = new MessageBundle("Der anfragende Benutzer hat diesen Bereich bereits reserviert.");
            TextMessage infoMessage = new TextMessage(messageBundle);
            owner.getClientSender().send(ClientSender.SendAction.MESSAGE, infoMessage);
            delete();
            return;
        }
        // Check if requested area is already reserved at that time
        if (requestedArea.isReservedAt(from, to)) {
            MessageBundle messageBundle = new MessageBundle("Der angefragte Bereich ist bereits reserviert.");
            TextMessage infoMessage = new TextMessage(messageBundle);
            owner.getClientSender().send(ClientSender.SendAction.MESSAGE, infoMessage);
            delete();
            return;
        }
        requestedArea.addReservation(requestingUser, from, to);
        MessageBundle messageBundle = new MessageBundle("key", owner, requestedArea, from, to);
        Notification notification = new Notification(requestingUser, owner.getWorld(), messageBundle);
        requestingUser.addNotification(notification);
        delete();
    }

    @Override
    public void decline() {
        Notification declineNotification = new Notification(requestingUser, owner.getWorld(), new MessageBundle("key"));
        requestingUser.addNotification(declineNotification);
        delete();
    }
}
