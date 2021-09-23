package model.user;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import model.MockGL20;
import model.context.Context;
import model.context.ContextID;
import model.context.spatial.Direction;
import model.context.spatial.ISpatialContextView;
import model.context.spatial.SpatialContext;
import model.context.spatial.ContextMap;
import model.exception.ContextNotFoundException;
import model.exception.UserNotFoundException;
import org.jetbrains.annotations.NotNull;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import view2.IModelObserver;

import java.util.*;

public class UserManagerTest {

    IUserManagerController testUserManagerController;
    IUserManagerView testUserManagerView;

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
        UserManager.getInstance().login(UUID.randomUUID(), "internUser", Status.ONLINE, Avatar.ALEX);
        SpatialContext world = new SpatialContext("World", Context.getGlobal());
        UserManager.getInstance().updateWorlds(Collections.singletonMap(world.getContextId(), world.getContextName()));
        UserManager.getInstance().getInternUser().joinWorld(world.getContextId());
        testUserManagerController = UserManager.getInstance();
        testUserManagerView = UserManager.getInstance();
    }

    @After
    public void tearDown() throws Exception {
        testUserManagerController = null;
        testUserManagerView = null;
        UserManager.getInstance().logout();
    }

    @Test(expected = IllegalStateException.class)
    public void login_getInternUser_logout() {
        UUID userId = UUID.randomUUID();
        UserManager.getInstance().login(userId, "internUser", Status.ONLINE, Avatar.ADAM);
        Assert.assertEquals(userId, UserManager.getInstance().getInternUser().getUserId());
        Assert.assertEquals(userId, testUserManagerView.getInternUserView().getUserId());
        UserManager.getInstance().logout();
        UserManager.getInstance().getInternUser();
    }



    @Test
    public void addRemoveGetExternUser() {
        UUID userId1 = UUID.randomUUID();
        UUID userId2 = UUID.randomUUID();
        Set<UUID> userSet = Set.of(userId1, userId2);
        testUserManagerController.addExternUser(userId1, userId1.toString(), Status.ONLINE, Avatar.ADAM);
        testUserManagerController.addExternUser(userId2, userId2.toString(), Status.ONLINE, Avatar.ADAM);
        try {
            Assert.assertEquals(testUserManagerView.getExternUserView(userId1).getUserId(), userId1);
            Assert.assertEquals(testUserManagerView.getExternUserView(userId2.toString()).getUserId(), userId2);
            testUserManagerController.getExternUsers().forEach((id, user) -> userSet.contains(id));
        } catch (UserNotFoundException e) {
            e.printStackTrace();
        }
        try {
            testUserManagerController.removeExternUser(userId1);
            testUserManagerView.getExternUserView(userId1).getUserId();
        } catch (UserNotFoundException e) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void getActiveUsers_getBannedUsers_getFriends() {
        ContextMap map = ContextMap.PUBLIC_ROOM_MAP;
        UUID userId1 = UUID.randomUUID();
        UUID userId2 = UUID.randomUUID();
        Set<UUID> userSet = Set.of(userId1, userId2);
        testUserManagerController.addExternUser(userId1, userId1.toString(), Status.ONLINE, Avatar.ADAM);
        testUserManagerController.addExternUser(userId2, userId2.toString(), Status.ONLINE, Avatar.ADAM);

        SpatialContext world = new SpatialContext("World", Context.getGlobal());
        Context.getGlobal().addChild(world);
        SpatialContext room = new SpatialContext("Room", world);
        world.addChild(room);
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
        room.build(ContextMap.PUBLIC_ROOM_MAP);
        Gdx.app.exit();

        testUserManagerController.getExternUsers().forEach((id, user) -> {
            try {
                user.joinWorld(world.getContextId());
            } catch (ContextNotFoundException e) {
                e.printStackTrace();
            }
        });
        userSet.forEach(id -> Assert.assertTrue(testUserManagerView.getActiveUsers().containsKey(id)));

        int posX = 500;
        int posY = 1500;

        UserManager.getInstance().getInternUser().setLocation(posX, posY, false, false, Direction.DOWN);
        testUserManagerController.getExternUsers().forEach((id, user) -> {
            try {
                user.setBan(UserManager.getInstance().getInternUser().getCurrentWorld().getContextId(), true);
            } catch (ContextNotFoundException e) {
                e.printStackTrace();
            }
        });
        userSet.forEach(id -> Assert.assertTrue(testUserManagerView.getBannedUsers().containsKey(id)));
        testUserManagerController.getExternUsers().forEach((id, user) -> user.setFriend(true));
        userSet.forEach(id -> Assert.assertTrue(testUserManagerView.getFriends().containsKey(id)));
    }

    @Test
    public void isLoggedIn() {
        Assert.assertTrue(UserManager.getInstance().isLoggedIn());
    }

    @Test
    public void getWorlds() {
        Map<ContextID, String> worlds = new HashMap<>();
        worlds.put(new ContextID("Global.TestWorld"), "TestWorld");
        UserManager.getInstance().updateWorlds(worlds);
        UserManager.getInstance().getWorlds().forEach((id, world) -> Assert.assertTrue(worlds.containsKey(world.getContextId())));
    }

    @Test
    public void getPrivateRooms() {
        Map<ContextID, String> rooms = new HashMap<>();
        rooms.put(new ContextID("Global.World.TestRoom"), "TestRoom");
        UserManager.getInstance().updateWorlds(rooms);
        UserManager.getInstance().getPrivateRooms().forEach((id, room) -> Assert.assertTrue(rooms.containsKey(room.getContextId())));
    }

}