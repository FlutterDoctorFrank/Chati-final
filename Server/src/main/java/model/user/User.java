package model.user;

import controller.network.ClientSender;
import controller.network.ClientSender.SendAction;
import model.communication.CommunicationHandler;
import model.context.Context;
import model.context.ContextID;
import model.context.global.GlobalContext;
import model.context.spatial.*;
import model.context.spatial.objects.Interactable;
import model.database.Database;
import model.database.IUserDatabase;
import model.exception.*;
import model.notification.INotification;
import model.notification.Notification;
import model.role.ContextRole;
import model.role.Permission;
import model.role.Role;
import model.timedEvents.AccountDeletion;
import model.timedEvents.TimedEventScheduler;
import model.timedEvents.AbsentUser;
import model.user.account.UserAccountManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Eine Klasse, welche einen Benutzer in der Anwendung repräsentiert.
 */
public class User implements IUser {

    /** Standard-Avatar eines Benutzers */
    private static final Avatar DEFAULT_AVATAR = Avatar.PLACEHOLDER;

    /** Wird zur eindeutigen Identifikation eines Benutzers verwendet. */
    private final UUID userId;

    /** Der Name des Benutzers. */
    private String username;

    /** Der Online-Status des Benutzers. */
    private Status status;

    /** Der Avatar des Benutzers, der für ihn und andere Spieler auf der Karte sichtbar ist. */
    private Avatar avatar;

    /** Zeitpunkt des letzten Ausloggens eines Benutzers. */
    private LocalDateTime lastLogoutTime;

    /** Zeitpunkt der letzten Aktivität des Benutzers. */
    private LocalDateTime lastActivity;

    /** Die aktuelle Welt des Benutzers. */
    private World currentWorld;

    /** Die aktuelle Position des Benutzers. */
    private Location currentLocation;

    /** Das Objekt, mit dem der Benutzer aktuell interagiert. */
    private Interactable currentInteractable;

