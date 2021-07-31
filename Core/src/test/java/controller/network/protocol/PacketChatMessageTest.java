package controller.network.protocol;

import model.communication.message.MessageType;
import org.junit.Assert;
import org.junit.Test;
import java.time.LocalDateTime;

public class PacketChatMessageTest extends PacketTest<PacketChatMessage> {

    public PacketChatMessageTest() {
        super(PacketChatMessage.class, LocalDateTime.class);
    }

    @Test
    public void infoSerializationTest() {
        this.before = new PacketChatMessage(randomBundle(), LocalDateTime.now());

        this.serialize();
        this.equals();
    }

    @Test
    public void messageSerializationTest() {
        this.before = new PacketChatMessage(randomEnum(MessageType.class, MessageType.INFO), randomUniqueId(),
                randomString(), LocalDateTime.now());

        this.serialize();
        this.equals();
    }

    @Override
    public void equals() {
        // Vergleiche Nachrichten-Typ
        if (this.before.getMessageType() != null) {
            Assert.assertNotNull(this.after.getMessageType());
            Assert.assertEquals(this.before.getMessageType(), this.after.getMessageType());
        } else {
            Assert.assertNull(this.after.getMessageType());
        }

        // Vergleiche Sender
        if (this.before.getSenderId() != null) {
            Assert.assertNotNull(this.after.getSenderId());
            Assert.assertEquals(this.before.getSenderId(), this.after.getSenderId());
        } else {
            Assert.assertNull(this.after.getSenderId());
        }

        // Vergleiche Nachricht
        if (this.before.getMessage() != null) {
            Assert.assertNotNull(this.after.getMessage());
            Assert.assertEquals(this.before.getMessage(), this.after.getMessage());
        } else {
            Assert.assertNull(this.after.getMessage());
        }

        // Vergleiche Zeitstempel
        if (this.before.getTimestamp() != null) {
            Assert.assertNotNull(this.after.getTimestamp());
            Assert.assertEquals(this.before.getTimestamp(), this.after.getTimestamp());
        } else {
            Assert.assertNull(this.after.getTimestamp());
        }

        // Vergleiche Informations-Nachricht
        if (this.before.getBundle() != null) {
            Assert.assertNotNull(this.after.getBundle());
            Assert.assertEquals(this.before.getBundle(), this.after.getBundle());
        } else {
            Assert.assertNull(this.after.getBundle());
        }
    }
}
