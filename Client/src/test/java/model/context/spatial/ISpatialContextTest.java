package model.context.spatial;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import model.communication.CommunicationMedium;
import model.communication.CommunicationRegion;
import model.context.Context;
import model.context.ContextID;
import model.user.UserManager;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import view2.IModelObserver;

import java.util.Set;

@Ignore
public class ISpatialContextTest {

    SpatialContext world;
    ContextMap map;
    ISpatialContextView testSpatialContextView;
    ISpatialContextView testWorldView;

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
            public void setWorldChanged() {
            }

            @Override
            public void setRoomChanged() {
            }

            @Override
            public void setMusicChanged() {
            }
        });
        world = new SpatialContext("world", Context.getGlobal());
        testWorldView = world;
        map = ContextMap.PUBLIC_ROOM_MAP;
        Game game = new Game() {
            @Override
            public void create() {
                world.build(map);
                Gdx.app.exit();
            }
        };
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        new Lwjgl3Application(game, config);
        testSpatialContextView = (SpatialContext)Context.getGlobal().getContext(new ContextID("Global.world.Disco"));
    }

    @After
    public void tearDown() throws Exception {
        world = null;
        map = null;
    }

    @Test
    public void getCommunicationRegion() {
        Assert.assertEquals(testSpatialContextView.getCommunicationRegion(), CommunicationRegion.RADIUS);
    }

    @Test
    public void getCommunicationMedia() {
        Assert.assertEquals(testSpatialContextView.getCommunicationMedia(),
                Set.of(CommunicationMedium.TEXT, CommunicationMedium.VOICE));
    }

    @Test
    public void getMap() {
        Assert.assertEquals(testWorldView.getMap(), map);
    }
}