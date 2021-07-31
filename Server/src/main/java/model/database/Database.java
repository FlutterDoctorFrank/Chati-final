package model.database;

import model.context.Context;
import model.context.ContextID;
import model.context.spatial.ContextReservation;
import model.context.spatial.SpatialContext;
import model.notification.Notification;
import model.notification.RoomRequest;
import model.role.Role;
import model.user.Avatar;
import model.user.User;

import java.sql.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

import static java.sql.Connection.TRANSACTION_NONE;

public class Database implements IUserAccountManagerDatabase, IUserDatabase, IContextDatabase {
    private static final String dbURL = "jdbc:derby:ChatiDB;create=true";
    private static Database database;

    private Database() {
        initialize();
    }



    @Override
    public void addWorld(SpatialContext world) {

        try {
            Connection con = DriverManager.getConnection(dbURL);
            PreparedStatement ps = con.prepareStatement("INSERT INTO WORLDS(NAME, ID) values(?,?)");
            ps.setString(1, world.getContextName());
            ps.setString(2, world.getContextId().getId());
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
            String worldId = world.getContextId().getId();
            PreparedStatement ps = con.prepareStatement("DELETE FROM WORLDS WHERE ID = " + "'" + worldId + "'");
            ps.executeUpdate();
            con.close();
        } catch (SQLException e) {
            System.out.print("Fehler in Database-removeWorld: " + e);
        }


    }

    @Override
    public SpatialContext getWorld(ContextID worldID) {
        // TODO
        return null;
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
            Statement st = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);

            //Pruefe ob username schon besitzt
            ResultSet res = st.executeQuery("SELECT * FROM USER_ACCOUNT WHERE USER_NAME = " + "'" +username+ "'");
            int row = 0;
            while (res.next()){
                row ++;
            }

