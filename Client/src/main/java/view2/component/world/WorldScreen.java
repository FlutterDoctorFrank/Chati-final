package view2.component.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import model.context.spatial.SpatialMap;
import model.user.IUserView;
import view2.Chati;
import view2.component.AbstractScreen;
import view2.component.world.body.Border;
import view2.component.world.body.InteractionObject;
import view2.component.world.body.InternUserAvatar;
import view2.component.world.body.UserAvatar;

import java.util.*;

public class WorldScreen extends AbstractScreen {

    public static final short BORDER_BIT = 1;
    public static final short USER_BIT = 2;
    public static final short INTERN_USER_BIT = 4;

    public static final float WORLD_STEP = 30;
    private static final int VELOCITY_ITERATIONS = 6;
    private static final int POSITION_ITERATIONS = 2;

    private final WorldInputProcessor worldInputProcessor;
    private final FitViewport viewport;
    private final WorldCamera camera;
    private final Box2DDebugRenderer debugRenderer;
    private final OrthogonalTiledMapRenderer tiledMapRenderer;
    private final World world;

    private final Map<IUserView, UserAvatar> externUserAvatars;
    private InternUserAvatar internUserAvatar;

    public WorldScreen() {
        this.worldInputProcessor = new WorldInputProcessor();
        this.externUserAvatars = new HashMap<>();
        this.camera = new WorldCamera();
        this.viewport = new FitViewport(Gdx.graphics.getWidth() / WorldCamera.PPM, Gdx.graphics.getHeight() / WorldCamera.PPM, camera);
        this.tiledMapRenderer = new OrthogonalTiledMapRenderer(null, 1 / WorldCamera.PPM);
        this.world = new World(new Vector2(0, 0), true);
        this.world.setContactListener(new WorldContactListener());
        this.debugRenderer = new Box2DDebugRenderer();
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
            tiledMapRenderer.render();
            debugRenderer.render(world, camera.combined);

            if (Chati.CHATI.isUserInfoChanged()) {
                loadExternUserAvatars();
            }
            externUserAvatars.values().forEach(UserAvatar::update);
            internUserAvatar.update();

            Chati.SPRITE_BATCH.setProjectionMatrix(camera.combined);
            Chati.SPRITE_BATCH.begin();
            internUserAvatar.draw(Chati.SPRITE_BATCH, delta);
            externUserAvatars.values().forEach(avatar -> avatar.draw(Chati.SPRITE_BATCH, delta));
            Chati.SPRITE_BATCH.end();

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

    public TiledMap getTiledMap() {
        return tiledMapRenderer.getMap();
    }

    public World getWorld() {
        return world;
    }

    public WorldCamera getCamera() {
        return camera;
    }

    public WorldInputProcessor getWorldInputProcessor() {
        return worldInputProcessor;
    }

    public InternUserAvatar getInternUserAvatar() {
        return internUserAvatar;
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
        SpatialMap spatialMap = Chati.CHATI.getUserManager().getInternUserView().getCurrentRoom().getMap();
        TiledMap tiledMap = new TmxMapLoader().load(spatialMap.getPath());
        tiledMapRenderer.setMap(tiledMap);
        tiledMapRenderer.getMap().getLayers().get("Borders").getObjects().getByType(RectangleMapObject.class)
                .forEach(border -> new Border(border.getRectangle()));
        tiledMapRenderer.getMap().getLayers().get("InteractiveObject").getObjects().getByType(RectangleMapObject.class)
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
