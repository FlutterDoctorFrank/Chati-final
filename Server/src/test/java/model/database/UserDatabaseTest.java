package model.database;


import model.MessageBundle;
import model.context.Context;
import model.context.global.GlobalContext;
import model.notification.FriendRequest;
import model.notification.Notification;
import model.role.Role;
import model.user.Avatar;
import model.user.User;
import org.junit.*;

import java.sql.*;
import java.time.ZoneOffset;

public class UserDatabaseTest {

    private IUserDatabase user_database;
    private IUserAccountManagerDatabase account_database;
    private static final String dbURL = "jdbc:derby:ChatiDB;create=true";

    @Before
    public void setUp(){

        //System.out.println("set up start");
        this.user_database = Database.getUserDatabase();
        this.account_database = Database.getUserAccountManagerDatabase();
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
    public void changeAvatarTest() {
        User test = this.account_database.createAccount("changeAvatar", "111");
        String test_id = test.getUserId().toString();
        this.user_database.changeAvatar(test, Avatar.ADAM);

        //pruefe ob Avatar in Datenbank geaendert ist
        String actual_avatar_name = null;
        try{
            Connection con = DriverManager.getConnection(dbURL);
            Statement st = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);

            //Suche der User in Datenbank
            ResultSet res = st.executeQuery("SELECT * FROM USER_ACCOUNT WHERE USER_ID = '" + test_id + "'");
            int row = 0;
            while (res.next()){
                row ++;
            }
            if (row == 1) {
                res.first();
                actual_avatar_name = res.getString("AVATAR_NAME");
            } else {
                System.out.println("wrong");
            }
            con.close();
        } catch(SQLException e){
            System.out.println(e);
        }

        Assert.assertEquals(Avatar.ADAM.toString(), actual_avatar_name);

    }

    @Test
    public void addFriendshipTest() {

        User test1 = this.account_database.createAccount("test1", "111");
        String test1_id = test1.getUserId().toString();
        User test2 = this.account_database.createAccount("test2", "222");
        String expected_id = test2.getUserId().toString();
        this.user_database.addFriendship(test1, test2);

        String actual_friend_id = null;
        try{
            Connection con = DriverManager.getConnection(dbURL);
            Statement st = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);

            //Suche der User in Datenbank
            ResultSet res = st.executeQuery("SELECT * FROM FRIENDSHIP WHERE USER_ID1 = '" + test1_id + "'");
            int row = 0;
            while (res.next()){
                row ++;
            }
            if (row == 1) {
                res.first();
                actual_friend_id = res.getString("USER_ID2");
            } else {
                System.out.println("mehr als 1 or not exist");
            }
            con.close();
        } catch(SQLException e){
            System.out.println(e);

        }

