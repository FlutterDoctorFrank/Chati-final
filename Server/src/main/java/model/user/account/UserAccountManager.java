package model.user.account;

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

public class UserAccountManager implements IUserAccountManager {
    private static final String USERNAME_FORMAT = "^\\w{2,16}";
    private static final String PASSWORD_FORMAT = "^.{4,32}";

    private static UserAccountManager userAccountManager;
    private final Map<UUID, User> registeredUsers;
    private final IUserAccountManagerDatabase database;

    private UserAccountManager() {
        database = Database.getUserAccountManagerDatabase();
        registeredUsers = database.getUsers();
    }

    @Override
    public void registerUser(String username, String password) throws IllegalAccountActionException {
        if (!username.matches(USERNAME_FORMAT))  {
            throw new IllegalAccountActionException("", "Der Benutzername hat ein falsches Format.");
        }
        if (!password.matches(PASSWORD_FORMAT)) {
            throw new IllegalAccountActionException("", "Das Passwort hat ein falsches Format.");
        }
        if (isRegistered(username)) {
            throw new IllegalAccountActionException("", "Ein Konto mit dem Benutzernamen existiert bereits.");
        }
        User createdUser = database.createAccount(username, password);
        registeredUsers.put(createdUser.getUserID(), createdUser);
    }

    @Override
    public User loginUser(String username, String password) throws IllegalAccountActionException {
        User user;
        try {
            user = getUser(username);
        } catch (UserNotFoundException e) {
            throw new IllegalAccountActionException("", "Login fehlgeschlagen. Bitte überprüfe deine Eingaben.", e);
        }
        if (!database.checkPassword(username, password)) {
            throw new IllegalAccountActionException("", "Login fehlgeschlagen. Bitte überprüfe deine Eingaben.");
        }
        if (user.isOnline()) {
            throw new IllegalAccountActionException("", "Das Konto ist bereits verbunden.");
        }
        user.setStatus(Status.ONLINE);
        GlobalContext.getInstance().addUser(user);
        return user;
    }

    @Override
    public void logoutUser(UUID userID) throws UserNotFoundException {
        User user = getUser(userID);
        if (!user.isOnline()) {
            return;
        }
        GlobalContext.getInstance().removeUser(user);
        user.setStatus(Status.OFFLINE);
        database.updateLastOnlineTime(user);
    }

    @Override
    public void deleteUser(UUID userID, String password) throws UserNotFoundException, IllegalAccountActionException {
        User user = getUser(userID);
        if (!database.checkPassword(user.getUsername(), password)) {
            throw new IllegalAccountActionException("", "Das eingegebene Passwort ist nicht korrekt.");
        }
        logoutUser(userID);
        registeredUsers.remove(userID);
        database.deleteAccount(user);
    }

    @Override
    public void changePassword(UUID userID, String password, String newPassword) throws UserNotFoundException, IllegalAccountActionException {
        User user = getUser(userID);
        if (!database.checkPassword(user.getUsername(), password)) {
            throw new IllegalAccountActionException("", "Das eingegebene Passwort ist nicht korrekt.");
        }
        if (!newPassword.matches(PASSWORD_FORMAT)) {
            throw new IllegalAccountActionException("", "Das neue Passwort hat nicht das richtige Format.");
        }
        database.setPassword(user, newPassword);
    }

    public static UserAccountManager getInstance() {
        if (userAccountManager == null) {
            userAccountManager = new UserAccountManager();
        }
        return userAccountManager;
    }

    public boolean isRegistered(String username) {
        return registeredUsers.entrySet().stream()
                .anyMatch(entry -> entry.getValue().getUsername().equals(username));
    }

    public boolean isRegistered(UUID userID) {
        return registeredUsers.containsKey(userID);
    }

    public User getUser(String username) throws UserNotFoundException {
        try {
            return registeredUsers.entrySet().stream()
                    .filter(entry -> entry.getValue().getUsername().equals(username))
                    .findFirst().orElseThrow().getValue();
        } catch(NoSuchElementException e) {
            throw new UserNotFoundException("User does not exist.", username, e);
        }
    }

    public User getUser(UUID userID) throws UserNotFoundException {
        User user = registeredUsers.get(userID);
        if (user == null) {
            throw new UserNotFoundException("User does not exist.", userID);
        }
        return user;
    }

    public Map<UUID, User> getUsersWithPermission(Context context, Permission permission) {
        return registeredUsers.entrySet().stream()
                .filter(entry -> entry.getValue().hasPermission(context, permission))
                .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}