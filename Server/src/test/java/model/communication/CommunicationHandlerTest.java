package model.communication;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.mock.graphics.MockGL20;
import controller.network.ClientSender;
import model.communication.message.AudioMessage;
import model.communication.message.MessageType;
import model.communication.message.TextMessage;
import model.context.ContextID;
import model.context.global.GlobalContext;
import model.context.spatial.ContextMap;
import model.context.spatial.Direction;
import model.context.spatial.Location;
import model.context.spatial.Room;
import model.context.spatial.World;
import model.database.Database;
import model.database.IContextDatabase;
import model.database.IUserAccountManagerDatabase;
import model.database.IUserDatabase;
import model.exception.ContextNotFoundException;
import model.exception.IllegalAccountActionException;
import model.exception.IllegalWorldActionException;
import model.role.Role;
import model.user.AdministrativeAction;
import model.user.User;
import model.user.account.UserAccountManager;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class CommunicationHandlerTest {
    private User user;
    private User actionUser;
    private TextMessage textMessage;
    private AudioMessage audioMessage;

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
    }

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
    public void typeTest() {
        setTestWorld("type");
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

            CommunicationHandler.handleTyping(user);
            Assert.assertEquals(user, actionUser);

        } catch (Exception e) {
            e.printStackTrace();

        }
    }


    @Test
    public void normalChatTest() {
        setTestWorld("chat");
        TypeClientSender typeClientSender = new TypeClientSender();
        try {
            this.userAccountManager.registerUser("chater", "11111");
            user = UserAccountManager.getInstance().loginUser("chater", "11111", typeClientSender);
            user.joinWorld(test_world.getContextId());
            Assert.assertEquals(test_world.getPublicRoom().getSpawnLocation(), user.getLocation());

            CommunicationHandler.handleTextMessage(user, "hallo");
            Assert.assertEquals(user, actionUser);
            Assert.assertEquals("hallo", textMessage.getTextMessage());

        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    @Test
    public void unknownCommandTest() {
        setTestWorld("unknown");
        TypeClientSender typeClientSender = new TypeClientSender();
        try {
            this.userAccountManager.registerUser("uchater", "11111");
            user = UserAccountManager.getInstance().loginUser("uchater", "11111", typeClientSender);
            user.joinWorld(test_world.getContextId());
            Assert.assertEquals(test_world.getPublicRoom().getSpawnLocation(), user.getLocation());

            CommunicationHandler.handleTextMessage(user, "\\area hallo");
            // Fehlerfall Sender auf null gesetzt
            Assert.assertNull(actionUser);
            Assert.assertEquals("chat.command.not-found", textMessage.getMessageBundle().getMessageKey());

        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    // Sender ist stumm geschaltet
    @Test
    public void falseChatTest() {
        setTestWorld("fchat");
        TypeClientSender typeClientSender = new TypeClientSender();
        try {
            this.userAccountManager.registerUser("fchater", "11111");
            user = UserAccountManager.getInstance().loginUser("fchater", "11111", typeClientSender);
            user.joinWorld(test_world.getContextId());
            this.userAccountManager.registerUser("shut", "11111");
            User shut = UserAccountManager.getInstance().loginUser("shut", "11111",
                    typeClientSender);
            shut.addRole(test_world, Role.ADMINISTRATOR);
            shut.joinWorld(test_world.getContextId());
            // Stumm schalten
            String[] args = new String[]{"mute"};
            shut.executeAdministrativeAction(user.getUserId(), AdministrativeAction.MUTE_USER, args);


            CommunicationHandler.handleTextMessage(user, "hallo");
            Assert.assertNull(actionUser);
            Assert.assertEquals("chat.standard.muted", textMessage.getMessageBundle().getMessageKey());

        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    @Test
    public void whisperChatTest() {
        setTestWorld("forwhisper");
        TypeClientSender typeClientSender = new TypeClientSender();
        try {
            this.userAccountManager.registerUser("whisper", "11111");
            user = UserAccountManager.getInstance().loginUser("whisper", "11111", typeClientSender);
            this.userAccountManager.registerUser("wlistener", "11111");
            User listener = UserAccountManager.getInstance().loginUser("wlistener", "11111",
                    typeClientSender);
            user.joinWorld(test_world.getContextId());
            listener.joinWorld(test_world.getContextId());
            Assert.assertEquals(test_world.getPublicRoom().getSpawnLocation(), user.getLocation());

            CommunicationHandler.handleTextMessage(user, "\\\\wlistener hi");
            Assert.assertEquals(user, actionUser);
            Assert.assertEquals(MessageType.WHISPER, textMessage.getMessageType());

        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    // Receiver ist nicht in einer Welt
    @Test
    public void whisperNotAvailableChatTest() {
        setTestWorld("forfwhisper");
        TypeClientSender typeClientSender = new TypeClientSender();
        try {
            this.userAccountManager.registerUser("fwhisper", "11111");
            user = UserAccountManager.getInstance().loginUser("fwhisper", "11111", typeClientSender);
            this.userAccountManager.registerUser("fwlistener", "11111");
            User listener = UserAccountManager.getInstance().loginUser("fwlistener", "11111",
                    typeClientSender);
            user.joinWorld(test_world.getContextId());

            CommunicationHandler.handleTextMessage(user, "\\\\fwlistener hi");
            Assert.assertEquals(MessageType.INFO, textMessage.getMessageType());
        } catch (IllegalAccountActionException | ContextNotFoundException | IllegalWorldActionException e) {
            e.printStackTrace();

        }
    }

    @Test
    public void whisperUserNotFoundChatTest() {
        setTestWorld("forfuwhisper");
        TypeClientSender typeClientSender = new TypeClientSender();
        try {
            this.userAccountManager.registerUser("fuwhisper", "11111");
            user = UserAccountManager.getInstance().loginUser("fuwhisper", "11111", typeClientSender);
            user.joinWorld(test_world.getContextId());

            CommunicationHandler.handleTextMessage(user, "\\\\fuwlistener hi");
            Assert.assertNull(actionUser);
            Assert.assertEquals("chat.command.whisper.user-not-found",
                    textMessage.getMessageBundle().getMessageKey());

        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    @Test
    public void whisperMyselfChatTest() {
        setTestWorld("formywhisper");
        TypeClientSender typeClientSender = new TypeClientSender();
        try {
            this.userAccountManager.registerUser("mywhisper", "11111");
            user = UserAccountManager.getInstance().loginUser("mywhisper", "11111", typeClientSender);
            user.joinWorld(test_world.getContextId());

            CommunicationHandler.handleTextMessage(user, "\\\\mywhisper hi");
            Assert.assertNull(actionUser);
            Assert.assertEquals("chat.command.whisper.illegal-user",
                    textMessage.getMessageBundle().getMessageKey());

        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    @Test
    public void worldChatTest() {
        setTestWorld("forworldchat");
        TypeClientSender typeClientSender = new TypeClientSender();
        try {
            this.userAccountManager.registerUser("worldchater", "11111");
            user = UserAccountManager.getInstance().loginUser("worldchater", "11111", typeClientSender);
            this.userAccountManager.registerUser("wolistener", "11111");
            User listener = UserAccountManager.getInstance().loginUser("wolistener", "11111",
                    typeClientSender);
            user.joinWorld(test_world.getContextId());
            user.addRole(test_world, Role.ADMINISTRATOR);
            listener.joinWorld(test_world.getContextId());
            Assert.assertEquals(test_world.getPublicRoom().getSpawnLocation(), user.getLocation());

            CommunicationHandler.handleTextMessage(user, "\\world hi");
            Assert.assertEquals(user, actionUser);
            Assert.assertEquals(MessageType.WORLD, textMessage.getMessageType());

        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    @Test
    public void worldNoPermissionChatTest() {
        setTestWorld("npworldchat");
        TypeClientSender typeClientSender = new TypeClientSender();
        try {
            this.userAccountManager.registerUser("npworldchater", "11111");
            user = UserAccountManager.getInstance().loginUser("npworldchater", "11111",
                    typeClientSender);
            this.userAccountManager.registerUser("npwolistener", "11111");
            User listener = UserAccountManager.getInstance().loginUser("npwolistener", "11111",
                    typeClientSender);
            user.joinWorld(test_world.getContextId());
            listener.joinWorld(test_world.getContextId());
            Assert.assertEquals(test_world.getPublicRoom().getSpawnLocation(), user.getLocation());

            CommunicationHandler.handleTextMessage(user, "\\world hi");
            Assert.assertNull(actionUser);
            Assert.assertEquals("chat.command.world.not-permitted",
                    textMessage.getMessageBundle().getMessageKey());

        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    @Test
    public void roomChatTest() {
        setTestWorld("forroomchat");
        TypeClientSender typeClientSender = new TypeClientSender();
        try {
            this.userAccountManager.registerUser("roomChater", "11111");
            user = UserAccountManager.getInstance().loginUser("roomChater", "11111", typeClientSender);
            this.userAccountManager.registerUser("rlistener", "11111");
            User listener = UserAccountManager.getInstance().loginUser("rlistener", "11111",
                    typeClientSender);
            user.joinWorld(test_world.getContextId());
            listener.joinWorld(test_world.getContextId());
            Assert.assertEquals(test_world.getPublicRoom().getSpawnLocation(), user.getLocation());
            Room test_room = new Room("test_roomchat", this.test_world, ContextMap.PRIVATE_ROOM_MAP,
                    "11111");
            test_room.build();
            this.test_world.addPrivateRoom(test_room);
            user.addRole(test_room, Role.ROOM_OWNER);
            user.teleport(test_room.getSpawnLocation());
            listener.teleport(test_room.getSpawnLocation());

            CommunicationHandler.handleTextMessage(user, "\\room hi");
            Assert.assertEquals(user, actionUser);
            Assert.assertEquals(MessageType.ROOM, textMessage.getMessageType());

        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    @Test
    public void roomNoPermissionTest() {
        setTestWorld("nproomchat");
        TypeClientSender typeClientSender = new TypeClientSender();
        try {
            this.userAccountManager.registerUser("nproomChater", "11111");
            user = UserAccountManager.getInstance().loginUser("nproomChater", "11111", typeClientSender);
            this.userAccountManager.registerUser("nprlistener", "11111");
            User listener = UserAccountManager.getInstance().loginUser("nprlistener", "11111",
                    typeClientSender);
            user.joinWorld(test_world.getContextId());
            listener.joinWorld(test_world.getContextId());
            Assert.assertEquals(test_world.getPublicRoom().getSpawnLocation(), user.getLocation());
            Room test_room = new Room("test_roomchat", this.test_world, ContextMap.PRIVATE_ROOM_MAP,
                    "11111");
            test_room.build();
            this.test_world.addPrivateRoom(test_room);
            user.addRole(test_room, Role.ROOM_OWNER);
            user.teleport(test_room.getSpawnLocation());
            listener.teleport(test_room.getSpawnLocation());

            CommunicationHandler.handleTextMessage(listener, "\\room hi");
            Assert.assertNull(actionUser);
            Assert.assertEquals("chat.command.room.not-permitted",
                    textMessage.getMessageBundle().getMessageKey());

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
            CommunicationHandler.handleVoiceMessage(user, voiceData);
            Assert.assertEquals(user, actionUser);
            Assert.assertNotNull(audioMessage);

        } catch (Exception e) {
            e.printStackTrace();

        }
    }

}
