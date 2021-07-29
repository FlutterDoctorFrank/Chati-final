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
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;

public class Database implements IUserAccountManagerDatabase, IUserDatabase, IGlobalContextDatabase {
    private static final String dbURL = "jdbc:derby:ChatiDB;create=true";
    private static Database database;


    @Override
    public void addWorld(SpatialContext world) {

        try {
            Connection con = DriverManager.getConnection(dbURL);
            PreparedStatement ps = con.prepareStatement("INSERT INTO WORLD(NAME, ID) values(?,?)");
            ps.setString(1, world.getContextName());
            ps.setString(2, world.getContextID().getId());
            ps.executeUpdate();
            con.close();
        } catch (SQLException e) {
            System.out.print("Fehler in Database-addWorld: " + e);
        }

    }

    @Override
    public void removeWorld(SpatialContext world) {

        try {
            Connection con = DriverManager.getConnection(dbURL);
            String worldId = world.getContextID().getId();
            PreparedStatement ps = con.prepareStatement("DELETE FROM WORLD WHERE ID = " + worldId);
            ps.executeUpdate();
            con.close();
        } catch (SQLException e) {
            System.out.print("Fehler in Database-removeWorld: " + e);
        }


    }

    @Override
    public void getWorld(ContextID worldID) {
        // TODO

    }

    @Override
    public Map<ContextID, SpatialContext> getWorlds() {
        return null; // TODO
    }

    //password --> hash; salt?
    // Ja, das muss hier passieren in dieser Klasse. Wir schicken das Passwort ja in Reinform, und sollen es nur als
    // Hash und Salt speichern. Das können wir dann zusammen überlegen, raoul
    @Override
    public User createAccount(String username, String password) {
        try {
            Connection con = DriverManager.getConnection(dbURL);

            con.close();
        } catch (SQLException e) {
            System.out.print("Fehler in Database-createAccount: " + e);
        }
        return null;
    }

    @Override
    public boolean checkPassword(String username, String password) {
        try {
            Connection con = DriverManager.getConnection(dbURL);

            con.close();
        } catch (SQLException e) {
            System.out.print("Fehler in Database-checkPassword: " + e);
        }
        return false;
    }
    @Override
    public void setPassword(User user, String newPassword) {
        try {
            Connection con = DriverManager.getConnection(dbURL);

            con.close();
        } catch (SQLException e) {
            System.out.print("Fehler in Database-setPassword: " + e);
        }

    }

    @Override
    public void changeAvatar(User user, Avatar avatar) {

        try {
            Connection con = DriverManager.getConnection(dbURL);
            PreparedStatement ps = con.prepareStatement("UPDATE USER SET AVATAR_NAME = " + avatar.getName() +
                    "WHERE USER_ID = " + user.getUserId().toString());
            ps.executeUpdate();
            con.close();
        } catch (SQLException e) {
            System.out.print("Fehler in Database-changeAvatar: " + e);
        }


    }

    @Override
    public void updateLastOnlineTime(User user) {

        try {
            Connection con = DriverManager.getConnection(dbURL);
            PreparedStatement ps = con.prepareStatement("UPDATE USER SET LAST_ONLINE_TIME = " + "???" +
                    "WHERE USER_ID = " + user.getUserId().toString());
            ps.executeUpdate();
            con.close();
        } catch (SQLException e) {
            System.out.print("Fehler in Database-updateLastOnlineTime: " + e);
        }


    }

    @Override
    public void deleteAccount(User user) {

        try {
            Connection con = DriverManager.getConnection(dbURL);
            PreparedStatement ps = con.prepareStatement("DELETE FROM USER WHERE USER_ID = "
                        + user.getUserId().toString());
            ps.executeUpdate();
            con.close();
        } catch (SQLException e) {
            System.out.print("Fehler in Database-deleteAccount: " + e);
        }


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

        try {
            Connection con = DriverManager.getConnection(dbURL);
            PreparedStatement ps = con.prepareStatement("INSERT INTO FRIENDSHIP(USER_ID1, USER_ID2) values(?,?)");
            ps.setString(1, first.getUserId().toString());
            ps.setString(2, second.getUserId().toString());
            ps.executeUpdate();
            con.close();
        } catch (SQLException e) {
            System.out.print("Fehler in Database-addFriendship: " + e);
        }

    }

