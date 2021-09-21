package model.context;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import model.context.spatial.SpatialContext;
import model.context.spatial.ContextMap;
import model.exception.ContextNotFoundException;
import model.user.UserManager;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import view2.IModelObserver;

@Ignore
public class ContextTest {

    SpatialContext world;
    SpatialContext room;
    ContextMap map;

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
        room = new SpatialContext("Room", world);
        map = ContextMap.PUBLIC_ROOM_MAP;
        Game game = new Game() {
            @Override
            public void create() {
                room.build(map);
                Gdx.app.exit();
            }
        };
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        new Lwjgl3Application(game, config);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void addChild() {
    }

    @Test
    public void removeChild() {
    }

    @Test
    public void getParent() {
    }

    @Test
    public void getContext() {
        ContextID contextId = new ContextID("Global.World.Room.Park.BankArea_1.Bank_3");
        try {
            Assert.assertEquals(Context.getGlobal().
                    getContext(contextId).contextId, contextId);
        } catch (ContextNotFoundException e) {
            e.printStackTrace();
        }
        contextId = new ContextID("keinContext");
        try {
            Context.getGlobal().getContext(contextId);
        } catch (ContextNotFoundException e) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void getGlobal() {
    }
}