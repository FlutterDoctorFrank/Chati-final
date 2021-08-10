package model.database;

import model.context.global.GlobalContext;
import model.context.spatial.SpatialMap;
import model.context.spatial.World;
import model.role.Role;
import model.user.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.*;

public class ContextDatabaseTest {

    private IContextDatabase context_database;
    private IUserDatabase user_database;
    private IUserAccountManagerDatabase account_database;
    private static final String dbURL = "jdbc:derby:ChatiDB;create=true";

    @Before
    public void setUp(){

        //System.out.println("set up start");
        this.context_database = Database.getContextDatabase();
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
    public void addWorldTest() {


        GlobalContext test_gbc = GlobalContext.getInstance();
        User performer = this.account_database.createAccount("performer", "111");
        this.user_database.addRole(performer, test_gbc, Role.OWNER);
        World test = new World("test_world", SpatialMap.MAP);
        System.out.println(test.getMap().getName());
        try {
            test_gbc.createWorld(performer.getUserId(), "test_world", SpatialMap.MAP);
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.context_database.addWorld(test);






    }

    @Test
    public void removeWorldTest() {
        /*
        //Füge eine Welt direkt in Datenbank
        try {
            Connection con = DriverManager.getConnection(dbURL);
            PreparedStatement ps = con.prepareStatement("INSERT INTO WORLDS(WORLD_ID, WORLD_NAME, MAP_NAME) values(?,?,?)");
            World test = new World("test_world", SpatialMap.PLACEHOLDER);
            ps.setString(1, "12345");
            ps.setString(2, "test_world");
            ps.setString(3, "test_map_name");
            ps.executeUpdate();
            con.close();
        } catch (Exception e) {
            System.out.print(e);
        }

        //löschen mit removeWorld

         */


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
