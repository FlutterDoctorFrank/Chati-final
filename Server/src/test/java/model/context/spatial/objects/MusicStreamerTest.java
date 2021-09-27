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
import model.role.Role;
import model.user.User;
import model.user.account.UserAccountManager;
import org.jetbrains.annotations.NotNull;
import org.junit.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;

public class MusicStreamerTest {
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
        public void send(@NotNull SendAction sendAction, @NotNull Object object) {

        }
    }


    private Area findMusicStreamer(Room test_room) {

        Assert.assertTrue(test_world.containsPrivateRoom(test_room));

        Area private_room = test_world.getPrivateRooms().get(test_room.getContextId());
        Area jukebox = null;
        Iterator<Area> area_iterator = private_room.getChildren().values().iterator();
        while (area_iterator.hasNext()) {
            Area current = area_iterator.next();
            //System.out.println(current.getContextName());
            if (current.getContextName().equals("Jukebox")) {
                jukebox = current;
                break;
            }
        }
        Assert.assertNotNull(jukebox);
        return jukebox;

    }

    @Test
    public void playMusicTest() {
        TestClientSender testClientSender = new TestClientSender();
        try {
            this.userAccountManager.registerUser("musicPlayer", "11111");
            User creater = UserAccountManager.getInstance().loginUser("musicPlayer", "11111",
                    testClientSender);
            creater.joinWorld(test_world.getContextId());
            Room test_room = new Room("test_room", this.test_world, ContextMap.PRIVATE_ROOM_MAP,
                    "11111");
            test_room.build();
            this.test_world.addPrivateRoom(test_room);
            creater.addRole(test_room, Role.ROOM_OWNER);
            creater.teleport(test_room.getSpawnLocation());
            Assert.assertEquals(test_room.getSpawnLocation(), creater.getLocation());

            // Find Jukebox(Area)
            Area jukebox = findMusicStreamer(test_room);
            MusicStreamer musicStreamer = null;
            if (jukebox instanceof MusicStreamer) {
                musicStreamer = (MusicStreamer) jukebox;
            }
            Assert.assertNotNull(musicStreamer);

            // Find Location
            Location jukebox_inter_loc = new Location(test_room, Direction.UP, 90, 70);
            creater.teleport(jukebox_inter_loc);
            Assert.assertEquals("Jukebox",
                    jukebox_inter_loc.getArea().getInteractable(musicStreamer.getContextId()).getContextName());

            // Interact, Play
            musicStreamer.interact(creater);
            Assert.assertFalse(creater.isMovable());
            String[] args = new String[]{ContextMusic.DREAMS.name()};
            musicStreamer.executeMenuOption(creater, 1, args);
            Assert.assertEquals(ContextMusic.DREAMS, test_room.getMusic());

            // Stop
            args = new String[]{};
            musicStreamer.executeMenuOption(creater, 3, args);
            Assert.assertNull(test_room.getMusic());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void pauseMusicTest() {
        TestClientSender testClientSender = new TestClientSender();
        try {
            this.userAccountManager.registerUser("musicPauser", "11111");
            User creater = UserAccountManager.getInstance().loginUser("musicPauser", "11111",
                    testClientSender);
            creater.joinWorld(test_world.getContextId());
            Room test_room = new Room("test_room", this.test_world, ContextMap.PRIVATE_ROOM_MAP,
                    "11111");
            test_room.build();
            this.test_world.addPrivateRoom(test_room);
            creater.addRole(test_room, Role.ROOM_OWNER);
            creater.teleport(test_room.getSpawnLocation());
            Assert.assertEquals(test_room.getSpawnLocation(), creater.getLocation());

            // Find Jukebox(Area)
            Area jukebox = findMusicStreamer(test_room);
            MusicStreamer musicStreamer = null;
            if (jukebox instanceof MusicStreamer) {
                musicStreamer = (MusicStreamer) jukebox;
            }
            Assert.assertNotNull(musicStreamer);

            // Find Location
            Location jukebox_inter_loc = new Location(test_room, Direction.UP, 90, 70);
            creater.teleport(jukebox_inter_loc);
            Assert.assertEquals("Jukebox",
                    jukebox_inter_loc.getArea().getInteractable(musicStreamer.getContextId()).getContextName());

            // Interact
            musicStreamer.interact(creater);
            Assert.assertFalse(creater.isMovable());
            String[] args = new String[]{ContextMusic.DREAMS.name()};
            musicStreamer.executeMenuOption(creater, 1, args);
            Assert.assertEquals(ContextMusic.DREAMS, test_room.getMusic());

            // Pause
            args = new String[]{};
            musicStreamer.executeMenuOption(creater, 2, args);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void noActionTest() {
        TestClientSender testClientSender = new TestClientSender();
        try {
            this.userAccountManager.registerUser("noAction", "11111");
            User creater = UserAccountManager.getInstance().loginUser("noAction", "11111",
                    testClientSender);
            creater.joinWorld(test_world.getContextId());
            Room test_room = new Room("test_room", this.test_world, ContextMap.PRIVATE_ROOM_MAP,
                    "11111");
            test_room.build();
            this.test_world.addPrivateRoom(test_room);
            creater.addRole(test_room, Role.ROOM_OWNER);
            creater.teleport(test_room.getSpawnLocation());
            Assert.assertEquals(test_room.getSpawnLocation(), creater.getLocation());

            // Find Jukebox(Area)
            Area jukebox = findMusicStreamer(test_room);
            MusicStreamer musicStreamer = null;
            if (jukebox instanceof MusicStreamer) {
                musicStreamer = (MusicStreamer) jukebox;
            }
            Assert.assertNotNull(musicStreamer);

            // Find Location
            Location jukebox_inter_loc = new Location(test_room, Direction.UP, 90, 70);
            creater.teleport(jukebox_inter_loc);
            Assert.assertEquals("Jukebox",
                    jukebox_inter_loc.getArea().getInteractable(musicStreamer.getContextId()).getContextName());

            // Interact
            creater.interact(musicStreamer.getContextId());
            creater.setCurrentInteractable(musicStreamer);
            Assert.assertFalse(creater.isMovable());
            String[] args = new String[]{};
            musicStreamer.executeMenuOption(creater, 0, args);
            Assert.assertNull(creater.getCurrentInteractable());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
