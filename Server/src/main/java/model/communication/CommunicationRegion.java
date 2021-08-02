package model.communication;

import model.context.spatial.Area;
import model.user.User;

import java.util.Map;
import java.util.UUID;

/**
 * Eine abstrakte Klasse, welche das räumliche Ausmaÿ repräsentiert, über welches in einem räumlichen Kontext
 * kommuniziert werden kann.
 */
public abstract class CommunicationRegion {

    /** Kontext, der die Instanz dieser Kommunikationsform nutzt. */
    protected Area area;

    /**
     * Gibt die Menge aller Benutzer zurück, die eine Nachricht des kommunizierenden Benutzers empfangen können.
     * @param user Kommunizierender Benutzer.
     * @return Menge aller Benutzer, die eine Nachricht vom kommunizierenden Benutzer empfangen können.
     */
    public abstract Map<UUID, User> getCommunicableUsers(User user);

    /**
     * Setzt den Kontext, der diese Kommunikationsform nutzt.
     * @param area Kontext, der diese Kommunikationsform nutzt.
     */
    public void setArea(Area area) {
        this.area = area;
    }
}
