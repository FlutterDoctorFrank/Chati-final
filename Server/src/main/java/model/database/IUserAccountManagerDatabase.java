package model.database;

import model.user.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.Map;
import java.util.UUID;

public interface IUserAccountManagerDatabase {

    @Nullable User createAccount(@NotNull final String username, @NotNull final String password);

    boolean checkPassword(@NotNull final String username, @NotNull final String password);

    void setPassword(@NotNull final User user, @NotNull final String newPassword);

    void updateLastOnlineTime(@NotNull final User user);

    void deleteAccount(@NotNull final User user);

    @Nullable User getUser(@NotNull final UUID userId);

    @Nullable User getUser(@NotNull final String username);

    @NotNull Map<UUID, User> getUsers();
}
