package model.user;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import model.*;
import model.context.Context;
import model.context.spatial.ContextMap;
import model.context.spatial.ContextMusic;
import model.context.spatial.Direction;
import model.context.spatial.SpatialContext;
import model.exception.ContextNotFoundException;
import model.exception.NotificationNotFoundException;
import model.exception.UserNotFoundException;
import model.notification.NotificationType;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Objects;
import java.util.UUID;

public class InternUserTest {

    UserManager userManager;
    InternUser internUser;
    IInternUserController internUserController;
    IInternUserView internUserView;

    @Before
    public void setUp() throws Exception {
        SetUp.setUpModelObserver();
        userManager = UserManager.getInstance();
        Status status = Status.ONLINE;

        userManager.login(RandomValues.randomUUID(), RandomValues.random8LengthString(),
                status, RandomValues.randomEnum(Avatar.class));
        SpatialContext world = new SpatialContext(ContextParameters.WORLD_NAME, Context.getGlobal());
        internUser = UserManager.getInstance().getInternUser();
        userManager.updateWorlds(Collections.singletonMap(world.getContextId(), world.getContextName()));
        internUser.joinWorld(world.getContextId());

        assert internUser.getCurrentWorld() != null;
        SpatialContext room = new SpatialContext(ContextParameters.ROOM_NAME, internUser.getCurrentWorld());
        internUser.getCurrentWorld().addChild(room);
        internUser.setCurrentRoom(room);

        new HeadlessApplication(new ApplicationAdapter() {
            @Override
            public void create() {
                Gdx.gl = new MockGL20();
            }
        });
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        room.build(ContextMap.PUBLIC_ROOM_MAP);

        internUserController = internUser;
        internUserView = internUser;
    }

    @After
    public void tearDown() {
        internUser.leaveWorld();
        internUser = null;
        UserManager.getInstance().logout();
        Gdx.app.exit();
    }



    @Test
    public void setGetMusic() throws ContextNotFoundException {
        ContextMusic contextMusic = ContextMusic.COUNTRYBODY;
        internUserController.setMusic(ContextParameters.DISCO_ID, contextMusic);
        float posX = ContextParameters.DISCO_LOCATION_X;
        float posY = ContextParameters.DISCO_LOCATION_Y;
        internUserController.setLocation(posX, posY, RandomValues.randomBoolean(), RandomValues.randomBoolean(),
                RandomValues.randomEnum(Direction.class));
        Assert.assertEquals(internUserView.getMusic(), contextMusic);
    }

    @Test
    public void add_update_remove_Notification() throws ContextNotFoundException, NotificationNotFoundException {
        UUID notificationId = RandomValues.randomUUID();
        assert internUser.getCurrentWorld() != null;
        internUserController.addNotification(internUser.getCurrentWorld().getContextId(), notificationId,
                new MessageBundle(RandomValues.random8LengthString()), LocalDateTime.now(),
                NotificationType.FRIEND_REQUEST, false, RandomValues.randomBoolean(), RandomValues.randomBoolean());
        Assert.assertTrue(internUserView.getWorldNotifications().containsKey(notificationId));
        Assert.assertFalse(internUserView.getWorldNotifications().get(notificationId).isRead());

        internUserController.updateNotification(notificationId,true,
                RandomValues.randomBoolean(), RandomValues.randomBoolean());
        Assert.assertTrue(internUserView.getWorldNotifications().get(notificationId).isRead());

        internUserController.removeNotification(notificationId);
        Assert.assertFalse(internUserView.getWorldNotifications().containsKey(notificationId));
    }

    @Test
    public void getCurrentWorld() {
        Assert.assertEquals(Objects.requireNonNull(internUserView.getCurrentWorld()).getContextId(), ContextParameters.WORLD_ID);
    }

    @Test
    public void getCurrentRoom() {
        Assert.assertEquals(Objects.requireNonNull(internUserView.getCurrentRoom()).getContextId(), ContextParameters.ROOM_ID);
    }

    @Test
    public void getGlobal_getWorld_Notifications() throws ContextNotFoundException {
        UUID worldNotificationId = RandomValues.randomUUID();
        UUID globalNotificationId = RandomValues.randomUUID();
        internUserController.addNotification(Objects.requireNonNull(internUserView.getCurrentWorld()).getContextId(), worldNotificationId,
                new MessageBundle(RandomValues.random8LengthString()), LocalDateTime.now(), NotificationType.FRIEND_REQUEST,
                true, true, true);
        internUserController.addNotification(Context.getGlobal().getContextId(), globalNotificationId,
                new MessageBundle(RandomValues.random8LengthString()), LocalDateTime.now(), NotificationType.FRIEND_REQUEST,
                true, true, true);
        Assert.assertTrue(internUserView.getWorldNotifications().containsKey(worldNotificationId));
        Assert.assertTrue(internUserView.getGlobalNotifications().containsKey(globalNotificationId));
    }

    @Test
    public void canTalk() throws ContextNotFoundException, UserNotFoundException {
        UUID externUserId = RandomValues.randomUUID();
        userManager.addExternUser(externUserId, RandomValues.random8LengthString(), Status.ONLINE,
                RandomValues.randomEnum(Avatar.class));
        userManager.getExternUserController(externUserId).setCommunicable(true);

        internUserController.setMute(ContextParameters.DISCO_ID, false);
        float posX = ContextParameters.DISCO_LOCATION_X;
        float posY = ContextParameters.DISCO_LOCATION_Y;
        internUserController.setLocation(posX, posY, RandomValues.randomBoolean(), RandomValues.randomBoolean(),
                RandomValues.randomEnum(Direction.class));
        Assert.assertTrue(internUserView.canTalk());
        userManager.getExternUserController(externUserId).setCommunicable(false);
        Assert.assertFalse(internUserView.canTalk());

        userManager.getExternUserController(externUserId).setCommunicable(true);
        posX = ContextParameters.PARK_LOCATION_X;
        posY = ContextParameters.PARK_LOCATION_Y;
        internUserController.setLocation(posX, posY,RandomValues.randomBoolean(), RandomValues.randomBoolean(),
                RandomValues.randomEnum(Direction.class));
        Assert.assertFalse(internUserView.canTalk());
    }

    @Test
    public void getCurrentInteractable() {
        float posX = ContextParameters.DISCO_LOCATION_JUKEBOX_INTERACT_X;
        float posY = ContextParameters.DISCO_LOCATION_JUKEBOX_INTERACT_Y;
        internUserController.setLocation(posX, posY, RandomValues.randomBoolean(), RandomValues.randomBoolean(), Direction.UP);
        Assert.assertEquals(Objects.requireNonNull(internUserView.getCurrentInteractable()).getContextId(), ContextParameters.DISCO_Jukebox_ID);

        UserManager.getInstance().getInternUser().setLocation(posX, posY, RandomValues.randomBoolean(), RandomValues.randomBoolean(), Direction.DOWN);
        Assert.assertNull(internUserView.getCurrentInteractable());

        posX = ContextParameters.DISCO_LOCATION_JUKEBOX_INTERACT_X;
        posY = ContextParameters.DISCO_LOCATION_JUKEBOX_INTERACT_Y - ContextParameters.TILE_LENGTH;
        UserManager.getInstance().getInternUser().setLocation(posX, posY, RandomValues.randomBoolean(), RandomValues.randomBoolean(), Direction.UP);
        Assert.assertNull(internUserView.getCurrentInteractable());
    }
}