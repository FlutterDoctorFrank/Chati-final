package controller.network;

import controller.network.mock.MockIUser;
import controller.network.protocol.PacketProfileAction;
import controller.network.protocol.PacketProfileAction.Action;
import model.context.spatial.IWorld;
import model.exception.IllegalAccountActionException;
import model.exception.UserNotFoundException;
import model.notification.INotification;
import model.notification.NotificationType;
import model.user.Avatar;
import model.user.Status;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.UUID;
import java.util.logging.Level;

public class PacketProfileActionTest extends PacketServerTest {

    @Test
    public void handleUnexpectedPacketTest() {
        final PacketProfileAction packet = Mockito.mock(PacketProfileAction.class);

        Mockito.when(packet.getAction()).thenReturn(Action.LOGOUT);

        this.handler.reset();
        this.connection.handle(packet);

        Assert.assertTrue(this.handler.logged());
        Assert.assertTrue(this.handler.logged(Level.WARNING, "Can not change/logout/delete user while not logged in"));
        Mockito.when(packet.getAction()).thenReturn(Action.LOGIN);

        this.login();
        this.handler.reset();
        this.connection.handle(packet);

        Assert.assertTrue(this.handler.logged());
        Assert.assertTrue(this.handler.logged(Level.WARNING, "Can not login/register user while already logged in"));
    }

    @Test
    public void handleInvalidPacketTest() {
        final PacketProfileAction packet = Mockito.mock(PacketProfileAction.class);

        Mockito.when(packet.getAction()).thenReturn(randomBoolean() ? Action.LOGIN : Action.REGISTER);
        Mockito.when(packet.getPassword()).thenReturn(randomString());

        this.handler.reset();
        this.connection.handle(packet);

        Assert.assertTrue(this.handler.logged());
        Assert.assertTrue(this.handler.logged(Level.WARNING, "Missing username or password for user login/register"));
        Mockito.when(packet.getAction()).thenReturn(Action.CHANGE_PASSWORD);

        this.login();
        this.handler.reset();
        this.connection.handle(packet);

        Assert.assertTrue(this.handler.logged());
        Assert.assertTrue(this.handler.logged(Level.WARNING, "Missing passwords for change-password action"));
        Mockito.when(packet.getAction()).thenReturn(Action.CHANGE_AVATAR);

        this.handler.reset();
        this.connection.handle(packet);

        Assert.assertTrue(this.handler.logged());
        Assert.assertTrue(this.handler.logged(Level.WARNING, "Missing avatar for change-avatar action"));
        Mockito.when(packet.getAction()).thenReturn(Action.CHANGE_STATUS);

        this.handler.reset();
        this.connection.handle(packet);

        Assert.assertTrue(this.handler.logged());
        Assert.assertTrue(this.handler.logged(Level.WARNING, "Missing status for change-status action"));
        Mockito.when(packet.getAction()).thenReturn(Action.DELETE);
        Mockito.when(packet.getPassword()).thenReturn(null);

        this.handler.reset();
        this.connection.handle(packet);

        Assert.assertTrue(this.handler.logged());
        Assert.assertTrue(this.handler.logged(Level.WARNING, "Missing password for delete action"));
    }

    @Test(expected = IllegalStateException.class)
    public void handleUnknownUserTest() {
        final PacketProfileAction packet = Mockito.mock(PacketProfileAction.class);

        Mockito.when(packet.getAction()).thenReturn(Action.DELETE);
        Mockito.when(packet.getUserId()).thenReturn(randomUniqueId());
        Mockito.when(packet.getPassword()).thenReturn(randomString());

        try {
            Mockito.doThrow(new UserNotFoundException("Unhandled mocked exception", randomUniqueId()))
                    .when(this.manager).deleteUser(Mockito.any(UUID.class), Mockito.anyString());

            this.login();
            this.handler.reset();
            this.connection.handle(packet);
        } catch (IllegalAccountActionException | UserNotFoundException ex) {
            Assert.fail(ex.getMessage());
        }
    }

