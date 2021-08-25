package controller.network;

import controller.network.ClientSender.SendAction;
import controller.network.protocol.PacketOutUserInfo;
import controller.network.protocol.PacketOutUserInfo.Action;
import controller.network.protocol.PacketOutUserInfo.UserInfo.Flag;
import model.context.spatial.IWorld;
import model.user.Avatar;
import model.user.IUser;
import model.user.Status;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PacketOutUserInfoTest extends PacketServerTest {

    public PacketOutUserInfoTest() {
        super(SendAction.USER_INFO);
    }

    @Test(expected = IllegalArgumentException.class)
    public void illegalPackagingTest() {
        this.getPacket(PacketOutUserInfo.class, new Object());
    }

    @Test
    public void globalFriendPackagingTest() {
        final IUser target = Mockito.mock(IUser.class);
        final Map<UUID, IUser> friends = new HashMap<>();

        Mockito.when(target.getUserId()).thenReturn(randomUniqueId());
        Mockito.when(target.getUsername()).thenReturn(randomString());
        Mockito.when(target.getStatus()).thenReturn(randomEnum(Status.class));
        friends.put(target.getUserId(), target);
        Mockito.when(this.user.getFriends()).thenReturn(friends);
        Mockito.when(this.user.getWorld()).thenReturn(null);

        final PacketOutUserInfo packet = this.getPacket(PacketOutUserInfo.class, target);

        Assert.assertNull(packet.getContextId());
        Assert.assertEquals(Action.UPDATE_USER, packet.getAction());
        Assert.assertEquals(target.getUserId(), packet.getInfo().getUserId());
        Assert.assertNotNull(packet.getInfo().getName());
        Assert.assertEquals(target.getUsername(), packet.getInfo().getName());
        Assert.assertTrue(packet.getInfo().getFlags().contains(Flag.FRIEND));
    }

    @Test
    public void globalRemovePackagingTest() {
        final IUser target = Mockito.mock(IUser.class);

        Mockito.when(target.getUserId()).thenReturn(randomUniqueId());
        Mockito.when(this.user.getFriends()).thenReturn(Collections.emptyMap());
        Mockito.when(this.user.getWorld()).thenReturn(null);

        final PacketOutUserInfo packet = this.getPacket(PacketOutUserInfo.class, target);

        Assert.assertNull(packet.getContextId());
        Assert.assertEquals(Action.REMOVE_USER, packet.getAction());
        Assert.assertEquals(target.getUserId(), packet.getInfo().getUserId());
        Assert.assertTrue(packet.getInfo().getFlags().isEmpty());
    }

    @Test
    public void worldUpdatePackagingTest() {
        final IUser target = Mockito.mock(IUser.class);
        final IWorld world = Mockito.mock(IWorld.class);
        final Map<UUID, IUser> collection = new HashMap<>();

        Mockito.when(target.getUserId()).thenReturn(randomUniqueId());
        Mockito.when(target.getUsername()).thenReturn(randomString());
        Mockito.when(target.getStatus()).thenReturn(randomEnum(Status.class));
        Mockito.when(target.getAvatar()).thenReturn(randomEnum(Avatar.class));
        Mockito.when(target.getWorld()).thenReturn(world);
        Mockito.when(world.getContextId()).thenReturn(randomContextId());
        Mockito.when(world.getBannedUsers()).thenReturn(Collections.emptyMap());
        Mockito.when(world.getReportedUsers()).thenReturn(collection);
        collection.put(target.getUserId(), target);
        Mockito.when(this.user.getWorld()).thenReturn(world);
        Mockito.when(this.user.getFriends()).thenReturn(collection);
        Mockito.when(this.user.getIgnoredUsers()).thenReturn(collection);

        final PacketOutUserInfo packet = this.getPacket(PacketOutUserInfo.class, target);

        Assert.assertEquals(world.getContextId(), packet.getContextId());
        Assert.assertEquals(Action.UPDATE_USER, packet.getAction());
        Assert.assertEquals(target.getUserId(), packet.getInfo().getUserId());
        Assert.assertNotNull(packet.getInfo().getName());
        Assert.assertEquals(target.getUsername(), packet.getInfo().getName());
        Assert.assertNotNull(packet.getInfo().getStatus());
        Assert.assertEquals(target.getStatus(), packet.getInfo().getStatus());
        Assert.assertNotNull(packet.getInfo().getAvatar());
        Assert.assertEquals(target.getAvatar(), packet.getInfo().getAvatar());
        Assert.assertTrue(packet.getInfo().getFlags().contains(Flag.FRIEND));
        Assert.assertTrue(packet.getInfo().getFlags().contains(Flag.IGNORED));
        Assert.assertTrue(packet.getInfo().getFlags().contains(Flag.REPORTED));
    }

    @Test
    public void worldBannedPackagingTest() {
        final IUser target = Mockito.mock(IUser.class);
        final IWorld world = Mockito.mock(IWorld.class);
        final Map<UUID, IUser> bans = new HashMap<>();

        Mockito.when(target.getUserId()).thenReturn(randomUniqueId());
        Mockito.when(target.getUsername()).thenReturn(randomString());
        Mockito.when(target.getStatus()).thenReturn(randomEnum(Status.class));
        Mockito.when(target.getWorld()).thenReturn(null);
        Mockito.when(world.getContextId()).thenReturn(randomContextId());
        bans.put(target.getUserId(), target);
        Mockito.when(world.getBannedUsers()).thenReturn(bans);
        Mockito.when(this.user.getWorld()).thenReturn(world);
        Mockito.when(this.user.getFriends()).thenReturn(Collections.emptyMap());

        final PacketOutUserInfo packet = this.getPacket(PacketOutUserInfo.class, target);

        Assert.assertEquals(world.getContextId(), packet.getContextId());
        Assert.assertEquals(Action.UPDATE_USER, packet.getAction());
        Assert.assertEquals(target.getUserId(), packet.getInfo().getUserId());
        Assert.assertNotNull(packet.getInfo().getName());
        Assert.assertEquals(target.getUsername(), packet.getInfo().getName());
        Assert.assertNotNull(packet.getInfo().getStatus());
        Assert.assertEquals(Status.OFFLINE, packet.getInfo().getStatus());
        Assert.assertTrue(packet.getInfo().getFlags().contains(Flag.BANNED));
    }

    @Test
    public void worldRemovePackagingTest() {
        final IUser target = Mockito.mock(IUser.class);
        final IWorld world = Mockito.mock(IWorld.class);

        Mockito.when(target.getUserId()).thenReturn(randomUniqueId());
        Mockito.when(target.getUsername()).thenReturn(randomString());
        Mockito.when(target.getStatus()).thenReturn(randomEnum(Status.class));
        Mockito.when(target.getWorld()).thenReturn(null);
        Mockito.when(world.getContextId()).thenReturn(randomContextId());
        Mockito.when(world.getBannedUsers()).thenReturn(Collections.emptyMap());
        Mockito.when(this.user.getWorld()).thenReturn(world);
        Mockito.when(this.user.getFriends()).thenReturn(Collections.emptyMap());

        final PacketOutUserInfo packet = this.getPacket(PacketOutUserInfo.class, target);

        Assert.assertNull(packet.getContextId());
        Assert.assertEquals(Action.REMOVE_USER, packet.getAction());
        Assert.assertEquals(target.getUserId(), packet.getInfo().getUserId());
        Assert.assertNotNull(packet.getInfo().getName());
        Assert.assertEquals(target.getUsername(), packet.getInfo().getName());
        Assert.assertNotNull(packet.getInfo().getStatus());
        Assert.assertEquals(Status.OFFLINE, packet.getInfo().getStatus());
        Assert.assertTrue(packet.getInfo().getFlags().isEmpty());
    }
}
