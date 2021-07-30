package modelTest.Context.Global;

import model.Context.Context;
import model.Context.Global.GlobalContext;
import model.Context.Spatial.ISpatialContextView;
import model.Context.Spatial.SpatialContext;
import model.context.ContextID;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import java.util.HashMap;
import java.util.Map;

public class GlobalContextTest {

    GlobalContext globalContext;

    @Before
    void setUp() {
        System.out.println("aufgerufen");

        Assert.assertEquals("Zu Beginn darf es keine CurrentWorld geben", null, globalContext.getWorld());
        Assert.assertEquals("Zu Beginn darf es keinen CurrenRoom geben", null, globalContext.getRoom());
        Assert.assertTrue(globalContext.getWorlds().isEmpty());
    }

    @After
    void tearDown() {
        globalContext = null;
    }

    @Test
    void updateWorldsAndGetWorlds() {
        globalContext = GlobalContext.getInstance();

        Map<ContextID, SpatialContext> worldsISpatialContextView = new HashMap<>();
        Map<ContextID, String> worldsString = new HashMap<>();
        ContextID contextID1 = new ContextID("global.world1");
        SpatialContext world1 = new SpatialContext("world1", globalContext, contextID1);
        worldsISpatialContextView.put(contextID1, world1);
        worldsString.put(contextID1, world1.getContextName());
        ContextID contextID2 = new ContextID("global.world2");
        SpatialContext world2 = new SpatialContext("world1", globalContext, contextID2);
        worldsISpatialContextView.put(contextID2, world2);
        worldsString.put(contextID2, world2.getContextName());

        globalContext.updateWorlds(worldsString);
        worldsISpatialContextView.forEach((ID, world) -> Assertions.assertEquals(globalContext.getWorlds().get(ID),
                worldsISpatialContextView.get(ID) ));
    }

    @Test
    void updateRooms() {
        ContextID contextId1 = new ContextID("affe");
        ContextID contextId2 = new ContextID("affe");
        SpatialContext context1 = new SpatialContext(null, null, contextId1);
        SpatialContext context2 = new SpatialContext(null, null, contextId2);
        Assertions.assertEquals(contextId1, contextId2);
        Assertions.assertEquals(context1, context2);
    }

    @Test
    void setWorld() {
    }

    @Test
    void setRoom() {
    }

    @Test
    void setMusic() {
    }

    @Test
    void getCurrentWold() {
    }

    @Test
    void getCurrentRoom() {
    }

    @Test
    void getInstance() {
    }

    @Test
    void getWorld() {
    }

    @Test
    void getRoom() {
    }
}