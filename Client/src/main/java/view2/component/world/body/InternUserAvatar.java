package view2.component.world.body;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import model.user.IInternUserView;
import view2.component.world.WorldScreen;

import java.util.*;

public class InternUserAvatar extends UserAvatar {

    private final LinkedList<Direction> currentDirectionalInputs;
    private Vector2 oldPosition;
    private boolean isSprinting;
    private boolean canInteract;

    public InternUserAvatar(IInternUserView internUser, World world) {
        super(internUser, world);
        body.setUserData(BodyType.INTERN_USER);
        body.destroyFixture(body.getFixtureList().get(0));
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.filter.categoryBits = WorldScreen.INTERN_USER_BIT;
        fixtureDef.filter.maskBits = WorldScreen.OBJECT_BIT | WorldScreen.BORDER_BIT;
        PolygonShape shape = new PolygonShape();
        shape.setAsBox((32 - 1) / WorldScreen.PPM / 2, (32 - 1) / WorldScreen.PPM / 2);
        fixtureDef.shape = shape;
        body.createFixture(fixtureDef);

        this.currentDirectionalInputs = new LinkedList<>();
        this.oldPosition = body.getPosition().cpy();
    }

    @Override
    public void update() {
        if (user.isTeleporting()) {
            teleport();
            return;
        }
        oldPosition = body.getPosition().cpy();
        Direction currentDirection = getCurrentDirectionalInput();
        float velocity = DEFAULT_VELOCITY;
        if (WorldScreen.getInstance().getWorldInputProcessor().isSprintPressed()) {
            isSprinting = true;
            velocity *= SPRINT_VELOCITY_FACTOR;
        } else {
            isSprinting = false;
        }
        if (currentDirection == null) {
            body.setLinearVelocity(0, 0);
        } else {
            switch (currentDirection) {
                case UP:
                    body.setLinearVelocity(0, velocity);
                    break;
                case LEFT:
                    body.setLinearVelocity(-velocity, 0);
                    break;
                case DOWN:
                    body.setLinearVelocity(0, -velocity);
                    break;
                case RIGHT:
                    body.setLinearVelocity(velocity, 0);
                    break;
                default:
                    body.setLinearVelocity(0, 0);
            }
        }
    }

    public Vector2 getOldPosition() {
        return oldPosition;
    }

    public boolean isSprinting() {
        return isSprinting;
    }

    private Direction getCurrentDirectionalInput() {
        Arrays.stream(Direction.values()).forEach(direction -> {
            if (!currentDirectionalInputs.contains(direction) && direction.isPressed()) {
                currentDirectionalInputs.add(direction);
            }
            if (currentDirectionalInputs.contains(direction) && !direction.isPressed()) {
                currentDirectionalInputs.remove(direction);
            }
        });
        return currentDirectionalInputs.peekLast();
    }

    public void setCanInteract(boolean canInteract) {
        this.canInteract = canInteract;
    }

    public void interact() {
        if (canInteract) {
            System.out.println("penis");
        }
    }
}
