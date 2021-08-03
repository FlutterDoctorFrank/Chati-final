package model.user;

import model.context.Context;
import model.context.global.GlobalContext;
import model.context.spatial.ILocationView;
import model.context.spatial.Location;
import model.exception.IllegalActionException;
import model.exception.NotificationNotFoundException;
import model.MessageBundle;
import model.notification.INotificationView;
import model.notification.Notification;
import model.role.ContextRole;
import model.context.ContextID;
import model.role.Role;
import view.Screens.IModelObserver;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Eine Klasse, welche einen Benutzer der Anwendung repräsentiert.
 */
public class User implements IUserController, IUserView{

    /**
     * eindeutige ID des Benutzers
     */
    private final UUID userId;
    /**
     * Name des Benutzers
     */
    private String username;
    /**
     * gibt an, ob sich der Benutzer in der aktuellen Welt befindet
     */
    private boolean inCurrentWorld;
    /**
     * gibt an, ob sich der Benutezr im aktuellen Raum befindet
     */
    private boolean inCurrentRoom;
    /**
     * gibt an, ob der Benutzer mit dem lokalen Benutzer befreundet ist
     */
    private boolean friend;
    /**
     * gibt an, ob der Benutzer vom lokalen Benutzern ignoriert wird
     */
    private boolean ignored;
    /**
     * gibt den Status des Benutzers an
     */
    private Status status;
    /**
     * alle Benachrichtigungen des Benutzers
     */
    private final Map<UUID, Notification> notifications;
    /**
     * alle Rollen des Benutzers
     */
    private final Map<ContextID, ContextRole> roles;
    /**
     * Kontexte, in denen der Benutzer gemeldet ist
     */
    private final Map<ContextID, Context> reported;
    /**
     * Kontexte, in denen der Benutzer stumm geschalten ist
     */
    private final Map<ContextID, Context> muted;
    /**
     * Kontexte, in denen der Benutzer gebannt ist
     */
    private final Map<ContextID, Context> banned;
    /**
     * aktuelle Position des Benutzers
     */
    private Location location;
    /**
     * Avatar des Benutzers
     */
    private Avatar avatar;
    /**
     * Beobachterschnittstelle, um View über Änderungen zu informieren
     */
    private final IModelObserver iModelObserver;


    public User(UUID userId) {
        this.userId = userId;
        iModelObserver = GlobalContext.getInstance().getIModelObserver();
        notifications = new HashMap<>();
        roles = new HashMap<>();
        reported = new HashMap<>();
        muted = new HashMap<>();
        banned = new HashMap<>();
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
            this.roles.put(contextId, new ContextRole(roles));
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
    public void addNotification(ContextID contextId, UUID notificationId, String messageKey, Object[] args,
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
        return roles.get(GlobalContext.getInstance().getContextId()).getRoles();
    }

    @Override
    public Set<Role> getWorldRoles() {
        return roles.get(GlobalContext.getInstance().getWorld().getContextId()).getRoles();
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(userId, user.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId);
    }
}
