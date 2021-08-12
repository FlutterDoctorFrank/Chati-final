package controller.network.protocol;

import controller.network.protocol.mock.MockPacketListenerIn;
import org.junit.Assert;
import org.junit.Test;

public class PacketInContextInteractTest extends PacketTest<PacketInContextInteract> {

    public PacketInContextInteractTest() {
        super(PacketInContextInteract.class);
    }

    @Test
    public void callListenerTest() {
        final MockPacketListenerIn listener = new MockPacketListenerIn();

        this.before = new PacketInContextInteract(randomContextId());
        this.before.call(listener);

        Assert.assertTrue(listener.handled(PacketInContextInteract.class));
    }

    @Test
    public void serializationTest() {
        this.before = new PacketInContextInteract(randomContextId());

        this.serialize();
        this.equals();
    }

    @Override
    public void equals() {
        // Vergleiche Kontext-ID
        Assert.assertEquals(this.before.getContextId(), this.after.getContextId());
    }
}
