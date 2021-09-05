package view2.world.component;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import controller.network.ServerSender;
import model.context.spatial.Direction;
import model.context.spatial.ISpatialContextView;
import model.user.IInternUserView;
import view2.Chati;
import view2.world.WorldCamera;
import view2.world.WorldScreen;

import java.util.*;

public class InternUserAvatar extends UserAvatar {

    private final LinkedList<Direction> currentDirectionalInputs;
    private Vector2 oldPosition;
    private boolean isTeleporting;
    private boolean isSprinting;
    private boolean canInteract;
    private boolean isColliding;

    public InternUserAvatar() {
        super(Chati.CHATI.getUserManager().getInternUserView());
        this.body.setUserData(ContactType.INTERN_USER);
        turnOnCollision();
        this.currentDirectionalInputs = new LinkedList<>();
        this.oldPosition = body.getPosition().cpy();
    }

    @Override
    public void update() {
        if (user.isTeleporting()) {
            isTeleporting = true;
            teleport();
            return;
        }
        isTeleporting = false;

        if (!user.isMovable()) {
            if (isColliding) {
                turnOffCollision();
            }
            return;
        } else if (!isColliding) {
            turnOnCollision();
        }

        float velocity = DEFAULT_VELOCITY;
        if (Chati.CHATI.getWorldScreen().getWorldInputProcessor().isSprintPressed()) {
            isSprinting = true;
            velocity *= SPRINT_VELOCITY_FACTOR;
        } else {
            isSprinting = false;
        }
        oldPosition = body.getPosition().cpy();
        currentDirection = getCurrentDirectionalInput();
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

    public void canInteract(boolean canInteract) {
        this.canInteract = canInteract;
    }

    public void interact() {
        if (canInteract) {
            IInternUserView internUser = Chati.CHATI.getInternUser();
            if (internUser == null) {
                return;
            }
            ISpatialContextView currentInteractable = internUser.getCurrentInteractable();
            if (currentInteractable == null) {
                return;
            }
            Chati.CHATI.send(ServerSender.SendAction.CONTEXT_INTERACT, currentInteractable.getContextId());
        }
    }

    public void sendPosition() {
        if (!isTeleporting && !oldPosition.epsilonEquals(getPosition())) {
            isTeleporting = false;
            Chati.CHATI.send(ServerSender.SendAction.AVATAR_MOVE,
                    getPosition().x * WorldCamera.PPM, getPosition().y * WorldCamera.PPM, isSprinting, currentDirection);
        }
    }

    private Direction getCurrentDirectionalInput() {
        Arrays.stream(Direction.values()).forEach(direction -> {
            if (!currentDirectionalInputs.contains(direction)
                    && Chati.CHATI.getWorldScreen().getWorldInputProcessor().isDirectionPressed(direction)) {
                currentDirectionalInputs.add(direction);
            }
            if (currentDirectionalInputs.contains(direction)
                    && !Chati.CHATI.getWorldScreen().getWorldInputProcessor().isDirectionPressed(direction)) {
                currentDirectionalInputs.remove(direction);
            }
        });
        return currentDirectionalInputs.peekLast();
    }

    private void turnOnCollision() {
        Fixture fixture = body.getFixtureList().get(0);
        if (fixture != null) {
            fixture.getFilterData().categoryBits = WorldScreen.INTERN_USER_BIT;
            fixture.getFilterData().maskBits = WorldScreen.COLLISION_AREA_BIT;
            this.isColliding = true;
        }
    }

    private void turnOffCollision() {
        Fixture fixture = body.getFixtureList().get(0);
        if (fixture != null) {
            fixture.getFilterData().categoryBits = WorldScreen.INTERN_USER_BIT;
            fixture.getFilterData().maskBits = 0;
            this.isColliding = false;
        }
    }
}
