package model.notification;

import model.MessageBundle;
import model.RandomValues;
import model.context.Context;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.UUID;

public class INotificationViewTest {

    UUID notificationId;
    MessageBundle messageBundle;
    LocalDateTime timeStamp = LocalDateTime.now();
    NotificationType notificationType;
    Notification notification;
    INotificationView iNotificationView;

    @Before
    public void setUp() {
        notificationId = RandomValues.randomUUID();
        messageBundle = new MessageBundle(RandomValues.random8LengthString());
        timeStamp = LocalDateTime.now();
        notificationType = NotificationType.INFORMATION;
        notification = new Notification(notificationId, new Context(RandomValues.random8LengthString(), Context.getGlobal()),
                messageBundle, timeStamp, notificationType , RandomValues.randomBoolean(), RandomValues.randomBoolean(),
                RandomValues.randomBoolean());
        iNotificationView = notification;
    }

    @After
    public void tearDown() {
        iNotificationView = null;
        notificationId = null;
        messageBundle = null;
        timeStamp = null;
        notificationType = null;
    }

    @Test
    public void getNotificationId() {
        Assert.assertEquals(iNotificationView.getNotificationId(), notificationId);
    }

    @Test
    public void getMessageBundle() {
        Assert.assertEquals(iNotificationView.getMessageBundle(), messageBundle);
    }

    @Test
    public void getTimestamp() {
        Assert.assertEquals(iNotificationView.getTimestamp(), timeStamp);
    }

    @Test
    public void getType() {
        Assert.assertEquals(iNotificationView.getType(), notificationType);
    }
}