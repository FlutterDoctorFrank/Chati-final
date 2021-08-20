package model.notification;

import model.MessageBundle;
import model.context.global.GlobalContext;
import model.exception.IllegalNotificationActionException;
import model.user.User;
import model.user.account.UserAccountManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class NotificationTest {
    Notification test_notif;
    User owner;
    private static final String dbURL = "jdbc:derby:ChatiDB;create=true";

    @Before
    public void setUp() {
        try {
            UserAccountManager.getInstance().registerUser("notif_owner", "11111");
            this.owner = UserAccountManager.getInstance().getUser("notif_owner");
            MessageBundle test_messageBundle = new MessageBundle("test_notif", "hallo");
            this.test_notif = new Notification(NotificationType.NOTIFICATION, owner, GlobalContext.getInstance(),
                    test_messageBundle);
            owner.addNotification(this.test_notif);
            //System.out.println(owner.getGlobalNotifications().size());
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
            UserAccountManager.getInstance().deleteUser(this.owner);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Test(expected = IllegalNotificationActionException.class)
    public void acceptTest() throws Exception{
        this.test_notif.accept();
    }

    @Test(expected = IllegalNotificationActionException.class)
    public void declineTest() throws Exception{
        this.test_notif.decline();
    }
}
