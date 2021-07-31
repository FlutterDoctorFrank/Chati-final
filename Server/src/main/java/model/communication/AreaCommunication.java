package model.communication;

import model.context.spatial.SpatialContext;
import model.user.User;

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
    public Map<UUID, User> getCommunicableUsers(User user) {
        return context.getUsers();
    }
}
