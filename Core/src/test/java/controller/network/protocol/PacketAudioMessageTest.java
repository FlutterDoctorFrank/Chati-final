package controller.network.protocol;

import controller.network.protocol.mock.MockPacketListener;
import org.junit.Assert;
import org.junit.Test;
import java.time.LocalDateTime;

public class PacketAudioMessageTest extends PacketTest<PacketAudioMessage> {

    public PacketAudioMessageTest() {
        super(PacketAudioMessage.class, LocalDateTime.class);
    }

    @Test
    public void callListenerTest() {
        final MockPacketListener listener = new MockPacketListener();

        this.before = new PacketAudioMessage(randomBytes());
        this.before.call(listener);

        Assert.assertTrue(listener.handled(PacketAudioMessage.class));
    }

    @Test
    public void clientSerializationTest() {
        this.before = new PacketAudioMessage(randomBytes());

        this.serialize();
        this.equals();
    }

    @Test
    public void serverAudioSerializationTest() {
        this.before = new PacketAudioMessage(LocalDateTime.now(), randomBytes(), randomFloat(), randomInt());

        this.serialize();
        this.equals();
    }

    @Test
    public void serverVoiceSerializationTest() {
        this.before = new PacketAudioMessage(randomUniqueId(), LocalDateTime.now(), randomBytes());

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
        Assert.assertArrayEquals(this.before.getAudioData(), this.after.getAudioData());
        Assert.assertEquals(this.before.getPosition(), this.after.getPosition(), 0.0f);
        Assert.assertEquals(this.before.getSeconds(), this.after.getSeconds());
    }
}
