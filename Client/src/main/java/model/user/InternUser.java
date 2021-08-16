package model.user;

import com.badlogic.gdx.Gdx;
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
    public void joinWorld(ContextID worldId, String worldName) {
        this.currentWorld = new SpatialContext(worldId, worldName, Context.getGlobal());
        Context.getGlobal().addChild(currentWorld);
        this.isInCurrentWorld = true;
        UserManager.getInstance().getModelObserver().setWorldChanged();
    }

    @Override
    public void leaveWorld() {
        if (currentWorld == null) {
            throw new IllegalStateException("User is not in a world.");
        }
        // Entferne alle Referenzen auf die Welt und alle untergeordneten Kontexte.
        reportedContexts.values().removeIf(context -> !context.equals(Context.getGlobal()));
        mutedContexts.values().removeIf(context -> !context.equals(Context.getGlobal()));
        bannedContexts.values().removeIf(context -> !context.equals(Context.getGlobal()));
        contextRoles.keySet().removeIf(context -> !context.equals(Context.getGlobal()));
        notifications.values().removeIf(notification -> !notification.getContext().equals(Context.getGlobal()));
        Context.getGlobal().removeChild(currentWorld);
        currentLocation = null;
        currentRoom = null;
        isInCurrentRoom = false;
        currentWorld = null;
        isInCurrentWorld = false;
        music = null;
        UserManager.getInstance().getModelObserver().setWorldChanged();
    }

    @Override
    public void joinRoom(ContextID roomId, String roomName, SpatialMap map) {
        if (currentWorld == null) {
            throw new IllegalStateException("User is not in world.");
        }
        currentLocation = null;
        music = null;
        // Entferne alten Raum aus der Kontexthierarchie, sofern er nicht die Welt selbst ist.
        if (currentRoom != null && !currentWorld.equals(currentRoom)) {
            leaveRoom();
        }
        // Überprüfe, ob zu betretender Raum der Welt entspricht, sonst erzeuge neuen Raum.
        if (currentWorld.getContextId().equals(roomId)) {
            this.currentRoom = currentWorld;
        } else {
            this.currentRoom = new SpatialContext(roomId, roomName, currentWorld);
            currentWorld.addChild(currentRoom);
        }

        Gdx.app.postRunnable(() -> {
            currentRoom.build(map);
        });
        isInCurrentRoom = true;
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
        UserManager.getInstance().getModelObserver().setMusicChanged();
    }

    @Override
    public void addNotification(ContextID contextId, UUID notificationId, MessageBundle messageBundle,
                                LocalDateTime timestamp, NotificationType type)
            throws ContextNotFoundException {
        if (notifications.containsKey(notificationId)) {
            throw new IllegalArgumentException("There is already a notification with this ID.");
        }
        notifications.put(notificationId, new Notification(notificationId,
                Context.getGlobal().getContext(contextId), messageBundle, timestamp, type));
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

    @Override
    public boolean isInPrivateRoom() {
        return currentWorld != null && currentRoom != null && !currentWorld.equals(currentRoom);
    }

    /**
     * Entfernt alle Referenzen auf einen verlassen (privaten) Raum.
     */
    public void leaveRoom() {
        // Entferne alle Referenzen auf den Raum und allen untergeordneten Kontexten.
        reportedContexts.values()
                .removeIf(context -> !context.equals(Context.getGlobal()) || !context.equals(currentWorld));
        mutedContexts.values()
                .removeIf(context -> !context.equals(Context.getGlobal()) || !context.equals(currentWorld));
        bannedContexts.values()
                .removeIf(context -> !context.equals(Context.getGlobal()) || !context.equals(currentWorld));
        contextRoles.keySet()
                .removeIf(context -> !context.equals(Context.getGlobal()) || !context.equals(currentWorld));
        currentWorld.removeChild(currentRoom);
        currentRoom = null;
        isInCurrentRoom = false;
    }
}