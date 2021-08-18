package model.communication;

import model.user.User;

import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Eine Klasse, welche eine radius-basierte Kommunikation repräsentiert.
 */
public class RadiusCommunication extends CommunicationRegion {

    /** Standardmäßiger Radius, innerhalb dem Benutzer kommunizieren können. */
    private static final int DEFAULT_RADIUS = 2;

    /** Radius, innerhalb dem Benutzer kommunizieren können.*/
    private final int radius;

    /**
     * Erzeugt eine neue Instanz der RadiusCommunication.
     */
    public RadiusCommunication() {
        super();
        this.radius = DEFAULT_RADIUS;
    }

    @Override
    public Map<UUID, User> getCommunicableUsers(User user) {
        return area.getUsers().values().stream()
                .filter(otherUser -> user.getLocation().distance(otherUser.getLocation()) <= radius)
                .collect(Collectors.toUnmodifiableMap(User::getUserId, Function.identity()));
    }

}