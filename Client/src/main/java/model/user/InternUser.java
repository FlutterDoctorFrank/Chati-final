package model.user;

import model.MessageBundle;
import model.communication.CommunicationMedium;
import model.context.Context;
import model.context.ContextID;
import model.context.spatial.*;
import model.exception.ContextNotFoundException;
import model.exception.NotificationNotFoundException;
import model.notification.INotificationView;
import model.notification.Notification;
import model.notification.NotificationType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Eine Klasse, welche den am Client angemeldeten Benutzer repräsentiert.
 */
public class InternUser extends User implements IInternUserController, IInternUserView {

    /** Die Welt, in der sich der interne Benutzer befindet. */
    private SpatialContext currentWorld;

    /** Der Raum, in dem sich der interne Benutzer befindet. */
    private SpatialContext currentRoom;

    /** Die Benachrichtigungen des internen Benutzers. */
    private final Map<UUID, Notification> notifications;

    /** Die Musik, die gerade abgespielt werden soll. */
    private ContextMusic music;

    /**
     * Erzeugt eine neue Instanz des intern angemeldeten Benutzers.
     * @param userId ID des Benutzers.
     * @param username Name des Benutzers.
     * @param status Status des Benutzers.
     * @param avatar Avatar des Benutzers.
     */
    public InternUser(@NotNull final UUID userId, @NotNull final String username,
                      @NotNull final Status status, @NotNull final Avatar avatar) {
        super(userId, username, status, avatar);
        this.currentWorld = null;
        this.currentRoom = null;
        this.notifications = new HashMap<>();
        this.music = null;
    }

    @Override
    public void joinWorld(@NotNull final String worldName) {
        this.currentWorld = new SpatialContext(worldName, Context.getGlobal());
        this.isInCurrentWorld = true;
        UserManager.getInstance().getModelObserver().setWorldChanged();
    }

    @Override
    public void leaveWorld() {
        if (currentWorld == null) {
            throw new IllegalStateException("User is not in a world.");
        }
        UserManager.getInstance().discardWorldInfo();
        notifications.values().removeIf(notification -> !notification.getContext().equals(Context.getGlobal()));
        Context.getGlobal().removeChild(currentWorld);
        currentRoom = null;
        currentWorld = null;
        music = null;
        UserManager.getInstance().getModelObserver().setWorldChanged();
    }

    @Override
    public void joinRoom(@NotNull final ContextID roomId, @NotNull final String roomName, @NotNull final ContextMap map) {
        if (currentWorld == null) {
            throw new IllegalStateException("User is not in world.");
        }
        // Entferne alten Raum aus der Kontexthierarchie.
        if (currentRoom != null) {
            leaveRoom();
        }
        this.currentRoom = new SpatialContext(roomName, currentWorld);
        this.currentWorld.addChild(this.currentRoom);
        this.isInCurrentRoom = true;
        this.currentRoom.build(map);
    }

    @Override
    public void setMusic(@NotNull final ContextID spatialId, @Nullable final ContextMusic music) throws ContextNotFoundException {
        Context current = getDeepestContext();
        while (current != null && !current.getContextId().equals(spatialId)) {
            current = current.getParent();
        }
        if (current == null) {
            throw new ContextNotFoundException("User is not in a context with this ID.", spatialId);
        }
        this.music = music;
        UserManager.getInstance().getModelObserver().setMusicChanged();
    }

    @Override
    public void addNotification(@NotNull final ContextID contextId, @NotNull final UUID notificationId,
                                @NotNull final MessageBundle messageBundle, @NotNull final LocalDateTime timestamp,
                                @NotNull final NotificationType type, final boolean isRead,
                                final boolean isAccepted, final boolean isDeclined) throws ContextNotFoundException {
        if (notifications.containsKey(notificationId)) {
            throw new IllegalArgumentException("There is already a notification with this ID.");
        }
        notifications.put(notificationId, new Notification(notificationId, Context.getGlobal().getContext(contextId),
                messageBundle, timestamp, type, isRead, isAccepted, isDeclined));
        UserManager.getInstance().getModelObserver().setNewNotificationReceived();
    }

