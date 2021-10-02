package model.timedEvents;

import model.exception.UserNotFoundException;
import model.user.User;
import model.user.account.UserAccountManager;
import org.jetbrains.annotations.NotNull;
import java.time.temporal.ChronoUnit;

/**
 * Repräsentiert das Ereignis, das Konto eines Benutzers nach einem bestimmten Zeitraum, in dem er nicht angemeldet war,
 * zu löschen.
 */
public class AccountDeletion extends TimedEvent {

    /** Benutzer, dessen Konto gelöscht werden soll. */
    private final User user;

    /**
     * Erzeugt eine neue Instanz des TimedEvent.
     * @param user Benutzer, dessen Konto gelöscht werden soll.
     */
    public AccountDeletion(@NotNull final User user) {
        super(user.getLastLogout().plus(UserAccountManager.ACCOUNT_DELETION_TIME, ChronoUnit.MONTHS));
        this.user = user;
    }

    @Override
    public void execute() {
        try {
            UserAccountManager.getInstance().deleteUser(user);
        } catch (UserNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isValid() {
        return !user.isOnline()
                && !user.getLastLogout().plus(UserAccountManager.ACCOUNT_DELETION_TIME, ChronoUnit.MONTHS).isAfter(time);
    }
}