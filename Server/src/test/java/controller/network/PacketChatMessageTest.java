package controller.network;

import controller.network.ClientSender.SendAction;
import controller.network.protocol.PacketChatMessage;
import controller.network.protocol.PacketOutContextJoin;
import model.communication.message.ITextMessage;
import model.communication.message.MessageType;
import model.context.spatial.IRoom;
import model.context.spatial.IWorld;
import model.user.IUser;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import java.time.LocalDateTime;

public class PacketChatMessageTest extends PacketServerTest {

    public PacketChatMessageTest() {
        super(SendAction.MESSAGE);
    }

    @Test(expected = IllegalArgumentException.class)
    public void illegalPackagingTest() {
        this.getPacket(PacketChatMessage.class, new Object());
    }

    @Test(expected = IllegalArgumentException.class)
    public void missingSenderPackagingTest() {
        final ITextMessage message = Mockito.mock(ITextMessage.class);

        Mockito.when(message.getMessageType()).thenReturn(MessageType.STANDARD);
        Mockito.when(message.getSender()).thenReturn(null);

        this.getPacket(PacketChatMessage.class, message);
    }

    @Test(expected = IllegalArgumentException.class)
    public void missingMessagePackagingTest() {
        final ITextMessage message = Mockito.mock(ITextMessage.class);

        Mockito.when(message.getMessageType()).thenReturn(MessageType.STANDARD);
        Mockito.when(message.getTextMessage()).thenReturn(null);

        this.getPacket(PacketChatMessage.class, message);
    }

    @Test(expected = IllegalArgumentException.class)
    public void missingBundlePackagingTest() {
        final ITextMessage message = Mockito.mock(ITextMessage.class);

        Mockito.when(message.getMessageType()).thenReturn(MessageType.INFO);
        Mockito.when(message.getMessageBundle()).thenReturn(null);

        this.getPacket(PacketChatMessage.class, message);
    }

    @Test
    public void chatPackagingTest() {
        final ITextMessage message = Mockito.mock(ITextMessage.class);
        final IUser sender = Mockito.mock(IUser.class);

        Mockito.when(sender.getUserId()).thenReturn(randomUniqueId());
        Mockito.when(message.getMessageType()).thenReturn(randomEnum(MessageType.class, MessageType.INFO));
        Mockito.when(message.getSender()).thenReturn(sender);
        Mockito.when(message.getTextMessage()).thenReturn(randomString());
        Mockito.when(message.getTimestamp()).thenReturn(LocalDateTime.now());

        final PacketChatMessage packet = this.getPacket(PacketChatMessage.class, message);

        Assert.assertNull(packet.getBundle());
        Assert.assertNotNull(packet.getMessageType());
        Assert.assertEquals(message.getMessageType(), packet.getMessageType());
        Assert.assertNotNull(packet.getSenderId());
        Assert.assertEquals(sender.getUserId(), packet.getSenderId());
        Assert.assertNotNull(packet.getMessage());
        Assert.assertEquals(message.getTextMessage(), packet.getMessage());
        Assert.assertNotNull(packet.getTimestamp());
        Assert.assertEquals(message.getTimestamp(), packet.getTimestamp());
    }

    @Test
    public void infoPackagingTest() {
        final ITextMessage message = Mockito.mock(ITextMessage.class);

        Mockito.when(message.getMessageType()).thenReturn(MessageType.INFO);
        Mockito.when(message.getMessageBundle()).thenReturn(randomBundle());
        Mockito.when(message.getTimestamp()).thenReturn(LocalDateTime.now());

        final PacketChatMessage packet = this.getPacket(PacketChatMessage.class, message);

        Assert.assertNotNull(packet.getBundle());
        Assert.assertEquals(message.getMessageBundle(), packet.getBundle());
        Assert.assertNotNull(packet.getMessageType());
        Assert.assertEquals(message.getMessageType(), packet.getMessageType());
        Assert.assertNull(packet.getSenderId());
        Assert.assertNull(packet.getMessage());
        Assert.assertNotNull(packet.getTimestamp());
        Assert.assertEquals(message.getTimestamp(), packet.getTimestamp());
    }
}
