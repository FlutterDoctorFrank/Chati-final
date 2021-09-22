package model.notification;

import model.MessageBundle;
import model.context.Context;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.Assert.*;

public class NotificationTest {

    UUID notificationId;
    MessageBundle messageBundle;
    LocalDateTime timeStamp = LocalDateTime.now();
    NotificationType notificationType;
    Notification notification;
    Context context;


    @Before
    public void setUp() throws Exception {
        notificationId = UUID.randomUUID();
        messageBundle = new MessageBundle("TestMessage");
        timeStamp = LocalDateTime.now();
        notificationType = NotificationType.INFORMATION;
        context = new Context("Test", Context.getGlobal());
        notification = new Notification(notificationId, context,
                messageBundle, timeStamp, notificationType , true, true, true);
    }

    @After
    public void tearDown() throws Exception {
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