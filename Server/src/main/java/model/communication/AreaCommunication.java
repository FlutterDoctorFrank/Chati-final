package model.communication;

import model.context.Context;
import model.context.spatial.SpatialContext;
import model.user.User;

import java.util.Map;
import java.util.UUID;

public class AreaCommunication extends CommunicationRegion {

    public AreaCommunication(SpatialContext context) {
        super(context);
    }

    @Override
    public Map<UUID, User> getCommunicableUsers(User user) {
        return context.getUsers();
    }
}
