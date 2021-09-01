package model.user.account;

import controller.network.ClientSender;
import model.context.Context;
import model.context.global.GlobalContext;
import model.database.Database;
import model.database.IUserAccountManagerDatabase;
import model.exception.IllegalAccountActionException;
import model.exception.UserNotFoundException;
import model.role.Permission;
import model.role.Role;
import model.user.User;
import org.jetbrains.annotations.NotNull;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Eine Klasse, welche der Verwaltung von Benutzerkonten dient. Kann nur einmal instanziiert
 * werden.
 */
public class UserAccountManager implements IUserAccountManager {

    /** Zeit in Monaten, nach deren Ablauf ohne ein Einloggen in das Benutzerkonto dieses gelöscht wird. */
    public static final int ACCOUNT_DELETION_TIME = 3;

    /** Regulärer Ausdruck für das Format von Benutzernamen */
    private static final String USERNAME_FORMAT = "^\\w{2,16}$";

    /** Regulärer Ausdruck für das Format des Passworts */
    private static final String PASSWORD_FORMAT = "^.{4,32}$";

    /** Singleton-Instanz der Klasse */
    private static UserAccountManager userAccountManager;

    /** Menge aller registrierter Benutzer */
    private final Map<UUID, User> registeredUsers;

    /** Erlaubt benötigten Zugriff auf die Datenbank */
    private final IUserAccountManagerDatabase database;

    /**
     * Erzeugt eine Instanz des UserAccountManager. Initialisiert die Menge der registrierten Benutzer mit Hilfe einer
     * Instanz der Datenbank.
     */
    private UserAccountManager() {
        database = Database.getUserAccountManagerDatabase();
        registeredUsers = new HashMap<>();
    }

    public void load() {
        this.registeredUsers.clear();
        this.registeredUsers.putAll(this.database.getUsers());
    }

    @Override
    public void registerUser(@NotNull final String username, @NotNull final String password) throws IllegalAccountActionException {
        // Überprüfe, ob der übergebene Benutzername dem vorgegebenen Format entspricht.
        if (!username.matches(USERNAME_FORMAT))  {
            throw new IllegalAccountActionException("errorMsg console", "account.register.illegal-name");
        }
        // Überprüfe, ob das übergebene Passwort dem vorgegebenen Format entspricht.
        if (!password.matches(PASSWORD_FORMAT)) {
            throw new IllegalAccountActionException("errorMsg console", "account.register.illegal-password");
        }
        // Überprüfe, ob bereits ein Konto mit diesem Benutzernamen existiert.
        if (isRegistered(username)) {
            throw new IllegalAccountActionException("errorMsg console", "account.register.already-taken", username);
        }
        // Erzeuge Benutzer und füge ihn zu den registrierten Benutzern hinzu.
        User createdUser = database.createAccount(username, password);

        if (createdUser == null) {
            throw new IllegalAccountActionException("errorMsg console", "account.register.failed");
        }

        // Der erste registrierte Benutzer eines Server bekommt die Rolle des Besitzers. Sollen mehrere Benutzer diese
        // Rolle besitzen, ist dies nur über einen Konsolenbefehl im Controller oder Datenbankmanipulation möglich.
        if (registeredUsers.isEmpty()) {
            createdUser.addRole(GlobalContext.getInstance(), Role.OWNER);
        }

        //User createdUser = new User(username);
        registeredUsers.put(createdUser.getUserId(), createdUser);
    }

    @Override
    public @NotNull User loginUser(@NotNull final String username, @NotNull final String password,
                                   @NotNull final ClientSender sender) throws IllegalAccountActionException {
        User user;
        // Überprüfe, ob ein Benutzer mit dem übergebenen Benutzernamen existiert.
        try {
            user = getUser(username, false);
        } catch (UserNotFoundException ex) {
            throw new IllegalAccountActionException("", ex, "account.login.invalid-input");
        }
        // Überprüfe, ob das übergebene Passwort korrekt ist.
        if (!database.checkPassword(username, password)) {
            throw new IllegalAccountActionException("", "account.login.invalid-input");
        }
        // Überprüfe, ob der Benutzer bereits eingeloggt ist.
        if (user.isOnline()) {
            throw new IllegalAccountActionException("", "account.login.already-online");
        }
        // Melde den Benutzer an.
        user.login(sender);
        GlobalContext.getInstance().addUser(user);
        return user;
    }

    @Override
    public void logoutUser(@NotNull final UUID userId) throws UserNotFoundException {
        User user = getUser(userId);
        // Überprüfe, ob der Benutzer angemeldet ist.
        if (!user.isOnline()) {
            return;
        }
        // Melde den Benutzer ab.
        user.logout();
        GlobalContext.getInstance().removeUser(user);
        database.updateLastOnlineTime(user);
    }

