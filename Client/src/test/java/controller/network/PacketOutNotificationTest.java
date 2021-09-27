package controller.network;

import controller.network.protocol.PacketOutNotification;
import controller.network.protocol.PacketOutNotification.Notification;
import model.notification.NotificationType;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import java.time.LocalDateTime;
import java.util.logging.Level;

public class PacketOutNotificationTest extends PacketClientTest {

    public PacketOutNotificationTest() {

    }

    @Test
    public void handleUnexpectedPacketTest() {
        final PacketOutNotification packet = Mockito.mock(PacketOutNotification.class);

        this.handler.reset();
        this.connection.handle(packet);

        Assert.assertTrue(this.handler.logged());
        Assert.assertTrue(this.handler.logged(Level.WARNING, "Can not receive notification while user is not logged in"));
    }

    @Test
    public void handleInvalidPacketTest() {
        final PacketOutNotification packet = Mockito.mock(PacketOutNotification.class);
        final Notification notification = Mockito.mock(Notification.class);

        Mockito.when(notification.getNotificationId()).thenReturn(randomUniqueId());
        Mockito.when(notification.getContextId()).thenReturn(randomContextId());
        Mockito.when(notification.getTimestamp()).thenReturn(LocalDateTime.now());
        Mockito.when(notification.getType()).thenReturn(randomEnum(NotificationType.class));
        Mockito.when(notification.getMessage()).thenReturn(randomBundle());
        Mockito.when(packet.getNotification()).thenReturn(notification);

        this.login();
        this.intern.addNotification(true);
        this.handler.reset();
        this.connection.handle(packet);

        Assert.assertTrue(this.handler.logged());
        Assert.assertTrue(this.handler.logged(Level.WARNING, "Server tried to send notification for unknown context"));
        Assert.assertTrue(this.intern.called("add-notification"));
    }
}
