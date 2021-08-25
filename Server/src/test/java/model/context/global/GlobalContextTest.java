package model.context.global;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.mock.graphics.MockGL20;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import model.communication.CommunicationMedium;
import model.context.Context;
import model.context.ContextID;
import model.context.spatial.SpatialMap;
import model.database.Database;
import model.database.IContextDatabase;
import model.database.IUserAccountManagerDatabase;
import model.database.IUserDatabase;
import model.role.Role;
import model.user.User;
import model.user.account.UserAccountManager;
import org.junit.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.UUID;

public class GlobalContextTest {

    private GlobalContext globalContext;
    private static final String dbURL = "jdbc:derby:ChatiDB;create=true";
    private IUserAccountManagerDatabase account_database;
    private IUserDatabase user_database;
    private IContextDatabase context_database;
    private UserAccountManager userAccountManager;

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
    public void setUp() {
        this.globalContext = GlobalContext.getInstance();
        this.account_database = Database.getUserAccountManagerDatabase();
        this.user_database = Database.getUserDatabase();
        this.context_database = Database.getContextDatabase();
        this.userAccountManager = UserAccountManager.getInstance();

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
    public void createWorldTest() {
        try {
            this.userAccountManager.registerUser("createWorld", "11111");
            User performer = this.userAccountManager.getUser("createWorld");
            performer.addRole(this.globalContext, Role.OWNER);
            UUID performer_id = performer.getUserId();
            this.globalContext.createWorld(performer_id, "testWorld", SpatialMap.PUBLIC_ROOM_MAP);

            Assert.assertEquals(1, this.globalContext.getWorlds().size());
            ContextID actual_world_id = this.globalContext.getWorlds().keySet().iterator().next();
            Assert.assertEquals("testWorld", this.globalContext.getWorld(actual_world_id).getContextName());
            //auch mit getChildren Methode
            Assert.assertEquals(1, this.globalContext.getChildren().size());
            Assert.assertTrue(this.globalContext.getChildren().containsKey(actual_world_id));
            //test auch getContext
            Context getContext = this.globalContext.getContext(actual_world_id);
            Assert.assertEquals("testWorld", getContext.getContextName());
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Test
    public void removeWorldTest() {
        try {
            //zuerst addWorld
            this.userAccountManager.registerUser("removeWorld", "11111");
            User performer = this.userAccountManager.getUser("removeWorld");
            performer.addRole(this.globalContext, Role.OWNER);
            UUID performer_id = performer.getUserId();
            this.globalContext.createWorld(performer_id, "testWorld", SpatialMap.PUBLIC_ROOM_MAP);

            Assert.assertEquals(1, this.globalContext.getWorlds().size());
            ContextID actual_world_id = this.globalContext.getWorlds().keySet().iterator().next();
            Assert.assertEquals("testWorld", this.globalContext.getWorld(actual_world_id).getContextName());

            Assert.assertEquals(1, this.globalContext.getChildren().size());
            Assert.assertTrue(this.globalContext.getChildren().containsKey(actual_world_id));

            //Nach erfolgreiche addWorld, remove
            this.globalContext.removeWorld(performer_id, actual_world_id);
            Assert.assertEquals(0, this.globalContext.getWorlds().size());
            Assert.assertEquals(0, this.globalContext.getChildren().size());

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Test
    public void getCommunicableUsersTest() {
        try {
            this.userAccountManager.registerUser("test", "11111");
            User test = this.userAccountManager.getUser("test");

            Map<UUID, User> test_map = this.globalContext.getCommunicableUsers(test);
            Assert.assertEquals(0, test_map.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void canCommunicateWithTest() {
        Assert.assertFalse(this.globalContext.canCommunicateWith(CommunicationMedium.TEXT));
        Assert.assertFalse(this.globalContext.canCommunicateWith(CommunicationMedium.VOICE));
    }
}
