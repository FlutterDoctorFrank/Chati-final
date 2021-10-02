package model.timedEvents;

import model.context.global.GlobalContext;
import model.user.User;
import org.jetbrains.annotations.NotNull;

import java.time.temporal.ChronoUnit;

/**
 * Repräsentiert das Ereignis, einen gemeldeten Benutzer nach einer bestimmten Zeit, in der er nicht mehr gemeldet wurde,
 * den Gemeldet-Zustand zu entziehen.
 */
public class UnreportUser extends TimedEvent {

    /** Benutzer, der nicht mehr gemeldet sein soll. */
    private final User user;

    /**
     * Erzeugt eine neue Instanz eines TimedEvent.
     * @param user Benutzer, der nicht mehr gemeldet sein soll.
     */
    public UnreportUser(@NotNull final User user) {
        super(user.getLastReported());
        this.user = user;
    }

    @Override
    public void execute() {
        GlobalContext.getInstance().removeReportedUser(user);
        GlobalContext.getInstance().getWorlds().values().forEach(world -> world.removeReportedUser(user));
    }

    @Override
    public boolean isValid() {
        return !user.getLastReported().plus(User.UNREPORT_TIME, ChronoUnit.DAYS).isAfter(time);
    }
}
