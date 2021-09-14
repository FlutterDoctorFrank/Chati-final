package view2.world.component;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import controller.network.ServerSender;
import model.context.spatial.Direction;
import model.context.spatial.ISpatialContextView;
import model.user.IInternUserView;
import view2.Chati;
import view2.world.WorldCamera;
import view2.world.WorldScreen;
import java.util.Arrays;
import java.util.LinkedList;

/**
 * Eine Klasse, welche den Avatar des intern angemeldeten Benutzers repräsentiert.
 */
public class InternUserAvatar extends UserAvatar {

    private final LinkedList<Direction> currentDirectionalInputs;
    private Vector2 oldPosition;
    private boolean isTeleporting;
    private boolean isSprinting;
    private int interactCount;

    /**
     * Erzeugt eine neue Instanz des InternUserAvatar.
     */
    public InternUserAvatar() {
        super(Chati.CHATI.getInternUser());
        this.body.setUserData(ContactType.INTERN_USER);
        Fixture fixture = body.getFixtureList().get(0);
        if (fixture != null) {
            fixture.getFilterData().categoryBits = WorldScreen.INTERN_USER_BIT;
            fixture.getFilterData().maskBits = WorldScreen.COLLISION_AREA_BIT;
        }
        this.currentDirectionalInputs = new LinkedList<>();
        this.oldPosition = body.getPosition().cpy();
    }

    @Override
    protected void update() {
        if (user.isTeleporting()) {
            isTeleporting = true;
            teleport();
            return;
        }
        isTeleporting = false;

        if (!user.isMovable()) {
            return;
        }

        float velocity = DEFAULT_VELOCITY;
        if (Chati.CHATI.getPreferences().isAlwaysSprinting()
                || Chati.CHATI.getWorldScreen().getWorldInputProcessor().isSprintPressed()) {
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

    /**
     * Lässt den Benutzer mit einem Interaktionsobjekt in der Nähe seines Avatars interagieren.
     */
    public void interact() {
        if (interactCount > 0) {
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

    /**
     * Sendet die aktuelle Position des Avatars zum Server.
     */
    public void sendPosition() {
        if (user.getLocation() == null) {
            return;
        }
        if (!isTeleporting && user.isMovable() && (!oldPosition.epsilonEquals(getPosition())
                || currentDirection != user.getLocation().getDirection())) {
            Chati.CHATI.send(ServerSender.SendAction.AVATAR_MOVE, WorldCamera.scaleFromUnit(getPosition().x),
                    WorldCamera.scaleFromUnit(getPosition().y), isSprinting, currentDirection);
        }
    }

    /**
     * Erhöht oder verringert den Zähler der sich um den Avatar des Benutzers befindlichen Interaktionsobjekte.
     * @param increment Falls true, wird der Zähler erhöht, sonst verringert.
     */
    public void countInteractables(final boolean increment) {
        if (increment) {
            interactCount++;
        } else if (interactCount > 0) {
            interactCount--;
        }
    }

    /**
     * Gibt anhand der momentan gedrückten Tasten zurück, in welche Richtung der Avatar bewegt werden soll.
     * @return Richtung, in die der Avatar bewegt werden soll.
     */
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
}
