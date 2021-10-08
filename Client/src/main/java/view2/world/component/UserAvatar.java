package view2.world.component;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import model.context.spatial.Direction;
import model.context.spatial.ILocationView;
import model.user.Avatar;
import model.user.IUserView;
import org.jetbrains.annotations.NotNull;
import view2.Chati;
import view2.userInterface.UserInfoContainer;
import view2.world.WorldCamera;
import view2.world.WorldScreen;

/**
 * Eine Klasse, welche den Avatar eines Benutzers repräsentiert.
 */
public class UserAvatar extends Sprite {

    /** Werden zur Anzeige eines Farbigen Rahmens um den Avatar verwendet, wenn der entsprechende Benutzer
     * gerade spricht. */
    private static final String VERTEX_SHADER = "attribute vec4 " + ShaderProgram.POSITION_ATTRIBUTE + ";\n"
            + "attribute vec4 " + ShaderProgram.COLOR_ATTRIBUTE + ";\n"
            + "attribute vec2 " + ShaderProgram.TEXCOORD_ATTRIBUTE + "0;\n" + "uniform mat4 u_projTrans;\n"
            + "varying vec4 v_color;\n" + "varying vec2 v_texCoords;\n" + "\n" + "void main()\n" + "{\n"
            + "   v_color = " + ShaderProgram.COLOR_ATTRIBUTE + ";\n"
            + "   v_texCoords = " + ShaderProgram.TEXCOORD_ATTRIBUTE + "0;\n"
            + "   gl_Position =  u_projTrans * " + ShaderProgram.POSITION_ATTRIBUTE + ";\n" + "}\n";

    private static final String FRAGMENT_SHADER = "#ifdef GL_ES\n"
            + "#define LOWP lowp\n" + "precision mediump float;\n" + "#else\n" + "#define LOWP \n" + "#endif\n"
            + "varying LOWP vec4 v_color;\n" + "varying vec2 v_texCoords;\n" + "uniform sampler2D u_texture;\n"
            + "void main()\n" + "{\n" + "  gl_FragColor = v_color * texture2D(u_texture, v_texCoords).a;\n" + "}";

    public static final float DEFAULT_VELOCITY = 100f;
    public static final float SPRINT_VELOCITY_FACTOR = 1.67f;
    protected static final float AVATAR_SIZE = 32;
    protected static final float TALK_BORDER_SIZE = 3.2f;
    protected static final float FRAME_DURATION = 0.15f;

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
    private final ShaderProgram shaderProgram;
    private final float alpha;

    protected Direction currentDirection;
    protected Direction previousDirection;
    private float stateTime;

    /**
     * Erzeugt eine neue Instanz des UserAvatar.
     * @param user Zu dem Avatar zugehöriger Benutzer.
     */
    public UserAvatar(@NotNull final IUserView user) {
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
        this.body.createFixture(fixtureDef);
        this.body.setUserData(ContactType.USER);

        this.shaderProgram = new ShaderProgram(VERTEX_SHADER, FRAGMENT_SHADER);

        Avatar avatar = user.getAvatar();
        this.alpha = avatar.isGhost() ? 0.67f : 1;
        this.avatarStandUp = Chati.CHATI.getDrawable(avatar.getPath() + "_stand_up").getRegion();
        this.avatarStandLeft = Chati.CHATI.getDrawable(avatar.getPath() + "_stand_left").getRegion();
        this.avatarStandDown = Chati.CHATI.getDrawable(avatar.getPath() + "_stand_down").getRegion();
        this.avatarStandRight = Chati.CHATI.getDrawable(avatar.getPath() + "_stand_right").getRegion();
        this.avatarRunUp = new Animation<>(FRAME_DURATION, Chati.CHATI.getRegions(avatar.getPath() + "_move_up"));
        this.avatarRunLeft = new Animation<>(FRAME_DURATION, Chati.CHATI.getRegions(avatar.getPath() + "_move_left"));
        this.avatarRunDown = new Animation<>(FRAME_DURATION, Chati.CHATI.getRegions(avatar.getPath() + "_move_down"));
        this.avatarRunRight = new Animation<>(FRAME_DURATION, Chati.CHATI.getRegions(avatar.getPath() + "_move_right"));
        setSize(WorldCamera.scaleToUnit(AVATAR_SIZE), WorldCamera.scaleToUnit(AVATAR_SIZE));
        setRegion(avatarStandDown);
        this.stateTime = 0;

        this.currentDirection = Direction.DOWN;
        this.previousDirection = Direction.DOWN;
    }

