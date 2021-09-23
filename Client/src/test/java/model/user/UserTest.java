package model.user;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import model.MockGL20;
import model.context.Context;
import model.context.ContextID;
import model.context.spatial.ContextMap;
import model.context.spatial.Direction;
import model.context.spatial.SpatialContext;
import model.exception.ContextNotFoundException;
import model.role.Permission;
import model.role.Role;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.ls.LSOutput;
import view2.IModelObserver;

import javax.print.DocFlavor;
import java.util.*;

public class UserTest {

    SpatialContext world;
    SpatialContext room;
    ContextMap map;
    User testUser;
    UUID userId;
    IUserController testUserController;
    IUserView testUserView;

    @Before
    public void setUp() throws Exception {
        UserManager.getInstance().setModelObserver(new IModelObserver() {
            @Override
            public void setUserInfoChanged() {
            }

            @Override
            public void setUserNotificationChanged() {
            }

            @Override
            public void setWorldListChanged() {

            }

            @Override
            public void setRoomListChanged() {

            }

            @Override
            public void setNewNotificationReceived() {

            }

            @Override
            public void setWorldChanged() {
            }

            @Override
            public void setRoomChanged() {
            }

            @Override
            public void setMusicChanged() {
            }
        });
        world = new SpatialContext("World", Context.getGlobal());
        UserManager.getInstance().updateWorlds(Collections.singletonMap(world.getContextId(), world.getContextName()));
        room = new SpatialContext("Room", world);
        UserManager.getInstance().login(UUID.randomUUID(), "internUser", Status.ONLINE, Avatar.ADAM);
        map = ContextMap.PUBLIC_ROOM_MAP;
        UserManager.getInstance().getInternUser().joinWorld(world.getContextId());
        UserManager.getInstance().updatePublicRoom(world.getContextId(), room.getContextId(), room.getContextName());
        UserManager.getInstance().getInternUser().joinRoom(room.getContextId(), room.getContextName(), map);
        userId = UUID.randomUUID();
        testUser = new User(userId, "initialName", Status.ONLINE, Avatar.ADAM);
        testUserController = testUser;
        testUserView = testUser;
    }

    @After
    public void tearDown() throws Exception {
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
        testUserController.setLocation(1200.0f, 1500.0f, true, false, Direction.DOWN);
        Assert.assertTrue(testUserView.isInCurrentRoom());
    }

    @Test
    public void setIsFriend() {
        boolean isFriend = new Random().nextBoolean();
        testUserController.setFriend(isFriend);
        Assert.assertEquals(isFriend, testUserView.isFriend());
    }

    @Test
    public void setIsIgnored() {
        boolean isIgnored = new Random().nextBoolean();
        testUserController.setIgnored(isIgnored);
        Assert.assertEquals(isIgnored, testUserView.isIgnored());
    }

    @Test
    public void setIsReport() {
        int posX = 500;
        int posY = 1500;
        UserManager.getInstance().getInternUser().setLocation(posX, posY, false, false, Direction.DOWN);
        try {
            testUserController.setReport(new ContextID("Global.World.Room.Disco"), true);
        } catch (ContextNotFoundException e) {
            e.printStackTrace();
        }
        Assert.assertTrue(testUserView.isReported());
        try {
            testUserController.setReport(new ContextID("Global.World.Room.Disco"), false);
        } catch (ContextNotFoundException e) {
            e.printStackTrace();
        }
        Assert.assertFalse(testUserView.isReported());
    }

    @Test
    public void setIsMute() {
        int posX = 500;
        int posY = 1500;
        UserManager.getInstance().getInternUser().setLocation(posX, posY, false, false, Direction.DOWN);
        try {
            testUserController.setMute(new ContextID("Global.World.Room.Disco"), true);
        } catch (ContextNotFoundException e) {
            e.printStackTrace();
        }
        Assert.assertTrue(testUserView.isMuted());
        try {
            testUserController.setMute(new ContextID("Global.World.Room.Disco"), false);
        } catch (ContextNotFoundException e) {
            e.printStackTrace();
        }
        Assert.assertFalse(testUserView.isMuted());
    }

