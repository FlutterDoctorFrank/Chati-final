package model.user;

import controller.network.ClientSender;
import model.MessageBundle;
import model.communication.CommunicationHandler;
import model.communication.message.TextMessage;
import model.context.Context;
import model.context.ContextID;
import model.context.IContext;
import model.context.global.GlobalContext;
import model.context.spatial.Location;
import model.context.spatial.SpatialContext;
import model.database.Database;
import model.database.IUserDatabase;
import model.exception.*;
import model.notification.INotification;
import model.notification.Notification;
import model.role.ContextRole;
import model.role.IContextRole;
import model.role.Permission;
import model.role.Role;
import model.user.account.UserAccountManager;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Eine Klasse, welche einen Benutzer in der Anwendung repräsentiert.
 */
public class User implements IUser {

    /** Standard-Avatar eines Benutzers */
    private static final Avatar DEFAULT_AVATAR = null;

    /** Wird zur eindeutigen Identifikation eines Benutzers verwendet. */
    private final UUID userId;

    /** Der Name des Benutzers. */
    private String username;

    /** Der Online-Status des Benutzers. */
    private Status status;

    /** Der Avatar des Benutzers, der für ihn und andere Spieler auf der Karte sichtbar ist. */
    private Avatar avatar;

    /** Die aktuelle Welt des Benutzers. */
    private SpatialContext currentWorld;

    /** Die aktuelle Position des Benutzers. */
    private Location currentLocation;

    /** Das Objekt, mit dem der Benutzer aktuell interagiert. */
    private SpatialContext currentInteractable;

    /** Wird zum abwickeln der Kommunikation verwendet. */
    private final CommunicationHandler communicationHandler;

    /** Menge der befreundeten Benutzer. */
    private final Map<UUID, User> friends;

    /** Menge der ignorierten Benutzer. */
    private final Map<UUID, User> ignoredUsers;

    /** Menge der Zuordnungen von Kontexten zu den jeweiligen Rollen des Benutzers. */
    private final Map<Context, ContextRole> contextRoles;

    /** Menge der Benachrichtigungen des Benutzers. */
    private final Map<UUID, Notification> notifications;

    /** Erlaubt Zugriff auf die Datenbank. */
    private final IUserDatabase database;

    /** Wird zum Versenden von Informationen an den Client verwendet, der mit diesem Benutzer angemeldet ist. */
    private ClientSender clientSender;

    /**
     * Erzeugt eine Instanz eines neu registrierten Benutzers.
     * @param username Benutzername des Benutzers.
     */
    public User(String username) {
        this.userId = UUID.randomUUID();
        this.username = username;
        this.status = Status.OFFLINE;
        this.avatar = DEFAULT_AVATAR;
        this.communicationHandler = new CommunicationHandler(this);
        this.friends = new HashMap<>();
        this.ignoredUsers = new HashMap<>();
        this.contextRoles = new HashMap<>();
        this.notifications = new HashMap<>();
        this.database = Database.getUserDatabase();
    }

    /**
     * Erzeugt eine Instanz eines Benutzers anhand von Informationen aus der Datenbank.
     * @param userId ID des Benutzers.
     * @param username Benutzername des Benutzers.
     * @param avatar Avatar des Benutzers.
     * @param friends Menge der befreundeten Benutzer.
     * @param ignoredUsers Menge der ignorierten Benutzer.
     * @param contextRoles Menge der Zuordnungen von Kontexten zu den jeweiligen Rollen des Benutzers.
     * @param notifications Menge der Benachrichtigungen des Benutzers.
     */
    public User(UUID userId, String username, Avatar avatar, Map<UUID, User> friends, Map<UUID, User> ignoredUsers,
                Map<Context, ContextRole> contextRoles, Map<UUID, Notification> notifications) {
        this.userId = userId;
        this.username = username;
        this.status = Status.OFFLINE;
        this.avatar = avatar;
        this.communicationHandler = new CommunicationHandler(this);
        this.friends = friends;
        this.ignoredUsers = ignoredUsers;
        this.contextRoles = contextRoles;
        this.notifications = notifications;
        this.database = Database.getUserDatabase();
    }

