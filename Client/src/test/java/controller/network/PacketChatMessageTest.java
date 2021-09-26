package controller.network;

import controller.network.ServerSender.SendAction;
import controller.network.protocol.PacketChatMessage;
import org.junit.Assert;
import org.junit.Test;

public class PacketChatMessageTest extends PacketClientTest {

    public PacketChatMessageTest() {
        super(SendAction.MESSAGE);
    }

    @Test(expected = IllegalArgumentException.class)
    public void illegalArgsPackagingTest() {
        this.getPacket(PacketChatMessage.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void illegalTypesPackagingTest() {
        this.getPacket(PacketChatMessage.class, new Object());
    }

    @Test
    public void messagePackagingTest() {
        final String message = randomString();

        final PacketChatMessage packet = this.getPacket(PacketChatMessage.class, message);

        Assert.assertNull(packet.getSenderId());
        Assert.assertNotNull(packet.getMessage());
        Assert.assertEquals(message, packet.getMessage());
    }
}