    /** Information, ob sich ein Benutzer momentan bewegen darf. */
    private boolean moveable;

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
    public User(@NotNull final String username) {
        this.userId = UUID.randomUUID();
        this.username = username;
        this.status = Status.OFFLINE;
        this.avatar = DEFAULT_AVATAR;
        this.lastLogoutTime = LocalDateTime.now();
        this.lastActivity = LocalDateTime.now();
        this.currentWorld = null;
        this.currentLocation = null;
        this.currentInteractable = null;
        this.moveable = true;
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
     * @param lastLogoutTime Zeitpunkt, an dem sich der Benutzer das letzte mal ausgeloggt hat.
     */
    public User(@NotNull final UUID userId, @NotNull final String username, @NotNull final Avatar avatar,
                @NotNull final LocalDateTime lastLogoutTime) {
        this.userId = userId;
        this.username = username;
        this.status = Status.OFFLINE;
        this.avatar = avatar;
        this.lastLogoutTime = lastLogoutTime;
        this.lastActivity = LocalDateTime.now();
        this.currentWorld = null;
        this.currentLocation = null;
        this.currentInteractable = null;
        this.moveable = true;
        this.friends = new HashMap<>();
        this.ignoredUsers = new HashMap<>();
        this.contextRoles = new HashMap<>();
        this.notifications = new HashMap<>();
        this.database = Database.getUserDatabase();
    }

    @Override
    public void joinWorld(@NotNull final ContextID worldId) throws ContextNotFoundException, IllegalWorldActionException {
        throwIfNotOnline();
        updateLastActivity();
        // Überprüfe, ob Benutzer bereits in einer Welt ist.
        if (currentWorld != null) {
            throw new IllegalStateException("User is already in a world.");
        }
        World world = GlobalContext.getInstance().getWorld(worldId);
        // Überprüfe, ob Benutzer in der Welt gesperrt ist.
        if (world.isBanned(this)) {
            throw new IllegalWorldActionException("", "Du bist in dieser Welt gesperrt.");
        }
        // Betrete die Welt.
        currentWorld = world;
        currentWorld.addUser(this);
    }

    @Override
    public void leaveWorld() throws IllegalStateException {
        throwIfNotOnline();
        throwIfNotInWorld();
        updateLastActivity();
        // Verlasse die Welt.
        World oldWorld = currentWorld;
        currentWorld = null;
        currentLocation = null;
        oldWorld.removeUser(this);
    }

    @Override
    public void move(final float posX, final float posY) throws IllegalPositionException {
        throwIfNotOnline();
        throwIfNotInWorld();
        updateLastActivity();
        // Überprüfe, ob sich der Benutzer bewegen darf.
        if (!moveable) {
            throw new IllegalStateException("User is not allowed to move.");
        }
        // Überprüfe, ob die Zielkoordinaten erlaubt sind.
        if (!currentLocation.getRoom().isLegal(posX, posY)) {
            throw new IllegalPositionException("Position is illegal.", this, posX, posY);
        }
        // Setze die neue Position des Benutzers.
        Location oldLocation = currentLocation;
        currentLocation.setPosition(posX, posY);
        updateArea(oldLocation, currentLocation);
        currentLocation.getRoom().getUsers().values().stream().filter(Predicate.not(this::equals))
                .forEach(receiver -> receiver.send(SendAction.AVATAR_MOVE, this));
    }

    @Override
    public void chat(@NotNull final String message) throws IllegalStateException {
        throwIfNotOnline();
        throwIfNotInWorld();
        updateLastActivity();
        CommunicationHandler.handleTextMessage(this, message);
    }

    @Override
    public void talk(final byte[] voiceData) {
        throwIfNotOnline();
        throwIfNotInWorld();
        CommunicationHandler.handleVoiceMessage(this, voiceData);
    }

    @Override
    public void executeAdministrativeAction(@NotNull final UUID targetID, @NotNull final AdministrativeAction administrativeAction,
                                            @NotNull final String[] args)
            throws UserNotFoundException, NoPermissionException {
        throwIfNotOnline();
        updateLastActivity();
        User target = UserAccountManager.getInstance().getUser(targetID);
        administrativeAction.execute(this, target, args);
    }

    @Override
    public void interact(@NotNull final ContextID spatialID) throws IllegalInteractionException, ContextNotFoundException {
        throwIfNotOnline();
        throwIfNotInWorld();
        updateLastActivity();
        // Überprüfe, ob der Benutzer bereits mit einem Objekt interagiert.
        if (currentInteractable != null) {
            throw new IllegalInteractionException("User is already interacting with a context.", this);
        }
        Area currentArea = currentLocation.getArea();
        Interactable interactable = currentArea.getInteractable(spatialID);
        // Überprüfe, ob ein Objekt in der Nähe des Benutzers mit dieser ID vorhanden ist und ob der Benutzer mit diesem
        // interagieren kann.
        if (!interactable.canInteract(this)) {
            throw new IllegalInteractionException("There is no interactable context with this ID near the user.", this);
        }
        // Interagiere mit dem Objekt.
        interactable.interact(this);
    }

    @Override
    public void executeOption(@NotNull final ContextID spatialID, final int menuOption,
                              @NotNull final String[] args) throws IllegalInteractionException, IllegalMenuActionException, ContextNotFoundException {
        throwIfNotOnline();
        throwIfNotInWorld();
        updateLastActivity();
        Area currentArea = currentLocation.getArea();
        Interactable interactable = currentArea.getInteractable(spatialID);
        // Überprüfe, ob ein Objekt in der Nähe des Benutzers mit dieser ID vorhanden ist und ob der Benutzer mit diesem
        // interagieren kann.
        if (!interactable.canInteract(this)) {
            throw new IllegalInteractionException("There is no interactable context with this ID near the user.", this);
        }
        // Überprüfe, ob der Benutzer das Menü dieses Objekts geöffnet hat.
        if (currentInteractable == null || !currentInteractable.equals(interactable)) {
            throw new IllegalInteractionException("The user has not opened the menu of this context.", this,
                    interactable);
        }
        // Führe die Menü-Option durch.
        currentInteractable.executeMenuOption(this, menuOption, args);
    }

    @Override
    public void deleteNotification(@NotNull final UUID notificationID) throws NotificationNotFoundException {
        throwIfNotOnline();
        updateLastActivity();
        Notification notification = notifications.get(notificationID);
        if (notification == null) {
            throw new NotificationNotFoundException("This user has no notification with this ID.", this, notificationID);
        }
        database.removeNotification(this, notification);
    }

    @Override
    public void manageNotification(@NotNull final UUID notificationID, final boolean accept) throws NotificationNotFoundException,
            IllegalNotificationActionException {
        throwIfNotOnline();
        updateLastActivity();
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
        // Lösche die entsprechende Benachrichtigung.
        deleteNotification(notificationID);
    }

    @Override
    public void setAvatar(@NotNull final Avatar avatar) {
        throwIfNotOnline();
        updateLastActivity();
        this.avatar = avatar;
        database.changeAvatar(this, avatar);
        // Sende geänderte Benutzerinformationen an alle relevanten Benutzer.
        updateUserInfo();
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
    public @NotNull Status getStatus() {
        return status;
    }

    @Override
    public @NotNull Avatar getAvatar() {
        return avatar;
    }

    @Override
    public @Nullable World getWorld() {
        return currentWorld;
    }

    @Override
    public @Nullable Location getLocation() {
        return currentLocation;
    }

    @Override
    public @NotNull Map<UUID, IUser> getFriends() {
        return Collections.unmodifiableMap(friends);
    }

    @Override
    public @NotNull Map<UUID, IUser> getIgnoredUsers() {
        return Collections.unmodifiableMap(ignoredUsers);
    }

    @Override
    public @NotNull ContextRole getGlobalRoles() {
        try {
            return contextRoles.values().stream()
                    .filter(contextRole -> contextRole.getContext().equals(GlobalContext.getInstance()))
                    .findFirst().orElseThrow();
        } catch (NoSuchElementException e) {
            return new ContextRole(this, GlobalContext.getInstance(), new HashSet<>());
        }
    }

    @Override
    public @NotNull Map<UUID, INotification> getGlobalNotifications() {
        return notifications.values().stream()
                .filter(notification -> notification.getContext().equals(GlobalContext.getInstance()))
                .collect(Collectors.toUnmodifiableMap(Notification::getNotificationId, Function.identity()));
    }

    /**
     * Teleportiert einen Benutzer an die angegebene Position.
     * @param newLocation Position, an die Benutzer teleportiert werden soll.
     */
    public void teleport(@NotNull final Location newLocation) {
        Location oldLocation = currentLocation;
        currentLocation = newLocation;
        updateArea(oldLocation, currentLocation);
        currentLocation.getRoom().getUsers().values().forEach(receiver -> receiver.send(SendAction.AVATAR_MOVE, this));
    }

    /**
     * Aktualisiert die Datenstrukturen der enthaltenen Benutzer in Kontexten nach einer Positionsänderung.
     * @param oldLocation Alte Position des Benutzers.
     * @param newLocation Neue Position des Benutzers.
     */
    private void updateArea(@Nullable final Location oldLocation, @Nullable final Location newLocation) {
        if (newLocation != null) {
            Area newArea = newLocation.getArea();
            if (oldLocation != null) {
                Area oldArea = oldLocation.getArea();
                Context lastCommonAncestor = oldArea.lastCommonAncestor(newArea);
                lastCommonAncestor.getChildren().values().forEach(child -> child.removeUser(this));
            }
            newArea.addUser(this);
        }
    }

    /**
     * Wird von der Datenbank verwendet, um die initiale Menge von Freunden hinzuzufügen..
     * @param friends Hinzuzufügende Benutzer.
     */
    public void addFriends(@NotNull final Map<UUID, User> friends) {
        this.friends.putAll(friends);
    }

    /**
     * Wird von der Datenbank verwendet, um die initiale Menge von ignorierten Benutzern hinzuzufügen.
     * @param ignoredUsers Hinzuzufügende Benutzer.
     */
    public void addIgnoredUsers(@NotNull final Map<UUID, User> ignoredUsers) {
        this.ignoredUsers.putAll(ignoredUsers);
    }

    /**
     * Fügt einen Benutzer in die Liste der Freunde hinzu.
     * @param user Hinzuzufügender Benutzer.
     */
    public void addFriend(@NotNull final User user) {
        friends.put(user.getUserId(), user);
        user.friends.put(userId, this);
        database.addFriendship(this, user);
        // Sende geänderte Benutzerinformationen an alle relevanten Benutzer.
        updateUserInfo();
    }

    /**
     * Entfernt einen Benutzer aus der Liste der Freunde.
     * @param user Zu entfernender Benutzer.
     */
    public void removeFriend(@NotNull final User user) {
        friends.remove(user.getUserId());
        database.removeFriendship(this, user);
        // Sende geänderte Benutzerinformationen an alle relevanten Benutzer.
        updateUserInfo();
    }

    /**
     * Fügt einen Benutzer in die Liste der ignorierten Benutzer hinzu.
     * @param user Hinzuzufügender Benutzer.
     */
    public void ignoreUser(@NotNull final User user) {
        ignoredUsers.put(user.getUserId(), user);
        database.addIgnoredUser(this, user);
        // Sende geänderte Benutzerinformationen an alle relevanten Benutzer.
        updateUserInfo();
    }

    /**
     * Entfernt einen Benutzer aus der Liste der ignorierten Benutzer.
     * @param user Zu entfernender Benutzer.
     */
    public void unignoreUser(@NotNull final User user) {
        ignoredUsers.remove(user.getUserId());
        database.removeIgnoredUser(this, user);
        // Sende geänderte Benutzerinformationen an alle relevanten Benutzer.
        updateUserInfo();
    }

    /**
     * Wird von den Datenbank verwendet, um die initialen Rollen eines Benutzers zu setzen.
     * @param contextRoles Zu setzende Rollen.
     */
    public void addRoles(@NotNull final Map<Context, ContextRole> contextRoles) {
        this.contextRoles.putAll(contextRoles);
    }

    /**
     * Fügt dem Benutzer eine Rolle in einem Kontext hinzu.
     * @param context Kontext, in dem die Rolle hinzugefügt werden soll.
     * @param role Hinzuzufügende Rolle.
     * @see ContextRole
     */
    public void addRole(@NotNull final Context context, @NotNull final Role role) {
        ContextRole contextRole = contextRoles.get(context);
        if (contextRole == null) {
            contextRole = new ContextRole(this, context, role);
            contextRoles.put(context, contextRole);
        } else {
            contextRole.addRole(role);
        }
        database.addRole(this, context, role);
        // Sende geänderte Rolleninformationen an alle relevanten Benutzer.
        updateRoleInfo(contextRole);
    }

    /**
     * Entzieht dem Benutzer eine Rolle in einem Kontext.
     * @param context Kontext, in dem die Rolle entzogen werden soll.
     * @param role Zu entziehende Rolle.
     */
    public void removeRole(@NotNull final Context context, @NotNull final Role role) {
        ContextRole contextRole = contextRoles.get(context);
        if (contextRole != null) {
            contextRole.removeRole(role);
            if (contextRole.getRoles().isEmpty()) {
                contextRoles.remove(context);
            }
            database.removeRole(this, context, role);
            // Sende geänderte Rolleninformationen an alle relevanten Benutzer.
            updateRoleInfo(contextRole);
        }
    }

    /**
     * Wird von der Datenbank verwendet, um die initialen Benachrichtigungen eines Benutzers zu setzen.
     * @param notifications Zu setzende Benachrichtigungen.
     */
    public void addNotifications(@NotNull final Map<UUID, Notification> notifications) {
        this.notifications.putAll(notifications);
    }

    /**
     * Fügt dem Benutzer eine Benachrichtigung hinzu.
     * @param notification Hinzuzufügende Benachrichtigung.
     */
    public void addNotification(@NotNull final Notification notification) {
        notifications.put(notification.getNotificationId(), notification);
        database.addNotification(this, notification);

        // Sende Benachrichtigung an Benutzer.
        this.send(ClientSender.SendAction.NOTIFICATION, notification);
    }

    /**
     * Überprüft, ob ein Benutzer eine Rolle in einem Kontext besitzt.
     * @param context Zu überprüfender Kontext.
     * @param role Zu überprüfende Rolle.
     * @return true, wenn der Benutzer die Rolle in dem Kontext besitzt, sonst false.
     */
    public boolean hasRole(@NotNull final Context context, @NotNull final Role role) {
        ContextRole contextRole = contextRoles.get(context);
        return contextRole != null && contextRole.hasRole(role);
    }

    /**
     * Überprüft, ob ein Benutzer eine Berechtigung in einem Kontext, oder einem übergeordneten Kontext besitzt.
     * @param context Zu überprüfender Kontext.
     * @param permission Zu überprüfende Berechtigung.
     * @return true, wenn der Benutzer die Rolle in dem Kontext, oder einem übergeordneten Kontext besitzt, sonst false.
     */
    public boolean hasPermission(@NotNull final Context context, @NotNull final Permission permission) {
        ContextRole contextRole = contextRoles.get(context);
        return (contextRole != null && contextRole.hasPermission(permission))
                || (context.getParent() != null && hasPermission(context.getParent(), permission));
    }

    /**
     * Überprüft, ob sich ein Benutzer in der Freundesliste befindet.
     * @param user Zu überprüfender Benutzer.
     * @return true, wenn sich der Benutzer in der Freundesliste befindet, sonst false.
     */
    public boolean isFriend(@NotNull final User user) {
        return friends.containsKey(user.getUserId());
    }

    /**
     * Überprüft, ob ein Benutzer ignoriert wird.
     * @param user Zu überprüfender Benutzer.
     * @return true, wenn der Benutzer ignoriert wird, sonst false.
     */
    public boolean isIgnoring(@NotNull final User user) {
        return ignoredUsers.containsKey(user.getUserId());
    }

    /**
     * Überprüft, ob ein Benutzer gerade mit dem übergebenen Kontext interagiert.
     * @param interactable Zu überprüfender Kontext.
     * @return true, wenn Benutzer mit dem Kontext interagiert, sonst false.
     */
    public boolean isInteractingWith(@NotNull final Interactable interactable) {
        return currentInteractable != null && currentInteractable.equals(interactable);
    }

    public boolean isInteracting() {
        return this.currentInteractable != null;
    }

    /**
     * Überprüft, ob ein Benutzer gerade online ist.
     * @return true, wenn der Benutzer online ist, sonst false.
     */
    public boolean isOnline() {
        if (this.status != Status.OFFLINE) {
            if (this.clientSender == null) {
                throw new IllegalStateException("User is marked as online but is not connected");
            }
            return true;
        }
        return false;
    }

    /**
     * Überprüft, ob ein Benutzer gerade in einer Welt ist.
     * @return true, wenn der Benutzer in einer Welt ist, sonst false.
     */
    public boolean isInWorld() {
        return currentWorld != null;
    }

    /**
     * Setzt das Objekt, mit dem der Benutzer momentan interagiert.
     * @param interactable Objekt, mit dem der Benutzer momentan interagiert.
     */
    public void setCurrentInteractable(@Nullable final Interactable interactable) {
        this.currentInteractable = interactable;
    }

    /**
     * Ändert den Status eines Benutzers.
     * @param status Neuer Status des Benutzers.
     */
    public void setStatus(@NotNull final Status status) {
        this.status = status;
        // Sende geänderte Benutzerinformationen an alle relevanten Benutzer.
        updateUserInfo();
    }

    /**
     * Aktualisiert den Zeitpunkt, an dem der Benutzer sich das letzte Mal ausgeloggt hat.
     */
    public void updateLastLogoutTime() {
        setStatus(Status.OFFLINE);
        this.lastLogoutTime = LocalDateTime.now();
        TimedEventScheduler.getInstance().put(new AccountDeletion(this));
    }

    /**
     * Aktualisiert den Zeitpunkt, an dem der Benutzer seine letzte Aktivität gezeigt hat.
     */
    public void updateLastActivity() {
        LocalDateTime now = LocalDateTime.now();
        // Aktualisiere diesen Wert nur alle 30 Sekunden, um Ressourcen des TimedEventScheduler zu sparen.
        if (lastActivity.until(now, ChronoUnit.SECONDS) >= 30) {
            if (status.equals(Status.AWAY)) {
                setStatus(Status.ONLINE);
            }
            this.lastActivity = now;
            TimedEventScheduler.getInstance().put(new AbsentUser(this));
        }
    }

    /**
     * Setzt die Information, ob der Benutzer sich momentan bewegen darf.
     * @param moveable true, wenn der Benutzer sich bewegen darf, sonst false.
     */
    public void setMoveable(final boolean moveable) {
        this.moveable = moveable;
    }

    /**
     * Gibt die Rollen des Benutzers innerhalb seiner aktuellen Welt und allen untergeordneten Kontexten zurück.
     * @return Menge der Rollen des Benutzers in seiner aktuellen Welt und allen untergeordneten Kontexten.
     * @throws IllegalStateException wenn sich der Benutzer in keiner Welt befindet.
     */
    public @NotNull Map<Context, ContextRole> getWorldRoles() throws IllegalStateException {
        // Prüfe, ob Benutzer in einer Welt ist.
        if (currentWorld == null) {
            throw new IllegalStateException("User is not in a world.");
        }
        Map<Context, ContextRole> worldRoles = new HashMap<>();
        addChildRoles(worldRoles, currentWorld);
        return worldRoles;
    }

    /**
     * Fügt zu einer Menge von Rollen in Kontexten die Rollen von allen untergeordneten Kontexten hinzu.
     * @param contextRoles Menge von Rollen in Kontexten.
     * @param context Kontext, von dem die Rollen der untergeordneten Kontexte hinzugefügt werden sollen.
     */
    private void addChildRoles(@NotNull final Map<Context, ContextRole> contextRoles, @NotNull final Context context) {
        Map<Context, ContextRole> found = this.contextRoles.values().stream()
                .filter(contextRole -> contextRole.getContext().equals(context))
                .collect(Collectors.toMap(ContextRole::getContext, Function.identity()));
        contextRoles.putAll(found);
        context.getChildren().values().forEach(child -> addChildRoles(contextRoles, child));
    }

    /**
     * Gibt die Benachrichtigungen des Benutzers innerhalb seiner aktuellen Welt zurück.
     * @return Menge der Benachrichtigungen des Benutzers in seiner aktuellen Welt.
     * @throws IllegalStateException wenn sich der Benutzer in keiner Welt befindet.
     */
    public @NotNull Map<UUID, INotification> getWorldNotifications() throws IllegalStateException {
        // Prüfe, ob Benutzer in einer Welt ist.
        if (currentWorld == null) {
            throw new IllegalStateException("User is not in a world.");
        }
        return notifications.values().stream()
                .filter(notification -> notification.getContext().equals(currentWorld))
                .collect(Collectors.toUnmodifiableMap(Notification::getNotificationId, Function.identity()));
    }

    /**
     * Gibt das Menü zurück, dass der Benutzer gerade geöffnet hat.
     * @return Interaktionsobjekt, mit dem der Benutzer gerade interagiert.
     */
    public @Nullable Menu getCurrentMenu() {
        return currentInteractable == null ? null : currentInteractable.getMenu();
    }

    /**
     * Gibt den Zeitpunkt zurück, an dem sich der Benutzer das letzte mal abgemeldet hat.
     * @return Letzter Zeitpunkt, an dem der Benutzer sich abgemeldet hat.
     */
    public @NotNull LocalDateTime getLastLogoutTime() {
        return lastLogoutTime;
    }

    /**
     * Gibt den Zeitpunkt zurück, an dem der Benutzer seine letzte Aktivität gezeigt hat.
     * @return Letzter Zeitpunkt, an dem der Benutzer eine Aktivität gezeigt hat.
     */
    public @NotNull LocalDateTime getLastActivity() {
        return lastActivity;
    }

    /**
     * Sendet Pakete an alle relevanten Benutzer mit der aktualisierten Benutzerinformation.
     */
    public void updateUserInfo() {
        final Map<UUID, User> receivers = new HashMap<>(friends);

        if (currentWorld != null) {
            receivers.putAll(currentWorld.getUsers());
        }

        receivers.put(userId, this);
        receivers.values().stream()
                .filter(User::isOnline)
                .forEach(user -> user.send(SendAction.USER_INFO, this));
    }

    /**
     * Sendet Pakete an alle relevanten Benutzer mit der aktualisierten Rolleninformation.
     */
    public void updateRoleInfo(@NotNull final ContextRole contextRole) {
        final Map<UUID, User> receivers = new HashMap<>(friends);

        if (currentWorld != null) {
            receivers.putAll(currentWorld.getUsers());
        }

        receivers.put(userId, this);
        receivers.values().stream()
                .filter(User::isOnline)
                .forEach(user -> user.send(SendAction.CONTEXT_ROLE, contextRole));
    }

    /**
     * Wirft eine Exception wenn der Benutzer nicht angemeldet ist.
     */
    private void throwIfNotOnline() {
        if (!isOnline()) {
            throw new IllegalStateException("User is not logged in.");
        }
    }

    /**
     * Wirft eine Exception wenn der Benutzer nicht in einer Welt ist.
     */
    private void throwIfNotInWorld() {
        if (!isInWorld()) {
            throw new IllegalStateException("User is not in world.");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return userId.equals(user.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId);
    }

    public void login(@NotNull final ClientSender sender) {
        if (this.isOnline()) {
            throw new IllegalStateException("User is already logged in");
        }

        this.clientSender = sender;
        this.status = Status.ONLINE;

        updateLastActivity();
    }

    public void logout() {
        if (!this.isOnline()) {
            throw new IllegalStateException("User is not logged in");
        }

        this.clientSender = null;
        this.status = Status.OFFLINE;

        if (this.currentWorld != null) {
            final World world = this.currentWorld;
            this.currentWorld = null;
            world.removeUser(this);
        }

        updateLastLogoutTime();
    }

    public void send(@NotNull final SendAction action, @NotNull final Object object) {
        if (this.isOnline()) {
            this.clientSender.send(action, object);
        }
    }
}