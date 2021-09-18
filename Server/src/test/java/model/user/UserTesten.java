package model.user;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.mock.graphics.MockGL20;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import controller.network.ClientSender;
import model.communication.CommunicationMedium;
import model.context.ContextID;
import model.context.global.GlobalContext;
import model.context.spatial.*;
import model.database.Database;
import model.database.IContextDatabase;
import model.database.IUserAccountManagerDatabase;
import model.database.IUserDatabase;
import model.exception.IllegalNotificationActionException;
import model.exception.NotificationNotFoundException;
import model.notification.*;
import model.role.Role;
import model.user.account.UserAccountManager;
import org.junit.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.UUID;

public class UserTesten {

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
        typeUsercount = 0;
        chatcount = 0;
        talkcount = 0;
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
    public void setUserNameTest() {

    }


    @Test
    public void joinWorldTest() {

        setTestWorld("forJoin");
        TestClientSender testClientSender = new TestClientSender();
        try {
            this.userAccountManager.registerUser("join", "11111");
            this.userAccountManager.loginUser("join", "11111", testClientSender);
            this.user = userAccountManager.getUser("join");
            Assert.assertEquals(Status.ONLINE, this.user.getStatus());
            this.user.joinWorld(test_world.getContextId());

            Assert.assertEquals("test_world", this.user.getWorld().getContextName());
        } catch (Exception e) {
            e.printStackTrace();
        }



    }

