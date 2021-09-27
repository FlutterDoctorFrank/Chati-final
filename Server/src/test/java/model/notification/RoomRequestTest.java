package model.notification;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.mock.graphics.MockGL20;
import controller.network.ClientSender;
import model.context.ContextID;
import model.context.global.GlobalContext;
import model.context.spatial.Room;
import model.context.spatial.ContextMap;
import model.context.spatial.World;
import model.exception.IllegalNotificationActionException;
import model.exception.NotificationNotFoundException;
import model.role.Role;
import model.user.User;
import model.user.account.UserAccountManager;
import org.jetbrains.annotations.NotNull;
import org.junit.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class RoomRequestTest {
    RoomRequest test_rr;
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
        public void send(@NotNull SendAction sendAction, @NotNull Object object) {

        }
    }

    @Test
    public void normalAcceptTest() {
        TestClientSender testClientSender = new TestClientSender();
        try {
            this.userAccountManager.registerUser("rrn_sender", "11111");
            sender = UserAccountManager.getInstance().loginUser("rrn_sender", "11111", testClientSender);
            sender.getGlobalNotifications().keySet().forEach(uuid -> {
                try {
                    sender.manageNotification(uuid, NotificationAction.DELETE);
                } catch (NotificationNotFoundException | IllegalNotificationActionException e) {
                    Assert.fail("Failed in delete initialized notifications");
                }
            });
            this.userAccountManager.registerUser("rrn_receiver", "22222");
            receiver = UserAccountManager.getInstance().loginUser("rrn_receiver", "22222", testClientSender);
            Room test_room = new Room("test_room", test_world, ContextMap.PRIVATE_ROOM_MAP, "11111");
            sender.joinWorld(test_world.getContextId());
            receiver.joinWorld(test_world.getContextId());
            this.test_world.addPrivateRoom(test_room);
            receiver.addRole(test_room, Role.ROOM_OWNER);
            test_rr = new RoomRequest(receiver, "hallo", sender, test_room);
            if (!receiver.getGlobalNotifications().values().contains(test_rr)) {
                receiver.addNotification(test_rr);
            }
            Assert.assertEquals(1, this.receiver.getWorldNotifications().size());
            Assert.assertEquals(0, this.sender.getWorldNotifications().size());

            this.test_rr.accept();

            Assert.assertEquals(1, this.sender.getWorldNotifications().size());

        } catch (Exception e) {
            e.printStackTrace();

        }

    }

    // Falls Raum nicht mehr existiert
    @Test
    public void roomNotExistAcceptTest() {
        TestClientSender testClientSender = new TestClientSender();
        try {
            this.userAccountManager.registerUser("rrn_sender", "11111");
            sender = UserAccountManager.getInstance().loginUser("rrn_sender", "11111", testClientSender);
            sender.getGlobalNotifications().keySet().forEach(uuid -> {
                try {
                    sender.manageNotification(uuid, NotificationAction.DELETE);
                } catch (NotificationNotFoundException | IllegalNotificationActionException e) {
                    Assert.fail("Failed in delete initialized notifications");
                }
            });
            this.userAccountManager.registerUser("rrn_receiver", "22222");
            receiver = UserAccountManager.getInstance().loginUser("rrn_receiver", "22222", testClientSender);
            Room test_room = new Room("test_room", test_world, ContextMap.PRIVATE_ROOM_MAP, "11111");
            sender.joinWorld(test_world.getContextId());
            receiver.joinWorld(test_world.getContextId());
            this.test_world.addPrivateRoom(test_room);
            receiver.addRole(test_room, Role.ROOM_OWNER);
            test_rr = new RoomRequest(receiver, "hallo", sender, test_room);
            if (!receiver.getGlobalNotifications().values().contains(test_rr)) {
                receiver.addNotification(test_rr);
            }
            Assert.assertEquals(1, this.receiver.getWorldNotifications().size());
            Assert.assertEquals(0, this.sender.getWorldNotifications().size());

            this.test_world.removePrivateRoom(test_room);
            this.test_rr.accept();

            Assert.assertEquals(0, this.sender.getWorldNotifications().size());

        } catch (Exception e) {
            e.printStackTrace();

        }

    }

    // If sender doesn't exist
    @Test
    public void notExistSenderAcceptTest() {
        TestClientSender testClientSender = new TestClientSender();
        try {
            this.userAccountManager.registerUser("rrn_sender", "11111");
            sender = UserAccountManager.getInstance().loginUser("rrn_sender", "11111", testClientSender);
            this.userAccountManager.registerUser("rrn_receiver", "22222");
            receiver = UserAccountManager.getInstance().loginUser("rrn_receiver", "22222", testClientSender);
            Room test_room = new Room("test_room", test_world, ContextMap.PRIVATE_ROOM_MAP, "11111");
            sender.joinWorld(test_world.getContextId());
            receiver.joinWorld(test_world.getContextId());
            this.test_world.addPrivateRoom(test_room);
            receiver.addRole(test_room, Role.ROOM_OWNER);
            test_rr = new RoomRequest(receiver, "hallo", sender, test_room);
            if (!receiver.getGlobalNotifications().values().contains(test_rr)) {
                receiver.addNotification(test_rr);
            }
            Assert.assertEquals(1, this.receiver.getWorldNotifications().size());
            Assert.assertEquals(0, this.sender.getWorldNotifications().size());

            UserAccountManager.getInstance().deleteUser(this.sender);
            this.test_rr.accept();


        } catch (Exception e) {
            e.printStackTrace();

        }

    }

    @Test
    public void normalDeclineTest() {
        TestClientSender testClientSender = new TestClientSender();
        try {
            this.userAccountManager.registerUser("rrn_sender", "11111");
            sender = UserAccountManager.getInstance().loginUser("rrn_sender", "11111", testClientSender);
            sender.getGlobalNotifications().keySet().forEach(uuid -> {
                try {
                    sender.manageNotification(uuid, NotificationAction.DELETE);
                } catch (NotificationNotFoundException | IllegalNotificationActionException e) {
                    Assert.fail("Failed in delete initialized notifications");
                }
            });
            this.userAccountManager.registerUser("rrn_receiver", "22222");
            receiver = UserAccountManager.getInstance().loginUser("rrn_receiver", "22222", testClientSender);
            Room test_room = new Room("test_room", test_world, ContextMap.PRIVATE_ROOM_MAP, "11111");
            sender.joinWorld(test_world.getContextId());
            receiver.joinWorld(test_world.getContextId());
            this.test_world.addPrivateRoom(test_room);
            receiver.addRole(test_room, Role.ROOM_OWNER);
            test_rr = new RoomRequest(receiver, "hallo", sender, test_room);
            if (!receiver.getGlobalNotifications().values().contains(test_rr)) {
                receiver.addNotification(test_rr);
            }
            Assert.assertEquals(1, this.receiver.getWorldNotifications().size());
            Assert.assertEquals(0, this.sender.getWorldNotifications().size());

            this.test_rr.decline();

            Assert.assertEquals(0, this.sender.getGlobalNotifications().size());

        } catch (Exception e) {
            e.printStackTrace();

        }

    }

    // Falls Raum nicht mehr existiert
    @Test
    public void roomNotExistDeclineTest() {
        TestClientSender testClientSender = new TestClientSender();
        try {
            this.userAccountManager.registerUser("rrn_sender", "11111");
            sender = UserAccountManager.getInstance().loginUser("rrn_sender", "11111", testClientSender);
            sender.getGlobalNotifications().keySet().forEach(uuid -> {
                try {
                    sender.manageNotification(uuid, NotificationAction.DELETE);
                } catch (NotificationNotFoundException | IllegalNotificationActionException e) {
                    Assert.fail("Failed in delete initialized notifications");
                }
            });
            this.userAccountManager.registerUser("rrn_receiver", "22222");
            receiver = UserAccountManager.getInstance().loginUser("rrn_receiver", "22222", testClientSender);
            Room test_room = new Room("test_room", test_world, ContextMap.PRIVATE_ROOM_MAP, "11111");
            sender.joinWorld(test_world.getContextId());
            receiver.joinWorld(test_world.getContextId());
            this.test_world.addPrivateRoom(test_room);
            receiver.addRole(test_room, Role.ROOM_OWNER);
            test_rr = new RoomRequest(receiver, "hallo", sender, test_room);
            if (!receiver.getGlobalNotifications().values().contains(test_rr)) {
                receiver.addNotification(test_rr);
            }
            Assert.assertEquals(1, this.receiver.getWorldNotifications().size());
            Assert.assertEquals(0, this.sender.getWorldNotifications().size());

            this.test_world.removePrivateRoom(test_room);
            this.test_rr.decline();

            Assert.assertEquals(0, this.sender.getWorldNotifications().size());

        } catch (Exception e) {
            e.printStackTrace();

        }

    }

    // If sender doesn't exist
    @Test
    public void notExistSenderDeclineTest() {
        TestClientSender testClientSender = new TestClientSender();
        try {
            this.userAccountManager.registerUser("rrn_sender", "11111");
            sender = UserAccountManager.getInstance().loginUser("rrn_sender", "11111", testClientSender);
            this.userAccountManager.registerUser("rrn_receiver", "22222");
            receiver = UserAccountManager.getInstance().loginUser("rrn_receiver", "22222", testClientSender);
            Room test_room = new Room("test_room", test_world, ContextMap.PRIVATE_ROOM_MAP, "11111");
            sender.joinWorld(test_world.getContextId());
            receiver.joinWorld(test_world.getContextId());
            this.test_world.addPrivateRoom(test_room);
            receiver.addRole(test_room, Role.ROOM_OWNER);
            test_rr = new RoomRequest(receiver, "hallo", sender, test_room);
            if (!receiver.getGlobalNotifications().values().contains(test_rr)) {
                receiver.addNotification(test_rr);
            }
            Assert.assertEquals(1, this.receiver.getWorldNotifications().size());
            Assert.assertEquals(0, this.sender.getWorldNotifications().size());
            UserAccountManager.getInstance().deleteUser(this.sender);
            this.test_rr.decline();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
