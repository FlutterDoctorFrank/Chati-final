package model.user;

import model.exception.UserNotFoundException;
import model.role.Permission;
import view2.IModelObserver;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Eine Klasse, über welche der intern angemeldete Benutzer sowie aller bekannten externen Benutzer verwaltet werden
 * können.
 */
public class UserManager implements IUserManagerController, IUserManagerView {

    /** Singleton-Instanz der Klasse. */
    private static UserManager userManager;

    /** Der auf diesem Client angemeldete Benutzer. */
    private InternUser internUser;

    /** Bekannte externe Benutzer. */
    private final Map<UUID, User> externUsers;

    /** Wird verwendet, um die View über Änderungen im Modell zu informieren. */
    private IModelObserver modelObserver;

    /**
     * Erzeugt eine neue Instanz des UserManager.
     */
    private UserManager() {
        this.internUser = null;
        this.externUsers = new HashMap<>();
        this.modelObserver = null;
    }

    @Override
    public void login(UUID userId, String username, Status status, Avatar avatar) {
        if (internUser != null) {
            throw new IllegalStateException("There is already a user logged in on this client.");
        }
        this.internUser = new InternUser(userId, username, status, avatar);
        UserManager.getInstance().getModelObserver().setUserInfoChanged();
    }

    @Override
    public void logout() {
        throwIfNotLoggedIn();
        UserManager.getInstance().getModelObserver().setUserInfoChanged();
        if (internUser.isInCurrentWorld) {
            UserManager.getInstance().getModelObserver().setWorldChanged();
        }
        internUser = null;
        externUsers.clear();
    }

    @Override
    public void addExternUser(UUID userId, String username, Status status, Avatar avatar) {
        this.externUsers.put(userId, new User(userId, username, status, avatar));
        UserManager.getInstance().getModelObserver().setUserInfoChanged();
    }

    @Override
    public void removeExternUser(UUID userId) throws UserNotFoundException {
        throwIfNotExists(userId);
        externUsers.remove(userId);
        UserManager.getInstance().getModelObserver().setUserInfoChanged();
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
    public Map<UUID, IUserController> getExternUsers() {
        return externUsers.values().stream().collect(Collectors.toUnmodifiableMap(User::getUserId, Function.identity()));
    }

    @Override
    public InternUser getInternUserView() {
        return internUser;
    }

    @Override
    public User getExternUserView(UUID userId) throws UserNotFoundException {
        throwIfNotExists(userId);
        return externUsers.get(userId);
    }

    @Override
    public IUserView getExternUserView(String username) throws UserNotFoundException {
        try {
            return externUsers.values().stream().filter(user -> user.getUsername().equals(username)).findFirst().orElseThrow();
        } catch (NoSuchElementException e) {
            throw new UserNotFoundException("No User with this name was found", username, e);
        }
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

    @Override
    public Map<UUID, IUserView> getUsersInRoom() {
        throwIfNotInWorld();
        return externUsers.values().stream().filter(User::isInCurrentRoom)
                .collect(Collectors.toUnmodifiableMap(User::getUserId, Function.identity()));
    }

    @Override
    public boolean isLoggedIn() {
        return internUser != null;
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
     * Wird aufgerufen, wenn der intern angemeldete Benutzer die Welt verlässt. Veranlasst, dass die Informationen
     * zu allen Kontexten innerhalb einer Welt für alle Benutzer verworfen werden. Entfernt externe Benutzer, mit denen
     * der intern angemeldete Benutzer nicht befreundet ist oder Benutzer, die nicht gesperrt sind sofern der intern
     * angemeldete Benutzer die Berechtigung zum Sperren besitzt.
     */
    public void discardWorldInfo() {
        Iterator<User> iterator = externUsers.values().iterator();
        while (iterator.hasNext()) {
            User user = iterator.next();
            if (!user.isKnown()) {
                iterator.remove();
            } else {
                user.discardWorldInfo();
            }
        }
        internUser.discardWorldInfo();
    }

    /**
     * Wird aufgerufen, wenn der intern angemeldete Benutzer einen Raum verlässt. Veranlasst, dass die Informationen
     * zu allen Kontexten innerhalb des Raums für alle Benutzer verworfen werden.
     */
    public void discardRoomInfo() {
        externUsers.values().forEach(User::discardRoomInfo);
        internUser.discardRoomInfo();
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
        if (!internUser.isInCurrentWorld()) {
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
