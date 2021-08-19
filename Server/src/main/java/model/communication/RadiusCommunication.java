package model.communication;

import model.user.User;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Eine Klasse, welche eine radius-basierte Kommunikation repräsentiert.
 */
public class RadiusCommunication extends CommunicationRegion {

    /** Standardmäßiger Radius, innerhalb dem Benutzer kommunizieren können. */
    private static final int DEFAULT_RADIUS = 2 * 32;

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
    public @NotNull Map<UUID, User> getCommunicableUsers(@NotNull final User user) {
        if (user.getLocation() == null) {
            throw new IllegalStateException("Users location is not available");
        }
        // ANMERKUNG: Radiusbasierte Kommunikation kann man bereichsübergreifend machen, wenn man hier die Benutzer des
        // Raums statt des Kontextes nimmt:
        /*
            return user.getLocation().getRoom().getUsers().values().stream()
                .filter(otherUser -> user.getLocation().distance(otherUser.getLocation()) <= radius)
                .collect(Collectors.toUnmodifiableMap(User::getUserId, Function.identity()));
         */
        return area.getUsers().values().stream()
                .filter(otherUser -> otherUser.getLocation() != null && user.getLocation().distance(otherUser.getLocation()) <= radius)
                .collect(Collectors.toMap(User::getUserId, Function.identity()));
    }

}