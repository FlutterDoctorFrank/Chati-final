package model.communication;

import model.context.Context;
import model.context.spatial.SpatialContext;
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
     * @param context Kontext, der die Instanz dieser Kommunikationsform nutzt.
     */
    public ParentCommunication(SpatialContext context) {
        super(context);
    }

    @Override
    public Map<UUID, User> getCommunicableUsers(User user) {
        Context parent = context.getParent();
        if (parent == null) {
            return new HashMap<>();
        }
        return parent.getCommunicableUsers(user);
    }
}
