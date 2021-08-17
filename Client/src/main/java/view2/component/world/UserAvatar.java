package view2.component.world;

import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import model.user.IUserView;

public class UserAvatar extends Sprite {

    public static final int BODY_MOVING_FORCE = 20;

    private enum Direction {UP, LEFT, DOWN, RIGHT, NEUTRAL}

    private final IUserView user;
    protected final Body body;

    private TextureRegion avatarStandUp;
    private TextureRegion avatarStandLeft;
    private TextureRegion avatarStandDown;
    private TextureRegion avatarStandRight;
    private Animation<TextureRegion> avatarRunUp;
    private Animation<TextureRegion> avatarRunLeft;
    private Animation<TextureRegion> avatarRunDown;
    private Animation<TextureRegion> avatarRunRight;
    // private Animation<TextureRegion> avatarDance;

    private Direction currentDirection;
    private Direction previousDirection;
    private float stateTimer;

    public UserAvatar(IUserView user) {
        this.user = user;

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        // Sollte später weg, wenn der Server die initiale Position festlegt?


        float userInitPosX = (int) WorldScreen.getInstance().getTiledMap().getProperties().get("spawnPosX") / WorldScreen.PPM;
        float userInitPosY = (int) WorldScreen.getInstance().getTiledMap().getProperties().get("spawnPosY") / WorldScreen.PPM;
        bodyDef.position.set(userInitPosX, userInitPosY);
        /*
        body.setTransform(new Vector2(user.getCurrentLocation().getPosX() / WorldScreen.PPM, user.getCurrentLocation().getPosY() / WorldScreen.PPM), body.getAngle());
        body.setAwake(true);
         */
        this.body = WorldScreen.getInstance().getWorld().createBody(bodyDef);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.filter.categoryBits = WorldScreen.AVATAR_BIT;
        fixtureDef.filter.maskBits = WorldScreen.OBJECT_BIT | WorldScreen.GROUND_BIT;
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(1.5f, 1.5f); /** Hardgecodete zahlen ? */
        fixtureDef.shape = shape;
        this.body.createFixture(fixtureDef);

        this.stateTimer = 0;

        initializeTextures();
    }

    @Override
    public void draw(Batch batch, float delta) {
        InteractButtonAnimation animation = new InteractButtonAnimation();
        animation.draw(batch);
        animation.update(delta);
        setPosition(getBody().getPosition().x - getWidth() / 2, getBody().getPosition().y - getHeight() / 2);
        setRegion(getCurrentFrameRegion(delta));

        super.draw(batch);
    }

    private void initializeTextures() {
        TextureAtlas atlas = new TextureAtlas("skins/Adam/Adam.pack");

        TextureAtlas.AtlasRegion stand = atlas.findRegion("Adam_idle");
        avatarStandRight = new TextureRegion(stand, 0, 16, 32, 48);
        avatarStandUp = new TextureRegion(stand, 32, 16, 32, 48);
        avatarStandLeft = new TextureRegion(stand, 64, 16, 32, 48);
        avatarStandDown = new TextureRegion(atlas.findRegion("Adam_idle"), 96, 16, 32, 48); /** Alles hardgecoded... */
        setBounds(0, 0, 32 / WorldScreen.PPM, 32 / WorldScreen.PPM);
        setRegion(avatarStandDown);
        currentDirection = Direction.DOWN;
        previousDirection = Direction.DOWN;

        TextureAtlas.AtlasRegion run = atlas.findRegion("Adam_run");
        Array<TextureRegion> frames = new Array<>();

        //running right animation
        for (int i = 0; i < 6; i++) {
            frames.add(new TextureRegion(run, i * 32, 16, 32, 48));
        }
        avatarRunRight = new Animation<>(0.1f, frames);
        frames.clear();

        //running up animation
        for (int i = 6; i < 12; i++) {
            frames.add(new TextureRegion(run, i * 32, 16, 32, 48));
        }
        avatarRunUp = new Animation<>(0.1f, frames);
        frames.clear();

        //running left animation
        for (int i = 12; i < 18; i++) {
            frames.add(new TextureRegion(run, i * 32, 16, 32, 48));
        }
        avatarRunLeft = new Animation<>(0.1f, frames);
        frames.clear();

        //running down animation
        for (int i = 18; i < 24; i++) {
            frames.add(new TextureRegion(run, i * 32, 16, 32, 48));
        }
        avatarRunDown = new Animation<>(0.1f, frames);
        frames.clear();

        //dance animation
        for (int i = 0; i < 5; i++) {
            frames.add(new TextureRegion(atlas.findRegion("Adam_idle"), i * 32, 16, 32, 48));
        }
        // avatarDance = new Animation<>(0.1f, frames);
        frames.clear();
    }

