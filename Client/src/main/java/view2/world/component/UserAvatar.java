package view2.world.component;

import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import model.context.spatial.Direction;
import model.context.spatial.ILocationView;
import model.user.IUserView;
import view2.Chati;
import view2.userInterface.UserInfoContainer;
import view2.world.WorldCamera;
import view2.world.WorldScreen;

public class UserAvatar extends Sprite {

    public static final float DEFAULT_VELOCITY = 10f;
    public static final float SPRINT_VELOCITY_FACTOR = 1.67f;
    protected static final float AVATAR_SIZE = 32;
    private static final float FRAME_DURATION = 0.1f;

    protected final IUserView user;
    protected final Body body;

    private final Head head;

    private final TextureRegion avatarStandUp;
    private final TextureRegion avatarStandLeft;
    private final TextureRegion avatarStandDown;
    private final TextureRegion avatarStandRight;
    private final Animation<TextureRegion> avatarRunUp;
    private final Animation<TextureRegion> avatarRunLeft;
    private final Animation<TextureRegion> avatarRunDown;
    private final Animation<TextureRegion> avatarRunRight;

    protected Direction currentDirection;
    protected Direction previousDirection;
    private float stateTime;

    public UserAvatar(IUserView user) {
        this.user = user;
        this.head = new Head();

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        this.body = Chati.CHATI.getWorldScreen().getWorld().createBody(bodyDef);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.filter.categoryBits = WorldScreen.USER_BIT;
        fixtureDef.filter.maskBits = 0;
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(WorldCamera.scaleToUnit(AVATAR_SIZE - 1) / 2,
                WorldCamera.scaleToUnit(AVATAR_SIZE - 1) / 2);
        fixtureDef.shape = shape;
        body.createFixture(fixtureDef);
        body.setUserData(ContactType.USER);

        String avatarPath = user.getAvatar().getPath();
        avatarStandUp = Chati.CHATI.getDrawable(avatarPath + "_stand_up").getRegion();
        avatarStandLeft = Chati.CHATI.getDrawable(avatarPath + "_stand_left").getRegion();
        avatarStandDown = Chati.CHATI.getDrawable(avatarPath + "_stand_down").getRegion();
        avatarStandRight = Chati.CHATI.getDrawable(avatarPath + "_stand_right").getRegion();
        avatarRunUp = new Animation<>(FRAME_DURATION, Chati.CHATI.getRegions(avatarPath + "_move_up"));
        avatarRunLeft = new Animation<>(FRAME_DURATION, Chati.CHATI.getRegions(avatarPath + "_move_left"));
        avatarRunDown = new Animation<>(FRAME_DURATION, Chati.CHATI.getRegions(avatarPath + "_move_down"));
        avatarRunRight = new Animation<>(FRAME_DURATION, Chati.CHATI.getRegions(avatarPath + "_move_right"));
        setBounds(0, 0, WorldCamera.scaleToUnit(AVATAR_SIZE), WorldCamera.scaleToUnit(AVATAR_SIZE));
        setRegion(avatarStandDown);
        this.stateTime = 0;
    }

    @Override
    public void draw(Batch batch, float delta) {
        update();
        setPosition(body.getPosition().x - getWidth() / 2, body.getPosition().y - getHeight() / 2);
        setRegion(getKeyFrame(delta));
        super.draw(batch);
    }

    public void drawHead(Batch batch, float delta) {
        head.draw(batch, delta);
    }

    private TextureRegion getKeyFrame(float delta) {
        currentDirection = getCurrentDirection();
        stateTime = currentDirection == previousDirection ? stateTime + delta : 0;
        if (currentDirection == previousDirection && body.getLinearVelocity().isZero()) {
            switch (previousDirection) {
                case UP:
                    return avatarStandUp;
                case LEFT:
                    return avatarStandLeft;
                case DOWN:
                    return avatarStandDown;
                default:
                    return avatarStandRight;
            }
        } else {
            previousDirection = currentDirection;
            switch (currentDirection) {
                case UP:
                    return avatarRunUp.getKeyFrame(stateTime, true);
                case LEFT:
                    return avatarRunLeft.getKeyFrame(stateTime, true);
                case DOWN:
                    return avatarRunDown.getKeyFrame(stateTime, true);
                default:
                    return avatarRunRight.getKeyFrame(stateTime, true);
            }
        }
    }

    protected Direction getCurrentDirection() {
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
        return previousDirection;
    }

    protected void update() {
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
            Vector2 destination = new Vector2(WorldCamera.scaleToUnit(location.getPosX()),
                    WorldCamera.scaleToUnit(location.getPosY()));
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

    public void teleport() {
        ILocationView location = user.getLocation();
        if (location == null) {
            return;
        }
        Vector2 newPosition = new Vector2(WorldCamera.scaleToUnit(location.getPosX()),
                WorldCamera.scaleToUnit(location.getPosY()));
        body.setTransform(newPosition, body.getAngle());
        body.setAwake(true);
        currentDirection = location.getDirection();
        previousDirection = currentDirection;
    }

    public IUserView getUser() {
        return user;
    }

    public Body getBody() {
        return body;
    }

    public Vector2 getPosition() {
        return body.getPosition();
    }

    private class Head extends Table {

        private static final float COMMUNICABLE_USER_ICON_SIZE = 35;
        private static final float SPACING = 10;

        private final Image communicableIcon;

        public Head() {
            setTransform(true);
            this.communicableIcon = new Image();
            add(this.communicableIcon).size(COMMUNICABLE_USER_ICON_SIZE).row();
            UserInfoContainer userInfoContainer = new UserInfoContainer(user);
            add(userInfoContainer);
        }

        @Override
        public void draw(Batch batch, float delta) {
            if (Chati.CHATI.getWorldScreen().getWorldInputProcessor().isShowNamesPressed()
                    || Chati.CHATI.getPreferences().getShowNamesInWorld()) {
                setPosition(body.getPosition().x, body.getPosition().y + UserAvatar.this.getHeight() +
                        WorldCamera.scaleToUnit(SPACING));
                setScale(WorldCamera.scaleToUnit(Chati.CHATI.getWorldScreen().getCamera().zoom));
                if (user.canCommunicateWith()) {
                    communicableIcon.setDrawable(Chati.CHATI.getDrawable("communicable_avatar"));
                } else {
                    communicableIcon.setDrawable(null);
                }
                act(delta);
                super.draw(batch, 1);
            }
        }
    }
}
