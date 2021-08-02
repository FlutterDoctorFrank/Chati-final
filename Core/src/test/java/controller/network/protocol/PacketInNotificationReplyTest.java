package controller.network.protocol;

import controller.network.protocol.PacketInNotificationReply.Action;
import org.junit.Assert;
import org.junit.Test;

public class PacketInNotificationReplyTest extends PacketTest<PacketInNotificationReply> {

    public PacketInNotificationReplyTest() {
        super(PacketInNotificationReply.class);
    }

    @Test
    public void serializationTest() {
        this.before = new PacketInNotificationReply(randomUniqueId(), randomEnum(Action.class));

        this.serialize();
        this.equals();
    }

    @Override
    public void equals() {
        // Vergleiche Benachrichtigungs-ID und Aktion
        Assert.assertEquals(this.before.getNotificationId(), this.after.getNotificationId());
        Assert.assertEquals(this.before.getAction(), this.after.getAction());
    }
}
