package model.user.account;

import controller.network.ClientSender;
import model.context.Context;
import model.context.global.GlobalContext;
import model.database.PasswordEncryption;
import model.exception.IllegalAccountActionException;
import model.exception.UserNotFoundException;
import model.role.Permission;
import model.role.Role;
import model.user.User;
import org.jetbrains.annotations.NotNull;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.UUID;


public class UserAccountManagerTest {

    private UserAccountManager userAccountManager;
    private static final String dbURL = "jdbc:derby:ChatiDB;create=true";


    //um login und andere Methoden, die login als Voraussetzung nehmen, zu testen
    class TestClientSender implements ClientSender {
        public void send(@NotNull SendAction sendAction, @NotNull Object object) {

        }
    }

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

        userAccountManager.load();


    }


    @Test
    public void registerUserTest() {
        try {
            this.userAccountManager.registerUser("registerTest", "11111");
            User real = this.userAccountManager.getUser("registerTest");
            UUID real_id = real.getUserId();
            User real_with_id = this.userAccountManager.getUser(real_id);
            User real_with_name = this.userAccountManager.getUser("registerTest");

            Assert.assertNotEquals(null, real);
            Assert.assertEquals(real, real_with_id);
            Assert.assertEquals(real, real_with_name);
            Assert.assertTrue(this.userAccountManager.isRegistered("registerTest"));
            Assert.assertTrue(this.userAccountManager.isRegistered(real_id));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test(expected = IllegalAccountActionException.class)
    public void wrongNameFormatTest() throws Exception{

            this.userAccountManager.registerUser("a", "11111");

    }

    @Test(expected = IllegalAccountActionException.class)
    public void wrongPasswordFormatTest() throws Exception{
        this.userAccountManager.registerUser("wrongPswF", "1");
    }

    @Test(expected = IllegalAccountActionException.class)
    public void duplicateTest() throws Exception{
        this.userAccountManager.registerUser("duplicate", "11111");
        this.userAccountManager.registerUser("duplicate", "11111");
    }

    @Test
    public void loginUserTest() {

        TestClientSender testClientSender = new TestClientSender();

        try {
            this.userAccountManager.registerUser("login", "11111");
            Assert.assertTrue(this.userAccountManager.isRegistered("login"));
            User actual = this.userAccountManager.loginUser("login", "11111", testClientSender);
            UUID actual_id = actual.getUserId();

            Assert.assertEquals("login", actual.getUsername());
            Assert.assertTrue(actual.isOnline());

            //auch mit getUser Methode zusammen testen
            User getUser_name = this.userAccountManager.getUser("login");
            User getUser_id = this.userAccountManager.getUser(actual_id);
            Assert.assertEquals(actual, getUser_name);
            Assert.assertEquals(actual, getUser_id);

            //in GlobalContext hinzugefuegt
            GlobalContext globalContext = GlobalContext.getInstance();
            Assert.assertTrue(globalContext.contains(actual));

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    public void logoutUserTest() {
        TestClientSender testClientSender = new TestClientSender();

        try {
            this.userAccountManager.registerUser("logoutUser", "11111");
            Assert.assertTrue(this.userAccountManager.isRegistered("logoutUser"));
            User actual = this.userAccountManager.loginUser("logoutUser", "11111", testClientSender);
            UUID actual_id = actual.getUserId();
            Assert.assertEquals("logoutUser", actual.getUsername());
            Assert.assertTrue(actual.isOnline());

            //Nach erfolgreiches Login testen dann Logout
            this.userAccountManager.logoutUser(actual_id);
            GlobalContext globalContext = GlobalContext.getInstance();
            Assert.assertFalse(globalContext.contains(actual));

        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    @Test
    public void deleteUserTest() {
        try {
            this.userAccountManager.registerUser("deleteTest", "11111");
            User real = this.userAccountManager.getUser("deleteTest");
            UUID real_id = real.getUserId();

            //Registieren erfolgreich
            Assert.assertTrue(this.userAccountManager.isRegistered("deleteTest"));
            Assert.assertTrue(this.userAccountManager.isRegistered(real_id));

            //delete testen
            this.userAccountManager.deleteUser(real_id, "11111");
            Assert.assertFalse(this.userAccountManager.isRegistered(real_id));
            Assert.assertFalse(this.userAccountManager.isRegistered("deleteTest"));
            GlobalContext globalContext = GlobalContext.getInstance();
            Assert.assertFalse(globalContext.contains(real));

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Test
    public void changePasswordTest() {
        try {
            this.userAccountManager.registerUser("changePassword", "11111");
            User real = this.userAccountManager.getUser("changePassword");
            UUID real_id = real.getUserId();
            //Registieren erfolgreich
            Assert.assertTrue(this.userAccountManager.isRegistered("changePassword"));
            Assert.assertTrue(this.userAccountManager.isRegistered(real_id));

            //changePassword testen
            this.userAccountManager.changePassword(real_id, "11111", "22222");
            //Pruefe ob in Datenbank geaendert
            String real_new_psw_salt = null;
            String real_new_psw_hash = null;
            try{
                Connection con = DriverManager.getConnection(dbURL);
                Statement st = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);

                ResultSet res = st.executeQuery("SELECT * FROM USER_ACCOUNT WHERE USER_ID = '" + real_id + "'");
                int count = 0;
                while (res.next()){
                    count ++;
                }
                if (count == 1) {
                    res.first();
                    real_new_psw_salt = res.getString("PSW_SALT");
                    real_new_psw_hash = res.getString("PSW_HASH");
                }
                con.close();
            } catch(SQLException e){
                System.out.println(e);
            }
            Assert.assertTrue(PasswordEncryption.verify("22222", real_new_psw_salt, real_new_psw_hash));


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test(expected = UserNotFoundException.class)
    public void userNotFoundTest() throws Exception{
        this.userAccountManager.getUser("userNotFound");
    }


    @Test
    public void getUsersWithPermissionTest() {
        try {
            this.userAccountManager.registerUser("getUsersWP", "11111");
            User real = this.userAccountManager.getUser("getUsersWP");
            UUID real_id = real.getUserId();

            Assert.assertTrue(this.userAccountManager.isRegistered("getUsersWP"));
            Assert.assertTrue(this.userAccountManager.isRegistered(real_id));

            //add Role und testen
            Context test_context = GlobalContext.getInstance();
            real.addRole(test_context, Role.OWNER);
            //2 Permissions davon testen
            Map<UUID, User> withPermission = this.userAccountManager.getUsersWithPermission(test_context,
                    Permission.ASSIGN_ADMINISTRATOR);
            Assert.assertTrue(withPermission.containsKey(real_id));
            withPermission = this.userAccountManager.getUsersWithPermission(test_context,
                    Permission.MANAGE_WORLDS);
            Assert.assertTrue(withPermission.containsKey(real_id));

        } catch (Exception e) {
            e.printStackTrace();
        }



    }
}
