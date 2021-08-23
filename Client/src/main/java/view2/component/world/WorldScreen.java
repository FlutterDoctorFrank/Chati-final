package view2.component.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.viewport.FitViewport;
import controller.network.ServerSender;
import model.context.spatial.SpatialMap;
import model.user.IUserView;
import view2.Assets;
import view2.Chati;
import view2.component.AbstractScreen;
import view2.component.hud.HeadUpDisplay;
import view2.component.world.body.InteractionObject;
import view2.component.world.body.InternUserAvatar;
import view2.component.world.body.UserAvatar;

import java.util.*;

public class WorldScreen extends AbstractScreen {

    public static final float PPM = 10;
    public static final short BORDER_BIT = 1;
    public static final short USER_BIT = 2;
    public static final short INTERN_USER_BIT = 4;
    public static final short OBJECT_BIT = 8;
    public static final float WORLD_STEP = 30;
    private static final int VELOCITY_ITERATIONS = 6;
    private static final int POSITION_ITERATIONS = 2;
    private static final float DEFAULT_ZOOM = 0.6f;
    private static final float MIN_ZOOM = 0.4f;
    private static final float MAX_ZOOM = 0.8f;
    private static final float ZOOM_STEP = 0.01f;
    private static final SpriteBatch SPRITE_BATCH = new SpriteBatch();

    private static WorldScreen worldScreen;

    private final WorldInputProcessor worldInputProcessor;
    private final FitViewport viewport;
    private final OrthographicCamera camera;
    private final Box2DDebugRenderer debugRenderer;
    private final OrthogonalTiledMapRenderer tiledMapRenderer;
    private final World world;

    private final Map<IUserView, UserAvatar> externUserAvatars;
    private InternUserAvatar internUserAvatar;

    private WorldScreen() {
        this.worldInputProcessor = new WorldInputProcessor();
        this.externUserAvatars = new HashMap<>();
        this.camera = new OrthographicCamera();
        this.camera.zoom = DEFAULT_ZOOM;
        this.viewport = new FitViewport(Gdx.graphics.getWidth() / PPM, Gdx.graphics.getHeight() / PPM, camera);
        this.tiledMapRenderer = new OrthogonalTiledMapRenderer(null, 1 / PPM);
        this.world = new World(new Vector2(0, 0), true);
        this.world.setContactListener(new WorldContactListener());
        this.debugRenderer = new Box2DDebugRenderer();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (Chati.getInstance().isRoomChanged()) {
            createMap();
            loadExternUserAvatars();
        }

        if (tiledMapRenderer.getMap() != null) {
            tiledMapRenderer.render();
            debugRenderer.render(world, camera.combined);

            if (Chati.getInstance().isUserInfoChanged()) {
                loadExternUserAvatars();
            }
            externUserAvatars.values().forEach(UserAvatar::update);
            internUserAvatar.update();

            SPRITE_BATCH.setProjectionMatrix(camera.combined);
            SPRITE_BATCH.begin();
            internUserAvatar.draw(SPRITE_BATCH, delta);
            externUserAvatars.values().forEach(avatar -> avatar.draw(SPRITE_BATCH, delta));
            SPRITE_BATCH.end();

            world.step(1 / WORLD_STEP, VELOCITY_ITERATIONS, POSITION_ITERATIONS);
            updateCameraPosition();
            sendPosition();
            tiledMapRenderer.setView(camera);
        }

        SPRITE_BATCH.setProjectionMatrix(stage.getCamera().combined);
        super.render(delta);
    }

    @Override
    public void show() {
        this.internUserAvatar = new InternUserAvatar(Chati.getInstance().getUserManager().getInternUserView(), world);
        loadExternUserAvatars();
    }

