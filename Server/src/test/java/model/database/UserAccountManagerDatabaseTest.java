package model.database;

import model.context.Context;
import model.context.global.GlobalContext;
import model.role.ContextRole;
import model.role.Role;
import model.user.User;
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
import java.sql.Timestamp;
import java.util.Map;
import java.util.UUID;


public class UserAccountManagerDatabaseTest {

    private IUserAccountManagerDatabase database;
    private IUserDatabase user_database;
    private static final String dbURL = "jdbc:derby:ChatiDB;create=true";

    @Before
    public void setUp(){
        //System.out.println("set up start");
        this.database = Database.getUserAccountManagerDatabase();
        this.user_database = Database.getUserDatabase();
        //System.out.println("set up success");
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
    public void createAccountTest() {

        //benutze database.createAccount, um ein neuer Benutzer zu erstellen und Informationen in Datenbank zu speichern
        User test = this.database.createAccount("name", "111");

        //suche direkt in Datenbank
        int row = 0;
        String realName = null;
        String realHash = null;
        String realSalt = null;
        UUID realUserID = null;
        try{
            Connection con = DriverManager.getConnection(dbURL);
            Statement st = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);

            //Suche der User in Datenbank und erhalte Name und Password
            ResultSet res = st.executeQuery("SELECT * FROM USER_ACCOUNT WHERE USER_NAME = 'name'");

            while (res.next()){
                row ++;
            }
            if (row == 1) {
                res.first();
                realName = res.getString("USER_NAME");
                realHash = res.getString("PSW_HASH");
                realSalt = res.getString("PSW_SALT");
                realUserID = UUID.fromString(res.getString("USER_ID"));

            } else {
                System.out.println("mehr als 1 or not exist");
            }
            con.close();

            Assert.assertEquals("name", realName);
            Assert.assertTrue(PasswordEncryption.verify("111", realSalt, realHash));
            Assert.assertEquals(test.getUserId(), realUserID);
        } catch(Exception e){
            System.out.println(e);

        }



    }

    //checkPassword(String username, String password)
    @Test
    public void checkPasswordTest(){
        User test = this.database.createAccount("checkPassword", "111");
        boolean realResult = this.database.checkPassword("checkPassword", "111");
        boolean wrongSituation = this.database.checkPassword("checkPassword", "222");

        Assert.assertEquals(true, realResult);
        Assert.assertEquals(false, wrongSituation);
    }

