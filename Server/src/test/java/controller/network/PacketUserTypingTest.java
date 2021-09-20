package controller.network;

import controller.network.ClientSender.SendAction;
import controller.network.mock.MockIUser;
import controller.network.protocol.PacketUserTyping;
import model.context.spatial.IWorld;
import model.user.IUser;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import java.util.logging.Level;

public class PacketUserTypingTest extends PacketServerTest {

    public PacketUserTypingTest() {
        super(SendAction.TYPING);
    }

    @Test(expected = IllegalArgumentException.class)
    public void illegalPackagingTest() {
        this.getPacket(PacketUserTyping.class, new Object());
    }

    @Test
    public void correctPackagingTest() {
        final IUser user = Mockito.mock(IUser.class);

        Mockito.when(user.getUserId()).thenReturn(randomUniqueId());

        final PacketUserTyping packet = this.getPacket(PacketUserTyping.class, user);

        Assert.assertEquals(user.getUserId(), packet.getSenderId());
    }

    @Test
    public void handleUnexpectedPacketTest() {
        final PacketUserTyping packet = new PacketUserTyping();

        this.handler.reset();
        this.connection.handle(packet);

        Assert.assertTrue(this.handler.logged());
        Assert.assertTrue(this.handler.logged(Level.WARNING, "Can not type while not logged in"));

        final MockIUser user = this.login();

        this.handler.reset();
        this.connection.handle(packet);

        Assert.assertTrue(this.handler.logged());
        Assert.assertTrue(this.handler.logged(Level.WARNING, "Can not type while not in a world"));
        Assert.assertFalse(user.called("type"));
    }

    @Test
    public void handleInvalidPacketTest() {
        final MockIUser user = this.login();
        final PacketUserTyping packet = Mockito.mock(PacketUserTyping.class);

        user.setWorld(Mockito.mock(IWorld.class));
        Mockito.when(packet.getSenderId()).thenReturn(randomUniqueId());

        this.handler.reset();
        this.connection.handle(packet);

        Assert.assertTrue(this.handler.logged());
        Assert.assertTrue(this.handler.logged(Level.WARNING, "User-ID must be the own or null"));
        Assert.assertFalse(user.called("type"));
    }

    @Test
    public void handleCorrectTalkTest() {
        final PacketUserTyping packet = new PacketUserTyping();
        final MockIUser user = this.login();

        user.setWorld(Mockito.mock(IWorld.class));

        this.handler.reset();
        this.connection.handle(packet);

        Assert.assertFalse(this.handler.logged());
        Assert.assertTrue(user.called("type"));
    }
}
