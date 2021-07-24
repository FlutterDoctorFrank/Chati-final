package model.communication;

import model.context.Context;
import model.user.User;

import java.util.Map;
import java.util.UUID;

public class AreaCommunication extends CommunicationRegion {

    public AreaCommunication(Context context) {
        super(context);
    }

    @Override
    public Map<UUID, User> getCommunicableUsers(User user) {
        return context.getUsers();
    }
}