            User user = null;
            if (row == 0) {
                user = new User(username);
                String sql = "INSERT INTO USER_ACCOUNT(USER_ID, USER_NAME, USER_PSW, LAST_ONLINE_TIME, AVATAR_NAME) values(?,?,?,?,?)";
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setString(1, user.getUserId().toString());
                ps.setString(2, username);
                ps.setString(3, password);
                ps.setString(4, null);
                ps.setString(5, null);
                ps.executeUpdate();

            } else {
                System.out.println("Name ist besitzt");
            }
            //res.close();
            //st.close();
            //con.commit();
            con.close();
            return user;
        } catch (SQLException e) {
            System.out.print("Fehler in Database-createAccount: " + e);
        }
        return null;
    }

    @Override
    public boolean checkPassword(String username, String password) {
        try{
            Connection con = DriverManager.getConnection(dbURL);
            Statement st = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);

            ResultSet res = st.executeQuery("SELECT * FROM USER_ACCOUNT WHERE USER_NAME = " + "'" + username + "'");
            int row = 0;
            while (res.next()){
                row ++;
            }
            System.out.println(row);

            if (row == 1) {
                res.first();

                String realPassword = res.getString("USER_PSW");
                boolean result =  realPassword.equals(password);
                return result;
            } else {
                System.out.println("User nicht existiert");
            }
            res.close();
            st.close();
            con.close();
            //con.commit();
        } catch(SQLException e){
            System.out.println("Fehler in Database-checkPassword: " + e);

        }
        return false;
    }
    @Override
    public void setPassword(User user, String newPassword) {
        try {
            Connection con = DriverManager.getConnection(dbURL);

            //TODO
            con.close();
        } catch (SQLException e) {
            System.out.print("Fehler in Database-setPassword: " + e);
        }

    }

    @Override
    public void changeAvatar(User user, Avatar avatar) {

        try {
            Connection con = DriverManager.getConnection(dbURL);
            PreparedStatement ps = con.prepareStatement("UPDATE USER_ACCOUNT SET AVATAR_NAME = " + "'" + avatar.getName() + "'" +
                    "WHERE USER_ID = " + "'" + user.getUserId().toString() + "'");
            ps.executeUpdate();
            con.close();
        } catch (SQLException e) {
            System.out.print("Fehler in Database-changeAvatar: " + e);
        }


    }

    @Override
    public void updateLastOnlineTime(User user) {

        //!!!!!!Timestamp noch zu bearbeiten
        //now
        try {
            Connection con = DriverManager.getConnection(dbURL);
            PreparedStatement ps = con.prepareStatement("UPDATE USER_ACCOUNT SET LAST_ONLINE_TIME = " + null +
                    "WHERE USER_ID = " + "'" + user.getUserId().toString() + "'");
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
            PreparedStatement ps = con.prepareStatement("DELETE FROM USER_ACCOUNT WHERE USER_ID = '"
                        + user.getUserId().toString() + "'");
            ps.executeUpdate();
            con.close();
        } catch (SQLException e) {
            System.out.print("Fehler in Database-deleteAccount: " + e);
        }


    }

    @Override
    public User getUser(UUID userID) {
        try{
            Connection con = DriverManager.getConnection(dbURL);
            Statement st = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);

            //Suche der User in Datenbank
            ResultSet res = st.executeQuery("SELECT * FROM USER_ACCOUNT WHERE USER_ID = " + "'" + userID.toString()+ "'");
            int row = 0;
            while (res.next()){
                row ++;
            }

            //Falls dieser User existiert
            if (row == 1) {
                res.first();
                String user_name = res.getString("USER_NAME");
                String avatar_name = res.getString("AVATAR_NAME");
                Avatar user_avatar = Avatar.valueOf(avatar_name);


                //!!! noch nicht bearbeitet!!!
                //TODO
                User user = new User(userID, user_name, user_avatar, null, null, null, null, null);

                return user;

            } else {
                System.out.println("mehr als 1 or not exist");
            }
            res.close();
            st.close();
            con.commit();
            con.close();
        } catch(SQLException e){
            System.out.println(e);

        }
        return null;
    }

    @Override
    public User getUser(String username) {
        try{
            Connection con = DriverManager.getConnection(dbURL);
            Statement st = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);

            //Suche der User in Datenbank
            ResultSet res = st.executeQuery("SELECT * FROM USER_ACCOUNT WHERE USER_NAME = " + "'" + username+ "'");
            int row = 0;
            while (res.next()){
                row ++;
            }

            //Falls dieser User existiert
            if (row == 1) {
                res.first();
                String user_id = res.getString("USER_ID");
                String avatar_name = res.getString("AVATAR_NAME");

                //!!!
                Avatar user_avatar = null;

                //!!! noch nicht bearbeitet!!!
                //TODO
                User user = new User(UUID.fromString(user_id), username, user_avatar, null, null, null, null, null);

                return user;

            } else if (row == 0){
                System.out.println("User not exist");
            } else {
                System.out.println("mehr als 1 exist");
            }
            //res.close();
            //st.close();
            //con.commit();
            con.close();

        } catch(SQLException e){
            System.out.println(e);

        }
        return null;
    }

    //alle
    @Override
    public Map<UUID, User> getUsers() {
        return new HashMap<>(); // TODO
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
            PreparedStatement ps = con.prepareStatement("DELETE FROM FRIENDSHIP WHERE (USER_ID1 = '"
                    + first.getUserId().toString() + "'" + " AND USER_ID2 = '" + second.getUserId().toString()
                    + "') OR (USER_ID1 = '" + second.getUserId().toString() + "'" + " AND USER_ID2 = '" + first.getUserId().toString() + "')");
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
            PreparedStatement ps = con.prepareStatement("DELETE FROM IGNORE WHERE (USER_ID = '"
                    + ignoringUser.getUserId().toString() + "'" + " AND IGNORED_ID = '" + ignoredUser.getUserId().toString() + "')");
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
            ps.setString(3, context.getContextId().getId());
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
            PreparedStatement ps = con.prepareStatement("DELETE FROM ROLE_WITH_CONTEXT WHERE (USER_ID = '"
                    + user.getUserId().toString() + "' AND ROLE = '" + role.name()
                    + "' AND CONTEXT_ID = '" + context.getContextId().getId() + "')");
            ps.executeUpdate();
            con.close();
        } catch (SQLException e) {
            System.out.print("Fehler in Database-removeRole: " + e);
        }


    }

    @Override
    public void addNotification(User user, Notification notification) {
        try {
            Connection con = DriverManager.getConnection(dbURL);
            PreparedStatement ps = con.prepareStatement("INSERT INTO NOTIFICATION " +
                    "(USER_ID CHAR, NOTIFICATION_ID CHAR, OWING_CONTEXT_ID CHAR, " +
                    "REQUESTER_ID CHAR, MESSAGE_KEY CHAR, ARGUMENTS CHAR, SEND_TIME TIMESTAMP , REQUESTING_CONTEXT_ID CHAR, " +
                    "REQUEST_TYPE CHAR) values(?,?,?,?,?,?,?,?,?)");
            ps.setString(1, user.getUserId().toString());
            ps.setString(2, notification.getNotificationId().toString());
            ps.setString(3, notification.getContext().getContextId().getId());
            if (notification.isRequest() == true){
                if (notification instanceof RoomRequest) {

                    //TODO
                    //vielleicht noch ein paar getter in Request-Klassen?

                }
            } else {
                ps.setString(4, null);
                ps.setString(5, null);
                ps.setString(6, null);
                //send_time noch zu bearbeiten
                ps.setString(7, null);
                ps.setString(8, null);
                ps.setString(9, null);

            }

            ps.executeUpdate();
            con.close();
        } catch (SQLException e) {
            System.out.print("Fehler in Database-addNotification: " + e);
        }

    }

    @Override
    public void removeNotification(User user, Notification notification) {
        try {
            Connection con = DriverManager.getConnection(dbURL);
            PreparedStatement ps = con.prepareStatement("DELETE FROM NOTIFICATION WHERE NOTIFICATION_ID = '"
                    + notification.getNotificationId().toString() + "'");
            ps.executeUpdate();
            con.close();
        } catch (SQLException e) {
            System.out.print("Fehler in Database-removeNotification: " + e);
        }
    }

    @Override
    public void addBannedUser(User user, Context world) {

        try {
            Connection con = DriverManager.getConnection(dbURL);
            PreparedStatement ps = con.prepareStatement("INSERT INTO BAN(USER_ID, WORLD_ID) values(?,?)");
            ps.setString(1, user.getUserId().toString());
            ps.setString(2, world.getContextId().getId());
            ps.executeUpdate();
            con.close();
        } catch (SQLException e) {
            System.out.print("Fehler in Database-addBannedUser: " + e);
        }


    }

    @Override
    public void removeBannedUser(User user, Context world) {
        try {
            Connection con = DriverManager.getConnection(dbURL);
            PreparedStatement ps = con.prepareStatement("DELETE FROM BAN WHERE (USER_ID = '"
                    + user.getUserId().toString() + "' AND WORLD_ID = '" + world.getContextId().getId() + "')");
            ps.executeUpdate();
            con.close();
        } catch (SQLException e) {
            System.out.print("Fehler in Database-addBannedUser: " + e);
        }


    }

    @Override
    public void addAreaReservation(ContextReservation contextReservation) {
        // TODO
    }

    @Override
    public void removeAreaReservation(ContextReservation contextReservation) {
        // TODO
    }

    public static void initialize() {
        try {
            Connection con = DriverManager.getConnection(dbURL);
            Statement statement = con.createStatement();

            //Suche alle Namen von schon existierten tables
            DatabaseMetaData meta = con.getMetaData();
            ResultSet res = meta.getTables(null, null, null, new String[]{"TABLE"});
            HashSet<String> set=new HashSet<String>();
            while (res.next()) {
                set.add(res.getString("TABLE_NAME"));
            }
            //System.out.println(set);

            //Pruefe ob alle tables existieren, falls nicht dann create
            if (!set.contains("USER_ACCOUNT")) {
                    String sql = "CREATE TABLE USER_ACCOUNT(USER_ID VARCHAR(36), USER_NAME VARCHAR(16) not null, USER_PSW VARCHAR(128) not null, " +
                        "LAST_ONLINE_TIME TIMESTAMP, AVATAR_NAME CHAR)";
                statement.execute(sql);

            }
            if (!set.contains("WORLDS")) {
                String sql = "CREATE TABLE WORLDS(WORLD_ID VARCHAR(36), WORLD_NAME CHAR)";
                statement.execute(sql);

            }
            if (!set.contains("BAN")) {
                String sql = "CREATE TABLE BAN(USER_ID VARCHAR(36), WORLD_ID VARCHAR(36))";
                statement.execute(sql);

            }
            if (!set.contains("IGNORE")) {
                String sql = "CREATE TABLE IGNORE(USER_ID VARCHAR(36), IGNORED_ID VARCHAR(36))";
                statement.execute(sql);

            }
            if (!set.contains("FRIENDSHIP")) {
                String sql = "CREATE TABLE FRIENDSHIP(USER_ID1 VARCHAR(36), USER_ID2 VARCHAR(36))";
                statement.execute(sql);

            }
            if (!set.contains("USER_RESERVATION")) {
                String sql = "CREATE TABLE USER_RESERVATION(USER_ID VARCHAR(36), START_TIME TIMESTAMP, END_TIME TIMESTAMP, CONTEXT_ID VARCHAR(36))";
                statement.execute(sql);

            }
            if (!set.contains("ROLE_WITH_CONTEXT")) {
                String sql = "CREATE TABLE ROLE_WITH_CONTEXT(USER_ID VARCHAR(36), ROLE CHAR, CONTEXT_ID VARCHAR(36))";
                statement.execute(sql);

            }
            if (!set.contains("NOTIFICATION")) {
                String sql = "CREATE TABLE NOTIFICATION(USER_ID VARCHAR(36), NOTIFICATION_ID VARCHAR(36), OWING_CONTEXT_ID VARCHAR(36), " +
                        "REQUESTER_ID VARCHAR(36), MESSAGE_KEY CHAR, ARGUMENTS CHAR, SEND_TIME TIMESTAMP, REQUESTING_CONTEXT_ID VARCHAR(36), " +
                        "REQUEST_TYPE CHAR)";
                statement.execute(sql);

            }
            statement.close();
            con.close();
            //DriverManager.getConnection("jdbc:derby:E:/DBTest;shutdown=true");
        } catch (SQLException e){
            System.out.print("Fehler in Database-init " + e );
        }

        // TODO
    }

    private static Database getInstance() {
        if (database == null) {
            database = new Database();
        }
        return database;
    }

    public static IUserAccountManagerDatabase getUserAccountManagerDatabase() {
        return getInstance();
    }

    public static IUserDatabase getUserDatabase() {
        return getInstance();
    }

    public static IContextDatabase getContextDatabase() {
        return getInstance();
    }
}