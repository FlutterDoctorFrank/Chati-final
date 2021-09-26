package controller.network;

import controller.network.ServerSender.SendAction;
import controller.network.protocol.PacketProfileAction;
import controller.network.protocol.PacketProfileAction.Action;
import model.user.Avatar;
import model.user.Status;
import org.junit.Assert;
import org.junit.Test;

public class PacketProfileActionTest extends PacketClientTest {

    public PacketProfileActionTest() {

    }

    @Test(expected = IllegalArgumentException.class)
    public void illegalLoginArgsPackagingTest() {
        this.getPacket(SendAction.PROFILE_LOGIN, PacketProfileAction.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void illegalLoginTypesPackagingTest() {
        this.getPacket(SendAction.PROFILE_LOGIN, PacketProfileAction.class, new Object(), new Object(), new Object());
    }

    @Test
    public void registerPackagingTest() {
        final String name = randomString();
        final String password = randomString();

        final PacketProfileAction packet = this.getPacket(SendAction.PROFILE_LOGIN, PacketProfileAction.class, name, password, true);

        Assert.assertEquals(Action.REGISTER, packet.getAction());
        Assert.assertNotNull(packet.getName());
        Assert.assertEquals(name, packet.getName());
        Assert.assertNotNull(packet.getPassword());
        Assert.assertEquals(password, packet.getPassword());
    }

    @Test
    public void loginPackagingTest() {
        final String name = randomString();
        final String password = randomString();

        final PacketProfileAction packet = this.getPacket(SendAction.PROFILE_LOGIN, PacketProfileAction.class, name, password, false);

        Assert.assertEquals(Action.LOGIN, packet.getAction());
        Assert.assertNotNull(packet.getName());
        Assert.assertEquals(name, packet.getName());
        Assert.assertNotNull(packet.getPassword());
        Assert.assertEquals(password, packet.getPassword());
    }

    @Test(expected = IllegalArgumentException.class)
    public void illegalChangePackagingTest() {
        this.getPacket(SendAction.PROFILE_CHANGE, PacketProfileAction.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void illegalChangePasswordPackagingTest() {
        this.getPacket(SendAction.PROFILE_CHANGE, PacketProfileAction.class, new Object(), new Object());
    }

    @Test(expected = IllegalArgumentException.class)
    public void illegalChangeProfilePackagingTest() {
        this.getPacket(SendAction.PROFILE_CHANGE, PacketProfileAction.class, new Object());
    }

    @Test
    public void changePasswordPackagingTest() {
        final String oldPassword = randomString();
        final String newPassword = randomString();

        final PacketProfileAction packet = this.getPacket(SendAction.PROFILE_CHANGE, PacketProfileAction.class, oldPassword, newPassword);

        Assert.assertEquals(Action.CHANGE_PASSWORD, packet.getAction());
        Assert.assertNotNull(packet.getPassword());
        Assert.assertEquals(oldPassword, packet.getPassword());
        Assert.assertNotNull(packet.getNewPassword());
        Assert.assertEquals(newPassword, packet.getNewPassword());
    }

    @Test
    public void changeAvatarPackagingTest() {
        final Avatar avatar = randomEnum(Avatar.class);

        final PacketProfileAction packet = this.getPacket(SendAction.PROFILE_CHANGE, PacketProfileAction.class, avatar);

        Assert.assertEquals(Action.CHANGE_AVATAR, packet.getAction());
        Assert.assertNotNull(packet.getAvatar());
        Assert.assertEquals(avatar, packet.getAvatar());
    }

    @Test
    public void changeStatusPackagingTest() {
        final Status status = randomEnum(Status.class);

        final PacketProfileAction packet = this.getPacket(SendAction.PROFILE_CHANGE, PacketProfileAction.class, status);

        Assert.assertEquals(Action.CHANGE_STATUS, packet.getAction());
        Assert.assertNotNull(packet.getStatus());
        Assert.assertEquals(status, packet.getStatus());
    }

    @Test(expected = IllegalArgumentException.class)
    public void illegalLogoutArgsPackagingTest() {
        this.getPacket(SendAction.PROFILE_LOGOUT, PacketProfileAction.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void illegalLogoutTypesPackagingTest() {
        this.getPacket(SendAction.PROFILE_LOGOUT, PacketProfileAction.class, new Object(), new Object());
    }

    @Test
    public void deletePackagingTest() {
        final String password = randomString();

        final PacketProfileAction packet = this.getPacket(SendAction.PROFILE_LOGOUT, PacketProfileAction.class, password, true);

        Assert.assertEquals(Action.DELETE, packet.getAction());
        Assert.assertNotNull(packet.getPassword());
        Assert.assertEquals(password, packet.getPassword());
    }

    @Test
    public void logoutPackagingTest() {
        final PacketProfileAction packet = this.getPacket(SendAction.PROFILE_LOGOUT, PacketProfileAction.class, "", false);

        Assert.assertEquals(Action.LOGOUT, packet.getAction());
        Assert.assertNotNull(packet.getPassword());
        Assert.assertTrue(packet.getPassword().isEmpty());
    }
}
