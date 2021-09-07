package model.user;

import com.google.common.collect.Sets;
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
import model.notification.NotificationAction;
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

    /** Wird zur eindeutigen Identifikation eines Benutzers verwendet. */
    private final UUID userId;

    /** Der Name des Benutzers. */
    private String username;

    /** Der Online-Status des Benutzers. */
    private Status status;

    /** Der Avatar des Benutzers, der für ihn und andere Spieler auf der Karte sichtbar ist. */
    private Avatar avatar;

    /** Zeitpunkt des letzten Ausloggen eines Benutzers. */
    private LocalDateTime lastLogoutTime;

    /** Zeitpunkt der letzten Aktivität des Benutzers. */
    private LocalDateTime lastActivity;

    /** Die aktuelle Welt des Benutzers. */
    private World currentWorld;

    /** Die aktuelle Position des Benutzers. */
    private Location currentLocation;

    /** Die Information, ob sich der Benutzer gerade schnell fortbewegt. */
    private boolean isSprinting;

    /** Das Objekt, mit dem der Benutzer aktuell interagiert. */
    private Interactable currentInteractable;

    /** Information, ob sich ein Benutzer momentan bewegen darf. */
    private boolean movable;

    /** Die Benutzer, mit denen dieser Benutzer gerade kommunizieren kann. */
    private final Map<UUID, User> communicableUsers;

    /** Menge der befreundeten Benutzer. */
    private final Map<UUID, User> friends;

    /** Menge der ignorierten Benutzer. */
    private final Map<UUID, User> ignoredUsers;

    /** Menge der Zuordnungen von Kontexten zu den jeweiligen Rollen des Benutzers. */
    private final Map<Context, ContextRole> contextRoles;

    /** Menge der Benachrichtigungen des Benutzers. */
    private final Map<UUID, Notification> notifications;

    /** Erlaubt den Zugriff auf die Datenbank. */
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
        this.avatar = Avatar.values()[new Random().nextInt(Avatar.values().length)];
        this.lastLogoutTime = LocalDateTime.now();
        this.lastActivity = LocalDateTime.now();
        this.currentWorld = null;
        this.currentLocation = null;
        this.currentInteractable = null;
        this.movable = true;
        this.communicableUsers = new HashMap<>();
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
     * @param lastLogoutTime Zeitpunkt, an dem sich der Benutzer das letzte Mal ausgeloggt hat.
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
        this.movable = true;
        this.communicableUsers = new HashMap<>();
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
            throw new IllegalWorldActionException("", "action.world-join.banned", world.getContextName());
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
    public void move(@NotNull final Direction direction, final float posX, final float posY, final boolean isSprinting) throws IllegalPositionException {
        throwIfNotOnline();
        throwIfNotInWorld();
        updateLastActivity();
        // Überprüfe, ob sich der Benutzer bewegen darf.
        if (!movable) {
            throw new IllegalStateException("User is not allowed to move.");
        }
        // Überprüfe, ob die Zielkoordinaten erlaubt sind.
        if (!currentLocation.getRoom().isLegal(posX, posY)) {
            throw new IllegalPositionException("Position is illegal.", this, posX, posY);
        }

        Area oldArea = currentLocation.getArea();
        currentLocation.setPosition(posX, posY);
        currentLocation.setDirection(direction);
        updateArea(oldArea, currentLocation.getArea());

        this.isSprinting = isSprinting;

        // Hier erhält der eigene Benutzer eine Bestätigung der Bewegung:
        currentLocation.getRoom().getUsers().values().forEach(receiver -> receiver.send(SendAction.AVATAR_MOVE, this));
        updateCommunicableUsers();
    }

    @Override
    public void type() throws IllegalStateException {
        throwIfNotOnline();
        throwIfNotInWorld();
        updateLastActivity();
        CommunicationHandler.handleTyping(this);
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
    public void executeAdministrativeAction(@NotNull final UUID targetId, @NotNull final AdministrativeAction administrativeAction,
                                            @NotNull final String[] args)
            throws UserNotFoundException, IllegalAdministrativeActionException, NoPermissionException {
        throwIfNotOnline();
        updateLastActivity();
        User target = UserAccountManager.getInstance().getUser(targetId);
        administrativeAction.execute(this, target, args);
    }

    @Override
    public void interact(@NotNull final ContextID spatialId) throws IllegalInteractionException, ContextNotFoundException {
        throwIfNotOnline();
        throwIfNotInWorld();
        updateLastActivity();

        Area currentArea = currentLocation.getArea();
        Interactable interactable = currentArea.getInteractable(spatialId);
        // Überprüfe, ob der Benutzer bereits mit einem Objekt interagiert.
        if (currentInteractable != null && !currentInteractable.equals(interactable)) {
            throw new IllegalInteractionException("User is already interacting with a context.", this);
        }
        // Überprüfe, ob ein Objekt in der Nähe des Benutzers mit dieser ID vorhanden ist und ob der Benutzer mit diesem
        // interagieren kann.
        if (!interactable.canInteract(this)) {
            throw new IllegalInteractionException("There is no interactable context with this ID near the user.", this);
        }
        // Interagiere mit dem Objekt.
        interactable.interact(this);
    }

    @Override
    public void executeOption(@NotNull final ContextID spatialId, final int menuOption,
                              @NotNull final String[] args) throws IllegalInteractionException, IllegalMenuActionException, ContextNotFoundException {
        throwIfNotOnline();
        throwIfNotInWorld();
        updateLastActivity();
        Area currentArea = currentLocation.getArea();
        Interactable interactable = currentArea.getInteractable(spatialId);
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
    public void manageNotification(@NotNull final UUID notificationId, final NotificationAction action) throws NotificationNotFoundException,
            IllegalNotificationActionException {
        throwIfNotOnline();
        updateLastActivity();
        Notification notification = notifications.get(notificationId);
        if (notification == null) {
            throw new NotificationNotFoundException("This user has no notification with this ID.", this, notificationId);
        }
        switch (action) {
            case READ:
                notification.read();
                break;
            case ACCEPT:
                notification.accept();
                break;
            case DECLINE:
                notification.decline();
                break;
            case DELETE:
                removeNotification(notification);
                return;
            default:
                throw new IllegalNotificationActionException("Invalid notification action", this, notification, false);
        }

        database.updateNotification(this, notification);
    }

    @Override
    public void setAvatar(@NotNull final Avatar avatar) {
        throwIfNotOnline();
        updateLastActivity();
        this.avatar = avatar;
        database.changeAvatar(this, avatar);

        /*
         * Wird der Avatar geändert, während sich der Benutzer innerhalb einer Welt befindet, so wird allen Benutzern
         * in dieser Welt der neue Avatar mitgeteilt. An die Freunde des Benutzers muss diese Information nicht
         * mitgeteilt werden.
         * Da der Avatar momentan nur im Startmenü geändert werden kann, wird der Fall nie eintreten.
         */
        if (currentWorld != null) {
            currentWorld.getUsers().values().stream()
                    .filter(Predicate.not(this::equals))
                    .forEach(receiver -> receiver.send(SendAction.USER_INFO, this));
        }
    }

    @Override
    public void setStatus(@NotNull final Status status) {
        this.status = status;

        // Sende geänderte Benutzerinformationen an alle relevanten Benutzer.
        updateUserInfo(true);
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
    public boolean isSprinting() {
        return isSprinting;
    }

    @Override
    public boolean isMovable() {
        return movable;
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
    public @NotNull Map<UUID, IUser> getCommunicableIUsers() {
        return Collections.unmodifiableMap(communicableUsers);
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
        Area oldArea = currentLocation != null ? currentLocation.getArea() : null;
        currentLocation = new Location(newLocation);
        updateArea(oldArea, currentLocation.getArea());
        currentLocation.getRoom().getUsers().values().forEach(receiver -> receiver.send(SendAction.AVATAR_SPAWN, this));
        updateCommunicableUsers();
    }

    /**
     * Wird von der Datenbank verwendet, um die initiale Menge von Freunden hinzuzufügen.
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
        if (friends.containsKey(user.getUserId())) {
            return;
        }

        friends.put(user.getUserId(), user);
        database.addFriendship(this, user);

        if (!user.isFriend(this)) {
            user.addFriend(this);
        }

        // Ausschließlich der eigene Benutzer muss über den neuen Freund informiert werden.
        // Der andere Benutzer wird über seine eigene addFriend() Methode informiert.
        send(SendAction.USER_INFO, user);
    }

    /**
     * Entfernt einen Benutzer aus der Liste der Freunde.
     * @param user Zu entfernender Benutzer.
     */
    public void removeFriend(@NotNull final User user) {
        if (!friends.containsKey(user.getUserId())) {
            return;
        }

        friends.remove(user.getUserId());
        database.removeFriendship(this, user);

        if (user.isFriend(this)) {
            user.removeFriend(this);
        }

        // Ausschließlich der eigene Benutzer muss über zu entfernenden Freund informiert werden.
        // Der andere Benutzer wird über seine eigene removeFriend() Methode informiert.
        send(SendAction.USER_INFO, user);
    }

    /**
     * Fügt einen Benutzer in die Liste der ignorierten Benutzer hinzu.
     * @param user Hinzuzufügender Benutzer.
     */
    public void ignoreUser(@NotNull final User user) {
        if (ignoredUsers.containsKey(user.getUserId())) {
            return;
        }

        ignoredUsers.put(user.getUserId(), user);
        updateCommunicableUsers();
        database.addIgnoredUser(this, user);

        send(SendAction.USER_INFO, user);
    }

    /**
     * Entfernt einen Benutzer aus der Liste der ignorierten Benutzer.
     * @param user Zu entfernender Benutzer.
     */
    public void unignoreUser(@NotNull final User user) {
        if (!ignoredUsers.containsKey(user.getUserId())) {
            return;
        }

        ignoredUsers.remove(user.getUserId());
        updateCommunicableUsers();
        database.removeIgnoredUser(this, user);

        send(SendAction.USER_INFO, user);
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
        this.send(SendAction.NOTIFICATION, notification);
    }

    /**
     * Entfernt dem Benutzer eine Benachrichtigung.
     * @param notification Zu entfernende Benachrichtigung.
     */
    public void removeNotification(@NotNull final Notification notification) {
        notifications.remove(notification.getNotificationId());
        database.removeNotification(this, notification);

        // Sende Löschung der Benachrichtigung an Benutzer.
        this.send(SendAction.NOTIFICATION_DELETE, notification);
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
     * @return true, wenn der Benutzer mit dem Kontext interagiert, sonst false.
     */
    public boolean isInteractingWith(@NotNull final Interactable interactable) {
        return currentInteractable != null && currentInteractable.equals(interactable);
    }

    /**
     * Gibt zurück, ob der Benutzer gerade mit einem Kontext interagiert.
     * @return true, wenn der Benutzer interagiert, sonst false.
     */
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
     * Gibt das Objekt zurück, mit dem der Benutzer momentan interagiert.
     * @return Objekt, mit dem der Benutzer interagiert oder null, wenn er mit keinem interagiert.
     */
    public @Nullable Interactable getCurrentInteractable() {
        return currentInteractable;
    }

    /**
     * Setzt das Objekt, mit dem der Benutzer momentan interagiert.
     * @param interactable Objekt, mit dem der Benutzer momentan interagiert.
     */
    public void setCurrentInteractable(@Nullable final Interactable interactable) {
        this.currentInteractable = interactable;
    }

    /**
     * Aktualisiert den Zeitpunkt, an dem der Benutzer sich das letzte Mal ausgeloggt hat.
     */
    public void updateLastLogoutTime() {
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

    public void setWorld(@Nullable final World world) {
        currentWorld = world;
    }

    /**
     * Setzt die Information, ob der Benutzer sich momentan bewegen darf.
     * @param movable true, wenn der Benutzer sich bewegen darf, sonst false.
     */
    public void setMovable(final boolean movable) {
        this.movable = movable;
    }

    /**
     * Gibt die Rollen des Benutzers innerhalb seines aktuellen Raums und allen untergeordneten Kontexten zurück.
     * @return Menge der Rollen des Benutzers in seinem aktuellen Raum und allen untergeordneten Kontexten.
     * @throws IllegalStateException wenn sich der Benutzer in keinem Raum befindet.
     */
    public @NotNull Map<Context, ContextRole> getRoomRoles() throws IllegalStateException {
        // Prüfe, ob der Benutzer in einer Welt beziehungsweise in einen Raum ist.
        if (currentWorld == null || currentLocation == null) {
            throw new IllegalStateException("User is not in a world.");
        }

        Map<Context, ContextRole> roomRoles = new HashMap<>();
        addChildRoles(roomRoles, currentLocation.getRoom());
        return roomRoles;
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
     * Gibt die Benutzer zurück, mit denen der Benutzer gerade kommunizieren kann.
     * @return Menge der Benutzer, mit denen dieser Benutzer gerade kommunizieren kann.
     */
    public @NotNull Map<UUID, User> getCommunicableUsers() {
        return new HashMap<>(communicableUsers);
    }

    /**
     * Gibt das Menü zurück, dass der Benutzer gerade geöffnet hat.
     * @return Interaktionsobjekt, mit dem der Benutzer gerade interagiert.
     */
    public @Nullable ContextMenu getCurrentMenu() {
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

    /*
     * Sendet Pakete an alle relevanten Benutzer mit der aktualisierten Benutzerinformation.
     */
    public void updateUserInfo(final boolean includeSelf) {
        final Map<UUID, User> receivers = new HashMap<>(friends);

        if (includeSelf) {
            receivers.put(userId, this);
        }

        if (currentWorld != null) {
            receivers.putAll(currentWorld.getUsers());
        }

        receivers.values().forEach(user -> user.send(SendAction.USER_INFO, this));
        // Aktualisiere die Benutzerinformation von gesperrten Benutzern auch für die Benutzer in den Welten.
        GlobalContext.getInstance().getWorlds().values().stream()
                .filter(world -> world.isBanned(this))
                .forEach(world -> world.getUsers().values().forEach(user -> user.send(SendAction.USER_INFO, this)));
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

        updateUserInfo(false);
        updateLastActivity();
    }

    public void logout() {
        if (!this.isOnline()) {
            throw new IllegalStateException("User is not logged in");
        }

        try {
            leaveWorld();
        } catch(IllegalStateException ignored) {
            // Benutzer ist nicht in einer Welt
        }

        this.clientSender = null;
        this.status = Status.OFFLINE;

        updateUserInfo(false);
        updateLastLogoutTime();
    }

    public void send(@NotNull final SendAction action, @NotNull final Object object) {
        if (this.isOnline()) {
            this.clientSender.send(action, object);
        }
    }

    /**
     * Aktualisiert die Menge der Benutzer, mit denen gerade kommuniziert werden kann.
     */
    private void updateCommunicableUsers() {
        if (currentLocation == null) {
            return;
        }
        Map<UUID, User> currentCommunicableUsers = getCommunicableUsers();
        Map<UUID, User> newCommunicableUsers = currentLocation.getArea().getCommunicableUsers(this);
        CommunicationHandler.filterIgnoredUsers(this, newCommunicableUsers);

        if (currentCommunicableUsers.keySet().equals(newCommunicableUsers.keySet())) {
            return;
        }

        communicableUsers.clear();
        communicableUsers.putAll(newCommunicableUsers);

        this.send(SendAction.COMMUNICABLES, this);

        Sets.symmetricDifference(currentCommunicableUsers.entrySet(), newCommunicableUsers.entrySet())
                .forEach(entry -> entry.getValue().updateCommunicableUsers());
    }

    /**
     * Aktualisiert die Datenstrukturen der enthaltenen Benutzer in Kontexten nach einer Positionsänderung.
     * @param oldArea der alte Bereich des Benutzers.
     * @param newArea der neue Bereich des Benutzers.
     */
    public void updateArea(@Nullable final Area oldArea, @Nullable final Area newArea) {
        if (newArea != null) {
            if (oldArea != null) {
                Context lastCommonAncestor = oldArea.lastCommonAncestor(newArea);
                lastCommonAncestor.getChildren().values().forEach(child -> child.removeUser(this));
            }
            newArea.addUser(this);
        }
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
}