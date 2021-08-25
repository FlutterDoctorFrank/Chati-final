package view2.component.world.body;

import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;
import model.context.spatial.ILocationView;
import model.user.IUserView;
import view2.Assets;
import view2.Chati;
import view2.component.UserInfoContainer;
import view2.component.hud.settings.WorldSettingsTable;
import view2.component.world.WorldScreen;

public class UserAvatar extends Sprite {

    public static final float DEFAULT_VELOCITY = 12.5f;
    public static final float SPRINT_VELOCITY_FACTOR = 1.75f;
    public static final float COMMUNICABLE_USER_ICON_SIZE = 35;

    protected final IUserView user;
    protected final Body body;

    private final UserInfoContainer userInfoContainer;
    private final Table communicableIconContainer;
    private final Image communicableIcon;

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
        this.userInfoContainer = new UserInfoContainer(user);
        this.userInfoContainer.setTransform(true);
        this.communicableIconContainer = new Table();
        this.communicableIconContainer.setTransform(true);
        this.communicableIcon = new Image();
        this.communicableIconContainer.add(communicableIcon).size(COMMUNICABLE_USER_ICON_SIZE);

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        this.body = Chati.CHATI.getWorldScreen().getWorld().createBody(bodyDef);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.filter.categoryBits = WorldScreen.USER_BIT;
        fixtureDef.filter.maskBits = 0;
        PolygonShape shape = new PolygonShape();
        shape.setAsBox((32 - 1) / WorldScreen.PPM / 2, (32 - 1) / WorldScreen.PPM / 2);
        fixtureDef.shape = shape;
        body.createFixture(fixtureDef);
        body.setUserData(BodyType.USER);

