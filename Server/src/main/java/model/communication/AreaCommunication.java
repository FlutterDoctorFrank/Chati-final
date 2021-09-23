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
    public AreaCommunication(final boolean exclusive) {
        super(exclusive);
    }

    @Override
    public @NotNull Map<UUID, User> getCommunicableUsers(@NotNull final User user) {
        Map<UUID, User> communicableUsers = new HashMap<>();

        if (area.contains(user)) {
            communicableUsers.putAll(area.getCommunicableUsers());

            if (!exclusive && area.getParent() != null) {
                communicableUsers.putAll(area.getParent().getCommunicableUsers(user));
            }
        }

        return communicableUsers;
    }
}