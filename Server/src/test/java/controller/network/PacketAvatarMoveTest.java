package controller.network;

import controller.network.ClientSender.SendAction;
import controller.network.protocol.PacketAvatarMove;
import controller.network.protocol.PacketAvatarMove.AvatarAction;
import model.context.spatial.Direction;
import model.context.spatial.ILocation;
import model.user.IUser;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

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
}
