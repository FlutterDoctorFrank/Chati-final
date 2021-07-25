package model.database;

import model.context.Context;
import model.context.ContextID;
import model.context.spatial.AreaReservation;
import model.context.spatial.SpatialContext;
import model.notification.Notification;
import model.role.Role;
import model.user.Avatar;
import model.user.User;

import java.util.Map;
import java.util.UUID;

public class Database implements IUserAccountManagerDatabase, IUserDatabase, IGlobalContextDatabase {
    private static final String dbURL = "jdbc:derby:ChatiDB;create=true";
    private static Database database;


    @Override
    public static Connection getConnection() {

        try {
            Connection connection = DriverManager
                    .getConnection(dbURL);

            connection.setAutoCommit(false);
            return connection;
        } catch (SQLException e){
            System.out.print(e);
        }
        return null;
    }

    @Override
    public void addWorld(SpatialContext world) {
        // TODO
    }

    @Override
    public void removeWorld(SpatialContext world) {
        // TODO
    }

    @Override
    public void getWorld(ContextID worldID) {
        // TODO
    }

    @Override
    public Map<ContextID, SpatialContext> getWorlds() {
        return null; // TODO
    }

    @Override
    public User createAccount(String username, String password) {
        return null; // TODO
    }

    @Override
    public boolean checkPassword(String username, String password) {
        return false; // TODO
    }

    @Override
    public void setPassword(User user, String newPassword) {
        // TODO
    }

    @Override
    public void changeAvatar(User user, Avatar avatar) {
        // TODO
    }

    @Override
    public void updateLastOnlineTime(User user) {
        // TODO
    }

    @Override
    public void deleteAccount(User user) {

    }

    @Override
    public User getUser(UUID userID) {
        return null; // TODO
    }

    @Override
    public User getUser(String username) {
        return null; // TODO
    }

    @Override
    public Map<UUID, User> getUsers() {
        return null; // TODO
    }

    @Override
    public void addFriendship(User first, User second) {
        // TODO
    }

    @Override
    public void removeFriendship(User first, User second) {
        // TODO
    }

    @Override
    public void addIgnoredUser(User ignoringUser, User ignoredUser) {
        // TODO
    }

    @Override
    public void removeIgnoredUser(User ignoringUser, User ignoredUser) {
        // TODO
    }

    @Override
    public void addRole(User user, Context context, Role role) {
        // TODO
    }

    @Override
    public void removeRole(User user, Context context, Role role) {
        // TODO
    }

    @Override
    public void addNotification(User user, Notification notification) {
        // TODO
    }

    @Override
    public void removeNotification(User user, Notification notification) {
        // TODO
    }

    @Override
    public void addBannedUser(User user, SpatialContext world) {
        // TODO
    }

    @Override
    public void removeBannedUser(User user, SpatialContext world) {
        // TODO
    }

    @Override
    public void addAreaReservation(AreaReservation areaReservation) {
        // TODO
    }

    @Override
    public void removeAreaReservation(AreaReservation areaReservation) {
        // TODO
    }

    public static void initialize() {
        // TODO
    }

    public static IUserAccountManagerDatabase getUserAccountManagerDatabase() {
        return database;
    }

    public static IUserDatabase getUserDatabase() {
        return database;
    }

    public static IGlobalContextDatabase getGlobalContextDatabase() {
        return database;
    }
}