package model.context.spatial.objects;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.mock.graphics.MockGL20;
import controller.network.ClientSender;
import model.context.global.GlobalContext;
import model.context.spatial.*;
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

public class SeatTest {

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

    private Seat findSeat(String bankName, String seatName, String parentAreaName) {

        Area public_room = test_world.getPublicRoom();
        // Find Area (Park, Disco or Hotel)
        Area parentArea = null;
        Iterator<Area> area_iterator = public_room.getChildren().values().iterator();
        while (area_iterator.hasNext()) {
            Area current = area_iterator.next();
            //System.out.println(current.getContextName());
            if (current.getContextName().equals(parentAreaName)) {
                parentArea = current;
                break;
            }
        }
        Assert.assertNotNull(parentArea);

        // Find bank
        Area bank = null;
        Iterator<Area> bank_iterator = parentArea.getChildren().values().iterator();
        while (bank_iterator.hasNext()) {
            Area current = bank_iterator.next();
            //System.out.println(current.getContextName());
            if (current.getContextName().equals(bankName)) {
                bank = current;
                break;
            }
        }
        Assert.assertNotNull(bank);

        // Find seat
        Area seat = null;
        Iterator<Area> seat_iterator = bank.getChildren().values().iterator();
        while (seat_iterator.hasNext()) {
            Area current = seat_iterator.next();
            //System.out.println(current.getContextName());
            if (current.getContextName().equals(seatName)) {
                seat = current;
                break;
            }
        }
        Assert.assertNotNull(seat);

        Seat result = null;
        if (seat instanceof Seat) {
            result = (Seat) seat;
        }
        Assert.assertNotNull(result);
        return result;

    }

    @Test
    public void downDirectionSitTest(){
        TestClientSender testClientSender = new TestClientSender();
        try {
            this.userAccountManager.registerUser("down", "11111");
            User down = UserAccountManager.getInstance().loginUser("down", "11111",
                    testClientSender);
            down.joinWorld(test_world.getContextId());
            Location seat7_front_loc = new Location(test_world.getPublicRoom(), Direction.DOWN,
                    1312, 345);
            Seat seat = findSeat("Bank_4", "Seat_7", "Park");
            down.teleport(seat7_front_loc);

            // sit
            seat.interact(down);
            Assert.assertFalse(down.isMovable());
            Assert.assertEquals(seat, down.getCurrentInteractable());
            String[] args = new String[]{};
            seat.executeMenuOption(down, 1, args);
            Assert.assertFalse(down.isMovable());

            // stand up
            seat.interact(down);
            Assert.assertTrue(down.isMovable());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test(expected = IllegalInteractionException.class)
    public void wrongMenuOptionSitTest() throws Exception{
        TestClientSender testClientSender = new TestClientSender();
            this.userAccountManager.registerUser("wrongsit", "11111");
            User down = UserAccountManager.getInstance().loginUser("wrongsit", "11111",
                    testClientSender);
            down.joinWorld(test_world.getContextId());
        Location seat7_front_loc = new Location(test_world.getPublicRoom(), Direction.DOWN,
                1312, 345);
            Seat seat = findSeat("Bank_4", "Seat_7", "Park");
            down.teleport(seat7_front_loc);

            // sit
            seat.interact(down);
        String[] args = new String[]{};
        seat.executeMenuOption(down, -1, args);


    }

    @Test
    public void rightDirectionSitTest(){
        TestClientSender testClientSender = new TestClientSender();
        try {
            this.userAccountManager.registerUser("right", "11111");
            User right = UserAccountManager.getInstance().loginUser("right", "11111",
                    testClientSender);
            right.joinWorld(test_world.getContextId());
            Location seat12_front_loc = new Location(test_world.getPublicRoom(), Direction.RIGHT,
                    1873, 677);
            Seat seat = findSeat("Bank_6", "Seat_12", "Park");
            right.teleport(seat12_front_loc);

            // sit
            seat.interact(right);
            Assert.assertFalse(right.isMovable());
            Assert.assertEquals(seat, right.getCurrentInteractable());
            String[] args = new String[]{};
            seat.executeMenuOption(right, 1, args);
            Assert.assertFalse(right.isMovable());

            // stand up
            seat.interact(right);
            Assert.assertTrue(right.isMovable());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void leftDirectionSitTest(){
        TestClientSender testClientSender = new TestClientSender();
        try {
            this.userAccountManager.registerUser("left", "11111");
            User left = UserAccountManager.getInstance().loginUser("left", "11111",
                    testClientSender);
            left.joinWorld(test_world.getContextId());
            Location seat13_front_loc = new Location(test_world.getPublicRoom(), Direction.LEFT,
                    1231, 1320);
            Seat seat = findSeat("Bank_7", "Seat_13", "Hotel");
            left.teleport(seat13_front_loc);

            // sit
            seat.interact(left);
            Assert.assertFalse(left.isMovable());
            Assert.assertEquals(seat, left.getCurrentInteractable());
            String[] args = new String[]{};
            seat.executeMenuOption(left, 1, args);
            Assert.assertFalse(left.isMovable());

            // stand up
            seat.interact(left);
            Assert.assertTrue(left.isMovable());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void upDirectionSitTest(){
        TestClientSender testClientSender = new TestClientSender();
        try {
            this.userAccountManager.registerUser("upsit", "11111");
            User upsit = UserAccountManager.getInstance().loginUser("upsit", "11111",
                    testClientSender);
            upsit.joinWorld(test_world.getContextId());
            Location seat21_front_loc = new Location(test_world.getPublicRoom(), Direction.UP,
                    1250, 665);
            Seat seat = findSeat("Bank_11", "Seat_21", "Hotel");
            upsit.teleport(seat21_front_loc);

            // sit
            seat.interact(upsit);
            Assert.assertFalse(upsit.isMovable());
            Assert.assertEquals(seat, upsit.getCurrentInteractable());
            String[] args = new String[]{};
            seat.executeMenuOption(upsit, 1, args);
            Assert.assertFalse(upsit.isMovable());

            // stand up
            seat.interact(upsit);
            Assert.assertTrue(upsit.isMovable());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
