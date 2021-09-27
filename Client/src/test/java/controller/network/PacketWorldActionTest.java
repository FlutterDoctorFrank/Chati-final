package controller.network;

import controller.network.ServerSender.SendAction;
import controller.network.protocol.PacketWorldAction;
import controller.network.protocol.PacketWorldAction.Action;
import model.context.ContextID;
import model.context.spatial.ContextMap;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import java.util.logging.Level;

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

    @Test
    public void handleUnexpectedPacketTest() {
        final PacketWorldAction packet = Mockito.mock(PacketWorldAction.class);

        this.handler.reset();
        this.connection.handle(packet);

        Assert.assertTrue(this.handler.logged());
        Assert.assertTrue(this.handler.logged(Level.WARNING, "Can not receive world action while user is not logged in"));
        Mockito.when(packet.getAction()).thenReturn(Action.LEAVE);

        this.login();
        this.handler.reset();
        this.connection.handle(packet);

        Assert.assertTrue(this.handler.logged());
        Assert.assertTrue(this.handler.logged(Level.WARNING, "user is not in a world"));
        Assert.assertFalse(this.intern.called("leave-world"));
        Assert.assertFalse(this.view.called("leave-world"));
        Mockito.when(packet.getAction()).thenReturn(Action.JOIN);

        this.joinWorld();
        this.view.reset();
        this.intern.reset();
        this.handler.reset();
        this.connection.handle(packet);

        Assert.assertTrue(this.handler.logged());
        Assert.assertTrue(this.handler.logged(Level.WARNING, "user is already in a world"));
        Assert.assertFalse(this.intern.called("join-world"));
        Assert.assertFalse(this.view.called("join-world-response"));
        Mockito.when(packet.getAction()).thenReturn(Action.LEAVE);
        Mockito.when(packet.getContextId()).thenReturn(randomContextId());

        this.handler.reset();
        this.connection.handle(packet);

        Assert.assertTrue(this.handler.logged());
        Assert.assertTrue(this.handler.logged(Level.WARNING, "Can not leave a world that has not been entered"));
        Assert.assertFalse(this.intern.called("leave-world"));
        Assert.assertFalse(this.view.called("leave-world"));
    }

    @Test
    public void handleInvalidPacketTest() {
        final PacketWorldAction packet = Mockito.mock(PacketWorldAction.class);

        Mockito.when(packet.getAction()).thenReturn(Action.JOIN);

        this.login();
        this.handler.reset();
        this.connection.handle(packet);

        Assert.assertTrue(this.handler.logged());
        Assert.assertTrue(this.handler.logged(Level.WARNING, "Context-ID of the joining world can not be null"));
        Assert.assertFalse(this.intern.called("join-world"));
        Mockito.when(packet.getContextId()).thenReturn(randomContextId());
        Mockito.when(packet.isSuccess()).thenReturn(true);

        this.intern.joinWorld(true);
        this.handler.reset();
        this.connection.handle(packet);
        this.intern.joinWorld(false);

        Assert.assertTrue(this.handler.logged());
        Assert.assertTrue(this.handler.logged(Level.WARNING, "Server tried to send world join for unknown world"));
        Assert.assertTrue(this.intern.called("join-world"));
        Assert.assertFalse(this.view.called("join-world-response"));
        Mockito.when(packet.getAction()).thenReturn(Action.LEAVE);
        Mockito.when(packet.getContextId()).thenReturn(null);

        this.joinWorld();
        this.handler.reset();
        this.connection.handle(packet);

        Assert.assertTrue(this.handler.logged());
        Assert.assertTrue(this.handler.logged(Level.WARNING, "Context-ID of the leaving world can not be null"));
        Assert.assertFalse(this.intern.called("leave-world"));
        Assert.assertFalse(this.view.called("leave-world"));
    }

    @Test
    public void handleCorrectActionTest() {
        final PacketWorldAction packet = Mockito.mock(PacketWorldAction.class);

        Mockito.when(packet.isSuccess()).thenReturn(true);
        Mockito.when(packet.getAction()).thenReturn(Action.CREATE);

        this.login();
        this.handler.reset();
        this.connection.handle(packet);

        Assert.assertFalse(this.handler.logged());
        Assert.assertTrue(this.view.called("create-world-response"));
        Mockito.when(packet.getAction()).thenReturn(Action.DELETE);

        this.handler.reset();
        this.connection.handle(packet);

        Assert.assertFalse(this.handler.logged());
        Assert.assertTrue(this.view.called("delete-world-response"));
        Mockito.when(packet.getAction()).thenReturn(Action.JOIN);
        Mockito.when(packet.getContextId()).thenReturn(randomContextId());

        this.handler.reset();
        this.connection.handle(packet);

        Assert.assertFalse(this.handler.logged());
        Assert.assertTrue(this.intern.called("join-world"));
        Assert.assertTrue(this.view.called("join-world-response"));
        Mockito.when(packet.getAction()).thenReturn(Action.LEAVE);

        this.handler.reset();
        this.connection.handle(packet);

        Assert.assertFalse(this.handler.logged());
        Assert.assertTrue(this.intern.called("leave-world"));
        Assert.assertTrue(this.view.called("leave-world"));
    }
}
