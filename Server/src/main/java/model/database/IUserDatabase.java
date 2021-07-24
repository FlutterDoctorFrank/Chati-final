package model.database;

import model.context.Context;
import model.context.spatial.AreaReservation;
import model.context.spatial.SpatialContext;
import model.notification.Notification;
import model.role.Role;
import model.user.User;

public interface IUserDatabase {
    public void addFriendship(User first, User second);
    public void removeFriendship(User first, User second);
    public void addIgnoredUser(User ignoringUser, User ignoredUser);
    public void removeIgnoredUser(User ignoringUser, User ignoredUser);
    public void addRole(User user, Context context, Role role);
    public void removeRole(User user, Context context, Role role);
    public void addNotification(User user, Notification notification);
    public void removeNotification(User user, Notification notification);
    public void addBannedUser(User user, SpatialContext world);
    public void removeBannedUser(User user, SpatialContext world);
    public void addAreaReservation(AreaReservation areaReservation);
    public void removeAreaReservation(AreaReservation areaReservation);
}