    @Test
    public void setIsBan() {
        int posX = 500;
        int posY = 1500;
        UserManager.getInstance().getInternUser().setLocation(posX, posY, false, false, Direction.DOWN);
        UserManager.getInstance().addExternUser(testUser.getUserId(), testUser.getUsername(), testUser.getStatus(),
                testUser.getAvatar());
        try {
            testUserController.setBan(new ContextID("Global.World.Room.Disco"), true);
        } catch (ContextNotFoundException e) {
            e.printStackTrace();
        }
        Assert.assertTrue(testUserView.isBanned());
        UserManager.getInstance().addExternUser(testUser.getUserId(), testUser.getUsername(), testUser.getStatus(),
                testUser.getAvatar());
        try {
            testUserController.setBan(new ContextID("Global.World.Room.Disco"), false);
        } catch (ContextNotFoundException e) {
            e.printStackTrace();
        }
        Assert.assertFalse(testUserView.isBanned());
    }

    @Test
    public void setRoles_HasRoleGet_HighestRole() {
        int posX = 1200;
        int posY = 80;
        UserManager.getInstance().getInternUser().setLocation(posX, posY, false, false, Direction.DOWN);
        testUser.setLocation(posX, posY, false, false, Direction.DOWN);

        try {
            testUserController.setRoles(Context.getGlobal().getContextId(), Set.of(Role.ADMINISTRATOR));
            testUserController.setRoles(new ContextID("Global.World.Room.Park"), Set.of(Role.AREA_MANAGER));
        } catch (ContextNotFoundException e) {
            e.printStackTrace();
        }
        Assert.assertTrue(testUserView.hasRole(Role.ADMINISTRATOR));
        Assert.assertTrue(testUserView.hasRole(Role.AREA_MANAGER));
        posX = 500;
        posY = 1500;
        UserManager.getInstance().getInternUser().setLocation(posX, posY, false, false, Direction.DOWN);
        Assert.assertFalse(testUserView.hasRole(Role.AREA_MANAGER));
        Assert.assertEquals(testUserView.getHighestRole(), Role.ADMINISTRATOR);
    }

    @Test
    public void hasPermission() {
        int posX = 1200;
        int posY = 1500;
        UserManager.getInstance().getInternUser().setLocation(posX, posY, false, false, Direction.DOWN);
        testUser.setLocation(posX, posY, false, false, Direction.DOWN);
        try {
            testUserController.setRoles(new ContextID("Global.World.Room.Park"), Set.of(Role.AREA_MANAGER));
        } catch (ContextNotFoundException e) {
            e.printStackTrace();
        }
        Assert.assertFalse(testUserView.hasPermission(Permission.ASSIGN_ADMINISTRATOR));
        Assert.assertFalse(testUserView.hasPermission(Permission.MUTE));
        posY = 80;
        UserManager.getInstance().getInternUser().setLocation(posX, posY, false, false, Direction.DOWN);
        testUser.setLocation(posX, posY, false, false, Direction.DOWN);
        Assert.assertTrue(testUserView.hasPermission(Permission.MUTE));
        try {
            testUserController.setRoles(Context.getGlobal().getContextId(), Set.of(Role.OWNER));
        } catch (ContextNotFoundException e) {
            e.printStackTrace();
        }
        Assert.assertTrue(testUserView.hasPermission(Permission.ASSIGN_ADMINISTRATOR));
    }

    @Test
    public void setPosition_getCurrentLocation() {
        float posX = 1200.0f;
        float posY = 1500.0f;
        testUserController.setLocation(posX, posY, false, false, Direction.DOWN);
        Assert.assertEquals(posX, testUserView.getLocation().getPosX(), 0.0f);
        Assert.assertEquals(posY, testUserView.getLocation().getPosY(), 0.0f);
    }

    @Test
    public void canBeTeleported() throws ContextNotFoundException {
        testUserController.setFriend(true);
        testUserController.setStatus(Status.ONLINE);
        float posX = 1800.0f;
        float posY = 1500.0f;
        testUserController.setLocation(posX, posY, false, false, Direction.DOWN);
        Assert.assertTrue(testUserView.canTeleportTo());
        testUserController.setStatus(Status.BUSY);
        Assert.assertFalse(testUserView.canTeleportTo());
        Set<Role> moderator = new HashSet<Role>();
        moderator.add(Role.MODERATOR);
        UserManager.getInstance().getInternUser().setRoles(UserManager.getInstance().getInternUser().getCurrentWorld().getContextId(),
                moderator);
        Assert.assertTrue(testUserView.canTeleportTo());
    }

