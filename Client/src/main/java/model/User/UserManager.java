package model.User;

import model.Context.Global.GlobalContext;
import model.Exceptions.NotInWorldException;
import model.Exceptions.NotLoggedInException;
import model.Exceptions.UserNotFoundException;
import view.Screens.IModelObserver;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Eine Klasse, über welche der intern angemeldete Benutzer sowie aller bekannten externen
 * Benutzer verwaltet werden können.
 */
public class UserManager implements IUserManagerController, IUserManagerView{

    static UserManager userManager;
    private User intern;
    private Map<UUID, User> externs;


    @Override
    public void setInternUser(UUID userId) {
        intern = new User(userId);
    }

    @Override
    public void addExternUser(UUID userId) {
        externs.put(userId, new User(userId));
    }

    @Override
    public void removeExternUser(UUID userId) throws UserNotFoundException {
        if(externs.remove(userId)==null){
            throw new UserNotFoundException("Client don't know this user", userId);
        }
    }

    @Override
    public IUserController getInternUser() throws NotLoggedInException {
        if (intern==null){
            throw new NotLoggedInException();
        }
        return intern;
    }

    @Override
    public IUserController getExternUser(UUID userId) throws UserNotFoundException {
        checkExternContains(userId);
        return externs.get(userId);
    }

    @Override
    public IUserView getInternUserView() throws NotLoggedInException {
        if (intern==null){
            throw new NotLoggedInException();
        }
        return intern;
    }

    @Override
    public IUserView getExternUserView(UUID userId) throws UserNotFoundException {
        checkExternContains(userId);
        return externs.get(userId);
    }

    @Override
    public Map<UUID, IUserView> getActiveUsers() throws NotInWorldException {
        CheckUserIsInWorld();
        return externs.entrySet().stream().filter(user -> user.getValue().isInCurrentWorld())
                .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Override
    public Map<UUID, IUserView> getFriends() throws NotLoggedInException {
        checkIsNotLoggedIn();
        return externs.entrySet().stream()
                .filter(user -> user.getValue().isFriend())
                .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Override
    public Map<UUID, IUserView> getBannedUsers() throws NotInWorldException {
        CheckUserIsInWorld();
        return externs.entrySet().stream()
                .filter(user -> user.getValue().isBanned())
                .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue));
    }


    /**
     * Gibt die Singleton-Instanz der Klasse zurück.
     * @return Instanz des UserManager.
     */
    public static UserManager getInstance(){
        return userManager;
    }


    /**
     * wirft eine Exeption, wenn der Benutzer in keiner Welt ist
     * @throws NotInWorldException wenn der Benutzer keiner welt beigetreten ist.
     */
    private void CheckUserIsInWorld() throws NotInWorldException {
        if (GlobalContext.getInstance().getCurrentWold()==null){
            throw new NotInWorldException("user hasn't joined a world");
        }
    }

    /**
     * wirft eine Exeption, wenn niemand eingeloggt ist
     * @throws NotLoggedInException wenn niemand eingeloggt ist
     */
    private void checkIsNotLoggedIn() throws NotLoggedInException {
        if (intern==null){
            throw new NotLoggedInException();
        }
    }

    /**
     * wirft eine Exeption, wenn ein Benutzer dem KLienten nicht bekannt ist
     * @param userId ID des Benutzers
     * @throws UserNotFoundException wenn der Benutzer nicht in externs enthalten ist
     */
    private void checkExternContains(UUID userId) throws UserNotFoundException {
        if(!externs.containsKey(userId)){
            throw new UserNotFoundException("Client don't know this user", userId);
        }
    }
}
