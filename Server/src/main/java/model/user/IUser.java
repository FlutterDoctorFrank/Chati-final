package model.user;

import model.context.ContextID;
import model.context.IContext;
import model.context.spatial.ILocation;
import model.context.spatial.ISpatialContext;
import model.exception.*;
import model.notification.INotification;
import model.role.IContextRole;

import java.util.Map;
import java.util.UUID;

public interface IUser {
    public void joinWorld(ContextID worldID) throws IllegalWorldActionException, IllegalActionException, ContextNotFoundException;
    public void leaveWorld() throws IllegalActionException;
    public void chat(String message);
    public void talk(byte[] voicedata);
    public void move(int posX, int posY) throws IllegalPositionException, IllegalActionException;
    public void executeAdministrativeAction(UUID targetID, AdministrativeAction action, String[] args) throws UserNotFoundException, IllegalAdministrativeActionException, IllegalActionException, NoPermissionException;
    public void interact(ContextID spatialID) throws IllegalInteractionException;
    public void executeOption(ContextID spatialID, int menuOption, String[] args) throws IllegalInteractionException, IllegalMenuActionException;
    public void deleteNotification(UUID notificationID) throws NotificationNotFoundException;
    public void manageNotification(UUID notificationID, boolean accept) throws NotificationNotFoundException, IllegalNotificationActionException;
    public void setStatus(Status status);
    public void setAvatar(Avatar avatar);

    public UUID getUserID();
    public String getUsername();
    public Avatar getAvatar();
    public ISpatialContext getWorld();
    public ILocation getLocation();
    public Map<UUID, IUser> getFriends();
    public Map<UUID, IUser> getIgnoredUsers();
    public Map<IContext, IContextRole> getGlobalRoles();
    public Map<IContext, IContextRole> getWorldRoles();
    public Map<UUID, INotification> getGlobalNotifications();
    public Map<UUID, INotification> getWorldNotification();
}
