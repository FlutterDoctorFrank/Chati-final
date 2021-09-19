package model.user;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.mock.graphics.MockGL20;
import controller.network.ClientSender;
import model.context.ContextID;
import model.context.global.GlobalContext;
import model.context.spatial.ContextMap;
import model.context.spatial.IWorld;
import model.context.spatial.Room;
import model.context.spatial.World;
import model.database.Database;
import model.database.IContextDatabase;
import model.database.IUserAccountManagerDatabase;
import model.database.IUserDatabase;
import model.exception.*;
import model.notification.INotification;
import model.notification.NotificationAction;
import model.notification.NotificationType;
import model.role.Role;
import model.user.account.UserAccountManager;
import org.junit.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;

public class AdministrativeActionTest {

    private User user;

    private IUserAccountManagerDatabase account_database;
    private IUserDatabase user_database;
    private IContextDatabase context_database;
    private static final String dbURL = "jdbc:derby:ChatiDB;create=true";
    private World test_world;
    private UserAccountManager userAccountManager;
    private GlobalContext globalContext;

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
        this.userAccountManager = UserAccountManager.getInstance();
        this.globalContext = GlobalContext.getInstance();
        this.account_database = Database.getUserAccountManagerDatabase();
        this.user_database = Database.getUserDatabase();
        this.context_database = Database.getContextDatabase();
        this.test_world = new World("test_world", ContextMap.PUBLIC_ROOM_MAP);




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

