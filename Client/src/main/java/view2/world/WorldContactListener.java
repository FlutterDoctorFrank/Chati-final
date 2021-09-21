package view2.world;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import org.jetbrains.annotations.NotNull;
import view2.Chati;
import view2.world.component.ContactType;

/**
 * Eine Klasse, welche einen ContactListener repräsentiert, der den Kontakt zwischen dem Benutzer und einem
 * Interaktionsobjekt verarbeitet.
 */
public class WorldContactListener implements ContactListener {

    @Override
    public void beginContact(@NotNull final Contact contact) {
        if (Chati.CHATI.getWorldScreen().getInternUserAvatar() == null) {
            return;
        }

        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();

        if (isInternUserAvatar(fixtureA, fixtureB) && isInteractionArea(fixtureA, fixtureB)) {
            Chati.CHATI.getWorldScreen().getInternUserAvatar().countInteractables(true);
        }
    }

    @Override
    public void endContact(@NotNull final Contact contact) {
        if (Chati.CHATI.getWorldScreen().getInternUserAvatar() == null) {
            return;
        }

        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();

        if (isInternUserAvatar(fixtureA, fixtureB) && isInteractionArea(fixtureA, fixtureB)) {
            Chati.CHATI.getWorldScreen().getInternUserAvatar().countInteractables(false);
        }
    }

    @Override
    public void preSolve(@NotNull final Contact contact, @NotNull final Manifold oldManifold) {
        if (Chati.CHATI.getWorldScreen().getInternUserAvatar() == null) {
            return;
        }

        if (isInternUserAvatar(contact.getFixtureA(), contact.getFixtureB())) {
            if (!Chati.CHATI.getWorldScreen().getInternUserAvatar().getUser().isMovable()) {
                contact.setEnabled(false);
            }
        }
    }

    @Override
    public void postSolve(@NotNull final Contact contact, @NotNull final ContactImpulse impulse) {
    }

    /**
     * Gibt zurück, ob eine der beiden Kontaktflächen zu dem intern angemeldeten Benutzer gehört.
     * @param fixtureA Erste Kontaktfläche.
     * @param fixtureB Zweite Kontaktfläche.
     * @return true, wenn eine der Flächen zu dem Benutzer gehört, sonst false.
     */
    private boolean isInternUserAvatar(@NotNull final Fixture fixtureA, @NotNull final Fixture fixtureB) {
        return fixtureA.getBody().getUserData() == ContactType.INTERN_USER
                || fixtureB.getBody().getUserData() == ContactType.INTERN_USER;
    }

    /**
     * Gibt zurück, ob eine der beiden Kontaktflächen zu einem Interaktionsobjekt gehört.
     * @param fixtureA Erste Kontaktfläche.
     * @param fixtureB Zweite Kontaktfläche.
     * @return true, wenn eine der Flächen zu einem Interaktionsobjekt gehört, sonst false.
     */
    private boolean isInteractionArea(@NotNull final Fixture fixtureA, @NotNull final Fixture fixtureB) {
        return fixtureA.getBody().getUserData() == ContactType.INTERACTION_AREA
                || fixtureB.getBody().getUserData() == ContactType.INTERACTION_AREA;
    }
}
