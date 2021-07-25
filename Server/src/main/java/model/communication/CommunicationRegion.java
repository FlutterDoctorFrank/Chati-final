package model.communication;

import model.context.Context;
import model.context.spatial.SpatialContext;
import model.user.User;

import java.util.Map;
import java.util.UUID;

public abstract class CommunicationRegion {
    protected SpatialContext context;

    public CommunicationRegion(SpatialContext context) {
        this.context = context;
    }

    public abstract Map<UUID, User> getCommunicableUsers(User user);
}
