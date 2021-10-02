package model.user;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import model.ContextParameters;
import model.MockGL20;
import model.RandomValues;
import model.SetUp;
import model.context.Context;
import model.context.ContextID;
import model.context.spatial.ContextMap;
import model.context.spatial.Direction;
import model.context.spatial.SpatialContext;
import model.exception.ContextNotFoundException;
import model.role.Permission;
import model.role.Role;
import org.junit.*;

import java.util.*;

public class UserTest {

    SpatialContext world;
    SpatialContext room;
    ContextMap map;
    UserManager userManager;
    InternUser internUser;
    User testUser;
    UUID userId;
    IUserController testUserController;
    IUserView testUserView;


    @Before
    public void setUp() throws Exception {
        SetUp.setUpModelObserver();
        userManager = UserManager.getInstance();

        world = new SpatialContext(ContextParameters.WORLD_NAME, Context.getGlobal());
        userManager.updateWorlds(Collections.singletonMap(world.getContextId(), world.getContextName()));

        room = new SpatialContext(ContextParameters.ROOM_NAME, world);
        userManager.login(RandomValues.randomUUID(), RandomValues.random8LengthString(),
                Status.ONLINE, RandomValues.randomEnum(Avatar.class));

        internUser = userManager.getInternUser();
        map = ContextMap.PUBLIC_ROOM_MAP;
        internUser.joinWorld(world.getContextId());
        userManager.updatePublicRoom(world.getContextId(), room.getContextId(), room.getContextName());
        internUser.joinRoom(room.getContextId(), room.getContextName(), map);
        internUser.setMovable(true);

        userId = RandomValues.randomUUID();
        testUser = new User(userId, RandomValues.random8LengthString(),
                Status.ONLINE, RandomValues.randomEnum(Avatar.class));
        testUserController = testUser;
        testUserView = testUser;
    }

    @After
    public void tearDown() {
        world = null;
        room = null;
        map = null;
        testUser = null;
        userId = null;
        testUserController = null;
        testUserView = null;
        UserManager.getInstance().logout();
    }

