package model.notification;

import model.MessageBundle;
import model.context.Context;
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

    public Notification(User owner, Context owningContext, MessageBundle messageBundle) {
        this.notificationID = UUID.randomUUID();
        this.owner = owner;
        this.owningContext = owningContext;
        this.messageBundle = messageBundle;
        this.timestamp = LocalDateTime.now();
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

    public void accept() throws IllegalNotificationActionException {
        delete();
    }

    public void decline() throws IllegalNotificationActionException {
        delete();
    }

    public boolean isRequest() {
        return false;
    }

    protected void delete()  {
        owner.removeNotification(this);
    }
}
