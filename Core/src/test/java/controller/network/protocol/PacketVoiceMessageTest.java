package controller.network.protocol;

import controller.network.protocol.mock.MockPacketListener;
import org.junit.Assert;
import org.junit.Test;
import java.time.LocalDateTime;

public class PacketVoiceMessageTest extends PacketTest<PacketVoiceMessage> {

    public PacketVoiceMessageTest() {
        super(PacketVoiceMessage.class, LocalDateTime.class);
    }

    @Test
    public void callListenerTest() {
        final MockPacketListener listener = new MockPacketListener();

        this.before = new PacketVoiceMessage(randomBytes());
        this.before.call(listener);

        Assert.assertTrue(listener.handled(PacketVoiceMessage.class));
    }

    @Test
    public void clientSerializationTest() {
        this.before = new PacketVoiceMessage(randomBytes());

        this.serialize();
        this.equals();
    }

    @Test
    public void serverSerializationTest() {
        this.before = new PacketVoiceMessage(randomUniqueId(), LocalDateTime.now(), randomBytes());

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

        // Vergleiche Zeitstempel
        if (this.before.getTimestamp() != null) {
            Assert.assertNotNull(this.after.getTimestamp());
            Assert.assertEquals(this.before.getTimestamp(), this.after.getTimestamp());
        } else {
            Assert.assertNull(this.after.getTimestamp());
        }

        // Vergleiche Sprachdaten
        Assert.assertArrayEquals(this.before.getVoiceData(), this.after.getVoiceData());
    }
}
