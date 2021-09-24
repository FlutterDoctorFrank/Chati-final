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
import model.exception.UserNotFoundException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

public class UserManagerTest {

    IUserManagerController testUserManagerController;
    IUserManagerView testUserManagerView;

    @Before
    public void setUp() throws Exception {
        SetUp.setUpModelObserver();
        UserManager.getInstance().login(RandomValues.randomUUID(), RandomValues.random8LengthString(), Status.ONLINE,
                RandomValues.randomEnum(Avatar.class));
        SpatialContext world = new SpatialContext(ContextParameters.WORLD_NAME, Context.getGlobal());
        UserManager.getInstance().updateWorlds(Collections.singletonMap(world.getContextId(), world.getContextName()));
        UserManager.getInstance().getInternUser().joinWorld(world.getContextId());
        testUserManagerController = UserManager.getInstance();
        testUserManagerView = UserManager.getInstance();
    }

    @After
    public void tearDown() {
        testUserManagerController = null;
        testUserManagerView = null;
        UserManager.getInstance().logout();
    }

    @Test(expected = IllegalStateException.class)
    public void login_getInternUser_logout() {
        UUID userId = RandomValues.randomUUID();
        UserManager.getInstance().login(userId, RandomValues.random8LengthString(), Status.ONLINE,
                RandomValues.randomEnum(Avatar.class));
        Assert.assertEquals(userId, Objects.requireNonNull(testUserManagerView.getInternUserView()).getUserId());
        UserManager.getInstance().logout();
        Assert.assertNull(testUserManagerView.getInternUserView());
    }



    @Test(expected = UserNotFoundException.class)
    public void addRemoveGetExternUser() throws UserNotFoundException {
        UUID userId1 = RandomValues.randomUUID();
        UUID userId2 = RandomValues.randomUUID();
        Set<UUID> userSet = Set.of(userId1, userId2);
        testUserManagerController.addExternUser(userId1, RandomValues.random8LengthString(), Status.ONLINE,
                RandomValues.randomEnum(Avatar.class));
        testUserManagerController.addExternUser(userId2, RandomValues.random8LengthString(), Status.ONLINE,
                RandomValues.randomEnum(Avatar.class));

        Assert.assertEquals(testUserManagerView.getExternUserView(userId1).getUserId(), userId1);
        Assert.assertEquals(testUserManagerView.getExternUserView(userId2.toString()).getUserId(), userId2);
        testUserManagerController.getExternUsers().forEach((id, user) -> Assert.assertTrue(userSet.contains(id)));

        testUserManagerController.removeExternUser(userId1);
        testUserManagerView.getExternUserView(userId1).getUserId();
    }

    @Test
    public void getActiveUsers_getBannedUsers_getFriends() {
        UUID userId1 = RandomValues.randomUUID();
        UUID userId2 = RandomValues.randomUUID();

        Set<UUID> userSet = Set.of(userId1, userId2);
        testUserManagerController.addExternUser(userId1, RandomValues.random8LengthString(), Status.ONLINE,
                RandomValues.randomEnum(Avatar.class));
        testUserManagerController.addExternUser(userId2, RandomValues.random8LengthString(), Status.ONLINE,
                RandomValues.randomEnum(Avatar.class));

        SpatialContext world = new SpatialContext(ContextParameters.WORLD_NAME, Context.getGlobal());
        Context.getGlobal().addChild(world);

        SpatialContext room = new SpatialContext(ContextParameters.ROOM_NAME, world);
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

        float posX = ContextParameters.DISCO_LOCATION_X;
        float posY = ContextParameters.DISCO_LOCATION_Y;

        UserManager.getInstance().getInternUser().setLocation(posX, posY, RandomValues.randomBoolean(),
                RandomValues.randomBoolean(), RandomValues.randomEnum(Direction.class));
        testUserManagerController.getExternUsers().forEach((id, user) -> {
            try {
                user.setBan(Objects.requireNonNull(UserManager.getInstance().getInternUser().getCurrentWorld()).getContextId(), true);
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
        worlds.put(ContextParameters.WORLD_ID, ContextParameters.WORLD_NAME);
        UserManager.getInstance().updateWorlds(worlds);
        UserManager.getInstance().getWorlds().forEach((id, world) ->
                Assert.assertTrue(worlds.containsKey(world.getContextId())));
    }

    @Test
    public void getPrivateRooms() {
        Map<ContextID, String> rooms = new HashMap<>();
        rooms.put(ContextParameters.PRIVATE_ROOM_ID, ContextParameters.PRIVATE_ROOM_NAME);
        UserManager.getInstance().updateWorlds(rooms);
        UserManager.getInstance().getPrivateRooms().forEach((id, room) ->
                Assert.assertTrue(rooms.containsKey(room.getContextId())));
    }
}