package controller.network.protocol;

import controller.network.protocol.PacketOutNotification.Notification;
import org.junit.Assert;
import org.junit.Test;
import java.time.LocalDateTime;

public class PacketOutNotificationTest extends PacketTest<PacketOutNotification> {

    public PacketOutNotificationTest() {
        super(PacketOutNotification.class, LocalDateTime.class);
    }

    @Test
    public void serializationTest() {
        this.before = new PacketOutNotification(new Notification(randomUniqueId(), randomContextId(), randomBundle(),
                LocalDateTime.now(), randomBoolean()));

        this.serialize();
        this.equals();
    }

    @Override
    public void equals() {
        // Vergleiche Benachrichtigung
        Assert.assertEquals(this.before.getNotification(), this.after.getNotification());

        // Vergleichen der genauen Werte der Benachrichtigung
        Assert.assertEquals(this.before.getNotification().getContextId(), this.after.getNotification().getContextId());
        Assert.assertEquals(this.before.getNotification().getMessage(), this.after.getNotification().getMessage());
        Assert.assertEquals(this.before.getNotification().getTimestamp(), this.after.getNotification().getTimestamp());
        Assert.assertEquals(this.before.getNotification().isRequest(), this.after.getNotification().isRequest());
    }
}
