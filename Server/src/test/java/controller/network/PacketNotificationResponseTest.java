package controller.network;

import controller.network.ClientSender.SendAction;
import controller.network.mock.MockIUser;
import controller.network.protocol.PacketNotificationResponse;
import model.notification.INotification;
import model.notification.NotificationAction;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import java.util.logging.Level;

public class PacketNotificationResponseTest extends PacketServerTest {

    public PacketNotificationResponseTest() {
        super(SendAction.NOTIFICATION_DELETE);
    }

    @Test(expected = IllegalArgumentException.class)
    public void illegalPackagingTest() {
        this.getPacket(PacketNotificationResponse.class, new Object());
    }

    @Test
    public void correctPackagingTest() {
        final INotification notification = Mockito.mock(INotification.class);

        Mockito.when(notification.getNotificationId()).thenReturn(randomUniqueId());

        final PacketNotificationResponse packet = this.getPacket(PacketNotificationResponse.class, notification);

        Assert.assertEquals(NotificationAction.DELETE, packet.getAction());
        Assert.assertEquals(notification.getNotificationId(), packet.getNotificationId());
    }

    @Test
    public void handleUnexpectedPacketTest() {
        final PacketNotificationResponse packet = new PacketNotificationResponse(randomUniqueId(), randomEnum(NotificationAction.class));

        this.handler.reset();
        this.connection.handle(packet);

        Assert.assertTrue(this.handler.logged());
        Assert.assertTrue(this.handler.logged(Level.WARNING, "Can not interact with notification while not logged in"));
    }

    @Test
    public void handleIllegalResponseTest() {
        final PacketNotificationResponse packet = new PacketNotificationResponse(randomUniqueId(), randomEnum(NotificationAction.class));
        final MockIUser user = this.login();

        user.manageNotification(true, false);

        this.handler.reset();
        this.connection.handle(packet);

        Assert.assertTrue(this.handler.logged());
        Assert.assertTrue(this.handler.logged(Level.WARNING, "unknown notification"));
        Assert.assertTrue(user.called("manage-notification"));

        user.reset();
        user.manageNotification(false, true);

        this.handler.reset();
        this.connection.handle(packet);

        Assert.assertTrue(this.handler.logged());
        Assert.assertTrue(this.handler.logged(Level.WARNING, "illegal notification"));
        Assert.assertTrue(user.called("manage-notification"));
    }

    @Test
    public void handleCorrectResponseTest() {
        final PacketNotificationResponse packet = Mockito.mock(PacketNotificationResponse.class);
        final MockIUser user = this.login();

        user.manageNotification(false, false);
        Mockito.when(packet.getNotificationId()).thenReturn(randomUniqueId());
        Mockito.when(packet.getAction()).thenReturn(randomEnum(NotificationAction.class, NotificationAction.DELETE));

        this.handler.reset();
        this.connection.handle(packet);

        Assert.assertTrue(user.called("manage-notification"));
        Mockito.when(packet.getAction()).thenReturn(NotificationAction.DELETE);
        user.reset();

        this.handler.reset();
        this.connection.handle(packet);

        Assert.assertFalse(this.handler.logged());
        Assert.assertTrue(user.called("manage-notification"));
    }
}
