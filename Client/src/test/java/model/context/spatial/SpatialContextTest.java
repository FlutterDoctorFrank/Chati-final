package model.context.spatial;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import model.MockGL20;
import model.context.Context;
import model.context.ContextID;
import model.exception.ContextNotFoundException;
import model.user.UserManager;
import org.junit.*;
import view2.IModelObserver;

import java.util.Set;

public class SpatialContextTest {

    SpatialContext world;
    SpatialContext room;
    ContextMap map;

    @Before
    public void setUp() throws Exception {
         world = (SpatialContext) Context.getGlobal().getContext(new ContextID("Global.World"));
         room = (SpatialContext) Context.getGlobal().getContext(new ContextID("Global.World.Room"));
         map = ContextMap.PUBLIC_ROOM_MAP;
    }

    @After
    public void tearDown() throws Exception {
        world = null;
        map = null;
    }

    @BeforeClass
    public static void startGdx(){
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
        SpatialContext world = new SpatialContext("World", Context.getGlobal());
        SpatialContext room = new SpatialContext("Room", world);
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
        Set<String> contextIdSet = Set.of(
                "Global",
                "Global.World",
                "Global.World.Room",
                "Global.World.Room.Park",
                "Global.World.Room.Disco",
                "Global.World.Room.Hotel",
                "Global.World.Room.Park.Spawn",
                "Global.World.Room.Hotel.Reception",
                "Global.World.Room.Park.Podium",
                "Global.World.Room.Disco.Bar",
                "Global.World.Room.Disco.Table_1",
                "Global.World.Room.Disco.Table_2",
                "Global.World.Room.Disco.Table_1.GameBoard_1",
                "Global.World.Room.Disco.Table_2.GameBoard_2",
                "Global.World.Room.Hotel.Bank_11",
                "Global.World.Room.Hotel.Bank_12",
                "Global.World.Room.Disco.Jukebox",
                "Global.World.Room.Hotel.Bank_8",
                "Global.World.Room.Park.Bank_3",
                "Global.World.Room.Park.Bank_6",
                "Global.World.Room.Hotel.Bank_7",
                "Global.World.Room.Hotel.Bank_9",
                "Global.World.Room.Hotel.Bank_10",
                "Global.World.Room.Park.Bank_1",
                "Global.World.Room.Park.Bank_2",
                "Global.World.Room.Park.Bank_4",
                "Global.World.Room.Park.Bank_5",
                "Global.World.Room.Park.Podium.Area_Planner",
                "Global.World.Room.Hotel.Bank_11.Seat_21",
                "Global.World.Room.Hotel.Bank_11.Seat_22",
                "Global.World.Room.Hotel.Bank_11.Seat_23",
                "Global.World.Room.Hotel.Bank_12.Seat_24",
                "Global.World.Room.Hotel.Bank_12.Seat_25",
                "Global.World.Room.Hotel.Bank_12.Seat_26",
                "Global.World.Room.Disco.Bar.Seat_27",
                "Global.World.Room.Disco.Bar.Seat_28",
                "Global.World.Room.Disco.Bar.Seat_29",
                "Global.World.Room.Disco.Bar.Seat_30",
                "Global.World.Room.Park.Bank_3.Seat_5",
                "Global.World.Room.Park.Bank_3.Seat_6",
                "Global.World.Room.Park.Bank_6.Seat_11",
                "Global.World.Room.Park.Bank_6.Seat_12",
                "Global.World.Room.Hotel.Bank_7.Seat_13",
                "Global.World.Room.Hotel.Bank_7.Seat_14",
                "Global.World.Room.Hotel.Bank_8.Seat_15",
                "Global.World.Room.Hotel.Bank_8.Seat_16",
                "Global.World.Room.Hotel.Bank_9.Seat_17",
                "Global.World.Room.Hotel.Bank_9.Seat_18",
                "Global.World.Room.Hotel.Bank_10.Seat_19",
                "Global.World.Room.Hotel.Bank_10.Seat_20",
                "Global.World.Room.Park.Bank_1.Seat_1",
                "Global.World.Room.Park.Bank_1.Seat_2",
                "Global.World.Room.Park.Bank_2.Seat_3",
                "Global.World.Room.Park.Bank_2.Seat_4",
                "Global.World.Room.Park.Bank_4.Seat_7",
                "Global.World.Room.Park.Bank_4.Seat_8",
                "Global.World.Room.Park.Bank_5.Seat_9",
                "Global.World.Room.Park.Bank_5.Seat_10");
        testChildContexts(Context.getGlobal(), contextIdSet);
    }

    private void testChildContexts(Context root, Set<String> contextIdSet){
        root.getChildren().forEach((childId, child) -> {
           Assert.assertTrue(contextIdSet.contains(child.getContextId().toString()));
           testChildContexts(child, contextIdSet);
        });
    }

    @Test
    public void getArea() {
        int posX = 500;
        int posY = 1500;
        Assert.assertEquals((room.getArea(posX, posY).getContextId()).toString(), "Global.World.Room.Disco");
    }

    @Test
    public void getExpanse_isIn() {
        try {
            Assert.assertTrue(((SpatialContext)Context.getGlobal().
                    getContext(new ContextID("Global.World.Room.Disco"))).getExpanse().isIn(500, 1500));
        } catch (ContextNotFoundException e) {
            e.printStackTrace();
        }
    }
}