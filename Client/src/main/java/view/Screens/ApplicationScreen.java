package view.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapObject;
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
import com.badlogic.gdx.utils.viewport.Viewport;
import controller.network.ServerSender;
import model.context.spatial.ILocationView;
import model.user.IInternUserView;
import model.user.IUserView;
import view.Chati;
import view.Sprites.Avatar;
import view.Sprites.InteractiveTileObject;
import view.Sprites.UserAvatar;
import view.UIComponents.Hud;
import view.UIComponents.LoginTable;
import java.util.*;

public class ApplicationScreen implements Screen {
    protected Chati game;
    protected SpriteBatch spriteBatch = new SpriteBatch();
    protected OrthographicCamera gamecam;
    protected Viewport gamePort;
    protected Hud hud;

    private static final int[] movementKeys = {Input.Keys.UP, Input.Keys.DOWN, Input.Keys.LEFT, Input.Keys.RIGHT};
    public static LinkedList<Integer> keyPresses = new LinkedList<>();

    //Map variables
    private TmxMapLoader mapLoader;
    private TiledMap map;
    private OrthogonalTiledMapRenderer mapRenderer;

    //Box2d variables
    private World world;
    private Box2DDebugRenderer box2DDebugRenderer;
    private WorldContactListener contactListener;

    private ArrayList<Avatar> avatars;
    private final int bodyMovingForce = 25;

    private int userAvatarIndex = 0;
    private Vector2 userAvatarPosition;

    public ApplicationScreen(Hud hud) {
        this.hud = hud;
    }

    public ApplicationScreen(Chati game) {
        this.game = game;
        this.hud = new Hud(spriteBatch, this);
        gamecam = new OrthographicCamera();
        gamePort = new FitViewport(Chati.V_WIDTH / Chati.PPM, Chati.V_HEIGHT / Chati.PPM, gamecam);
        hud.addMenuTable(new LoginTable(hud));

    }


    public ApplicationScreen(Chati game, Hud hud, String mapPath, Map<UUID, IUserView> users) {  //
        gamecam = new OrthographicCamera();
        gamePort = new FitViewport(Chati.V_WIDTH / Chati.PPM, Chati.V_HEIGHT / Chati.PPM, gamecam);
        this.hud = hud;
        this.contactListener = new WorldContactListener();

        mapLoader = new TmxMapLoader();
        map = mapLoader.load(mapPath);
        mapRenderer = new OrthogonalTiledMapRenderer(map, 1 / Chati.PPM);
        world = new World(new Vector2(0, 0), true);
        world.setContactListener(contactListener);
        box2DDebugRenderer = new Box2DDebugRenderer();

        addBorders(map.getLayers().get("Borders").getObjects().getByType(RectangleMapObject.class));
        for (MapObject object : map.getLayers().get("InteractiveObject").getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rectangle = ((RectangleMapObject) object).getRectangle();
            new InteractiveTileObject(world, map.getProperties().get("tilewidth", Integer.class), rectangle);
        }

        avatars = new ArrayList<>();

        IInternUserView user = game.getUserManager().getInternUserView();
        float userInitPosX = (int) map.getProperties().get("spawnPosX") / Chati.PPM;
        float userInitPosY = (int) map.getProperties().get("spawnPosY") / Chati.PPM;
        Avatar userAvatar = new UserAvatar(world, user.getUserId(), userInitPosX, userInitPosY);
        userAvatarPosition = new Vector2(userInitPosX,userInitPosY);
        avatars.add(userAvatarIndex, userAvatar);
        for (var entry : users.entrySet()) {
            if (!entry.getKey().equals(user.getUserId())) {
                ILocationView position = entry.getValue().getLocation();
                Avatar avatar = new Avatar(world, entry.getKey(), position.getPosX() / Chati.PPM, position.getPosY() / Chati.PPM);
                avatars.add(avatar);
            }
        }


    }

