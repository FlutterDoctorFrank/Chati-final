package model.notification;

import model.MessageBundle;
import model.context.Context;
import model.context.spatial.SpatialContext;
import model.exception.IllegalNotificationActionException;
import model.user.User;

import java.time.LocalDateTime;
import java.util.UUID;

public class Notification implements INotification {
    private final UUID notificationID;
    protected final User owner;
    private final Context owningContext;
    private final MessageBundle messageBundle;
    private final LocalDateTime timestamp;
    protected final User requestingUser;
    protected final SpatialContext requestedContext;

    public Notification(User owner, Context owningContext, MessageBundle messageBundle,
                        User requestingUser, SpatialContext requestedContext) {
        this.notificationID = UUID.randomUUID();
        this.owner = owner;
        this.owningContext = owningContext;
        this.messageBundle = messageBundle;
        this.timestamp = LocalDateTime.now();
        this.requestingUser = requestingUser;
        this.requestedContext = requestedContext;
    }

    @Override
    public UUID getNotificationID() {
        return notificationID;
    }

    @Override
    public Context getContext() {
        return owningContext;
    }

    @Override
    public MessageBundle getMessageBundle() {
        return messageBundle;
    }

    @Override
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    @Override
    public User getRequestingUser() {
        return requestingUser;
    }

    @Override
    public SpatialContext getRequestedContext() {
        return requestedContext;
    }

    public void accept() throws IllegalNotificationActionException {
        throw new IllegalNotificationActionException("This notification is not a request.", owner, this, true);
    }

    public void decline() throws IllegalNotificationActionException {
        throw new IllegalNotificationActionException("This notification is not a request.", owner, this, false);
    }

    public boolean isRequest() {
        return false;
    }

    protected void delete()  {
        owner.removeNotification(this);
    }
}
