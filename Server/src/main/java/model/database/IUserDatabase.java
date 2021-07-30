package model.database;

import model.context.Context;
import model.context.spatial.AreaReservation;
import model.context.spatial.SpatialContext;
import model.notification.Notification;
import model.role.Role;
import model.user.Avatar;
import model.user.User;

public interface IUserDatabase {
    void changeAvatar(User user, Avatar avatar);
    void addFriendship(User first, User second);
    void removeFriendship(User first, User second);
    void addIgnoredUser(User ignoringUser, User ignoredUser);
    void removeIgnoredUser(User ignoringUser, User ignoredUser);
    void addRole(User user, Context context, Role role);
    void removeRole(User user, Context context, Role role);
    void addNotification(User user, Notification notification);
    void removeNotification(User user, Notification notification);
}
