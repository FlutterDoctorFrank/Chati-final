package model.context.spatial;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.mock.graphics.MockGL20;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import controller.network.ClientSender;
import model.context.ContextID;
import model.context.global.GlobalContext;
import model.database.Database;
import model.database.IContextDatabase;
import model.database.IUserAccountManagerDatabase;
import model.database.IUserDatabase;
import model.role.Role;
import model.user.User;
import model.user.account.UserAccountManager;
import org.jetbrains.annotations.NotNull;
import org.junit.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class WorldTest {

    private World test_world;
    private IContextDatabase context_database;
    private IUserDatabase user_database;
    private IUserAccountManagerDatabase account_database;
    private static final String dbURL = "jdbc:derby:ChatiDB;create=true";
    private GlobalContext globalContext;
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
        this.context_database = Database.getContextDatabase();
        this.user_database = Database.getUserDatabase();
        this.account_database = Database.getUserAccountManagerDatabase();
        this.test_world = new World("test_world", ContextMap.PUBLIC_ROOM_MAP);
        this.globalContext = GlobalContext.getInstance();
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

        globalContext.load();
        userAccountManager.load();

    }


    @Test
    public void addPrivateRoomTest() {
        Room test_room = new Room("test_room", this.test_world, ContextMap.PUBLIC_ROOM_MAP,
                "11111");
        this.test_world.addPrivateRoom(test_room);
        Assert.assertEquals(1, this.test_world.getPrivateRooms().size());
        ContextID actual_room_id = test_room.getContextId();
        Assert.assertEquals(test_room, this.test_world.getPrivateRooms().get(actual_room_id));
        Assert.assertEquals(test_room, this.test_world.getChildren().get(actual_room_id));

        //test auch containsPrivateRoom
        Assert.assertTrue(this.test_world.containsPrivateRoom(test_room));



    }

    @Test
    public void removePrivateRoomTest() {
        // zuerst add ein Raum
        Room test_room = new Room("test_room", this.test_world, ContextMap.PUBLIC_ROOM_MAP,
                "11111");
        this.test_world.addPrivateRoom(test_room);
        Assert.assertEquals(1, this.test_world.getPrivateRooms().size());
        ContextID actual_room_id = test_room.getContextId();
        Assert.assertEquals(test_room, this.test_world.getPrivateRooms().get(actual_room_id));

        // remove
        this.test_world.removePrivateRoom(test_room);
        Assert.assertEquals(0, this.test_world.getPrivateRooms().size());
    }

    static class TestClientSender implements ClientSender {
        public void send(@NotNull SendAction sendAction, @NotNull Object object) {

        }
    }


    @Test
    public void addAndRemoveUserTest() {
        try {
            userAccountManager.registerUser("performer", "22222");
            User performer = userAccountManager.getUser("performer");
            performer.addRole(globalContext, Role.OWNER);
            globalContext.createWorld(performer.getUserId(), "test_world", ContextMap.PUBLIC_ROOM_MAP);
            ContextID newworld_id = globalContext.getIWorlds().keySet().iterator().next();
            test_world = globalContext.getWorld(newworld_id);
        } catch (Exception e) {
            e.printStackTrace();
        }

        UserAccountManager userAccountManager = UserAccountManager.getInstance();
        TestClientSender testClientSender = new TestClientSender();
        try {
            userAccountManager.registerUser("addUser", "11111");
            User test_user = userAccountManager.loginUser("addUser", "11111", testClientSender);
            test_user.joinWorld(test_world.getContextId());
            this.test_world.addUser(test_user);
            Assert.assertTrue(this.test_world.getUsers().containsKey(test_user.getUserId()));

            this.test_world.removeUser(test_user);
            Assert.assertFalse(this.test_world.getUsers().containsKey(test_user.getUserId()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Test fuer add/removeReportedUser bei Context
    @Test
    public void reportedUserTest() {

        try {
            userAccountManager.registerUser("r_performer", "22222");
            User performer = userAccountManager.getUser("r_performer");
            performer.addRole(globalContext, Role.OWNER);
            globalContext.createWorld(performer.getUserId(), "test_world", ContextMap.PUBLIC_ROOM_MAP);
            ContextID newworld_id = globalContext.getIWorlds().keySet().iterator().next();
            test_world = globalContext.getWorld(newworld_id);

            userAccountManager.registerUser("reported", "11111");
            User reported = userAccountManager.getUser("reported");

            test_world.addReportedUser(reported);
            Assert.assertTrue(test_world.getReportedUsers().containsValue(reported));

            test_world.removeReportedUser(reported);
            Assert.assertFalse(test_world.getReportedUsers().containsValue(reported));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Test fuer add/removeMutedUser bei Context
    @Test
    public void mutedUserTest() {

        try {
            userAccountManager.registerUser("m_performer", "22222");
            User performer = userAccountManager.getUser("m_performer");
            performer.addRole(globalContext, Role.OWNER);
            globalContext.createWorld(performer.getUserId(), "test_world", ContextMap.PUBLIC_ROOM_MAP);
            ContextID newworld_id = globalContext.getIWorlds().keySet().iterator().next();
            test_world = globalContext.getWorld(newworld_id);

            userAccountManager.registerUser("muted", "11111");
            User muted = userAccountManager.getUser("muted");

            test_world.addMutedUser(muted);
            Assert.assertTrue(test_world.getMutedUsers().containsValue(muted));

            test_world.removeMutedUser(muted);
            Assert.assertFalse(test_world.getMutedUsers().containsValue(muted));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Test fuer add/removeBannedUser bei Context
    @Test
    public void bannedUserTest() {
        try {
            userAccountManager.registerUser("b_performer", "22222");
            User performer = userAccountManager.getUser("b_performer");
            performer.addRole(globalContext, Role.OWNER);
            globalContext.createWorld(performer.getUserId(), "test_world", ContextMap.PUBLIC_ROOM_MAP);
            ContextID newworld_id = globalContext.getIWorlds().keySet().iterator().next();
            test_world = globalContext.getWorld(newworld_id);

            userAccountManager.registerUser("banned", "11111");
            User banned = userAccountManager.getUser("banned");

            test_world.addBannedUser(banned);
            Assert.assertTrue(test_world.getBannedUsers().containsValue(banned));

            test_world.removeBannedUser(banned);
            Assert.assertFalse(test_world.getBannedUsers().containsValue(banned));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
