package model.notification;

import model.MessageBundle;
import model.context.global.GlobalContext;
import model.user.User;
import model.user.account.UserAccountManager;
import org.junit.After;
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
            this.receiver.addNotification(this.test_fr);

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
            UserAccountManager.getInstance().deleteUser(this.sender);
            UserAccountManager.getInstance().deleteUser(this.receiver);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Test
    public void acceptTest() {

    }

}
