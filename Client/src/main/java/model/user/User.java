package model.user;

import model.communication.CommunicationMedium;
import model.context.Context;
import model.context.ContextID;
import model.context.spatial.Direction;
import model.context.spatial.ISpatialContextView;
import model.context.spatial.Location;
import model.context.spatial.SpatialContext;
import model.exception.ContextNotFoundException;
import model.exception.UserNotFoundException;
import model.role.Permission;
import model.role.Role;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

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

    /** Die Information, ob sich der Benutzer in einem privaten Raum befindet. */
    protected boolean isInPrivateRoom;

    /** Die aktuelle Welt des Benutzers. */
    protected SpatialContext currentWorld;

    /** Der aktuelle Raum des Benutzers. */
    protected SpatialContext currentRoom;

    /** Die aktuelle Position des Benutzers auf der Karte des aktuell angezeigten Raums. */
    protected Location currentLocation;

    /** Eine Information darüber, ob dieser Benutzer durch ein Teleportieren zu seiner aktuellen Position bewegt
     *  werden soll. */
    private boolean isTeleporting;

    /** Eine Information darüber, ob sich dieser Benutzer schnell fortbewegt. */
    private boolean isSprinting;

    /** Eine Information darüber, ob sich dieser Benutzer bewegen darf. */
    private boolean isMovable;

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

    /** Die Rollen eines Benutzers in den jeweiligen Kontexten. */
    protected final Map<Context, Set<Role>> contextRoles;

    /**
     * Erzeugt eine neue Instanz eines Benutzers.
     * @param userId Die eindeutige ID des Benutzers.
     * @param username Name des Benutzers.
     * @param status Status des Benutzers.
     * @param avatar Avatar des Benutzers.
     */
    public User(@NotNull final UUID userId, @NotNull final String username,
                @NotNull final Status status, @Nullable final Avatar avatar) {
        this.userId = userId;
        this.username = username;
        this.status = status;
        this.avatar = avatar;
        this.currentWorld = null;
        this.currentRoom = null;
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
    public void setUsername(@NotNull final String username) {
        if (!this.username.equals(username)) {
            this.username = username;
            UserManager.getInstance().getModelObserver().setUserInfoChanged();
        }
    }

    @Override
    public void setStatus(@NotNull final Status status) {
        if (this.status != status) {
            this.status = status;
            UserManager.getInstance().getModelObserver().setUserInfoChanged();
        }
    }

    @Override
    public void setAvatar(@NotNull final Avatar avatar) {
        if (this.avatar != avatar) {
            this.avatar = avatar;
            UserManager.getInstance().getModelObserver().setUserInfoChanged();
        }
    }

    @Override
    public void joinWorld(@NotNull ContextID worldId) throws ContextNotFoundException {
        this.currentWorld = Context.getGlobal().getChildren().get(worldId);
        if (currentWorld == null) {
            throw new ContextNotFoundException("Tried to join a world that does not exist.", worldId);
        }
        UserManager.getInstance().getModelObserver().setUserInfoChanged();
    }

    @Override
    public void leaveWorld() {
        if (this.currentWorld != null) {
            discardWorldInfo();
            this.currentWorld = null;
            UserManager.getInstance().getModelObserver().setUserInfoChanged();
        }
    }

    @Override
    public void joinRoom(@NotNull final ContextID roomId) throws ContextNotFoundException {
        if (currentWorld == null) {
            throw new IllegalStateException("Cannot join a room when not in a world.");
        }
        this.currentRoom = currentWorld.getContext(roomId);
        UserManager.getInstance().getModelObserver().setUserInfoChanged();
    }

    @Override
    public void leaveRoom() {
        if (this.currentRoom != null) {
            discardRoomInfo();
            this.currentRoom = null;
            UserManager.getInstance().getModelObserver().setUserInfoChanged();
        }
    }

    @Override
    public void setInPrivateRoom(final boolean isInPrivateRoom) {
        if (this.isInPrivateRoom != isInPrivateRoom) {
            this.isInPrivateRoom = isInPrivateRoom;
            UserManager.getInstance().getModelObserver().setUserInfoChanged();
        }
    }

    @Override
    public void setFriend(final boolean isFriend) {
        if (this.isFriend != isFriend) {
            this.isFriend = isFriend;
            if (!isFriend && !isKnown()) {
                try {
                    UserManager.getInstance().removeExternUser(userId);
                } catch (UserNotFoundException e) {
                    e.printStackTrace();
                }
            }
            UserManager.getInstance().getModelObserver().setUserInfoChanged();
        }
    }

    @Override
    public void setIgnored(final boolean isIgnored) {
        if (this.isIgnored != isIgnored) {
            this.isIgnored = isIgnored;
            UserManager.getInstance().getModelObserver().setUserInfoChanged();
        }
    }

    @Override
    public void setCommunicable(final boolean canCommunicateWith) {
        if (this.canCommunicateWith != canCommunicateWith) {
            this.canCommunicateWith = canCommunicateWith;
            UserManager.getInstance().getModelObserver().setUserInfoChanged();
        }
    }

    @Override
    public void setMovable(final boolean isMovable) {
        if (this.isMovable != isMovable) {
            this.isMovable = isMovable;
            UserManager.getInstance().getModelObserver().setUserInfoChanged();
        }
    }

    @Override
    public void setReport(@NotNull final ContextID contextId, final boolean isReported) throws ContextNotFoundException {
        if (isReported) {
            reportedContexts.put(contextId, Context.getGlobal().getContext(contextId));
        } else {
            reportedContexts.remove(contextId);
        }
        UserManager.getInstance().getModelObserver().setUserInfoChanged();
    }

    @Override
    public void setMute(@NotNull final ContextID contextId, final boolean isMuted) throws ContextNotFoundException {
        if (isMuted) {
            mutedContexts.put(contextId, Context.getGlobal().getContext(contextId));
        } else {
            mutedContexts.remove(contextId);
        }
        UserManager.getInstance().getModelObserver().setUserInfoChanged();
    }

    @Override
    public void setBan(@NotNull final ContextID contextId, final boolean isBanned) throws ContextNotFoundException {
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
    public void setRoles(@NotNull final ContextID contextId, @NotNull final Set<Role> roles) throws ContextNotFoundException {
        Context context = Context.getGlobal().getContext(contextId);
        contextRoles.put(context, roles);
        UserManager.getInstance().getModelObserver().setUserInfoChanged();
    }

    @Override
    public void setLocation(final float posX, final float posY, final boolean isTeleporting, final boolean isSprinting,
                            @NotNull final Direction direction) {
        if (this.currentLocation == null) {
            this.currentLocation = new Location(posX, posY, direction);
        } else {
            this.currentLocation.setCoordinates(posX, posY);
            this.currentLocation.setDirection(direction);
        }

        this.isTeleporting = isTeleporting;
        this.isSprinting = isSprinting;
    }

    @Override
    public @NotNull UUID getUserId() {
        return userId;
    }

    @Override
    public @NotNull String getUsername() {
        return username;
    }

    @Override
    public @NotNull Avatar getAvatar() {
        return avatar;
    }

    @Override
    public @NotNull Status getStatus() {
        InternUser internUser = UserManager.getInstance().getInternUser();
        return (this.equals(internUser) || status != Status.INVISIBLE) ? status
                : (internUser.hasPermission(Permission.SEE_INVISIBLE_USERS) ? Status.INVISIBLE
                : (this.isInCurrentRoom() ? Status.ONLINE
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
    public boolean canWhisper() {
        IInternUserView internUser = UserManager.getInstance().getInternUser();
        return internUser.getCurrentWorld() != null && this.isOnline() && (this.status != Status.BUSY
                && ((this.canCommunicateWith && !this.isIgnored) || this.isFriend)
                || internUser.hasPermission(Permission.CONTACT_USER) || this.hasPermission(Permission.CONTACT_USER));
    }

    @Override
    public boolean canBeInvited() {
        InternUser internUser = UserManager.getInstance().getInternUser();
        return this.isOnline() && !this.isInCurrentRoom() && internUser.isInPrivateRoom
                && internUser.hasPermission(Permission.MANAGE_PRIVATE_ROOM) && this.status != Status.BUSY;
    }

    @Override
    public boolean canBeKicked() {
        InternUser internUser = UserManager.getInstance().getInternUser();
        return this.isOnline() && this.isInCurrentRoom() && internUser.isInPrivateRoom
                && internUser.hasPermission(Permission.MANAGE_PRIVATE_ROOM)
                && !this.hasPermission(Permission.ENTER_PRIVATE_ROOM);
    }

    @Override
    public boolean canTeleportTo() {
        InternUser internUser = UserManager.getInstance().getInternUser();
        return internUser.isMovable() && this.isOnline() && this.getCurrentWorld() != null && (!this.isInPrivateRoom
                || internUser.hasPermission(Permission.ENTER_PRIVATE_ROOM)) && ((this.isFriend
                && this.status != Status.BUSY) || internUser.hasPermission(Permission.TELEPORT_TO_USER));
    }

    @Override
    public boolean canBeReported() {
        InternUser internUser = UserManager.getInstance().getInternUser();
        return internUser.getCurrentWorld() != null && this.isOnline() && this.isInCurrentWorld()
                && !internUser.hasPermission(Permission.BAN_MODERATOR) && !this.hasPermission(Permission.BAN_MODERATOR)
                && (!internUser.hasPermission(Permission.BAN_USER) || this.hasPermission(Permission.BAN_USER));
    }

    @Override
    public boolean canBeMuted() {
        InternUser internUser = UserManager.getInstance().getInternUser();
        return this.isOnline() && internUser.getCurrentWorld() != null && this.isInCurrentWorld()
                && internUser.hasPermission(Permission.MUTE) && !this.hasPermission(Permission.MUTE);
    }

    @Override
    public boolean canBeBanned() {
        InternUser internUser = UserManager.getInstance().getInternUser();
        return internUser.getCurrentWorld() != null && !this.hasPermission(Permission.BAN_MODERATOR)
                && (internUser.hasPermission(Permission.BAN_MODERATOR) || internUser.hasPermission(Permission.BAN_USER)
                && !this.hasPermission(Permission.BAN_USER));
    }

    @Override
    public boolean canAssignModerator() {
        InternUser internUser = UserManager.getInstance().getInternUser();
        return internUser.getCurrentWorld() != null && internUser.hasPermission(Permission.ASSIGN_MODERATOR)
                && !this.hasRole(Role.ADMINISTRATOR) && !this.hasRole(Role.OWNER) && !this.hasRole(Role.BOT)
                && !this.isBanned();
    }

    @Override
    public boolean canCommunicateWith() {
        return canCommunicateWith;
    }

    @Override
    public boolean canTalk() {
        return canCommunicateWith && currentLocation != null
                && currentLocation.getArea().getCommunicationMedia().contains(CommunicationMedium.VOICE);
    }

    @Override
    public boolean canShow() {
        return canCommunicateWith && currentLocation != null
                && currentLocation.getArea().getCommunicationMedia().contains(CommunicationMedium.VIDEO);
    }

    @Override
    public boolean canShare() {
        return canShow() && hasPermission(Permission.SHARE_SCREEN);
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
        InternUser internUser = UserManager.getInstance().getInternUser();
        if (currentWorld == null || internUser.currentWorld == null) {
            return false;
        }
        return currentWorld.equals(internUser.currentWorld);
    }

    @Override
    public boolean isInCurrentRoom() {
        InternUser internUser = UserManager.getInstance().getInternUser();
        if (!isInCurrentWorld() || currentRoom == null || internUser.currentRoom == null) {
            return false;
        }
        return currentRoom.equals(internUser.currentRoom) && currentLocation != null;
    }

    @Override
    public @Nullable ISpatialContextView getCurrentWorld() {
        return currentWorld;
    }

    @Override
    public @Nullable ISpatialContextView getCurrentRoom() {
        return currentRoom;
    }

    @Override
    public @Nullable Location getLocation() {
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
    public boolean isMovable() {
        return isMovable;
    }

    @Override
    public boolean hasRole(@NotNull final Role role) {
        return hasRole(UserManager.getInstance().getInternUser().getDeepestContext(), role);
    }

    @Override
    public boolean hasPermission(@NotNull final Permission permission) {
        return hasPermission(UserManager.getInstance().getInternUser().getDeepestContext(), permission);
    }

    @Override
    public @Nullable Role getHighestRole() {
        return hasRole(Role.OWNER) ? Role.OWNER
                : (hasRole(Role.ADMINISTRATOR) ? Role.ADMINISTRATOR
                : (hasRole(Role.MODERATOR) ? Role.MODERATOR
                : (hasRole(Role.ROOM_OWNER) ? Role.ROOM_OWNER
                : (hasRole(Role.AREA_MANAGER) ? Role.AREA_MANAGER
                : (hasRole(Role.BOT) ? Role.BOT
                : null)))));
    }

    /**
     * Gibt den untergeordnetsten Kontext zurück, in dem sich der Benutzer befindet.
     * @return Untergeordnetster Kontext, in dem sich der Benutzer befindet.
     */
    public @NotNull Context getDeepestContext() {
        if (status == Status.OFFLINE) {
            throw new IllegalStateException("User is not online");
        }

        return UserManager.getInstance().getInternUser().getCurrentWorld() == null || !isInCurrentWorld() ? Context.getGlobal()
                : (UserManager.getInstance().getInternUser().getCurrentRoom() == null || !isInCurrentRoom()
                    || currentLocation == null ? UserManager.getInstance().getInternUser().getCurrentWorld()
                : currentLocation.getArea());
    }

    /**
     * Veranlasst das Löschen aller Informationen zu Kontexten innerhalb der Welt des intern angemeldeten Benutzers.
     */
    public void discardWorldInfo() {
        reportedContexts.values().removeIf(context -> !context.equals(Context.getGlobal()));
        mutedContexts.values().removeIf(context -> !context.equals(Context.getGlobal()));
        bannedContexts.values().removeIf(context -> !context.equals(Context.getGlobal()));
        contextRoles.keySet().removeIf(context -> !context.equals(Context.getGlobal()));
        currentRoom = null;
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
        currentLocation = null;
    }

    /**
     * Überprüft, ob dieser Benutzer dem aktuell angemeldeten internen Benutzer noch bekannt ist.
     * @return true, wenn der Benutzer noch bekannt ist, sonst false.
     */
    public boolean isKnown() {
        return isInCurrentWorld() || isFriend
                || isBanned() && (UserManager.getInstance().getInternUser().hasPermission(Permission.BAN_USER)
                || UserManager.getInstance().getInternUser().hasPermission(Permission.BAN_MODERATOR));
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
     * Überprüft, ob der Benutzer eine Berechtigung in einem Kontext besitzt.
     * @param context Zu überprüfender Kontext.
     * @param permission Zu überprüfende Berechtigung.
     * @return true, wenn der Benutzer die Berechtigung in dem Kontext besitzt, sonst false.
     */
    protected boolean hasPermission(@NotNull final Context context, @NotNull final Permission permission) {
        return contextRoles.containsKey(context)
                && contextRoles.get(context).stream().anyMatch(role -> role.hasPermission(permission))
                || context.getParent() != null && hasPermission(context.getParent(), permission);
    }

    /**
     * Überprüft, ob der Benutzer eine Rolle in einem Kontext besitzt.
     * @param context Zu überprüfender Kontext.
     * @param role Zu überprüfende Rolle.
     * @return true, wenn der Benutzer die Rolle in dem Kontext besitzt, sonst false.
     */
    private boolean hasRole(@NotNull final Context context, @NotNull final Role role) {
        return contextRoles.containsKey(context) && contextRoles.get(context).contains(role)
                || context.getParent() != null && hasRole(context.getParent(), role);
    }
}