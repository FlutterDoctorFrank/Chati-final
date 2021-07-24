package model.database;

import model.user.Avatar;
import model.user.User;

import java.util.Map;
import java.util.UUID;

public interface IUserAccountManagerDatabase {
    public User createAccount(String username, String password);
    public boolean checkPassword(String username, String password);
    public void setPassword(User user, String newPassword);
    public void changeAvatar(User user, Avatar avatar);
    public void updateLastOnlineTime(User user);
    public void deleteAccount(User user);
    public User getUser(UUID userID);
    public User getUser(String username);
    public Map<UUID, User> getUsers();
}
