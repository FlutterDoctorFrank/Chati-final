package model.communication;

import model.user.User;
import org.jetbrains.annotations.NotNull;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Eine Klasse, welche eine bereichs-basierte Kommunikation repräsentiert.
 */
public class AreaCommunication extends CommunicationRegion {

    /**
     * Erzeugt eine neue Instanz der AreaCommunication.
     */
    public AreaCommunication() {
        super();
    }

    @Override
    public @NotNull Map<UUID, User> getCommunicableUsers(@NotNull final User user) {
        return new HashMap<>(area.getUsers());
    }
}