package model.communication;

import model.context.Context;
import model.user.User;

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
    public Map<UUID, User> getCommunicableUsers(User user) {
        Context parent = area.getParent();
        if (parent == null) {
            return new HashMap<>();
        }
        return parent.getCommunicableUsers(user);
    }
}
