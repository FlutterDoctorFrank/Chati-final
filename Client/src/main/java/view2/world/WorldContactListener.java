package view2.world;

import com.badlogic.gdx.physics.box2d.*;
import view2.Chati;
import view2.world.component.ContactType;

public class WorldContactListener implements ContactListener {

    @Override
    public void beginContact(Contact contact) {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();
        if ((fixtureA.getBody().getUserData() == ContactType.INTERN_USER
                || fixtureB.getBody().getUserData() == ContactType.INTERN_USER)
            && (fixtureA.getBody().getUserData() == ContactType.INTERACTION_AREA
                || fixtureB.getBody().getUserData() == ContactType.INTERACTION_AREA)) {
            Chati.CHATI.getWorldScreen().getInternUserAvatar().canInteract(true);
        }
    }

    @Override
    public void endContact(Contact contact) {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();
        if ((fixtureA.getBody().getUserData() == ContactType.INTERN_USER
                || fixtureB.getBody().getUserData() == ContactType.INTERN_USER)
                && (fixtureA.getBody().getUserData() == ContactType.INTERACTION_AREA
                || fixtureB.getBody().getUserData() == ContactType.INTERACTION_AREA)) {
            if (Chati.CHATI.getWorldScreen().getCurrentInteractableWindow() == null) {
                return;
            }
            Chati.CHATI.getWorldScreen().getInternUserAvatar().canInteract(false);
        }
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {
    }
}