    @Test
    public void leaveWorldTest() {
        setTestWorld("forLeave");
        TestClientSender testClientSender = new TestClientSender();
        try {
            this.userAccountManager.registerUser("leave", "11111");
            this.userAccountManager.loginUser("leave", "11111", testClientSender);
            this.user = userAccountManager.getUser("leave");
            Thread.sleep(1500);
            this.user.joinWorld(test_world.getContextId());
            Assert.assertEquals("test_world", this.user.getWorld().getContextName());

            this.user.leaveWorld();
            Assert.assertEquals(null, this.user.getWorld());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Test
    public void moveTest() {
        setTestWorld("forMove");
        TestClientSender testClientSender = new TestClientSender();
        try {
            this.userAccountManager.registerUser("move", "11111");
            this.userAccountManager.loginUser("move", "11111", testClientSender);
            this.user = userAccountManager.getUser("move");
            Thread.sleep(1500);
            this.user.joinWorld(test_world.getContextId());
            Assert.assertEquals("test_world", this.user.getWorld().getContextName());

            this.user.move(Direction.DOWN, 1401, 1000, false);
            Assert.assertEquals(1401, (int)this.user.getLocation().getPosX());
            Assert.assertEquals(1000, (int)this.user.getLocation().getPosY());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public int typeUsercount = 0;
    public int chatcount = 0;
    public int talkcount = 0;
    class TypeClientSender implements ClientSender {
        public void send(SendAction sendAction, Object object) {
            if (sendAction == SendAction.TYPING) {
                typeUsercount = 1;
            } else if (sendAction == SendAction.MESSAGE) {
                chatcount = 1;
            } else if (sendAction == SendAction.AUDIO) {
                talkcount = 1;
            }
        }
    }

    // Type/Chat/Talk koennen zusammen mit CommunicationHandler testen
    // Noch Fehlerfall nicht geschrieben
    @Test
    public void typeTest() {
        setTestWorld("fortype");
        TypeClientSender typeClientSender = new TypeClientSender();
        try {
            this.userAccountManager.registerUser("typer", "11111");
            user = UserAccountManager.getInstance().loginUser("typer", "11111", typeClientSender);
            this.userAccountManager.registerUser("rrn_receiver", "22222");
            User receiver = UserAccountManager.getInstance().loginUser("rrn_receiver", "22222",
                    typeClientSender);
            user.joinWorld(test_world.getContextId());
            receiver.joinWorld(test_world.getContextId());
            Assert.assertEquals(test_world.getPublicRoom().getSpawnLocation(), user.getLocation());
            Assert.assertEquals(test_world.getPublicRoom().getSpawnLocation(), receiver.getLocation());
            Assert.assertEquals(2, user.getCommunicableUsers().size());
            Assert.assertTrue(user.getCommunicableUsers().containsValue(user));
            Assert.assertTrue(user.getCommunicableUsers().containsValue(receiver));

            user.type();
            Assert.assertEquals(1, typeUsercount);

        } catch (Exception e) {
            e.printStackTrace();

        }
    }


    @Test
    public void chatTest() {
        setTestWorld("forchat");
        TypeClientSender typeClientSender = new TypeClientSender();
        try {
            this.userAccountManager.registerUser("chater", "11111");
            user = UserAccountManager.getInstance().loginUser("chater", "11111", typeClientSender);
            user.joinWorld(test_world.getContextId());
            Assert.assertEquals(test_world.getPublicRoom().getSpawnLocation(), user.getLocation());

            user.chat("hallo");
            Assert.assertEquals(1, chatcount);

        } catch (Exception e) {
            e.printStackTrace();

        }
    }


    @Test
    public void talkTest() {
        setTestWorld("fortalk");
        TypeClientSender typeClientSender = new TypeClientSender();
        try {
            this.userAccountManager.registerUser("talker", "11111");
            user = UserAccountManager.getInstance().loginUser("talker", "11111", typeClientSender);
            this.userAccountManager.registerUser("listener", "11111");
            User listener = UserAccountManager.getInstance().loginUser("listener", "11111",
                    typeClientSender);
            user.joinWorld(test_world.getContextId());
            listener.joinWorld(test_world.getContextId());
            Location newloc = new Location(test_world.getPublicRoom(), Direction.UP, 1300, 1000);
            //System.out.println(newloc.getArea().getContextName());
            user.move(Direction.UP, 1300, 1000, false);
            listener.move(Direction.UP, 1300, 1000, false);
            Assert.assertTrue(user.getLocation().getArea().canCommunicateWith(CommunicationMedium.VOICE));
            Assert.assertTrue(listener.getLocation().getArea().canCommunicateWith(CommunicationMedium.VOICE));

            byte a = 1;
            byte b = 2;
            byte[] voiceData = new byte[]{a, b};
            user.talk(voiceData);
            Assert.assertEquals(1, talkcount);

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


    @Test
    public void interactTest() {

    }


    @Test
    public void executeOptionTest() {

    }


    @Test
    public void deleteNotificationTest() {

    }


    @Test
    public void manageNotificationTest() {

    }


    @Test
    public void setAvatarTest() {
        TestClientSender testClientSender = new TestClientSender();
        try {
            userAccountManager.registerUser("setAvatar", "11111");
            User setAvatar = userAccountManager.loginUser("setAvatar", "11111", testClientSender);

            String first_avatar_name = setAvatar.getAvatar().name();
            if (first_avatar_name != "ADAM") {
                setAvatar.setAvatar(Avatar.ADAM);
                Assert.assertEquals("ADAM", setAvatar.getAvatar().name());
            } else {
                setAvatar.setAvatar(Avatar.ADAM);
                Assert.assertEquals("ADAM", setAvatar.getAvatar().name());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Test
    public void teleportTest() {

    }


    //Datenbank
    @Test
    public void addFriendsTest() {

    }


    @Test
    public void addFriendTest() {
        try {
            userAccountManager.registerUser("addF", "11111");
            this.user = userAccountManager.getUser("addF");
            this.userAccountManager.registerUser("friend", "22222");
            User friend = this.userAccountManager.getUser("friend");
            this.user.addFriend(friend);

            Assert.assertTrue(this.user.getFriends().containsKey(friend.getUserId()));
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    //Datenbank
    @Test
    public void addIgnoresTest() {

    }


    @Test
    public void ignoreTest() {
        try {
            userAccountManager.registerUser("igno", "11111");
            this.user = userAccountManager.getUser("igno");
            this.userAccountManager.registerUser("ignore", "22222");
            User ignore = this.userAccountManager.getUser("ignore");
            this.user.ignoreUser(ignore);
            Assert.assertTrue(this.user.getIgnoredUsers().containsKey(ignore.getUserId()));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Test
    public void removeFriendTest() {
        try {
            userAccountManager.registerUser("removeF", "11111");
            this.user = userAccountManager.getUser("removeF");
            this.userAccountManager.registerUser("unfriend", "22222");
            User unfriend = this.userAccountManager.getUser("unfriend");
            this.user.addFriend(unfriend);
            Assert.assertTrue(this.user.getFriends().containsKey(unfriend.getUserId()));

            this.user.removeFriend(unfriend);
            Assert.assertFalse(this.user.getFriends().containsKey(unfriend.getUserId()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Test
    public void unignoreTest() {
        try {
            userAccountManager.registerUser("unigno", "11111");
            this.user = userAccountManager.getUser("unigno");
            this.userAccountManager.registerUser("unignore", "22222");
            User unignore = this.userAccountManager.getUser("unignore");
            this.user.ignoreUser(unignore);
            Assert.assertTrue(this.user.getIgnoredUsers().containsKey(unignore.getUserId()));

            this.user.unignoreUser(unignore);
            Assert.assertFalse(this.user.getIgnoredUsers().containsKey(unignore.getUserId()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Test
    public void addRoleTest() {
        try {
            userAccountManager.registerUser("addRole", "11111");
            this.user = userAccountManager.getUser("addRole");
            this.user.addRole(GlobalContext.getInstance(), Role.OWNER);

            Assert.assertTrue(user.getGlobalRoles().getRoles().contains(Role.OWNER));
            Assert.assertTrue(user.hasRole(globalContext, Role.OWNER));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Test
    public void removeRoleTest() {
        try {
            userAccountManager.registerUser("removeRole", "11111");
            this.user = userAccountManager.getUser("removeRole");
            this.user.addRole(GlobalContext.getInstance(), Role.OWNER);
            Assert.assertTrue(user.getGlobalRoles().getRoles().contains(Role.OWNER));

            this.user.removeRole(GlobalContext.getInstance(), Role.OWNER);
            Assert.assertFalse(user.getGlobalRoles().getRoles().contains(Role.OWNER));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    //Datenbank
    @Test
    public void addRolesTest() {

    }


    @Test
    public void addNotificationTest() {

    }


    @Test
    public void removeNotificationTest() {

    }


    //Datenbank
    @Test
    public void addNotificationsTest() {

    }


    @Test
    public void setCurrentInteractableTest() {

    }


    @Test
    public void setStatusTest() {

    }


    @Test
    public void updateLastLogoutTimeTest() {

    }


    @Test
    public void updateLastActivityTest() {

    }


    @Test
    public void setMovableTest() {

    }


    @Test
    public void setClientSenderTest() {

    }


    @Test
    public void addChildRolesTest() {

    }


    @Test
    public void updateUserInfoTest() {

    }


    @Test
    public void updateRoleInfoTest() {

    }

    @Test
    public void logoutTest() {
        setTestWorld("forLogout");
        TestClientSender testClientSender = new TestClientSender();
        try {
            userAccountManager.registerUser("logout", "11111");
            this.user = userAccountManager.loginUser("logout", "11111", testClientSender);
            Thread.sleep(1500);
            this.user.joinWorld(this.test_world.getContextId());
            Assert.assertEquals(Status.ONLINE, this.user.getStatus());

            this.user.logout();
            Assert.assertEquals(null, this.user.getWorld());
            Assert.assertEquals(Status.OFFLINE, this.user.getStatus());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
