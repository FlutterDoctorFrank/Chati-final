package view.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.*;

public class WorldContactListener implements ContactListener {
    @Override
    public void beginContact(Contact contact) {
        if (isInteractiveArea(contact.getFixtureA(), contact.getFixtureB())) {
            Gdx.app.log("Begin", "");
        }
    }

    @Override
    public void endContact(Contact contact) {
        if (isInteractiveArea(contact.getFixtureA(), contact.getFixtureB())) {
            Gdx.app.log("End", "");
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
}
