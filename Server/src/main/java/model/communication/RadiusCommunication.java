package model.communication;

import model.user.User;
import org.jetbrains.annotations.NotNull;
import java.util.Map;
import java.util.UUID;

/**
 * Eine Klasse, welche eine radius-basierte Kommunikation repräsentiert.
 */
public class RadiusCommunication extends AreaCommunication {

    /** Standardmäßiger Radius, innerhalb dem Benutzer kommunizieren können. */
    private static final int DEFAULT_RADIUS = 2 * 32;

    /** Radius, innerhalb dem Benutzer kommunizieren können.*/
    private final int radius;

    /**
     * Erzeugt eine neue Instanz der RadiusCommunication.
     */
    public RadiusCommunication(final boolean exclusive) {
        this(exclusive, DEFAULT_RADIUS);
    }

    /**
     * Erzeugt eine neue Instanz der RadiusCommunication.
     */
    public RadiusCommunication(final boolean exclusive, final int radius) {
        super(exclusive);
        this.radius = radius;
    }

    @Override
    public @NotNull Map<UUID, User> getCommunicableUsers(@NotNull final User user) {
        if (user.getLocation() == null) {
            throw new IllegalStateException("Users location is not available");
        }

        Map<UUID, User> communicableUsers = super.getCommunicableUsers(user);
        communicableUsers.values().removeIf(other -> other.getLocation() == null || user.getLocation().distance(other.getLocation()) > radius);
        return communicableUsers;
    }
}