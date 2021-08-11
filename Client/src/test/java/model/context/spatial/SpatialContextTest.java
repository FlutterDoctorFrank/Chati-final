package model.context.spatial;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import model.context.Context;
import model.context.ContextID;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import model.context.spatial.SpatialMap;

import java.util.Set;

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

        Set<String> contextIdSet = Set.of("global.world.Park", "global.world.Disco", "global.world.Hotel",
                "global.world.Disco.SpawnZone", "global.world.Hotel.Rezeption", "global.world.Disco.Bar",
                "global.world.Disco.gameboard_2", "global.world.Disco.gameboard_1", "global.world.Hotel.gameboard_3",
                "global.world.Disco.Juckebox", "global.world.Hotel.BankArea_8", "global.world.Hotel.BankArea_11",
                "global.world.Hotel.BankArea_12", "global.world.Hotel.BankArea_10", "global.world.Hotel.BankArea_9",
                "global.world.Hotel.BankArea_7", "global.world.Park.BankArea_4", "global.world.Park.BankArea_5",
                "global.world.Park.BankArea_6", "global.world.Park.BankArea_3", "global.world.Disco.Bar.Chair_4",
                "global.world.Disco.Bar.Chair_3", "global.world.Disco.Bar.Chair_2", "global.world.Disco.Bar.Chair_1",
                "global.world.Park.BankArea_1", "global.world.Park.BankArea_2", "global.world.Hotel.BankArea_11.Bank_25",
                "global.world.Hotel.BankArea_12.Bank_23", "global.world.Hotel.BankArea_11.Bank_20",
                "global.world.Hotel.BankArea_11.Bank_19", "global.world.Hotel.BankArea_8.Bank_17",
                "global.world.Hotel.BankArea_9.Bank_16", "global.world.Hotel.BankArea_8.Bank_13",
                "global.world.Hotel.BankArea_8.Bank_12", "global.world.Hotel.BankArea_12.Bank_26",
                "global.world.Hotel.BankArea_10.Bank_22", "global.world.Hotel.BankArea_10.Bank_21",
                "global.world.Hotel.BankArea_9.Bank_18", "global.world.Hotel.BankArea_7.Bank_15",
                "global.world.Hotel.BankArea_7.Bank_14", "global.world.Park.BankArea_3.Bank_24",
                "global.world.Park.BankArea_6.Bank_10", "global.world.Park.BankArea_5.Bank_8",
                "global.world.Park.BankArea_4.Bank_6", "global.world.Park.BankArea_1.Bank_4",
                "global.world.Park.BankArea_2.Bank_2", "global.world.Park.BankArea_3.Bank_11",
                "global.world.Park.BankArea_6.Bank_9", "global.world.Park.BankArea_5.Bank_7",
                "global.world.Park.BankArea_4.Bank_5", "global.world.Park.BankArea_1.Bank_3",
                "global.world.Park.BankArea_2.Bank_1");
        testChildContexts(Context.getGlobal(), contextIdSet);


    }

    private void testChildContexts(Context root, Set<String> contextIdSet){
        root.getChildren().forEach((childId, child) -> {
            Assert.assertTrue(contextIdSet.contains(child.getContextId()));
            testChildContexts(child, contextIdSet);
        });
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