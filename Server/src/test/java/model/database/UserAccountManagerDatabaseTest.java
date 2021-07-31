package model.database;

import model.user.Avatar;
import model.user.User;
import org.junit.*;

import java.sql.*;
import java.util.UUID;

public class UserAccountManagerDatabaseTest {

    private IUserAccountManagerDatabase database;
    private static final String dbURL = "jdbc:derby:ChatiDB;create=true";

    @Before
    public void setUp(){

        //System.out.println("set up start");
        this.database = Database.getUserAccountManagerDatabase();
        //System.out.println("set up success");
    }


    //!!!!!!!!!dropTable problem!!!!!!!!
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
        String realPassword = null;
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
                realPassword = res.getString("USER_PSW");
                realUserID = UUID.fromString(res.getString("USER_ID"));

            } else {
                System.out.println("mehr als 1 or not exist");
            }
            con.close();
        } catch(SQLException e){
            System.out.println(e);

        }
        Assert.assertEquals("name", realName);
        Assert.assertEquals("111", realPassword);
        Assert.assertEquals(test.getUserId(), realUserID);


    }

    //getUser(username)
    @Test

    public void getUserTest(){

        User test = this.database.createAccount("getUserTest", "111");
        System.out.println("build User test success");

        User real = this.database.getUser("getUserTest");
        System.out.println("build User real success");

        Assert.assertEquals(test.getUserId(), real.getUserId());

        //Erstellen ein Benutzer und direkt speichern in Datenbank
        /*
        User test = new User("getUserTest");
        User real = null;
        try {
            Connection con = DriverManager.getConnection(dbURL);
            String sql = "INSERT INTO USER_ACCOUNT(USER_ID, USER_NAME, USER_PSW, LAST_ONLINE_TIME, AVATAR_NAME) values(?,?,?,?,?)";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, test.getUserId().toString());
            ps.setString(2, test.getUsername());
            //Angenommen Password = "000"
            ps.setString(3, "000");
            ps.setString(4, null);
            ps.setString(5, null);
            ps.executeUpdate();
            con.close();
        } catch (SQLException e) {
            System.out.print("Fehler in getUserTest: " + e);
        }

        //Erhalten Benutzer mit getUser Methode
        real = this.database.getUser("getUserTest");
        */




    }

}
