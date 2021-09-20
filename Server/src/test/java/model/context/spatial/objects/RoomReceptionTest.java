package model.context.spatial.objects;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.mock.graphics.MockGL20;
import controller.network.ClientSender;
import model.context.ContextID;
import model.context.global.GlobalContext;
import model.context.spatial.*;
import model.database.Database;
import model.database.IContextDatabase;
import model.database.IUserAccountManagerDatabase;
import model.database.IUserDatabase;
import model.exception.*;
import model.role.Role;
import model.user.User;
import model.user.account.UserAccountManager;
import org.junit.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;

public class RoomReceptionTest {
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
        try {
            this.userAccountManager = UserAccountManager.getInstance();
            this.globalContext = GlobalContext.getInstance();

            userAccountManager.registerUser("performer", "22222");
            User performer = userAccountManager.getUser("performer");
            performer.addRole(globalContext, Role.OWNER);
            globalContext.createWorld(performer.getUserId(), "room_recep",
                    ContextMap.PUBLIC_ROOM_MAP);

            IWorld test_world = null;
            Iterator<World> iterator = globalContext.getWorlds().values().iterator();
            while (iterator.hasNext()) {
                IWorld world = iterator.next();
                if (world.getContextName() == "room_recep") {
                    test_world = world;
                    break;
                }
            }
            Assert.assertNotNull(test_world);
            this.test_world = this.globalContext.getWorld(test_world.getContextId());


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

        globalContext.load();
        userAccountManager.load();

    }

    class TestClientSender implements ClientSender {
        public void send(SendAction sendAction, Object object) {

        }
    }

    @Test
    public void roomCreateTest() {
        TestClientSender testClientSender = new TestClientSender();
        try {
            this.userAccountManager.registerUser("rCreater", "11111");
            User creater = UserAccountManager.getInstance().loginUser("rCreater", "11111",
                    testClientSender);
            creater.joinWorld(test_world.getContextId());

            // Find RoomReception Area
            Area reception = findReception();
            Assert.assertNotNull(reception);

            // Teleport and Interact
            Location recep_loc = new Location(test_world.getPublicRoom(), Direction.UP,
                    1550, 1800);
            creater.teleport(recep_loc);
            //System.out.println(creater.getLocation().getArea().getContextName());
            creater.interact(reception.getContextId());
            Assert.assertFalse(creater.isMovable());

            RoomReception roomReception = null;
            if (reception instanceof RoomReception) {
                roomReception = (RoomReception) reception;
            }
            Assert.assertNotNull(roomReception);

            // Create room
            String[] args = new String[]{"inter_test", "1234", ContextMap.PRIVATE_ROOM_MAP.name()};
            roomReception.executeMenuOption(creater, 1, args);

            Assert.assertTrue(creater.isMovable());
            Assert.assertEquals("inter_test", creater.getLocation().getRoom().getContextName());

        } catch (Exception e) {
            e.printStackTrace();

        }

    }

    @Test
    public void roomJoinTest() {
        TestClientSender testClientSender = new TestClientSender();
        try {
            this.userAccountManager.registerUser("rJoinC", "11111");
            User creater = UserAccountManager.getInstance().loginUser("rJoinC", "11111",
                    testClientSender);
            creater.joinWorld(test_world.getContextId());

            this.userAccountManager.registerUser("rJoin", "11111");
            User join = UserAccountManager.getInstance().loginUser("rJoin", "11111",
                    testClientSender);
            join.joinWorld(test_world.getContextId());

            // Find RoomReception Area
            Area reception = findReception();
            Assert.assertNotNull(reception);

            // Teleport, Creater creates first the room, then Join joins the room
            Location recep_loc = new Location(test_world.getPublicRoom(), Direction.UP,
                    1550, 1800);
            creater.teleport(recep_loc);
            creater.interact(reception.getContextId());
            // Create room
            RoomReception roomReception = null;
            if (reception instanceof RoomReception) {
                roomReception = (RoomReception) reception;
            }
            Assert.assertNotNull(roomReception);
            String[] args = new String[]{"inter_test", "1234", ContextMap.PRIVATE_ROOM_MAP.name()};
            roomReception.executeMenuOption(creater, 1, args);
            Assert.assertEquals("inter_test", creater.getLocation().getRoom().getContextName());

            // Join
            join.teleport(recep_loc);
            join.interact(reception.getContextId());
            Assert.assertFalse(join.isMovable());

            // Create room
            String[] join_args = new String[]{"inter_test", "1234"};
            roomReception.executeMenuOption(join, 2, join_args);
            Assert.assertTrue(join.isMovable());
            Assert.assertEquals("inter_test", join.getLocation().getRoom().getContextName());

        } catch (Exception e) {
            e.printStackTrace();

        }

    }