    private TextureRegion getCurrentFrameRegion(float delta) {
        currentDirection = getDirection();
        stateTimer = currentDirection == previousDirection ? stateTimer + delta : 0;
        if (!currentDirection.equals(Direction.NEUTRAL)) {
            previousDirection = currentDirection;
        }
        switch (currentDirection) {
            case UP:
                return avatarRunUp.getKeyFrame(stateTimer, true);
            case LEFT:
                return avatarRunLeft.getKeyFrame(stateTimer, true);
            case DOWN:
                return avatarRunDown.getKeyFrame(stateTimer, true);
            case RIGHT:
                return avatarRunRight.getKeyFrame(stateTimer, true);
            case NEUTRAL:
                switch (previousDirection) {
                    case UP:
                        return avatarStandUp;
                    case LEFT:
                        return avatarStandLeft;
                    case RIGHT:
                        return avatarStandRight;
                    default:
                        return avatarStandDown;
            }
            default:
                return null;
        }
    }

    private Direction getDirection() {
        if (body.getLinearVelocity().y > 0)
            return Direction.UP;
        else if (body.getLinearVelocity().y < 0)
            return Direction.DOWN;
        else if (body.getLinearVelocity().x > 0)
            return Direction.RIGHT;
        else if (body.getLinearVelocity().x < 0)
            return Direction.LEFT;
        else
            return Direction.NEUTRAL;
    }

    public Body getBody() {
        return body;
    }

    public IUserView getUser() {
        return user;
    }

    public void updatePosition(boolean teleport) {
        Vector2 newPosition = new Vector2(user.getCurrentLocation().getPosX(), user.getCurrentLocation().getPosY());
        if (teleport) {
            body.setTransform(newPosition, body.getAngle());
            body.setAwake(true);
        } else {
            new Thread(() -> {
                Vector2 currentPosition = body.getWorldCenter();
                Vector2 normMovingVector = newPosition.sub(currentPosition).nor();

                while (!currentPosition.epsilonEquals(newPosition)) {
                    body.applyLinearImpulse(normMovingVector.scl(BODY_MOVING_FORCE), body.getWorldCenter(), true);
                }
                body.setLinearVelocity(0, 0);
            }).start();
        }
    }










    private class InteractButtonAnimation extends Sprite {

        private final Animation<TextureRegion> interactionButton;
        private float buttonStateTimer;

        public InteractButtonAnimation() {
            buttonStateTimer = 0;
            Array<TextureRegion> frames = new Array<TextureRegion>();
            TextureAtlas buttonAtlas = new TextureAtlas("icons/interact_button/rotating_button.pack");
            for (int i = 0; i < 4; i++) {
                frames.add(new TextureRegion(buttonAtlas.findRegion("button"), i * 16, 0, 16, 16));
            }
            interactionButton = new Animation<>(0.1f, frames);

            setBounds(0, 0, 16 / WorldScreen.PPM, 16 / WorldScreen.PPM);
            setRegion(buttonAtlas.findRegion("button"), 0, 0, 16, 16);
        }

        public void update(float dt) {
            this.setPosition(getBody().getPosition().x - getWidth() / 2, getBody().getPosition().y + getHeight());
            this.setRegion(getButtonFrame(dt));
        }

        private  TextureRegion getButtonFrame(float dt) {
            TextureRegion region = interactionButton.getKeyFrame(buttonStateTimer, true);
            buttonStateTimer += dt;
            return region;
        }
    }
}
