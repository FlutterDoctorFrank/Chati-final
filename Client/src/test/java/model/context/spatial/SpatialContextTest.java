package model.context.spatial;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import model.ContextParameters;
import model.MockGL20;
import model.SetUp;
import model.context.Context;
import model.exception.ContextNotFoundException;
import org.junit.*;

import java.util.Set;

public class SpatialContextTest {

    SpatialContext world;
    SpatialContext room;
    ContextMap map;

    @Before
    public void setUp() throws Exception {
         world = (SpatialContext) Context.getGlobal().getContext(ContextParameters.WORLD_ID);
         room = (SpatialContext) Context.getGlobal().getContext(ContextParameters.ROOM_ID);
         map = ContextMap.PUBLIC_ROOM_MAP;
    }

    @After
    public void tearDown() {
        world = null;
        map = null;
    }

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
    public void buildContextTree() {
        testChildContexts(Context.getGlobal(), ContextParameters.CONTEXT_ID_SET);
    }

    private void testChildContexts(Context root, Set<String> contextIdSet){
        root.getChildren().forEach((childId, child) -> {
           Assert.assertTrue(contextIdSet.contains(child.getContextId().toString()));
           testChildContexts(child, contextIdSet);
        });
    }

    @Test
    public void getArea() {
        float posX = ContextParameters.DISCO_LOCATION_X;
        float posY = ContextParameters.DISCO_LOCATION_Y;
        Assert.assertEquals((room.getArea(posX, posY).getContextId()), ContextParameters.DISCO_ID);
    }

    @Test
    public void getExpanse_isIn() {
        try {
            Assert.assertTrue(((SpatialContext)Context.getGlobal().
                    getContext(ContextParameters.DISCO_ID)).getExpanse().isIn(ContextParameters.DISCO_LOCATION_X,
                    ContextParameters.DISCO_LOCATION_Y));
        } catch (ContextNotFoundException e) {
            e.printStackTrace();
        }
    }
}