    @Override
    public void render(float delta) {
        //Clear game screen
        Gdx.gl.glClearColor(0, 102 / 255f, 102 / 255f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (!Objects.isNull(mapRenderer) && !Objects.isNull(box2DDebugRenderer)) {

            mapRenderer.render();
            box2DDebugRenderer.render(world, gamecam.combined);
            spriteBatch.setProjectionMatrix(gamecam.combined);
            spriteBatch.begin();
            for (Avatar avatar : avatars) {
                avatar.draw(spriteBatch);
                avatar.update(delta, spriteBatch);
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

        body.setLinearVelocity(0, 0);

        switch (getKeyPressed()) {
            case Input.Keys.LEFT:
                if (body.getLinearVelocity().x >= -20) {
                    body.applyLinearImpulse(new Vector2(-bodyMovingForce, 0), bodyCenter, true);
                }
                break;
            case Input.Keys.RIGHT:
                if (body.getLinearVelocity().x <= 20) {
                    body.applyLinearImpulse(new Vector2(bodyMovingForce, 0), bodyCenter, true);
                }
                break;
            case Input.Keys.UP:
                if (body.getLinearVelocity().y <= 20) {
                    body.applyLinearImpulse(new Vector2(0, bodyMovingForce), bodyCenter, true);
                }
                break;
            case Input.Keys.DOWN:
                if (body.getLinearVelocity().y >= -20) {
                    body.applyLinearImpulse(new Vector2(0, -bodyMovingForce), bodyCenter, true);
                }
                break;
            default:
                body.setLinearVelocity(0, 0);
                break;
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

        //interact button
        if (Gdx.input.isKeyJustPressed(Input.Keys.E) && contactListener.canInteract()) {
            //TODO get current context
            /*
            Object[] context = {game.getUserManager().getInternUserView().getCurrentRoom().getContextId()};
            hud.getSender().send(ServerSender.SendAction.CONTEXT_INTERACT, context);
             */

            System.out.println("interagieren");
        }

        if (!userAvatarPosition.epsilonEquals(bodyPosition)) {
            sendPositionToServer(body.getPosition().x * Chati.PPM, body.getPosition().y * Chati.PPM);
            userAvatarPosition = new Vector2(bodyPosition.x, bodyPosition.y);
        }

    }

    private int getKeyPressed() {
        for (int key : movementKeys) {
            if (Gdx.input.isKeyPressed(key)) {
                if (!keyPresses.contains(key)) {
                    keyPresses.add(key);
                }
            } else {
                keyPresses.remove((Object) key);
            }
        }

        if (!keyPresses.isEmpty()) {
            return keyPresses.getLast();
        } else {
            return -2;
        }
    }

    private void sendPositionToServer(float posX, float posY) {
        int positionX = (int) posX;
        int positionY = (int) posY;
        Object[] position = {positionX, positionY};
        hud.getSender().send(ServerSender.SendAction.AVATAR_MOVE, position);
    }

    public void updateAvatarsPositions() {
        new Thread(() -> {
            for (var entry : game.getUserManager().getActiveUsers().entrySet()) {
                Avatar currentUserAvatar = findAvatar(entry.getValue().getUserId());

                if (!currentUserAvatar.equals(null)) {
                    Vector2 newPosition = new Vector2(entry.getValue().getLocation().getPosX(), entry.getValue().getLocation().getPosY());
                    moveAvatar(currentUserAvatar, newPosition, false);
                } else {
                    Avatar newAvatar = new Avatar(world, entry.getKey(), entry.getValue().getLocation().getPosX(), entry.getValue().getLocation().getPosY());
                    avatars.add(newAvatar);
                }
            }
        });
    }

    private Avatar findAvatar(UUID id) {
        for (Avatar avatar : avatars) {
            if (avatar.getUser().equals(id)) {
                return avatar;
            }
        }
        return null;
    }

    public void moveAvatar(Avatar avatar, Vector2 endPosition, boolean teleport) {
        if (teleport) {
            avatar.getBdef().position.set(endPosition);
        } else {
            Vector2 currentPosition = avatar.getBody().getWorldCenter();
            Vector2 normMovingVector = endPosition.sub(currentPosition).nor();

            while (!currentPosition.epsilonEquals(endPosition)) {
                avatar.getBody().applyLinearImpulse(normMovingVector.scl(bodyMovingForce), avatar.getBody().getWorldCenter(), true);
            }
            avatar.getBody().setLinearVelocity(0, 0);
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

    public TiledMap getMap() {
        return map;
    }
}