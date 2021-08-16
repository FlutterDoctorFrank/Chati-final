package view.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.*;

public class WorldContactListener implements ContactListener {
    private boolean canInteract = false;

    @Override
    public void beginContact(Contact contact) {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();
        if (isInteractiveArea(fixtureA, fixtureB) && isUserAvatar(fixtureA, fixtureB)) {
            canInteract = true;
        }
    }

    @Override
    public void endContact(Contact contact) {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();
        if (isInteractiveArea(fixtureA, fixtureB) && isUserAvatar(fixtureA, fixtureB)) {
            canInteract = false;
        }
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }

    private boolean isInteractiveArea(Fixture fixA, Fixture fixB) {
        if (fixA.getUserData() == "interactive-area" || fixB.getUserData() == "interactive-area") {
            return true;
        }
        return false;
    }

    private boolean isUserAvatar(Fixture fixA, Fixture fixB) {
        if (fixA.getUserData() == "user-avatar" || fixB.getUserData() == "user-avatar") {
            return true;
        }
        return false;
    }

    public boolean canInteract() {
        return canInteract;
    }
}