    @Override
    public void joinWorld(ContextID worldID) throws IllegalStateException, ContextNotFoundException,
            IllegalWorldActionException {
        // Überprüfe, ob Benutzer bereits in einer Welt ist.
        if (currentWorld != null) {
            throw new IllegalStateException("User is already in a world.");
        }
        SpatialContext world = GlobalContext.getInstance().getWorld(worldID);

        // Überprüfe, ob Benutzer in der Welt gesperrt ist.
        if (world.isBanned(this)) {
            throw new IllegalWorldActionException("", "Du bist in dieser Welt gesperrt.");
        }

        // Betrete die Welt.
        currentWorld = world;
        currentWorld.addUser(this);

        // Sende die entsprechenden Pakete an den Benutzer und an andere Benutzer.
        clientSender.send(ClientSender.SendAction.WORLD_ACTION, currentWorld);
        clientSender.send(ClientSender.SendAction.CONTEXT_JOIN, currentWorld);
        clientSender.send(ClientSender.SendAction.CONTEXT_ROLE, contextRoles);
        clientSender.send(ClientSender.SendAction.NOTIFICATION, getWorldNotifications());
        currentWorld.getUsers().forEach((userID, user) -> {
            user.getClientSender().send(ClientSender.SendAction.USER_INFO, this);
        });

        // Positioniere den Avatar an der Anfangsposition der Karte der Welt und schicke die entsprechenden Pakete.
        teleport(currentWorld.getSpawnLocation());
    }

    @Override
    public void leaveWorld() throws IllegalStateException {
        // Überprüfe, ob Benutzer in einer Welt ist.
        if (currentWorld == null) {
            throw new IllegalStateException("User is not in a world.");
        }

        // Sende die entsprechenden Pakete an den Benutzer und an andere Benutzer.
        currentWorld.getUsers().forEach((userID, user) -> {
            user.getClientSender().send(ClientSender.SendAction.AVATAR_REMOVE, this);
            user.getClientSender().send(ClientSender.SendAction.USER_INFO, this);
        });
        clientSender.send(ClientSender.SendAction.WORLD_ACTION, null); // Hier is noch unklar wie das gehen soll?

        // Verlasse die Welt.
        currentLocation = null;
        currentWorld.removeUser(this);
        currentWorld = null;
    }

    @Override
    public void move(int posX, int posY) throws IllegalPositionException, IllegalStateException {
        // Überprüfe, ob Benutzer sich in einer Welt befindet.
        if (currentWorld == null || currentLocation == null) {
            throw new IllegalStateException("User is not in a world.");
        }
        SpatialContext currentRoom = getRoom();
        SpatialContext currentArea = getArea();

        // Überprüfe, ob im aktuellen Bereich des Benutzers eine Bewegung erlaubt ist und sende ein Paket mit der
        // aktuellen Position.
        if (!currentArea.isMoveable()) {
            clientSender.send(ClientSender.SendAction.AVATAR_MOVE, this);
            throw new IllegalStateException("Movement is not allowed.");
        }

        // Überprüfe, ob die Zielkoordinaten erlaubt sind und sende ein Paket mit der aktuellen Position.
        if (!currentRoom.isLegal(posX, posY)) {
            clientSender.send(ClientSender.SendAction.AVATAR_MOVE, this);
            throw new IllegalPositionException("Position is illegal.", this, posX, posY);
        }

        // Setze die neue Position des Benutzers.
        currentLocation.setPosition(posX, posY);

        // Sende die entsprechenden Pakete an diesen Benutzer und an andere Benutzer.
        // ANMERKUNG: Hier muss evtl. der eigene Benutzer herausgefiltert werden, falls dieser nicht das Paket erhalten
        // soll.
        currentRoom.getUsers().forEach((userID, user) -> {
            user.getClientSender().send(ClientSender.SendAction.AVATAR_MOVE, this);
        });

        // Ermittle, ob sich der Bereich des Benutzers geändert hat, und entferne ihn aus den verlassenen Bereichen
        // und füge ihn zu den betretenen Bereichen hinzu.
        SpatialContext newArea = getArea();
        if (!currentArea.equals(newArea)) {
            Context lastCommonAncestor = currentArea.lastCommonAncestor(newArea);
            lastCommonAncestor.removeUser(this);
            newArea.addUser(this);
            // Sende Information über laufende Musik im neu betretenen Kontext.
            clientSender.send(ClientSender.SendAction.CONTEXT_MUSIC, newArea);
        }
    }

