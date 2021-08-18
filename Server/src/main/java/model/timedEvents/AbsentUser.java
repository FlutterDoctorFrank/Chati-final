package model.timedEvents;

import model.user.Status;
import model.user.User;
import org.jetbrains.annotations.NotNull;
import java.time.temporal.ChronoUnit;

/**
 * Repräsentiert das Ereignis, einem Benutzer nach einer bestimmten Zeit der Inaktivität den Status {@link Status#AWAY}
 * zuzuweisen.
 */
public class AbsentUser extends TimedEvent {

    /** Benutzer, dem der Status zugewiesen werden soll. */
    private final User user;

    /**
     * Erzeugt eine neue Instanz des TimedEvent.
     * @param user Benutzer, dem der Status zugewiesen werden soll.
     */
    public AbsentUser(@NotNull final User user) {
        super(user.getLastActivity().plus(Status.AWAY_TIME, ChronoUnit.MINUTES));
        this.user = user;
    }

    @Override
    public void execute() {
        user.setStatus(Status.AWAY);
    }

    @Override
    public boolean isValid() {
        return user.isOnline() && user.getLastActivity().plus(Status.AWAY_TIME, ChronoUnit.MINUTES).equals(time);
    }
}