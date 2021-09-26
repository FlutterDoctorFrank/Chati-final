package controller.network;

import controller.network.ServerSender.SendAction;
import controller.network.protocol.PacketNotificationResponse;
import model.notification.NotificationAction;
import org.junit.Assert;
import org.junit.Test;
import java.util.UUID;

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
}