    @Test
    public void setPasswordTest() {
        //Erstelle ein Benutzer
        User test = this.database.createAccount("setPassword", "111");
        //aendern Password zu 222
        this.database.setPassword(test, "222");

        //Suche direkt in Datenbank
        String real_salt = null;
        String real_hash = null;
        try{
            Connection con = DriverManager.getConnection(dbURL);
            Statement st = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);

            //Suche der User in Datenbank und erhalte Password
            ResultSet res = st.executeQuery("SELECT * FROM USER_ACCOUNT WHERE USER_NAME = 'setPassword'");
            res.first();
            real_hash = res.getString("PSW_HASH");
            real_salt = res.getString("PSW_SALT");
            con.close();

            Assert.assertTrue(PasswordEncryption.verify("222", real_salt, real_hash));
        } catch(Exception e){
            System.out.println(e);
        }


    }

    @Test
    public void updateLastOnlineTime() {
        //Erstelle ein Benutzer
        User test = this.database.createAccount("updateTime", "111");
        Timestamp current_time = new Timestamp(System.currentTimeMillis());
        this.database.updateLastOnlineTime(test);

        Timestamp real_time = null;
        try{
            Connection con = DriverManager.getConnection(dbURL);
            Statement st = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);

            //Suche der User in Datenbank und erhalte UpdateTime
            ResultSet res = st.executeQuery("SELECT * FROM USER_ACCOUNT WHERE USER_NAME = 'updateTime'");
            res.first();
            real_time = res.getTimestamp("LAST_ONLINE_TIME");
            con.close();
        } catch(SQLException e){
            System.out.println("Test" + e);
        }

        //Wegen laufendes Program
        //current_time - 1s < timestamp in Datenbank < current_time + 1s
        Timestamp a_second_after_current_time = new Timestamp(current_time.getTime() + 1000);
        Timestamp a_second_before_current_time = new Timestamp(current_time.getTime() - 1000);
        boolean result1 = a_second_after_current_time.after(real_time);
        boolean result2 = a_second_before_current_time.before(real_time);
        Assert.assertEquals(true, result1);
        Assert.assertEquals(true, result2);
    }

    @Test
    public void deleteAccount() {
        //Erstelle ein Benutzer
        User test = this.database.createAccount("deleteAccount", "111");
        this.database.deleteAccount(test);

        int row = 0;
        try{
            Connection con = DriverManager.getConnection(dbURL);
            Statement st = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);

            ResultSet res = st.executeQuery("SELECT * FROM USER_ACCOUNT WHERE USER_NAME = 'deleteAccount'");

            while (res.next()){
                row ++;
            }
            con.close();
        } catch(SQLException e){
            System.out.println(e);
        }
        Assert.assertEquals(0, row);
    }

    //getUser(username)
    @Test
    public void getUserTest(){

        User test = this.database.createAccount("getUserTest", "111");
        User real = this.database.getUser("getUserTest");

        Assert.assertEquals(test.getUserId(), real.getUserId());
        Assert.assertEquals(test.getUsername(), real.getUsername());

        /*
        //Role Testen
        Context test_context = GlobalContext.getInstance();
        this.user_database.addRole(test, test_context, Role.OWNER);
        real = this.database.getUser("getUserTest");
        Assert.assertEquals(1, real.getGlobalRoles().getRoles().size());
        //Fuege zwei Rolen hiinzu, die in gleichem Context sind
        this.user_database.addRole(test, test_context, Role.ADMINISTRATOR);
        real = this.database.getUser("getUserTest");
        Assert.assertEquals(2, real.getGlobalRoles().getRoles().size());
        Assert.assertTrue(real.getGlobalRoles().getRoles().contains(Role.ADMINISTRATOR));
        Assert.assertTrue(real.getGlobalRoles().getRoles().contains(Role.OWNER));

        //Freundesliste
        User friend = this.database.createAccount("friend", "222");
        String friend_id = friend.getUserId().toString();
        this.user_database.addFriendship(test, friend);
        //Prüfe ob echt hinzufügen
        int friend_count = 0;
        try{
            Connection con = DriverManager.getConnection(dbURL);
            Statement st = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);

            ResultSet res = st.executeQuery("SELECT * FROM FRIENDSHIP WHERE USER_ID2 = '" + friend_id + "'");
            while (res.next()){
                friend_count ++;
            }
            con.close();
        } catch(SQLException e){
            System.out.println(e);
        }
        Assert.assertEquals(1, friend_count);
        //Pruefe ob man mit getUser Freundesliste richtig bekommen kann
        real = this.database.getUser("getUserTest");
        Assert.assertEquals(1, real.getFriends().size());
        Assert.assertEquals("friend", real.getFriends().get(friend.getUserId()).getUsername());
        real = this.database.getUser("getUserTest");
        Assert.assertEquals(1, real.getFriends().size());
        Assert.assertEquals("friend", real.getFriends().get(friend.getUserId()).getUsername());


        //IgnoredUser
        User ignore = this.database.createAccount("ignore", "222");
        String ignore_id = ignore.getUserId().toString();
        this.user_database.addIgnoredUser(test, ignore);
        int ignore_count = 0;
        try{
            Connection con = DriverManager.getConnection(dbURL);
            Statement st = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);

            ResultSet res = st.executeQuery("SELECT * FROM IGNORE WHERE IGNORED_ID = '" + ignore_id + "'");
            while (res.next()){
                ignore_count ++;
            }
            con.close();
        } catch(SQLException e){
            System.out.println(e);
        }
        Assert.assertEquals(1, ignore_count);
        //Pruefe ob man mit getUser IgnoredUsers richtig bekommen kann
        real = this.database.getUser("getUserTest");
        Assert.assertEquals(1, real.getIgnoredUsers().size());
        Assert.assertEquals("ignore", real.getIgnoredUsers().get(ignore.getUserId()).getUsername());

         */


    }

    //getUser(userID)
    @Test
    public void getUserTest2(){

        User test = this.database.createAccount("getUserTest", "111");
        UUID testID = test.getUserId();

        User real = this.database.getUser(testID);

        Assert.assertEquals(test.getUserId(), real.getUserId());
        Assert.assertEquals(test.getUsername(), real.getUsername());

        /*
        //Fuege eine Role hinzu und pruefe, ob mit getUser korrekte Rolen bekommen kann
        Context test_context = GlobalContext.getInstance();
        this.user_database.addRole(test, test_context, Role.OWNER);
        User real_after_add_role = this.database.getUser(testID);
        ContextRole actual_context_role = real_after_add_role.getGlobalRoles();
        int count = actual_context_role.getRoles().size();

        System.out.println(real_after_add_role.getGlobalRoles().getRoles());
        System.out.println(real_after_add_role.getGlobalRoles().getContext().getContextId().getId());

        Assert.assertEquals(1,count);
        Assert.assertEquals(true, actual_context_role.getRoles().contains(Role.OWNER));

        //Fuege zwei Rolen hiinzu, die in gleichem Context sind
        this.user_database.addRole(test, test_context, Role.ADMINISTRATOR);
        real_after_add_role = this.database.getUser(testID);
        actual_context_role = real_after_add_role.getGlobalRoles();
        count = actual_context_role.getRoles().size();
        Assert.assertEquals(2,count);
        Assert.assertEquals(true, actual_context_role.getRoles().contains(Role.OWNER));
        Assert.assertEquals(true, actual_context_role.getRoles().contains(Role.ADMINISTRATOR));

        //Freundesliste
        User friend = this.database.createAccount("friend", "222");
        String friend_id = friend.getUserId().toString();
        this.user_database.addFriendship(test, friend);
        //Prüfe ob echt hinzufügen
        int friend_count = 0;
        try{
            Connection con = DriverManager.getConnection(dbURL);
            Statement st = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);

            ResultSet res = st.executeQuery("SELECT * FROM FRIENDSHIP WHERE USER_ID2 = '" + friend_id + "'");
            while (res.next()){
                friend_count ++;
            }
            con.close();
        } catch(SQLException e){
            System.out.println(e);
        }
        Assert.assertEquals(1, friend_count);
        //Pruefe ob man mit getUser Freundesliste richtig bekommen kann
        real = this.database.getUser(testID);
        Assert.assertEquals(1, real.getFriends().size());
        Assert.assertEquals("friend", real.getFriends().get(friend.getUserId()).getUsername());

        //IgnoredUser
        User ignore = this.database.createAccount("ignore", "222");
        String ignore_id = ignore.getUserId().toString();
        this.user_database.addIgnoredUser(test, ignore);
        int ignore_count = 0;
        try{
            Connection con = DriverManager.getConnection(dbURL);
            Statement st = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);

            ResultSet res = st.executeQuery("SELECT * FROM IGNORE WHERE IGNORED_ID = '" + ignore_id + "'");
            while (res.next()){
                ignore_count ++;
            }
            con.close();
        } catch(SQLException e){
            System.out.println(e);
        }
        Assert.assertEquals(1, ignore_count);
        //Pruefe ob man mit getUser IgnoredUsers richtig bekommen kann
        real = this.database.getUser(testID);
        Assert.assertEquals(1, real.getIgnoredUsers().size());
        Assert.assertEquals("ignore", real.getIgnoredUsers().get(ignore.getUserId()).getUsername());

         */

    }

    @Test
    public void getUsers() {
        User test1 = this.database.createAccount("test1", "111");
        User test2 = this.database.createAccount("test2", "222");
        User test3 = this.database.createAccount("ignore", "333");

        //addRole
        Context test_context = GlobalContext.getInstance();
        this.user_database.addRole(test1, test_context, Role.OWNER);
        this.user_database.addRole(test2, test_context, Role.OWNER);
        this.user_database.addRole(test2, test_context, Role.ADMINISTRATOR);
        //addFriend
        this.user_database.addFriendship(test1, test2);
        //addIgnore
        this.user_database.addIgnoredUser(test1, test3);

        Map<UUID, User> real_users = this.database.getUsers();
        Assert.assertEquals(3, real_users.size());
        User real1 = real_users.get(test1.getUserId());
        User real2 = real_users.get(test2.getUserId());
        User real3 = real_users.get(test3.getUserId());


        Assert.assertEquals(test1.getUserId(), real1.getUserId() );
        Assert.assertEquals(test2.getUserId(), real2.getUserId() );
        Assert.assertEquals(test1.getUsername(), real1.getUsername() );
        Assert.assertEquals(test2.getUsername(), real2.getUsername() );
        //Role
        Assert.assertEquals(1, real1.getGlobalRoles().getRoles().size());
        Assert.assertTrue(real1.getGlobalRoles().getRoles().contains(Role.OWNER));
        Assert.assertTrue(real2.getGlobalRoles().getRoles().contains(Role.OWNER));
        Assert.assertTrue(real2.getGlobalRoles().getRoles().contains(Role.ADMINISTRATOR));
        Assert.assertEquals(2, real2.getGlobalRoles().getRoles().size());
        //Friendship
        Assert.assertEquals(1, real1.getFriends().size());
        Assert.assertEquals("test2", real1.getFriends().get(test2.getUserId()).getUsername());
        Assert.assertEquals(1, real2.getFriends().size());
        Assert.assertEquals("test1", real2.getFriends().get(test1.getUserId()).getUsername());
        //Ignore
        Assert.assertEquals(1, real1.getIgnoredUsers().size());
        Assert.assertEquals("ignore", real1.getIgnoredUsers().get(test3.getUserId()).getUsername());
        //System.out.println(real1.getUserId());

        //System.out.println(real1.getGlobalRoles().getRoles().size());

    }





}