    @Override
    public void deleteUser(@NotNull final UUID userId, @NotNull final String password) throws UserNotFoundException, IllegalAccountActionException {
        User user = getUser(userId);
        // Überprüfe, ob das aktuelle Passwort korrekt übergeben wurde.
        if (!database.checkPassword(user.getUsername(), password)) {
            throw new IllegalAccountActionException("", "account.delete.invalid-password");
        }
        // Melde den Benutzer ab und lösche sein Benutzerkonto.
        deleteUser(user);
    }

    @Override
    public void changePassword(@NotNull final UUID userId, @NotNull final String password,
                               @NotNull final String newPassword) throws UserNotFoundException,
            IllegalAccountActionException {
        User user = getUser(userId);
        user.updateLastActivity();
        // Überprüfe, ob das aktuelle Passwort korrekt übergeben wurde.
        if (!database.checkPassword(user.getUsername(), password)) {
            throw new IllegalAccountActionException("", "account.change-password.invalid-password");
        }
        // Überprüfe, ob das neue Passwort dem vorgegebenen Format entspricht.
        if (!newPassword.matches(PASSWORD_FORMAT)) {
            throw new IllegalAccountActionException("", "account.change-password.illegal-password");
        }
        // Ändere das Passwort.
        database.setPassword(user, newPassword);
    }

    /**
     * Erzeugt eine Instanz der Klasse, falls diese noch nicht existiert und gibt diese zurück.
     * @return Die Instanz des UserAccountManager.
     */
    public static @NotNull UserAccountManager getInstance() {
        if (userAccountManager == null) {
            userAccountManager = new UserAccountManager();
        }
        return userAccountManager;
    }

    /**
     * Überprüft, ob ein Benutzer mit übergebener ID existiert.
     * @param userId Die ID des Benutzers.
     * @return true, wenn der Benutzer existiert, sonst false.
     */
    public boolean isRegistered(@NotNull final UUID userId) {
        return registeredUsers.containsKey(userId);
    }

    /**
     * Überprüft, ob ein Benutzer mit übergebenem Benutzernamen existiert.
     * @param username Der Benutzername des Benutzers.
     * @return true, wenn der Benutzer existiert, sonst false.
     */
    public boolean isRegistered(@NotNull final String username) {
        return registeredUsers.values().stream()
                .anyMatch(user -> user.getUsername().equalsIgnoreCase(username));
    }

    /**
     * Gibt den Benutzer mit der angegebenen ID zurück.
     * @param userId ID des zurückzugebenden Benutzers.
     * @return Benutzer.
     * @throws UserNotFoundException: wenn kein Benutzer mit der ID existiert.
     */
    public @NotNull User getUser(@NotNull final UUID userId) throws UserNotFoundException {
        User user = registeredUsers.get(userId);
        if (user == null) {
            throw new UserNotFoundException("User does not exist.", userId);
        }
        return user;
    }

    /**
     * Gibt den Benutzer mit dem angegebenen Benutzernamen zurück.
     * @param username Benutzername des zurückzugebenden Benutzers.
     * @return Benutzer.
     * @throws UserNotFoundException: wenn kein Benutzer mit dem Benutzernamen existiert.
     */
    public @NotNull User getUser(@NotNull final String username) throws UserNotFoundException {
        return this.getUser(username, true);
    }

    private @NotNull User getUser(@NotNull final String username, final boolean ignoreCase) throws UserNotFoundException {
        try {
            return this.registeredUsers.values().stream()
                    .filter(user -> ignoreCase ? user.getUsername().equalsIgnoreCase(username) : user.getUsername().equals(username))
                    .findFirst().orElseThrow();
        } catch (NoSuchElementException ex) {
            throw new UserNotFoundException("User does not exist.", username, ex);
        }
    }

    /**
     * Gibt die Menge aller Benutzer zurück, die eine bestimmte Berechtigung in einem Kontext, oder einem übergeordneten
     * Kontext von diesem, besitzen.
     * @param context Kontext, in der ein Benutzer eine Berechtigung besitzt.
     * @param permission Berechtigung, die ein Benutzer besitzt.
     * @return Menge aller Benutzer mit der Berechtigung in dem Kontext.
     */
    public @NotNull Map<UUID, User> getUsersWithPermission(@NotNull final Context context,
                                                           @NotNull final Permission permission) {
        return registeredUsers.values().stream()
                .filter(user -> user.hasPermission(context, permission))
                .collect(Collectors.toMap(User::getUserId, Function.identity()));
    }

    /**
     * Löscht das Benutzerkonto eines Benutzers.
     * @param user Zu löschender Benutzer.
     * @throws UserNotFoundException wenn der Benutzer nicht existiert.
     */
    public void deleteUser(@NotNull final User user) throws UserNotFoundException {
        logoutUser(user.getUserId());

        // Gibt es möglicherweise eine Möglichkeit ohne Cast?
        user.getFriends().values().stream()
                .map(friend -> (User) friend)
                .forEach(user::removeFriend);
        GlobalContext.getInstance().getWorlds().values().stream()
                .filter(world -> world.isBanned(user))
                .forEach(world -> world.removeBannedUser(user));

        registeredUsers.remove(user.getUserId());
        database.deleteAccount(user);
    }
}