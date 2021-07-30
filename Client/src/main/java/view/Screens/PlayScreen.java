package view.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import model.communication.message.MessageType;
import model.context.spatial.Menu;
import view.Chati;
import view.Sprites.Avatar;
import view.UIComponents.Hud;

import java.time.LocalDateTime;
import java.util.UUID;

public class PlayScreen extends ApplicationScreen {

    //Map variables
    private TmxMapLoader mapLoader;
    private TiledMap map;
    private OrthogonalTiledMapRenderer mapRenderer;

    //Box2d variables
    private World world;
    private Box2DDebugRenderer box2DDebugRenderer;

    private Avatar avatar;

    public PlayScreen(Chati game) {
        super(game);
        gamecam = new OrthographicCamera();
        gamePort = new FitViewport(Chati.V_WIDTH / Chati.PPM, Chati.V_HEIGHT/ Chati.PPM, gamecam);
        this.hud = new Hud(spriteBatch, this);

        mapLoader = new TmxMapLoader();
        map = mapLoader.load("maps/map.tmx");
        mapRenderer = new OrthogonalTiledMapRenderer(map, 1 / Chati.PPM);
        world = new World(new Vector2(0, 0), true);
        box2DDebugRenderer = new Box2DDebugRenderer();

        addBorders(map.getLayers().get("Borders").getObjects().getByType(RectangleMapObject.class), false);
        addBorders(map.getLayers().get("Interactable").getObjects().getByType(RectangleMapObject.class), true);

        int id = (int) map.getLayers().get("Interactable").getObjects().getByType(RectangleMapObject.class).get(0).getProperties().get("ID");
        System.out.println(id);

        gamecam.position.set(4 , 4, 0);

        avatar = new Avatar(world, map);
    }

    @Override
    public void render(float delta) {
        update(delta);

        //Clear game screen with black
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        mapRenderer.render();
        box2DDebugRenderer.render(world, gamecam.combined);
        spriteBatch.setProjectionMatrix(hud.getStage().getCamera().combined);
        hud.getStage().draw();

    }

    private void update(float dt) {
        /*
        boolean keyPressedUp = Gdx.input.isKeyPressed(Input.Keys.UP);
        boolean keyPressedDown = Gdx.input.isKeyPressed(Input.Keys.DOWN);
        boolean keyPressedLeft = Gdx.input.isKeyPressed(Input.Keys.LEFT);
        boolean keyPressedRight = Gdx.input.isKeyPressed(Input.Keys.RIGHT);

        if (keyPressedUp) {
            gamecam.position.y += 100*dt;
        } else if (keyPressedDown) {
            gamecam.position.y -= 100*dt;
        } else if (keyPressedLeft) {
            gamecam.position.x -= 100*dt;
        } else if (keyPressedRight) {
            gamecam.position.x += 100*dt;
        } */


        handleInput(dt);
        world.step(1 / 30f, 6, 2); // 1 time per minute
        gamecam.update();
        mapRenderer.setView(gamecam);
    }

    private void handleInput(float dt) {
        MapProperties prop = map.getProperties();
        int mapLeft = 0;
        int mapBottom = 0;
        float mapTop = prop.get("height", Integer.class); // dimension in tiles
        float mapRight = prop.get("width", Integer.class); // dimension in tiles
        float tilePixelWidth = prop.get("tilewidth", Integer.class);
        float tilePixelHeight = prop.get("tileheight", Integer.class);
        float mapPixelsTop = mapTop * tilePixelHeight;
        float mapPixelRight = mapRight * tilePixelWidth;

        float cameraHalfWidth = Chati.V_WIDTH / 2 ;
        float cameraHalfHeight = Chati.V_HEIGHT / 2;

        float cameraLeftBoundary = (mapLeft + cameraHalfWidth) / Chati.PPM;
        float cameraBottomBoundary = (mapBottom + cameraHalfHeight) / Chati.PPM;
        float cameraRightBoundary = (mapPixelRight - cameraHalfWidth) / Chati.PPM;
        float cameraTopBoundary = (mapPixelsTop - cameraHalfHeight) / Chati.PPM;

        Body body = avatar.getBody();
        Vector2 bodyCenter = body.getWorldCenter();
        int bodyMovingForce = 50;
        Vector2 bodyPosition = body.getPosition();
        boolean keyPressedUp = Gdx.input.isKeyPressed(Input.Keys.UP);
        boolean keyPressedDown = Gdx.input.isKeyPressed(Input.Keys.DOWN);
        boolean keyPressedLeft = Gdx.input.isKeyPressed(Input.Keys.LEFT);
        boolean keyPressedRight = Gdx.input.isKeyPressed(Input.Keys.RIGHT);

        float newPosoitionX = dt*10;
        float newPosoitionY = dt*10;

        if (keyPressedLeft && body.getLinearVelocity().x >= -20) {
            body.applyLinearImpulse(new Vector2(-bodyMovingForce, 0), bodyCenter, true);
        } else if (keyPressedRight && body.getLinearVelocity().x <= 20) {
            body.applyLinearImpulse(new Vector2(bodyMovingForce, 0), bodyCenter, true);
        } else if (keyPressedUp && body.getLinearVelocity().y <= 20) {
            body.applyLinearImpulse(new Vector2(0, bodyMovingForce), bodyCenter, true);
        } else if (keyPressedDown && body.getLinearVelocity().y >= -20) {
            body.applyLinearImpulse(new Vector2(0, -bodyMovingForce), bodyCenter, true);
        } else if (!keyPressedDown && !keyPressedUp && !keyPressedLeft && !keyPressedRight) {
            Vector2 velocityVector = body.getLinearVelocity();
            if (velocityVector != new Vector2(0, 0)) {
                body.applyForce(new Vector2(-20 * velocityVector.x, -20 * velocityVector.y), bodyCenter, true);
            }
        }

        if (bodyPosition.x >= cameraLeftBoundary && bodyPosition.x <= cameraRightBoundary) {
            gamecam.position.x = body.getPosition().x;
        } else if (bodyPosition.x < cameraLeftBoundary){
            gamecam.position.x = cameraLeftBoundary;
        } else if (bodyPosition.x > cameraRightBoundary){
            gamecam.position.x = cameraRightBoundary;
        }

        if (bodyPosition.y >= cameraBottomBoundary && bodyPosition.y <= cameraTopBoundary) {
            gamecam.position.y = body.getPosition().y;
        } else if (bodyPosition.y > cameraTopBoundary){
            gamecam.position.y = cameraTopBoundary;
        } else if (bodyPosition.y < cameraBottomBoundary) {
            gamecam.position.y = cameraBottomBoundary;
        }
    }

    @Override
    public void resize(int width, int height) {
        gamePort.update(width, height);
    }

    private void addBorders(Array<RectangleMapObject> borders, boolean isCollidable) {
        for (RectangleMapObject object: borders) {
            Rectangle bounds = object.getRectangle();
            BodyDef bdef = new BodyDef();
            PolygonShape shape= new PolygonShape();
            FixtureDef fdef = new FixtureDef();
            Body body;


            bdef.type = BodyDef.BodyType.StaticBody;
            bdef.position.set((bounds.getX() + bounds.getWidth() / 2) / Chati.PPM, (bounds.getY() + bounds.getHeight() / 2) / Chati.PPM);

            body = world.createBody(bdef);

            shape.setAsBox((bounds.getWidth() / 2) / Chati.PPM, (bounds.getHeight() / 2) / Chati.PPM);

            if (isCollidable) {
                fdef.isSensor = true;
            }

            fdef.shape = shape;
            body.createFixture(fdef);
        }
    }
}
