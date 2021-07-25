package model.communication;

import model.context.spatial.SpatialContext;
import model.user.User;

import java.util.Map;
import java.util.UUID;

public class ParentCommunication extends CommunicationRegion {

    public ParentCommunication(SpatialContext context) {
        super(context);
    }

    @Override
    public Map<UUID, User> getCommunicableUsers(User user) {
        return context.getParent().getCommunicableUsers(user);
    }
}
