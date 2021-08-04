package model.user;

import model.exception.UserNotFoundException;
import view.Screens.IModelObserver;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Eine Klasse, über welche der intern angemeldete Benutzer sowie aller bekannten externen Benutzer verwaltet werden
 * können.
 */
public class UserManager implements IUserManagerController, IUserManagerView{

    /** Singleton-Instanz der Klasse. */
    private static UserManager userManager;

    /** Der auf diesem Client angemeldete Benutzer. */
    private InternUser internUser;

    /** Bekannte externe Benutzer. */
    private final Map<UUID, User> externUsers;

    /** Wird verwendet, um die View über Änderungen im Modell zu informieren. */
    protected IModelObserver modelObserver;

    /**
     * Erzeugt eine neue Instanz des UserManager.
     */
    private UserManager() {
        this.internUser = null;
        this.externUsers = new HashMap<>();
    }

    @Override
    public void setInternUser(UUID userId, String username, Status status, Avatar avatar) {
        internUser = new InternUser(userId, username, status, avatar);
    }

    @Override
    public void addExternUser(UUID userId, String username, Status status, Avatar avatar) {
        externUsers.put(userId, new User(userId, username, status, avatar));
    }

    @Override
    public void removeExternUser(UUID userId) throws UserNotFoundException {
        if (externUsers.remove(userId) == null) {
            throw new UserNotFoundException("Tried to remove an unknown extern User.", userId);
        }
    }

    @Override
    public InternUser getInternUserController() {
        throwIfNotLoggedIn();
        return internUser;
    }

    @Override
    public User getExternUserController(UUID userId) throws UserNotFoundException {
        throwIfNotExists(userId);
        return externUsers.get(userId);
    }

    @Override
    public InternUser getInternUserView() {
        throwIfNotLoggedIn();
        return internUser;
    }

    @Override
    public User getExternUserView(UUID userId) throws UserNotFoundException {
        throwIfNotExists(userId);
        return externUsers.get(userId);
    }

    @Override
    public Map<UUID, IUserView> getActiveUsers() {
        throwIfNotInWorld();
        return externUsers.values().stream().filter(User::isInCurrentWorld)
                .collect(Collectors.toUnmodifiableMap(User::getUserId, Function.identity()));
    }

    @Override
    public Map<UUID, IUserView> getFriends() {
        throwIfNotLoggedIn();
        return externUsers.values().stream().filter(User::isFriend)
                .collect(Collectors.toUnmodifiableMap(User::getUserId, Function.identity()));
    }

    @Override
    public Map<UUID, IUserView> getBannedUsers() {
        throwIfNotInWorld();
        return externUsers.values().stream().filter(User::isBanned)
                .collect(Collectors.toUnmodifiableMap(User::getUserId, Function.identity()));
    }

    /**
     * Gibt die Singleton-Instanz der Klasse zurück.
     * @return Instanz des UserManager.
     */
    public static UserManager getInstance() {
        if (userManager == null) {
            userManager = new UserManager();
        }
        return userManager;
    }

    /**
     * Gibt den an diesem Client angemeldeten Benutzer zurück.
     * @return An diesem Client angemeldeten Benutzer.
     */
    public InternUser getInternUser() {
        return internUser;
    }

    /**
     * Wirft eine Exeption, wenn auf diesem Client kein Benutzer angemeldet ist.
     * @throws IllegalStateException wenn auf diesem Client kein Benutzer angemeldet ist.
     */
    private void throwIfNotLoggedIn() {
        if (internUser == null) {
            throw new IllegalStateException("There is no user logged in on this client.");
        }
    }

    /**
     * Wirft eine Exeption, wenn der intern angemeldete Benutzer in keiner Welt ist.
     * @throws IllegalStateException wenn der intern angemeldete Benutzer in keiner Welt ist.
     */
    private void throwIfNotInWorld() {
        if (internUser.getCurrentWorld() == null) {
            throw new IllegalStateException("User is not in world.");
        }
    }

    /**
     * Wirft eine Exeption, wenn dem Client kein externer Benutzer mit der ID bekannt ist.
     * @param userId ID des gesuchten Benutzers.
     * @throws UserNotFoundException wenn dem Client kein externer Benutzer mit der ID bekannt ist.
     */
    private void throwIfNotExists(UUID userId) throws UserNotFoundException {
        if (!externUsers.containsKey(userId)) {
            throw new UserNotFoundException("No user with this ID was found.", userId);
        }
    }

    /**
     * Setzt den Beobachter des Modells, über den die View über Änderungen benachrichtigt werden kann.
     * @param modelObserver Beobachter des Modells.
     */
    public void setModelObserver(IModelObserver modelObserver) {
        this.modelObserver = modelObserver;
    }

    /**
     * Gibt den Beobachter des Modells zurück.
     * @return Beobachter des Modells.
     */
    public IModelObserver getModelObserver() {
        return modelObserver;
    }
}
