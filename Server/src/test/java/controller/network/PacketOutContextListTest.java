package controller.network;

import controller.network.ClientSender.SendAction;
import controller.network.protocol.PacketOutContextList;
import controller.network.protocol.PacketOutContextList.ContextInfo;
import model.context.ContextID;
import model.context.global.IGlobalContext;
import model.context.spatial.IWorld;
import model.context.spatial.Room;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import java.util.HashMap;
import java.util.Map;

public class PacketOutContextListTest extends PacketServerTest {

    public PacketOutContextListTest() {
        super(SendAction.CONTEXT_LIST);
    }

    @Test(expected = IllegalArgumentException.class)
    public void illegalPackagingTest() {
        this.getPacket(PacketOutContextList.class, new Object());
    }

    @Test
    public void worldsPackagingTest() {
        final IGlobalContext global = Mockito.mock(IGlobalContext.class);
        final Map<ContextID, IWorld> worlds = new HashMap<>();
        final int size = randomInt(2) + 1;

        while (worlds.size() < size) {
            final ContextID contextId = randomContextId();
            final IWorld world = Mockito.mock(IWorld.class);

            Mockito.when(world.getContextId()).thenReturn(contextId);
            Mockito.when(world.getContextName()).thenReturn(randomString());

            worlds.put(contextId, world);
        }

        Mockito.when(global.getContextId()).thenReturn(randomContextId());
        Mockito.when(global.getIWorlds()).thenReturn(worlds);

        final PacketOutContextList packet = this.getPacket(PacketOutContextList.class, global);

        Assert.assertNull(packet.getContextId());
        Assert.assertEquals(worlds.size(), packet.getInfos().length);

        for (final ContextInfo info : packet.getInfos()) {
            Assert.assertTrue(worlds.containsKey(info.getContextId()));
            Assert.assertEquals(worlds.get(info.getContextId()).getContextId(), info.getContextId());
            Assert.assertEquals(worlds.get(info.getContextId()).getContextName(), info.getName());
        }
    }

    @Test
    public void roomsPackagingTest() {
        final IWorld world = Mockito.mock(IWorld.class);
        final Room publicRoom = Mockito.mock(Room.class);
        final Map<ContextID, Room> rooms = new HashMap<>();
        final int size = randomInt(2) + 1;

        while (rooms.size() < size) {
            final ContextID contextId = randomContextId();
            final Room room = Mockito.mock(Room.class);

            Mockito.when(room.getContextId()).thenReturn(contextId);
            Mockito.when(room.getContextName()).thenReturn(randomString());

            rooms.put(contextId, room);
        }

        Mockito.when(publicRoom.getContextId()).thenReturn(randomContextId());
        Mockito.when(publicRoom.getContextName()).thenReturn(randomString());
        Mockito.when(world.getContextId()).thenReturn(randomContextId());
        Mockito.when(world.getPublicRoom()).thenReturn(publicRoom);
        Mockito.when(world.getPrivateRooms()).thenReturn(rooms);

        final PacketOutContextList packet = this.getPacket(PacketOutContextList.class, world);

        Assert.assertNotNull(packet.getContextId());
        Assert.assertEquals(world.getContextId(), packet.getContextId());
        Assert.assertEquals(rooms.size() + 1, packet.getInfos().length);

        for (final ContextInfo info : packet.getInfos()) {
            if (rooms.containsKey(info.getContextId())) {
                Assert.assertEquals(rooms.get(info.getContextId()).getContextId(), info.getContextId());
                Assert.assertEquals(rooms.get(info.getContextId()).getContextName(), info.getName());
                Assert.assertTrue(info.isPrivate());
            } else {
                Assert.assertEquals(publicRoom.getContextId(), info.getContextId());
                Assert.assertEquals(publicRoom.getContextName(), info.getName());
                Assert.assertFalse(info.isPrivate());
            }
        }
    }
}
