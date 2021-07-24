package model.communication;

import model.context.Context;
import model.user.User;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;


public class RadiusCommunication extends CommunicationRegion {
    private static final int DEFAULT_RADIUS = 2;
    private final int radius;

    public RadiusCommunication(Context context) {
        super(context);
        radius = DEFAULT_RADIUS;
    }


    @Override
    public Map<UUID, User> getCommunicableUsers(User user) {
        return context.getUsers().entrySet().stream()
                .filter((entry) -> user.getLocation().distance(entry.getValue().getLocation()) <= radius)
                .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
