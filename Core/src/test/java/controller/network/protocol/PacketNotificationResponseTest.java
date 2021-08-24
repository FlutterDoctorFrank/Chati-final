package controller.network.protocol;

import controller.network.protocol.PacketNotificationResponse.Action;
import controller.network.protocol.mock.MockPacketListenerIn;
import org.junit.Assert;
import org.junit.Test;

public class PacketNotificationResponseTest extends PacketTest<PacketNotificationResponse> {

    public PacketNotificationResponseTest() {
        super(PacketNotificationResponse.class);
    }

    @Test
    public void callListenerTest() {
        final MockPacketListenerIn listener = new MockPacketListenerIn();

        this.before = new PacketNotificationResponse(randomUniqueId(), randomEnum(Action.class));
        this.before.call(listener);

        Assert.assertTrue(listener.handled(PacketNotificationResponse.class));
    }

    @Test
    public void serializationTest() {
        this.before = new PacketNotificationResponse(randomUniqueId(), randomEnum(Action.class));

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