        userAccountManager.load();
        globalContext.load();
/*
        if (userAccountManager.isRegistered(this.user.getUserId())) {
            try {
                userAccountManager.deleteUser(user);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

 */


    }


    class TestClientSender implements ClientSender {
        public void send(SendAction sendAction, Object object) {

        }
    }

    private void setTestWorld(String performerName) {
        userAccountManager = UserAccountManager.getInstance();
        globalContext = GlobalContext.getInstance();
        try {
            userAccountManager.registerUser(performerName, "22222");
            User performer = userAccountManager.getUser(performerName);
            performer.addRole(globalContext, Role.OWNER);
            if (globalContext.getIWorlds().size() == 0) {
                globalContext.createWorld(performer.getUserId(), "test_world", ContextMap.PUBLIC_ROOM_MAP);
            }
            ContextID newworld_id = globalContext.getIWorlds().keySet().iterator().next();
            test_world = globalContext.getWorld(newworld_id);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void executeFriendTest() {
        TestClientSender testClientSender = new TestClientSender();

        try {

            // Invite Friend
            this.userAccountManager.registerUser("targetF", "11111");
            User target = this.userAccountManager.loginUser("targetF", "11111", testClientSender);
            this.userAccountManager.registerUser("senderF", "22222");
            this.user = this.userAccountManager.loginUser("senderF", "22222", testClientSender);
            String[] args = new String[]{"hallo"};

            this.user.executeAdministrativeAction(target.getUserId(), AdministrativeAction.INVITE_FRIEND, args);

            INotification actual_notif = null;
            Iterator<INotification> iterator = target.getGlobalNotifications().values().iterator();
            while (iterator.hasNext()) {
                INotification notif = iterator.next();
                if (notif.getNotificationType() == NotificationType.FRIEND_REQUEST) {
                    actual_notif = notif;
                    break;
                }
            }
            //Assert.assertEquals("friendRequestKey", actual_notif.getMessageBundle().getMessageKey());
            Assert.assertNotNull(actual_notif);
            Assert.assertEquals("senderF", actual_notif.getMessageBundle().getArguments()[0]);
            Assert.assertEquals("hallo", actual_notif.getMessageBundle().getArguments()[1]);


            // Remove Friend
            this.user.addFriend(target);
            target.addFriend(this.user);
            args = new String[]{};

            this.user.executeAdministrativeAction(target.getUserId(), AdministrativeAction.REMOVE_FRIEND, args);
            Assert.assertEquals(0, this.user.getFriends().size());
            Assert.assertEquals(0, target.getFriends().size());

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Test
    public void executeIgnoreTest() {
        TestClientSender testClientSender = new TestClientSender();

        try {

            this.userAccountManager.registerUser("targetI", "11111");
            User target = this.userAccountManager.loginUser("targetI", "11111", testClientSender);
            this.userAccountManager.registerUser("senderI", "22222");
            this.user = this.userAccountManager.loginUser("senderI", "22222", testClientSender);


            // Ignore User
            this.user.addFriend(target);
            target.addFriend(this.user);
            Assert.assertEquals(1, this.user.getFriends().size());
            Assert.assertEquals(1, target.getFriends().size());
            String[] args = new String[]{};
            this.user.executeAdministrativeAction(target.getUserId(), AdministrativeAction.IGNORE_USER, args);
            Assert.assertEquals(0, this.user.getFriends().size());
            Assert.assertEquals(0, target.getFriends().size());
            Assert.assertEquals(1, this.user.getIgnoredUsers().size());
            Assert.assertTrue(this.user.getIgnoredUsers().keySet().contains(target.getUserId()));


            // Unignore User
            this.user.executeAdministrativeAction(target.getUserId(), AdministrativeAction.UNIGNORE_USER, args);
            Assert.assertEquals(0, this.user.getIgnoredUsers().size());

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    public void executeRoomInviteKickTest() {
        TestClientSender testClientSender = new TestClientSender();

        try {

            this.userAccountManager.registerUser("roomOwner", "22222");
            this.user = this.userAccountManager.loginUser("roomOwner", "22222", testClientSender);
            this.userAccountManager.registerUser("targetR", "11111");
            User target = this.userAccountManager.loginUser("targetR", "11111", testClientSender);

            setTestWorld("execRoom");

            IWorld test_world = null;
            ContextID world_id = null;
            Iterator<IWorld> iterator = globalContext.getIWorlds().values().iterator();
            while (iterator.hasNext()) {
                IWorld world = iterator.next();
                if (world.getContextName() == "test_world") {
                    test_world = world;
                    world_id = world.getContextId();
                    break;
                }
            }
            Assert.assertNotNull(test_world);
            target.joinWorld(world_id);
            this.user.joinWorld(world_id);

            Room test_room = new Room("test_room", this.test_world, ContextMap.PRIVATE_ROOM_MAP,
                    "11111");
            test_room.build();
            this.test_world.addPrivateRoom(test_room);
            this.user.addRole(test_room, Role.ROOM_OWNER);
            this.user.teleport(test_room.getSpawnLocation());

            String[] args = new String[]{"come"};

            // Invite
            this.user.executeAdministrativeAction(target.getUserId(), AdministrativeAction.ROOM_INVITE, args);

            Assert.assertEquals(1, target.getWorldNotifications().size());
            INotification room_invitation = target.getWorldNotifications().values().iterator().next();
            Assert.assertEquals(NotificationType.ROOM_INVITATION,
                    target.getWorldNotifications().values().iterator().next().getNotificationType());

            // Accept Room Invitation
            target.manageNotification(room_invitation.getNotificationId(), NotificationAction.ACCEPT);
            Assert.assertEquals("test_room", target.getLocation().getRoom().getContextName());

            // Kick
            args = new String[]{"out"};
            this.user.executeAdministrativeAction(target.getUserId(), AdministrativeAction.ROOM_KICK, args);
            Assert.assertNotEquals("test_room", target.getLocation().getRoom().getContextName());
            Assert.assertTrue(target.getLocation().getRoom().getMap().isPublicRoomMap());


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    public void executeTeleportTest() {
        TestClientSender testClientSender = new TestClientSender();

        try {

            this.userAccountManager.registerUser("performerB", "22222");
            this.user = this.userAccountManager.loginUser("performerB", "22222", testClientSender);
            this.userAccountManager.registerUser("targetT", "11111");
            User target = this.userAccountManager.loginUser("targetT", "11111", testClientSender);
            this.user.addRole(globalContext, Role.OWNER);
            setTestWorld("executeTele");

            IWorld test_world = null;
            ContextID world_id = null;
            Iterator<IWorld> iterator = globalContext.getIWorlds().values().iterator();
            while (iterator.hasNext()) {
                IWorld world = iterator.next();
                if (world.getContextName() == "test_world") {
                    test_world = world;
                    world_id = world.getContextId();
                    break;
                }
            }
            Assert.assertNotNull(test_world);
            target.joinWorld(world_id);
            this.user.joinWorld(world_id);
            Room test_room = new Room("test_room", this.test_world, ContextMap.PRIVATE_ROOM_MAP,
                    "11111");
            test_room.build();
            this.test_world.addPrivateRoom(test_room);
            target.addRole(test_room, Role.ROOM_OWNER);
            target.teleport(test_room.getSpawnLocation());

            String[] args = new String[]{"hi"};

            // Teleport
            this.user.executeAdministrativeAction(target.getUserId(), AdministrativeAction.TELEPORT_TO_USER, args);
            Assert.assertEquals("test_room", this.user.getLocation().getRoom().getContextName());

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    public void executeReportTest() {
        TestClientSender testClientSender = new TestClientSender();

        try {

            this.userAccountManager.registerUser("performerR", "22222");
            this.user = this.userAccountManager.loginUser("performerR", "22222", testClientSender);
            this.userAccountManager.registerUser("targetRo", "11111");
            User target = this.userAccountManager.loginUser("targetRo", "11111", testClientSender);
            setTestWorld("execRep");

            IWorld test_world = null;
            ContextID world_id = null;
            Iterator<IWorld> iterator = globalContext.getIWorlds().values().iterator();
            while (iterator.hasNext()) {
                IWorld world = iterator.next();
                if (world.getContextName() == "test_world") {
                    test_world = world;
                    world_id = world.getContextId();
                    break;
                }
            }
            Assert.assertNotNull(test_world);
            target.joinWorld(world_id);
            this.user.joinWorld(world_id);

            String[] args = new String[]{"schlecht"};

            // Report
            this.user.executeAdministrativeAction(target.getUserId(), AdministrativeAction.REPORT_USER, args);
            Assert.assertEquals(1, this.user.getWorldNotifications().size());
            Assert.assertEquals(this.user.getUsername(),
                    this.user.getWorldNotifications().values().iterator().next().getMessageBundle().getArguments()[0]);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    public void executeMuteTest() {
        TestClientSender testClientSender = new TestClientSender();

        try {

            this.userAccountManager.registerUser("roomOwner", "22222");
            this.user = this.userAccountManager.loginUser("roomOwner", "22222", testClientSender);
            this.userAccountManager.registerUser("targetM", "11111");
            User target = this.userAccountManager.loginUser("targetM", "11111", testClientSender);

            setTestWorld("execMute");

            IWorld test_world = null;
            ContextID world_id = null;
            Iterator<IWorld> iterator = globalContext.getIWorlds().values().iterator();
            while (iterator.hasNext()) {
                IWorld world = iterator.next();
                if (world.getContextName() == "test_world") {
                    test_world = world;
                    world_id = world.getContextId();
                    break;
                }
            }
            Assert.assertNotNull(test_world);
            target.joinWorld(world_id);
            this.user.joinWorld(world_id);

            Room test_room = new Room("test_room", this.test_world, ContextMap.PRIVATE_ROOM_MAP,
                    "11111");
            test_room.build();
            this.test_world.addPrivateRoom(test_room);
            this.user.addRole(test_room, Role.ROOM_OWNER);
            this.user.teleport(test_room.getSpawnLocation());

            String[] args = new String[]{"come"};

            // Zuerst Invite
            this.user.executeAdministrativeAction(target.getUserId(), AdministrativeAction.ROOM_INVITE, args);

            Assert.assertEquals(1, target.getWorldNotifications().size());
            INotification room_invitation = target.getWorldNotifications().values().iterator().next();
            Assert.assertEquals(NotificationType.ROOM_INVITATION,
                    target.getWorldNotifications().values().iterator().next().getNotificationType());

            // Accept Room Invitation
            target.manageNotification(room_invitation.getNotificationId(), NotificationAction.ACCEPT);
            Assert.assertEquals("test_room", target.getLocation().getRoom().getContextName());

            // Mute
            args = new String[]{"mute"};
            this.user.executeAdministrativeAction(target.getUserId(), AdministrativeAction.MUTE_USER, args);
            Assert.assertTrue(test_room.getMutedUsers().containsValue(target));

            // Unmute
            args = new String[]{"alright"};
            this.user.executeAdministrativeAction(target.getUserId(), AdministrativeAction.UNMUTE_USER, args);
            Assert.assertFalse(test_room.getMutedUsers().containsValue(target));

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test(expected = IllegalAdministrativeActionException.class)
    public void executeAlreadyMuteTest() throws Exception{
        TestClientSender testClientSender = new TestClientSender();
        try {
            this.userAccountManager.registerUser("aroomOwner", "22222");
            this.user = this.userAccountManager.loginUser("aroomOwner", "22222", testClientSender);
            this.userAccountManager.registerUser("altargetM", "11111");
            User target = this.userAccountManager.loginUser("altargetM", "11111", testClientSender);

            setTestWorld("execAlMute");

            IWorld test_world = null;
            ContextID world_id = null;
            Iterator<IWorld> iterator = globalContext.getIWorlds().values().iterator();
            while (iterator.hasNext()) {
                IWorld world = iterator.next();
                if (world.getContextName() == "test_world") {
                    test_world = world;
                    world_id = world.getContextId();
                    break;
                }
            }
            Assert.assertNotNull(test_world);
            target.joinWorld(world_id);
            this.user.joinWorld(world_id);

            Room test_room = new Room("test_room", this.test_world, ContextMap.PRIVATE_ROOM_MAP,
                    "11111");
            test_room.build();
            this.test_world.addPrivateRoom(test_room);
            this.user.addRole(test_room, Role.ROOM_OWNER);
            this.user.teleport(test_room.getSpawnLocation());

            String[] args = new String[]{"come"};

            // Zuerst Invite
            this.user.executeAdministrativeAction(target.getUserId(), AdministrativeAction.ROOM_INVITE, args);

            Assert.assertEquals(1, target.getWorldNotifications().size());
            INotification room_invitation = target.getWorldNotifications().values().iterator().next();
            Assert.assertEquals(NotificationType.ROOM_INVITATION,
                    target.getWorldNotifications().values().iterator().next().getNotificationType());

            // Accept Room Invitation
            target.manageNotification(room_invitation.getNotificationId(), NotificationAction.ACCEPT);
            Assert.assertEquals("test_room", target.getLocation().getRoom().getContextName());

            // Mute
            args = new String[]{"mute"};
            this.user.executeAdministrativeAction(target.getUserId(), AdministrativeAction.MUTE_USER, args);
            Assert.assertTrue(test_room.getMutedUsers().containsValue(target));

            // Duplicate Muting
            this.user.executeAdministrativeAction(target.getUserId(), AdministrativeAction.MUTE_USER, args);
            Assert.assertTrue(test_room.getMutedUsers().containsValue(target));

        } catch (IllegalAccountActionException | UserNotFoundException
                | ContextNotFoundException | IllegalWorldActionException | NoPermissionException
                |  NotificationNotFoundException | IllegalNotificationActionException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void executeBanTest() {
        TestClientSender testClientSender = new TestClientSender();
        try {

            this.userAccountManager.registerUser("performerB", "22222");
            this.user = this.userAccountManager.loginUser("performerB", "22222", testClientSender);
            this.userAccountManager.registerUser("targetB", "11111");
            User target = this.userAccountManager.loginUser("targetB", "11111", testClientSender);
            this.user.addRole(globalContext, Role.OWNER);
            setTestWorld("executeBan");

            IWorld test_world = null;
            ContextID world_id = null;
            Iterator<IWorld> iterator = globalContext.getIWorlds().values().iterator();
            while (iterator.hasNext()) {
                IWorld world = iterator.next();
                if (world.getContextName() == "test_world") {
                    test_world = world;
                    world_id = world.getContextId();
                    break;
                }
            }
            Assert.assertNotNull(test_world);
            target.joinWorld(world_id);
            this.user.joinWorld(world_id);

            String[] args = new String[]{"out"};

            // Ban
            this.user.executeAdministrativeAction(target.getUserId(), AdministrativeAction.BAN_USER, args);
            Assert.assertNull(target.getWorld());

            // Unban
            args = new String[]{"in"};
            this.user.executeAdministrativeAction(target.getUserId(), AdministrativeAction.UNBAN_USER, args);
            Assert.assertFalse(test_world.getBannedUsers().containsKey(target.getUserId()));


        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Test
    public void executeRoleSystemTest() {
        TestClientSender testClientSender = new TestClientSender();

        try {

            this.userAccountManager.registerUser("Owner", "22222");
            this.user = this.userAccountManager.loginUser("Owner", "22222", testClientSender);
            this.userAccountManager.registerUser("targetA", "11111");
            User target = this.userAccountManager.loginUser("targetA", "11111", testClientSender);
            this.user.addRole(globalContext, Role.OWNER);
            setTestWorld("execAssign");

            IWorld test_world = null;
            ContextID world_id = null;
            Iterator<IWorld> iterator = globalContext.getIWorlds().values().iterator();
            while (iterator.hasNext()) {
                IWorld world = iterator.next();
                if (world.getContextName() == "test_world") {
                    test_world = world;
                    world_id = world.getContextId();
                    break;
                }
            }
            Assert.assertNotNull(test_world);
            target.joinWorld(world_id);
            this.user.joinWorld(world_id);

            String[] args = new String[]{"gut"};

            // assign Moderator
            this.user.executeAdministrativeAction(target.getUserId(), AdministrativeAction.ASSIGN_MODERATOR, args);
            World world = globalContext.getWorld(test_world.getContextId());
            Assert.assertTrue(target.hasRole(world, Role.MODERATOR));

            // withdraw Moderator
            args = new String[]{"schlecht!"};
            this.user.executeAdministrativeAction(target.getUserId(), AdministrativeAction.WITHDRAW_MODERATOR, args);
            Assert.assertFalse(target.hasRole(world, Role.MODERATOR));

            // assign Administrator
            this.user.executeAdministrativeAction(target.getUserId(), AdministrativeAction.ASSIGN_ADMINISTRATOR, args);
            Assert.assertTrue(target.hasRole(globalContext, Role.ADMINISTRATOR));

            // withdraw Administrator
            this.user.executeAdministrativeAction(target.getUserId(), AdministrativeAction.WITHDRAW_ADMINISTRATOR, args);
            Assert.assertFalse(target.hasRole(globalContext, Role.ADMINISTRATOR));

        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
