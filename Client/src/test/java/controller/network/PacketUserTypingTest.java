package controller.network;

import controller.network.ServerSender.SendAction;
import controller.network.protocol.PacketUserTyping;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import java.util.logging.Level;

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

    @Test
    public void handleUnexpectedPacketTest() {
        final PacketUserTyping packet = Mockito.mock(PacketUserTyping.class);

        this.handler.reset();
        this.connection.handle(packet);

        Assert.assertTrue(this.handler.logged());
        Assert.assertTrue(this.handler.logged(Level.WARNING, "Can not receive typing information while user is not logged in"));

        this.login();
        this.handler.reset();
        this.connection.handle(packet);

        Assert.assertTrue(this.handler.logged());
        Assert.assertTrue(this.handler.logged(Level.WARNING, "Can not receive typing information while user is not in a world"));
    }

    @Test
    public void handleInvalidPacketTest() {
        final PacketUserTyping packet = Mockito.mock(PacketUserTyping.class);

        this.login();
        this.joinWorld();
        this.handler.reset();
        this.connection.handle(packet);

        Assert.assertTrue(this.handler.logged());
        Assert.assertTrue(this.handler.logged(Level.WARNING, "Typing User-ID can not be null"));
        Assert.assertFalse(this.view.called("show-typing-user"));
    }

    @Test
    public void handleCorrectPacketTest() {
        final PacketUserTyping packet = Mockito.mock(PacketUserTyping.class);

        Mockito.when(packet.getSenderId()).thenReturn(randomUniqueId());

        this.login();
        this.joinWorld();
        this.handler.reset();
        this.connection.handle(packet);

        Assert.assertFalse(this.handler.logged());
        Assert.assertTrue(this.view.called("show-typing-user"));
    }
}
