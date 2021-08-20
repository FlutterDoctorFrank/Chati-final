package view2.component.world;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import controller.network.ServerSender;
import model.user.IInternUserView;
import view2.Chati;

import java.util.*;

public class InternUserAvatar extends UserAvatar {

    private final LinkedList<Direction> currentDirectionalInputs;
    private boolean canInteract;

    public InternUserAvatar(IInternUserView internUser, World world) {
        super(internUser, world);
        body.setUserData(BodyType.INTERN_USER);
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
        if (currentDirection != null) {
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