    @Override
    public void updateNotification(@NotNull final UUID notificationId, final boolean isRead,
                                   final boolean isAccepted, final boolean isDeclined) throws NotificationNotFoundException {
        Notification notification = notifications.get(notificationId);
        if (notification == null) {
            throw new NotificationNotFoundException("There is no notification with this ID.", notificationId);
        }
        if (isRead) {
            notification.setRead();
        }
        if (isAccepted) {
            notification.setAccepted();
        }
        if (isDeclined) {
            notification.setDeclined();
        }
        UserManager.getInstance().getModelObserver().setUserNotificationChanged();
    }

    @Override
    public void removeNotification(@NotNull final UUID notificationId) throws NotificationNotFoundException {
        if (notifications.remove(notificationId) == null) {
            throw new NotificationNotFoundException("There is no notification with this ID.", notificationId);
        }
        UserManager.getInstance().getModelObserver().setUserNotificationChanged();
    }

    @Override
    public @Nullable SpatialContext getCurrentWorld() {
        return currentWorld;
    }

    @Override
    public @Nullable SpatialContext getCurrentRoom() {
        return currentRoom;
    }

    @Override
    public @NotNull Map<UUID, INotificationView> getGlobalNotifications() {
        return notifications.values().stream()
                .filter(notification -> notification.getContext().equals(Context.getGlobal()))
                .collect(Collectors.toUnmodifiableMap(Notification::getNotificationId, Function.identity()));
    }

    @Override
    public @NotNull Map<UUID, INotificationView> getWorldNotifications() {
        return notifications.values().stream()
                .filter(notification -> notification.getContext().equals(currentWorld))
                .collect(Collectors.toUnmodifiableMap(Notification::getNotificationId, Function.identity()));
    }

    @Override
    public @Nullable ContextMusic getMusic() {
        return music;
    }

    @Override
    public boolean canTalk() {
        return currentWorld != null && currentRoom != null && currentLocation != null
                && currentLocation.getArea().getCommunicationMedia().contains(CommunicationMedium.VOICE)
                && !isMuted() && !UserManager.getInstance().getCommunicableUsers().isEmpty();
    }

    public @Nullable ISpatialContextView getCurrentInteractable() {
        float posX = currentLocation.getPosX();
        float posY = currentLocation.getPosY();
        List<SpatialContext> interactables = currentRoom.getDescendants().values().stream()
                .filter(context -> context.getExpanse().isAround(posX, posY, SpatialContext.INTERACTION_DISTANCE)
                && context.isInteractable()).collect(Collectors.toList());
        if (interactables.isEmpty()) {
            return null;
        }
        if (interactables.size() == 1) {
            return interactables.get(0);
        }
        for (SpatialContext interactable : interactables) {
            if (isInDirection(interactable)) {
                return interactable;
            }
        }
        return null;
    }

    /**
     * Entfernt alle Referenzen auf einen verlassen (privaten) Raum.
     */
    public void leaveRoom() {
        // Entferne alle Referenzen auf den Raum und allen untergeordneten Kontexten.
        UserManager.getInstance().discardRoomInfo();
        music = null;
        currentLocation = null;
        currentRoom = null;
    }

    /**
     * Gibt zurück, ob sich ein Kontext in der Blickrichtung des Benutzers befindet.
     * @param interactable Zu überprüfender Kontext.
     * @return true, wenn sich der Kontext in der Blickrichtung befindet, sonst false.
     */
    private boolean isInDirection(SpatialContext interactable) {
        switch (currentLocation.getDirection()) {
            case UP:
                return currentLocation.getPosY() <= interactable.getExpanse().getCenter().getPosY();
            case LEFT:
                return currentLocation.getPosX() >= interactable.getExpanse().getCenter().getPosX();
            case DOWN:
                return currentLocation.getPosY() >= interactable.getExpanse().getCenter().getPosY();
            case RIGHT:
                return currentLocation.getPosX() <= interactable.getExpanse().getCenter().getPosX();
            default:
                return false;
        }
    }
}