    @Override
    public void chat(String message) {
        communicationHandler.handleTextMessage(message);
    }

    @Override
    public void talk(byte[] voicedata) {
        communicationHandler.handleVoiceMessage(voicedata);
    }

    @Override
    public void executeAdministrativeAction(UUID targetID, AdministrativeAction administrativeAction, String[] args)
            throws UserNotFoundException, IllegalStateException, NoPermissionException {
        User target = UserAccountManager.getInstance().getUser(targetID);
        administrativeAction.execute(this, target, args);
    }

    @Override
    public void interact(ContextID spatialID) throws IllegalInteractionException {
        // Überprüfe, ob der Benutzer bereits mit einem Objekt interagiert.
        if (currentInteractable != null) {
            throw new IllegalInteractionException("User is already interacting with a context.", this);
        }
        SpatialContext currentArea = currentLocation.getArea();
        SpatialContext interactable = currentArea.getChildren().get(spatialID);
        // Überprüfe, ob ein Objekt in der Nähe des Benutzers mit dieser ID vorhanden ist und ob der Benutzer mit diesem
        // interagieren kann.
        if (interactable == null || !interactable.canInteract(this)) {
            throw new IllegalInteractionException("There is no interactable context with this ID near the user.", this);
        }
        // Interagiere mit dem Objekt.
        interactable.interact(this);
    }

    @Override
    public void executeOption(ContextID spatialID, int menuOption, String[] args) throws IllegalInteractionException,
            IllegalMenuActionException {
        SpatialContext currentArea = currentLocation.getArea();
        SpatialContext interactable = currentArea.getChildren().get(spatialID);
        // Überprüfe, ob ein Objekt in der Nähe des Benutzers mit dieser ID vorhanden ist und ob der Benutzer mit diesem
        // interagieren kann.
        if (interactable == null || !interactable.canInteract(this)) {
            throw new IllegalInteractionException("There is no interactable context with this ID near the user.", this);
        }
        // Überprüfe, ob der Benutzer das Menü dieses Objekts geöffnet hat.
        if (!currentInteractable.equals(interactable)) {
            throw new IllegalInteractionException("The user has not opened the menu of this context.", this, interactable);
        }
        // Führe die Menü-Option durch.
        currentInteractable.executeMenuOption(this, menuOption, args);
    }

    @Override
    public void deleteNotification(UUID notificationID) throws NotificationNotFoundException {
        if (notifications.remove(notificationID) == null) {
            throw new NotificationNotFoundException("This user has no notification with this ID.", this, notificationID);
        }
    }

    @Override
    public void manageNotification(UUID notificationID, boolean accept) throws NotificationNotFoundException,
            IllegalNotificationActionException {
        Notification notification = notifications.get(notificationID);
        // Überprüfe, ob die Benachrichtigung vorhanden ist.
        if (notification == null) {
            throw new NotificationNotFoundException("This user has no notification with this ID.", this, notificationID);
        }
        // Akzeptiere die Benachrichtigung, oder lehne sie ab.
        if (accept) {
            notification.accept();
        } else {
            notification.decline();
        }
    }

