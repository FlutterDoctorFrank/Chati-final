package controller.network;

import controller.network.protocol.PacketOutContextJoin;
import model.context.spatial.ContextMap;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import java.util.logging.Level;

public class PacketOutContextJoinTest extends PacketClientTest {

    public PacketOutContextJoinTest() {

    }

    @Test
    public void handleUnexpectedPacketTest() {
        final PacketOutContextJoin packet = Mockito.mock(PacketOutContextJoin.class);

        this.handler.reset();
        this.connection.handle(packet);

        Assert.assertTrue(this.handler.logged());
        Assert.assertTrue(this.handler.logged(Level.WARNING, "Can not join context while user is not logged in"));

        this.login();
        this.handler.reset();
        this.connection.handle(packet);

        Assert.assertTrue(this.handler.logged());
        Assert.assertTrue(this.handler.logged(Level.WARNING, "Can not join context while user is not in a world"));
    }

    @Test
    public void handleInvalidPacketTest() {
        final PacketOutContextJoin packet = Mockito.mock(PacketOutContextJoin.class);

        Mockito.when(packet.getContextId()).thenReturn(randomContextId());
        Mockito.when(packet.isJoin()).thenReturn(true);

        this.login();
        this.joinWorld();
        this.handler.reset();
        this.connection.handle(packet);

        Assert.assertTrue(this.handler.logged());
        Assert.assertTrue(this.handler.logged(Level.WARNING, "Name of joined context can not be null"));
        Assert.assertFalse(this.intern.called("join-room"));
        Mockito.when(packet.getName()).thenReturn(randomString());

        this.handler.reset();
        this.connection.handle(packet);

        Assert.assertTrue(this.handler.logged());
        Assert.assertTrue(this.handler.logged(Level.WARNING, "Map of joined context can not be null"));
        Assert.assertFalse(this.intern.called("join-room"));
        Mockito.when(packet.getMap()).thenReturn(randomEnum(ContextMap.class));

        this.intern.joinRoom(true);
        this.handler.reset();
        this.connection.handle(packet);

        Assert.assertTrue(this.handler.logged());
        Assert.assertTrue(this.handler.logged(Level.WARNING, "Server tried to send context join unknown room"));
        Assert.assertTrue(this.intern.called("join-room"));
    }
}
