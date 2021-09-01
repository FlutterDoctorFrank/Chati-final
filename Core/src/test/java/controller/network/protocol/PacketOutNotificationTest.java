package controller.network.protocol;

import controller.network.protocol.PacketOutNotification.Notification;
import controller.network.protocol.mock.MockPacketListenerOut;
import model.notification.NotificationType;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import java.time.LocalDateTime;

public class PacketOutNotificationTest extends PacketTest<PacketOutNotification> {

    public PacketOutNotificationTest() {
        super(PacketOutNotification.class, LocalDateTime.class);
    }

    @Test
    public void callListenerTest() {
        final MockPacketListenerOut listener = new MockPacketListenerOut();

        this.before = new PacketOutNotification(Mockito.mock(Notification.class));
        this.before.call(listener);

        Assert.assertTrue(listener.handled(PacketOutNotification.class));
    }

    @Test
    public void serializationTest() {
        this.before = new PacketOutNotification(new Notification(randomUniqueId(), randomContextId(), randomBundle(),
                LocalDateTime.now(), randomEnum(NotificationType.class)));
        this.before.getNotification().setAccepted(randomBoolean());
        this.before.getNotification().setDeclined(randomBoolean());
        this.before.getNotification().setRead(randomBoolean());

        this.serialize();
        this.equals();
    }

    @Test
    public void equalNotificationTest() {
        final Notification first = new Notification(randomUniqueId(), randomContextId(), randomBundle(),
                LocalDateTime.now(), randomEnum(NotificationType.class));
        final Notification second = new Notification(first.getNotificationId(), first.getContextId(),
                first.getMessage(), first.getTimestamp(), first.getType());

        Assert.assertEquals(first, first);
        Assert.assertEquals(first, second);
        Assert.assertEquals(first.hashCode(), second.hashCode());
        Assert.assertNotEquals(first, new Object());
    }

    @Override
    public void equals() {
        // Vergleiche Benachrichtigung
        Assert.assertEquals(this.before.getNotification(), this.after.getNotification());

        // Vergleichen der genauen Werte der Benachrichtigung
        Assert.assertEquals(this.before.getNotification().getContextId(), this.after.getNotification().getContextId());
        Assert.assertEquals(this.before.getNotification().getMessage(), this.after.getNotification().getMessage());
        Assert.assertEquals(this.before.getNotification().getTimestamp(), this.after.getNotification().getTimestamp());
        Assert.assertEquals(this.before.getNotification().getType(), this.after.getNotification().getType());
        Assert.assertEquals(this.before.getNotification().isAccepted(), this.after.getNotification().isAccepted());
        Assert.assertEquals(this.before.getNotification().isDeclined(), this.after.getNotification().isDeclined());
        Assert.assertEquals(this.before.getNotification().isRead(), this.after.getNotification().isRead());
    }
}
