package model.context.spatial;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.mock.graphics.MockGL20;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import controller.network.ClientSender;
import model.context.ContextID;
import model.context.global.GlobalContext;
import model.database.Database;
import model.database.IContextDatabase;
import model.database.IUserAccountManagerDatabase;
import model.database.IUserDatabase;
import model.role.Role;
import model.user.User;
import model.user.account.UserAccountManager;
import org.jetbrains.annotations.NotNull;
import org.junit.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;

public class RoomTest {

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
        this.userAccountManager.load();
        this.globalContext.load();

    }

    class TestClientSender implements ClientSender {
        public void send(@NotNull SendAction sendAction, @NotNull Object object) {

        }
    }

    @Test
    public void privateRoomAddUserTest() {
        try {

            // Erstelle eine Welt
            this.userAccountManager.registerUser("pRATester", "11111");
            TestClientSender testClientSender = new TestClientSender();
            User privateRoomTester = this.userAccountManager.loginUser("pRATester", "11111",
                    testClientSender);
            privateRoomTester.addRole(this.globalContext, Role.OWNER);

            // Wegen Thread
            if (this.globalContext.getIWorlds().size() == 0) {
                this.globalContext.createWorld(privateRoomTester.getUserId(), "test_world", ContextMap.PUBLIC_ROOM_MAP);
                Assert.assertEquals(1, this.globalContext.getIWorlds().size());
            }
            ContextID test_world_id = this.globalContext.getIWorlds().keySet().iterator().next();
            World test_world = this.globalContext.getWorld(test_world_id);

            // Erstelle ein Raum
            Room test_proom = new Room("testa_room", test_world, ContextMap.PRIVATE_ROOM_MAP, "11111");
            test_world.addPrivateRoom(test_proom);
            Assert.assertTrue(test_proom.isPrivate());

            // addUser
            privateRoomTester.joinWorld(test_world_id);
            test_proom.addUser(privateRoomTester);

            Assert.assertEquals(1, test_proom.getUsers().size());
            Assert.assertTrue(test_proom.getUsers().values().contains(privateRoomTester));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void privateRoomRemoveUserTest() {
        try {
            // Erstelle eine Welt
            this.userAccountManager.registerUser("pRRTester", "11111");
            TestClientSender testClientSender = new TestClientSender();
            User privateRoomTester = this.userAccountManager.loginUser("pRRTester", "11111",
                    testClientSender);
            privateRoomTester.addRole(this.globalContext, Role.OWNER);

            // Wegen Thread
            if (this.globalContext.getIWorlds().size() == 0) {
                this.globalContext.createWorld(privateRoomTester.getUserId(), "test_world", ContextMap.PUBLIC_ROOM_MAP);
                Assert.assertEquals(1, this.globalContext.getIWorlds().size());
            }
            ContextID test_world_id = this.globalContext.getIWorlds().keySet().iterator().next();
            World test_world = this.globalContext.getWorld(test_world_id);

            // Erstelle ein Raum
            Room test_proom = new Room("testr_room", test_world, ContextMap.PRIVATE_ROOM_MAP, "11111");
            test_world.addPrivateRoom(test_proom);


            // addUser
            privateRoomTester.joinWorld(test_world_id);
            test_proom.addUser(privateRoomTester);
            privateRoomTester.addRole(test_proom, Role.ROOM_OWNER);
            Assert.assertEquals(1, test_proom.getUsers().size());

            // Der einzige Benutzer geht raus und dann dieses Raum wird geloescht
            test_proom.removeUser(privateRoomTester);
            Assert.assertFalse(test_world.getPrivateRooms().values().contains(test_proom));
            Assert.assertFalse(test_world.getChildren().values().contains(test_proom));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void checkPasswordTest() {
        try {
            // Erstelle eine Welt
            this.userAccountManager.registerUser("pRCTester", "11111");
            TestClientSender testClientSender = new TestClientSender();
            User privateRoomTester = this.userAccountManager.loginUser("pRCTester", "11111",
                    testClientSender);
            privateRoomTester.addRole(this.globalContext, Role.OWNER);

            // Wegen Thread
            if (this.globalContext.getIWorlds().size() == 0) {
                this.globalContext.createWorld(privateRoomTester.getUserId(), "test_world", ContextMap.PUBLIC_ROOM_MAP);
                Assert.assertEquals(1, this.globalContext.getIWorlds().size());
            }
            ContextID test_world_id = this.globalContext.getIWorlds().keySet().iterator().next();
            World test_world = this.globalContext.getWorld(test_world_id);

            // Erstelle ein Raum
            Room test_proom = new Room("test_room", test_world, ContextMap.PRIVATE_ROOM_MAP, "11111");
            test_world.addPrivateRoom(test_proom);
            privateRoomTester.addRole(test_proom, Role.ROOM_OWNER);

            // checkPassword
            Assert.assertTrue(test_proom.checkPassword("11111"));
            Assert.assertFalse(test_proom.checkPassword("11110"));

        } catch (Exception e) {

        }
    }

    // Area Testen
    // playMusic, getMusic, stopMusic bei Area
    @Test
    public void musicTest() {
        try {
            // Erstelle eine Welt
            this.userAccountManager.registerUser("pRMuTester", "11111");
            TestClientSender testClientSender = new TestClientSender();
            User privateRoomTester = this.userAccountManager.loginUser("pRMuTester", "11111",
                    testClientSender);
            privateRoomTester.addRole(this.globalContext, Role.OWNER);

            // Wegen Thread
            if (this.globalContext.getIWorlds().size() == 0) {
                this.globalContext.createWorld(privateRoomTester.getUserId(), "test_world", ContextMap.PUBLIC_ROOM_MAP);
                Assert.assertEquals(1, this.globalContext.getIWorlds().size());
            }
            ContextID test_world_id = this.globalContext.getIWorlds().keySet().iterator().next();
            World test_world = this.globalContext.getWorld(test_world_id);

            // Erstelle ein Raum
            Room test_proom = new Room("test_room", test_world, ContextMap.PRIVATE_ROOM_MAP, "11111");
            test_world.addPrivateRoom(test_proom);
            privateRoomTester.addRole(test_proom, Role.ROOM_OWNER);

            // Musik testen
            test_proom.playMusic(ContextMusic.DREAMS);
            Assert.assertEquals(ContextMusic.DREAMS, test_proom.getMusic());

            test_proom.stopMusic();
            Assert.assertNull(test_proom.getMusic());

        } catch (Exception e) {

        }
    }

    // auch isReservedBy, At, AtBy bei Area
    @Test
    public void addAreaReservationTest() {
        try {
            // Erstelle eine Welt
            this.userAccountManager.registerUser("pRMuTester", "11111");
            TestClientSender testClientSender = new TestClientSender();
            User privateRoomTester = this.userAccountManager.loginUser("pRMuTester", "11111",
                    testClientSender);
            privateRoomTester.addRole(this.globalContext, Role.OWNER);

            // Wegen Thread
            if (this.globalContext.getIWorlds().size() == 0) {
                this.globalContext.createWorld(privateRoomTester.getUserId(), "test_world", ContextMap.PUBLIC_ROOM_MAP);
                Assert.assertEquals(1, this.globalContext.getIWorlds().size());
            }
            ContextID test_world_id = this.globalContext.getIWorlds().keySet().iterator().next();
            World test_world = this.globalContext.getWorld(test_world_id);

            // Erstelle ein Raum
            Room test_proom = new Room("test_room", test_world, ContextMap.PRIVATE_ROOM_MAP, "11111");
            test_world.addPrivateRoom(test_proom);
            privateRoomTester.addRole(test_proom, Role.ROOM_OWNER);

            // Ein Benutzer macht eine AreaReservation
            this.userAccountManager.registerUser("ARTester", "11111");
            User aRTester = this.userAccountManager.loginUser("ARTester", "11111",
                    testClientSender);
            LocalDateTime from = LocalDateTime.now();
            LocalDateTime to = from.plusDays(1);
            test_proom.addReservation(aRTester, from, to);

            Assert.assertTrue(test_proom.isReservedAt(from, to));
            Assert.assertTrue(test_proom.isReservedBy(aRTester));
            Assert.assertTrue(test_proom.isReservedAtBy(aRTester, from, to));


        } catch (Exception e) {

        }


    }

    // noch nicht fertig
    @Test
    public void removeAreaReservationTest() {
        try {
            // Erstelle eine Welt
            this.userAccountManager.registerUser("pRMuTester", "11111");
            TestClientSender testClientSender = new TestClientSender();
            User privateRoomTester = this.userAccountManager.loginUser("pRMuTester", "11111",
                    testClientSender);
            privateRoomTester.addRole(this.globalContext, Role.OWNER);

            // Wegen Thread
            if (this.globalContext.getIWorlds().size() == 0) {
                this.globalContext.createWorld(privateRoomTester.getUserId(), "test_world", ContextMap.PUBLIC_ROOM_MAP);
                Assert.assertEquals(1, this.globalContext.getIWorlds().size());
            }
            ContextID test_world_id = this.globalContext.getIWorlds().keySet().iterator().next();
            World test_world = this.globalContext.getWorld(test_world_id);

            // Erstelle ein Raum
            Room test_proom = new Room("test_room", test_world, ContextMap.PRIVATE_ROOM_MAP, "11111");
            test_world.addPrivateRoom(test_proom);
            privateRoomTester.addRole(test_proom, Role.ROOM_OWNER);

            // Ein Benutzer macht eine AreaReservation
            this.userAccountManager.registerUser("ARTester", "11111");
            User aRTester = this.userAccountManager.loginUser("ARTester", "11111",
                    testClientSender);
            LocalDateTime from = LocalDateTime.now();
            LocalDateTime to = from.plusDays(1);
            test_proom.addReservation(aRTester, from, to);
            Assert.assertTrue(test_proom.isReservedBy(aRTester));

            // remove Reservation
            AreaReservation areaReservation = new AreaReservation(aRTester, test_proom, from, to);
            //privateRoomTester.executeAdministrativeAction();
            test_proom.removeReservation(areaReservation);

            //Assert.assertFalse(test_proom.isReservedBy(aRTester));


        } catch (Exception e) {

        }

    }
}