    @Override
    public void draw(@NotNull final Batch batch, final float alpha) {
        update();
        setRegion(getKeyFrame());

        if (Chati.CHATI.getAudioManager().isTalking(user)) {
            setSize(WorldCamera.scaleToUnit(AVATAR_SIZE + TALK_BORDER_SIZE),
                    WorldCamera.scaleToUnit(AVATAR_SIZE + TALK_BORDER_SIZE));
            setPosition(getPosition().x - getWidth() / 2, getPosition().y - getHeight() / 2);
            setColor(Color.GREEN);
            batch.setShader(shaderProgram);
            super.draw(batch, this.alpha);
        }

        setSize(WorldCamera.scaleToUnit(AVATAR_SIZE), WorldCamera.scaleToUnit(AVATAR_SIZE));
        setPosition(getPosition().x - getWidth() / 2, getPosition().y - getHeight() / 2);
        setColor(Color.WHITE);
        batch.setShader(null);
        super.draw(batch, this.alpha);
    }

    /**
     * Zeichnet den Container mit den Benutzerinformationen über dem Avatar,
     * @param batch Verwendeter Batch.
     */
    public void drawHead(@NotNull final Batch batch) {
        head.draw(batch, 1);
    }

    /**
     * Gibt die momentan anzuzeigende Textur zurück.
     * @return Anzuzeigende Textur.
     */
    private @NotNull TextureRegion getKeyFrame() {
        currentDirection = getCurrentDirection();
        stateTime = currentDirection == previousDirection ? stateTime + Gdx.graphics.getDeltaTime() : 0;
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

    /**
     * Bestimmt die momentane Richtung des Avatars anhand der auf ihn einwirkenden Kräfte.
     * @return Richtung des Avatars.
     */
    protected @NotNull Direction getCurrentDirection() {
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

    /**
     * Aktualisiert den Avatar durch ein Teleportieren oder Hinbewegen zu der im Modell hinterlegten Benutzerposition.
     */
    protected void update() {
        ILocationView location = user.getLocation();
        if (location == null) {
            return;
        }
        if (user.isTeleporting()) {
            teleport();
        } else {
            float velocity = WorldCamera.scaleToUnit(DEFAULT_VELOCITY);
            if (user.isSprinting()) {
                velocity *= SPRINT_VELOCITY_FACTOR;
                setFrameDuration(FRAME_DURATION / SPRINT_VELOCITY_FACTOR);
            } else {
                setFrameDuration(FRAME_DURATION);
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

    /**
     * Teleportiert den Avatar zu der im Modell hinterlegten Benutzerposition.
     */
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

    /**
     * Gibt den zugehörigen Benutzer zurück.
     * @return Zugehöriger Benutzer.
     */
    public @NotNull IUserView getUser() {
        return user;
    }

    /**
     * Gibt den Body des Avatars zurück.
     * @return Body
     */
    public @NotNull Body getBody() {
        return body;
    }

    /**
     * Gibt die Position des Avatars zurück.
     * @return Position
     */
    public @NotNull Vector2 getPosition() {
        return body.getPosition();
    }

    /**
     * Setzt die Zeitspanne, in der jeweils ein Frame einer Bewegungsanimation angezeigt wird.
     * @param frameDuration Dauer, in der ein Frame einer Bewegungsanimation angezeigt wird.
     */
    protected void setFrameDuration(float frameDuration) {
        avatarRunUp.setFrameDuration(frameDuration);
        avatarRunLeft.setFrameDuration(frameDuration);
        avatarRunDown.setFrameDuration(frameDuration);
        avatarRunRight.setFrameDuration(frameDuration);
    }

    /**
     * Eine Klasse, welche die über dem Avatar anzuzeigenden Benutzerinformationen repräsentiert.
     */
    private class Head extends Table {

        private static final float COMMUNICABLE_USER_ICON_SIZE = 35;
        private static final float SPACING = 10;

        private final Image communicableIcon;

        /**
         * Erzeugt eine neue Instanz des Head.
         */
        public Head() {
            setTransform(true);
            this.communicableIcon = new Image();
            add(this.communicableIcon).size(COMMUNICABLE_USER_ICON_SIZE).row();
            UserInfoContainer userInfoContainer = new UserInfoContainer(user);
            add(userInfoContainer);
        }

        @Override
        public void draw(@NotNull final Batch batch, float parentAlpha) {
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
                act(Gdx.graphics.getDeltaTime());
                super.draw(batch, parentAlpha);
            }
        }
    }
}
