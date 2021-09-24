package model.context;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import model.ContextParameters;
import model.MockGL20;
import model.RandomValues;
import model.SetUp;
import model.context.spatial.ContextMap;
import model.context.spatial.SpatialContext;
import model.exception.ContextNotFoundException;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class ContextTest {

    @BeforeClass
    public static void startGdx(){
        SetUp.setUpModelObserver();
        SpatialContext world = new SpatialContext(ContextParameters.WORLD_NAME, Context.getGlobal());
        SpatialContext room = new SpatialContext(ContextParameters.ROOM_NAME, world);
        world.addChild(room);
        Context.getGlobal().addChild(world);
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
    }

    @AfterClass
    public static void closeGdx(){
        Gdx.app.exit();
    }

    @Test
    public void addRemoveChildGetParent() {
        SpatialContext spatialContext = new SpatialContext(RandomValues.random8LengthString(), Context.getGlobal());
        Assert.assertEquals(spatialContext.getParent(), Context.getGlobal());
        Context.getGlobal().addChild(spatialContext);
        Assert.assertTrue(Context.getGlobal().children.containsValue(spatialContext));
        Context.getGlobal().removeChild(spatialContext);
        Assert.assertFalse(Context.getGlobal().children.containsValue(spatialContext));
    }

    @Test
    public void getContext() {
        ContextID contextId = ContextParameters.SEAT_ID;
        try {
            Assert.assertEquals(Context.getGlobal().
                    getContext(contextId).contextId, contextId);
        } catch (ContextNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Test(expected = ContextNotFoundException.class)
    public void  getContextException() throws ContextNotFoundException {
        ContextID contextId = new ContextID(RandomValues.randomString(2));
        Context.getGlobal().getContext(contextId);
    }

    @Test
    public void getGlobal() {
        Assert.assertEquals(Context.getGlobal().contextId.toString(), ContextParameters.GLOBAL_NAME);
    }
}