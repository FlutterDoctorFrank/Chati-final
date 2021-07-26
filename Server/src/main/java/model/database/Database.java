package model.database;

import model.context.Context;
import model.context.ContextID;
import model.context.spatial.AreaReservation;
import model.context.spatial.SpatialContext;
import model.notification.Notification;
import model.role.Role;
import model.user.Avatar;
import model.user.User;

import java.sql.Connection;
import java.util.Map;
import java.util.UUID;

public class Database implements IUserAccountManagerDatabase, IUserDatabase, IGlobalContextDatabase {
    private static final String dbURL = "jdbc:derby:ChatiDB;create=true";
    private static Database database;
    //private Connection connection;

/*
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

 */

    @Override
    public void addWorld(SpatialContext world) {
        // TODO
        /*
        PreparedStatement ps = this.connection.prepareStatement("INSERT INTO WORLD(NAME, ID) values(?,?)");
        ps.setString(1,world.getContextName);
        ps.setString(2,world.getContextID);
        ps.executeUpdate();
        this.connection.close();
         */

    }

    @Override
    public void removeWorld(SpatialContext world) {
        // TODO
        /*
        String worldId = world.getContextID;
        PreparedStatement ps = this.connection.prepareStatement("DELETE FROM WORLD WHERE ID = worldId");
        ps.executeUpdate();
        this.connection.close();
         */
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
        /*
        PreparedStatement ps = this.connection.prepareStatement("UPDATE USER SET AVATAR_NAME = avatar.getName
                WHERE USER_ID = user.getUserID");
        ps.executeUpdate();
        this.connection.close();
         */

    }

    @Override
    public void updateLastOnlineTime(User user) {
        // TODO
        /*
        PreparedStatement ps = this.connection.prepareStatement("UPDATE USER SET LAST_ONLINE_TIME = ???
                WHERE USER_ID = user.getUserID");
        ps.executeUpdate();
        this.connection.close();
         */
    }

    @Override
    public void deleteAccount(User user) {
         /*
        PreparedStatement ps = this.connection.prepareStatement("DELETE FROM USER WHERE USER_ID = user.getUserID");
        ps.executeUpdate();
        this.connection.close();
         */

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
        /*
        PreparedStatement ps = this.connection.prepareStatement("INSERT INTO FRIENDSHIP(USER_ID1, USER_ID2) values(?,?)");
        ps.setString(1,first.getUserID);
        ps.setString(2,second.getUserID);
        ps.executeUpdate();
        this.connection.close();
         */
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