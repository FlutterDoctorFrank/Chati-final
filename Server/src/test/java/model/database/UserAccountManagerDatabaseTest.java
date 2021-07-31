package model.database;

import model.user.Avatar;
import model.user.User;
import org.junit.*;

import java.sql.*;

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

        User test = this.database.createAccount("name", "111");


        /*
        try{
            Connection con = DriverManager.getConnection(dbURL);
            Statement st = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);

            //Suche der User in Datenbank
            ResultSet res = st.executeQuery("SELECT * FROM USER_ACCOUNT WHERE USER_NAME = 'name'");
            int row = 0;
            while (res.next()){
                row ++;
            }

            Assert.assertEquals(1, row);
            if (row == 1) {
                res.first();
                String user_name = res.getString("USER_NAME");
                String avatar_name = res.getString("AVATAR_NAME");
                Avatar user_avatar = Avatar.valueOf(avatar_name);

                //Assert.assertEquals("name", user_name);


            } else {
                System.out.println("mehr als 1 or not exist");
            }
            con.close();
        } catch(SQLException e){
            System.out.println(e);

        }
        //Assert.assertEquals(realUser.getUserId(), test.getUserId());
        */

    }


}
