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
        clientSender.send(ClientSender.SendAction.CONTEXT_ROLE, getWorldRoles());
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
        SpatialContext currentRoom = currentLocation.getRoom();
        SpatialContext currentArea = currentLocation.getArea();

        // Überprüfe, ob im aktuellen Bereich des Benutzers eine Bewegung erlaubt ist.
        if (!currentArea.isMoveable()) {
            throw new IllegalStateException("Movement is not allowed.");
        }

        // Überprüfe, ob die Zielkoordinaten erlaubt sind.
        if (!currentRoom.isLegal(posX, posY)) {
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
        SpatialContext newArea = currentLocation.getArea();
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

    @Override
    public Map<UUID, INotification> getGlobalNotifications() {
        return notifications.entrySet().stream()
                .filter(entry -> entry.getValue().getContext().equals(GlobalContext.getInstance()))
                .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    /**
     * Teleportiert einen Benutzer an die angegebene Position.
     * @param newLocation Position, an die Benutzer teleportiert werden soll.
     */
    public void teleport(Location newLocation) {
        SpatialContext newRoom = newLocation.getRoom();
        SpatialContext newArea = newLocation.getArea();
        // Durchzuführen, wenn der Benutzer bereits eine Position in der Welt hat.
        if (currentLocation != null) {
            SpatialContext currentRoom = currentLocation.getRoom();
            SpatialContext currentArea = currentLocation.getArea();
            // Durchzuführen, wenn sich durch das Teleportieren der Bereich geändert hat.
            if (!currentArea.equals(newArea)) {
                // Entferne Benutzer aus altem Bereich.
                Context lastCommonAncestor = currentArea.lastCommonAncestor(newArea);
                lastCommonAncestor.removeUser(this);
                // Durchzuführen, wenn sich durch das Teleportieren der Raum geändert hat.
                if (!currentRoom.equals(newRoom)) {
                    // Sende Pakete zum entfernen des Avatars im alten Raum.
                    currentRoom.getUsers().forEach((userId, user) -> {
                        user.getClientSender().send(ClientSender.SendAction.AVATAR_REMOVE, this);
                    });
                }
            }
        }
        // Sende neue Position an andere Benutzer im Raum, und Position anderer Benutzer im Raum an teleportierenden
        // Benutzer.
        newRoom.getUsers().forEach((userId, user) -> {
            user.getClientSender().send(ClientSender.SendAction.AVATAR_MOVE, this);
            clientSender.send(ClientSender.SendAction.AVATAR_MOVE, user);
        });
        // Betrete neuen Bereich.
        newArea.addUser(this);
        // Sende Musikinformationen des neuen Bereichs.
        clientSender.send(ClientSender.SendAction.CONTEXT_MUSIC, newArea);
    }

    /**
     * Fügt einen Benutzer in die Liste der Freunde hinzu.
     * @param user Hinzuzufügender Benutzer.
     */
    public void addFriend(User user) {
        friends.put(user.getUserId(), user);
        database.addFriendship(this, user);
        // Sende geänderte Benutzerinformationen an alle relevanten Benutzer.
        updateUserInfo();
    }

    /**
     * Entfernt einen Benutzer aus der Liste der Freunde.
     * @param user Zu entfernender Benutzer.
     */
    public void removeFriend(User user) {
        friends.remove(user.getUserId());
        database.removeFriendship(this, user);
        // Sende geänderte Benutzerinformationen an alle relevanten Benutzer.
        updateUserInfo();
    }

    /**
     * Fügt einen Benutzer in die Liste der ignorierten Benutzer hinzu.
     * @param user Hinzuzufügender Benutzer.
     */
    public void ignoreUser(User user) {
        ignoredUsers.put(user.getUserId(), user);
        database.addIgnoredUser(this, user);
        // Sende geänderte Benutzerinformationen an alle relevanten Benutzer.
        updateUserInfo();
    }

    /**
     * Entfernt einen Benutzer aus der Liste der ignorierten Benutzer.
     * @param user Zu entfernender Benutzer.
     */
    public void unignoreUser(User user) {
        ignoredUsers.remove(user.getUserId());
        database.removeIgnoredUser(this, user);
        // Sende geänderte Benutzerinformationen an alle relevanten Benutzer.
        updateUserInfo();
    }

    /**
     * Fügt dem Benutzer eine Rolle in einem Kontext hinzu.
     * @param context Kontext, in dem die Rolle hinzugefügt werden soll.
     * @param role Hinzuzufügende Rolle.
     * @see ContextRole
     */
    public void addRole(Context context, Role role) {
        ContextRole contextRole = contextRoles.get(context);
        if (contextRole == null) {
            contextRoles.put(context, new ContextRole(context, role));
        } else {
            contextRole.addRole(role);
        }
        database.addRole(this, context, role);
        // Sende geänderte Rolleninformationen an alle relevanten Benutzer.
        updateRoleInfo();
    }

    /**
     * Entzieht dem Benutzer eine Rolle in einem Kontext.
     * @param context Kontext, in dem die Rolle entzogen werden soll.
     * @param role Zu entziehende Rolle.
     */
    public void removeRole(Context context, Role role) {
        ContextRole contextRole = contextRoles.get(context);
        if (contextRole != null) {
            contextRole.removeRole(role);
            if (contextRole.getRoles().isEmpty()) {
                contextRoles.remove(context);
            }
            database.removeRole(this, context, role);
            // Sende geänderte Rolleninformationen an alle relevanten Benutzer.
            updateRoleInfo();
        }
    }

    /**
     * Fügt dem Benutzer eine Benachrichtigung hinzu.
     * @param notification Hinzuzufügende Benachrichtigung.
     */
    public void addNotification(Notification notification) {
        notifications.put(notification.getNotificationID(), notification);
        database.addNotification(this, notification);
        // Sende Benachrichtigung an Benutzer.
        clientSender.send(ClientSender.SendAction.NOTIFICATION, Collections.singletonMap(notification.getNotificationID(), notification));
    }

    /**
     * Entfernt eine Benachrichtigung des Benutzers.
     * @param notification Zu entfernende Benachrichtigung.
     */
    public void removeNotification(Notification notification) {
        notifications.remove(notification.getNotificationID());
        database.removeNotification(this, notification);
    }

    /**
     * Überprüft, ob ein Benutzer eine Rolle in einem Kontext besitzt.
     * @param context Zu überprüfender Kontext.
     * @param role Zu überprüfende Rolle.
     * @return true, wenn der Benutzer die Rolle in dem Kontext besitzt, sonst false.
     */
    public boolean hasRole(Context context, Role role) {
        ContextRole contextRole = contextRoles.get(context);
        return contextRole != null && contextRole.hasRole(role);
    }

    /**
     * Überprüft, ob ein Benutzer eine Berechtigung in einem Kontext, oder einem übergeordneten Kontext besitzt.
     * @param context Zu überprüfender Kontext.
     * @param permission Zu überprüfende Berechtigung.
     * @return true, wenn der Benutzer die Rolle in dem Kontext, oder einem übergeordneten Kontext besitzt, sonst false.
     */
    public boolean hasPermission(Context context, Permission permission) {
        if (context == null) {
            return false;
        }
        ContextRole contextRole = contextRoles.get(context);
        return contextRole != null && contextRole.hasPermission(permission) || hasPermission(context.getParent(), permission);
    }

    /**
     * Überprüft, ob sich ein Benutzer in der Freundesliste befindet.
     * @param user Zu überprüfender Benutzer.
     * @return true, wenn sich der Benutzer in der Freundesliste befindet, sonst false.
     */
    public boolean isFriend(User user) {
        return friends.containsKey(user.getUserId());
    }

    /**
     * Überprüft, ob ein Benutzer ignoriert wird.
     * @param user Zu überprüfender Benutzer.
     * @return true, wenn der Benutzer ignoriert wird, sonst false.
     */
    public boolean isIgnoring(User user) {
        return ignoredUsers.containsKey(user.getUserId());
    }

    /**
     * Überprüft, ob ein Benutzer gerade mit dem übergebenen Kontext interagiert.
     * @param interactable Zu überprüfender Kontext.
     * @return true, wenn Benutzer mit dem Kontext interagiert, sonst false.
     */
    public boolean isInteractingWith(SpatialContext interactable) {
        return currentInteractable.equals(interactable);
    }

    /**
     * Überprüft, ob ein Benutzer gerade online ist.
     * @return true, wenn der Benutzer online ist, sonst false.
     */
    public boolean isOnline() {
        return !status.equals(Status.OFFLINE);
    }

    /**
     * Ändert den Status eines Benutzers.
     * @param status Neuer Status des Benutzers.
     */
    public void setStatus(Status status) {
        this.status = status;
        // Sende geänderte Benutzerinformationen an alle relevanten Benutzer.
        updateUserInfo();
    }

    /**
     * Setzt die Instanz des ClientSenders.
     * @param clientSender Instanz des ClientSenders.
     */
    public void setClientSender(ClientSender clientSender) {
        this.clientSender = clientSender;
    }

    /**
     * Gibt die Rollen des Benutzers innerhalb seiner aktuellen Welt zurück.
     * @return Menge der Rollen des Benutzers in seiner aktuellen Welt.
     * @throws IllegalStateException wenn sich der Benutzer in keiner Welt befindet.
     */
    public Map<IContext, IContextRole> getWorldRoles() throws IllegalStateException {
        // Prüfe, ob Benutzer in einer Welt ist.
        if (currentWorld == null) {
            throw new IllegalStateException("User is not in a world.");
        }
        return contextRoles.entrySet().stream()
                .filter(entry -> entry.getKey().isInContext(currentWorld))
                .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    /**
     * Gibt die Benachrichtigungen des Benutzers innerhalb seiner aktuellen Welt zurück.
     * @return Menge der Benachrichtigungen des Benutzers in seiner aktuellen Welt.
     * @throws IllegalStateException wenn sich der Benutzer in keiner Welt befindet.
     */
    public Map<UUID, INotification> getWorldNotifications() throws IllegalStateException {
        // Prüfe, ob Benutzer in einer Welt ist.
        if (currentWorld == null) {
            throw new IllegalStateException("User is not in a world.");
        }
        return notifications.entrySet().stream()
                .filter(entry -> entry.getValue().getContext().equals(currentWorld))
                .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    /**
     * @return ClientSender.
     */
    public ClientSender getClientSender() {
        return clientSender;
    }

    /**
     * Sendet Pakete an alle relevanten Benutzer mit der aktualisierten Benutzerinformation.
     */
    public void updateUserInfo() {
        Map<UUID, User> receivers = getWorld().getUsers();
        receivers.putAll(friends);
        receivers.forEach((userID, user) -> {
            user.getClientSender().send(ClientSender.SendAction.USER_INFO, this);
        });
    }

    /**
     * Sendet Pakete an alle relevanten Benutzer mit der aktualisierten Rolleninformation.
     */
    public void updateRoleInfo() {
        Map<UUID, User> receivers = getWorld().getUsers();
        receivers.putAll(friends);
        receivers.forEach((userId, user) -> {
            user.getClientSender().send(ClientSender.SendAction.CONTEXT_ROLE, this);
        });
    }
}