        initializeSprite();
        this.stateTimer = 0;
        currentDirection = Direction.DOWN;
        previousDirection = Direction.DOWN;
    }

    @Override
    public void draw(Batch batch, float delta) {
        //InteractButtonAnimation animation = new InteractButtonAnimation();
        //animation.draw(batch);
        if (Chati.CHATI.getWorldScreen().getWorldInputProcessor().isShowNamesPressed()
            || WorldSettingsTable.SHOW_NAME) {
            userInfoContainer.setPosition(body.getPosition().x, body.getPosition().y + getHeight());
            userInfoContainer.setScale(1 / WorldScreen.PPM * Chati.CHATI.getWorldScreen().getCamera().zoom);
            userInfoContainer.act(delta);
            userInfoContainer.draw(batch, 1);
            if (!user.equals(Chati.CHATI.getUserManager().getInternUserView())) {
                if (user.canCommunicateWith()) {
                    communicableIcon.setDrawable(Assets.COMMUNICABLE_ICON);
                } else {
                    communicableIcon.setDrawable(null);
                }
                communicableIconContainer.setPosition(body.getPosition().x, body.getPosition().y + 1.5f * getHeight());
                communicableIconContainer.setScale(1 / WorldScreen.PPM * Chati.CHATI.getWorldScreen().getCamera().zoom);
                communicableIconContainer.act(delta);
                communicableIconContainer.draw(batch, 1);
            }
        }
        setPosition(body.getPosition().x - getWidth() / 2, body.getPosition().y - getHeight() / 2);
        setRegion(getCurrentFrameRegion(delta));
        super.draw(batch);
    }

    private void initializeSprite() {
        TextureAtlas atlas = new TextureAtlas(user.getAvatar().getPath());

        TextureAtlas.AtlasRegion stand = atlas.findRegion(user.getAvatar().getIdleRegion());
        avatarStandRight = new TextureRegion(stand, 0, 16, 32, 48);
        avatarStandUp = new TextureRegion(stand, 32, 16, 32, 48);
        avatarStandLeft = new TextureRegion(stand, 64, 16, 32, 48);
        avatarStandDown = new TextureRegion(stand, 96, 16, 32, 48); /** Alles hardgecoded... */

        TextureAtlas.AtlasRegion run = atlas.findRegion(user.getAvatar().getRunRegion());
        Array<TextureRegion> frames = new Array<>();
        //running right animation
        for (int i = 0; i < 6; i++) frames.add(new TextureRegion(run, i * 32, 16, 32, 48));
        avatarRunRight = new Animation<>(0.1f, frames);
        frames.clear();
        //running up animation
        for (int i = 6; i < 12; i++) frames.add(new TextureRegion(run, i * 32, 16, 32, 48));
        avatarRunUp = new Animation<>(0.1f, frames);
        frames.clear();
        //running left animation
        for (int i = 12; i < 18; i++) frames.add(new TextureRegion(run, i * 32, 16, 32, 48));
        avatarRunLeft = new Animation<>(0.1f, frames);
        frames.clear();
        //running down animation
        for (int i = 18; i < 24; i++) frames.add(new TextureRegion(run, i * 32, 16, 32, 48));
        avatarRunDown = new Animation<>(0.1f, frames);
        frames.clear();

        setBounds(0, 0, 32 / WorldScreen.PPM, 32 / WorldScreen.PPM);
        setRegion(avatarStandDown);
    }

    private TextureRegion getCurrentFrameRegion(float delta) {
        currentDirection = getCurrentDirection();
        stateTimer = currentDirection == previousDirection ? stateTimer + delta : 0;
        if (currentDirection == null) {
            if (previousDirection == Direction.UP) {
                return avatarStandUp;
            }
            if (previousDirection == Direction.LEFT) {
                return avatarStandLeft;
            }
            if (previousDirection == Direction.RIGHT) {
                return avatarStandRight;
            }
            return avatarStandDown;
        } else {
            previousDirection = currentDirection;
            if (currentDirection == Direction.UP) {
                return avatarRunUp.getKeyFrame(stateTimer, true);
            }
            if (currentDirection == Direction.LEFT) {
                return avatarRunLeft.getKeyFrame(stateTimer, true);
            }
            if (currentDirection == Direction.RIGHT) {
                return avatarRunRight.getKeyFrame(stateTimer, true);
            }
            return avatarRunDown.getKeyFrame(stateTimer, true);
        }
    }

    private Direction getCurrentDirection() {
        Vector2 velocity = body.getLinearVelocity();
        if (velocity.y > Math.abs(velocity.x)) {
            return Direction.UP;
        }
        if (velocity.x < -Math.abs(velocity.y)) {
            return Direction.LEFT;
        }
        if (velocity.y < -Math.abs(velocity.x)) {
            return Direction.DOWN;
        }
        if (velocity.x > Math.abs(velocity.y)) {
            return Direction.RIGHT;
        }
        return null;
    }

    public void teleport() {
        ILocationView location = user.getLocation();
        if (location == null) {
            return;
        }
        Vector2 newPosition = new Vector2(location.getPosX() / WorldScreen.PPM, location.getPosY() / WorldScreen.PPM);
        body.setTransform(newPosition, body.getAngle());
        body.setAwake(true);
    }

    public void update() {
        ILocationView location = user.getLocation();
        if (location == null) {
            return;
        }
        if (user.isTeleporting()) {
            teleport();
        } else {
            float velocity = DEFAULT_VELOCITY;
            if (user.isSprinting()) {
                velocity *= SPRINT_VELOCITY_FACTOR;
            }
            Vector2 destination = new Vector2(location.getPosX() / WorldScreen.PPM, location.getPosY() / WorldScreen.PPM);
            if (body.getPosition().epsilonEquals(destination)) {
                body.setLinearVelocity(0, 0);
            } else {
                float distance = body.getPosition().dst(destination);
                Vector2 velocityVector = destination.cpy().sub(body.getPosition()).nor();
                if (distance <= velocity / WorldScreen.WORLD_STEP) {
                    body.setLinearVelocity(velocityVector.scl(distance * WorldScreen.WORLD_STEP));
                } else {
                    body.setLinearVelocity(velocityVector.scl(velocity));
                }
            }
        }
    }

    public IUserView getUser() {
        return user;
    }

    public Vector2 getPosition() {
        return body.getPosition();
    }

    public Body getBody() {
        return body;
    }

    private class InteractButtonAnimation extends Sprite {

        private final Animation<TextureRegion> interactionButton;
        private float buttonStateTimer;

        public InteractButtonAnimation() {
            buttonStateTimer = 0;
            Array<TextureRegion> frames = new Array<>();
            TextureAtlas buttonAtlas = new TextureAtlas("icons/interact_button/rotating_button.pack");
            for (int i = 0; i < 4; i++) {
                frames.add(new TextureRegion(buttonAtlas.findRegion("button"), i * 16, 0, 16, 16)); /** Hard gecoded...*/
            }
            interactionButton = new Animation<>(0.1f, frames);
            setBounds(0, 0, 16 / WorldScreen.PPM, 16 / WorldScreen.PPM);
            setRegion(buttonAtlas.findRegion("button"), 0, 0, 16, 16);
        }

        @Override
        public void draw(Batch batch, float delta) {
            this.setPosition(body.getPosition().x - getWidth() / 2, body.getPosition().y + getHeight());
            this.setRegion(getButtonFrame(delta));
        }

        private TextureRegion getButtonFrame(float delta) {
            TextureRegion region = interactionButton.getKeyFrame(buttonStateTimer, true);
            buttonStateTimer += delta;
            return region;
        }
    }
}
