package controller.network;

import controller.network.ClientSender.SendAction;
import controller.network.protocol.PacketAudioMessage;
import model.communication.message.IAudioMessage;
import model.user.IUser;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import java.time.LocalDateTime;

public class PacketAudioMessageTest extends PacketServerTest {

    public PacketAudioMessageTest() {
        super(SendAction.AUDIO);
    }

    @Test(expected = IllegalArgumentException.class)
    public void illegalPackagingTest() {
        this.getPacket(PacketAudioMessage.class, new Object());
    }

    @Test(expected = IllegalArgumentException.class)
    public void illegalSenderPackagingTest() {
        final IAudioMessage message = Mockito.mock(IAudioMessage.class);

        Mockito.when(message.getSender()).thenReturn(null);

        this.getPacket(PacketAudioMessage.class, message);
    }

    @Test
    public void correctPackagingTest() {
        final IAudioMessage message = Mockito.mock(IAudioMessage.class);
        final IUser sender = Mockito.mock(IUser.class);

        Mockito.when(sender.getUserId()).thenReturn(randomUniqueId());
        Mockito.when(message.getSender()).thenReturn(sender);
        Mockito.when(message.getTimestamp()).thenReturn(LocalDateTime.now());
        Mockito.when(message.getAudioData()).thenReturn(randomBytes());

        final PacketAudioMessage packet = this.getPacket(PacketAudioMessage.class, message);

        Assert.assertNotNull(packet.getSenderId());
        Assert.assertEquals(sender.getUserId(), packet.getSenderId());
        Assert.assertNotNull(packet.getTimestamp());
        Assert.assertEquals(message.getTimestamp(), packet.getTimestamp());
        Assert.assertArrayEquals(message.getAudioData(), packet.getAudioData());
    }
}
