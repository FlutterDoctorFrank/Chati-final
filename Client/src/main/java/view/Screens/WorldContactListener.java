package view.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.*;

public class WorldContactListener implements ContactListener {
    private boolean canInteract = false;

    @Override
    public void beginContact(Contact contact) {
        if (isInteractiveArea(contact.getFixtureA(), contact.getFixtureB())) {
            Gdx.app.log("Begin", "");
            canInteract = true;
        }
    }

    @Override
    public void endContact(Contact contact) {
        if (isInteractiveArea(contact.getFixtureA(), contact.getFixtureB())) {
            Gdx.app.log("End", "");
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

    public boolean canInteract() {
        return canInteract;
    }
}
