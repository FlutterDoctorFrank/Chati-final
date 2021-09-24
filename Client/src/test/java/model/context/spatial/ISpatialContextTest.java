package model.context.spatial;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import model.ContextParameters;
import model.MockGL20;
import model.SetUp;
import model.communication.CommunicationMedium;
import model.communication.CommunicationRegion;
import model.context.Context;
import org.junit.*;

import java.util.Set;

public class ISpatialContextTest {

    ContextMap map;
    ISpatialContextView testRoomView;
    ISpatialContextView testSpatialContextView;


    @Before
    public void setUp() throws Exception {
        map = ContextMap.PUBLIC_ROOM_MAP;
        testRoomView = (SpatialContext)Context.getGlobal().getContext(ContextParameters.ROOM_ID);
        testSpatialContextView = (SpatialContext)Context.getGlobal().getContext(ContextParameters.DISCO_ID);
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
        Assert.assertEquals(testRoomView.getMap(), map);
    }
}