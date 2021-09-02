package view2.component.world.body;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import controller.network.ServerSender;
import model.context.spatial.Direction;
import model.context.spatial.ISpatialContextView;
import model.user.IInternUserView;
import view2.Chati;
import view2.component.world.WorldCamera;
import view2.component.world.WorldScreen;

import java.util.*;

public class InternUserAvatar extends UserAvatar {

    private final LinkedList<Direction> currentDirectionalInputs;
    private Vector2 oldPosition;
    private boolean isTeleporting;
    private boolean isSprinting;
    private boolean canInteract;

    public InternUserAvatar() {
        super(Chati.CHATI.getUserManager().getInternUserView());
        body.setUserData(BodyType.INTERN_USER);
        body.destroyFixture(body.getFixtureList().get(0));
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.filter.categoryBits = WorldScreen.INTERN_USER_BIT;
        fixtureDef.filter.maskBits = WorldScreen.BORDER_BIT;
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
            isTeleporting = true;
            teleport();
            return;
        }
        isTeleporting = false;
        oldPosition = body.getPosition().cpy();
        currentDirection = getCurrentDirectionalInput();
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
            IInternUserView internUser = Chati.CHATI.getUserManager().getInternUserView();
            if (internUser == null) {
                return;
            }
            Set<ISpatialContextView> currentInteractables = new HashSet<>(internUser.getCurrentInteractables().values());
            // Falls sich um den Benutzer mehrere Kontexte befinden, mit denen interagiert werden kann, entscheide
            // anhand der Blickrichtung, mit welchem Kontext interagiert werden soll.
            ISpatialContextView interactingContext = null;
            if (currentInteractables.isEmpty()) {
                return;
            } else if (currentInteractables.size() == 1) {
                interactingContext = currentInteractables.iterator().next();
            } else {
                if (internUser.getLocation() == null) {
                    return;
                }

                if (previousDirection == Direction.UP) {
                    interactingContext = currentInteractables.stream()
                            .filter(interactable -> interactable.getCenter().getPosY() > internUser.getLocation().getPosY())
                            .iterator().next();
                } else if (previousDirection == Direction.LEFT) {
                    interactingContext = currentInteractables.stream()
                            .filter(interactable -> interactable.getCenter().getPosX() < internUser.getLocation().getPosX())
                            .iterator().next();
                } else if (previousDirection == Direction.DOWN) {
                    interactingContext = currentInteractables.stream()
                            .filter(interactable -> interactable.getCenter().getPosY() < internUser.getLocation().getPosY())
                            .iterator().next();
                } else if (previousDirection == Direction.RIGHT) {
                    interactingContext = currentInteractables.stream()
                            .filter(interactable -> interactable.getCenter().getPosX() > internUser.getLocation().getPosX())
                            .iterator().next();
                }
            }
            if (interactingContext == null) {
                return;
            }
            Chati.CHATI.getServerSender().send(ServerSender.SendAction.CONTEXT_INTERACT, interactingContext.getContextId());
        }
    }

    public void sendPosition() {
        if (!isTeleporting && !oldPosition.epsilonEquals(getPosition())) {
            isTeleporting = false;
            Chati.CHATI.getServerSender().send(ServerSender.SendAction.AVATAR_MOVE,
                    getPosition().x * WorldCamera.PPM, getPosition().y * WorldCamera.PPM, isSprinting, currentDirection);
        }
    }

    protected Direction getCurrentDirectionalInput() {
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
}
