package model.communication;

import model.context.spatial.SpatialContext;
import model.user.User;

import java.util.Map;
import java.util.UUID;

/**
 * Eine abstrakte Klasse, welche das räumliche Ausmaÿ repräsentiert, über welches in einem räumlichen Kontext
 * kommuniziert werden kann.
 */
public abstract class CommunicationRegion {

    /** Kontext, der die Instanz dieser Kommunikationsform nutzt. */
    protected SpatialContext context;

    /**
     * Erzeugt eine neue Instanz der CommunicationRegion
     * @param context Kontext, der die Instanz dieser Kommunikationsform nutzt.
     */
    public CommunicationRegion(SpatialContext context) {
        this.context = context;
    }

    /**
     * Gibt die Menge aller Benutzer zurück, die eine Nachricht des kommunizierenden Benutzers empfangen können.
     * @param user Kommunizierender Benutzer.
     * @return Menge aller Benutzer, die eine Nachricht vom kommunizierenden Benutzer empfangen können.
     */
    public abstract Map<UUID, User> getCommunicableUsers(User user);
}
