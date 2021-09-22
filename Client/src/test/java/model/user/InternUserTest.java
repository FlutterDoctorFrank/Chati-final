package model.user;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import model.MessageBundle;
import model.MockGL20;
import model.context.Context;
import model.context.ContextID;
import model.context.global.GlobalContext;
import model.context.spatial.ContextMap;
import model.context.spatial.ContextMusic;
import model.context.spatial.Direction;
import model.context.spatial.SpatialContext;
import model.exception.ContextNotFoundException;
import model.exception.NotificationNotFoundException;
import model.exception.UserNotFoundException;
import model.notification.Notification;
import model.notification.NotificationType;
import org.junit.*;
import view2.IModelObserver;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.Assert.*;

public class InternUserTest {

    InternUser internUser;
    IInternUserController internUserController;
    IInternUserView internUserView;

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

        Status status = Status.ONLINE;
        UserManager.getInstance().login(UUID.randomUUID(), "internUser", status, Avatar.ADAM);
        internUser = UserManager.getInstance().getInternUser();
        internUser.joinWorld("World");
        SpatialContext room = new SpatialContext("Room", internUser.getCurrentWorld());
        internUser.getCurrentWorld().addChild(room);
        internUser.setCurrentRoom(room);
        internUser.setInCurrentRoom(true);
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
    public void tearDown() throws Exception {
        internUser.leaveWorld();
        internUser = null;
        UserManager.getInstance().logout();
        Gdx.app.exit();
    }



    @Test
    public void setGetMusic() throws ContextNotFoundException {
        ContextMusic contextMusic = ContextMusic.COUNTRYBODY;
        internUserController.setMusic(new ContextID("Global.World.Room.Disco"), contextMusic);
        int posX = 500;
        int posY = 1500;
        internUserController.setLocation(posX, posY, false, false, Direction.DOWN);
        Assert.assertEquals(internUserView.getMusic(), contextMusic);
    }

    @Test
    public void add_update_remove_Notification() throws ContextNotFoundException, NotificationNotFoundException {
        UUID notificationId = UUID.randomUUID();
        internUserController.addNotification(internUser.getCurrentWorld().getContextId(), notificationId,
                new MessageBundle("testMessage"), LocalDateTime.now(), NotificationType.FRIEND_REQUEST,
                false, true, true);
        Assert.assertTrue(internUserView.getWorldNotifications().containsKey(notificationId));
        Assert.assertFalse(internUserView.getWorldNotifications().get(notificationId).isRead());
        internUserController.updateNotification(notificationId,true, true, true);
        Assert.assertTrue(internUserView.getWorldNotifications().get(notificationId).isRead());
        internUserController.removeNotification(notificationId);
        Assert.assertFalse(internUserView.getWorldNotifications().containsKey(notificationId));
    }

    @Test
    public void getCurrentWorld() {
        Assert.assertEquals(internUserView.getCurrentWorld().getContextId(), new ContextID("Global.World"));
    }

    @Test
    public void getCurrentRoom() {
        Assert.assertEquals(internUserView.getCurrentRoom().getContextId(), new ContextID("Global.World.Room"));
    }

    @Test
    public void getGlobal_getWorld_Notifications() throws ContextNotFoundException {
        UUID worldNotificationId = UUID.randomUUID();
        UUID globalNotificationId = UUID.randomUUID();
        internUserController.addNotification(internUserView.getCurrentWorld().getContextId(), worldNotificationId,
                new MessageBundle("testMessage"), LocalDateTime.now(), NotificationType.FRIEND_REQUEST,
                true, true, true);
        internUserController.addNotification(Context.getGlobal().getContextId(), globalNotificationId,
                new MessageBundle("testMessage"), LocalDateTime.now(), NotificationType.FRIEND_REQUEST,
                true, true, true);
        Assert.assertTrue(internUserView.getWorldNotifications().containsKey(worldNotificationId));
        Assert.assertTrue(internUserView.getGlobalNotifications().containsKey(globalNotificationId));
    }

    @Test
    public void canTalk() throws ContextNotFoundException, UserNotFoundException {
        UUID externUserId = UUID.randomUUID();
        UserManager.getInstance().addExternUser(externUserId, "user1", Status.ONLINE, Avatar.ADAM);
        UserManager.getInstance().getExternUserController(externUserId).setCommunicable(true);
        internUserController.setMute(new ContextID("Global.World.Room.Disco"), false);
        int posX = 500;
        int posY = 1500;
        internUserController.setLocation(posX, posY, false, false, Direction.DOWN);
        Assert.assertTrue(internUserView.canTalk());
        UserManager.getInstance().getExternUserController(externUserId).setCommunicable(false);
        Assert.assertFalse(internUserView.canTalk());
        UserManager.getInstance().getExternUserController(externUserId).setCommunicable(true);
        posX = 1200;
        posY = 80;
        UserManager.getInstance().getInternUser().setLocation(posX, posY, false, false, Direction.DOWN);
        Assert.assertFalse(internUserView.canTalk());
    }

    @Test
    public void getCurrentInteractable() {
        int posX = 930;
        int posY = 1806;
        UserManager.getInstance().getInternUser().setLocation(posX, posY, false, false, Direction.UP);
        Assert.assertEquals(internUserView.getCurrentInteractable().getContextId(), new ContextID("Global.World.Room.Disco.Jukebox"));
        UserManager.getInstance().getInternUser().setLocation(posX, posY, false, false, Direction.DOWN);
        Assert.assertNull(internUserView.getCurrentInteractable());
        posX = 930;
        posY = 1606;
        UserManager.getInstance().getInternUser().setLocation(posX, posY, false, false, Direction.UP);
        Assert.assertNull(internUserView.getCurrentInteractable());
    }
}