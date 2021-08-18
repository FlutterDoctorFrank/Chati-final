package model.communication;

import model.user.User;

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
    public Map<UUID, User> getCommunicableUsers(User user) {
        Map<UUID, User> communicableUsers = new HashMap<>();
        // Prüfe, ob sich kommunizierender Benutzer in dem Kontext befindet.
        if (area.contains(user)) {
            // Füge alle Benutzer in diesem Kontext hinzu, die sich nicht in untergeordneten Kontexten befinden.
            communicableUsers.putAll(area.getUsers());
            area.getChildren()
                    .values().forEach(child -> communicableUsers.keySet().removeAll(child.getUsers().keySet()));
        }
        return communicableUsers;
    }
}