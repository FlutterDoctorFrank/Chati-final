package model.notification;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.mock.graphics.MockGL20;
import controller.network.ClientSender;
import model.communication.CommunicationMedium;
import model.context.ContextID;
import model.context.global.GlobalContext;
import model.context.spatial.ContextMap;
import model.context.spatial.Room;
import model.context.spatial.World;
import model.exception.ContextNotFoundException;
import model.exception.IllegalAccountActionException;
import model.exception.IllegalNotificationActionException;
import model.exception.IllegalWorldActionException;
import model.role.Role;
import model.user.User;
import model.user.account.UserAccountManager;
import org.junit.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class RoomInvitationTest {
    RoomInvitation test_ri;
    User sender;
    User receiver;
    UserAccountManager userAccountManager;
    GlobalContext globalContext;
    World test_world;
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
        try {
            this.userAccountManager = UserAccountManager.getInstance();
            this.globalContext = GlobalContext.getInstance();

            userAccountManager.registerUser("performer", "22222");
            User performer = userAccountManager.getUser("performer");
            performer.addRole(globalContext, Role.OWNER);
            globalContext.createWorld(performer.getUserId(), "test_world", ContextMap.PUBLIC_ROOM_MAP);
            ContextID newworld_id = globalContext.getIWorlds().keySet().iterator().next();
            test_world = globalContext.getWorld(newworld_id);

        } catch (Exception e) {
            e.printStackTrace();
        }
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
        this.userAccountManager.load();
        this.globalContext.load();

    }

    class TestClientSender implements ClientSender {
        public void send(SendAction sendAction, Object object) {

        }
    }

    // Receiver nicht mehr in dieser Welt
    @Test(expected = IllegalStateException.class)
    public void falseAccept1() {
        TestClientSender testClientSender = new TestClientSender();
        try {
            this.userAccountManager.registerUser("accept1", "11111");
            sender = UserAccountManager.getInstance().loginUser("accept1", "11111", testClientSender);
            this.userAccountManager.registerUser("receiver1", "22222");
            receiver = UserAccountManager.getInstance().loginUser("receiver1", "22222", testClientSender);
            sender.joinWorld(this.test_world.getContextId());
            receiver.joinWorld(this.test_world.getContextId());
            Room test_room = new Room("test_room", this.test_world, ContextMap.PRIVATE_ROOM_MAP,
                    "11111");
            test_room.build();
            this.test_world.addPrivateRoom(test_room);
            Assert.assertTrue(test_room.canCommunicateWith(CommunicationMedium.TEXT));
            Assert.assertTrue(test_room.canCommunicateWith(CommunicationMedium.VOICE));
            this.sender.addRole(test_room, Role.ROOM_OWNER);
            this.sender.teleport(test_room.getSpawnLocation());

            test_ri = new RoomInvitation(this.receiver, "hi", this.sender, test_room);
            this.receiver.addNotification(test_ri);
            Assert.assertEquals(1, this.receiver.getWorldNotifications().size());

            this.receiver.logout();
            this.test_ri.accept();

        } catch (IllegalAccountActionException | ContextNotFoundException | IllegalWorldActionException
                | IllegalNotificationActionException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void falseAccept2() {
        TestClientSender testClientSender = new TestClientSender();
        try {
            this.userAccountManager.registerUser("accept2", "11111");
            sender = UserAccountManager.getInstance().loginUser("accept2", "11111", testClientSender);
            this.userAccountManager.registerUser("receiver2", "22222");
            receiver = UserAccountManager.getInstance().loginUser("receiver2", "22222", testClientSender);
            sender.joinWorld(this.test_world.getContextId());
            receiver.joinWorld(this.test_world.getContextId());
            Room test_room = new Room("test_room", this.test_world, ContextMap.PRIVATE_ROOM_MAP,
                    "11111");
            test_room.build();
            this.test_world.addPrivateRoom(test_room);
            this.sender.addRole(test_room, Role.ROOM_OWNER);
            this.sender.teleport(test_room.getSpawnLocation());

            test_ri = new RoomInvitation(this.receiver, "hi", this.sender, test_room);
            this.receiver.addNotification(test_ri);
            Assert.assertEquals(1, this.receiver.getWorldNotifications().size());

            this.sender.teleport(this.test_world.getPublicRoom().getSpawnLocation());
            this.test_ri.accept();

            Assert.assertNotEquals("test_room", this.receiver.getLocation().getRoom().getContextName());

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    public void falseAccept3() {
        TestClientSender testClientSender = new TestClientSender();
        try {
            this.userAccountManager.registerUser("accept3", "11111");
            sender = UserAccountManager.getInstance().loginUser("accept3", "11111", testClientSender);
            this.userAccountManager.registerUser("receiver3", "22222");
            receiver = UserAccountManager.getInstance().loginUser("receiver3", "22222", testClientSender);
            sender.joinWorld(this.test_world.getContextId());
            receiver.joinWorld(this.test_world.getContextId());
            Room test_room = new Room("test_room", this.test_world, ContextMap.PRIVATE_ROOM_MAP,
                    "11111");
            test_room.build();
            this.test_world.addPrivateRoom(test_room);
            //this.sender.teleport(test_room.getSpawnLocation());

            test_ri = new RoomInvitation(this.receiver, "hi", this.sender, test_room);
            this.receiver.addNotification(test_ri);
            Assert.assertEquals(1, this.receiver.getWorldNotifications().size());

            this.sender.teleport(this.test_world.getPublicRoom().getSpawnLocation());
            this.test_ri.accept();

            Assert.assertNotEquals("test_room", this.receiver.getLocation().getRoom().getContextName());

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    public void declineTest() {
        TestClientSender testClientSender = new TestClientSender();
        try {
            this.userAccountManager.registerUser("decline1", "11111");
            sender = UserAccountManager.getInstance().loginUser("decline1", "11111", testClientSender);
            this.userAccountManager.registerUser("receiverd", "22222");
            receiver = UserAccountManager.getInstance().loginUser("receiverd", "22222", testClientSender);
            sender.joinWorld(this.test_world.getContextId());
            receiver.joinWorld(this.test_world.getContextId());
            Room test_room = new Room("test_room", this.test_world, ContextMap.PRIVATE_ROOM_MAP,
                    "11111");
            test_room.build();
            this.test_world.addPrivateRoom(test_room);
            //this.sender.teleport(test_room.getSpawnLocation());

            test_ri = new RoomInvitation(this.receiver, "hi", this.sender, test_room);
            this.receiver.addNotification(test_ri);
            Assert.assertEquals(1, this.receiver.getWorldNotifications().size());

            this.test_ri.decline();

            Assert.assertEquals(1, this.sender.getWorldNotifications().size());

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
