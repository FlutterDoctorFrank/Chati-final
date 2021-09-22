package model.context;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import controller.network.protocol.PacketAvatarMove;
import model.MockGL20;
import model.context.spatial.SpatialContext;
import model.context.spatial.ContextMap;
import model.exception.ContextNotFoundException;
import model.user.UserManager;
import org.junit.*;
import view2.IModelObserver;

public class ContextTest {

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
    public void addRemoveChildGetParent() {
        SpatialContext spatialContext = new SpatialContext("TestWorld", Context.getGlobal());
        Assert.assertEquals(spatialContext.getParent(), Context.getGlobal());
        Context.getGlobal().addChild(spatialContext);
        Assert.assertTrue(Context.getGlobal().children.containsValue(spatialContext));
        Context.getGlobal().removeChild(spatialContext);
        Assert.assertFalse(Context.getGlobal().children.containsValue(spatialContext));
    }

    @Test
    public void getContext() {
        ContextID contextId = new ContextID("Global.World.Room.Hotel.Bank_12.Seat_24");
        try {
            Assert.assertEquals(Context.getGlobal().
                    getContext(contextId).contextId, contextId);
        } catch (ContextNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Test(expected = ContextNotFoundException.class)
    public void  getContextException() throws ContextNotFoundException {
        ContextID contextId = new ContextID("keinContext");
        Context.getGlobal().getContext(contextId);
    }

    @Test
    public void getGlobal() {
        Assert.assertEquals(Context.getGlobal().contextId.toString(), "Global");
    }
}