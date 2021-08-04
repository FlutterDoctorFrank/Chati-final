package model.user;

import model.MessageBundle;
import model.context.Context;
import model.context.ContextID;
import model.context.spatial.Music;
import model.context.spatial.SpatialContext;
import model.context.spatial.SpatialMap;
import model.exception.ContextNotFoundException;
import model.exception.NotificationNotFoundException;
import model.notification.INotificationView;
import model.notification.Notification;

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

    /** Die Benachrichtigungen des Benutzers. */
    private final Map<UUID, Notification> notifications;

    /** Die Musik, die gerade abgespielt werden soll. */
    private Music music;

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
        this.music = null;
    }

    @Override
    public void setWorld(ContextID worldId, String worldName) {
        this.currentWorld = new SpatialContext(worldId, worldName, Context.getGlobal());
        this.isInCurrentWorld = true;
    }

    @Override
    public void setRoom(ContextID roomId, String roomName, SpatialMap map) {
        if (currentWorld == null) {
            throw new IllegalStateException("User is not in world.");
        }
        // Entferne alten Raum aus der Kontexthierarchie, sofern er nicht die Welt selbst ist.
        if (currentRoom != null && !currentWorld.equals(currentRoom)) {
            currentWorld.removeChild(currentRoom);
        }
        // Überprüfe, ob zu betretender Raum der Welt entspricht, sonst erzeuge neuen Raum.
        if (currentWorld.getContextId().equals(roomId)) {
            this.currentRoom = currentWorld;
        } else {
            this.currentRoom = new SpatialContext(roomId, roomName, currentWorld);
        }
        // Erzeuge die Kontexthierarchie des Raums anhand der Karte.
        this.currentRoom.buildContextTree(map);
        this.isInCurrentRoom = true;
    }

    @Override
    public void setMusic(ContextID spatialId, Music music) throws ContextNotFoundException {
        Context current = currentRoom.getArea(currentLocation.getPosX(), currentLocation.getPosY());
        do {
            if (!current.getContextId().equals(spatialId)) {
                throw new ContextNotFoundException("User is not in a context with this ID.", spatialId);
            }
            current = current.getParent();
        } while (current != null);
        this.music = music;
    }

    @Override
    public void addNotification(ContextID contextId, UUID notificationId, MessageBundle messageBundle,
                                LocalDateTime timestamp, boolean isRequest)
            throws ContextNotFoundException {
        if (notifications.containsKey(notificationId)) {
            throw new IllegalArgumentException("There is already a notification with this ID.");
        }
        notifications.put(notificationId, new Notification(notificationId,
                Context.getGlobal().getContext(contextId), messageBundle, timestamp, isRequest));
        UserManager.getInstance().getModelObserver().setUserNotificationChanged();
    }

    @Override
    public void removeNotification(UUID notificationId) throws NotificationNotFoundException {
        if(notifications.remove(notificationId) == null){
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
    public Music getMusic() {
        return music;
    }
}