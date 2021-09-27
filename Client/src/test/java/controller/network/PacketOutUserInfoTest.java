package controller.network;

import controller.network.protocol.PacketOutUserInfo;
import controller.network.protocol.PacketOutUserInfo.Action;
import controller.network.protocol.PacketOutUserInfo.UserInfo;
import controller.network.protocol.PacketOutUserInfo.UserInfo.Flag;
import model.user.Avatar;
import model.user.Status;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import java.util.Set;
import java.util.logging.Level;

public class PacketOutUserInfoTest extends PacketClientTest {

    public PacketOutUserInfoTest() {

    }

    @Test
    public void handleUnexpectedPacketTest() {
        final PacketOutUserInfo packet = Mockito.mock(PacketOutUserInfo.class);

        this.handler.reset();
        this.connection.handle(packet);

        Assert.assertTrue(this.handler.logged());
        Assert.assertTrue(this.handler.logged(Level.WARNING, "Can not receive user info while user is not logged in"));
        Mockito.when(packet.getAction()).thenReturn(Action.UPDATE_USER);
        Mockito.when(packet.getContextId()).thenReturn(randomContextId());

        this.login();
        this.joinWorld();
        this.handler.reset();
        this.connection.handle(packet);

        Assert.assertTrue(this.handler.logged());
        Assert.assertTrue(this.handler.logged(Level.WARNING, "Can not receive user info of a world that has not been entered"));
    }

    @Test
    public void handleInvalidPacketTest() {
        final PacketOutUserInfo packet = Mockito.mock(PacketOutUserInfo.class);
        final UserInfo info = Mockito.mock(UserInfo.class);

        Mockito.when(info.getUserId()).thenReturn(randomUniqueId());
        Mockito.when(packet.getAction()).thenReturn(Action.REMOVE_USER);
        Mockito.when(packet.getInfo()).thenReturn(info);

        this.login();
        this.manager.getExternUserController(true);
        this.handler.reset();
        this.connection.handle(packet);

        Assert.assertTrue(this.handler.logged());
        Assert.assertTrue(this.handler.logged(Level.WARNING, "Server tried to send user info of unknown user"));
        Assert.assertTrue(this.manager.called("remove-extern-user"));
        Mockito.when(packet.getAction()).thenReturn(Action.UPDATE_USER);

        this.joinWorld();

        Mockito.when(packet.getContextId()).thenReturn(this.intern.getWorld());
        Mockito.when(info.getWorld()).thenReturn(this.intern.getWorld());
        Mockito.when(info.getFlags()).thenReturn(Set.of(Flag.BANNED));

        this.handler.reset();
        this.connection.handle(packet);

        Assert.assertTrue(this.handler.logged());
        Assert.assertTrue(this.handler.logged(Level.WARNING, "User can not be in a world where he is banned"));
        Mockito.when(info.getUserId()).thenReturn(randomUniqueId());
        Mockito.when(info.getWorld()).thenReturn(null);

        this.manager.getExternUserController(true);
        this.handler.reset();
        this.connection.handle(packet);

        Assert.assertTrue(this.handler.logged());
        Assert.assertTrue(this.handler.logged(Level.WARNING, "Name of unknown user can not be null"));
        Mockito.when(info.getUserId()).thenReturn(this.intern.getUserId());
        Mockito.when(info.getStatus()).thenReturn(randomEnum(Status.class));
        Mockito.when(info.getAvatar()).thenReturn(randomEnum(Avatar.class));

        this.intern.setContext(true);
        this.handler.reset();
        this.connection.handle(packet);

        Assert.assertTrue(this.handler.logged());
        Assert.assertTrue(this.handler.logged(Level.WARNING, "Server tried to send user info for unknown context"));
    }

    @Test
    public void handleCorrectPacketTest() {
        final PacketOutUserInfo packet = Mockito.mock(PacketOutUserInfo.class);
        final UserInfo info = Mockito.mock(UserInfo.class);

        Mockito.when(info.getUserId()).thenReturn(randomUniqueId());
        Mockito.when(info.getName()).thenReturn(randomString());
        Mockito.when(info.getStatus()).thenReturn(randomEnum(Status.class));
        Mockito.when(info.getAvatar()).thenReturn(randomEnum(Avatar.class));
        Mockito.when(packet.getInfo()).thenReturn(info);
        Mockito.when(packet.getAction()).thenReturn(Action.UPDATE_USER);

        this.login();
        this.manager.getExternUserController(true);
        this.handler.reset();
        this.connection.handle(packet);

        Assert.assertFalse(this.handler.logged());
        Assert.assertTrue(this.manager.called("add-extern-user"));
        Assert.assertTrue(this.manager.called("get-extern-user-controller"));
        Mockito.when(info.getUserId()).thenReturn(this.intern.getUserId());
        Mockito.when(info.getWorld()).thenReturn(randomContextId());
        Mockito.when(info.getRoom()).thenReturn(randomContextId());

        this.joinWorld();

        Mockito.when(packet.getContextId()).thenReturn(this.intern.getWorld());

        this.handler.reset();
        this.connection.handle(packet);

        Assert.assertFalse(this.handler.logged());
        Assert.assertTrue(this.intern.called("set-status"));
        Assert.assertTrue(this.intern.called("set-avatar"));
        Assert.assertTrue(this.intern.called("join-world"));
        Assert.assertTrue(this.intern.called("join-room"));
        Assert.assertTrue(this.intern.called("set-report"));
    }
}
