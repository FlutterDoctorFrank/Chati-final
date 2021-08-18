package view2.component.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import model.context.spatial.SpatialMap;
import view2.Chati;
import view2.component.AbstractScreen;
import view2.component.hud.HeadUpDisplay;

import java.util.*;
import java.util.stream.Collectors;

public class WorldScreen extends AbstractScreen {

    public static final float PPM = 10; //pixels per meter
    public static final short GROUND_BIT = 1;
    public static final short AVATAR_BIT = 2;
    public static final short OBJECT_BIT = 4;
    private static final SpriteBatch SPRITE_BATCH = new SpriteBatch();

    private static WorldScreen worldScreen;

    private final WorldInputProcessor worldInputProcessor;
    private final FitViewport gameViewport;
    private final Box2DDebugRenderer debugRenderer;
    private OrthogonalTiledMapRenderer tiledMapRenderer;
    private TiledMap tiledMap;
    private World world;

    private final Set<UserAvatar> externUserAvatars;
    private InternUserAvatar internUserAvatar;

    private WorldScreen() {
        this.worldInputProcessor = new WorldInputProcessor();
        this.externUserAvatars = new HashSet<>();
        this.gameViewport = new FitViewport(Gdx.graphics.getWidth() / PPM, Gdx.graphics.getHeight() / PPM, new OrthographicCamera());
        this.debugRenderer = new Box2DDebugRenderer();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Wechsel den Raum, wenn die entsprechende Flag gesetzt ist.
        if (Chati.getInstance().isRoomChanged()) {
            createMap();
            addBorders();
            addInteractiveObjects();
        }

        if (Chati.getInstance().isInternUserPositionChanged()) {
            if (internUserAvatar == null) {
                internUserAvatar = new InternUserAvatar(Chati.getInstance().getUserManager().getInternUserView());
            }
            internUserAvatar.updatePosition(true);
        }

        if (Chati.getInstance().isUserPositionChanged()) {
            updateExternUserAvatars();
        }

        if (tiledMap != null && internUserAvatar != null) {
            tiledMapRenderer.render();
            debugRenderer.render(world, gameViewport.getCamera().combined);

            SPRITE_BATCH.setProjectionMatrix(gameViewport.getCamera().combined);
            SPRITE_BATCH.begin();
            internUserAvatar.move();
            internUserAvatar.draw(SPRITE_BATCH, delta);
            externUserAvatars.forEach(avatar -> avatar.draw(SPRITE_BATCH, delta));
            SPRITE_BATCH.end();

            world.step(1 / 30f, 6, 2); /** Was sind das für Zahlen? Kein hardcoden, irgendwo Konstanten setzen... Wo kommen die her?*/
            gameViewport.getCamera().update();
            tiledMapRenderer.setView((OrthographicCamera) gameViewport.getCamera());
        }

        SPRITE_BATCH.setProjectionMatrix(stage.getCamera().combined);
        super.render(delta);
    }

    @Override
    public InputProcessor getInputProcessor() {
        return new InputMultiplexer(stage, worldInputProcessor);
    }

    @Override
    public void resize(int width, int height) {
        gameViewport.update(width, height);
    }

    public TiledMap getTiledMap() {
        return tiledMap;
    }

    public Viewport getGameViewport() {
        return gameViewport;
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
        tiledMap = new TmxMapLoader().load(spatialMap.getPath());
        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap, 1 / PPM);
        world = new World(new Vector2(0, 0), true);
        world.setContactListener(new WorldContactListener());
    }

    private void addBorders() {
        tiledMap.getLayers().get("Borders").getObjects().getByType(RectangleMapObject.class).forEach(border -> {
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

    private void addInteractiveObjects() {
        tiledMap.getLayers().get("InteractiveObject").getObjects().getByType(RectangleMapObject.class)
                .forEach(interactiveObject -> {
            Rectangle rectangle = interactiveObject.getRectangle();
            new InteractionObject(rectangle);
        });
    }

    private void updateExternUserAvatars() {
        externUserAvatars.forEach(externUserAvatar -> {
            if (!Chati.getInstance().getUserManager().getUsersInRoom().containsValue(externUserAvatar.getUser())) {
                externUserAvatars.remove(externUserAvatar);
            }
        });
        Chati.getInstance().getUserManager().getUsersInRoom().values().forEach(externUser -> {
            if (!externUserAvatars.stream().map(UserAvatar::getUser).collect(Collectors.toSet()).contains(externUser)) {
                externUserAvatars.add(new UserAvatar(externUser));
            }
        });
        externUserAvatars.forEach(externUserAvatar -> externUserAvatar.updatePosition(false));
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

    public void clear() {
        internUserAvatar = null;
        externUserAvatars.clear();
    }
}
