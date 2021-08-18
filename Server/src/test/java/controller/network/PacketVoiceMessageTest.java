package controller.network;

import controller.network.ClientSender.SendAction;
import controller.network.protocol.PacketChatMessage;
import controller.network.protocol.PacketVoiceMessage;
import model.communication.message.ITextMessage;
import model.communication.message.IVoiceMessage;
import model.communication.message.MessageType;
import model.user.IUser;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import java.time.LocalDateTime;

public class PacketVoiceMessageTest extends PacketServerTest {

    public PacketVoiceMessageTest() {
        super(SendAction.VOICE);
    }

    @Test(expected = IllegalArgumentException.class)
    public void illegalPackagingTest() {
        this.getPacket(PacketVoiceMessage.class, new Object());
    }

    @Test(expected = IllegalArgumentException.class)
    public void illegalSenderPackagingTest() {
        final IVoiceMessage message = Mockito.mock(IVoiceMessage.class);

        Mockito.when(message.getSender()).thenReturn(null);

        this.getPacket(PacketVoiceMessage.class, message);
    }

    @Test
    public void correctPackagingTest() {
        final IVoiceMessage message = Mockito.mock(IVoiceMessage.class);
        final IUser sender = Mockito.mock(IUser.class);

        Mockito.when(sender.getUserId()).thenReturn(randomUniqueId());
        Mockito.when(message.getSender()).thenReturn(sender);
        Mockito.when(message.getTimestamp()).thenReturn(LocalDateTime.now());
        Mockito.when(message.getVoiceData()).thenReturn(randomBytes());

        final PacketVoiceMessage packet = this.getPacket(PacketVoiceMessage.class, message);

        Assert.assertNotNull(packet.getSenderId());
        Assert.assertEquals(sender.getUserId(), packet.getSenderId());
        Assert.assertNotNull(packet.getTimestamp());
        Assert.assertEquals(message.getTimestamp(), packet.getTimestamp());
        Assert.assertArrayEquals(message.getVoiceData(), packet.getVoiceData());
    }
}
