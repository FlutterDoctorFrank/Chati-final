package model.context.spatial;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import model.context.Context;
import model.context.ContextID;
import model.exception.ContextNotFoundException;
import model.user.UserManager;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import view2.IModelObserver;

import java.util.Set;

public class SpatialContextTest {

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
        world = null;
        room = null;
        map = null;
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
                "Global.World.Room.Park.SpawnArea",
                "Global.World.Room.Hotel.Rezeption",
                "Global.World.Room.Disco.Bar",
                "Global.World.Room.Disco.gameBoard_2",
                "Global.World.Room.Disco.gameBoard_1",
                "Global.World.Room.Hotel.gameBoard_3",
                "Global.World.Room.Disco.Juckebox",
                "Global.World.Room.Hotel.BankArea_8",
                "Global.World.Room.Hotel.BankArea_11",
                "Global.World.Room.Hotel.BankArea_12",
                "Global.World.Room.Hotel.BankArea_10",
                "Global.World.Room.Hotel.BankArea_9",
                "Global.World.Room.Hotel.BankArea_7",
                "Global.World.Room.Park.BankArea_4",
                "Global.World.Room.Park.BankArea_5",
                "Global.World.Room.Park.BankArea_6",
                "Global.World.Room.Park.BankArea_3",
                "Global.World.Room.Disco.Bar.Chair_4",
                "Global.World.Room.Disco.Bar.Chair_3",
                "Global.World.Room.Disco.Bar.Chair_2",
                "Global.World.Room.Disco.Bar.Chair_1",
                "Global.World.Room.Park.BankArea_1",
                "Global.World.Room.Park.BankArea_6.Bank_9",
                "Global.World.Room.Park.BankArea_2",
                "Global.World.Room.Park.BankArea_6.Bank_10",
                "Global.World.Room.Park.BankArea_3.Bank_24",
                "Global.World.Room.Park.BankArea_3.Bank_11",
                "Global.World.Room.Park.BankArea_4.Bank_6",
                "Global.World.Room.Park.BankArea_1.Bank_4",
                "Global.World.Room.Hotel.BankArea_11.Bank_25",
                "Global.World.Room.Hotel.BankArea_12.Bank_23",
                "Global.World.Room.Hotel.BankArea_11.Bank_20",
                "Global.World.Room.Hotel.BankArea_11.Bank_19",
                "Global.World.Room.Hotel.BankArea_8.Bank_17",
                "Global.World.Room.Hotel.BankArea_9.Bank_16",
                "Global.World.Room.Hotel.BankArea_8.Bank_13",
                "Global.World.Room.Hotel.BankArea_8.Bank_12",
                "Global.World.Room.Park.BankArea_2.Bank_2",
                "Global.World.Room.Hotel.BankArea_12.Bank_26",
                "Global.World.Room.Hotel.BankArea_10.Bank_22",
                "Global.World.Room.Hotel.BankArea_10.Bank_21",
                "Global.World.Room.Hotel.BankArea_9.Bank_18",
                "Global.World.Room.Hotel.BankArea_7.Bank_15",
                "Global.World.Room.Hotel.BankArea_7.Bank_14",
                "Global.World.Room.Park.BankArea_5.Bank_8",
                "Global.World.Room.Park.BankArea_4.Bank_5",
                "Global.World.Room.Park.BankArea_1.Bank_3",
                "Global.World.Room.Park.BankArea_2.Bank_1",
                "Global.World.Room.Park.BankArea_5.Bank_7");
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
        Assert.assertEquals((world.getArea(posX, posY).getContextId()).toString(), "Global.world.Disco");
    }

    @Test
    public void getExpanse_isIn() {
        try {
            Assert.assertTrue(((SpatialContext)Context.getGlobal().
                    getContext(new ContextID("Global.world.Disco"))).getExpanse().isIn(500, 1500));
        } catch (ContextNotFoundException e) {
            e.printStackTrace();
        }
    }
}