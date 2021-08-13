package controller.network;

import controller.network.ClientSender.SendAction;
import controller.network.protocol.PacketWorldAction;
import controller.network.protocol.PacketWorldAction.Action;
import model.context.spatial.IWorld;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

public class PacketWorldActionTest extends PacketServerTest {

    public PacketWorldActionTest() {
        super(SendAction.WORLD_ACTION);
    }

    @Test(expected = IllegalArgumentException.class)
    public void illegalPackagingTest() {
        this.getPacket(PacketWorldAction.class, new Object());
    }

    @Test
    public void joinPackagingTest() {
        final IWorld world = Mockito.mock(IWorld.class);

        Mockito.when(world.getContextId()).thenReturn(randomContextId());
        Mockito.when(world.getContextName()).thenReturn(randomString());
        Mockito.when(this.user.getWorld()).thenReturn(world);

        final PacketWorldAction packet = this.getPacket(PacketWorldAction.class, world);

        Assert.assertEquals(Action.JOIN, packet.getAction());
        Assert.assertNotNull(packet.getContextId());
        Assert.assertEquals(world.getContextId(), packet.getContextId());
        Assert.assertNotNull(packet.getName());
        Assert.assertEquals(world.getContextName(), packet.getName());
        Assert.assertNull(packet.getMap());
        Assert.assertNull(packet.getMessage());
        Assert.assertTrue(packet.isSuccess());
    }

    @Test
    public void leavePackagingTest() {
        final IWorld world = Mockito.mock(IWorld.class);

        Mockito.when(world.getContextId()).thenReturn(randomContextId());
        Mockito.when(world.getContextName()).thenReturn(randomString());
        Mockito.when(this.user.getWorld()).thenReturn(null);

        final PacketWorldAction packet = this.getPacket(PacketWorldAction.class, world);

        Assert.assertEquals(Action.LEAVE, packet.getAction());
        Assert.assertNotNull(packet.getContextId());
        Assert.assertEquals(world.getContextId(), packet.getContextId());
        Assert.assertNotNull(packet.getName());
        Assert.assertEquals(world.getContextName(), packet.getName());
        Assert.assertNull(packet.getMap());
        Assert.assertNull(packet.getMessage());
        Assert.assertTrue(packet.isSuccess());
    }
}
