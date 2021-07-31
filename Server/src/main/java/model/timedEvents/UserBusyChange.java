package model.timedEvents;

import model.user.Status;
import model.user.User;

import java.time.temporal.ChronoUnit;

public class UserBusyChange extends TimedEvent {

    private final User user;

    public UserBusyChange(User user) {
        super(user.getLastActivity().plus(Status.AWAY_TIME, ChronoUnit.MINUTES));
        this.user = user;
    }

    @Override
    public void execute() {
        user.setStatus(Status.AWAY);
    }

    @Override
    public boolean isValid() {
        return user.isOnline()
                && user.getLastActivity().plus(Status.AWAY_TIME, ChronoUnit.MINUTES).equals(time);
    }
}
