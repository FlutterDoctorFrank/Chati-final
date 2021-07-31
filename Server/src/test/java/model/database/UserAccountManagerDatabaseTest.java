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

        this.database = Database.getUserAccountManagerDatabase();
    }

    private void dropTable(String tableName){
        try {
            Connection con = DriverManager.getConnection(dbURL);
            PreparedStatement ps = con.prepareStatement("DROP TABLE " + tableName);
            ps.executeUpdate();
            con.close();
        } catch (SQLException e) {
            System.out.print("Fehler in deleteData: " + e);
        }
    }

    @After
    public void tearDown() {
        dropTable("USER_ACCOUNT");
        dropTable("WORLDS");
        dropTable("BAN");
        dropTable("IGNORE");
        dropTable("FRIENDSHIP");
        dropTable("USER_RESERVATION");
        dropTable("ROLE_WITH_CONTEXT");
        dropTable("NOTIFICATION");

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


}
