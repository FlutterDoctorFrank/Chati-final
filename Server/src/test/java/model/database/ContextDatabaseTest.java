package model.database;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class ContextDatabaseTest {

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
    public void addWorldTest() {

    }

    @Test
    public void removeWorldTest() {

    }

    @Test
    public void getWorldTest() {

    }

    @Test
    public void getWorldsTest() {

    }

    @Test
    public void addBannedUserTest() {

    }

    @Test
    public void removeBannedUserTest() {

    }

    @Test
    public void addAreaReservationTest() {

    }

    @Test
    public void removeAreaReservationTest() {

    }
}
