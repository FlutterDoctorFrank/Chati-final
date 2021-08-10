package model.database;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backend.headless.mock.graphics.MockGL20;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import model.context.ContextID;
import model.context.global.GlobalContext;
import model.context.spatial.SpatialMap;
import model.context.spatial.World;
import model.role.Role;
import model.user.User;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.*;
import java.util.Map;

public class ContextDatabaseTest {

    private IContextDatabase context_database;
    private IUserDatabase user_database;
    private IUserAccountManagerDatabase account_database;
    private static final String dbURL = "jdbc:derby:ChatiDB;create=true";

    @BeforeClass
    public static void openGdx() {
        new HeadlessApplication(new ApplicationAdapter() {
            @Override
            public void create() {
                Gdx.gl = new MockGL20();
            }
        });
    }

    @AfterClass
    public static void closeGdx() {
        Gdx.app.exit();
    }

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
        //System.out.println(test.getMap().getName());
        /*
        try {
            test_gbc.createWorld(performer.getUserId(), "test_world", SpatialMap.MAP);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Assert.assertEquals(1, test_gbc.getWorlds().size());

         */

        this.context_database.addWorld(test);

        //Suche direkt in Datenbank
        try {
            Connection con = DriverManager.getConnection(dbURL);
            Statement st = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
            ResultSet res = st.executeQuery("SELECT * FROM WORLDS WHERE WORLD_NAME = '" + test.getContextName() + "'");
            int count = 0;
            if (res.next()) {
                count ++;
            }

            Assert.assertEquals(1, count);
            res.first();
            String real_world_name = res.getString("WORLD_NAME");
            String real_map_name = res.getString("MAP_NAME");

            Assert.assertEquals("test_world", real_world_name);
            Assert.assertEquals(SpatialMap.MAP.name(), real_map_name);

            st.close();
            con.close();
        } catch (SQLException e) {
            System.out.print("Fehler in deleteData: " + e);
        }








    }

    @Test
    public void removeWorldTest() {
        GlobalContext test_gbc = GlobalContext.getInstance();
        User performer = this.account_database.createAccount("performer", "111");
        this.user_database.addRole(performer, test_gbc, Role.OWNER);
        World test = new World("test_world", SpatialMap.MAP);
        /*
        //add World
        try {
            test_gbc.createWorld(performer.getUserId(), "test_world", SpatialMap.MAP);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Assert.assertEquals(1, test_gbc.getWorlds().size());

         */
        this.context_database.addWorld(test);
        //Pruefe ob eine Welt echt hinzugefuegt ist
        try {
            Connection con = DriverManager.getConnection(dbURL);
            Statement st = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
            ResultSet res = st.executeQuery("SELECT * FROM WORLDS WHERE WORLD_NAME = '" + test.getContextName() + "'");
            int count = 0;
            if (res.next()) {
                count ++;
            }

            Assert.assertEquals(1, count);
            res.first();
            String real_world_name = res.getString("WORLD_NAME");
            String real_map_name = res.getString("MAP_NAME");

            Assert.assertEquals("test_world", real_world_name);
            Assert.assertEquals(SpatialMap.MAP.name(), real_map_name);

            st.close();
            con.close();
        } catch (SQLException e) {
            System.out.print("Fehler in deleteData: " + e);
        }

        //remove
        this.context_database.removeWorld(test);
        try {
            Connection con = DriverManager.getConnection(dbURL);
            Statement st = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
            ResultSet res = st.executeQuery("SELECT * FROM WORLDS WHERE WORLD_NAME = '" + test.getContextName() + "'");
            int count = 0;
            if (res.next()) {
                count ++;
            }

            Assert.assertEquals(0, count);
            st.close();
            con.close();
        } catch (SQLException e) {
            System.out.print("Fehler in deleteData: " + e);
        }





    }

    @Test
    public void getWorldTest() {
        //GlobalContext test_gbc = GlobalContext.getInstance();
        //User performer = this.account_database.createAccount("performer", "111");
        //this.user_database.addRole(performer, test_gbc, Role.OWNER);
        World test = new World("test_world", SpatialMap.MAP);
        //add
        this.context_database.addWorld(test);

        //getWorld
        World real = this.context_database.getWorld(test.getContextId());
        Assert.assertEquals(test, real);


    }

    @Test
    public void getWorldsTest() {
        World test1 = new World("test1_world", SpatialMap.MAP);
        World test2 = new World("test2_world", SpatialMap.MAP);
        this.context_database.addWorld(test1);
        this.context_database.addWorld(test2);

        //getWorlds
        Map<ContextID, World> real_worlds = this.context_database.getWorlds();
        Assert.assertEquals(2, real_worlds.size());
        Assert.assertEquals("test1_world", real_worlds.get(test1.getContextId()).getContextName());
        Assert.assertEquals("test2_world", real_worlds.get(test2.getContextId()).getContextName());


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
