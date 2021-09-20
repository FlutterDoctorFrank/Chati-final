package controller.network;

import controller.network.ClientSender.SendAction;
import controller.network.mock.MockIUser;
import controller.network.protocol.PacketWorldAction;
import controller.network.protocol.PacketWorldAction.Action;
import model.context.ContextID;
import model.context.spatial.ContextMap;
import model.context.spatial.IWorld;
import model.exception.ContextNotFoundException;
import model.exception.IllegalWorldActionException;
import model.exception.NoPermissionException;
import model.exception.UserNotFoundException;
import model.role.Permission;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import java.util.logging.Level;

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

    @Test
    public void handleUnexpectedPacketTest() {
        final PacketWorldAction packet = new PacketWorldAction(randomEnum(Action.class, Action.CREATE), randomContextId());

        this.handler.reset();
        this.connection.handle(packet);

        Assert.assertTrue(this.handler.logged());
        Assert.assertTrue(this.handler.logged(Level.WARNING, "Can not perform world action while not logged in"));
    }

    @Test
    public void handleIllegalPacketTest() {
        final PacketWorldAction packet = Mockito.mock(PacketWorldAction.class);
        final MockIUser user = this.login();

        Mockito.when(packet.getAction()).thenReturn(Action.CREATE);
        if (randomBoolean()) {
            Mockito.when(packet.getMap()).thenReturn(randomEnum(ContextMap.class));
        } else {
            Mockito.when(packet.getName()).thenReturn(randomString());
        }

        this.handler.reset();
        this.connection.handle(packet);

        Assert.assertTrue(this.handler.logged());
        Assert.assertTrue(this.handler.logged(Level.WARNING, "Missing world-name or world-map for create action"));
        Mockito.when(packet.getAction()).thenReturn(randomEnum(Action.class, Action.CREATE));

        this.handler.reset();
        this.connection.handle(packet);

        Assert.assertTrue(this.handler.logged());
        Assert.assertTrue(this.handler.logged(Level.WARNING, "Missing context-id for world action"));
        Assert.assertFalse(user.called("join-world"));
        Assert.assertFalse(user.called("leave-world"));
    }

    @Test(expected = IllegalStateException.class)
    public void handleUnknownUserTest() {
        final PacketWorldAction packet = Mockito.mock(PacketWorldAction.class);
        final MockIUser user = this.login();

        Mockito.when(packet.getAction()).thenReturn(Action.CREATE);
        Mockito.when(packet.getMap()).thenReturn(randomEnum(ContextMap.class));
        Mockito.when(packet.getName()).thenReturn(randomString());

        try {
            Mockito.doThrow(new UserNotFoundException("Unhandled mocked exception", user.getUserId()))
                    .when(this.global).createWorld(Mockito.eq(user.getUserId()), Mockito.anyString(), Mockito.any(ContextMap.class));

            this.handler.reset();
            this.connection.handle(packet);
        } catch (IllegalWorldActionException | UserNotFoundException | NoPermissionException ex) {
            Assert.fail(ex.getMessage());
        }
    }

    @Test
    public void handleIllegalActionTest() {
        final PacketWorldAction packet = Mockito.mock(PacketWorldAction.class);
        final MockIUser user = this.login();

        user.joinWorld(true, false);
        Mockito.when(packet.getAction()).thenReturn(Action.JOIN);
        Mockito.when(packet.getContextId()).thenReturn(randomContextId());

        this.handler.reset();
        this.connection.handle(packet);

        Assert.assertTrue(this.handler.logged());
        Assert.assertTrue(this.handler.logged(Level.WARNING, "unknown world"));
        Assert.assertTrue(user.called("join-world"));

        user.reset();
        user.joinWorld(false, true);

        this.handler.reset();
        this.connection.handle(packet);

        Assert.assertTrue(user.called("join-world"));

        try {
            Mockito.when(packet.getAction()).thenReturn(Action.DELETE);
            Mockito.doThrow(new NoPermissionException("Unhandled mocked exception", "", user, randomEnum(Permission.class)))
                    .when(this.global).removeWorld(Mockito.eq(user.getUserId()), Mockito.any(ContextID.class));

            this.handler.reset();
            this.connection.handle(packet);

            Assert.assertTrue(this.handler.logged());
            Assert.assertTrue(this.handler.logged(Level.INFO, "missing permission"));
        } catch (ContextNotFoundException | UserNotFoundException | NoPermissionException ex) {
            Assert.fail(ex.getMessage());
        }
    }

    @Test
    public void handleCorrectActionTest() {
        final PacketWorldAction packet = Mockito.mock(PacketWorldAction.class);
        final MockIUser user = this.login();

        Mockito.when(packet.getContextId()).thenReturn(randomContextId());
        Mockito.when(packet.getAction()).thenReturn(Action.JOIN);

        this.handler.reset();
        this.connection.handle(packet);

        Assert.assertFalse(this.handler.logged());
        Assert.assertTrue(user.called("join-world"));
        Mockito.when(packet.getAction()).thenReturn(Action.LEAVE);

        this.handler.reset();
        this.connection.handle(packet);

        Assert.assertFalse(this.handler.logged());
        Assert.assertTrue(user.called("leave-world"));
        Mockito.when(packet.getAction()).thenReturn(Action.DELETE);

        this.handler.reset();
        this.connection.handle(packet);
    }
}
