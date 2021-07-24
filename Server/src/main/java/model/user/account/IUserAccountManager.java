package model.user.account;

import model.exception.IllegalAccountActionException;
import model.exception.IllegalActionException;
import model.exception.UserNotFoundException;
import model.user.IUser;

import java.util.UUID;

public interface IUserAccountManager {
    public void registerUser(String username, String password) throws IllegalAccountActionException;
    public IUser loginUser(String username, String password) throws IllegalAccountActionException;
    public void logoutUser(UUID userID) throws UserNotFoundException, IllegalActionException;
    public void deleteUser(UUID userID, String password) throws UserNotFoundException, IllegalAccountActionException;
    public void changePassword(UUID userID, String password, String newPassword) throws UserNotFoundException, IllegalAccountActionException;
}
