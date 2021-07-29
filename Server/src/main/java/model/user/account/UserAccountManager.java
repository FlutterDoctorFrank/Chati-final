package model.user.account;

import controller.network.ClientSender;
import model.context.Context;
import model.context.global.GlobalContext;
import model.database.Database;
import model.database.IUserAccountManagerDatabase;
import model.exception.IllegalAccountActionException;
import model.exception.UserNotFoundException;
import model.role.Permission;
import model.user.Status;
import model.user.User;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Eine Klasse, welche der Verwaltung von Benutzerkonten dient. Kann nur einmal instanziiert
 * werden.
 */
public class UserAccountManager implements IUserAccountManager {

    /** Regulärer Ausdruck für das Format von Benutzernamen */
    private static final String USERNAME_FORMAT = "^\\w{2,16}";

    /** Regulärer Ausdruck für das Format des Passwortds */
    private static final String PASSWORD_FORMAT = "^.{4,32}";

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
        registeredUsers = database.getUsers();
    }

    @Override
    public void registerUser(String username, String password) throws IllegalAccountActionException {
        // Überprüfe, ob der übergeben Benutzername dem vorgegebenen Format entspricht.
        if (!username.matches(USERNAME_FORMAT))  {
            throw new IllegalAccountActionException("errorMsg console", "Der Benutzername hat ein falsches Format. - key");
        }
        // Überprüfe, ob das übergebene Passwort dem vorgegebenen Format entspricht.
        if (!password.matches(PASSWORD_FORMAT)) {
            throw new IllegalAccountActionException("errorMsg console", "Das Passwort hat ein falsches Format. - key");
        }
        // Überprüfe, ob bereits ein Konto mit diesem Benutzernamen existiert.
        if (isRegistered(username)) {
            throw new IllegalAccountActionException("errorMsg console", "Ein Konto mit dem Benutzernamen existiert bereits. - key");
        }
        // Erzeuge Benutzer und füge ihn zu den registrierten Benutzern hinzu.
        User createdUser = database.createAccount(username, password);
        registeredUsers.put(createdUser.getUserId(), createdUser);
    }

    @Override
    public User loginUser(String username, String password, ClientSender sender) throws IllegalAccountActionException {
        User user;
        // Überprüfe, ob ein Benutzer mit dem übergebenen Benutzernamen existiert.
        try {
            user = getUser(username);
        } catch (UserNotFoundException e) {
            throw new IllegalAccountActionException("", "Login fehlgeschlagen. Bitte überprüfe deine Eingaben.", e);
        }
        // Überprüfe, ob das übergebene Passwort korrekt ist.
        if (!database.checkPassword(username, password)) {
            throw new IllegalAccountActionException("", "Login fehlgeschlagen. Bitte überprüfe deine Eingaben.");
        }
        // Überprüfe, ob der Benutzer bereits eingeloggt ist.
        if (user.isOnline()) {
            throw new IllegalAccountActionException("", "Das Konto ist bereits verbunden.");
        }
        // Melde den Benutzer an.
        user.setClientSender(sender);
        user.setStatus(Status.ONLINE);
        GlobalContext.getInstance().addUser(user);
        return user;
    }

    @Override
    public void logoutUser(UUID userId) throws UserNotFoundException {
        User user = getUser(userId);
        // Überprüfe, ob der Benutzer angemeldet ist.
        if (!user.isOnline()) {
            return;
        }
        // Melde den Benutzer ab.
        GlobalContext.getInstance().removeUser(user);
        user.setStatus(Status.OFFLINE);
        database.updateLastOnlineTime(user);
    }

    @Override
    public void deleteUser(UUID userId, String password) throws UserNotFoundException, IllegalAccountActionException {
        User user = getUser(userId);
        // Überprüfe, ob das aktuelle Passwort korrekt übergeben wurde.
        if (!database.checkPassword(user.getUsername(), password)) {
            throw new IllegalAccountActionException("", "Das eingegebene Passwort ist nicht korrekt.");
        }
        // Melde den Benutzer ab und lösche sein Benutzerkonto.
        logoutUser(userId);
        registeredUsers.remove(userId);
        database.deleteAccount(user);
    }

    @Override
    public void changePassword(UUID userId, String password, String newPassword) throws UserNotFoundException, IllegalAccountActionException {
        User user = getUser(userId);
        // Überprüfe, ob das aktuelle Passwort korrekt übergeben wurde.
        if (!database.checkPassword(user.getUsername(), password)) {
            throw new IllegalAccountActionException("", "Das eingegebene Passwort ist nicht korrekt.");
        }
        // Überprüfe, ob das neue Passwort dem vorgegebenen Format entspricht.
        if (!newPassword.matches(PASSWORD_FORMAT)) {
            throw new IllegalAccountActionException("", "Das neue Passwort hat nicht das richtige Format.");
        }
        // Ändere das Passwort.
        database.setPassword(user, newPassword);
    }

    /**
     * Erzeugt eine Instanz der Klasse, falls diese noch nicht existiert und gibt diese zurück.
     * @return Die Instanz des UserAccountManager.
     */
    public static UserAccountManager getInstance() {
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
    public boolean isRegistered(UUID userId) {
        return registeredUsers.containsKey(userId);
    }

    /**
     * Überprüft, ob ein Benutzer mit übergebenem Benutzernamen existiert.
     * @param username Der Benutzername des Benutzers.
     * @return true, wenn der Benutzer existiert, sonst false.
     */
    public boolean isRegistered(String username) {
        return registeredUsers.entrySet().stream()
                .anyMatch(user -> user.getValue().getUsername().equals(username));
    }

    /**
     * Gibt den Benutzer mit der angegebenen ID zurück.
     * @param userId ID des zurückzugebenden Benutzers.
     * @return Benutzer.
     * @throws UserNotFoundException: wenn kein Benutzer mit der ID existiert.
     */
    public User getUser(UUID userId) throws UserNotFoundException {
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
    public User getUser(String username) throws UserNotFoundException {
        try {
            return registeredUsers.entrySet().stream()
                    .filter(user -> user.getValue().getUsername().equals(username))
                    .findFirst().orElseThrow().getValue();
        } catch(NoSuchElementException e) {
            throw new UserNotFoundException("User does not exist.", username, e);
        }
    }

    /**
     * Gibt die Menge aller Benutzer zurück, die eine bestimmte Berechtigung in einem Kontext, oder einem übergeordneten
     * Kontext von diesem, besitzen.
     * @param context Kontext, in der ein Benutzer eine Berechtigung besitzt.
     * @param permission Berechtigung, die ein Benutzer besitzt.
     * @return Menge aller Benutzer mit der Berechtigung in dem Kontext.
     */
    public Map<UUID, User> getUsersWithPermission(Context context, Permission permission) {
        return registeredUsers.entrySet().stream()
                .filter(user -> user.getValue().hasPermission(context, permission))
                .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}