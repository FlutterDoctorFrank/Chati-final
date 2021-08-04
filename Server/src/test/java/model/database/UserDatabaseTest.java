package model.database;


import model.user.Avatar;
import model.user.User;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.sql.*;
import java.util.UUID;

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

    }

    @Test
    public void removeRoleTest() {

    }

    @Test
    public void addNotificationTest() {

    }

    @Test
    public void removeNotificationTest() {

    }


}
