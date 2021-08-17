package model.user;

import model.context.Context;
import model.context.spatial.Location;
import model.exception.ContextNotFoundException;
import model.context.ContextID;
import model.role.Permission;
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
    protected final Map<ContextID, Context> reportedContexts;

    /** Die Kontexte, in denen der Benutzer stummgeschaltet ist. */
    protected final Map<ContextID, Context> mutedContexts;

    /** Die Kontexte, in denen der Benutzer gesperrt ist. */
    protected final Map<ContextID, Context> bannedContexts;

    /** Die Rollen eines Benutzer in den jeweiligen Kontexten. */
    protected final Map<Context, Set<Role>> contextRoles;

    /**
     * Erzeugt eine neue Instanz eines Benutzers.
     * @param userId Die eindeutige ID des Benutzers.
     * @param username Name des Benutzers.
     * @param status Status des Benutzers.
     * @param avatar Avatar des Benutzers.
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
        if (isReported) {
            reportedContexts.put(contextId, Context.getGlobal().getContext(contextId));
        } else {
            reportedContexts.remove(contextId);
        }

        UserManager.getInstance().getModelObserver().setUserInfoChanged();
    }

    @Override
    public void setMute(ContextID contextId, boolean isMuted) throws ContextNotFoundException {
        if (isMuted) {
            mutedContexts.put(contextId, Context.getGlobal().getContext(contextId));
        } else {
            mutedContexts.remove(contextId);
        }
        UserManager.getInstance().getModelObserver().setUserInfoChanged();
    }

    @Override
    public void setBan(ContextID contextId, boolean isBanned) throws ContextNotFoundException {
        //System.out.println("banned context id " + contextId);
        //System.out.println(UserManager.getInstance().getBannedUsers());
        if (isBanned) {
            bannedContexts.put(contextId, Context.getGlobal().getContext(contextId));
            // System.out.println(UserManager.getInstance().getBannedUsers());
        } else {
            bannedContexts.remove(contextId);
        }
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
        InternUser internUser = UserManager.getInstance().getInternUser();
        if (internUser.getCurrentLocation() == null) {
            // System.out.println();
            return reportedContexts.containsKey(Context.getGlobal().getContextId());
        }
        Context current = internUser.getCurrentLocation().getArea();
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
        InternUser internUser = UserManager.getInstance().getInternUser();
        if (internUser.getCurrentLocation() == null) {
            return mutedContexts.containsKey(Context.getGlobal().getContextId());
        }
        Context current = internUser.getCurrentLocation().getArea();
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
        InternUser internUser = UserManager.getInstance().getInternUser();
        if (internUser.getCurrentLocation() == null) {
            return bannedContexts.containsKey(Context.getGlobal().getContextId());
        }
        Context current = internUser.getCurrentLocation().getArea();
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
    public boolean hasRole(Role role) {
        //System.out.println("Area  " +UserManager.getInstance().getInternUser().getCurrentLocation().getArea().getContextId());
        return currentLocation == null ? hasRole(Context.getGlobal(), role)
                : hasRole(UserManager.getInstance().getInternUser().getCurrentLocation().getArea(), role);
    }

    @Override
    public boolean hasPermission(Permission permission) {
        return currentLocation == null ? hasPermission(Context.getGlobal(), permission)
                : hasPermission(UserManager.getInstance().getInternUser().getCurrentLocation().getArea(), permission);
    }

    @Override
    public Role getHighestRole() {
        return hasRole(Role.OWNER) ? Role.OWNER
                : (hasRole(Role.ADMINISTRATOR) ? Role.ADMINISTRATOR
                : (hasRole(Role.MODERATOR) ? Role.MODERATOR
                : (hasRole(Role.ROOM_OWNER) ? Role.ROOM_OWNER
                : (hasRole(Role.AREA_MANAGER) ? Role.AREA_MANAGER
                : null))));
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

    /**
     * Überprüft, ob der Benutzer eine Rolle in einem Kontext besitzt.
     * @param context Zu überprüfender Kontext.
     * @param role Zu überprüfende Rolle.
     * @return true, wenn der Benutzer die Rolle in dem Kontext besitzt, sonst false.
     */
    private boolean hasRole(Context context, Role role) {
        //System.out.println(context.getContextId());
        return contextRoles.containsKey(context) && contextRoles.get(context).contains(role)
                || context.getParent() != null && hasRole(context.getParent(), role);
    }

    /**
     * Überprüft, ob der Benutzer eine Berechtigung in einem Kontext besitzt.
     * @param context Zu überprüfender Kontext.
     * @param permission Zu überprüfende Berechtigung.
     * @return true, wenn der Benutzer die Berechtigung in dem Kontext besitzt, sonst false.
     */
    private boolean hasPermission(Context context, Permission permission) {
        return contextRoles.containsKey(context)
                && contextRoles.get(context).stream().anyMatch(role -> role.hasPermission(permission))
                || context.getParent() != null && hasPermission(context.getParent(), permission);
    }
}