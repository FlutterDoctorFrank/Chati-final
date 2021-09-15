package model.communication;

import model.context.spatial.Area;
import model.user.User;
import org.jetbrains.annotations.NotNull;
import java.util.Map;
import java.util.UUID;

/**
 * Eine abstrakte Klasse, welche das räumliche Ausmaß repräsentiert, über welches in einem räumlichen Kontext
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
    public abstract @NotNull Map<UUID, User> getCommunicableUsers(@NotNull final User user);

    /**
     * Setzt den Kontext, der diese Kommunikationsform nutzt.
     * @param area Kontext, der diese Kommunikationsform nutzt.
     */
    public void setArea(@NotNull final Area area) {
        this.area = area;
    }
}