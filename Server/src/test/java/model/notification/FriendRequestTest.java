package model.notification;

import model.exception.IllegalNotificationActionException;
import model.exception.NotificationNotFoundException;
import model.user.User;
import model.user.account.UserAccountManager;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class FriendRequestTest {
    FriendRequest test_fr;
    User sender;
    User receiver;
    UserAccountManager userAccountManager;
    private static final String dbURL = "jdbc:derby:ChatiDB;create=true";

    @Before
    public void setUp() {
        try {
            this.userAccountManager = UserAccountManager.getInstance();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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
        this.userAccountManager.load();



    }

    @Test
    public void normalAcceptTest() {
        try {
            this.userAccountManager.registerUser("frn_sender", "11111");
            sender = UserAccountManager.getInstance().getUser("frn_sender");
            sender.getGlobalNotifications().keySet().forEach(uuid -> {
                try {
                    sender.manageNotification(uuid, NotificationAction.DELETE);
                } catch (NotificationNotFoundException | IllegalNotificationActionException e) {
                    Assert.fail("Failed in delete initialized notifications");
                }
            });
            this.userAccountManager.registerUser("frn_receiver", "22222");
            receiver = UserAccountManager.getInstance().getUser("frn_receiver");
            receiver.getGlobalNotifications().keySet().forEach(uuid -> {
                try {
                    sender.manageNotification(uuid, NotificationAction.DELETE);
                } catch (NotificationNotFoundException | IllegalNotificationActionException e) {
                    Assert.fail("Failed in delete initialized notifications");
                }
            });
            test_fr = new FriendRequest(receiver, "hallo", sender);
            if (!receiver.getGlobalNotifications().values().contains(test_fr)) {
                receiver.addNotification(test_fr);
            }
            Assert.assertEquals(1, this.receiver.getGlobalNotifications().size());
            Assert.assertEquals(0, this.sender.getGlobalNotifications().size());

            this.test_fr.accept();

            Assert.assertTrue(this.receiver.getFriends().values().contains(this.sender));
            Assert.assertTrue(this.sender.getFriends().values().contains(this.receiver));
            Assert.assertEquals(1, this.sender.getGlobalNotifications().size());

        } catch (Exception e) {
            e.printStackTrace();

        }

    }

    // Falls die zwei Benutzer schon Freunde sind
    @Test
    public void uselessAcceptTest() {
        try {
            this.userAccountManager.registerUser("fru_sender", "11111");
            sender = UserAccountManager.getInstance().getUser("fru_sender");
            sender.getGlobalNotifications().keySet().forEach(uuid -> {
                try {
                    sender.manageNotification(uuid, NotificationAction.DELETE);
                } catch (NotificationNotFoundException | IllegalNotificationActionException e) {
                    Assert.fail("Failed in delete initialized notifications");
                }
            });
            this.userAccountManager.registerUser("fru_receiver", "22222");
            receiver = UserAccountManager.getInstance().getUser("fru_receiver");
            receiver.getGlobalNotifications().keySet().forEach(uuid -> {
                try {
                    sender.manageNotification(uuid, NotificationAction.DELETE);
                } catch (NotificationNotFoundException | IllegalNotificationActionException e) {
                    Assert.fail("Failed in delete initialized notifications");
                }
            });
            test_fr = new FriendRequest(receiver, "hallo", sender);
            if (!receiver.getGlobalNotifications().values().contains(test_fr)) {
                receiver.addNotification(test_fr);
            }
            Assert.assertEquals(1, this.receiver.getGlobalNotifications().size());
            Assert.assertEquals(0, this.sender.getGlobalNotifications().size());

            this.receiver.addFriend(this.sender);
            this.sender.addFriend(this.receiver);
            this.test_fr.accept();

            Assert.assertEquals(0, this.sender.getGlobalNotifications().size());

        } catch (Exception e) {
            e.printStackTrace();

        }

    }

    // If sender then ignores receiver
    @Test
    public void ignoredAcceptTest() {
        try {
            this.userAccountManager.registerUser("fri_sender", "11111");
            sender = UserAccountManager.getInstance().getUser("fri_sender");
            sender.getGlobalNotifications().keySet().forEach(uuid -> {
                try {
                    sender.manageNotification(uuid, NotificationAction.DELETE);
                } catch (NotificationNotFoundException | IllegalNotificationActionException e) {
                    Assert.fail("Failed in delete initialized notifications");
                }
            });
            this.userAccountManager.registerUser("fri_receiver", "22222");
            receiver = UserAccountManager.getInstance().getUser("fri_receiver");
            receiver.getGlobalNotifications().keySet().forEach(uuid -> {
                try {
                    sender.manageNotification(uuid, NotificationAction.DELETE);
                } catch (NotificationNotFoundException | IllegalNotificationActionException e) {
                    Assert.fail("Failed in delete initialized notifications");
                }
            });
            test_fr = new FriendRequest(receiver, "hallo", sender);
            if (!receiver.getGlobalNotifications().values().contains(test_fr)) {
                receiver.addNotification(test_fr);
            }
            Assert.assertEquals(1, this.receiver.getGlobalNotifications().size());
            Assert.assertEquals(0, this.sender.getGlobalNotifications().size());

            this.sender.ignoreUser(this.receiver);
            this.test_fr.accept();

            Assert.assertFalse(this.receiver.getFriends().values().contains(this.sender));
            Assert.assertFalse(this.sender.getFriends().values().contains(this.receiver));

        } catch (Exception e) {
            e.printStackTrace();

        }

    }

    // If sender doesn't exist
    @Test
    public void notExistSenderAcceptTest() {
        try {
            this.userAccountManager.registerUser("frne_sender", "11111");
            sender = UserAccountManager.getInstance().getUser("frne_sender");
            sender.getGlobalNotifications().keySet().forEach(uuid -> {
                try {
                    sender.manageNotification(uuid, NotificationAction.DELETE);
                } catch (NotificationNotFoundException | IllegalNotificationActionException e) {
                    Assert.fail("Failed in delete initialized notifications");
                }
            });
            this.userAccountManager.registerUser("frne_receiver", "22222");
            receiver = UserAccountManager.getInstance().getUser("frne_receiver");
            receiver.getGlobalNotifications().keySet().forEach(uuid -> {
                try {
                    sender.manageNotification(uuid, NotificationAction.DELETE);
                } catch (NotificationNotFoundException | IllegalNotificationActionException e) {
                    Assert.fail("Failed in delete initialized notifications");
                }
            });
            test_fr = new FriendRequest(receiver, "hallo", sender);
            if (!receiver.getGlobalNotifications().values().contains(test_fr)) {
                receiver.addNotification(test_fr);
            }
            Assert.assertEquals(1, this.receiver.getGlobalNotifications().size());
            Assert.assertEquals(0, this.sender.getGlobalNotifications().size());

            UserAccountManager.getInstance().deleteUser(this.sender);
            this.test_fr.accept();

            Assert.assertFalse(this.receiver.getFriends().values().contains(this.sender));
            Assert.assertFalse(this.sender.getFriends().values().contains(this.receiver));

        } catch (Exception e) {
            e.printStackTrace();

        }

    }

    @Test
    public void normalDeclineTest() {
        try {
            this.userAccountManager.registerUser("frnd_sender", "11111");
            sender = UserAccountManager.getInstance().getUser("frnd_sender");
            sender.getGlobalNotifications().keySet().forEach(uuid -> {
                try {
                    sender.manageNotification(uuid, NotificationAction.DELETE);
                } catch (NotificationNotFoundException | IllegalNotificationActionException e) {
                    Assert.fail("Failed in delete initialized notifications");
                }
            });
            this.userAccountManager.registerUser("frnd_receiver", "22222");
            receiver = UserAccountManager.getInstance().getUser("frnd_receiver");
            receiver.getGlobalNotifications().keySet().forEach(uuid -> {
                try {
                    sender.manageNotification(uuid, NotificationAction.DELETE);
                } catch (NotificationNotFoundException | IllegalNotificationActionException e) {
                    Assert.fail("Failed in delete initialized notifications");
                }
            });
            test_fr = new FriendRequest(receiver, "hallo", sender);
            if (!receiver.getGlobalNotifications().values().contains(test_fr)) {
                receiver.addNotification(test_fr);
            }
            Assert.assertEquals(1, this.receiver.getGlobalNotifications().size());
            Assert.assertEquals(0, this.sender.getGlobalNotifications().size());

            this.test_fr.decline();

            Assert.assertFalse(this.receiver.getFriends().values().contains(this.sender));
            Assert.assertFalse(this.sender.getFriends().values().contains(this.receiver));
            Assert.assertEquals(1, this.sender.getGlobalNotifications().size());

        } catch (Exception e) {
            e.printStackTrace();

        }

    }

    // Falls die zwei Benutzer schon Freunde sind
    @Test
    public void uselessDeclineTest() {
        try {
            this.userAccountManager.registerUser("frud_sender", "11111");
            sender = UserAccountManager.getInstance().getUser("frud_sender");
            sender.getGlobalNotifications().keySet().forEach(uuid -> {
                try {
                    sender.manageNotification(uuid, NotificationAction.DELETE);
                } catch (NotificationNotFoundException | IllegalNotificationActionException e) {
                    Assert.fail("Failed in delete initialized notifications");
                }
            });
            this.userAccountManager.registerUser("frud_receiver", "22222");
            receiver = UserAccountManager.getInstance().getUser("frud_receiver");
            receiver.getGlobalNotifications().keySet().forEach(uuid -> {
                try {
                    sender.manageNotification(uuid, NotificationAction.DELETE);
                } catch (NotificationNotFoundException | IllegalNotificationActionException e) {
                    Assert.fail("Failed in delete initialized notifications");
                }
            });
            test_fr = new FriendRequest(receiver, "hallo", sender);
            if (!receiver.getGlobalNotifications().values().contains(test_fr)) {
                receiver.addNotification(test_fr);
            }
            Assert.assertEquals(1, this.receiver.getGlobalNotifications().size());
            Assert.assertEquals(0, this.sender.getGlobalNotifications().size());

            this.receiver.addFriend(this.sender);
            this.sender.addFriend(this.receiver);
            this.test_fr.decline();

            Assert.assertEquals(0, this.sender.getGlobalNotifications().size());
            Assert.assertTrue(this.receiver.getFriends().values().contains(this.sender));
            Assert.assertTrue(this.sender.getFriends().values().contains(this.receiver));

        } catch (Exception e) {
            e.printStackTrace();

        }

    }

    // If sender doesn't exist
    @Test
    public void notExistSenderDeclineTest() {
        try {
            this.userAccountManager.registerUser("frned_sender", "11111");
            sender = UserAccountManager.getInstance().getUser("frned_sender");
            sender.getGlobalNotifications().keySet().forEach(uuid -> {
                try {
                    sender.manageNotification(uuid, NotificationAction.DELETE);
                } catch (NotificationNotFoundException | IllegalNotificationActionException e) {
                    Assert.fail("Failed in delete initialized notifications");
                }
            });
            this.userAccountManager.registerUser("frned_receiver", "22222");
            receiver = UserAccountManager.getInstance().getUser("frned_receiver");
            receiver.getGlobalNotifications().keySet().forEach(uuid -> {
                try {
                    sender.manageNotification(uuid, NotificationAction.DELETE);
                } catch (NotificationNotFoundException | IllegalNotificationActionException e) {
                    Assert.fail("Failed in delete initialized notifications");
                }
            });
            test_fr = new FriendRequest(receiver, "hallo", sender);
            if (!receiver.getGlobalNotifications().values().contains(test_fr)) {
                receiver.addNotification(test_fr);
            }
            Assert.assertEquals(1, this.receiver.getGlobalNotifications().size());
            Assert.assertEquals(0, this.sender.getGlobalNotifications().size());

            UserAccountManager.getInstance().deleteUser(this.sender);
            this.test_fr.decline();

            Assert.assertFalse(this.receiver.getFriends().values().contains(this.sender));
            Assert.assertFalse(this.sender.getFriends().values().contains(this.receiver));
            Assert.assertEquals(0, this.sender.getGlobalNotifications().size());

        } catch (Exception e) {

        }

    }

}
