package model.communication;

import model.context.Context;
import model.user.User;

import java.util.Map;
import java.util.UUID;

public abstract class CommunicationRegion {
    protected Context context;

    public CommunicationRegion(Context context) {
        this.context = context;
    }

    public abstract Map<UUID, User> getCommunicableUsers(User user);
}
