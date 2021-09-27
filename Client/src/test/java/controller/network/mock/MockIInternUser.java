package controller.network.mock;

import model.MessageBundle;
import model.context.ContextID;
import model.context.spatial.ContextMap;
import model.context.spatial.ContextMusic;
import model.context.spatial.Direction;
import model.exception.ContextNotFoundException;
import model.exception.NotificationNotFoundException;
import model.notification.NotificationType;
import model.role.Role;
import model.user.Avatar;
import model.user.IInternUserController;
import model.user.Status;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class MockIInternUser implements IInternUserController {

    private final Set<String> calls;

    private final UUID userId;
    private final String username;
    private Avatar avatar;
    private Status status;
    private ContextID world;

    public MockIInternUser(@NotNull final UUID userId, @NotNull final String username,
                           @NotNull final Status status, @NotNull final Avatar avatar) {
        this.calls = new HashSet<>();
        this.userId = userId;
        this.username = username;
        this.avatar = avatar;
        this.status = status;
    }

    public @NotNull UUID getUserId() {
        return this.userId;
    }

    public @Nullable ContextID getWorld() {
        return this.world;
    }

    private boolean unknownRoom;

    @Override
    public void joinRoom(@NotNull final ContextID roomId, @NotNull final String roomName,
                         @NotNull final ContextMap map) throws ContextNotFoundException {
        this.calls.add("join-room");

        if (this.unknownRoom) {
            throw new ContextNotFoundException("Mocked ContextNotFoundException", roomId);
        }
    }

    public void joinRoom(final boolean throwUnknown) {
        this.unknownRoom = throwUnknown;
    }

    private boolean unknownNotificationContext;

    @Override
    public void addNotification(@NotNull final ContextID contextId, @NotNull final UUID notificationId, @NotNull MessageBundle messageBundle, @NotNull LocalDateTime timestamp, @NotNull NotificationType type, boolean isRead, boolean isAccepted, boolean isDeclined) throws ContextNotFoundException {
        this.calls.add("add-notification");

        if (this.unknownNotificationContext) {
            throw new ContextNotFoundException("Mocked ContextNotFoundException", contextId);
        }
    }

    public void addNotification(final boolean throwUnknown) {
        this.unknownNotificationContext = throwUnknown;
    }

    private boolean unknownNotification;

    @Override
    public void updateNotification(@NotNull final UUID notificationId, final boolean isRead,
                                   final boolean isAccepted, final boolean isDeclined) throws NotificationNotFoundException {
        this.calls.add("update-notification");

        if (this.unknownNotification) {
            throw new NotificationNotFoundException("Mocked NotificationNotFoundException", notificationId);
        }
    }

    public void updateNotification(final boolean throwUnknown) {
        this.unknownNotification = throwUnknown;
    }

    @Override
    public void removeNotification(@NotNull final UUID notificationId) throws NotificationNotFoundException {
        this.calls.add("remove-notification");

        if (this.unknownNotification) {
            throw new NotificationNotFoundException("Mocked NotificationNotFoundException", notificationId);
        }
    }

    @Override
    public void setUsername(@NotNull final String username) {

    }

    @Override
    public void setStatus(@NotNull final Status status) {
        this.calls.add("set-status");
        this.status = status;
    }

    @Override
    public void setAvatar(@NotNull final Avatar avatar) {
        this.calls.add("set-avatar");
        this.avatar = avatar;
    }

    private boolean unknownWorld;

    @Override
    public void joinWorld(@NotNull final ContextID worldId) throws ContextNotFoundException {
        this.calls.add("join-world");
        this.world = worldId;

        if (this.unknownWorld) {
            throw new ContextNotFoundException("Mocked ContextNotFoundException", worldId);
        }
    }

    public void joinWorld(final boolean throwUnknown) {
        this.unknownWorld = throwUnknown;
    }

    @Override
    public void leaveWorld() {
        this.calls.add("leave-world");
        this.world = null;
    }

    @Override
    public void joinRoom(@NotNull final ContextID roomId) throws ContextNotFoundException {
        this.calls.add("join-room");
    }

    @Override
    public void leaveRoom() {
        this.calls.add("leave-room");
    }

    @Override
    public void setInPrivateRoom(boolean isInPrivateRoom) {

    }

    @Override
    public void setFriend(final boolean isFriend) {
        this.calls.add("set-friend");
    }

    @Override
    public void setIgnored(final boolean isIgnored) {
        this.calls.add("set-ignored");
    }

    @Override
    public void setCommunicable(final boolean canCommunicateWith) {
        this.calls.add("set-communicable");
    }

    @Override
    public void setMovable(final boolean isMovable) {
        this.calls.add("set-movable");
    }

    private boolean unknownContext;

    @Override
    public void setMusic(@NotNull final ContextID spatialId, @Nullable final ContextMusic music,
                         final boolean looping, final boolean random) throws ContextNotFoundException {
        this.calls.add("set-music");

        if (this.unknownContext) {
            throw new ContextNotFoundException("Mocked ContextNotFoundException", spatialId);
        }
    }

    @Override
    public void setReport(@NotNull final ContextID contextId, final boolean isReported) throws ContextNotFoundException {
        this.calls.add("set-report");

        if (this.unknownContext) {
            throw new ContextNotFoundException("Mocked ContextNotFoundException", contextId);
        }
    }

    @Override
    public void setMute(@NotNull final ContextID contextId, final boolean isMuted) throws ContextNotFoundException {
        this.calls.add("set-mute");

        if (this.unknownContext) {
            throw new ContextNotFoundException("Mocked ContextNotFoundException", contextId);
        }
    }

    @Override
    public void setBan(@NotNull final ContextID contextId, final boolean isBanned) throws ContextNotFoundException {
        this.calls.add("set-ban");

        if (this.unknownContext) {
            throw new ContextNotFoundException("Mocked ContextNotFoundException", contextId);
        }
    }

    @Override
    public void setRoles(@NotNull final ContextID contextId, @NotNull final Set<Role> roles) throws ContextNotFoundException {
        this.calls.add("set-roles");

        if (this.unknownContext) {
            throw new ContextNotFoundException("Mocked ContextNotFoundException", contextId);
        }
    }

    public void setContext(final boolean throwUnknown) {
        this.unknownContext = throwUnknown;
    }

    @Override
    public void setLocation(final float posX, final float posY, final boolean isTeleporting, final boolean isSprinting,
                            @NotNull final Direction direction) {
        this.calls.add("set-location");
    }

    public boolean called(@NotNull final String method) {
        return this.calls.contains(method);
    }

    public void reset() {
        this.calls.clear();
    }
}