    @Override
    public void hide() {
        if (internUserAvatar != null) {
            world.destroyBody(internUserAvatar.getBody());
            internUserAvatar = null;
        }
        externUserAvatars.values().forEach(userAvatar -> world.destroyBody(userAvatar.getBody()));
        externUserAvatars.clear();
        tiledMapRenderer.setMap(null);
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

    public WorldInputProcessor getWorldInputProcessor() {
        return worldInputProcessor;
    }

    public InternUserAvatar getInternUserAvatar() {
        return internUserAvatar;
    }

    public void zoom(boolean in) {
        if (in && camera.zoom <= MAX_ZOOM) {
            camera.zoom += ZOOM_STEP;
        } else if (!in && camera.zoom >= WorldScreen.MIN_ZOOM) {
            camera.zoom -= ZOOM_STEP;
        }
    }

    public static WorldScreen getInstance() {
        if (worldScreen == null) {
            worldScreen = new WorldScreen();
            worldScreen.getStage().addActor(HeadUpDisplay.getInstance());
        }
        return worldScreen;
    }

    private void createMap() {
        SpatialMap spatialMap = Chati.getInstance().getUserManager().getInternUserView().getCurrentRoom().getMap();
        TiledMap tiledMap = new TmxMapLoader().load(spatialMap.getPath());
        tiledMapRenderer.setMap(tiledMap);
        createBorders();
        createInteractionObjects();
    }

    private void createBorders() {
        tiledMapRenderer.getMap().getLayers().get("Borders").getObjects().getByType(RectangleMapObject.class).forEach(border -> {
            Rectangle bounds = border.getRectangle();

            BodyDef bodyDef = new BodyDef();
            bodyDef.type = BodyDef.BodyType.StaticBody;
            bodyDef.position.set((bounds.getX() + bounds.getWidth() / 2) / PPM, (bounds.getY() + bounds.getHeight() / 2) / PPM);
            Body body = world.createBody(bodyDef);

            FixtureDef fixtureDef = new FixtureDef();
            PolygonShape shape = new PolygonShape();
            shape.setAsBox((bounds.getWidth() / 2) / PPM, (bounds.getHeight() / 2) / PPM);
            fixtureDef.shape = shape;
            body.createFixture(fixtureDef);
        });
    }

    private void createInteractionObjects() {
        tiledMapRenderer.getMap().getLayers().get("InteractiveObject").getObjects().getByType(RectangleMapObject.class)
                .forEach(interactiveObject -> {
            Rectangle rectangle = interactiveObject.getRectangle();
            new InteractionObject(rectangle);
        });
    }

    private void loadExternUserAvatars() {
        Chati.getInstance().getUserManager().getUsersInRoom().values().forEach(externUser -> {
            if (!externUserAvatars.containsKey(externUser)) {
                UserAvatar newUserAvatar = new UserAvatar(externUser, world);
                externUserAvatars.put(externUser, newUserAvatar);
                newUserAvatar.teleport();
            }
        });
        Iterator<Map.Entry<IUserView, UserAvatar>> iterator = externUserAvatars.entrySet().iterator();
        while (iterator.hasNext()) {
            UserAvatar userAvatar = iterator.next().getValue();
            if (!Chati.getInstance().getUserManager().getUsersInRoom().containsValue(userAvatar.getUser())) {
                world.destroyBody(userAvatar.getBody());
                iterator.remove();
            }
        }
    }

    private void updateCameraPosition() {
        TiledMapTileLayer layer = (TiledMapTileLayer) tiledMapRenderer.getMap().getLayers().get(0);
        float mapWidth = layer.getWidth() * layer.getTileWidth();
        float mapHeight = layer.getHeight() * layer.getTileHeight();

        float cameraTopBoundary =
                (mapHeight - Gdx.graphics.getHeight() * camera.zoom / 2f) / PPM;
        float cameraLeftBoundary =
                (Gdx.graphics.getWidth() / 2f * camera.zoom) / PPM;
        float cameraBottomBoundary =
                (Gdx.graphics.getHeight() * camera.zoom / 2f) / PPM;
        float cameraRightBoundary =
                (mapWidth - Gdx.graphics.getWidth() * camera.zoom / 2f) / PPM;

        Vector2 internUserPosition = internUserAvatar.getPosition();
        if (internUserPosition.x >= cameraLeftBoundary && internUserPosition.x <= cameraRightBoundary) {
            camera.position.x = internUserPosition.x;
        } else if (internUserPosition.x < cameraLeftBoundary) {
            camera.position.x = cameraLeftBoundary;
        } else if (internUserPosition.x > cameraRightBoundary) {
            camera.position.x = cameraRightBoundary;
        }

        if (internUserPosition.y >= cameraBottomBoundary && internUserPosition.y <= cameraTopBoundary) {
            camera.position.y = internUserPosition.y;
        } else if (internUserPosition.y > cameraTopBoundary) {
            camera.position.y = cameraTopBoundary;
        } else if (internUserPosition.y < cameraBottomBoundary) {
            camera.position.y = cameraBottomBoundary;
        }

        camera.update();
    }

    private void sendPosition() {
        Vector2 oldPosition = internUserAvatar.getOldPosition();
        Vector2 newPosition = internUserAvatar.getPosition();
        if (!oldPosition.epsilonEquals(newPosition)) {
            Chati.getInstance().getServerSender().send(ServerSender.SendAction.AVATAR_MOVE,
                    newPosition.x * WorldScreen.PPM, newPosition.y * WorldScreen.PPM);
        }
    }
}
