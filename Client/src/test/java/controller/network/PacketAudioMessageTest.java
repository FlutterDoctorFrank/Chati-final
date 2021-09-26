package controller.network;

import controller.network.ServerSender.SendAction;
import controller.network.protocol.PacketAudioMessage;
import org.junit.Assert;
import org.junit.Test;

public class PacketAudioMessageTest extends PacketClientTest {

    public PacketAudioMessageTest() {
        super(SendAction.VOICE);
    }

    @Test(expected = IllegalArgumentException.class)
    public void illegalArgsPackagingTest() {
        this.getPacket(PacketAudioMessage.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void illegalTypesPackagingTest() {
        this.getPacket(PacketAudioMessage.class, new Object());
    }

    @Test
    public void messagePackagingTest() {
        final byte[] voiceData = randomBytes();

        final PacketAudioMessage packet = this.getPacket(PacketAudioMessage.class, voiceData);

        Assert.assertNull(packet.getSenderId());
        Assert.assertArrayEquals(voiceData, packet.getAudioData());
    }
}
