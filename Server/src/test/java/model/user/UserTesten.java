package model.user;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backend.headless.mock.graphics.MockGL20;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import controller.network.ClientSender;
import model.context.Context;
import model.context.global.GlobalContext;
import model.context.spatial.SpatialMap;
import model.context.spatial.World;
import model.database.Database;
import model.database.IContextDatabase;
import model.database.IUserAccountManagerDatabase;
import model.database.IUserDatabase;
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
        // Erstellen ein Benutzer
        this.account_database = Database.getUserAccountManagerDatabase();
        this.user_database = Database.getUserDatabase();
        this.context_database = Database.getContextDatabase();
        UserAccountManager userAccountManager = UserAccountManager.getInstance();
        UserTesten.TestClientSender testClientSender = new UserTesten.TestClientSender();
        try {
            userAccountManager.registerUser("usertest", "11111");
            userAccountManager.loginUser("usertest", "11111", testClientSender);
            this.user = userAccountManager.getUser("usertest");
        } catch (Exception e) {
            e.printStackTrace();
        }


        // Erstellen eine Welt
        User performer = null;
        GlobalContext test_gbc = GlobalContext.getInstance();
        try {
            userAccountManager.registerUser("performer", "22222");
            userAccountManager.loginUser("performer", "22222", testClientSender);
            performer = userAccountManager.getUser("performer");

            this.user_database.addRole(performer, test_gbc, Role.OWNER);
            performer.addRole(test_gbc, Role.OWNER);
            performer = userAccountManager.getUser("performer");
        } catch (Exception e) {
            e.printStackTrace();
        }

        test_world = new World("test_world", SpatialMap.MAP);
        this.context_database.addWorld(test_world);
        try {
            test_gbc.createWorld(performer.getUserId(), "test_world", SpatialMap.MAP);
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

    }


    class TestClientSender implements ClientSender {
        public void send(SendAction sendAction, Object object) {

        }
    }


    @Test
    public void joinWorldTest() {

        /*
        try {
            this.user.joinWorld(test_world.getContextId());
        } catch (Exception e) {
            e.printStackTrace();
        }

         */

    }

    @Test
    public void leaveWorldTest() {

    }


    @Test
    public void moveTest() {

    }


    @Test
    public void chatTest() {

    }


    @Test
    public void talkTest() {

    }


    @Test
    public void executeAdministrativeActionTest() {

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

    }


    //Datenbank
    @Test
    public void addIgnoresTest() {

    }


    @Test
    public void ignoreTest() {

    }


    @Test
    public void removeFriendTest() {

    }


    @Test
    public void unignoreTest() {

    }


    @Test
    public void addRoleTest() {

    }


    @Test
    public void removeRoleTest() {

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

}
