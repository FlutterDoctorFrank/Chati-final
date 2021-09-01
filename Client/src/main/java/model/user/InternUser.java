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
import java.time.LocalDateTime;
import java.util.HashMap;
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

    /** Information, ob eine neue Benachrichtigung erhalten wurde. */
    private boolean receivedNewNotification;

    /** Die Musik, die gerade abgespielt werden soll. */
    private ContextMusic music;

    /**
     * Erzeugt eine neue Instanz des intern angemeldeten Benutzers.
     * @param userId ID des Benutzers.
     * @param username Name des Benutzers.
     * @param status Status des Benutzers.
     * @param avatar Avatar des Benutzers.
     */
    public InternUser(UUID userId, String username, Status status, Avatar avatar) {
        super(userId, username, status, avatar);
        this.currentWorld = null;
        this.currentRoom = null;
        this.notifications = new HashMap<>();
        this.receivedNewNotification = false;
        this.music = null;
    }

    @Override
    public void joinWorld(String worldName) {
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
    public void joinRoom(ContextID roomId, String roomName, ContextMap map) {
        if (currentWorld == null) {
            throw new IllegalStateException("User is not in world.");
        }
        // Entferne alten Raum aus der Kontexthierarchie.
        if (currentRoom != null) {
            leaveRoom();
        }
        this.currentRoom = new SpatialContext(roomName, currentWorld);
        this.isInCurrentRoom = true;
        this.currentRoom.build(map);
    }

    @Override
    public void setMusic(ContextID spatialId, ContextMusic music) throws ContextNotFoundException {
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
    public void addNotification(ContextID contextId, UUID notificationId, MessageBundle messageBundle,
                LocalDateTime timestamp, NotificationType type, boolean isRead, boolean isAccepted, boolean isDeclined)
                throws ContextNotFoundException {
        if (notifications.containsKey(notificationId)) {
            throw new IllegalArgumentException("There is already a notification with this ID.");
        }
        notifications.put(notificationId, new Notification(notificationId, Context.getGlobal().getContext(contextId),
                messageBundle, timestamp, type, isRead, isAccepted, isDeclined));
        receivedNewNotification = true;
        UserManager.getInstance().getModelObserver().setUserNotificationChanged();
    }

    @Override
    public void updateNotification(UUID notificationId, boolean isRead, boolean isAccepted, boolean isDeclined)
                throws NotificationNotFoundException {
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
    public void removeNotification(UUID notificationId) throws NotificationNotFoundException {
        if (notifications.remove(notificationId) == null) {
            throw new NotificationNotFoundException("There is no notification with this ID.", notificationId);
        }
        UserManager.getInstance().getModelObserver().setUserNotificationChanged();
    }

    @Override
    public SpatialContext getCurrentWorld() {
        return currentWorld;
    }

    @Override
    public SpatialContext getCurrentRoom() {
        return currentRoom;
    }

    @Override
    public Map<UUID, INotificationView> getGlobalNotifications() {
        return notifications.values().stream()
                .filter(notification -> notification.getContext().equals(Context.getGlobal()))
                .collect(Collectors.toUnmodifiableMap(Notification::getNotificationId, Function.identity()));
    }

    @Override
    public Map<UUID, INotificationView> getWorldNotifications() {
        return notifications.values().stream()
                .filter(notification -> notification.getContext().equals(currentWorld))
                .collect(Collectors.toUnmodifiableMap(Notification::getNotificationId, Function.identity()));
    }

    @Override
    public boolean receivedNewNotification() {
        boolean temp = receivedNewNotification;
        receivedNewNotification = false;
        return temp;
    }

    @Override
    public ContextMusic getMusic() {
        return music;
    }

    @Override
    public boolean canTalk() {
        return currentWorld != null && currentRoom != null && currentLocation != null
                && currentLocation.getArea().getCommunicationMedia().contains(CommunicationMedium.VOICE)
                && !isMuted() && !UserManager.getInstance().getCommunicableUsers().isEmpty();
    }

    @Override
    public Map<ContextID, ISpatialContextView> getCurrentInteractables() {
        float posX = currentLocation.getPosX();
        float posY = currentLocation.getPosY();
        return currentRoom.getDescendants().values().stream()
                .filter(context -> context.getExpanse().isAround(posX, posY, SpatialContext.INTERACTION_DISTANCE)
                && context.isInteractable()).collect(Collectors.toUnmodifiableMap(SpatialContext::getContextId, Function.identity()));
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
}