    @Override
    public void removeFriendship(User first, User second) {

        try {
            Connection con = DriverManager.getConnection(dbURL);
            PreparedStatement ps = con.prepareStatement("DELETE FROM FRIENDSHIP WHERE (USER_ID1 = "
                    + first.getUserId().toString() + " AND USER_ID2 = " + second.getUserId().toString() + ") OR (USER_ID1 = "
                    + second.getUserId().toString() + " AND USER_ID2 = " + first.getUserId().toString() + ")");
            ps.executeUpdate();
            con.close();
        } catch (SQLException e) {
            System.out.print("Fehler in Database-removeFriendship: " + e);
        }


    }

    @Override
    public void addIgnoredUser(User ignoringUser, User ignoredUser) {


        try {
            Connection con = DriverManager.getConnection(dbURL);
            PreparedStatement ps = con.prepareStatement("INSERT INTO IGNORE(USER_ID, IGNORED_ID) values(?,?)");
            ps.setString(1, ignoringUser.getUserId().toString());
            ps.setString(2, ignoredUser.getUserId().toString());
            ps.executeUpdate();
            con.close();
        } catch (SQLException e) {
            System.out.print("Fehler in Database-addIgnoredUser: " + e);
        }




    }

    @Override
    public void removeIgnoredUser(User ignoringUser, User ignoredUser) {


        try {
            Connection con = DriverManager.getConnection(dbURL);
            PreparedStatement ps = con.prepareStatement("DELETE FROM IGNORE WHERE USER_ID = "
                    + ignoringUser.getUserId().toString() + " AND IGNORED_ID = " + ignoredUser.getUserId().toString());
            ps.executeUpdate();
            con.close();
        } catch (SQLException e) {
            System.out.print("Fehler in Database-removeIgnoredUser: " + e);
        }




    }

    @Override
    public void addRole(User user, Context context, Role role) {

        try {
            Connection con = DriverManager.getConnection(dbURL);
            PreparedStatement ps = con.prepareStatement("INSERT INTO ROLE_WITH_CONTEXT " +
                    "(USER_ID, ROLE, CONTEXT_ID) values(?,?,?)");
            ps.setString(1, user.getUserId().toString());
            ps.setString(2, role.name());
            ps.setString(3, context.getContextID().getId());
            ps.executeUpdate();
            con.close();
        } catch (SQLException e) {
            System.out.print("Fehler in Database-addRole: " + e);
        }


    }

    @Override
    public void removeRole(User user, Context context, Role role) {

        try {
            Connection con = DriverManager.getConnection(dbURL);
            PreparedStatement ps = con.prepareStatement("DELETE FROM ROLE_WITH_CONTEXT WHERE USER_ID = "
                    + user.getUserId().toString() + " AND ROLE = " + role.name()
                    + " AND CONTEXT_ID = " + context.getContextID().getId());
            ps.executeUpdate();
            con.close();
        } catch (SQLException e) {
            System.out.print("Fehler in Database-removeRole: " + e);
        }


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

        try {
            Connection con = DriverManager.getConnection(dbURL);
            PreparedStatement ps = con.prepareStatement("INSERT INTO BAN (USER_ID, WORLD_ID) values(?,?)");
            ps.setString(1, user.getUserId().toString());
            ps.setString(2, world.getContextID().getId());
            ps.executeUpdate();
            con.close();
        } catch (SQLException e) {
            System.out.print("Fehler in Database-addBannedUser: " + e);
        }


    }

    @Override
    public void removeBannedUser(User user, SpatialContext world) {
        try {
            Connection con = DriverManager.getConnection(dbURL);
            PreparedStatement ps = con.prepareStatement("DELETE FROM BAN WHERE USER_ID = "
                    + user.getUserId().toString() + " AND WORLD_ID = " + world.getContextID().getId());
            ps.executeUpdate();
            con.close();
        } catch (SQLException e) {
            System.out.print("Fehler in Database-addBannedUser: " + e);
        }


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