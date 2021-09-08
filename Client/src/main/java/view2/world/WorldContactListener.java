package view2.world;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import org.jetbrains.annotations.NotNull;
import view2.Chati;
import view2.world.component.ContactType;

public class WorldContactListener implements ContactListener {

    @Override
    public void beginContact(@NotNull final Contact contact) {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();

        if (isInternAvatar(fixtureA, fixtureB) && isInteractionArea(fixtureA, fixtureB)) {
            Chati.CHATI.getWorldScreen().getInternUserAvatar().canInteract(true);
        }
    }

    @Override
    public void endContact(@NotNull final Contact contact) {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();

        if (isInternAvatar(fixtureA, fixtureB) && isInteractionArea(fixtureA, fixtureB)) {
            Chati.CHATI.getWorldScreen().getInternUserAvatar().canInteract(false);
        }
    }

    @Override
    public void preSolve(@NotNull final Contact contact, @NotNull final Manifold oldManifold) {
        if (isInternAvatar(contact.getFixtureA(), contact.getFixtureB())) {
            if (!Chati.CHATI.getWorldScreen().getInternUserAvatar().getUser().isMovable()) {
                contact.setEnabled(false);
            }
        }
    }

    @Override
    public void postSolve(@NotNull final Contact contact, @NotNull final ContactImpulse impulse) {

    }

    private boolean isInternAvatar(@NotNull final Fixture first, @NotNull final Fixture second) {
        return first.getBody().getUserData() == ContactType.INTERN_USER
                || second.getBody().getUserData() == ContactType.INTERN_USER;
    }

    public boolean isInteractionArea(@NotNull final Fixture first, @NotNull final Fixture second) {
        return first.getBody().getUserData() == ContactType.INTERACTION_AREA
                || second.getBody().getUserData() == ContactType.INTERACTION_AREA;
    }
}
