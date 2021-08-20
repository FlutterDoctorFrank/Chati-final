package view2.component.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
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
import model.context.spatial.SpatialMap;
import model.user.IUserView;
import view2.Chati;
import view2.component.AbstractScreen;
import view2.component.hud.HeadUpDisplay;

import java.util.*;

public class WorldScreen extends AbstractScreen {

    public static final float PPM = 10; //pixels per meter
    public static final float DEFAULT_ZOOM = 0.6f;
    public static final float MIN_ZOOM = 0.4f;
    public static final float MAX_ZOOM = 0.8f;
    public static final float ZOOM_STEP = 0.01f;
    public static final short GROUND_BIT = 1;
    public static final short AVATAR_BIT = 2;
    public static final short OBJECT_BIT = 4;
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
        tiledMapRenderer = new OrthogonalTiledMapRenderer(null, 1 / PPM);
        world = new World(new Vector2(0, 0), true);
        world.setContactListener(new WorldContactListener());
        this.debugRenderer = new Box2DDebugRenderer();
        this.internUserAvatar = new InternUserAvatar(Chati.getInstance().getUserManager().getInternUserView(), world);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Wechsel den Raum, wenn die entsprechende Flag gesetzt ist.
        if (Chati.getInstance().isRoomChanged()) {
            setMap();
            createBorders();
            createInteractionObjects();

            int spawnPosX = (int) WorldScreen.getInstance().getTiledMap().getProperties().get("spawnPosX");
            int spawnPosY = (int) WorldScreen.getInstance().getTiledMap().getProperties().get("spawnPosY");
            internUserAvatar.body.setTransform(spawnPosX / WorldScreen.PPM, spawnPosY / WorldScreen.PPM, internUserAvatar.body.getAngle());
        }

        if (tiledMapRenderer.getMap() != null) {
            tiledMapRenderer.render();
            debugRenderer.render(world, camera.combined);

            updateInternUserAvatar();
            updateExternUserAvatars();
            updateCamera();
        }

        SPRITE_BATCH.setProjectionMatrix(camera.combined);
        SPRITE_BATCH.begin();
        internUserAvatar.draw(SPRITE_BATCH, delta);
        externUserAvatars.values().forEach(avatar -> avatar.draw(SPRITE_BATCH, delta));
        SPRITE_BATCH.end();

        world.step(1 / 30f, 6, 2); /** Was sind das für Zahlen? Kein hardcoden, irgendwo Konstanten setzen... Wo kommen die her?*/
        camera.update();
        tiledMapRenderer.setView(camera);
        SPRITE_BATCH.setProjectionMatrix(stage.getCamera().combined);
        super.render(delta);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void show() {
        this.internUserAvatar = new InternUserAvatar(Chati.getInstance().getUserManager().getInternUserView(), world);
    }

    @Override
    public void hide() {
        externUserAvatars.clear();
        tiledMapRenderer.setMap(null);
    }

    @Override
    public InputProcessor getInputProcessor() {
        return new InputMultiplexer(stage, worldInputProcessor);
    }

    public TiledMap getTiledMap() {
        return tiledMapRenderer.getMap();
    }

    public static WorldScreen getInstance() {
        if (worldScreen == null) {
            worldScreen = new WorldScreen();
            worldScreen.getStage().addActor(HeadUpDisplay.getInstance());
        }
        return worldScreen;
    }

    private void setMap() {
        SpatialMap spatialMap = Chati.getInstance().getUserManager().getInternUserView().getCurrentRoom().getMap();
        TiledMap tiledMap = new TmxMapLoader().load(spatialMap.getPath());
        tiledMapRenderer.setMap(tiledMap);
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

    private void updateInternUserAvatar() {
        if (Chati.getInstance().isInternUserPositionChanged()) {
            internUserAvatar.teleport();
        }
        internUserAvatar.move();
    }

    private void updateExternUserAvatars() {
        if (Chati.getInstance().isUserPositionChanged()) {
            externUserAvatars.values().removeIf(userAvatar -> !Chati.getInstance().getUserManager().getUsersInRoom()
                    .containsValue(userAvatar.getUser()));
            Chati.getInstance().getUserManager().getActiveUsers().values().forEach(externUser -> {
                if (!externUserAvatars.containsKey(externUser)) {
                    UserAvatar newUserAvatar = new UserAvatar(externUser, world);
                    externUserAvatars.put(externUser, newUserAvatar);
                    newUserAvatar.teleport();
                }
            });
        }
        externUserAvatars.values().forEach(externUserAvatar -> externUserAvatar.move(false));
    }

    private void updateCamera() {
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

        Vector2 bodyPosition = internUserAvatar.getPosition();
        if (bodyPosition.x >= cameraLeftBoundary && bodyPosition.x <= cameraRightBoundary) {
            camera.position.x = bodyPosition.x;
        } else if (bodyPosition.x < cameraLeftBoundary) {
            camera.position.x = cameraLeftBoundary;
        } else if (bodyPosition.x > cameraRightBoundary) {
            camera.position.x = cameraRightBoundary;
        }

        if (bodyPosition.y >= cameraBottomBoundary && bodyPosition.y <= cameraTopBoundary) {
            camera.position.y = bodyPosition.y;
        } else if (bodyPosition.y > cameraTopBoundary) {
            camera.position.y = cameraTopBoundary;
        } else if (bodyPosition.y < cameraBottomBoundary) {
            camera.position.y = cameraBottomBoundary;
        }
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
}
