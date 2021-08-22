package model.user;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backend.headless.mock.graphics.MockGL20;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import controller.network.ClientSender;
import model.context.Context;
import model.context.ContextID;
import model.context.global.GlobalContext;
import model.context.spatial.Room;
import model.context.spatial.SpatialMap;
import model.context.spatial.World;
import model.context.spatial.WorldTest;
import model.database.Database;
import model.database.IContextDatabase;
import model.database.IUserAccountManagerDatabase;
import model.database.IUserDatabase;
import model.notification.INotification;
import model.notification.Notification;
import model.role.Permission;
import model.role.Role;
import model.user.account.UserAccountManager;
import model.user.account.UserAccountManagerTest;
import org.junit.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
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
            if (globalContext.getWorlds().size() == 0) {
                globalContext.createWorld(performer.getUserId(), "test_world", SpatialMap.MAP);
            }
            ContextID newworld_id = globalContext.getWorlds().keySet().iterator().next();
            System.out.println(globalContext.getWorlds().size());
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
        UserTesten.TestClientSender testClientSender = new UserTesten.TestClientSender();
        try {
            this.userAccountManager.registerUser("join", "11111");
            this.userAccountManager.loginUser("join", "11111", testClientSender);
            this.user = userAccountManager.getUser("join");
            Assert.assertEquals(Status.ONLINE, this.user.getStatus());
            Thread.sleep(1500);
            this.user.joinWorld(test_world.getContextId());

            Assert.assertEquals("test_world", this.user.getWorld().getContextName());
        } catch (Exception e) {
            e.printStackTrace();
        }



    }

    @Test
    public void leaveWorldTest() {
        setTestWorld("forLeave");
        UserTesten.TestClientSender testClientSender = new UserTesten.TestClientSender();
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
        UserTesten.TestClientSender testClientSender = new UserTesten.TestClientSender();
        try {
            this.userAccountManager.registerUser("move", "11111");
            this.userAccountManager.loginUser("move", "11111", testClientSender);
            this.user = userAccountManager.getUser("move");
            Thread.sleep(1500);
            this.user.joinWorld(test_world.getContextId());
            Assert.assertEquals("test_world", this.user.getWorld().getContextName());

            this.user.move(1401, 1000);
            Assert.assertEquals(1401, (int)this.user.getLocation().getPosX());
            Assert.assertEquals(1000, (int)this.user.getLocation().getPosY());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Test
    public void chatTest() {

    }


    @Test
    public void talkTest() {

    }


    @Test
    public void executeAdministrativeActionTest() {
        UserTesten.TestClientSender testClientSender = new UserTesten.TestClientSender();

        try {
            // Invite Friend
            this.userAccountManager.registerUser("target", "11111");
            User target = this.userAccountManager.loginUser("target", "11111", testClientSender);
            this.userAccountManager.registerUser("sender", "22222");
            this.user = this.userAccountManager.loginUser("sender", "22222", testClientSender);
            String[] args = new String[]{"hallo"};

            this.user.executeAdministrativeAction(target.getUserId(), AdministrativeAction.INVITE_FRIEND, args);
            Assert.assertEquals(1, target.getGlobalNotifications().size());
            UUID actual_friend_request_id = target.getGlobalNotifications().keySet().iterator().next();
            INotification actual_notif= target.getGlobalNotifications().get(actual_friend_request_id);
            Assert.assertEquals("friendRequestKey", actual_notif.getMessageBundle().getMessageKey());
            Assert.assertEquals("sender", actual_notif.getMessageBundle().getArguments()[0]);
            Assert.assertEquals("hallo", actual_notif.getMessageBundle().getArguments()[1]);


            // Remove Friend
            this.user.addFriend(target);
            target.addFriend(this.user);
            args = new String[]{};

            this.user.executeAdministrativeAction(target.getUserId(), AdministrativeAction.REMOVE_FRIEND, args);
            Assert.assertEquals(0, this.user.getFriends().size());
            Assert.assertEquals(0, target.getFriends().size());


            // Ignore User
            this.user.addFriend(target);
            target.addFriend(this.user);
            Assert.assertEquals(1, this.user.getFriends().size());
            Assert.assertEquals(1, target.getFriends().size());
            args = new String[]{};
            this.user.executeAdministrativeAction(target.getUserId(), AdministrativeAction.IGNORE_USER, args);
            Assert.assertEquals(0, this.user.getFriends().size());
            Assert.assertEquals(0, target.getFriends().size());
            Assert.assertEquals(1, this.user.getIgnoredUsers().size());
            Assert.assertTrue(this.user.getIgnoredUsers().keySet().contains(target.getUserId()));


            // Unignore User
            this.user.executeAdministrativeAction(target.getUserId(), AdministrativeAction.UNIGNORE_USER, args);
            Assert.assertEquals(0, this.user.getIgnoredUsers().size());


            // Ban User

            setTestWorld("forBanUser");
            Thread.sleep(1500);
            this.user.joinWorld(this.test_world.getContextId());
            this.user.addRole(this.test_world, Role.MODERATOR);
            //System.out.println(this.user.hasPermission(this.test_world, Permission.BAN_USER));
            //System.out.println(this.user.hasPermission(this.test_world, Permission.BAN_MODERATOR));
            //Assert.assertTrue(this.user.hasPermission(this.test_world, Permission.BAN_MODERATOR));
            //this.user.executeAdministrativeAction(target.getUserId(), AdministrativeAction.BAN_USER, args);


            // Room Invite
            // Room Kick
            // Teleport to User
            // Report User
            // Mute User
            // Unmute User

            // Unban User
            // Assign Moderator
            // Withdraw Moderator
            // Assign Administrator
            // Withdraw Administrator


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
        UserTesten.TestClientSender testClientSender = new UserTesten.TestClientSender();
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
