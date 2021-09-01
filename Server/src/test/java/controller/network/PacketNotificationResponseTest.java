package controller.network;

import controller.network.ClientSender.SendAction;
import controller.network.protocol.PacketNotificationResponse;
import model.notification.INotification;
import model.notification.NotificationAction;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

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
}
