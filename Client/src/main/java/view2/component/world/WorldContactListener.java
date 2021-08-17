package view2.component.world;

import com.badlogic.gdx.physics.box2d.*;

public class WorldContactListener implements ContactListener {

    @Override
    public void beginContact(Contact contact) {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();
        if (isInteractionObject(fixtureA, fixtureB) && isUserAvatar(fixtureA, fixtureB)) {
            WorldScreen.getInstance().getInternUserAvatar().setCanInteract(true);
        }
    }

    @Override
    public void endContact(Contact contact) {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();
        if (isInteractionObject(fixtureA, fixtureB) && isUserAvatar(fixtureA, fixtureB)) {
            WorldScreen.getInstance().getInternUserAvatar().setCanInteract(false);
        }
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }

    private boolean isInteractionObject(Fixture fixA, Fixture fixB) {
        return fixA.getUserData() == "interaction-object" || fixB.getUserData() == "interaction-object";
    }

    private boolean isUserAvatar(Fixture fixA, Fixture fixB) {
        return fixA.getUserData() == "intern-user-avatar" || fixB.getUserData() == "intern-user-avatar";
    }
}
