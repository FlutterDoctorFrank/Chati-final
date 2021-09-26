package controller.network;

import controller.network.ServerSender.SendAction;
import controller.network.protocol.PacketWorldAction;
import controller.network.protocol.PacketWorldAction.Action;
import model.context.ContextID;
import model.context.spatial.ContextMap;
import org.junit.Assert;
import org.junit.Test;

public class PacketWorldActionTest extends PacketClientTest {

    public PacketWorldActionTest() {

    }

    @Test(expected = IllegalArgumentException.class)
    public void illegalActionArgsPackagingTest() {
        this.getPacket(SendAction.WORLD_ACTION, PacketWorldAction.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void illegalActionTypesPackagingTest() {
        this.getPacket(SendAction.WORLD_ACTION, PacketWorldAction.class, new Object(), new Object());
    }

    @Test
    public void joinPackagingTest() {
        final ContextID worldId = randomContextId();

        final PacketWorldAction packet = this.getPacket(SendAction.WORLD_ACTION, PacketWorldAction.class, worldId, true);

        Assert.assertEquals(Action.JOIN, packet.getAction());
        Assert.assertNotNull(packet.getContextId());
        Assert.assertEquals(worldId, packet.getContextId());
    }

    @Test
    public void leavePackagingTest() {
        final ContextID worldId = randomContextId();

        final PacketWorldAction packet = this.getPacket(SendAction.WORLD_ACTION, PacketWorldAction.class, worldId, false);

        Assert.assertEquals(Action.LEAVE, packet.getAction());
        Assert.assertNotNull(packet.getContextId());
        Assert.assertEquals(worldId, packet.getContextId());
    }

    @Test(expected = IllegalArgumentException.class)
    public void illegalCreateArgsPackagingTest() {
        this.getPacket(SendAction.WORLD_CREATE, PacketWorldAction.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void illegalCreateTypesPackagingTest() {
        this.getPacket(SendAction.WORLD_CREATE, PacketWorldAction.class, new Object(), new Object());
    }

    @Test
    public void createPackagingTest() {
        final ContextMap map = randomEnum(ContextMap.class);
        final String name = randomString();

        final PacketWorldAction packet = this.getPacket(SendAction.WORLD_CREATE, PacketWorldAction.class, map, name);

        Assert.assertEquals(Action.CREATE, packet.getAction());
        Assert.assertNotNull(packet.getMap());
        Assert.assertEquals(map, packet.getMap());
        Assert.assertNotNull(packet.getName());
        Assert.assertEquals(name, packet.getName());
    }

    @Test(expected = IllegalArgumentException.class)
    public void illegalDeleteArgsPackagingTest() {
        this.getPacket(SendAction.WORLD_DELETE, PacketWorldAction.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void illegalDeleteTypesPackagingTest() {
        this.getPacket(SendAction.WORLD_DELETE, PacketWorldAction.class, new Object());
    }

    @Test
    public void deletePackagingTest() {
        final ContextID worldId = randomContextId();

        final PacketWorldAction packet = this.getPacket(SendAction.WORLD_DELETE, PacketWorldAction.class, worldId);

        Assert.assertEquals(Action.DELETE, packet.getAction());
        Assert.assertNotNull(packet.getContextId());
        Assert.assertEquals(worldId, packet.getContextId());
    }
}
