package controller.network;

import controller.network.ClientSender.SendAction;
import controller.network.protocol.PacketAvatarMove;
import controller.network.protocol.PacketAvatarMove.AvatarAction;
import model.context.spatial.ILocation;
import model.context.spatial.IRoom;
import model.user.IUser;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

public class PacketAvatarMoveTest extends PacketServerTest {

    public PacketAvatarMoveTest() {

    }

    @Test(expected = IllegalArgumentException.class)
    public void illegalUpdatePackagingTest() {
        this.getPacket(SendAction.AVATAR_MOVE, PacketAvatarMove.class, new Object());
    }

    @Test(expected = IllegalArgumentException.class)
    public void illegalRemovePackagingTest() {
        this.getPacket(SendAction.AVATAR_REMOVE, PacketAvatarMove.class, new Object());
    }

    @Test
    public void spawnPackagingTest() {
        final IUser target = Mockito.mock(IUser.class);
        final ILocation location = Mockito.mock(ILocation.class);
        final ILocation self = Mockito.mock(ILocation.class);

        Mockito.when(location.getRoom()).thenReturn(Mockito.mock(IRoom.class));
        Mockito.when(location.getPosX()).thenReturn(randomInt(64));
        Mockito.when(location.getPosY()).thenReturn(randomInt(64));
        Mockito.when(target.getUserId()).thenReturn(randomUniqueId());
        Mockito.when(target.getLocation()).thenReturn(location);
        Mockito.when(self.getRoom()).thenReturn(Mockito.mock(IRoom.class));
        Mockito.when(this.user.getLocation()).thenReturn(self);

        final PacketAvatarMove packet = this.getPacket(SendAction.AVATAR_MOVE, PacketAvatarMove.class, target);

        Assert.assertEquals(AvatarAction.SPAWN_AVATAR, packet.getAction());
        Assert.assertNotNull(packet.getUserId());
        Assert.assertEquals(target.getUserId(), packet.getUserId());
        Assert.assertEquals(location.getPosX(), packet.getPosX());
        Assert.assertEquals(location.getPosY(), packet.getPosY());
    }

    @Test
    public void updatePackagingTest() {
        final IUser target = Mockito.mock(IUser.class);
        final ILocation location = Mockito.mock(ILocation.class);
        final IRoom room = Mockito.mock(IRoom.class);

        Mockito.when(location.getRoom()).thenReturn(room);
        Mockito.when(location.getPosX()).thenReturn(randomInt(64));
        Mockito.when(location.getPosY()).thenReturn(randomInt(64));
        Mockito.when(target.getUserId()).thenReturn(randomUniqueId());
        Mockito.when(target.getLocation()).thenReturn(location);
        Mockito.when(this.user.getLocation()).thenReturn(location);

        final PacketAvatarMove packet = this.getPacket(SendAction.AVATAR_MOVE, PacketAvatarMove.class, target);

        Assert.assertEquals(AvatarAction.UPDATE_AVATAR, packet.getAction());
        Assert.assertNotNull(packet.getUserId());
        Assert.assertEquals(target.getUserId(), packet.getUserId());
        Assert.assertEquals(location.getPosX(), packet.getPosX());
        Assert.assertEquals(location.getPosY(), packet.getPosY());
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
