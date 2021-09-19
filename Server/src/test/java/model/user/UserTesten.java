package model.user;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.mock.graphics.MockGL20;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import controller.network.ClientSender;
import model.communication.CommunicationMedium;
import model.communication.message.AudioMessage;
import model.communication.message.TextMessage;
import model.context.ContextID;
import model.context.global.GlobalContext;
import model.context.spatial.*;
import model.database.Database;
import model.database.IContextDatabase;
import model.database.IUserAccountManagerDatabase;
import model.database.IUserDatabase;
import model.exception.*;
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
        actionUser = null;
        textMessage = null;
        audioMessage = null;
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

    private User actionUser;
    private TextMessage textMessage;
    private AudioMessage audioMessage;
    class TypeClientSender implements ClientSender {
        public void send(SendAction sendAction, Object object) {
            if (sendAction == SendAction.TYPING) {
                actionUser = (User) object;
            } else if (sendAction == SendAction.MESSAGE) {
                textMessage = (TextMessage) object;
                actionUser = textMessage.getSender();
            } else if (sendAction == SendAction.AUDIO) {
                audioMessage = (AudioMessage) object;
                actionUser = audioMessage.getSender();
            }
        }
    }

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
            Assert.assertNotNull(actionUser);
            Assert.assertEquals(user.getUserId(), actionUser.getUserId());

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
            Assert.assertNotNull(actionUser);
            Assert.assertEquals(user.getUserId(), actionUser.getUserId());

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
            Assert.assertNotNull(actionUser);
            Assert.assertEquals(user.getUserId(), actionUser.getUserId());

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
