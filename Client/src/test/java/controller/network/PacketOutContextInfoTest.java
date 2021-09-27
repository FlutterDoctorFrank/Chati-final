package controller.network;

import controller.network.protocol.PacketOutContextInfo;
import model.context.spatial.ContextMusic;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import java.util.UUID;
import java.util.logging.Level;

public class PacketOutContextInfoTest extends PacketClientTest {

    public PacketOutContextInfoTest() {

    }

    @Test
    public void handleUnexpectedPacketTest() {
        final PacketOutContextInfo packet = Mockito.mock(PacketOutContextInfo.class);

        this.handler.reset();
        this.connection.handle(packet);

        Assert.assertTrue(this.handler.logged());
        Assert.assertTrue(this.handler.logged(Level.WARNING, "Can not receive context info while user is not logged in"));

        this.login();
        this.handler.reset();
        this.connection.handle(packet);

        Assert.assertTrue(this.handler.logged());
        Assert.assertTrue(this.handler.logged(Level.WARNING, "Can not receive context info while user is not in a world"));
    }

    @Test
    public void handleInvalidPacketTest() {
        final PacketOutContextInfo packet = Mockito.mock(PacketOutContextInfo.class);

        Mockito.when(packet.getContextId()).thenReturn(randomContextId());

        this.login();
        this.joinWorld();
        this.intern.setContext(true);
        this.handler.reset();
        this.connection.handle(packet);

        Assert.assertTrue(this.handler.logged());
        Assert.assertTrue(this.handler.logged(Level.WARNING, "Server tried to send context info for unknown context"));
        Assert.assertTrue(this.intern.called("set-music"));
        Mockito.when(packet.getMutes()).thenReturn(new UUID[]{randomUniqueId()});

        this.intern.setContext(false);
        this.manager.getExternUserController(true);
        this.handler.reset();
        this.connection.handle(packet);

        Assert.assertTrue(this.handler.logged());
        Assert.assertTrue(this.handler.logged(Level.WARNING, "Server tried to send mute of unknown user"));
        Assert.assertTrue(this.manager.called("get-extern-user-controller"));
    }

    @Test
    public void handleCorrectPacketTest() {
        final PacketOutContextInfo packet = Mockito.mock(PacketOutContextInfo.class);

        this.login();
        this.joinWorld();

        Mockito.when(packet.getContextId()).thenReturn(randomContextId());
        Mockito.when(packet.getMusic()).thenReturn(randomEnum(ContextMusic.class));
        Mockito.when(packet.getMutes()).thenReturn(new UUID[]{this.intern.getUserId(), randomUniqueId()});

        this.handler.reset();
        this.connection.handle(packet);

        Assert.assertFalse(this.handler.logged());
        Assert.assertTrue(this.intern.called("set-music"));
        Assert.assertTrue(this.intern.called("set-mute"));
        Assert.assertTrue(this.manager.called("get-extern-user-controller"));
    }
}
