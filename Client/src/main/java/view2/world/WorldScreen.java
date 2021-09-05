package view2.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import model.context.ContextID;
import model.context.spatial.ContextMenu;
import model.user.IInternUserView;
import model.user.IUserView;
import view2.Chati;
import view2.ChatiScreen;
import view2.world.component.CollisionArea;
import view2.world.component.InteractionObject;
import view2.world.component.InternUserAvatar;
import view2.world.component.UserAvatar;
import view2.userInterface.interactableMenu.*;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class WorldScreen extends ChatiScreen {

    public static final short COLLISION_AREA_BIT = 1;
    public static final short USER_BIT = 2;
    public static final short INTERN_USER_BIT = 4;

    public static final float WORLD_STEP = 30;
    private static final int VELOCITY_ITERATIONS = 6;
    private static final int POSITION_ITERATIONS = 2;
    private static final int[] LAYERS_RENDER_BEFORE_AVATAR = new int[]{0, 1, 2};
    private static final int[] LAYERS_RENDER_AFTER_AVATAR = new int[]{3};

    private final WorldInputProcessor worldInputProcessor;
    private final WorldCamera camera;
    private final FitViewport viewport;
    private final OrthogonalTiledMapRenderer tiledMapRenderer;
    // private final Box2DDebugRenderer debugRenderer;
    private final World world;

    private final Map<IUserView, UserAvatar> externUserAvatars;
    private InternUserAvatar internUserAvatar;
    private InteractableWindow currentInteractableWindow;

    public WorldScreen() {
        this.worldInputProcessor = new WorldInputProcessor();
        this.camera = new WorldCamera();
        this.viewport = new FitViewport(Gdx.graphics.getWidth() / WorldCamera.PPM, Gdx.graphics.getHeight() / WorldCamera.PPM, camera);
        this.tiledMapRenderer = new OrthogonalTiledMapRenderer(null, 1 / WorldCamera.PPM);
        // this.debugRenderer = new Box2DDebugRenderer();
        this.world = new World(new Vector2(0, 0), true);
        this.world.setContactListener(new WorldContactListener());
        this.externUserAvatars = new HashMap<>();
        this.internUserAvatar = null;
        this.currentInteractableWindow = null;
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (Chati.CHATI.isRoomChanged()) {
            destroy();
            createMap();
            initialize();
        }
        if (tiledMapRenderer.getMap() != null) {
            if (Chati.CHATI.isUserInfoChanged()) {
                loadExternUserAvatars();
            }
            externUserAvatars.values().forEach(UserAvatar::update);
            internUserAvatar.update();

            tiledMapRenderer.render(LAYERS_RENDER_BEFORE_AVATAR);
            Chati.CHATI.getSpriteBatch().setProjectionMatrix(camera.combined);
            Chati.CHATI.getSpriteBatch().begin();
            externUserAvatars.values().forEach(avatar -> avatar.draw(Chati.CHATI.getSpriteBatch(), delta));
            internUserAvatar.draw(Chati.CHATI.getSpriteBatch(), delta);
            externUserAvatars.values().forEach(avatar -> avatar.drawHead(Chati.CHATI.getSpriteBatch(), delta));
            internUserAvatar.drawHead(Chati.CHATI.getSpriteBatch(), delta);
            Chati.CHATI.getSpriteBatch().end();
            tiledMapRenderer.render(LAYERS_RENDER_AFTER_AVATAR);
            // debugRenderer.render(world, camera.combined);

            world.step(1 / WORLD_STEP, VELOCITY_ITERATIONS, POSITION_ITERATIONS);

            internUserAvatar.sendPosition();
            camera.update();
            tiledMapRenderer.setView(camera);
        }
        super.render(delta);
    }

    @Override
    public void show() {
        super.show();
        initialize();
    }

    @Override
    public void hide() {
        destroy();
        super.hide();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public InputProcessor getInputProcessor() {
        return new InputMultiplexer(stage, worldInputProcessor);
    }

    public WorldInputProcessor getWorldInputProcessor() {
        return worldInputProcessor;
    }

    public TiledMap getTiledMap() {
        return tiledMapRenderer.getMap();
    }

    public World getWorld() {
        return world;
    }

    public WorldCamera getCamera() {
        return camera;
    }

    public InternUserAvatar getInternUserAvatar() {
        return internUserAvatar;
    }

    public void openMenu(ContextID contextId, ContextMenu contextMenu) {
        switch (contextMenu) {
            case ROOM_RECEPTION_MENU:
                currentInteractableWindow = new RoomReceptionWindow(contextId);
                break;
            case MUSIC_STREAMER_MENU:
                currentInteractableWindow = new MusicStreamerWindow(contextId);
                break;
            case AREA_PLANNER_MENU:
                currentInteractableWindow = new AreaPlannerWindow(contextId);
                break;
            case GAME_BOARD_MENU:
                currentInteractableWindow = new GameBoardWindow(contextId);
                break;
            case SEAT_MENU:
                currentInteractableWindow = new SeatWindow(contextId);
                break;
            case PORTAL_MENU:
                currentInteractableWindow = new PortalWindow(contextId);
                break;
            default:
                throw new IllegalArgumentException("Tried to open an unsupported menu.");
        }
        currentInteractableWindow.open();
    }

    public void closeMenu(ContextID contextId, ContextMenu contextMenu) {
        if (currentInteractableWindow != null && (!currentInteractableWindow.getInteractableId().equals(contextId)
            || currentInteractableWindow.getInteractableMenu() != contextMenu)) {
            throw new IllegalArgumentException("Tried to close a menu that is not open.");
        }
        stage.closeWindow(currentInteractableWindow);
        currentInteractableWindow = null;
    }

    public void menuActionResponse(boolean success, String messageKey) {
        if (currentInteractableWindow == null) {
            return;
        }
        currentInteractableWindow.receiveResponse(success, messageKey);
    }

    private void initialize() {
        this.internUserAvatar = new InternUserAvatar();
        loadExternUserAvatars();
    }

    private void destroy() {
        Array<Body> bodies = new Array<>();
        world.getBodies(bodies);
        bodies.forEach(world::destroyBody);
        internUserAvatar = null;
        externUserAvatars.clear();
        tiledMapRenderer.setMap(null);
    }

    private void createMap() {
        IInternUserView internUser = Chati.CHATI.getUserManager().getInternUserView();
        if (internUser == null || internUser.getCurrentRoom() == null || internUser.getCurrentRoom().getMap() == null) {
            return;
        }
        TiledMap tiledMap = new TmxMapLoader().load(internUser.getCurrentRoom().getMap().getPath());
        tiledMapRenderer.setMap(tiledMap);
        tiledMapRenderer.getMap().getLayers().get("Collisions").getObjects().getByType(RectangleMapObject.class)
                .forEach(border -> new CollisionArea(border.getRectangle()));
        tiledMapRenderer.getMap().getLayers().get("Interactions").getObjects().getByType(RectangleMapObject.class)
                .forEach(interactiveObject -> new InteractionObject(interactiveObject.getRectangle()));
    }

    private void loadExternUserAvatars() {
        Chati.CHATI.getUserManager().getUsersInRoom().values().forEach(externUser -> {
            if (!externUserAvatars.containsKey(externUser)) {
                UserAvatar newUserAvatar = new UserAvatar(externUser);
                externUserAvatars.put(externUser, newUserAvatar);
                newUserAvatar.teleport();
            }
        });
        Iterator<Map.Entry<IUserView, UserAvatar>> iterator = externUserAvatars.entrySet().iterator();
        while (iterator.hasNext()) {
            UserAvatar userAvatar = iterator.next().getValue();
            if (!Chati.CHATI.getUserManager().getUsersInRoom().containsValue(userAvatar.getUser())) {
                world.destroyBody(userAvatar.getBody());
                iterator.remove();
            }
        }
    }
}
