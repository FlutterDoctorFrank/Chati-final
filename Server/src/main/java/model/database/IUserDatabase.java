package model.database;

import model.context.Context;
import model.notification.Notification;
import model.role.Role;
import model.user.Avatar;
import model.user.User;
import org.jetbrains.annotations.NotNull;

public interface IUserDatabase {

    void changeAvatar(@NotNull final User user, @NotNull final Avatar avatar);

    void addFriendship(@NotNull final User first, @NotNull final User second);

    void removeFriendship(@NotNull final User first, @NotNull final User second);

    void addIgnoredUser(@NotNull final User ignoringUser, @NotNull final User ignoredUser);

    void removeIgnoredUser(@NotNull final User ignoringUser, @NotNull final User ignoredUser);

    void addRole(@NotNull final User user, @NotNull final Context context, @NotNull final Role role);

    void removeRole(@NotNull final User user, @NotNull final Context context, @NotNull final Role role);

    void addNotification(@NotNull final User user, @NotNull final Notification notification);

    void updateNotification(@NotNull final User user, @NotNull final Notification notification);

    void removeNotification(@NotNull final User user, @NotNull final Notification notification);
}
