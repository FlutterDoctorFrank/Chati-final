package controller.network.mock;

import model.context.ContextID;
import model.context.global.IGlobalContext;
import model.context.spatial.Direction;
import model.context.spatial.ILocation;
import model.context.spatial.IWorld;
import model.exception.ContextNotFoundException;
import model.exception.IllegalAdministrativeActionException;
import model.exception.IllegalInteractionException;
import model.exception.IllegalMenuActionException;
import model.exception.IllegalNotificationActionException;
import model.exception.IllegalPositionException;
import model.exception.IllegalWorldActionException;
import model.exception.NoPermissionException;
import model.exception.NotificationNotFoundException;
import model.exception.UserNotFoundException;
import model.notification.INotification;
import model.notification.NotificationAction;
import model.role.IContextRole;
import model.role.Permission;
import model.user.AdministrativeAction;
import model.user.Avatar;
import model.user.IUser;
import model.user.Status;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mockito.Mockito;
import util.RandomTest;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class MockIUser implements IUser {

    private final IGlobalContext global;

    private final Set<String> calls;
    private final String name;
    private final UUID userId;

    public MockIUser(@NotNull final IGlobalContext global) {
        this.global = global;
        this.calls = new HashSet<>();
        this.name = RandomTest.randomString();
        this.userId = RandomTest.randomUniqueId();
        this.avatar = RandomTest.randomEnum(Avatar.class);
        this.status = RandomTest.randomEnum(Status.class);
        this.role = Mockito.mock(IContextRole.class);
    }

    private boolean unknownWorld;
    private boolean illegalWorld;

    @Override
    public void joinWorld(@NotNull final ContextID worldId) throws ContextNotFoundException, IllegalWorldActionException {
        this.calls.add("join-world");

        if (this.unknownWorld) {
            throw new ContextNotFoundException("", worldId);
        }

        if (this.illegalWorld) {
            throw new IllegalWorldActionException("", "");
        }
    }

    public void joinWorld(final boolean throwUnknown, final boolean throwIllegal) {
        this.unknownWorld = throwUnknown;
        this.illegalWorld = throwIllegal;
    }

    @Override
    public void leaveWorld() throws IllegalStateException {
        this.calls.add("leave-world");
    }

    @Override
    public void move(@NotNull final Direction direction, final float posX, final float posY, final boolean isSprinting) throws IllegalPositionException {
        this.calls.add("move");

        if (posX < 0 || posY < 0) {
            if (this.location != null) {
                Mockito.when(this.location.getDirection()).thenReturn(Direction.UP);
                Mockito.when(this.location.getPosX()).thenReturn(0.0f);
                Mockito.when(this.location.getPosY()).thenReturn(0.0f);
            }

            throw new IllegalPositionException("Invalid Position Test", this, posX, posY);
        }

        if (this.location != null) {
            Mockito.when(this.location.getDirection()).thenReturn(direction);
            Mockito.when(this.location.getPosX()).thenReturn(posX);
            Mockito.when(this.location.getPosY()).thenReturn(posY);
        }
    }

    @Override
    public void type() {
        this.calls.add("type");
    }

    @Override
    public void chat(@NotNull final String message, final byte[] imageData, @Nullable final String imageName) {
        this.calls.add("chat");
    }

    @Override
    public void talk(final byte[] voiceData) {
        this.calls.add("talk");
    }

    @Override
    public void show(byte[] frameData, int number) {
        this.calls.add("show");
    }

    private boolean unknownUser;
    private boolean illegalUserAction;
    private boolean invalidPermission;

    @Override
    public void executeAdministrativeAction(@NotNull final UUID targetId, @NotNull final AdministrativeAction action,
                                            @NotNull final String[] args) throws UserNotFoundException, IllegalAdministrativeActionException, NoPermissionException {
        this.calls.add("execute-administrative-action");

        if (this.unknownUser) {
            throw new UserNotFoundException("", targetId);
        }

        if (this.illegalUserAction) {
            throw new IllegalAdministrativeActionException("", this, Mockito.mock(IUser.class), action);
        }

        if (this.invalidPermission) {
            throw new NoPermissionException("", "", this, RandomTest.randomEnum(Permission.class));
        }
    }

    public void executeAdministrativeAction(final boolean throwUnknown, final boolean throwIllegal, final boolean throwPermission) {
        this.unknownUser = throwUnknown;
        this.illegalUserAction = throwIllegal;
        this.invalidPermission = throwPermission;
    }

    private boolean unknownContext;
    private boolean illegalContext;

    @Override
    public void interact(@NotNull final ContextID interactableId) throws IllegalInteractionException, ContextNotFoundException {
        this.calls.add("interact");

        if (this.unknownContext) {
            throw new ContextNotFoundException("", interactableId);
        }

        if (this.illegalContext) {
            throw new IllegalInteractionException("", this);
        }
    }

    public void interact(final boolean throwUnknown, final boolean throwIllegal) {
        this.unknownContext = throwUnknown;
        this.illegalContext = throwIllegal;
    }

    @Override
    public void executeOption(@NotNull final ContextID interactableId, final int menuOption, @NotNull final String[] args)
            throws ContextNotFoundException, IllegalInteractionException, IllegalMenuActionException {
        this.calls.add("execute-option");

        if (this.unknownContext) {
            throw new ContextNotFoundException("", interactableId);
        }

        if (this.illegalContext) {
            throw new IllegalInteractionException("", this);
        }

        if (menuOption < 0) {
            throw new IllegalMenuActionException("", "");
        }
    }

    public void executeOption(final boolean throwUnknown, final boolean throwIllegal) {
        this.unknownContext = throwUnknown;
        this.illegalContext = throwIllegal;
    }

    private boolean unknownNotification;
    private boolean illegalAction;

    @Override
    public void manageNotification(@NotNull final UUID notificationId, @NotNull final NotificationAction action)
            throws NotificationNotFoundException, IllegalNotificationActionException {
        this.calls.add("manage-notification");

        if (this.unknownNotification) {
            throw new NotificationNotFoundException("", this, notificationId);
        }

        if (this.illegalAction) {
            throw new IllegalNotificationActionException("", this, Mockito.mock(INotification.class), false);
        }
    }

    public void manageNotification(final boolean throwUnknown, final boolean throwIllegal) {
        this.unknownNotification = throwUnknown;
        this.illegalAction = throwIllegal;
    }

    @Override
    public @NotNull UUID getUserId() {
        return this.userId;
    }

    @Override
    public @NotNull String getUsername() {
        return this.name;
    }

    private Status status;

    @Override
    public @NotNull Status getStatus() {
        return this.status;
    }

    @Override
    public void setStatus(@NotNull final Status status) {
        this.calls.add("set-status");
        this.status = status;
    }

    private Avatar avatar;

    @Override
    public @NotNull Avatar getAvatar() {
        return this.avatar;
    }

    @Override
    public void setAvatar(@NotNull final Avatar avatar) {
        this.calls.add("set-avatar");
        this.avatar = avatar;
    }

    private IWorld world;

    @Override
    public @Nullable IWorld getWorld() {
        return this.world;
    }

    public void setWorld(@Nullable final IWorld world) {
        this.world = world;
    }

    private ILocation location;

    @Override
    public @Nullable ILocation getLocation() {
        return this.location;
    }

    public void setLocation(@Nullable final ILocation location) {
        this.location = location;
    }

    @Override
    public boolean isSprinting() {
        return false;
    }

    @Override
    public boolean isMovable() {
        return false;
    }

    private Map<UUID, IUser> friends;

    @Override
    public @NotNull Map<UUID, IUser> getFriends() {
        return this.friends != null ? friends : Collections.emptyMap();
    }

    public void setFriends(@Nullable final Map<UUID, IUser> friends) {
        this.friends = friends;
    }

    @Override
    public @NotNull Map<UUID, IUser> getIgnoredUsers() {
        return Collections.emptyMap();
    }

    @Override
    public @NotNull Map<UUID, IUser> getCommunicableIUsers() {
        return Collections.emptyMap();
    }

    private final IContextRole role;

    @Override
    public @NotNull IContextRole getGlobalRoles() {
        Mockito.when(this.role.getRoles()).thenReturn(Collections.emptySet());
        Mockito.when(this.role.getContext()).thenReturn(this.global);
        Mockito.when(this.role.getUser()).thenReturn(this);

        return this.role;
    }

    private Map<UUID, INotification> notifications;

    @Override
    public @NotNull Map<UUID, INotification> getGlobalNotifications() {
        return this.notifications != null ? notifications : Collections.emptyMap();
    }

    public void setGlobalNotifications(@Nullable final Map<UUID, INotification> notifications) {
        this.notifications = notifications;
    }

    public boolean called(@NotNull final String method) {
        return this.calls.contains(method);
    }

    public void reset() {
        this.calls.clear();
    }
}
