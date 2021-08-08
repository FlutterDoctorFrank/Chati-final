package view.Sprites;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import org.junit.Test;
import view.Chati;

import java.util.UUID;

public class Avatar extends Sprite {
    private World world;
    private Body body;
    private BodyDef bdef;
    private FixtureDef fdef;
    private UUID user;

    private TextureAtlas atlas;
    private TextureRegion avatarStandDown;
    private TextureRegion avatarStandUp;
    private TextureRegion avatarStandLeft;
    private TextureRegion avatarStandRight;
    private Animation avatarRunUp;
    private Animation avatarRunDown;
    private Animation avatarRunLeft;
    private Animation avatarRunRight;
    private Animation avatarDance;

    private enum Direction {LEFT, RIGHT, UP, DOWN, STAND}
    private Direction currentDirection;
    private Direction previousDirection;
    private float stateTimer;

    private static final short MARIO_BIT = 0;

    public Avatar(World world, Vector2 position) {
        this.bdef = new BodyDef();
        this.fdef = new FixtureDef();
        this.world = world;

        bdef = new BodyDef();
        bdef.position.set(position);
        bdef.type = BodyDef.BodyType.DynamicBody;
        body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(1.5f, 1.5f);
        fdef.filter.categoryBits = Chati.AVATAR_BIT;
        fdef.filter.maskBits = Chati.OBJECT_BIT | Chati.DEFAULT_BIT;
        fdef.shape = shape;
        body.createFixture(fdef);

        stateTimer = 0;
        atlas = new TextureAtlas("skins/Adam/Adam.pack");

        TextureAtlas.AtlasRegion stand = atlas.findRegion("Adam_idle");
        avatarStandRight = new TextureRegion(stand, 0, 0, 32, 48);
        avatarStandUp = new TextureRegion(stand, 32, 0, 32, 48);
        avatarStandLeft = new TextureRegion(stand, 64, 0, 32, 48);
        avatarStandDown = new TextureRegion(atlas.findRegion("Adam_idle"), 96, 0, 32, 48);
        setBounds(0, 0, 32 / Chati.PPM, 32 / Chati.PPM);
        setRegion(avatarStandDown);
        currentDirection = Direction.DOWN;
        previousDirection = Direction.DOWN;

        TextureAtlas.AtlasRegion run = atlas.findRegion("Adam_run");
        Array<TextureRegion> frames = new Array<TextureRegion>();

        //running right animation
        for (int i = 0; i < 6; i++) {
            frames.add(new TextureRegion(run, i * 32, 0, 32, 48));
        }
        avatarRunRight = new Animation(0.1f, frames);
        frames.clear();

        //running up animation
        for (int i = 6; i < 12; i++) {
            frames.add(new TextureRegion(run, i * 32, 0, 32, 48));
        }
        avatarRunUp = new Animation(0.1f, frames);
        frames.clear();

        //running left animation
        for (int i = 12; i < 18; i++) {
            frames.add(new TextureRegion(run, i * 32, 0, 32, 48));
        }
        avatarRunLeft = new Animation(0.1f, frames);
        frames.clear();

        //running down animation
        for (int i = 18; i < 24; i++) {
            frames.add(new TextureRegion(run, i * 32, 0, 32, 48));
        }
        avatarRunDown = new Animation(0.1f, frames);
        frames.clear();

        //dance animation
        for (int i = 0; i < 5; i++) {
            frames.add(new TextureRegion(atlas.findRegion("Adam_idle"), i * 32, 0, 32, 48));
        }
        avatarDance = new Animation(1f, frames);
        frames.clear();
    }

    public Avatar(World world, String avatarPath, UUID id, int posX, int posY) {
        this.bdef = new BodyDef();
        this.fdef = new FixtureDef();
        this.world = world;
        this.user = id;

        bdef = new BodyDef();
        bdef.position.set(posX, posY);
        bdef.type = BodyDef.BodyType.DynamicBody;
        body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(1, 1);
        fdef.shape = shape;
        body.createFixture(fdef);

        atlas = new TextureAtlas("skins/Adam/Adam.pack");

        avatarStandDown = new TextureRegion(new Texture("skins/Adam_32x32.png"), 864, 32, 32, 32);
        setBounds(0, 0, 32 / Chati.PPM, 32 / Chati.PPM);
        setRegion(avatarStandDown);
    }

    public void update(float dt) {
        setPosition(getBody().getPosition().x - getWidth() / 2, getBody().getPosition().y - getHeight() / 2);
        setRegion(getFrame(dt));
    }

    private TextureRegion getFrame(float dt) {
        currentDirection = getDirection();

        TextureRegion region;
        switch (currentDirection) {
            case UP:
                region = (TextureRegion) avatarRunUp.getKeyFrame(stateTimer, true);
                break;
            case DOWN:
                region = (TextureRegion) avatarRunDown.getKeyFrame(stateTimer, true);
                break;
            case LEFT:
                region = (TextureRegion) avatarRunLeft.getKeyFrame(stateTimer, true);
                break;
            case RIGHT:
                region = (TextureRegion) avatarRunRight.getKeyFrame(stateTimer, true);
                break;
            default:
                if (previousDirection.equals(Direction.UP)) {
                    region = avatarStandUp;
                } else if (previousDirection.equals(Direction.DOWN)) {
                    region = avatarStandDown;
                } else if (previousDirection.equals(Direction.LEFT)) {
                    region = avatarStandLeft;
                } else {
                    region = avatarStandRight;
                }
        }

        stateTimer = currentDirection == previousDirection ? stateTimer + dt : 0;
        if (!currentDirection.equals(Direction.STAND)) {
            previousDirection = currentDirection;
        }

        return region;
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
            return Direction.STAND;
    }


    public Body getBody() {
        return body;
    }

    public BodyDef getBdef() {
        return bdef;
    }

    public UUID getUser() {
        return user;
    }
}
