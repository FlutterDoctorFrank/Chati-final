package model.communication;

import model.context.Context;
import model.user.User;
import org.jetbrains.annotations.NotNull;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Eine Kommunikationsform, welches die Kommunikationsform des übergeordneten Kontextes verwendet. Wird verwendet, um
 * über mehrere Bereiche hinweg kommunizieren zu können.
 */
public class ParentCommunication extends CommunicationRegion {

    /**
     * Erzeugt eine neue Instanz der ParentCommunication
     */
    public ParentCommunication() {
        super();
    }

    @Override
    public @NotNull Map<UUID, User> getCommunicableUsers(@NotNull final User user) {
        Context parent = area.getParent();
        if (parent == null) {
            return new HashMap<>();
        }
        return parent.getUsers();
    }
}