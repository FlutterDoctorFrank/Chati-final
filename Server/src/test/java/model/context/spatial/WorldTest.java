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
import model.user.User;
import model.user.account.UserAccountManager;
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
        this.test_world = new World("test_world", SpatialMap.MAP);

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
        Room test_room = new Room("test_room", this.test_world, this.test_world, SpatialMap.MAP,
                "11111");
        this.test_world.addPrivateRoom(test_room);
        Assert.assertEquals(1, this.test_world.getPrivateRooms().size());
        ContextID actual_room_id = test_room.getContextId();
        Assert.assertEquals(test_room, this.test_world.getPrivateRooms().get(actual_room_id));

        //test auch containsPrivateRoom
        Assert.assertTrue(this.test_world.containsPrivateRoom(test_room));



    }

    @Test
    public void removePrivateRoomTest() {
        // zuerst add ein Raum
        Room test_room = new Room("test_room", this.test_world, this.test_world, SpatialMap.MAP,
                "11111");
        this.test_world.addPrivateRoom(test_room);
        Assert.assertEquals(1, this.test_world.getPrivateRooms().size());
        ContextID actual_room_id = test_room.getContextId();
        Assert.assertEquals(test_room, this.test_world.getPrivateRooms().get(actual_room_id));

        // remove
        this.test_world.removePrivateRoom(test_room);
        Assert.assertEquals(0, this.test_world.getPrivateRooms().size());
    }

    class TestClientSender implements ClientSender {
        public void send(SendAction sendAction, Object object) {

        }
    }

    /*
    @Test
    public void addUserTest() {

        UserAccountManager userAccountManager = UserAccountManager.getInstance();
        WorldTest.TestClientSender testClientSender = new TestClientSender();
        try {
            userAccountManager.registerUser("addUser", "11111");
            User test_user = userAccountManager.loginUser("addUser", "11111", testClientSender);
            this.test_world.addUser(test_user);
        } catch (Exception e) {
            e.printStackTrace();
        }




    }

     */



}