    @Test
    public void handleIllegalActionTest() {
        final PacketProfileAction packet = Mockito.mock(PacketProfileAction.class);

        Mockito.when(packet.getAction()).thenReturn(Action.LOGIN);
        Mockito.when(packet.getName()).thenReturn(randomString());
        Mockito.when(packet.getPassword()).thenReturn(randomString());

        try {
            Mockito.doThrow(new IllegalAccountActionException("Unhandled mocked exception", "")).when(this.manager)
                    .loginUser(Mockito.anyString(), Mockito.anyString(), Mockito.any(ClientSender.class));

            this.handler.reset();
            this.connection.handle(packet);
        } catch (IllegalAccountActionException ex) {
            Assert.fail(ex.getMessage());
        }

        this.login();

        try {
            Mockito.when(packet.getAction()).thenReturn(Action.DELETE);
            Mockito.doThrow(new IllegalAccountActionException("Unhandled mocked exception", "")).when(this.manager)
                    .deleteUser(Mockito.any(UUID.class), Mockito.anyString());

            this.handler.reset();
            this.connection.handle(packet);
        } catch (IllegalAccountActionException | UserNotFoundException ex) {
            Assert.fail(ex.getMessage());
        }
    }

    @Test
    public void handleCorrectActionTest() {
        final PacketProfileAction packet = Mockito.mock(PacketProfileAction.class);

        Mockito.when(packet.getAction()).thenReturn(Action.REGISTER);
        Mockito.when(packet.getName()).thenReturn(randomString());
        Mockito.when(packet.getPassword()).thenReturn(randomString());

        this.handler.reset();
        this.connection.handle(packet);

        final MockIUser user = new MockIUser(this.global);

        try {
            final IWorld world = Mockito.mock(IWorld.class);
            final INotification notification = Mockito.mock(INotification.class);

            Mockito.when(world.getContextId()).thenReturn(randomContextId());
            Mockito.when(world.getContextName()).thenReturn(randomString());
            Mockito.when(notification.getNotificationId()).thenReturn(randomUniqueId());
            Mockito.when(notification.getContext()).thenReturn(world);
            Mockito.when(notification.getMessageBundle()).thenReturn(randomBundle());
            Mockito.when(notification.getTimestamp()).thenReturn(LocalDateTime.now());
            Mockito.when(notification.getNotificationType()).thenReturn(randomEnum(NotificationType.class));

            user.setFriends(Collections.singletonMap(user.getUserId(), user));
            user.setGlobalNotifications(Collections.singletonMap(notification.getNotificationId(), notification));

            Mockito.when(packet.getAction()).thenReturn(Action.LOGIN);
            Mockito.when(this.manager.loginUser(Mockito.anyString(), Mockito.anyString(), Mockito.eq(this.connection))).thenReturn(user);
            Mockito.when(this.global.getIWorlds()).thenReturn(Collections.singletonMap(randomContextId(), world));
            Mockito.when(this.global.getContextId()).thenReturn(randomContextId());

            this.handler.reset();
            this.connection.handle(packet);
        } catch (IllegalAccountActionException ex) {
            Assert.fail("Failed to login mocked user");
        }

        Mockito.when(packet.getAction()).thenReturn(Action.CHANGE_PASSWORD);
        Mockito.when(packet.getNewPassword()).thenReturn(randomString());

        this.handler.reset();
        this.connection.handle(packet);

        Mockito.when(packet.getAction()).thenReturn(Action.CHANGE_AVATAR);
        Mockito.when(packet.getAvatar()).thenReturn(randomEnum(Avatar.class, user.getAvatar()));

        this.handler.reset();
        this.connection.handle(packet);

        Assert.assertTrue(user.called("set-avatar"));
        Assert.assertEquals(packet.getAvatar(), user.getAvatar());
        Mockito.when(packet.getAction()).thenReturn(Action.CHANGE_STATUS);
        Mockito.when(packet.getStatus()).thenReturn(randomEnum(Status.class, user.getStatus()));

        this.handler.reset();
        this.connection.handle(packet);

        Assert.assertTrue(user.called("set-status"));
        Assert.assertEquals(packet.getStatus(), user.getStatus());
        Mockito.when(packet.getAction()).thenReturn(Action.DELETE);

        this.handler.reset();
        this.connection.handle(packet);

        Mockito.when(packet.getAction()).thenReturn(Action.LOGOUT);

        this.login();
        this.handler.reset();
        this.connection.handle(packet);
    }
}
