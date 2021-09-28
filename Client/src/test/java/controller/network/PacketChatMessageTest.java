package controller.network;

import controller.network.ServerSender.SendAction;
import controller.network.protocol.PacketChatMessage;
import model.communication.message.MessageType;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import java.time.LocalDateTime;
import java.util.logging.Level;

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

    @Test
    public void handleUnexpectedPacketTest() {
        final PacketChatMessage packet = Mockito.mock(PacketChatMessage.class);

        this.handler.reset();
        this.connection.handle(packet);

        Assert.assertTrue(this.handler.logged());
        Assert.assertTrue(this.handler.logged(Level.WARNING, "Can not receive chat message while user is not logged in"));

        this.login();
        this.handler.reset();
        this.connection.handle(packet);

        Assert.assertTrue(this.handler.logged());
        Assert.assertTrue(this.handler.logged(Level.WARNING, "Can not receive chat message while user is not in a world"));
    }

    @Test
    public void handleInvalidPacketTest() {
        final PacketChatMessage packet = Mockito.mock(PacketChatMessage.class);

        this.login();
        this.joinWorld();
        this.handler.reset();
        this.connection.handle(packet);

        Assert.assertTrue(this.handler.logged());
        Assert.assertTrue(this.handler.logged(Level.WARNING, "Timestamp can not be null"));
        Mockito.when(packet.getTimestamp()).thenReturn(LocalDateTime.now());

        this.handler.reset();
        this.connection.handle(packet);

        Assert.assertTrue(this.handler.logged());
        Assert.assertTrue(this.handler.logged(Level.WARNING, "MessageType can not be null"));
        Mockito.when(packet.getMessageType()).thenReturn(randomEnum(MessageType.class, MessageType.INFO));

        this.handler.reset();
        this.connection.handle(packet);

        Assert.assertTrue(this.handler.logged());
        Assert.assertTrue(this.handler.logged(Level.WARNING, "Sender User-ID can not be null"));
        Mockito.when(packet.getSenderId()).thenReturn(randomUniqueId());

        this.handler.reset();
        this.connection.handle(packet);

        Assert.assertTrue(this.handler.logged());
        Assert.assertTrue(this.handler.logged(Level.WARNING, "Message can not be null"));
        Mockito.when(packet.getMessage()).thenReturn(randomString());
        Mockito.when(packet.getImageData()).thenReturn(randomBytes());

        this.handler.reset();
        this.connection.handle(packet);

        Assert.assertTrue(this.handler.logged());
        Assert.assertTrue(this.handler.logged(Level.WARNING, "Image-Name can not be null if an image was sent"));
        Mockito.when(packet.getImageName()).thenReturn(randomString());

        this.view.showChatMessage(true);
        this.handler.reset();
        this.connection.handle(packet);

        Assert.assertTrue(this.handler.logged());
        Assert.assertTrue(this.handler.logged(Level.WARNING, "Server tried to send message from unknown sender"));
        Mockito.when(packet.getMessageType()).thenReturn(MessageType.INFO);

        this.handler.reset();
        this.connection.handle(packet);

        Assert.assertTrue(this.handler.logged());
        Assert.assertTrue(this.handler.logged(Level.WARNING, "MessageBundle can not be null"));
    }

    @Test
    public void handleCorrectPacketTest() {
        final PacketChatMessage packet = Mockito.mock(PacketChatMessage.class);

        Mockito.when(packet.getTimestamp()).thenReturn(LocalDateTime.now());
        Mockito.when(packet.getMessageType()).thenReturn(MessageType.STANDARD);
        Mockito.when(packet.getSenderId()).thenReturn(randomUniqueId());
        Mockito.when(packet.getMessage()).thenReturn(randomString());
        Mockito.when(packet.getImageData()).thenReturn(new byte[0]);

        this.login();
        this.joinWorld();
        this.handler.reset();
        this.connection.handle(packet);

        Assert.assertFalse(this.handler.logged());
        Assert.assertTrue(this.view.called("show-chat-message"));
        Assert.assertFalse(this.view.called("show-info-message"));
        Mockito.when(packet.getMessageType()).thenReturn(MessageType.INFO);
        Mockito.when(packet.getBundle()).thenReturn(randomBundle());

        this.view.reset();
        this.handler.reset();
        this.connection.handle(packet);

        Assert.assertFalse(this.handler.logged());
        Assert.assertFalse(this.view.called("show-chat-message"));
        Assert.assertTrue(this.view.called("show-info-message"));
    }
}
