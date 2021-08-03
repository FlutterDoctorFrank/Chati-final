package modelTest.Context.Global;

import model.Context.Global.GlobalContext;
import model.Context.Spatial.ISpatialContextView;
import model.Context.Spatial.SpatialContext;
import model.context.ContextID;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import view.Screens.IModelObserver;
import java.util.HashMap;
import java.util.Map;

public class GlobalContextTest {

    GlobalContext globalContext;

    @Before
    public void setUp() {
        globalContext = GlobalContext.getInstance();
        globalContext.setIModelObserver(new IModelObserver() {
            @Override
            public void setUserInfoChanged() {

            }

            @Override
            public void setUserNotificationChanged() {

            }

            @Override
            public void setUserPositionChanged() {

            }

            @Override
            public void setWorldInfoChanged() {

            }

            @Override
            public void setRoomInfoChanged() {

            }

            @Override
            public void setMapChanged() {

            }
        });
        Assert.assertEquals("Zu Beginn darf es keine CurrentWorld geben", null, globalContext.getWorld());
        Assert.assertEquals("Zu Beginn darf es keinen CurrenRoom geben", null, globalContext.getRoom());
        //Assert.assertTrue(globalContext.getWorlds().isEmpty());
    }

    @After
    public void tearDown() {
        globalContext = null;
    }

    @Test
    public void updateWorldsAndGetWorlds() {

/*        Map<ContextID, ISpatialContextView> worldsISpatialContextView = new HashMap();
        Map<ContextID, String> worldsString = new HashMap();
        ContextID contextID1 = new ContextID("global.world1");
        ISpatialContextView world1 = new SpatialContext("world1", globalContext, contextID1);
        worldsISpatialContextView.put(contextID1, world1);
        worldsString.put(contextID1, world1.getContextName());
        ContextID contextID2 = new ContextID("global.world2");
        ISpatialContextView world2 = new SpatialContext("world2", globalContext, contextID2);
        worldsISpatialContextView.put(contextID2, world2);
        worldsString.put(contextID2, world2.getContextName());

        globalContext.updateWorlds(worldsString);
        Assert.assertEquals(globalContext.getWorlds(), worldsISpatialContextView); */
    }

    @Test
    public void updateRooms() {

    }

    @Test
    public void setWorld() {
    }

    @Test
    public void setRoom() {
    }

    @Test
    public void setMusic() {
    }

    @Test
    public void getCurrentWold() {
    }

    @Test
    public void getCurrentRoom() {
    }

    @Test
    public void getInstance() {
    }

    @Test
    public void getWorld() {
    }

    @Test
    public void getRoom() {
    }
}