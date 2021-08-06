package model.user.account;

import model.user.Status;
import model.user.User;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

public class UserAccountManagerTest {

    private UserAccountManager userAccountManager;
    private static final String dbURL = "jdbc:derby:ChatiDB;create=true";

    @Before
    public void setUp() {
        this.userAccountManager = UserAccountManager.getInstance();
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

    }


    @Test
    public void registerUserTest() {
        try {
            this.userAccountManager.registerUser("registerTest", "11111");
            User real = this.userAccountManager.getUser("registerTest");
            UUID real_id = real.getUserId();
            User real2 = this.userAccountManager.getUser(real_id);

            Assert.assertNotEquals(null, real);
            Assert.assertEquals(real, real2);
            Assert.assertTrue(this.userAccountManager.isRegistered("registerTest"));
            Assert.assertTrue(this.userAccountManager.isRegistered(real_id));
        } catch (Exception e) {
            e.printStackTrace();
        }



    }

    @Test
    public void loginUserTest() {

    }

    @Test
    public void logoutUserTest() {

    }

    //userID, password
    @Test
    public void deleteUserTest() {

    }

    //User
    @Test
    public void deleteUserTest2() {

    }

    @Test
    public void changePasswordTest() {

    }

    @Test
    public void getUserTest() {

    }

    @Test
    public void getUsersWithPermissionTest() {

    }
}
