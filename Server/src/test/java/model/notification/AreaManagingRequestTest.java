package model.notification;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.mock.graphics.MockGL20;
import controller.network.ClientSender;
import model.context.ContextID;
import model.context.global.GlobalContext;
import model.context.spatial.ContextMap;
import model.context.spatial.World;
import model.role.Role;
import model.user.User;
import model.user.account.UserAccountManager;
import org.junit.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;

public class AreaManagingRequestTest {

    AreaManagingRequest test_amr;
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

    @Test
    public void normalAccept() {
        TestClientSender testClientSender = new TestClientSender();
        try {
            // Erster Benutzer automatisch Besitzer
            this.userAccountManager.registerUser("normal1", "11111");
            sender = UserAccountManager.getInstance().loginUser("normal1", "11111", testClientSender);
            this.userAccountManager.registerUser("nreceiver1", "22222");
            receiver = UserAccountManager.getInstance().loginUser("nreceiver1", "22222",
                    testClientSender);
            receiver.addRole(GlobalContext.getInstance(), Role.ADMINISTRATOR);
            sender.joinWorld(this.test_world.getContextId());
            receiver.joinWorld(this.test_world.getContextId());

            LocalDateTime from = LocalDateTime.now();
            test_amr = new AreaManagingRequest(receiver, sender, test_world.getPublicRoom(), from,
                    from.plusDays(1));
            Assert.assertEquals(sender, test_amr.getRequestingUser());
            Assert.assertEquals(test_world.getPublicRoom(), test_amr.getRequestedArea());
            Assert.assertEquals(from, test_amr.getFrom());
            Assert.assertEquals(from.plusDays(1), test_amr.getTo());

            test_amr.accept();

            Assert.assertTrue(test_world.getPublicRoom().isReservedBy(sender));
            Assert.assertEquals(1, sender.getWorldNotifications().size());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Receiver No Permission
    @Test
    public void falseAccept1() {
        TestClientSender testClientSender = new TestClientSender();
        try {
            // Erster Benutzer automatisch Besitzer
            this.userAccountManager.registerUser("false1", "11111");
            sender = UserAccountManager.getInstance().loginUser("false1", "11111", testClientSender);
            this.userAccountManager.registerUser("freceiver1", "22222");
            receiver = UserAccountManager.getInstance().loginUser("freceiver1", "22222",
                    testClientSender);
            sender.joinWorld(this.test_world.getContextId());
            receiver.joinWorld(this.test_world.getContextId());

            LocalDateTime from = LocalDateTime.now();
            test_amr = new AreaManagingRequest(receiver, sender, test_world.getPublicRoom(), from,
                    from.plusDays(1));
            Assert.assertEquals(sender, test_amr.getRequestingUser());
            Assert.assertEquals(test_world.getPublicRoom(), test_amr.getRequestedArea());
            Assert.assertEquals(from, test_amr.getFrom());
            Assert.assertEquals(from.plusDays(1), test_amr.getTo());

            test_amr.accept();
            Assert.assertFalse(test_world.getPublicRoom().isReservedBy(sender));
            Assert.assertEquals(0, sender.getWorldNotifications().size());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    // Already Reserved
    @Test
    public void falseAccept2() {
        TestClientSender testClientSender = new TestClientSender();
        try {
            // Erster Benutzer automatisch Besitzer
            this.userAccountManager.registerUser("false2", "11111");
            sender = UserAccountManager.getInstance().loginUser("false2", "11111", testClientSender);
            this.userAccountManager.registerUser("nreceiver2", "22222");
            receiver = UserAccountManager.getInstance().loginUser("nreceiver2", "22222",
                    testClientSender);
            receiver.addRole(GlobalContext.getInstance(), Role.ADMINISTRATOR);
            sender.joinWorld(this.test_world.getContextId());
            receiver.joinWorld(this.test_world.getContextId());

            LocalDateTime from = LocalDateTime.now();
            test_amr = new AreaManagingRequest(receiver, sender, test_world.getPublicRoom(), from,
                    from.plusDays(1));
            Assert.assertEquals(sender, test_amr.getRequestingUser());
            Assert.assertEquals(test_world.getPublicRoom(), test_amr.getRequestedArea());
            Assert.assertEquals(from, test_amr.getFrom());
            Assert.assertEquals(from.plusDays(1), test_amr.getTo());

            test_amr.accept();
            Assert.assertTrue(test_world.getPublicRoom().isReservedBy(sender));
            Assert.assertEquals(1, sender.getWorldNotifications().size());

            /*
            // Duplicate
            AreaManagingRequest dup_request = new AreaManagingRequest(receiver, sender, test_world.getPublicRoom(),
                    from, from.plusDays(1));
            dup_request.accept();
            System.out.println(test_world.getPublicRoom().);
            // keine Rueck-Notifikation fuer 2.Request
            Assert.assertEquals(1, sender.getWorldNotifications().size());

             */

            // Another User also want to reserve
            this.userAccountManager.registerUser("naive", "33333");
            User naive = this.userAccountManager.loginUser("naive", "33333", testClientSender);
            naive.joinWorld(test_world.getContextId());

            AreaManagingRequest naive_request = new AreaManagingRequest(receiver, naive, test_world.getPublicRoom(),
                    from, from.plusDays(1));
            naive_request.accept();
            Assert.assertFalse(test_world.getPublicRoom().isReservedBy(naive));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // RequestingUser nicht existiert
    @Test
    public void falseAccept3() {
        TestClientSender testClientSender = new TestClientSender();
        try {
            // Erster Benutzer automatisch Besitzer
            this.userAccountManager.registerUser("false3", "11111");
            sender = UserAccountManager.getInstance().loginUser("false3", "11111", testClientSender);
            this.userAccountManager.registerUser("nreceiver3", "22222");
            receiver = UserAccountManager.getInstance().loginUser("nreceiver3", "22222",
                    testClientSender);
            receiver.addRole(GlobalContext.getInstance(), Role.ADMINISTRATOR);
            sender.joinWorld(this.test_world.getContextId());
            receiver.joinWorld(this.test_world.getContextId());

            LocalDateTime from = LocalDateTime.now();
            test_amr = new AreaManagingRequest(receiver, sender, test_world.getPublicRoom(), from,
                    from.plusDays(1));
            Assert.assertEquals(sender, test_amr.getRequestingUser());
            Assert.assertEquals(test_world.getPublicRoom(), test_amr.getRequestedArea());
            Assert.assertEquals(from, test_amr.getFrom());
            Assert.assertEquals(from.plusDays(1), test_amr.getTo());

            this.userAccountManager.deleteUser(sender);
            test_amr.accept();
            Assert.assertFalse(test_world.getPublicRoom().isReservedBy(sender));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void normalDecline() {
        TestClientSender testClientSender = new TestClientSender();
        try {
            // Erster Benutzer automatisch Besitzer
            this.userAccountManager.registerUser("normald1", "11111");
            sender = UserAccountManager.getInstance().loginUser("normald1", "11111", testClientSender);
            this.userAccountManager.registerUser("ndreceiver1", "22222");
            receiver = UserAccountManager.getInstance().loginUser("ndreceiver1", "22222",
                    testClientSender);
            receiver.addRole(GlobalContext.getInstance(), Role.ADMINISTRATOR);
            sender.joinWorld(this.test_world.getContextId());
            receiver.joinWorld(this.test_world.getContextId());

            LocalDateTime from = LocalDateTime.now();
            test_amr = new AreaManagingRequest(receiver, sender, test_world.getPublicRoom(), from,
                    from.plusDays(1));
            Assert.assertEquals(sender, test_amr.getRequestingUser());
            Assert.assertEquals(test_world.getPublicRoom(), test_amr.getRequestedArea());
            Assert.assertEquals(from, test_amr.getFrom());
            Assert.assertEquals(from.plusDays(1), test_amr.getTo());

            test_amr.decline();

            Assert.assertFalse(test_world.getPublicRoom().isReservedBy(sender));
            Assert.assertEquals(1, sender.getWorldNotifications().size());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // No Permission
    @Test
    public void falseDecline1() {
        TestClientSender testClientSender = new TestClientSender();
        try {
            // Erster Benutzer automatisch Besitzer
            this.userAccountManager.registerUser("falsed1", "11111");
            sender = UserAccountManager.getInstance().loginUser("falsed1", "11111", testClientSender);
            this.userAccountManager.registerUser("fdreceiver1", "22222");
            receiver = UserAccountManager.getInstance().loginUser("fdreceiver1", "22222",
                    testClientSender);

            sender.joinWorld(this.test_world.getContextId());
            receiver.joinWorld(this.test_world.getContextId());

            LocalDateTime from = LocalDateTime.now();
            test_amr = new AreaManagingRequest(receiver, sender, test_world.getPublicRoom(), from,
                    from.plusDays(1));
            Assert.assertEquals(sender, test_amr.getRequestingUser());
            Assert.assertEquals(test_world.getPublicRoom(), test_amr.getRequestedArea());
            Assert.assertEquals(from, test_amr.getFrom());
            Assert.assertEquals(from.plusDays(1), test_amr.getTo());

            test_amr.decline();

            Assert.assertFalse(test_world.getPublicRoom().isReservedBy(sender));
            Assert.assertEquals(0, sender.getWorldNotifications().size());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // RequestingUser nicht existiert
    @Test
    public void falseDecline2() {
        TestClientSender testClientSender = new TestClientSender();
        try {
            // Erster Benutzer automatisch Besitzer
            this.userAccountManager.registerUser("falsed2", "11111");
            sender = UserAccountManager.getInstance().loginUser("falsed2", "11111", testClientSender);
            this.userAccountManager.registerUser("fdreceiver2", "22222");
            receiver = UserAccountManager.getInstance().loginUser("fdreceiver2", "22222",
                    testClientSender);
            receiver.addRole(GlobalContext.getInstance(), Role.ADMINISTRATOR);
            sender.joinWorld(this.test_world.getContextId());
            receiver.joinWorld(this.test_world.getContextId());

            LocalDateTime from = LocalDateTime.now();
            test_amr = new AreaManagingRequest(receiver, sender, test_world.getPublicRoom(), from,
                    from.plusDays(1));
            Assert.assertEquals(sender, test_amr.getRequestingUser());
            Assert.assertEquals(test_world.getPublicRoom(), test_amr.getRequestedArea());
            Assert.assertEquals(from, test_amr.getFrom());
            Assert.assertEquals(from.plusDays(1), test_amr.getTo());

            this.userAccountManager.deleteUser(sender);
            test_amr.decline();
            Assert.assertFalse(test_world.getPublicRoom().isReservedBy(sender));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
