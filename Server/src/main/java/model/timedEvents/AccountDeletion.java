package model.timedEvents;

import model.exception.UserNotFoundException;
import model.user.User;
import model.user.account.UserAccountManager;
import org.jetbrains.annotations.NotNull;
import java.time.temporal.ChronoUnit;

public class AccountDeletion extends TimedEvent {

    private final User user;

    public AccountDeletion(@NotNull final User user) {
        super(user.getLastLogoutTime().plus(UserAccountManager.ACCOUNT_DELETION_TIME, ChronoUnit.MONTHS));
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
                && !user.getLastLogoutTime().plus(UserAccountManager.ACCOUNT_DELETION_TIME, ChronoUnit.MONTHS).isAfter(time);
    }
}