    @BeforeClass
    public static void startGdx(){
        new HeadlessApplication(new ApplicationAdapter() {
            @Override
            public void create(){
                Gdx.gl = new MockGL20();
            }
        });
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @AfterClass
    public static void closeGdx(){
        Gdx.app.exit();
    }

    @Test
    public void getUserId() {
        Assert.assertEquals(userId, testUserView.getUserId());
    }

    @Test
    public void setGetUsername() {
        String username = UUID.randomUUID().toString();
        testUserController.setUsername(username);
        Assert.assertEquals(username, testUserView.getUsername());
    }

    @Test
    public void setGetStatus() {
        Status status = Status.AWAY;
        testUserController.setStatus(status);
        Assert.assertEquals(status, testUserView.getStatus());
    }

    @Test
    public void setGetAvatar() {
        Avatar avatar = Avatar.ADAM;
        testUserController.setAvatar(avatar);
        Assert.assertEquals(avatar, testUserView.getAvatar());
    }

    @Test
    public void setIsInCurrentWorld() throws ContextNotFoundException {
        testUserController.joinWorld(world.getContextId());
        Assert.assertTrue(testUserView.isInCurrentWorld());
    }

    @Test
    public void setIsInCurrentRoom() throws ContextNotFoundException {
        testUserController.joinWorld(world.getContextId());
        testUserController.joinRoom(room.getContextId());
        testUserController.setLocation(ContextParameters.DISCO_LOCATION_X, ContextParameters.DISCO_LOCATION_Y,
                RandomValues.randomBoolean(), RandomValues.randomBoolean(), RandomValues.randomEnum(Direction.class));
        Assert.assertTrue(testUserView.isInCurrentRoom());
    }

    @Test
    public void setIsFriend() {
        boolean isFriend = RandomValues.randomBoolean();
        testUserController.setFriend(isFriend);
        Assert.assertEquals(isFriend, testUserView.isFriend());
    }

    @Test
    public void setIsIgnored() {
        boolean isIgnored = RandomValues.randomBoolean();
        testUserController.setIgnored(isIgnored);
        Assert.assertEquals(isIgnored, testUserView.isIgnored());
    }

    @Test
    public void setIsReport() throws ContextNotFoundException {
        float posX = ContextParameters.DISCO_LOCATION_X;
        float posY = ContextParameters.DISCO_LOCATION_Y;
        internUser.setLocation(posX, posY, RandomValues.randomBoolean(), RandomValues.randomBoolean(),
                RandomValues.randomEnum(Direction.class));

        testUserController.setReport(ContextParameters.DISCO_ID, true);
        Assert.assertTrue(testUserView.isReported());

        testUserController.setReport(ContextParameters.DISCO_ID, false);
        Assert.assertFalse(testUserView.isReported());
    }

    @Test
    public void setIsMute() throws ContextNotFoundException {
        float posX = ContextParameters.DISCO_LOCATION_X;
        float posY = ContextParameters.DISCO_LOCATION_Y;
        internUser.setLocation(posX, posY, RandomValues.randomBoolean(),
                RandomValues.randomBoolean(), RandomValues.randomEnum(Direction.class));

        testUserController.setMute(ContextParameters.DISCO_ID, true);
        Assert.assertTrue(testUserView.isMuted());

        testUserController.setMute(ContextParameters.DISCO_ID, false);
        Assert.assertFalse(testUserView.isMuted());
    }

    @Test
    public void setIsBan() throws ContextNotFoundException {
        float posX = ContextParameters.DISCO_LOCATION_X;
        float posY = ContextParameters.DISCO_LOCATION_Y;
        internUser.setLocation(posX, posY, RandomValues.randomBoolean(),
                RandomValues.randomBoolean(), RandomValues.randomEnum(Direction.class));
        userManager.addExternUser(testUser.getUserId(), testUser.getUsername(), testUser.getStatus(),
                testUser.getAvatar());

        testUserController.setBan(ContextParameters.DISCO_ID, true);
        Assert.assertTrue(testUserView.isBanned());

        userManager.addExternUser(testUser.getUserId(), testUser.getUsername(), testUser.getStatus(),
                testUser.getAvatar());

        testUserController.setBan(ContextParameters.DISCO_ID, false);
        Assert.assertFalse(testUserView.isBanned());
    }

    @Test
    public void setRoles_HasRoleGet_HighestRole() throws ContextNotFoundException {
        float posX = ContextParameters.PARK_LOCATION_X;
        float posY = ContextParameters.PARK_LOCATION_Y;
        internUser.setLocation(posX, posY, RandomValues.randomBoolean(),
                RandomValues.randomBoolean(), RandomValues.randomEnum(Direction.class));
        testUserController.setLocation(posX, posY, RandomValues.randomBoolean(),
                RandomValues.randomBoolean(), RandomValues.randomEnum(Direction.class));

        testUserController.setRoles(Context.getGlobal().getContextId(), Set.of(Role.ADMINISTRATOR));
        testUserController.setRoles(ContextParameters.PARK_Id, Set.of(Role.AREA_MANAGER));
        Assert.assertTrue(testUserView.hasRole(Role.ADMINISTRATOR));
        Assert.assertTrue(testUserView.hasRole(Role.AREA_MANAGER));

        posX = ContextParameters.DISCO_LOCATION_X;
        posY = ContextParameters.DISCO_LOCATION_Y;
        UserManager.getInstance().getInternUser().setLocation(posX, posY, RandomValues.randomBoolean(),
                RandomValues.randomBoolean(), RandomValues.randomEnum(Direction.class));

        Assert.assertFalse(testUserView.hasRole(Role.AREA_MANAGER));
        Assert.assertEquals(testUserView.getHighestRole(), Role.ADMINISTRATOR);
    }

    @Test
    public void hasPermission() throws ContextNotFoundException {
        float posX = ContextParameters.DISCO_LOCATION_X;
        float posY = ContextParameters.DISCO_LOCATION_Y;
        internUser.setLocation(posX, posY, RandomValues.randomBoolean(),
                RandomValues.randomBoolean(), RandomValues.randomEnum(Direction.class));
        testUserController.setLocation(posX, posY, RandomValues.randomBoolean(),
                RandomValues.randomBoolean(), RandomValues.randomEnum(Direction.class));
        internUser.setStatus(Status.ONLINE);

        testUserController.setRoles(ContextParameters.PARK_Id, Set.of(Role.AREA_MANAGER));

        Assert.assertFalse(testUserView.hasPermission(Permission.ASSIGN_ADMINISTRATOR));
        Assert.assertFalse(testUserView.hasPermission(Permission.MUTE));

        posX = ContextParameters.PARK_LOCATION_X;
        posY = ContextParameters.PARK_LOCATION_Y;
        internUser.setLocation(posX, posY, RandomValues.randomBoolean(),
                RandomValues.randomBoolean(), RandomValues.randomEnum(Direction.class));
        testUserController.setLocation(posX, posY, RandomValues.randomBoolean(),
                RandomValues.randomBoolean(), RandomValues.randomEnum(Direction.class));
        Assert.assertTrue(testUserView.hasPermission(Permission.MUTE));

        testUserController.setRoles(Context.getGlobal().getContextId(), Set.of(Role.OWNER));
        Assert.assertTrue(testUserView.hasPermission(Permission.ASSIGN_ADMINISTRATOR));
    }

    @Test
    public void setPosition_getCurrentLocation() {
        float posX = ContextParameters.DISCO_LOCATION_X;
        float posY = ContextParameters.DISCO_LOCATION_Y;
        testUserController.setLocation(posX, posY, RandomValues.randomBoolean(),
                RandomValues.randomBoolean(), RandomValues.randomEnum(Direction.class));
        Assert.assertEquals(posX, Objects.requireNonNull(testUserView.getLocation()).getPosX(), 0.0f);
        Assert.assertEquals(posY, testUserView.getLocation().getPosY(), 0.0f);
    }

    @Test
    public void canBeTeleported() throws ContextNotFoundException {
        testUserController.setFriend(true);
        testUserController.setStatus(Status.ONLINE);
        float posX = ContextParameters.DISCO_LOCATION_X;
        float posY = ContextParameters.DISCO_LOCATION_Y;
        testUserController.joinWorld(ContextParameters.WORLD_ID);
        testUserController.setLocation(posX, posY, RandomValues.randomBoolean(),
                RandomValues.randomBoolean(), RandomValues.randomEnum(Direction.class));

        Assert.assertTrue(testUserView.canTeleportTo());

        testUserController.setStatus(Status.BUSY);
        internUser.setStatus(Status.ONLINE);
        Assert.assertFalse(testUserView.canTeleportTo());

        Set<Role> moderator = new HashSet<>();
        moderator.add(Role.MODERATOR);
        assert internUser.getCurrentWorld() != null;
        internUser.setRoles(internUser.getCurrentWorld().getContextId(),
                moderator);
        Assert.assertTrue(testUserView.canTeleportTo());
    }

    @Test
    public void canBeInvited_canBeKicked() throws ContextNotFoundException {
        internUser.joinWorld(world.getContextId());
        internUser.joinRoom(room.getContextId(), ContextParameters.ROOM_NAME, ContextMap.PUBLIC_ROOM_MAP);
        testUserController.joinWorld(world.getContextId());
        testUserController.joinRoom(room.getContextId());

        ContextID privateRoomId =  ContextParameters.PRIVATE_ROOM_ID;
        Map<ContextID,String> privateRooms = new HashMap<>();
        privateRooms.put(privateRoomId, ContextParameters.PRIVATE_ROOM_NAME);
        testUserController.setStatus(Status.ONLINE);

        userManager.updatePrivateRooms(world.getContextId(), privateRooms);
        userManager.getInternUser().joinRoom(privateRoomId,
                ContextParameters.PRIVATE_ROOM_NAME, ContextMap.PRIVATE_ROOM_MAP);
        internUser.setInPrivateRoom(true);
        internUser.setLocation(ContextParameters.PRIVATE_ROOM_LOCATION_X, ContextParameters.PRIVATE_ROOM_LOCATION_Y,
                RandomValues.randomBoolean(), RandomValues.randomBoolean(), RandomValues.randomEnum(Direction.class));

        Set<Role> owner = new HashSet<>();
        owner.add(Role.ROOM_OWNER);
        internUser.setRoles(privateRoomId, owner);
        Assert.assertTrue(testUserView.canBeInvited());

        testUserController.leaveRoom();
        testUserController.joinRoom(privateRoomId);
        testUserController.setInPrivateRoom(true);
        testUserController.setLocation(ContextParameters.PRIVATE_ROOM_LOCATION_X, ContextParameters.PRIVATE_ROOM_LOCATION_Y,
                RandomValues.randomBoolean(), RandomValues.randomBoolean(), RandomValues.randomEnum(Direction.class));
        Assert.assertFalse(testUserView.canBeInvited());
        Assert.assertTrue(testUserView.canBeKicked());

        internUser.leaveWorld();
        Assert.assertFalse(testUserView.canBeKicked());
    }

    @Test
    public void canBeReported() throws ContextNotFoundException {
        testUserController.joinWorld(world.getContextId());
        Assert.assertTrue(testUserView.canBeReported());

        Set<Role> administrator = new HashSet<>();
        administrator.add(Role.ADMINISTRATOR);
        internUser.setRoles(Context.getGlobal().getContextId(), administrator);
        Assert.assertFalse(testUserView.canBeReported());
    }

    @Test
    public void canBeBanned() throws ContextNotFoundException {
        Set<Role> administrator = new HashSet<>();
        administrator.add(Role.ADMINISTRATOR);

        Set<Role> moderator = new HashSet<>();
        moderator.add(Role.MODERATOR);

        internUser.setRoles(Context.getGlobal().getContextId(), moderator);
        Assert.assertTrue(testUserView.canBeBanned());

        testUserController.setRoles(Context.getGlobal().getContextId(), moderator);
        Assert.assertFalse(testUserView.canBeBanned());

        UserManager.getInstance().getInternUser().setRoles(Context.getGlobal().getContextId(), administrator);
        Assert.assertTrue(testUserView.canBeBanned());
    }

    @Test
    public void canAssignModerator() throws ContextNotFoundException {
        Set<Role> administrator = new HashSet<>();
        administrator.add(Role.ADMINISTRATOR);

        internUser.setRoles(Context.getGlobal().getContextId(), administrator);
        Assert.assertTrue(testUser.canAssignModerator());

        testUserController.setRoles(Context.getGlobal().getContextId(), administrator);
        Assert.assertFalse(testUserView.canAssignModerator());
    }

    @Test
    public void canBeMuted() throws ContextNotFoundException {
        internUser.joinWorld(world.getContextId());
        internUser.joinRoom(room.getContextId(), ContextParameters.ROOM_NAME, ContextMap.PUBLIC_ROOM_MAP);
        testUserController.joinWorld(world.getContextId());
        testUserController.joinRoom(room.getContextId());

        Set<Role> administrator = new HashSet<>();
        administrator.add(Role.ADMINISTRATOR);
        testUserController.setStatus(Status.ONLINE);
        internUser.setRoles(Context.getGlobal().getContextId(), administrator);
        Assert.assertTrue(testUser.canBeMuted());

        testUserController.setRoles(Context.getGlobal().getContextId(), administrator);
        Assert.assertFalse(testUser.canBeMuted());
    }

    @Test
    public void isTeleportingIsSprinting() throws ContextNotFoundException {
        boolean isTeleporting = RandomValues.randomBoolean();
        boolean isSprinting = RandomValues.randomBoolean();

        testUserController.joinWorld(world.getContextId());
        testUserController.joinRoom(room.getContextId());
        testUserController.setLocation(ContextParameters.DISCO_LOCATION_X, ContextParameters.DISCO_LOCATION_Y,
                isTeleporting, isSprinting, RandomValues.randomEnum(Direction.class));

        Assert.assertEquals(testUserView.isTeleporting(), isTeleporting);
        Assert.assertEquals(testUserView.isSprinting(), isSprinting);
    }

    @Test
    public void isMoveable(){
        boolean moveable = RandomValues.randomBoolean();
        testUserController.setMovable(moveable);
        Assert.assertEquals(testUserView.isMovable(), moveable);
    }
}