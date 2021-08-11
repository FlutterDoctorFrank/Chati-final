package model.context.spatial;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import model.context.Context;
import model.context.ContextID;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import model.context.spatial.SpatialMap;

import static org.junit.Assert.*;

public class SpatialContextTest {

    SpatialContext world;
    SpatialMap map;

    @Before
    public void setUp() throws Exception {
         world = new SpatialContext(new ContextID("global.world"), "world", Context.getGlobal());
         map = SpatialMap.MAP;
    }

    @After
    public void tearDown() throws Exception {
        world = null;
        map = null;
    }

    @Test
    public void buildContextTree() {
        Game game = new Game() {
            @Override
            public void create() {
                world.build(map);
                Gdx.app.exit();
            }
        };
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        new Lwjgl3Application(game, config);
    }

    @Test
    public void getArea() {
    }

    @Test
    public void getExpanse() {
    }

    @Test
    public void getCommunicationRegion() {
    }

    @Test
    public void getCommunicationMedia() {
    }

    @Test
    public void getMap() {
    }
}