    @Override
    public void setStatus(Status status) {
        this.status = status;
        // Sende geänderte Benutzerinformationen an alle relevanten Benutzer.
        updateUserInfo();
    }

    @Override
    public void setAvatar(Avatar avatar) {
        this.avatar = avatar;
        // Sende geänderte Benutzerinformationen an alle relevanten Benutzer.
        updateUserInfo();
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
        return null;
    }

    @Override
    public Avatar getAvatar() {
        return avatar;
    }

    @Override
    public SpatialContext getWorld() {
        return currentWorld;
    }

    @Override
    public Location getLocation() {
        return currentLocation;
    }

    @Override
    public Map<UUID, IUser> getFriends() {
        return Collections.unmodifiableMap(friends);
    }

    @Override
    public Map<UUID, IUser> getIgnoredUsers() {
        return Collections.unmodifiableMap(ignoredUsers);
    }

    @Override
    public Map<IContext, IContextRole> getGlobalRoles() {
        return contextRoles.entrySet().stream()
                .filter(entry -> entry.getKey().equals(GlobalContext.getInstance()))
                .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    /*
    @Override
    public Map<IContext, IContextRole> getWorldRoles() {
        return contextRoles.entrySet().stream()
                .filter(entry -> entry.getKey().equals(currentWorld))
                .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue));
    }
     */

    @Override
    public Map<UUID, INotification> getGlobalNotifications() {
        return notifications.entrySet().stream()
                .filter(entry -> entry.getValue().getContext().equals(GlobalContext.getInstance()))
                .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public Map<UUID, INotification> getWorldNotifications() {
        return notifications.entrySet().stream()
                .filter(entry -> entry.getValue().getContext().equals(currentWorld))
                .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public void addFriend(User user) {
        friends.put(user.getUserId(), user);
        database.addFriendship(this, user);
        // Sende geänderte Benutzerinformationen an alle relevanten Benutzer.
        updateUserInfo();
    }

    public void removeFriend(User user) {
        friends.remove(user.getUserId());
        database.removeFriendship(this, user);
        // Sende geänderte Benutzerinformationen an alle relevanten Benutzer.
        updateUserInfo();
    }

    public void ignoreUser(User user) {
        ignoredUsers.put(user.getUserId(), user);
        database.addIgnoredUser(this, user);
        // Sende geänderte Benutzerinformationen an alle relevanten Benutzer.
        updateUserInfo();
    }

    public void unignoreUser(User user) {
        ignoredUsers.remove(user.getUserId());
        database.removeIgnoredUser(this, user);
        // Sende geänderte Benutzerinformationen an alle relevanten Benutzer.
        updateUserInfo();
    }

    public void addRole(Context context, Role role) {
        ContextRole contextRole = contextRoles.get(context);
        if (contextRole == null) {
            contextRoles.put(context, new ContextRole(context, role));
        } else {
            contextRole.addRole(role);
        }
        database.addRole(this, context, role);
        clientSender.send(ClientSender.SendAction.CONTEXT_ROLE, contextRoles);
    }

    public void removeRole(Context context, Role role) {
        ContextRole contextRole = contextRoles.get(context);
        contextRole.removeRole(role);
        if (contextRole.getRoles().isEmpty()) {
            contextRoles.remove(context);
        }
        database.removeRole(this, context, role);
        clientSender.send(ClientSender.SendAction.CONTEXT_ROLE, contextRoles);
    }

    public void addNotification(Notification notification) {
        notifications.put(notification.getNotificationID(), notification);
        database.addNotification(this, notification);
        clientSender.send(ClientSender.SendAction.NOTIFICATION, Collections.singletonMap(notification.getNotificationID(), notification));
    }

    public void removeNotification(Notification notification) {
        notifications.remove(notification.getNotificationID());
        database.removeNotification(this, notification);
    }

    public boolean hasRole(Context context, Role role) {
        ContextRole contextRole = contextRoles.get(context);
        return contextRole != null && contextRole.hasRole(role);
    }

    public boolean hasPermission(Context context, Permission permission) {
        ContextRole contextRole = contextRoles.get(context);
        if (contextRole != null && contextRole.hasPermission(permission)) {
            return true;
        } else {
            return hasPermission(context.getParent(), permission);
        }
    }

    /*
    public Context getHighestPermittedContext(Context context, Permission permission) {
        Context highestPermittedContext = null;
        Context currentContext = context;
        ContextRole contextRole;
        do {
            contextRole = contextRoles.get(currentContext);
            if (contextRole != null && contextRole.hasPermission(permission)) {
                highestPermittedContext = currentContext;
            }
            currentContext = currentContext.getParent();
        } while (currentContext != null);
        return highestPermittedContext;
    }
    */

    public boolean isFriend(User user) {
        return friends.containsKey(user.getUserId());
    }

    public boolean isIgnoring(User user) {
        return ignoredUsers.containsKey(user.getUserId());
    }

    public void teleport(Location newLocation) {
        SpatialContext currentRoom = getRoom();
        SpatialContext currentArea = getArea();
        currentLocation = newLocation;
        SpatialContext newRoom = getRoom();
        SpatialContext newArea = getArea();
        // check if user entered a new area
        if (!currentArea.equals(newArea)) {
            // check if user entered a new room
            if (!currentRoom.equals(newRoom)) {
                // Remove avatar from all other users in the old room
                currentRoom.getUsers().forEach((userID, user) -> {
                    user.getClientSender().send(ClientSender.SendAction.AVATAR_REMOVE, this);
                });
                // Send room join information to user
                clientSender.send(ClientSender.SendAction.CONTEXT_JOIN, this);
                // Send position of user to other users in new room & position of others in new room to joining user
                newRoom.getUsers().forEach((userID, user) -> {
                    clientSender.send(ClientSender.SendAction.AVATAR_MOVE, user);
                    user.getClientSender().send(ClientSender.SendAction.AVATAR_MOVE, this);
                });
                // check if user entered a private room
                if (newRoom.isPrivate()) {
                    MessageBundle messageBundle = new MessageBundle("PrivateRoomEnterMessageKey", new Object[]{username});
                    TextMessage info = new TextMessage(messageBundle);
                    // send message to users in private room
                    newRoom.getUsers().forEach((userID, user) -> {
                        user.getClientSender().send(ClientSender.SendAction.MESSAGE, info);
                    });
                }
            }
            Context lastCommonAncestor = currentArea.lastCommonAncestor(newArea);
            lastCommonAncestor.removeUser(this);
            newArea.addUser(this);
            // Send music info of new context
            clientSender.send(ClientSender.SendAction.CONTEXT_MUSIC, newArea);
        }
    }

    public boolean isInteractingWith(SpatialContext interactable) {
        return currentInteractable.equals(interactable);
    }

    public void setCurrentInteractable(SpatialContext interactable) {
        this.currentInteractable = interactable;
    }

    public boolean isOnline() {
        return !status.equals(Status.OFFLINE);
    }

    public void setClientSender(ClientSender clientSender) {
        this.clientSender = clientSender;
    }

    public ClientSender getClientSender() {
        return clientSender;
    }

    public SpatialContext getArea() {
        return currentLocation != null ? currentLocation.getArea() : null;
    }

    public SpatialContext getRoom() {
        return currentLocation != null ? currentLocation.getRoom() : null;
    }

    public void updateUserInfo() {
        Map<UUID, User> receivers = getWorld().getUsers();
        receivers.putAll(friends);
        receivers.forEach((userID, user) -> {
            user.getClientSender().send(ClientSender.SendAction.USER_INFO, this);
        });
    }
}