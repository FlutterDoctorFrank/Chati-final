package controller.network;

import controller.network.protocol.PacketOutContextList;
import controller.network.protocol.PacketOutContextList.ContextInfo;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import java.util.logging.Level;

public class PacketOutContextListTest extends PacketClientTest {

    public PacketOutContextListTest() {

    }

    @Test
    public void handleUnexpectedPacketTest() {
        final PacketOutContextList packet = Mockito.mock(PacketOutContextList.class);

        this.handler.reset();
        this.connection.handle(packet);

        Assert.assertTrue(this.handler.logged());
        Assert.assertTrue(this.handler.logged(Level.WARNING, "Can not receive context list while user is not logged in"));
    }

    @Test
    public void handleInvalidPacketTest() {
        final PacketOutContextList packet = Mockito.mock(PacketOutContextList.class);
        final ContextInfo info = Mockito.mock(ContextInfo.class);

        Mockito.when(info.getContextId()).thenReturn(randomContextId());
        Mockito.when(info.getName()).thenReturn(randomString());
        Mockito.when(info.isPrivate()).thenReturn(false);
        Mockito.when(packet.getContextId()).thenReturn(randomContextId());
        Mockito.when(packet.getInfos()).thenReturn(new ContextInfo[]{info, info});

        this.login();
        this.manager.updateRooms(true, true);
        this.handler.reset();
        this.connection.handle(packet);

        Assert.assertTrue(this.handler.logged());
        Assert.assertTrue(this.handler.logged(Level.WARNING, "Server tried to send multiple public rooms for world"));
        Assert.assertTrue(this.handler.logged(Level.WARNING, "Server tried to send rooms info for unknown world"));
        Assert.assertTrue(this.manager.called("update-private-rooms"));
    }

    @Test
    public void handleCorrectPacketTest() {
        final PacketOutContextList packet = Mockito.mock(PacketOutContextList.class);
        final ContextInfo world = Mockito.mock(ContextInfo.class);
        final ContextInfo room = Mockito.mock(ContextInfo.class);

        Mockito.when(world.getContextId()).thenReturn(randomContextId());
        Mockito.when(world.getName()).thenReturn(randomString());
        Mockito.when(world.isPrivate()).thenReturn(false);
        Mockito.when(room.getContextId()).thenReturn(randomContextId());
        Mockito.when(room.getName()).thenReturn(randomString());
        Mockito.when(room.isPrivate()).thenReturn(true);
        Mockito.when(packet.getInfos()).thenReturn(new ContextInfo[]{world, room});

        this.login();
        this.handler.reset();
        this.connection.handle(packet);

        Assert.assertFalse(this.handler.logged());
        Assert.assertTrue(this.manager.called("update-worlds"));
        Mockito.when(packet.getContextId()).thenReturn(randomContextId());

        this.manager.reset();
        this.handler.reset();
        this.connection.handle(packet);

        Assert.assertFalse(this.handler.logged());
        Assert.assertTrue(this.manager.called("update-public-room"));
        Assert.assertTrue(this.manager.called("update-private-rooms"));
    }
}
