package model.user;

import model.context.Context;
import model.context.spatial.Location;
import model.exception.ContextNotFoundException;
import model.context.ContextID;
import model.exception.UserNotFoundException;
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

    /** Die Information, ob sich der Benutzer in einem privaten Raum befindet. */
    protected boolean isInPrivateRoom;

    /** Die aktuelle Position des Benutzers auf der Karte des aktuell angezeigten Raums. */
    protected Location currentLocation;

    /** Eine Information darüber, ob dieser Benutzer durch ein Teleportieren zu seiner aktuellen Position bewegt
     *  werden soll. */
    private boolean isTeleporting;

    /** Eine Information darüber, ob sich dieser Benutzer schnell fortbewegt. */
    private boolean isSprinting;

    /** Die Information, ob der Benutzer mit dem intern angemeldeten Benutzer dieses Clients befreundet ist. */
    private boolean isFriend;

    /** Die Information, ob der Benutzer von dem intern angemeldeten Benutzer dieses Clients ignoriert wird. */
    private boolean isIgnored;

    /** Die Information, ob der intern angemeldete Benutzer mit diesem Benutzer gerade kommunizieren kann. */
    private boolean canCommunicateWith;

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
        this.isSprinting = false;
        this.isTeleporting = false;
        this.isFriend = false;
        this.isIgnored = false;
        this.canCommunicateWith = false;
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
        if (!isInCurrentWorld) {
            discardWorldInfo();
        }
        UserManager.getInstance().getModelObserver().setUserInfoChanged();
    }

    @Override
    public void setInCurrentRoom(boolean isInCurrentRoom) {
        this.isInCurrentRoom = isInCurrentRoom;
        if (!isInCurrentRoom) {
            discardRoomInfo();
        }
        UserManager.getInstance().getModelObserver().setUserInfoChanged();
    }

    @Override
    public void setInPrivateRoom(boolean isInPrivateRoom) {
        this.isInPrivateRoom = isInPrivateRoom;
        UserManager.getInstance().getModelObserver().setUserInfoChanged();
    }

    @Override
    public void setFriend(boolean isFriend) {
        boolean wasFriend = this.isFriend;
        this.isFriend = isFriend;
        if (wasFriend && !isFriend && !isKnown()) {
            try {
                UserManager.getInstance().removeExternUser(userId);
            } catch (UserNotFoundException e) {
                e.printStackTrace();
            }
        }
        UserManager.getInstance().getModelObserver().setUserInfoChanged();
    }

    @Override
    public void setIgnored(boolean isIgnored) {
        this.isIgnored = isIgnored;
        UserManager.getInstance().getModelObserver().setUserInfoChanged();
    }

    @Override
    public void setCommunicable(boolean canCommunicateWith) {
        this.canCommunicateWith = canCommunicateWith;
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
        if (isBanned) {
            bannedContexts.put(contextId, Context.getGlobal().getContext(contextId));
        } else {
            bannedContexts.remove(contextId);
        }
        if (!isKnown()) {
            try {
                UserManager.getInstance().removeExternUser(userId);
            } catch (UserNotFoundException e) {
                e.printStackTrace();
            }
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
    public void setLocation(final float posX, final float posY, final boolean isTeleporting, final boolean isSprinting) {
        this.currentLocation = new Location(posX, posY);
        this.isTeleporting = isTeleporting;
        this.isSprinting = isSprinting;
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
    public Avatar getAvatar() {
        return avatar;
    }

    @Override
    public Status getStatus() {
        InternUser internUser = UserManager.getInstance().getInternUser();
        return (this.equals(internUser) || status != Status.INVISIBLE) ? status
                : (internUser.hasPermission(Permission.SEE_INVISIBLE_USERS) ? Status.INVISIBLE
                : (this.isInCurrentRoom ? Status.ONLINE
                : Status.OFFLINE));
    }

    @Override
    public boolean isOnline() {
        return getStatus() != Status.OFFLINE;
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
    public boolean canBeInvited() {
        InternUser internUser = UserManager.getInstance().getInternUser();
        return this.isOnline() && internUser.isInCurrentRoom && internUser.isInPrivateRoom && !this.isInCurrentRoom
                && internUser.hasPermission(Permission.MANAGE_PRIVATE_ROOM) && this.status != Status.BUSY;
    }

    @Override
    public boolean canBeKicked() {
        InternUser internUser = UserManager.getInstance().getInternUser();
        return this.isOnline() && internUser.isInCurrentRoom && internUser.isInPrivateRoom && this.isInCurrentRoom
                && internUser.hasPermission(Permission.MANAGE_PRIVATE_ROOM)
                && !this.hasPermission(Permission.ENTER_PRIVATE_ROOM);
    }

    @Override
    public boolean canTeleportTo() {
        InternUser internUser = UserManager.getInstance().getInternUser();
        return this.isOnline() && (!this.isInPrivateRoom || internUser.hasPermission(Permission.ENTER_PRIVATE_ROOM))
                && ((this.isFriend && this.status != Status.BUSY) || internUser.hasPermission(Permission.TELEPORT_TO_USER));
    }

    @Override
    public boolean canBeReported() {
        InternUser internUser = UserManager.getInstance().getInternUser();
        return this.isOnline() && internUser.isInCurrentWorld && this.isInCurrentWorld
                && !internUser.hasPermission(Permission.BAN_MODERATOR) && !this.hasPermission(Permission.BAN_MODERATOR)
                && (!internUser.hasPermission(Permission.BAN_USER) || this.hasPermission(Permission.BAN_USER));
    }

    @Override
    public boolean canBeMuted() {
        InternUser internUser = UserManager.getInstance().getInternUser();
        return this.isOnline() && internUser.isInCurrentWorld() && this.isInCurrentWorld()
                && internUser.hasPermission(Permission.MUTE) && !this.hasPermission(Permission.MUTE);
    }

    @Override
    public boolean canBeBanned() {
        InternUser internUser = UserManager.getInstance().getInternUser();
        return !this.hasPermission(Permission.BAN_MODERATOR) && ((internUser.isInCurrentWorld()
                && internUser.hasPermission(Permission.BAN_MODERATOR)) || (internUser.hasPermission(Permission.BAN_USER)
                && !this.hasPermission(Permission.BAN_USER)));
    }

    @Override
    public boolean canAssignModerator() {
        InternUser internUser = UserManager.getInstance().getInternUser();
        return internUser.hasPermission(Permission.ASSIGN_MODERATOR) && !this.hasRole(Role.ADMINISTRATOR)
                && !this.hasRole(Role.OWNER) && !this.isBanned();
    }

    @Override
    public boolean canCommunicateWith() {
        return canCommunicateWith;
    }

    @Override
    public boolean isReported() {
        Context current = UserManager.getInstance().getInternUser().getDeepestContext();
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
        Context current = UserManager.getInstance().getInternUser().getDeepestContext();
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
        Context current = UserManager.getInstance().getInternUser().getDeepestContext();
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
    public Location getLocation() {
        return currentLocation;
    }

    @Override
    public boolean isTeleporting() {
        if (isTeleporting) {
            isTeleporting = false;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean isSprinting() {
        return isSprinting;
    }

    @Override
    public boolean hasRole(Role role) {
        return hasRole(UserManager.getInstance().getInternUser().getDeepestContext(), role);
    }

    @Override
    public boolean hasPermission(Permission permission) {
        return hasPermission(UserManager.getInstance().getInternUser().getDeepestContext(), permission);
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

    /**
     * Gibt den untergeordnetsten Kontext zurück, in dem sich der Benutzer befindet.
     * @return Untergeordnetster Kontext, in dem sich der Benutzer befindet.
     */
    public Context getDeepestContext() {
        return status == Status.OFFLINE ? null
                : (UserManager.getInstance().getInternUser().getCurrentWorld() == null || !isInCurrentWorld ? Context.getGlobal()
                : (UserManager.getInstance().getInternUser().getCurrentRoom() == null || !isInCurrentRoom
                    || currentLocation == null ? UserManager.getInstance().getInternUser().getCurrentWorld()
                : currentLocation.getArea()));
    }

    /**
     * Veranlasst das Löschen aller Informationen zu Kontexten innerhalb der Welt des intern angemeldeten Benutzers.
     */
    public void discardWorldInfo() {
        reportedContexts.values().removeIf(context -> !context.equals(Context.getGlobal()));
        mutedContexts.values().removeIf(context -> !context.equals(Context.getGlobal()));
        bannedContexts.values().removeIf(context -> !context.equals(Context.getGlobal()));
        contextRoles.keySet().removeIf(context -> !context.equals(Context.getGlobal()));
        isInCurrentWorld = false;
        isInCurrentRoom = false;
        currentLocation = null;
    }

    /**
     * Veranlasst das Löschen aller Informationen zu Kontexten innerhalb des Raums des intern angemeldeten Benutzers.
     */
    public void discardRoomInfo() {
        reportedContexts.values().removeIf(context -> !context.equals(Context.getGlobal())
                && !context.equals(UserManager.getInstance().getInternUser().getCurrentWorld()));
        mutedContexts.values().removeIf(context -> !context.equals(Context.getGlobal())
                && !context.equals(UserManager.getInstance().getInternUser().getCurrentWorld()));
        bannedContexts.values().removeIf(context -> !context.equals(Context.getGlobal())
                && !context.equals(UserManager.getInstance().getInternUser().getCurrentWorld()));
        contextRoles.keySet().removeIf(context -> !context.equals(Context.getGlobal())
                && !context.equals(UserManager.getInstance().getInternUser().getCurrentWorld()));
        isInCurrentRoom = false;
        currentLocation = null;
    }

    /**
     * Überprüft, ob dieser Benutzer dem aktuell angemeldeten internen Benutzer noch bekannt ist.
     * @return true, wenn der Benutzer noch bekannt ist, sonst false.
     */
    public boolean isKnown() {
        return isInCurrentWorld || isFriend
                || isBanned() && UserManager.getInstance().getInternUser().hasPermission(Permission.BAN_USER)
                && UserManager.getInstance().getInternUser().hasPermission(Permission.BAN_MODERATOR);
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