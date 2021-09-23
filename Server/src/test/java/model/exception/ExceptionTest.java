package model.exception;

import model.MessageBundle;
import model.communication.CommunicationMedium;
import model.context.Context;
import model.context.ContextID;
import model.context.global.GlobalContext;
import model.notification.Notification;
import model.role.Permission;
import model.user.*;
import model.user.account.UserAccountManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.UUID;

public class ExceptionTest {

    private static final String dbURL = "jdbc:derby:ChatiDB;create=true";

    private void deleteData(String tableName){

        try {
            Connection con = DriverManager.getConnection(dbURL);
            Statement st = con.createStatement();
            st.executeUpdate("DELETE FROM " + tableName);
            st.close();
            con.close();
        } catch (SQLException e) {
            System.out.print("Fehler in deleteData: " + e);
        }
    }

    @After
    public void tearDown() {
        deleteData("USER_ACCOUNT");
        deleteData("WORLDS");
        deleteData("BAN");
        deleteData("IGNORE");
        deleteData("FRIENDSHIP");
        deleteData("USER_RESERVATION");
        deleteData("ROLE_WITH_CONTEXT");
        deleteData("NOTIFICATION");
        GlobalContext.getInstance().load();
        UserAccountManager.getInstance().load();

    }


    @Test
    public void testContextNotFoundException() {
        Throwable throwable = new Throwable();
        Context test_context = new Context("test_context", null) {
            @Override
            public @NotNull Map<UUID, User> getCommunicableUsers(@NotNull User communicatingUser) {
                return null;
            }

            @Override
            public boolean canCommunicateWith(@NotNull CommunicationMedium medium) {
                return false;
            }
        };
        ContextNotFoundException test = new ContextNotFoundException("test", test_context.getContextId(),
                throwable);

        Assert.assertEquals(test_context.getContextId(), test.getContextID());
    }

    @Test(expected = IllegalAccountActionException.class)
    public void testIllegalIllegalAccountActionException() throws Exception{
        Throwable throwable = new Throwable();
        String[] args = new String[]{};
        IllegalAccountActionException test = new IllegalAccountActionException("message", throwable, "key", args);
        throw test;
    }

    @Test(expected = IllegalActionException.class)
    public void testIllegalActionException1() throws Exception{
        IllegalActionException test = new IllegalActionException("message");
        throw test;
    }

    @Test(expected = IllegalActionException.class)
    public void testIllegalActionException2() throws Exception{
        Throwable throwable = new Throwable();
        IllegalActionException test = new IllegalActionException("message", throwable);
        throw test;
    }

