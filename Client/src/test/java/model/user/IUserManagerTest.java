package model.user;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import model.context.Context;
import model.context.spatial.SpatialContext;
import model.context.spatial.ContextMap;
import model.exception.ContextNotFoundException;
import model.exception.UserNotFoundException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import view2.IModelObserver;

import java.util.Set;
import java.util.UUID;

@Ignore
public class IUserManagerTest {

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
        testUserManagerController = UserManager.getInstance();
        testUserManagerView = UserManager.getInstance();
    }

    @After
    public void tearDown() throws Exception {
        testUserManagerController = null;
        testUserManagerView = null;
    }

    @Test
    public void login_getInternUser_logout() {
        UUID userId = UUID.randomUUID();
        UserManager.getInstance().login(userId, "internUser", Status.ONLINE, Avatar.BAT);
        Assert.assertEquals(userId, UserManager.getInstance().getInternUser().getUserId());
        Assert.assertEquals(userId, testUserManagerView.getInternUserView().getUserId());
        UserManager.getInstance().logout();
        Assert.assertEquals(null, UserManager.getInstance().getInternUser());
    }

    @Test
    public void addRemoveGetExternUser() {
        UUID userId1 = UUID.randomUUID();
        UUID userId2 = UUID.randomUUID();
        Set<UUID> userSet = Set.of(userId1, userId2);
        testUserManagerController.addExternUser(userId1, userId1.toString(), Status.ONLINE, Avatar.BAT);
        testUserManagerController.addExternUser(userId2, userId2.toString(), Status.ONLINE, Avatar.BAT);
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
        UserManager.getInstance().login(UUID.randomUUID(), "internUser", Status.ONLINE, Avatar.BAT);
        testUserManagerController.addExternUser(userId1, userId1.toString(), Status.ONLINE, Avatar.BAT);
        testUserManagerController.addExternUser(userId2, userId2.toString(), Status.ONLINE, Avatar.BAT);
        SpatialContext world = new SpatialContext("World", Context.getGlobal());
        SpatialContext room = new SpatialContext("Room", world);
        Game game = new Game() {
            @Override
            public void create() {
                room.build(map);
                Gdx.app.exit();
                UserManager.getInstance().getInternUser().joinWorld(world.getContextName());
                UserManager.getInstance().getInternUser().joinRoom(world.getContextId(), world.getContextName(), map);
            }
        };
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        new Lwjgl3Application(game, config);
        testUserManagerController.getExternUsers().forEach((id, user) -> user.setInCurrentWorld(true));
        userSet.forEach(id -> Assert.assertTrue(testUserManagerView.getActiveUsers().containsKey(id)));
        int posX = 500;
        int posY = 1500;
        UserManager.getInstance().getInternUser().setLocation(posX, posY, false, false, null);
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
        UserManager.getInstance().logout();
    }
}