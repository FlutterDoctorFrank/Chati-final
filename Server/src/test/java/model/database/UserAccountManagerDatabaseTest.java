package model.database;

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
        String real_psw = null;
        try{
            Connection con = DriverManager.getConnection(dbURL);
            Statement st = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);

            //Suche der User in Datenbank und erhalte Password
            ResultSet res = st.executeQuery("SELECT * FROM USER_ACCOUNT WHERE USER_NAME = 'setPassword'");
            res.first();
            real_psw = res.getString("USER_PSW");
            con.close();
        } catch(SQLException e){
            System.out.println(e);
        }
        Assert.assertEquals("222", real_psw);

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
        //+1s
        Timestamp sss = new Timestamp(current_time.getTime() + 1000);
        boolean result = sss.after(real_time);
        Assert.assertEquals(true, result);
        //Assert.assertEquals(current_time, real_time);
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

    //getUser(userID)
    @Test
    public void getUserTest2(){

        User test = this.database.createAccount("getUserTest", "111");
        UUID testID = test.getUserId();

        User real = this.database.getUser(testID);

        Assert.assertEquals(test.getUserId(), real.getUserId());
        Assert.assertEquals(test.getUsername(), real.getUsername());


    }

    @Test
    public void getUsers() {

    }





}