        Assert.assertEquals(expected_id, actual_friend_id);
    }

    @Test
    public void removeFriendshipTest() {
        User test1 = this.account_database.createAccount("test1", "111");
        String test1_id = test1.getUserId().toString();
        User test2 = this.account_database.createAccount("test2", "222");
        String expected_id = test2.getUserId().toString();
        //Zwei Moeglichkeiten der Reihenfolge
        this.user_database.addFriendship(test1, test2);
        this.user_database.addFriendship(test2, test1);

        //Test ob zwei mal hinzufuegen
        try{
            Connection con = DriverManager.getConnection(dbURL);
            Statement st = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
            //Suche der User in Datenbank
            ResultSet res = st.executeQuery("SELECT * FROM FRIENDSHIP WHERE USER_ID1 = '" + test1_id +
                    "' OR USER_ID2 = '" + test1_id + "'");
            int row = 0;
            while (res.next()){
                row ++;
            }
            Assert.assertEquals(2, row);
            con.close();
        } catch(SQLException e){
            System.out.println(e);
        }

        //einmal removeFriendship benutzen teste, ob zwei Zeile geloescht sind
        this.user_database.removeFriendship(test1, test2);

        //suche in Datenbank, ob das geloescht
        int row = 0;
        try{
            Connection con = DriverManager.getConnection(dbURL);
            Statement st = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);

            //Suche der User in Datenbank und erhalte Name und Password
            ResultSet res = st.executeQuery("SELECT * FROM FRIENDSHIP WHERE (USER_ID1 = '" + test1_id +
                    "' OR USER_ID2 = '" + test1_id + "')");
            while (res.next()){
                row ++;
            }

            con.close();
        } catch(SQLException e){
            System.out.println(e);
        }
        Assert.assertEquals(0, row);

    }

    @Test
    public void addIgnoredUserTest() {
        User test1 = this.account_database.createAccount("test1", "111");
        String test1_id = test1.getUserId().toString();
        User test2 = this.account_database.createAccount("test2", "222");
        String expected_id = test2.getUserId().toString();
        this.user_database.addIgnoredUser(test1, test2);

        String actual_ignored_id = null;
        try{
            Connection con = DriverManager.getConnection(dbURL);
            Statement st = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);

            //Suche der User in Datenbank
            ResultSet res = st.executeQuery("SELECT * FROM IGNORE WHERE USER_ID = '" + test1_id + "'");
            int row = 0;
            while (res.next()){
                row ++;
            }
            if (row == 1) {
                res.first();
                actual_ignored_id = res.getString("IGNORED_ID");
            } else {
                System.out.println("mehr als 1 or not exist");
            }
            con.close();
        } catch(SQLException e){
            System.out.println(e);

        }

        Assert.assertEquals(expected_id, actual_ignored_id);
    }

    @Test
    public void removeIgnoredUserTest() {
        User test1 = this.account_database.createAccount("test1", "111");
        String test1_id = test1.getUserId().toString();
        User test2 = this.account_database.createAccount("test2", "222");
        String expected_id = test2.getUserId().toString();
        //Zwei Moeglichkeiten der Reihenfolge
        this.user_database.addIgnoredUser(test1, test2);


        //remove mit Methode removeIgnoredUser
        this.user_database.removeIgnoredUser(test1, test2);

        //suche in Datenbank, ob das geloescht
        int row = 0;
        try{
            Connection con = DriverManager.getConnection(dbURL);
            Statement st = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);

            //Suche der User in Datenbank und erhalte Name und Password
            ResultSet res = st.executeQuery("SELECT * FROM IGNORE WHERE USER_ID = '" + test1_id + "'");
            while (res.next()){
                row ++;
            }
            con.close();
        } catch(SQLException e){
            System.out.println(e);
        }
        Assert.assertEquals(0, row);

    }

    @Test
    public void addRoleTest() {
        User test = this.account_database.createAccount("addRole", "111");
        Context test_context = GlobalContext.getInstance();
        this.user_database.addRole(test, test_context, Role.OWNER);
        String actual_user_id = test.getUserId().toString();

        //Suche in Datenbank, ob erfolgreich hinzufuegt
        String actual_context_id = null;
        String actual_role = null;
        try{
            Connection con = DriverManager.getConnection(dbURL);
            Statement st = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);

            ResultSet res = st.executeQuery("SELECT * FROM ROLE_WITH_CONTEXT WHERE USER_ID = '" + actual_user_id + "'");
            int row = 0;
            while (res.next()){
                row ++;
            }
            if (row == 1) {
                res.first();
                actual_context_id = res.getString("CONTEXT_ID");
                actual_role = res.getString("USER_ROLE");
            } else {
                System.out.println("wrong");
            }
            con.close();
        } catch(SQLException e){
            System.out.println(e);
        }

        System.out.println(actual_role);
        System.out.println(actual_context_id);

        Assert.assertEquals(test_context.getContextId().getId(), actual_context_id);
        Assert.assertEquals(Role.OWNER.name(), actual_role);

    }

    @Test
    public void removeRoleTest() {
        User test = this.account_database.createAccount("removeRole", "111");
        Context test_context = GlobalContext.getInstance();
        this.user_database.addRole(test, test_context, Role.OWNER);
        String actual_user_id = test.getUserId().toString();

        //Pruefe ob echt hinzufuegen
        int add_row = 0;
        try{
            Connection con = DriverManager.getConnection(dbURL);
            Statement st = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);

            ResultSet res = st.executeQuery("SELECT * FROM ROLE_WITH_CONTEXT WHERE USER_ID = '" + actual_user_id + "'");
            while (res.next()){
                add_row ++;
            }
            con.close();
        } catch(SQLException e){
            System.out.println(e);
        }
        Assert.assertEquals(1, add_row);

        this.user_database.removeRole(test, test_context, Role.OWNER);
        //Pruefe ob geloescht
        int row = 0;
        try{
            Connection con = DriverManager.getConnection(dbURL);
            Statement st = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);

            ResultSet res = st.executeQuery("SELECT * FROM ROLE_WITH_CONTEXT WHERE USER_ID = '" + actual_user_id + "'");
            while (res.next()){
                add_row ++;
            }
            con.close();
        } catch(SQLException e){
            System.out.println(e);
        }
        Assert.assertEquals(0, row);
    }

    @Test
    public void addNotificationTest() {
        User test = this.account_database.createAccount("addNotif", "111");
        User requester = this.account_database.createAccount("friend_requester", "222");
        Context test_context = GlobalContext.getInstance();

        //verschiedene NotificationType
        MessageBundle notif_messageBundle = new MessageBundle("notification");
        //Notification
        Notification test_notif = new Notification(test, test_context, notif_messageBundle);
        String expected_notif_id = test_notif.getNotificationId().toString();
        //FriendRequest
        FriendRequest test_friendRequest = new FriendRequest(test, "bitte bitte", requester);
        String expected_friendRequest_id = test_friendRequest.getNotificationId().toString();


        //Fuege Notifikationen mit Methode addNotification
        this.user_database.addNotification(test, test_notif);
        this.user_database.addNotification(test, test_friendRequest);


        //normale Notification
        //Suche der User in Datenbank
        String actual_user_id = null;
        String actual_owing_context_id = null;
        Timestamp actual_send_time = null;
        String actual_notification_type = null;
        Boolean actual_is_read = null;
        Boolean actual_is_accepted = null;
        Boolean actual_is_declined = null;

        try{
            Connection con = DriverManager.getConnection(dbURL);
            Statement st = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);

            ResultSet res = st.executeQuery("SELECT * FROM NOTIFICATION WHERE NOTIFICATION_ID = '" +
                    expected_notif_id + "'");
            int notif_row = 0;
            while (res.next()){
                notif_row ++;
            }
            if (notif_row == 1) {
                res.first();
                actual_user_id = res.getString("USER_ID");
                actual_owing_context_id = res.getString("CONTEXT_ID");
                actual_send_time = res.getTimestamp("TIMESTAMP");
                actual_notification_type = res.getString("NOTIFICATION_TYPE");
                actual_is_read = res.getBoolean("IS_READ");
                actual_is_accepted = res.getBoolean("IS_ACCEPTED");
                actual_is_declined = res.getBoolean("IS_DECLINED");
            } else {
                System.out.println("wrong");
            }
            con.close();
        } catch(SQLException e){
            System.out.println(e);
        }

        Assert.assertEquals(test.getUserId().toString(), actual_user_id);
        Assert.assertEquals(test_notif.getContext().getContextId().getId(), actual_owing_context_id);
        Assert.assertEquals(test_notif.getTimestamp(), actual_send_time.toLocalDateTime());
        Assert.assertEquals(test_notif.getNotificationType().name(), actual_notification_type);
        Assert.assertFalse(actual_is_read);
        Assert.assertFalse(actual_is_accepted);
        Assert.assertFalse(actual_is_declined);


        //FriendRequest
        actual_user_id = null;
        actual_owing_context_id = null;
        actual_send_time = null;


        String actual_requester_id = null;
        String actual_user_message = null;
        try{
            Connection con = DriverManager.getConnection(dbURL);
            Statement st = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);

            ResultSet res = st.executeQuery("SELECT * FROM NOTIFICATION WHERE NOTIFICATION_ID = '" +
                    expected_friendRequest_id + "'");
            int request_row = 0;
            while (res.next()){
                request_row ++;
            }
            if (request_row == 1) {
                res.first();
                actual_user_id = res.getString("USER_ID");
                actual_owing_context_id = res.getString("CONTEXT_ID");
                actual_send_time = res.getTimestamp("TIMESTAMP");
                actual_requester_id = res.getString("ARGUMENT_1");
                actual_user_message = res.getString("ARGUMENT_2");
            } else {
                System.out.println("wrong");
            }
            con.close();
        } catch(SQLException e){
            System.out.println(e);
        }

        Assert.assertEquals(test.getUserId().toString(), actual_user_id);
        Assert.assertEquals(test_friendRequest.getContext().getContextId().getId(), actual_owing_context_id);
        Assert.assertEquals(test_friendRequest.getTimestamp(), actual_send_time.toLocalDateTime());
        Assert.assertEquals(test_friendRequest.getMessageBundle().getArguments()[0].toString(), "friend_requester");
        Assert.assertEquals(test_friendRequest.getMessageBundle().getArguments()[1].toString(), actual_user_message);
    }

    @Test
    public void removeNotificationTest() {
        User test = this.account_database.createAccount("removeNotif", "111");
        Context test_context = GlobalContext.getInstance();
        MessageBundle notif_messageBundle = new MessageBundle("notification");
        //Erstelle eine Notification
        Notification test_notif = new Notification(test, test_context, notif_messageBundle);
        String test_notif_id = test_notif.getNotificationId().toString();
        this.user_database.addNotification(test, test_notif);

        //Pruefe, ob echt eine Notifikation hinzufuegen
        int add_row = 0;
        try{
            Connection con = DriverManager.getConnection(dbURL);
            Statement st = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);

            ResultSet res = st.executeQuery("SELECT * FROM NOTIFICATION WHERE NOTIFICATION_ID = '" +
                    test_notif_id + "'");

            while (res.next()){
                add_row ++;
            }

            con.close();
        } catch(SQLException e){
            System.out.println(e);
        }
        Assert.assertEquals(1, add_row);


        this.user_database.removeNotification(test, test_notif);
        //Suche in Datenbank, ob diese Notifikation geloescht
        int notif_row = 0;
        try{
            Connection con = DriverManager.getConnection(dbURL);
            Statement st = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);

            ResultSet res = st.executeQuery("SELECT * FROM NOTIFICATION WHERE NOTIFICATION_ID = '" +
                    test_notif_id + "'");

            while (res.next()){
                notif_row ++;
            }

            con.close();
        } catch(SQLException e){
            System.out.println(e);
        }

        Assert.assertEquals(0, notif_row);

    }

    @Test
    public void updateNotificationTest() {
        User test = this.account_database.createAccount("updateNotif", "111");
        User requester = this.account_database.createAccount("update_requester", "222");
        Context test_context = GlobalContext.getInstance();

        //verschiedene NotificationType
        MessageBundle notif_messageBundle = new MessageBundle("notification");
        //Notification
        Notification test_notif = new Notification(test, test_context, notif_messageBundle);
        String expected_notif_id = test_notif.getNotificationId().toString();
        //FriendRequest
        FriendRequest test_friendRequest = new FriendRequest(test, "bitte bitte", requester);
        String expected_friendRequest_id = test_friendRequest.getNotificationId().toString();


        //Fuege Notifikationen mit Methode addNotification
        this.user_database.addNotification(test, test_notif);
        this.user_database.addNotification(test, test_friendRequest);

        //update Notification
        test_notif.read();
        this.user_database.updateNotification(test, test_notif);
        try {
            test_friendRequest.accept();
            this.user_database.updateNotification(test, test_friendRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Suche die Notification direkt in Datenbank
        Boolean actual_is_read = null;
        Boolean actual_is_accepted = null;
        Boolean actual_is_declined = null;
        try{
            Connection con = DriverManager.getConnection(dbURL);
            Statement st = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);

            ResultSet res = st.executeQuery("SELECT * FROM NOTIFICATION WHERE NOTIFICATION_ID = '" +
                    expected_notif_id + "'");
            int notif_row = 0;
            while (res.next()){
                notif_row ++;
            }
            if (notif_row == 1) {
                res.first();
                actual_is_read = res.getBoolean("IS_READ");

            } else {
                System.out.println("wrong");
            }
            con.close();
        } catch(SQLException e){
            System.out.println(e);
        }

        Assert.assertTrue(actual_is_read);

        // Suche FreundesRequest direkt in Datenbank
        actual_is_read = null;
        actual_is_accepted = null;
        actual_is_declined = null;
        try{
            Connection con = DriverManager.getConnection(dbURL);
            Statement st = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);

            ResultSet res = st.executeQuery("SELECT * FROM NOTIFICATION WHERE NOTIFICATION_ID = '" +
                    expected_friendRequest_id + "'");
            int notif_row = 0;
            while (res.next()){
                notif_row ++;
            }
            if (notif_row == 1) {
                res.first();
                actual_is_read = res.getBoolean("IS_READ");
                actual_is_accepted = res.getBoolean("IS_ACCEPTED");
                actual_is_declined = res.getBoolean("IS_DECLINED");


            } else {
                System.out.println("wrong");
            }
            con.close();
        } catch(SQLException e){
            System.out.println(e);
        }

        Assert.assertTrue(actual_is_read);
        Assert.assertTrue(actual_is_accepted);
        Assert.assertFalse(actual_is_declined);


    }


}
