package model.User;

import model.Context.Context;
import model.Context.Global.GlobalContext;
import model.Context.Spatial.ILocationView;
import model.Context.Spatial.Location;
import model.Exceptions.IllegalActionException;
import model.Exceptions.NotificationNotFoundException;
import model.MessageBundle;
import model.Notification.INotificationView;
import model.Notification.Notification;
import model.Role.ContextRole;
import model.context.ContextID;
import model.role.Role;
import model.user.Avatar;
import model.user.Status;
import view.Screens.IModelObserver;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Eine Klasse, welche einen Benutzer der Anwendung repräsentiert.
 */
public class User implements IUserController, IUserView{

    private UUID userId;
    private String username;
    private boolean inCurrentWorld;
    private boolean inCurrentRoom;
    private boolean friend;
    private boolean ignored;
    private Status status;
    private Map<UUID, Notification> notifications;
    private Map<ContextID, ContextRole> roles;
    private Map<ContextID, Context> reported;
    private Map<ContextID, Context> muted;
    private Map<ContextID, Context> banned;
    private Location location;
    private Avatar avatar;
    private IModelObserver iModelObserver;


    public User(UUID userId) {
        this.userId = userId;
    }


    @Override
    public void setUsername(String username) {
        this.username = username;
        iModelObserver.setUserInfoChanged();
    }

    @Override
    public void setStatus(Status status) {
        this.status = status;
        iModelObserver.setUserInfoChanged();
    }

    @Override
    public void setAvatar(Avatar avatar) {
        this.avatar = avatar;
        iModelObserver.setUserInfoChanged();
    }

    @Override
    public void setCurrentWorld(boolean inWorld) {
        inCurrentWorld = inWorld;
        iModelObserver.setUserInfoChanged();
    }

    @Override
    public void setCurrentRoom(boolean inRoom) {
        inCurrentRoom = inRoom;
        iModelObserver.setUserInfoChanged();
    }

    @Override
    public void setFriend(boolean isFriend) {
        friend = isFriend;
        iModelObserver.setUserInfoChanged();
    }

    @Override
    public void setIgnored(boolean isIgnored) {
        ignored = isIgnored;
        iModelObserver.setUserInfoChanged();
    }

    @Override
    public void setReport(ContextID contextId, boolean isReported) {
        if(!(reported.put(contextId, GlobalContext.getInstance().getContext(contextId)) == null)){
            reported.remove(contextId);
        }
        iModelObserver.setUserInfoChanged();
    }

    @Override
    public void setMute(ContextID contextId, boolean isMuted) {
        if(!(muted.put(contextId, GlobalContext.getInstance().getContext(contextId)) == null)){
            muted.remove(contextId);
        }
        iModelObserver.setUserInfoChanged();
    }

    @Override
    public void setBan(ContextID contextId, boolean isBanned) {
        if(!(banned.put(contextId, GlobalContext.getInstance().getContext(contextId)) == null)){
            banned.remove(contextId);
        }
        iModelObserver.setUserInfoChanged();
    }

    @Override
    public void setRoles(ContextID contextId, Set<Role> roles) {
        ContextRole contextRole = this.roles.get(contextId);
        if (contextRole == null) {
            this.roles.put(contextId, new ContextRole(GlobalContext.getInstance().getContext(contextId), roles));
        } else {
            roles.forEach(role -> {
                try {
                    contextRole.addRole(role);
                } catch (IllegalActionException e) {
                    e.printStackTrace();
                }
            });
        }
        iModelObserver.setUserInfoChanged();
    }

    @Override
    public void addNotification(ContextID contextId, UUID notificationId, String messageKey, String[] args,
                                LocalDateTime timestamp, boolean isRequest) throws IllegalActionException {
        if (notifications.containsKey(notificationId)){
            throw new IllegalActionException("this notificationId maps already to a notification!");
        }
        notifications.put(notificationId, new Notification(notificationId, timestamp, isRequest,
                new MessageBundle(messageKey, args), GlobalContext.getInstance().getContext(contextId)));
        iModelObserver.setUserNotificationChanged();
    }

    @Override
    public void removeNotification(UUID notificationId) throws NotificationNotFoundException{
        if(notifications.remove(notificationId) == null){
            throw new NotificationNotFoundException("This notificationId doesn't map to any Notification", notificationId);
        }
        iModelObserver.setUserNotificationChanged();
    }

    @Override
    public void setPosition(int posX, int posY) {
        location = new Location(posX, posY);
        iModelObserver.setUserPositionChanged();
    }

    @Override
    public UUID getUserId() {
        return userId;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public Status getStatus() {
        return status;
    }

    @Override
    public Avatar getAvatar() {
        return avatar;
    }

    @Override
    public boolean isFriend() {
        return friend;
    }

    @Override
    public boolean isIgnored() {
        return ignored;
    }

    @Override
    public boolean isReported() {
        return reported.containsKey(location.getArea().getContextId());
    }

    @Override
    public boolean isMuted() {
        return muted.containsKey(location.getArea().getContextId());
    }

    @Override
    public boolean isBanned() {
        return banned.containsKey(location.getArea().getContextId());
    }

    @Override
    public boolean isInCurrentWorld() {
        return inCurrentWorld;
    }

    @Override
    public boolean isInCurrentRoom() {
        return inCurrentRoom;
    }

    @Override
    public ILocationView getCurrentLocation() {
        return location;
    }

    @Override
    public Set<Role> getGlobalRoles() {
        return roles.get(GlobalContext.getInstance()).getRoles();
    }

    @Override
    public Set<Role> getWorldRoles() {
        return roles.get(GlobalContext.getInstance().getWorld()).getRoles();
    }

    @Override
    public Map<UUID, INotificationView> getGlobalNotifications() {
        return notifications.entrySet().stream().filter(context -> context.getValue().getContext().equals(GlobalContext.
                getInstance())).collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Override
    public Map<UUID, INotificationView> getWorldNotifications() {
        return notifications.entrySet().stream().filter(context -> context.getValue().getContext().equals(GlobalContext.
                getInstance().getWorld())).collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
