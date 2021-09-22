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
import view2.IModelObserver;
import java.util.Collections;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

public class IUserTest {

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
}