package view2.component.world.body;

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import controller.network.ServerSender;
import model.context.ContextID;
import model.context.spatial.ISpatialContextView;
import view2.Chati;
import view2.component.KeyAction;
import view2.component.world.WorldCamera;
import view2.component.world.WorldScreen;

import java.util.*;

public class InternUserAvatar extends UserAvatar {

    private final LinkedList<Direction> currentDirectionalInputs;
    private Vector2 oldPosition;
    private boolean isSprinting;
    private boolean canInteract;

    public InternUserAvatar() {
        super(Chati.CHATI.getUserManager().getInternUserView());
        body.setUserData(BodyType.INTERN_USER);
        body.destroyFixture(body.getFixtureList().get(0));
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.filter.categoryBits = WorldScreen.INTERN_USER_BIT;
        fixtureDef.filter.maskBits = WorldScreen.OBJECT_BIT | WorldScreen.BORDER_BIT;
        PolygonShape shape = new PolygonShape();
        shape.setAsBox((32 - 1) / WorldCamera.PPM / 2, (32 - 1) / WorldCamera.PPM / 2); // TODO auf 32 ändern
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
        if (Chati.CHATI.getWorldScreen().getWorldInputProcessor().isSprintPressed()) {
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

    public void canInteract(boolean canInteract) {
        this.canInteract = canInteract;
    }

    public void interact() {
        if (canInteract) {
            float interactionDistance = getWidth();
            Vector2 positionInLookingDirection;
            if (body.getAngle() == Math.PI) {
                positionInLookingDirection = new Vector2(body.getPosition().x, body.getPosition().y + interactionDistance);
            } else if (body.getAngle() == Math.PI) {
                positionInLookingDirection = new Vector2(body.getPosition().x - interactionDistance, body.getPosition().y);
            } else if (body.getAngle() == 0) {
                positionInLookingDirection = new Vector2(body.getPosition().x + interactionDistance, body.getPosition().y);
            } else {
                positionInLookingDirection = new Vector2(body.getPosition().x, body.getPosition().y + interactionDistance);
            }
            ISpatialContextView context = Chati.CHATI.getUserManager().getInternUserView().getCurrentRoom()
                    .getArea(positionInLookingDirection.x * WorldCamera.PPM, positionInLookingDirection.y * WorldCamera.PPM);
            if (context != null) {
                Chati.CHATI.getServerSender().send(ServerSender.SendAction.CONTEXT_INTERACT, context.getContextId());
            }
        }
    }

    public void sendPosition() {
        if (!oldPosition.epsilonEquals(getPosition())) {
            Chati.CHATI.getServerSender().send(ServerSender.SendAction.AVATAR_MOVE,
                    getPosition().x * WorldCamera.PPM, getPosition().y * WorldCamera.PPM, isSprinting);
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
}
