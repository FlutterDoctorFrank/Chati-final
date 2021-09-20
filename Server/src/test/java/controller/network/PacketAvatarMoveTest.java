package controller.network;

import controller.network.ClientSender.SendAction;
import controller.network.mock.MockIUser;
import controller.network.protocol.PacketAvatarMove;
import controller.network.protocol.PacketAvatarMove.AvatarAction;
import model.context.spatial.Direction;
import model.context.spatial.ILocation;
import model.context.spatial.IWorld;
import model.user.IUser;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import java.util.logging.Level;

public class PacketAvatarMoveTest extends PacketServerTest {

    public PacketAvatarMoveTest() {

    }

    @Test(expected = IllegalArgumentException.class)
    public void illegalSpawnPackagingTest() {
        this.getPacket(SendAction.AVATAR_SPAWN, PacketAvatarMove.class, new Object());
    }

    @Test(expected = IllegalArgumentException.class)
    public void illegalMovePackagingTest() {
        this.getPacket(SendAction.AVATAR_MOVE, PacketAvatarMove.class, new Object());
    }

    @Test(expected = IllegalArgumentException.class)
    public void illegalRemovePackagingTest() {
        this.getPacket(SendAction.AVATAR_REMOVE, PacketAvatarMove.class, new Object());
    }

    @Test(expected = IllegalStateException.class)
    public void illegalSpawnUserPackagingTest() {
        final IUser user = Mockito.mock(IUser.class);

        Mockito.when(user.getLocation()).thenReturn(null);

        this.getPacket(SendAction.AVATAR_SPAWN, PacketAvatarMove.class, user);
    }

    @Test(expected = IllegalStateException.class)
    public void illegalMoveUserPackagingTest() {
        final IUser user = Mockito.mock(IUser.class);

        Mockito.when(user.getLocation()).thenReturn(null);

        this.getPacket(SendAction.AVATAR_MOVE, PacketAvatarMove.class, user);
    }

    @Test
    public void spawnPackagingTest() {
        final IUser target = Mockito.mock(IUser.class);
        final ILocation location = Mockito.mock(ILocation.class);

        Mockito.when(location.getDirection()).thenReturn(randomEnum(Direction.class));
        Mockito.when(location.getPosX()).thenReturn(randomFloat());
        Mockito.when(location.getPosY()).thenReturn(randomFloat());
        Mockito.when(target.getLocation()).thenReturn(location);
        Mockito.when(target.getUserId()).thenReturn(randomUniqueId());
        Mockito.when(target.isMovable()).thenReturn(randomBoolean());

        final PacketAvatarMove packet = this.getPacket(SendAction.AVATAR_SPAWN, PacketAvatarMove.class, target);

        Assert.assertEquals(AvatarAction.SPAWN_AVATAR, packet.getAction());
        Assert.assertNotNull(packet.getUserId());
        Assert.assertEquals(target.getUserId(), packet.getUserId());
        Assert.assertEquals(location.getDirection(), packet.getDirection());
        Assert.assertEquals(location.getPosX(), packet.getPosX(), 0.0f);
        Assert.assertEquals(location.getPosY(), packet.getPosY(), 0.0f);
        Assert.assertFalse(packet.isSprinting());
        Assert.assertEquals(target.isMovable(), packet.isMovable());
    }

    @Test
    public void movePackagingTest() {
        final IUser target = Mockito.mock(IUser.class);
        final ILocation location = Mockito.mock(ILocation.class);

        Mockito.when(location.getDirection()).thenReturn(randomEnum(Direction.class));
        Mockito.when(location.getPosX()).thenReturn(randomFloat());
        Mockito.when(location.getPosY()).thenReturn(randomFloat());
        Mockito.when(target.getLocation()).thenReturn(location);
        Mockito.when(target.getUserId()).thenReturn(randomUniqueId());
        Mockito.when(target.isSprinting()).thenReturn(randomBoolean());
        Mockito.when(target.isMovable()).thenReturn(randomBoolean());
        Mockito.when(this.user.getLocation()).thenReturn(location);

        final PacketAvatarMove packet = this.getPacket(SendAction.AVATAR_MOVE, PacketAvatarMove.class, target);

        Assert.assertEquals(AvatarAction.MOVE_AVATAR, packet.getAction());
        Assert.assertNotNull(packet.getUserId());
        Assert.assertEquals(target.getUserId(), packet.getUserId());
        Assert.assertEquals(location.getDirection(), packet.getDirection());
        Assert.assertEquals(location.getPosX(), packet.getPosX(), 0.0f);
        Assert.assertEquals(location.getPosY(), packet.getPosY(), 0.0f);
        Assert.assertEquals(target.isSprinting(), packet.isSprinting());
        Assert.assertEquals(target.isMovable(), packet.isMovable());
    }

