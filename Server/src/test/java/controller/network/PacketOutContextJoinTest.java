package controller.network;

import controller.network.ClientSender.SendAction;
import controller.network.protocol.PacketOutContextJoin;
import model.context.spatial.IRoom;
import model.context.spatial.IWorld;
import model.context.spatial.SpatialMap;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

public class PacketOutContextJoinTest extends PacketServerTest {

    public PacketOutContextJoinTest() {
        super(SendAction.CONTEXT_JOIN);
    }

    @Test(expected = IllegalArgumentException.class)
    public void illegalPackagingTest() {
        this.getPacket(PacketOutContextJoin.class, new Object());
    }

    @Test(expected = IllegalArgumentException.class)
    public void illegalRoomPackagingTest() {
        final IRoom room = Mockito.mock(IRoom.class);

        Mockito.when(room.getMap()).thenReturn(null);
        Mockito.when(this.user.getWorld()).thenReturn(Mockito.mock(IWorld.class));

        this.getPacket(PacketOutContextJoin.class, room);
    }

    @Test
    public void joinPackagingTest() {
        final IRoom room = Mockito.mock(IRoom.class);

        Mockito.when(room.getContextId()).thenReturn(randomContextId());
        Mockito.when(room.getContextName()).thenReturn(randomString());
        Mockito.when(room.getMap()).thenReturn(randomEnum(SpatialMap.class));
        Mockito.when(this.user.getWorld()).thenReturn(Mockito.mock(IWorld.class));

        final PacketOutContextJoin packet = this.getPacket(PacketOutContextJoin.class, room);

        Assert.assertEquals(room.getContextId(), packet.getContextId());
        Assert.assertNotNull(packet.getName());
        Assert.assertEquals(room.getContextName(), packet.getName());
        Assert.assertNotNull(packet.getMap());
        Assert.assertEquals(room.getMap(), packet.getMap());
        Assert.assertTrue(packet.isJoin());
    }

    @Test
    public void leavePackagingTest() {
        final IRoom room = Mockito.mock(IRoom.class);

        Mockito.when(room.getContextId()).thenReturn(randomContextId());
        Mockito.when(this.user.getWorld()).thenReturn(null);

        final PacketOutContextJoin packet = this.getPacket(PacketOutContextJoin.class, room);

        Assert.assertEquals(room.getContextId(), packet.getContextId());
        Assert.assertNull(packet.getName());
        Assert.assertNull(packet.getMap());
        Assert.assertFalse(packet.isJoin());
    }
}
