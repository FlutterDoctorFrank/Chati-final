package controller.network;

import controller.network.ServerSender.SendAction;
import controller.network.protocol.PacketUserTyping;
import org.junit.Assert;
import org.junit.Test;

public class PacketUserTypingTest extends PacketClientTest {

    public PacketUserTypingTest() {
        super(SendAction.TYPING);
    }

    @Test(expected = IllegalArgumentException.class)
    public void illegalPackagingTest() {
        this.getPacket(PacketUserTyping.class, new Object());
    }

    @Test
    public void typingPackagingTest() {
        final PacketUserTyping packet = this.getPacket(PacketUserTyping.class);

        Assert.assertNull(packet.getSenderId());
    }
}
