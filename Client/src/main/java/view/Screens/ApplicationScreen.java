package view.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.MapRenderer;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import controller.network.ServerSender;
import model.context.spatial.ILocationView;
import model.context.spatial.SpatialMap;
import model.user.IInternUserView;
import model.user.IUserManagerView;
import model.user.IUserView;
import model.user.InternUser;
import view.Chati;
import view.Sprites.Avatar;
import view.Sprites.InteractiveTileObject;
import view.UIComponents.Hud;
import view.UIComponents.LoginTable;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class ApplicationScreen implements Screen {
    protected Chati game;
    protected SpriteBatch spriteBatch = new SpriteBatch();
    protected OrthographicCamera gamecam;
    protected Viewport gamePort;
    protected Hud hud;

    private enum KEY_PRESS {
        RIGHT, LEFT, DOWN, UP, NONE
    }

    //Map variables
    private TmxMapLoader mapLoader;
    private TiledMap map;
    private OrthogonalTiledMapRenderer mapRenderer;

    //Box2d variables
    private World world;
    private Box2DDebugRenderer box2DDebugRenderer;

    private ArrayList<Avatar> avatars;
    private Avatar avatar;
    private KEY_PRESS currentKeyPressed = KEY_PRESS.NONE;
    private final int bodyMovingForce = 25;

    private int userAvatarIndex = 0;

    public ApplicationScreen(Hud hud) {
        this.hud = hud;
    }

    public ApplicationScreen (Chati game) {
        this.game = game;
        this.hud = new Hud(spriteBatch, this);
        gamecam = new OrthographicCamera();
        gamePort = new FitViewport(Chati.V_WIDTH / Chati.PPM, Chati.V_HEIGHT / Chati.PPM, gamecam);
        hud.addMenuTable(new LoginTable(hud));

    }


    public ApplicationScreen(Hud hud, String mapPath , Map<UUID, IUserView> users) {  //
        gamecam = new OrthographicCamera();
        gamePort = new FitViewport(Chati.V_WIDTH / Chati.PPM, Chati.V_HEIGHT / Chati.PPM, gamecam);
        this.hud = hud;

        mapLoader = new TmxMapLoader();
        map = mapLoader.load("maps/map.tmx");   //mapPath
        mapRenderer = new OrthogonalTiledMapRenderer(map, 1 / Chati.PPM);
        world = new World(new Vector2(0, 0), true);
        world.setContactListener(new WorldContactListener());
        box2DDebugRenderer = new Box2DDebugRenderer();

        avatars = new ArrayList<>();

        /*
        Avatar userAvatar = new Avatar(world, new Vector2(50 , 40));
        Avatar test = new Avatar(world, new Vector2(40, 80));
        avatars.add(test);
        avatars.add(userAvatarIndex,userAvatar);
         */

        IInternUserView user = game.getUserManager().getInternUserView();
        for (var entry : users.entrySet()) {
            if (entry.getKey().equals(user.getUserId())) {
                Avatar userAvatar = new Avatar(world, user.getUserId(), user.getCurrentLocation().getPosX(), user.getCurrentLocation().getPosY());
                avatars.add(userAvatarIndex,userAvatar);
            }
            ILocationView position = entry.getValue().getCurrentLocation();
            Avatar avatar = new Avatar(world, entry.getKey(), position.getPosX(), position.getPosY());
            avatars.add(avatar);
        }

        addBorders(map.getLayers().get("Borders").getObjects().getByType(RectangleMapObject.class));

        /*
        for (MapObject object: map.getLayers().get("Interactable").getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rectangle = ((RectangleMapObject) object).getRectangle();
            new InteractiveTileObject(world, rectangle);
        }
         */

    }

    @Override
    public void render(float delta) {
        //Clear game screen
        Gdx.gl.glClearColor(0, 102/255f, 102/255f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (!Objects.isNull(mapRenderer) && !Objects.isNull(box2DDebugRenderer)) {

            mapRenderer.render();
            box2DDebugRenderer.render(world, gamecam.combined);
            spriteBatch.setProjectionMatrix(gamecam.combined);
            spriteBatch.begin();
            for( Avatar avatar :  avatars) {
                avatar.draw(spriteBatch);
                avatar.update(delta);
            }
            spriteBatch.end();
            update(delta);
        }

        spriteBatch.setProjectionMatrix(hud.getCamera().combined);
        hud.act();
        hud.draw();
    }

    @Override
    public void resize(int width, int height) {
        gamePort.update(width, height);
    }

    @Override
    public void show() {

    }

    public Chati getGame() {
        return game;
    }

    public Hud getHud() {
        return hud;
    }


    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }

    private void update(float dt) {
        handleInput();
        world.step(1 / 30f, 6, 2);
        gamecam.update();
        mapRenderer.setView(gamecam);
    }

    private void handleInput() {
        MapProperties prop = map.getProperties();
        int mapLeft = 0;
        int mapBottom = 0;
        float mapTop = prop.get("height", Integer.class); // dimension in tiles
        float mapRight = prop.get("width", Integer.class); // dimension in tiles
        float tilePixelWidth = prop.get("tilewidth", Integer.class);
        float tilePixelHeight = prop.get("tileheight", Integer.class);
        float mapPixelsTop = mapTop * tilePixelHeight;
        float mapPixelRight = mapRight * tilePixelWidth;

        float cameraHalfWidth = Chati.V_WIDTH / 2;
        float cameraHalfHeight = Chati.V_HEIGHT / 2;

        float cameraLeftBoundary = (mapLeft + cameraHalfWidth) / Chati.PPM;
        float cameraBottomBoundary = (mapBottom + cameraHalfHeight) / Chati.PPM;
        float cameraRightBoundary = (mapPixelRight - cameraHalfWidth) / Chati.PPM;
        float cameraTopBoundary = (mapPixelsTop - cameraHalfHeight) / Chati.PPM;

        Body body = avatars.get(userAvatarIndex).getBody();
        Vector2 bodyCenter = body.getWorldCenter();

        Vector2 bodyPosition = body.getPosition();
        boolean keyPressedUp = Gdx.input.isKeyPressed(Input.Keys.UP);
        boolean keyPressedDown = Gdx.input.isKeyPressed(Input.Keys.DOWN);
        boolean keyPressedLeft = Gdx.input.isKeyPressed(Input.Keys.LEFT);
        boolean keyPressedRight = Gdx.input.isKeyPressed(Input.Keys.RIGHT);

        if (keyPressedLeft && body.getLinearVelocity().x >= -20 && (currentKeyPressed.equals(KEY_PRESS.LEFT) || currentKeyPressed.equals(KEY_PRESS.NONE))) {
            body.applyLinearImpulse(new Vector2(-bodyMovingForce, 0), bodyCenter, true);
            currentKeyPressed = KEY_PRESS.LEFT;
        } else if (keyPressedRight && body.getLinearVelocity().x <= 20 && (currentKeyPressed.equals(KEY_PRESS.RIGHT) || currentKeyPressed.equals(KEY_PRESS.NONE))) {
            body.applyLinearImpulse(new Vector2(bodyMovingForce, 0), bodyCenter, true);
            currentKeyPressed = KEY_PRESS.RIGHT;
        } else if (keyPressedUp && body.getLinearVelocity().y <= 20 && (currentKeyPressed.equals(KEY_PRESS.UP) || currentKeyPressed.equals(KEY_PRESS.NONE))) {
            body.applyLinearImpulse(new Vector2(0, bodyMovingForce), bodyCenter, true);
            currentKeyPressed = KEY_PRESS.UP;
        } else if (keyPressedDown && body.getLinearVelocity().y >= -20 && (currentKeyPressed.equals(KEY_PRESS.LEFT) || currentKeyPressed.equals(KEY_PRESS.NONE))) {
            body.applyLinearImpulse(new Vector2(0, -bodyMovingForce), bodyCenter, true);
            currentKeyPressed = KEY_PRESS.DOWN;
        } else if (!keyPressedDown && !keyPressedUp && !keyPressedLeft && !keyPressedRight) {
            body.setLinearVelocity(0, 0);
            currentKeyPressed = KEY_PRESS.NONE;
        }

        if (bodyPosition.x >= cameraLeftBoundary && bodyPosition.x <= cameraRightBoundary) {
            gamecam.position.x = body.getPosition().x;
        } else if (bodyPosition.x < cameraLeftBoundary) {
            gamecam.position.x = cameraLeftBoundary;
        } else if (bodyPosition.x > cameraRightBoundary) {
            gamecam.position.x = cameraRightBoundary;
        }

        if (bodyPosition.y >= cameraBottomBoundary && bodyPosition.y <= cameraTopBoundary) {
            gamecam.position.y = body.getPosition().y;
        } else if (bodyPosition.y > cameraTopBoundary) {
            gamecam.position.y = cameraTopBoundary;
        } else if (bodyPosition.y < cameraBottomBoundary) {
            gamecam.position.y = cameraBottomBoundary;
        }

       // sendPositionToServer(body.getPosition().x, body.getPosition().y);   //Test DELETE
    }

    private void sendPositionToServer(float posX, float posY) {
        int positionX = (int) posX;
        int positionY = (int) posY;
        Object[] position = {positionX, positionY};
        hud.getSender().send(ServerSender.SendAction.AVATAR_MOVE, position);
    }

    public void updateAvatarsPositions() {
        new Thread(() -> {
            for(var entry : game.getUserManager().getActiveUsers().entrySet()) {
                for(Avatar avatar : avatars) {
                    if(avatar.getUser().equals(entry.getValue().getUserId())) {
                        Vector2 newPosition = new Vector2(entry.getValue().getCurrentLocation().getPosX(), entry.getValue().getCurrentLocation().getPosY());
                        moveAvatar(avatar, newPosition, false);
                    }
                }
            }
        });
    }

    public void moveAvatar(Avatar avatar, Vector2 endPosition, boolean teleport) {
        if(teleport) {
            avatar.getBdef().position.set(endPosition);
        } else {
            Vector2 currentPosition = avatar.getBody().getWorldCenter();
            Vector2 normMovingVector = endPosition.sub(currentPosition).nor();

            while (!currentPosition.epsilonEquals(endPosition)) {
                avatar.getBody().applyLinearImpulse(normMovingVector.scl(bodyMovingForce), currentPosition, true);
            }
            avatar.getBody().setLinearVelocity(0,0);
        }
    }

    private void addBorders(Array<RectangleMapObject> borders) {
        for (RectangleMapObject object : borders) {
            Rectangle bounds = object.getRectangle();
            BodyDef bdef = new BodyDef();
            PolygonShape shape = new PolygonShape();
            FixtureDef fdef = new FixtureDef();

            bdef.type = BodyDef.BodyType.StaticBody;
            bdef.position.set((bounds.getX() + bounds.getWidth() / 2) / Chati.PPM, (bounds.getY() + bounds.getHeight() / 2) / Chati.PPM);

            Body body = world.createBody(bdef);
            shape.setAsBox((bounds.getWidth() / 2) / Chati.PPM, (bounds.getHeight() / 2) / Chati.PPM);

            fdef.shape = shape;
            body.createFixture(fdef);
        }
    }


}