package view2.component.world;

import com.badlogic.gdx.physics.box2d.*;
import view2.Chati;
import view2.component.world.body.BodyType;

public class WorldContactListener implements ContactListener {

    @Override
    public void beginContact(Contact contact) {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();
        if ((fixtureA.getBody().getUserData() == BodyType.INTERN_USER || fixtureB.getBody().getUserData() == BodyType.INTERN_USER)
            && (fixtureA.getBody().getUserData() == BodyType.INTERACTION_AREA
                || fixtureB.getBody().getUserData() == BodyType.INTERACTION_AREA)) {
            Chati.CHATI.getWorldScreen().getInternUserAvatar().setCanInteract(true);
        }
    }

    @Override
    public void endContact(Contact contact) {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();
        if ((fixtureA.getBody().getUserData() == BodyType.INTERN_USER || fixtureB.getBody().getUserData() == BodyType.INTERN_USER)
                && (fixtureA.getBody().getUserData() == BodyType.INTERACTION_AREA
                || fixtureB.getBody().getUserData() == BodyType.INTERACTION_AREA)) {
            Chati.CHATI.getWorldScreen().getInternUserAvatar().setCanInteract(false);
        }
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {
    }
}
