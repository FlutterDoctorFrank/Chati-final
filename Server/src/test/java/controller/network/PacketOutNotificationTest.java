package controller.network;

import controller.network.ClientSender.SendAction;
import controller.network.protocol.PacketOutNotification;
import model.context.IContext;
import model.notification.INotification;
import model.notification.NotificationType;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import java.time.LocalDateTime;

public class PacketOutNotificationTest extends PacketServerTest {

    public PacketOutNotificationTest() {
        super(SendAction.NOTIFICATION);
    }

    @Test(expected = IllegalArgumentException.class)
    public void illegalPackagingTest() {
        this.getPacket(PacketOutNotification.class, new Object());
    }

    @Test
    public void correctPackagingTest() {
        final INotification notification = Mockito.mock(INotification.class);
        final IContext context = Mockito.mock(IContext.class);

        Mockito.when(context.getContextId()).thenReturn(randomContextId());
        Mockito.when(notification.getNotificationId()).thenReturn(randomUniqueId());
        Mockito.when(notification.getContext()).thenReturn(context);
        Mockito.when(notification.getMessageBundle()).thenReturn(randomBundle());
        Mockito.when(notification.getTimestamp()).thenReturn(LocalDateTime.now());
        Mockito.when(notification.getNotificationType()).thenReturn(randomEnum(NotificationType.class));
        Mockito.when(notification.isAccepted()).thenReturn(randomBoolean());
        Mockito.when(notification.isDeclined()).thenReturn(randomBoolean());
        Mockito.when(notification.isRead()).thenReturn(randomBoolean());

        final PacketOutNotification packet = this.getPacket(PacketOutNotification.class, notification);

        Assert.assertEquals(notification.getNotificationId(), packet.getNotification().getNotificationId());
        Assert.assertEquals(context.getContextId(), packet.getNotification().getContextId());
        Assert.assertEquals(notification.getMessageBundle(), packet.getNotification().getMessage());
        Assert.assertEquals(notification.getTimestamp(), packet.getNotification().getTimestamp());
        Assert.assertEquals(notification.getNotificationType(), packet.getNotification().getType());
        Assert.assertEquals(notification.isAccepted(), packet.getNotification().isAccepted());
        Assert.assertEquals(notification.isDeclined(), packet.getNotification().isDeclined());
        Assert.assertEquals(notification.isRead(), packet.getNotification().isRead());
    }
}