    @Test
    public void canBeInvited_canBeKicked() throws ContextNotFoundException {
        ContextID privateRoomId =  new ContextID("Global.World.PrivateRoom");
        UserManager.getInstance().getInternUser().joinWorld(world.getContextId());
        UserManager.getInstance().getInternUser().joinRoom(room.getContextId(),
                "Room", ContextMap.PUBLIC_ROOM_MAP);
        testUser.joinWorld(world.getContextId());
        testUser.joinRoom(room.getContextId());
        Map<ContextID,String> privateRooms = new HashMap<>();
        privateRooms.put(privateRoomId, "PrivateRoom");
        testUser.setStatus(Status.ONLINE);
        UserManager.getInstance().updatePrivateRooms(world.getContextId(), privateRooms);
        UserManager.getInstance().getInternUser().joinRoom(privateRoomId,
                "PrivateRoom", ContextMap.PRIVATE_ROOM_MAP);
        UserManager.getInstance().getInternUser().setInPrivateRoom(true);
        UserManager.getInstance().getInternUser().setLocation(40, 40, true, false, Direction.DOWN);
        Set<Role> owner = new HashSet<>();
        owner.add(Role.ROOM_OWNER);
        UserManager.getInstance().getInternUser().setRoles(privateRoomId, owner);
        Assert.assertTrue(testUser.canBeInvited());
        testUser.leaveRoom();
        testUser.joinRoom(privateRoomId);
        testUser.setInPrivateRoom(true);
        testUser.setLocation(40, 40, true, false, Direction.DOWN);
        Assert.assertFalse(testUser.canBeInvited());
        Assert.assertTrue(testUser.canBeKicked());
        UserManager.getInstance().getInternUser().leaveWorld();
        Assert.assertFalse(testUser.canBeKicked());

    }

    @Test
    public void canBeReported() throws ContextNotFoundException {
        testUser.joinWorld(world.getContextId());
        Assert.assertTrue(testUser.canBeReported());
        Set<Role> administrator = new HashSet<>();
        administrator.add(Role.ADMINISTRATOR);
        UserManager.getInstance().getInternUser().setRoles(Context.getGlobal().getContextId(), administrator);
        Assert.assertFalse(testUser.canBeReported());
    }

    @Test
    public void canBeBanned() throws ContextNotFoundException {
        Set<Role> administrator = new HashSet<>();
        administrator.add(Role.ADMINISTRATOR);
        Set<Role> moderator = new HashSet<>();
        moderator.add(Role.MODERATOR);
        UserManager.getInstance().getInternUser().setRoles(Context.getGlobal().getContextId(), moderator);
        Assert.assertTrue(testUser.canBeBanned());
        testUser.setRoles(Context.getGlobal().getContextId(), moderator);
        Assert.assertFalse(testUser.canBeBanned());
        UserManager.getInstance().getInternUser().setRoles(Context.getGlobal().getContextId(), administrator);
        Assert.assertTrue(testUser.canBeBanned());
    }

    @Test
    public void canAssignModerator() throws ContextNotFoundException {
        Set<Role> administrator = new HashSet<>();
        administrator.add(Role.ADMINISTRATOR);
        UserManager.getInstance().getInternUser().setRoles(Context.getGlobal().getContextId(), administrator);
        Assert.assertTrue(testUser.canAssignModerator());
        testUser.setRoles(Context.getGlobal().getContextId(), administrator);
        Assert.assertFalse(testUser.canAssignModerator());
    }

    @Test
    public void canBeMuted() throws ContextNotFoundException {
        UserManager.getInstance().getInternUser().joinWorld(world.getContextId());
        UserManager.getInstance().getInternUser().joinRoom(room.getContextId(),
                "Room", ContextMap.PUBLIC_ROOM_MAP);
        testUser.joinWorld(world.getContextId());
        testUser.joinRoom(room.getContextId());
        Set<Role> administrator = new HashSet<>();
        administrator.add(Role.ADMINISTRATOR);
        testUser.setStatus(Status.ONLINE);
        UserManager.getInstance().getInternUser().setRoles(Context.getGlobal().getContextId(), administrator);
        Assert.assertTrue(testUser.canBeMuted());
        testUser.setRoles(Context.getGlobal().getContextId(), administrator);
        Assert.assertFalse(testUser.canBeMuted());
    }

    @Test
    public void isTeleportingIsSprinting() throws ContextNotFoundException {
        boolean isTeleporting = true;
        boolean isSprinting = true;
        testUser.joinWorld(world.getContextId());
        testUser.joinRoom(room.getContextId());
        testUserController.setLocation(40, 40, isTeleporting, isSprinting, Direction.DOWN);
        Assert.assertEquals(testUserView.isTeleporting(), isTeleporting);
        Assert.assertEquals(testUserView.isSprinting(), isSprinting);
    }

    @Test
    public void isMoveable(){
        boolean moveable = true;
        testUserController.setMovable(moveable);
        Assert.assertEquals(testUserView.isMovable(), moveable);
    }
}