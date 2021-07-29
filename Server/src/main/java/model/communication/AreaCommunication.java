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
     * @param context Kontext, der die Instanz dieser Kommunikationsform nutzt.
     */
    public AreaCommunication(SpatialContext context) {
        super(context);
    }

    @Override
    public Map<UUID, User> getCommunicableUsers(User user) {
        return context.getUsers();
    }
}
