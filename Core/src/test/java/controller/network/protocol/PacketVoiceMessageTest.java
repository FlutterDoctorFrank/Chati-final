package controller.network.protocol;

import org.junit.Assert;
import org.junit.Test;
import java.time.LocalDateTime;

public class PacketVoiceMessageTest extends PacketTest<PacketVoiceMessage> {

    public PacketVoiceMessageTest() {
        super(PacketVoiceMessage.class, LocalDateTime.class);
    }

    @Test
    public void serializationTest() {
        final byte[] bytes = new byte[randomInt(64)];

        for (int index = 0; index < bytes.length; index++) {
            bytes[index] = (byte) randomInt();
        }

        this.before = new PacketVoiceMessage(randomUniqueId(), LocalDateTime.now(), bytes);

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
