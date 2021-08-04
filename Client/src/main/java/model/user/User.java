package model.user;

import model.context.Context;
import model.context.spatial.Location;
import model.exception.ContextNotFoundException;
import model.context.ContextID;
import model.role.Role;

import java.util.*;

/**
 * Eine Klasse, welche einen Benutzer der Anwendung repräsentiert.
 */
public class User implements IUserController, IUserView {

    /** Die eindeutige ID des Benutzers */
    private final UUID userId;

    /** Der Name eines Benutzers. */
    private String username;

    /** Der Online-Status des Benutzers. */
    private Status status;

    /** Der Avatar des Benutzers. */
    private Avatar avatar;

    /** Die Information, ob sich der Benutzer gerade in der aktuellen Welt des intern angemeldeten Benutzers dieses
        Clients befindet.*/
    protected boolean isInCurrentWorld;

    /** Die Information, ob sich der Benutzer gerade im aktuellen Raum des intern angemeldeten Benutzers dieses
        Clients befindet. */
    protected boolean isInCurrentRoom;

    /** Die aktuelle Position des Benutzers auf der Karte des aktuell angezeigten Raums. */
    protected Location currentLocation;

    /** Die Information, ob der Benutzer mit dem intern angemeldeten Benutzer dieses Clients befreundet ist. */
    private boolean isFriend;

    /** Die Information, ob der Benutzer von dem intern angemeldeten Benutzer dieses Clients ignoriert wird. */
    private boolean isIgnored;

    /** Die Information, ob sich der intern angemeldete Benutzer zu diesem Benutzer teleportieren kann. */
    private boolean canTeleportTo;

    /** Die Kontexte, in denen der Benutzer gemeldet ist. */
    private final Map<ContextID, Context> reportedContexts;

    /** Die Kontexte, in denen der Benutzer stummgeschaltet ist. */
    private final Map<ContextID, Context> mutedContexts;

    /** Die Kontexte, in denen der Benutzer gesperrt ist. */
    private final Map<ContextID, Context> bannedContexts;

    /** Die Rollen eines Benutzer in den jeweiligen Kontexten. */
    private final Map<Context, Set<Role>> contextRoles;

    /**
     * Erzeugt eine neue Instanz eines Benutzers.
     * @param userId Die eindeutige ID des Benutzers.
     */
    public User(UUID userId, String username, Status status, Avatar avatar) {
        this.userId = userId;
        this.username = username;
        this.status = status;
        this.avatar = avatar;
        this.isInCurrentWorld = false;
        this.isInCurrentRoom = false;
        this.currentLocation = null;
        this.isFriend = false;
        this.isIgnored = false;
        this.canTeleportTo = false;
        this.reportedContexts = new HashMap<>();
        this.mutedContexts = new HashMap<>();
        this.bannedContexts = new HashMap<>();
        this.contextRoles = new HashMap<>();
        UserManager.getInstance().getModelObserver().setUserInfoChanged();
    }

    @Override
    public void setUsername(String username) {
        this.username = username;
        UserManager.getInstance().getModelObserver().setUserInfoChanged();
    }

    @Override
    public void setStatus(Status status) {
        this.status = status;
        UserManager.getInstance().getModelObserver().setUserInfoChanged();
    }

    @Override
    public void setAvatar(Avatar avatar) {
        this.avatar = avatar;
        UserManager.getInstance().getModelObserver().setUserInfoChanged();
    }

    @Override
    public void setInCurrentWorld(boolean isInCurrentWorld) {
        this.isInCurrentWorld = isInCurrentWorld;
        UserManager.getInstance().getModelObserver().setUserInfoChanged();
    }

    @Override
    public void setInCurrentRoom(boolean isInCurrentRoom) {
        this.isInCurrentRoom = isInCurrentRoom;
        UserManager.getInstance().getModelObserver().setUserInfoChanged();
    }

    @Override
    public void setFriend(boolean isFriend) {
        this.isFriend = isFriend;
        UserManager.getInstance().getModelObserver().setUserInfoChanged();
    }

    @Override
    public void setIgnored(boolean isIgnored) {
        this.isIgnored = isIgnored;
        UserManager.getInstance().getModelObserver().setUserInfoChanged();
    }

    @Override
    public void setTeleportable(boolean canTeleportTo) {
        this.canTeleportTo = canTeleportTo;
        UserManager.getInstance().getModelObserver().setUserInfoChanged();
    }

    @Override
    public void setReport(ContextID contextId, boolean isReported) throws ContextNotFoundException {
        reportedContexts.put(contextId, Context.getGlobal().getContext(contextId));
        UserManager.getInstance().getModelObserver().setUserInfoChanged();
    }

    @Override
    public void setMute(ContextID contextId, boolean isMuted) throws ContextNotFoundException {
        mutedContexts.put(contextId, Context.getGlobal().getContext(contextId));
        UserManager.getInstance().getModelObserver().setUserInfoChanged();
    }

    @Override
    public void setBan(ContextID contextId, boolean isBanned) throws ContextNotFoundException {
        bannedContexts.put(contextId, Context.getGlobal().getContext(contextId));
        UserManager.getInstance().getModelObserver().setUserInfoChanged();
    }

    @Override
    public void setRoles(ContextID contextId, Set<Role> roles) throws ContextNotFoundException {
        Context context = Context.getGlobal().getContext(contextId);
        contextRoles.put(context, roles);
        UserManager.getInstance().getModelObserver().setUserInfoChanged();
    }

    @Override
    public void setPosition(int posX, int posY) {
        currentLocation = new Location(posX, posY);
        UserManager.getInstance().getModelObserver().setUserPositionChanged();
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
        return isFriend;
    }

    @Override
    public boolean isIgnored() {
        return isIgnored;
    }

    @Override
    public boolean canTeleportTo() {
        return canTeleportTo;
    }

    @Override
    public boolean isReported() {
        Context current = UserManager.getInstance().getInternUser().getCurrentLocation().getArea();
        do {
            if (reportedContexts.containsKey(current.getContextId())) {
                return true;
            }
            current = current.getParent();
        } while (current != null);
        return false;
    }

    @Override
    public boolean isMuted() {
        Context current = UserManager.getInstance().getInternUser().getCurrentLocation().getArea();
        do {
            if (mutedContexts.containsKey(current.getContextId())) {
                return true;
            }
            current = current.getParent();
        } while (current != null);
        return false;
    }

    @Override
    public boolean isBanned() {
        Context current = UserManager.getInstance().getInternUser().getCurrentLocation().getArea();
        do {
            if (bannedContexts.containsKey(current.getContextId())) {
                return true;
            }
            current = current.getParent();
        } while (current != null);
        return false;
    }

    @Override
    public boolean isInCurrentWorld() {
        return isInCurrentWorld;
    }

    @Override
    public boolean isInCurrentRoom() {
        return isInCurrentRoom;
    }

    @Override
    public Location getCurrentLocation() {
        return currentLocation;
    }

    @Override
    public Set<Role> getGlobalRoles() {
        return contextRoles.get(Context.getGlobal());
    }

    @Override
    public Set<Role> getWorldRoles() {
        return contextRoles.get(UserManager.getInstance().getInternUser().getCurrentWorld());
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