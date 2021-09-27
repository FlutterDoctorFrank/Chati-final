package controller.network;

import controller.network.ServerSender.SendAction;
import controller.network.protocol.PacketNotificationResponse;
import model.notification.NotificationAction;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import java.util.UUID;
import java.util.logging.Level;

public class PacketNotificationResponseTest extends PacketClientTest {

    public PacketNotificationResponseTest() {

    }

    @Test(expected = IllegalArgumentException.class)
    public void illegalDeleteArgsPackagingTest() {
        this.getPacket(SendAction.NOTIFICATION_DELETE, PacketNotificationResponse.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void illegalDeleteTypesPackagingTest() {
        this.getPacket(SendAction.NOTIFICATION_DELETE, PacketNotificationResponse.class, new Object());
    }

    @Test
    public void deletePackagingTest() {
        final UUID notificationId = randomUniqueId();

        final PacketNotificationResponse packet = this.getPacket(SendAction.NOTIFICATION_DELETE, PacketNotificationResponse.class, notificationId);

        Assert.assertEquals(NotificationAction.DELETE, packet.getAction());
        Assert.assertEquals(notificationId, packet.getNotificationId());
    }

    @Test(expected = IllegalArgumentException.class)
    public void illegalReadArgsPackagingTest() {
        this.getPacket(SendAction.NOTIFICATION_READ, PacketNotificationResponse.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void illegalReadTypesPackagingTest() {
        this.getPacket(SendAction.NOTIFICATION_READ, PacketNotificationResponse.class, new Object());
    }

    @Test
    public void readPackagingTest() {
        final UUID notificationId = randomUniqueId();

        final PacketNotificationResponse packet = this.getPacket(SendAction.NOTIFICATION_READ, PacketNotificationResponse.class, notificationId);

        Assert.assertEquals(NotificationAction.READ, packet.getAction());
        Assert.assertEquals(notificationId, packet.getNotificationId());
    }

    @Test(expected = IllegalArgumentException.class)
    public void illegalResponseArgsPackagingTest() {
        this.getPacket(SendAction.NOTIFICATION_RESPONSE, PacketNotificationResponse.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void illegalResponseTypesPackagingTest() {
        this.getPacket(SendAction.NOTIFICATION_RESPONSE, PacketNotificationResponse.class, new Object(), new Object());
    }

    @Test
    public void acceptPackagingTest() {
        final UUID notificationId = randomUniqueId();

        final PacketNotificationResponse packet = this.getPacket(SendAction.NOTIFICATION_RESPONSE, PacketNotificationResponse.class, notificationId, true);

        Assert.assertEquals(NotificationAction.ACCEPT, packet.getAction());
        Assert.assertEquals(notificationId, packet.getNotificationId());
    }

    @Test
    public void declinePackagingTest() {
        final UUID notificationId = randomUniqueId();

        final PacketNotificationResponse packet = this.getPacket(SendAction.NOTIFICATION_RESPONSE, PacketNotificationResponse.class, notificationId, false);

        Assert.assertEquals(NotificationAction.DECLINE, packet.getAction());
        Assert.assertEquals(notificationId, packet.getNotificationId());
    }

    @Test
    public void handleUnexpectedPacketTest() {
        final PacketNotificationResponse packet = Mockito.mock(PacketNotificationResponse.class);

        this.handler.reset();
        this.connection.handle(packet);

        Assert.assertTrue(this.handler.logged());
        Assert.assertTrue(this.handler.logged(Level.WARNING, "Can not receive notification response while user is not logged in"));
    }

    @Test
    public void handleInvalidPacketTest() {
        final PacketNotificationResponse packet = Mockito.mock(PacketNotificationResponse.class);

        Mockito.when(packet.getAction()).thenReturn(randomEnum(NotificationAction.class, NotificationAction.DELETE));
        Mockito.when(packet.getNotificationId()).thenReturn(randomUniqueId());

        this.login();
        this.intern.updateNotification(true);
        this.handler.reset();
        this.connection.handle(packet);

        Assert.assertTrue(this.handler.logged());
        Assert.assertTrue(this.handler.logged(Level.WARNING, "Server tried to send notification delete for unknown notification"));
        Assert.assertTrue(this.intern.called("update-notification"));
    }

    @Test
    public void handleCorrectPacketTest() {
        final PacketNotificationResponse packet = Mockito.mock(PacketNotificationResponse.class);

        Mockito.when(packet.getAction()).thenReturn(NotificationAction.READ);
        Mockito.when(packet.getNotificationId()).thenReturn(randomUniqueId());

        this.login();
        this.handler.reset();
        this.connection.handle(packet);

        Assert.assertFalse(this.handler.logged());
        Assert.assertTrue(this.intern.called("update-notification"));
        Assert.assertFalse(this.intern.called("remove-notification"));
        Mockito.when(packet.getAction()).thenReturn(NotificationAction.ACCEPT);

        this.intern.reset();
        this.handler.reset();
        this.connection.handle(packet);

        Assert.assertFalse(this.handler.logged());
        Assert.assertTrue(this.intern.called("update-notification"));
        Assert.assertFalse(this.intern.called("remove-notification"));
        Mockito.when(packet.getAction()).thenReturn(NotificationAction.DECLINE);

        this.intern.reset();
        this.handler.reset();
        this.connection.handle(packet);

        Assert.assertFalse(this.handler.logged());
        Assert.assertTrue(this.intern.called("update-notification"));
        Assert.assertFalse(this.intern.called("remove-notification"));
        Mockito.when(packet.getAction()).thenReturn(NotificationAction.DELETE);

        this.intern.reset();
        this.handler.reset();
        this.connection.handle(packet);

        Assert.assertFalse(this.handler.logged());
        Assert.assertFalse(this.intern.called("update-notification"));
        Assert.assertTrue(this.intern.called("remove-notification"));
    }
}
