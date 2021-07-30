package model.database;

import model.user.Avatar;
import model.user.User;

import java.util.Map;
import java.util.UUID;

public interface IUserAccountManagerDatabase {
    User createAccount(String username, String password);
    boolean checkPassword(String username, String password);
    void setPassword(User user, String newPassword);
    void updateLastOnlineTime(User user);
    void deleteAccount(User user);
    User getUser(UUID userID);
    User getUser(String username);
    Map<UUID, User> getUsers();
}
