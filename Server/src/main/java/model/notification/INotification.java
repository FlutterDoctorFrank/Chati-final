package model.notification;

import model.MessageBundle;
import model.context.IContext;
import model.context.spatial.ISpatialContext;
import model.user.User;

import java.time.LocalDateTime;
import java.util.UUID;

public interface INotification {
    public UUID getNotificationID();
    public IContext getContext();
    public MessageBundle getMessageBundle();
    public LocalDateTime getTimestamp();
}