    @Test
    public void testIllegalAdministrativeActionException() {
        Throwable throwable = new Throwable();
        try {
            UserAccountManager.getInstance().registerUser("testIllegAA1", "11111");
            User performer = UserAccountManager.getInstance().getUser("testIllegAA1");
            UserAccountManager.getInstance().registerUser("testIllegAA2", "11111");
            User target = UserAccountManager.getInstance().getUser("testIllegAA2");
            IllegalAdministrativeActionException test = new IllegalAdministrativeActionException("message",
                    performer, target, AdministrativeAction.ASSIGN_ADMINISTRATOR, throwable);

            Assert.assertEquals(performer, test.getPerformer());
            Assert.assertEquals(target, test.getTarget());
            Assert.assertEquals(AdministrativeAction.ASSIGN_ADMINISTRATOR, test.getAdministrativeAction());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testIllegalInteractionException1() {
        try {
            UserAccountManager.getInstance().registerUser("testIllegI1", "11111");
            User performer = UserAccountManager.getInstance().getUser("testIllegI1");
            IllegalInteractionException test = new IllegalInteractionException("message", performer);

            Assert.assertEquals(performer, test.getUser());

            Throwable throwable = new Throwable();
            IllegalInteractionException test2 = new IllegalInteractionException("message", performer, throwable);
            Assert.assertEquals(performer, test2.getUser());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test(expected = IllegalNotificationActionException.class)
    public void testIllegalNotificationActionException1() throws Exception{
        try {
            UserAccountManager.getInstance().registerUser("testIllegN1", "11111");
            User performer = UserAccountManager.getInstance().getUser("testIllegN1");

            MessageBundle test_messageBundle = new MessageBundle("test_notif", "hallo");
            Notification notification = new Notification(performer, GlobalContext.getInstance(), test_messageBundle);
            IllegalNotificationActionException test = new IllegalNotificationActionException("message",
                    performer, notification, false);
            Assert.assertFalse(test.isAccepted());
            Assert.assertEquals(performer, test.getUser());
            Assert.assertEquals(notification, test.getNotification());
            throw test;
        } catch (IllegalAccountActionException e) {
            e.printStackTrace();
        }
    }

    @Test(expected = IllegalNotificationActionException.class)
    public void testIllegalNotificationActionException2() throws Exception{
        try {
            Throwable throwable = new Throwable();
            UserAccountManager.getInstance().registerUser("testIllegN2", "11111");
            User performer = UserAccountManager.getInstance().getUser("testIllegN2");

            MessageBundle test_messageBundle = new MessageBundle("test_notif", "hallo");
            Notification notification = new Notification(performer, GlobalContext.getInstance(), test_messageBundle);
            IllegalNotificationActionException test = new IllegalNotificationActionException("message",
                    performer, notification, false, throwable);
            Assert.assertFalse(test.isAccepted());
            throw test;
        } catch (IllegalAccountActionException e) {
            e.printStackTrace();
        }
    }

    @Test(expected = IllegalPositionException.class)
    public void testIllegalPositionException1() throws Exception{
        try {
            UserAccountManager.getInstance().registerUser("testIllegP1", "11111");
            User performer = UserAccountManager.getInstance().getUser("testIllegP1");

            IllegalPositionException test = new IllegalPositionException("message", performer, 1, 2);
            Assert.assertEquals(performer, test.getUser());
            Assert.assertTrue(test.getPosX() == 1f);
            Assert.assertTrue(test.getPosY() == 2f);
            throw test;
        } catch (IllegalAccountActionException e) {
            e.printStackTrace();
        }
    }

    @Test(expected = IllegalPositionException.class)
    public void testIllegalPositionException2() throws Exception{
        try {
            Throwable throwable = new Throwable();
            UserAccountManager.getInstance().registerUser("testIllegP2", "11111");
            User performer = UserAccountManager.getInstance().getUser("testIllegP2");

            IllegalPositionException test = new IllegalPositionException("message", performer, 1, 2, throwable);
            Assert.assertEquals(performer, test.getUser());
            Assert.assertTrue(test.getPosX() == 1f);
            Assert.assertTrue(test.getPosY() == 2f);
            throw test;
        } catch (IllegalAccountActionException e) {
            e.printStackTrace();
        }
    }

    @Test(expected = IllegalWorldActionException.class)
    public void testIllegalWorldActionException() throws Exception{
        IllegalWorldActionException test = new IllegalWorldActionException("message", "key");
        throw test;
    }

    @Test(expected = NoPermissionException.class)
    public void testNoPermissionException() throws Exception{
        try {
            UserAccountManager.getInstance().registerUser("testNP1", "11111");
            User performer = UserAccountManager.getInstance().getUser("testNP1");

            NoPermissionException test = new NoPermissionException("message", "key", performer,
                    Permission.BAN_MODERATOR);
            Assert.assertEquals(performer, test.getUser());
            Assert.assertEquals(Permission.BAN_MODERATOR, test.getPermission());
            throw test;
        } catch (IllegalAccountActionException e) {
            e.printStackTrace();
        }
    }

    @Test(expected = NotificationNotFoundException.class)
    public void testNotificationNotFoundException1() throws Exception{
        try {
            UserAccountManager.getInstance().registerUser("testNNT1", "11111");
            User performer = UserAccountManager.getInstance().getUser("testNNT1");

            MessageBundle test_messageBundle = new MessageBundle("test_notif", "hallo");
            Notification notification = new Notification(performer, GlobalContext.getInstance(), test_messageBundle);
            NotificationNotFoundException test = new NotificationNotFoundException("message",
                    performer, notification.getNotificationId());
            Assert.assertEquals(performer, test.getUser());
            Assert.assertEquals(notification.getNotificationId(), test.getNotificationId());
            throw test;
        } catch (IllegalAccountActionException e) {
            e.printStackTrace();
        }
    }

    @Test(expected = NotificationNotFoundException.class)
    public void testNotificationNotFoundException2() throws Exception{
        try {
            Throwable throwable = new Throwable();
            UserAccountManager.getInstance().registerUser("testNNT2", "11111");
            User performer = UserAccountManager.getInstance().getUser("testNNT2");

            MessageBundle test_messageBundle = new MessageBundle("test_notif", "hallo");
            Notification notification = new Notification(performer, GlobalContext.getInstance(), test_messageBundle);
            NotificationNotFoundException test = new NotificationNotFoundException("message",
                    performer, notification.getNotificationId(), throwable);
            Assert.assertEquals(performer, test.getUser());
            Assert.assertEquals(notification.getNotificationId(), test.getNotificationId());
            throw test;
        } catch (IllegalAccountActionException e) {
            e.printStackTrace();
        }
    }

    @Test(expected = UserNotFoundException.class)
    public void testUserNotFoundException1() throws Exception{
        UUID user_id = UUID.randomUUID();
        UserNotFoundException test = new UserNotFoundException("message", user_id);
        Assert.assertEquals(user_id, test.getUserID());
        throw test;
    }

    @Test(expected = UserNotFoundException.class)
    public void testUserNotFoundException2() throws Exception{
        UserNotFoundException test = new UserNotFoundException("message", "name");
        Assert.assertEquals("name", test.getUsername());
        throw test;
    }

    @Test(expected = UserNotFoundException.class)
    public void testUserNotFoundException3() throws Exception{
        UUID user_id = UUID.randomUUID();
        UserNotFoundException test = new UserNotFoundException("message", user_id, "name");
        Assert.assertEquals("name", test.getUsername());
        Assert.assertEquals(user_id, test.getUserID());
        throw test;
    }

    @Test(expected = UserNotFoundException.class)
    public void testUserNotFoundException4() throws Exception{
        Throwable throwable = new Throwable();
        UserNotFoundException test = new UserNotFoundException("message", "name", throwable);
        Assert.assertEquals("name", test.getUsername());
        throw test;
    }

    @Test(expected = UserNotFoundException.class)
    public void testUserNotFoundException5() throws Exception{
        UUID user_id = UUID.randomUUID();
        Throwable throwable = new Throwable();
        UserNotFoundException test = new UserNotFoundException("message", user_id, throwable);
        Assert.assertEquals(user_id, test.getUserID());
        throw test;
    }

    @Test(expected = UserNotFoundException.class)
    public void testUserNotFoundException6() throws Exception{
        UUID user_id = UUID.randomUUID();
        Throwable throwable = new Throwable();
        UserNotFoundException test = new UserNotFoundException("message", user_id, "name", throwable);
        Assert.assertEquals("name", test.getUsername());
        Assert.assertEquals(user_id, test.getUserID());
        throw test;
    }


}