    @Test
    public void removePackagingTest() {
        final IUser target = Mockito.mock(IUser.class);

        Mockito.when(target.getUserId()).thenReturn(randomUniqueId());

        final PacketAvatarMove packet = this.getPacket(SendAction.AVATAR_REMOVE, PacketAvatarMove.class, target);

        Assert.assertEquals(AvatarAction.REMOVE_AVATAR, packet.getAction());
        Assert.assertNotNull(packet.getUserId());
        Assert.assertEquals(target.getUserId(), packet.getUserId());
    }

    @Test
    public void handleUnexpectedPacketTest() {
        final PacketAvatarMove packet = new PacketAvatarMove(randomEnum(Direction.class), randomFloat(), randomFloat(), randomBoolean());

        this.handler.reset();
        this.connection.handle(packet);

        Assert.assertTrue(this.handler.logged());
        Assert.assertTrue(this.handler.logged(Level.WARNING, "Can not move while not logged in"));

        final MockIUser user = this.login();

        this.handler.reset();
        this.connection.handle(packet);

        Assert.assertTrue(this.handler.logged());
        Assert.assertTrue(this.handler.logged(Level.WARNING, "Can not move while not in a world"));
        Assert.assertFalse(user.called("move"));
    }

    @Test
    public void handleInvalidPacketTest() {
        final MockIUser user = this.login();
        final PacketAvatarMove packet = Mockito.mock(PacketAvatarMove.class);

        user.setWorld(Mockito.mock(IWorld.class));
        Mockito.when(packet.getAction()).thenReturn(randomEnum(AvatarAction.class, AvatarAction.MOVE_AVATAR));

        this.handler.reset();
        this.connection.handle(packet);

        Assert.assertTrue(this.handler.logged());
        Assert.assertTrue(this.handler.logged(Level.WARNING, "Only Avatar-Action MOVE_AVATAR is allowed"));
        Assert.assertFalse(user.called("move"));
        Mockito.when(packet.getAction()).thenReturn(AvatarAction.MOVE_AVATAR);
        Mockito.when(packet.getDirection()).thenReturn(null);

        this.handler.reset();
        this.connection.handle(packet);

        Assert.assertTrue(this.handler.logged());
        Assert.assertTrue(this.handler.logged(Level.WARNING, "Missing Direction for avatar move"));
        Assert.assertFalse(user.called("move"));
        Mockito.when(packet.getUserId()).thenReturn(randomUniqueId());

        this.handler.reset();
        this.connection.handle(packet);

        Assert.assertTrue(this.handler.logged());
        Assert.assertTrue(this.handler.logged(Level.WARNING, "User-ID must be the own or null"));
        Assert.assertFalse(user.called("move"));
    }

    @Test
    public void handleCorrectMoveTest() {
        final PacketAvatarMove packet = new PacketAvatarMove(randomEnum(Direction.class), randomFloat(), randomFloat(), randomBoolean());
        final MockIUser user = this.login();

        user.setWorld(Mockito.mock(IWorld.class));

        this.handler.reset();
        this.connection.handle(packet);

        Assert.assertFalse(this.handler.logged());
        Assert.assertTrue(user.called("move"));
    }

    @Test
    public void handleIllegalMoveTest() {
        final PacketAvatarMove packet = new PacketAvatarMove(randomEnum(Direction.class), -randomFloat(), -randomFloat(), randomBoolean());
        final MockIUser user = this.login();

        user.setWorld(Mockito.mock(IWorld.class));
        user.setLocation(Mockito.mock(ILocation.class));

        this.handler.reset();
        this.connection.handle(packet);

        Assert.assertTrue(this.handler.logged());
        Assert.assertTrue(this.handler.logged(Level.WARNING, "illegal movement"));
        Assert.assertTrue(user.called("move"));

        user.reset();
        user.setLocation(null);

        this.handler.reset();
        this.connection.handle(packet);

        Assert.assertTrue(this.handler.logged());
        Assert.assertTrue(this.handler.logged(Level.WARNING, "no valid position"));
        Assert.assertTrue(user.called("move"));
    }
}