    @Test
    public void roomRequestTest() {
        TestClientSender testClientSender = new TestClientSender();
        try {
            this.userAccountManager.registerUser("rCreaterR", "11111");
            User creater = UserAccountManager.getInstance().loginUser("rCreaterR", "11111",
                    testClientSender);
            creater.joinWorld(test_world.getContextId());

            this.userAccountManager.registerUser("rRequesterR", "11111");
            User requester = UserAccountManager.getInstance().loginUser("rRequesterR", "11111",
                    testClientSender);
            requester.joinWorld(test_world.getContextId());

            // Find RoomReception Area
            Area reception = findReception();
            Assert.assertNotNull(reception);

            // Teleport and Interact
            Location recep_loc = new Location(test_world.getPublicRoom(), Direction.UP,
                    1550, 1800);
            creater.teleport(recep_loc);
            creater.interact(reception.getContextId());
            Assert.assertFalse(creater.isMovable());

            RoomReception roomReception = null;
            if (reception instanceof RoomReception) {
                roomReception = (RoomReception) reception;
            }
            Assert.assertNotNull(roomReception);

            // Create room
            String[] args = new String[]{"inter_test", "1234", ContextMap.PRIVATE_ROOM_MAP.name()};
            roomReception.executeMenuOption(creater, 1, args);
            Assert.assertTrue(creater.isMovable());
            Assert.assertEquals("inter_test", creater.getLocation().getRoom().getContextName());

            // Send Request
            requester.teleport(recep_loc);
            requester.interact(reception.getContextId());
            Assert.assertFalse(requester.isMovable());
            int world_notif = creater.getWorldNotifications().size();
            String[] request_args = new String[]{"inter_test", "bitte"};
            roomReception.executeMenuOption(requester, 3, request_args);
            Assert.assertEquals(world_notif + 1, creater.getWorldNotifications().size());
        } catch (Exception e) {
            e.printStackTrace();

        }

    }

    private Area findReception() {
        Area public_room = test_world.getPublicRoom();
        Area hotel = null;
        Iterator<Area> area_iterator = public_room.getChildren().values().iterator();
        while (area_iterator.hasNext()) {
            Area current = area_iterator.next();
            //System.out.println(current.getContextName());
            if (current.getContextName().equals("Hotel")) {
                hotel = current;
                break;
            }
        }
        Area reception = null;
        if (hotel != null) {
            Iterator<Area> hotel_iterator = hotel.getChildren().values().iterator();
            while (hotel_iterator.hasNext()) {
                Area current = hotel_iterator.next();
                //System.out.println(current.getContextName());
                if (current.getContextName().equals("Reception")) {
                    reception = current;
                    break;
                }
            }
        }
        return reception;

    }

    @Test(expected = IllegalMenuActionException.class)
    public void argsFalseCreateTest() throws Exception{
        TestClientSender testClientSender = new TestClientSender();
        try {
            this.userAccountManager.registerUser("rfcreater", "11111");
            User creater = UserAccountManager.getInstance().loginUser("rfcreater", "11111",
                    testClientSender);
            creater.joinWorld(test_world.getContextId());

            Area reception = findReception();
            String[] args = new String[]{"abcd", "1234"};
            RoomReception roomReception = null;
            if (reception instanceof RoomReception) {
                roomReception = (RoomReception) reception;
            }
            Assert.assertNotNull(roomReception);

            roomReception.executeMenuOption(creater, 1, args);

        } catch (IllegalAccountActionException | ContextNotFoundException | IllegalWorldActionException
                | IllegalInteractionException e) {
            e.printStackTrace();
        }
    }

    @Test(expected = IllegalMenuActionException.class)
    public void mapNotFoundCreateTest() throws Exception{
        TestClientSender testClientSender = new TestClientSender();
        try {
            this.userAccountManager.registerUser("rfcreater", "11111");
            User creater = UserAccountManager.getInstance().loginUser("rfcreater", "11111",
                    testClientSender);
            creater.joinWorld(test_world.getContextId());

            Area reception = findReception();
            String[] args = new String[]{"abcd", "1234", "aaa"};
            RoomReception roomReception = null;
            if (reception instanceof RoomReception) {
                roomReception = (RoomReception) reception;
            }
            Assert.assertNotNull(roomReception);

            roomReception.executeMenuOption(creater, 1, args);

        } catch (IllegalAccountActionException | ContextNotFoundException | IllegalWorldActionException
                | IllegalInteractionException e) {
            e.printStackTrace();
        }
    }

