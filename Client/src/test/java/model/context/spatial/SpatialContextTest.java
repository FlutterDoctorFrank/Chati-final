package model.context.spatial;

import model.context.Context;
import model.context.ContextID;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import model.context.spatial.SpatialMap;

import static org.junit.Assert.*;

public class SpatialContextTest {

    Context global;
    SpatialContext world;
    SpatialMap map;

    @Before
    public void setUp() throws Exception {
         global = new Context(new ContextID("global"), "global", null);
         world = new SpatialContext(new ContextID("global.world"), "world", global);
         map = SpatialMap.MAP;
    }

    @After
    public void tearDown() throws Exception {
        global = null;
        world = null;
        map = null;
    }

    @Test
    public void buildContextTree() {
        world.buildContextTree(map);
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