package view2.component.world;

import com.badlogic.gdx.Gdx;
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
import view.Screens.WorldContactListener;
import view.Sprites.InteractiveTileObject;
import view2.Chati;
import view2.component.AbstractScreen;
import view2.component.hud.HeadUpDisplay;

import java.util.*;

public class WorldScreen extends AbstractScreen {

    public static final float PPM = 10; //pixels per meter
    private static final SpriteBatch SPRITE_BATCH = new SpriteBatch();

    private static WorldScreen worldScreen;

    private final Viewport gameViewport;
    private final Box2DDebugRenderer debugRenderer;
    private OrthogonalTiledMapRenderer tiledMapRenderer;
    private TiledMap tiledMap;
    private World world;

    private final List<UserAvatar> externUserAvatars;
    private InternUserAvatar internUserAvatar;

    private WorldScreen() {

        this.externUserAvatars = new ArrayList<>();
        this.gameViewport = new FitViewport(Gdx.graphics.getWidth() / PPM, Gdx.graphics.getHeight() / PPM, new OrthographicCamera());
        this.debugRenderer = new Box2DDebugRenderer();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (Chati.getInstance().isRoomChanged()) {
            createMap();
            addBorders();
            addInteractiveObjects();
            addAvatars();
        }

        if (Chati.getInstance().isUserInfoChanged()) {
            externUserAvatars.forEach(externUserAvatar -> externUserAvatar.updatePosition(false));
        }

        if (tiledMap != null) {
            tiledMapRenderer.render();
            debugRenderer.render(world, gameViewport.getCamera().combined);

            internUserAvatar.handleInput();

            SPRITE_BATCH.setProjectionMatrix(gameViewport.getCamera().combined);
            SPRITE_BATCH.begin();
            internUserAvatar.draw(SPRITE_BATCH);
            externUserAvatars.forEach(avatar -> avatar.draw(SPRITE_BATCH));
            SPRITE_BATCH.end();

            world.step(1 / 30f, 6, 2); /** Was sind das für Zahlen? Kein hardcoden, irgendwo Konstanten setzen... Wo kommen die her?*/
            gameViewport.getCamera().update();
            tiledMapRenderer.setView((OrthographicCamera) gameViewport.getCamera());
        }

        SPRITE_BATCH.setProjectionMatrix(stage.getCamera().combined);
        stage.act(delta);
        stage.draw();
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
            bodyDef.position.set((bounds.getX() + bounds.getWidth() / 2) / view.Chati.PPM, (bounds.getY() + bounds.getHeight() / 2) / view.Chati.PPM);
            Body body = world.createBody(bodyDef);

            FixtureDef fixtureDef = new FixtureDef();
            PolygonShape shape = new PolygonShape();
            shape.setAsBox((bounds.getWidth() / 2) / view.Chati.PPM, (bounds.getHeight() / 2) / view.Chati.PPM);
            fixtureDef.shape = shape;
            body.createFixture(fixtureDef);
        });
    }
    private void addInteractiveObjects() {
        /**
         * Das wollte Viktor ja noch ändern ?
         */
        tiledMap.getLayers().get("contextLayer").getObjects().getByType(RectangleMapObject.class).forEach(interactiveObject -> {
            String name = interactiveObject.getName();
            if (!(name.equals("Hotel") || name.equals("Park") || name.equals("Disco"))) {
                Rectangle rectangle = interactiveObject.getRectangle();
                new InteractiveTileObject(world, tiledMap.getProperties().get("tilewidth", Integer.class), rectangle);
            }
        });
        /**********************************************************************************************************/
    }

    private void addAvatars() {
        internUserAvatar = new InternUserAvatar(Chati.getInstance().getUserManager().getInternUserView());
        Chati.getInstance().getUserManager().getUsersInRoom().values()
                .forEach(externUser -> externUserAvatars.add(new UserAvatar(externUser)));
    }

    public World getWorld() {
        return world;
    }
}