    @Test(expected = IllegalMenuActionException.class)
    public void argsFalseJoinTest() throws Exception{
        TestClientSender testClientSender = new TestClientSender();
        try {
            this.userAccountManager.registerUser("rfjoiner", "11111");
            User creater = UserAccountManager.getInstance().loginUser("rfjoiner", "11111",
                    testClientSender);
            creater.joinWorld(test_world.getContextId());

            Area reception = findReception();
            String[] args = new String[]{"abcd"};
            RoomReception roomReception = null;
            if (reception instanceof RoomReception) {
                roomReception = (RoomReception) reception;
            }
            Assert.assertNotNull(roomReception);

            roomReception.executeMenuOption(creater, 2, args);

        } catch (IllegalAccountActionException | ContextNotFoundException | IllegalWorldActionException
                | IllegalInteractionException e) {
            e.printStackTrace();
        }
    }

    @Test(expected = IllegalMenuActionException.class)
    public void roomNotFoundJoinTest() throws Exception{
        TestClientSender testClientSender = new TestClientSender();
        try {
            this.userAccountManager.registerUser("rfjoiner", "11111");
            User creater = UserAccountManager.getInstance().loginUser("rfjoiner", "11111",
                    testClientSender);
            creater.joinWorld(test_world.getContextId());

            Area reception = findReception();
            String[] args = new String[]{"abcd", "1234"};
            RoomReception roomReception = null;
            if (reception instanceof RoomReception) {
                roomReception = (RoomReception) reception;
            }
            Assert.assertNotNull(roomReception);

            roomReception.executeMenuOption(creater, 2, args);

        } catch (IllegalAccountActionException | ContextNotFoundException | IllegalWorldActionException
                | IllegalInteractionException e) {
            e.printStackTrace();
        }
    }

    @Test(expected = IllegalMenuActionException.class)
    public void argsFalseRequestTest() throws Exception{
        TestClientSender testClientSender = new TestClientSender();
        try {
            this.userAccountManager.registerUser("rfjoiner", "11111");
            User creater = UserAccountManager.getInstance().loginUser("rfjoiner", "11111",
                    testClientSender);
            creater.joinWorld(test_world.getContextId());

            Area reception = findReception();
            String[] args = new String[]{"abcd"};
            RoomReception roomReception = null;
            if (reception instanceof RoomReception) {
                roomReception = (RoomReception) reception;
            }
            Assert.assertNotNull(roomReception);

            roomReception.executeMenuOption(creater, 3, args);

        } catch (IllegalAccountActionException | ContextNotFoundException | IllegalWorldActionException
                | IllegalInteractionException e) {
            e.printStackTrace();
        }
    }

    @Test(expected = IllegalMenuActionException.class)
    public void roomNotFoundRequestTest() throws Exception{
        TestClientSender testClientSender = new TestClientSender();
        try {
            this.userAccountManager.registerUser("rfjoiner", "11111");
            User creater = UserAccountManager.getInstance().loginUser("rfjoiner", "11111",
                    testClientSender);
            creater.joinWorld(test_world.getContextId());

            Area reception = findReception();
            String[] args = new String[]{"abcd", "1234"};
            RoomReception roomReception = null;
            if (reception instanceof RoomReception) {
                roomReception = (RoomReception) reception;
            }
            Assert.assertNotNull(roomReception);

            roomReception.executeMenuOption(creater, 3, args);

        } catch (IllegalAccountActionException | ContextNotFoundException | IllegalWorldActionException
                | IllegalInteractionException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void roomCreateInUserTest() {
        TestClientSender testClientSender = new TestClientSender();
        try {
            this.userAccountManager.registerUser("userCreater", "11111");
            User creater = UserAccountManager.getInstance().loginUser("userCreater", "11111",
                    testClientSender);
            creater.joinWorld(test_world.getContextId());

            // Find RoomReception Area
            Area reception = findReception();
            Assert.assertNotNull(reception);

            // Teleport and Interact
            Location recep_loc = new Location(test_world.getPublicRoom(), Direction.UP,
                    1550, 1800);
            creater.teleport(recep_loc);

            RoomReception roomReception = null;
            if (reception instanceof RoomReception) {
                roomReception = (RoomReception) reception;
            }
            Assert.assertNotNull(roomReception);

            String[] args = new String[]{"inter_test", "1234", ContextMap.PRIVATE_ROOM_MAP.name()};
            creater.setCurrentInteractable(roomReception);
            creater.executeOption(roomReception.getContextId(), 1, args);

            Assert.assertEquals("inter_test", creater.getLocation().getRoom().getContextName());

        } catch (Exception e) {
            e.printStackTrace();

        }

    }
}
