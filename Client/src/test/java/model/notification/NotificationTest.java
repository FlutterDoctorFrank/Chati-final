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

public class NotificationTest {

    UUID notificationId;
    MessageBundle messageBundle;
    LocalDateTime timeStamp = LocalDateTime.now();
    NotificationType notificationType;
    Notification notification;
    Context context;


    @Before
    public void setUp() {
        notificationId = RandomValues.randomUUID();
        messageBundle = new MessageBundle(RandomValues.random8LengthString());
        timeStamp = LocalDateTime.now();
        notificationType = NotificationType.INFORMATION;
        context = new Context(RandomValues.random8LengthString(), Context.getGlobal());
        notification = new Notification(notificationId, context,
                messageBundle, timeStamp, notificationType , RandomValues.randomBoolean(), RandomValues.randomBoolean(),
                RandomValues.randomBoolean());
    }

    @After
    public void tearDown() {
        notificationId = null;
        messageBundle = null;
        timeStamp = null;
        notificationType = null;
    }

    @Test
    public void getContext() {
        Assert.assertEquals(notification.getContext(), context);
    }
}