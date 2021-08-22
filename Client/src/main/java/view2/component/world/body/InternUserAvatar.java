package view2.component.world.body;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import controller.network.ServerSender;
import model.user.IInternUserView;
import view2.Chati;
import view2.component.world.WorldScreen;

import java.util.*;

public class InternUserAvatar extends UserAvatar {

    private final LinkedList<Direction> currentDirectionalInputs;
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
        currentDirectionalInputs = new LinkedList<>();
    }

    public void move() {
        Direction currentDirection = getCurrentDirectionalInput();
        float velocity = DEFAULT_VELOCITY;
        if (WorldScreen.getInstance().getWorldInputProcessor().isSprintPressed()) {
            velocity *= SPRINT_SPEED_FACTOR;
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
        if (body.getLinearVelocity().x != 0 || body.getLinearVelocity().y != 0) {
            Vector2 newPosition = body.getPosition();
            Chati.getInstance().getServerSender().send(ServerSender.SendAction.AVATAR_MOVE,
                    newPosition.x * WorldScreen.PPM, newPosition.y * WorldScreen.PPM);
        }
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
