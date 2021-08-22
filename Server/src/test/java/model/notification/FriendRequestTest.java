package model.notification;

import controller.network.ClientSender;
import model.MessageBundle;
import model.context.global.GlobalContext;
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
    private static final String dbURL = "jdbc:derby:ChatiDB;create=true";

    @Before
    public void setUp() {
        try {
            UserAccountManager.getInstance().registerUser("fr_sender", "11111");
            this.sender = UserAccountManager.getInstance().getUser("fr_sender");
            UserAccountManager.getInstance().registerUser("fr_receiver", "22222");
            this.receiver = UserAccountManager.getInstance().getUser("fr_receiver");
            this.test_fr = new FriendRequest(this.receiver, "hallo", this.sender);
            if (!this.receiver.getGlobalNotifications().values().contains(test_fr)) {
                this.receiver.addNotification(this.test_fr);
            }

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

        try {
            if (UserAccountManager.getInstance().isRegistered("fr_sender")) {
                UserAccountManager.getInstance().deleteUser(this.sender);
            }
            if (UserAccountManager.getInstance().isRegistered("fr_receiver")) {
                UserAccountManager.getInstance().deleteUser(this.receiver);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Test
    public void normalAcceptTest() {
        try {
            Assert.assertEquals(1, this.receiver.getGlobalNotifications().size());
            Assert.assertEquals(0, this.sender.getGlobalNotifications().size());

            this.test_fr.accept();

            Assert.assertTrue(this.receiver.getFriends().values().contains(this.sender));
            Assert.assertTrue(this.sender.getFriends().values().contains(this.receiver));
            Assert.assertEquals(1, this.sender.getGlobalNotifications().size());

        } catch (Exception e) {

        }

    }

    // Falls die zwei Benutzer schon Freunde sind
    @Test
    public void uselessAcceptTest() {
        try {
            Assert.assertEquals(1, this.receiver.getGlobalNotifications().size());
            Assert.assertEquals(0, this.sender.getGlobalNotifications().size());

            this.receiver.addFriend(this.sender);
            this.sender.addFriend(this.receiver);
            this.test_fr.accept();

            Assert.assertEquals(0, this.sender.getGlobalNotifications().size());

        } catch (Exception e) {

        }

    }

    // If sender then ignores receiver
    @Test
    public void ignoredAcceptTest() {
        try {
            Assert.assertEquals(1, this.receiver.getGlobalNotifications().size());
            Assert.assertEquals(0, this.sender.getGlobalNotifications().size());

            this.sender.ignoreUser(this.receiver);
            this.test_fr.accept();

            Assert.assertFalse(this.receiver.getFriends().values().contains(this.sender));
            Assert.assertFalse(this.sender.getFriends().values().contains(this.receiver));

        } catch (Exception e) {

        }

    }

    // If sender doesn't exist
    @Test
    public void notExistSenderAcceptTest() {
        try {
            Assert.assertEquals(1, this.receiver.getGlobalNotifications().size());
            Assert.assertEquals(0, this.sender.getGlobalNotifications().size());

            UserAccountManager.getInstance().deleteUser(this.sender);
            this.test_fr.accept();

            Assert.assertFalse(this.receiver.getFriends().values().contains(this.sender));
            Assert.assertFalse(this.sender.getFriends().values().contains(this.receiver));

        } catch (Exception e) {

        }

    }

    @Test
    public void normalDeclineTest() {
        try {
            Assert.assertEquals(1, this.receiver.getGlobalNotifications().size());
            Assert.assertEquals(0, this.sender.getGlobalNotifications().size());

            this.test_fr.decline();

            Assert.assertFalse(this.receiver.getFriends().values().contains(this.sender));
            Assert.assertFalse(this.sender.getFriends().values().contains(this.receiver));
            Assert.assertEquals(1, this.sender.getGlobalNotifications().size());

        } catch (Exception e) {

        }

    }

    // Falls die zwei Benutzer schon Freunde sind
    @Test
    public void uselessDeclineTest() {
        try {
            Assert.assertEquals(1, this.receiver.getGlobalNotifications().size());
            Assert.assertEquals(0, this.sender.getGlobalNotifications().size());

            this.receiver.addFriend(this.sender);
            this.sender.addFriend(this.receiver);
            this.test_fr.decline();

            Assert.assertEquals(0, this.sender.getGlobalNotifications().size());
            Assert.assertTrue(this.receiver.getFriends().values().contains(this.sender));
            Assert.assertTrue(this.sender.getFriends().values().contains(this.receiver));

        } catch (Exception e) {

        }

    }

    // If sender doesn't exist
    @Test
    public void notExistSenderDeclineTest() {
        try {
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
