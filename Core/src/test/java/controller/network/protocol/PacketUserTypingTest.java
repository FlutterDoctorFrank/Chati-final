package controller.network.protocol;

import controller.network.protocol.mock.MockPacketListener;
import org.junit.Assert;
import org.junit.Test;

public class PacketUserTypingTest extends PacketTest<PacketUserTyping> {

    public PacketUserTypingTest() {
        super(PacketUserTyping.class);
    }

    @Test
    public void callListenerTest() {
        final MockPacketListener listener = new MockPacketListener();

        this.before = new PacketUserTyping(randomUniqueId());
        this.before.call(listener);

        Assert.assertTrue(listener.handled(PacketUserTyping.class));
    }

    @Test
    public void serializationSetTest() {
        this.before = new PacketUserTyping(randomUniqueId());

        this.serialize();
        this.equals();
    }

    @Test
    public void serializationUnsetTest() {
        this.before = new PacketUserTyping();

        this.serialize();
        this.equals();
    }

    @Override
    public void equals() {
        // Vergleiche Sender
        if (this.before.getSenderId() != null) {
            Assert.assertNotNull(this.after.getSenderId());
            Assert.assertEquals(this.before.getSenderId(), this.after.getSenderId());
        } else {
            Assert.assertNull(this.after.getSenderId());
        }
    }
}
