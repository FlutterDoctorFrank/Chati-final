package model.context.spatial;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backend.headless.mock.graphics.MockGL20;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import controller.network.ClientSender;
import model.context.ContextID;
import model.context.global.GlobalContext;
import model.context.spatial.Room;
import model.context.spatial.SpatialMap;
import model.context.spatial.World;
import model.database.Database;
import model.database.IContextDatabase;
import model.database.IUserAccountManagerDatabase;
import model.database.IUserDatabase;
import model.notification.RoomRequest;
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
        this.test_world = new World("test_world", SpatialMap.PUBLIC_ROOM_MAP);
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

    }


    @Test
    public void addPrivateRoomTest() {
        Room test_room = new Room("test_room", this.test_world, SpatialMap.PUBLIC_ROOM_MAP,
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
        Room test_room = new Room("test_room", this.test_world, SpatialMap.PUBLIC_ROOM_MAP,
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
    public void addUserTest() {
        userAccountManager = UserAccountManager.getInstance();
        globalContext = GlobalContext.getInstance();
        try {
            userAccountManager.registerUser("performer", "22222");
            User performer = userAccountManager.getUser("performer");
            performer.addRole(globalContext, Role.OWNER);
            globalContext.createWorld(performer.getUserId(), "test_world", SpatialMap.PUBLIC_ROOM_MAP);
            ContextID newworld_id = globalContext.getWorlds().keySet().iterator().next();
            test_world = globalContext.getWorld(newworld_id);
        } catch (Exception e) {
            e.printStackTrace();
        }

        UserAccountManager userAccountManager = UserAccountManager.getInstance();
        WorldTest.TestClientSender testClientSender = new TestClientSender();
        try {
            Thread.sleep(1500);
            userAccountManager.registerUser("addUser", "11111");
            User test_user = userAccountManager.loginUser("addUser", "11111", testClientSender);
            test_user.joinWorld(test_world.getContextId());
            this.test_world.addUser(test_user);

            Assert.assertTrue(this.test_world.getUsers().containsKey(test_user.getUserId()));
        } catch (Exception e) {
            e.printStackTrace();
        }




    }





}
