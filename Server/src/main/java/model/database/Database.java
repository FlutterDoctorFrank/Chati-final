package model.database;

import com.badlogic.gdx.utils.SortedIntList;
import model.context.Context;
import model.context.ContextID;
import model.context.spatial.AreaReservation;
import model.context.spatial.SpatialMap;
import model.context.spatial.World;
import model.notification.Notification;
import model.notification.RoomRequest;
import model.role.Role;
import model.user.Avatar;
import model.user.User;

import java.sql.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;


public class Database implements IUserAccountManagerDatabase, IUserDatabase, IContextDatabase {
    private static final String dbURL = "jdbc:derby:ChatiDB;create=true";
    private static Database database;

    private Database() {

        dropTable("USER_ACCOUNT");
        dropTable("WORLDS");
        dropTable("BAN");
        dropTable("IGNORE");
        dropTable("FRIENDSHIP");
        dropTable("USER_RESERVATION");
        dropTable("ROLE_WITH_CONTEXT");
        dropTable("NOTIFICATION");

        initialize();
    }



    @Override
    public void addWorld(World world) {

        try {
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
            Connection con = DriverManager.getConnection(dbURL);
            PreparedStatement ps = con.prepareStatement("INSERT INTO WORLDS(WORLD_ID, WORLD_NAME, MAP_NAME) values(?,?,?)");
            ps.setString(1, world.getContextName());
            ps.setString(2, world.getContextId().getId());
            ps.setString(3, world.getMap().getName());
            ps.executeUpdate();
            con.close();
        } catch (Exception e) {
            System.out.print("Fehler in Database-addWorld: " + e);
        }

    }

    @Override
    public void removeWorld(World world) {

        try {
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
            Connection con = DriverManager.getConnection(dbURL);
            String worldId = world.getContextId().getId();
            PreparedStatement ps = con.prepareStatement("DELETE FROM WORLDS WHERE ID = " + "'" + worldId + "'");
            ps.executeUpdate();
            con.close();
        } catch (Exception e) {
            System.out.print("Fehler in Database-removeWorld: " + e);
        }


    }

    @Override
    public World getWorld(ContextID worldID) {
        try{
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
            Connection con = DriverManager.getConnection(dbURL);
            Statement st = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);

            //Suche die Welt in Datenbank
            ResultSet res = st.executeQuery("SELECT * FROM WORLDS WHERE WORLD_ID = " + "'" + worldID.getId()+ "'");
            int row = 0;
            while (res.next()){
                row ++;
            }
            //Falls diese Welt existiert
            if (row == 1) {
                res.first();
                String world_name = res.getString("WORLD_NAME");
                String map_name = res.getString("MAP_NAME");
                SpatialMap world_map = SpatialMap.valueOf(map_name);

                World world = new World(world_name, world_map);

                return world;

            } else {
                System.out.println("mehr als 1 or not exist");
            }
            res.close();
            st.close();
            con.commit();
            con.close();
        } catch(Exception e){
            System.out.println("Fehler in getWorld: " + e);

        }
        return null;
    }

    @Override
    public Map<ContextID, World> getWorlds() {

        Map<ContextID, World> worlds = new HashMap<ContextID, World>();
        try{
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
            Connection con = DriverManager.getConnection(dbURL);
            Statement st = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);

            //Suche alle Welt in Datenbank
            ResultSet res = st.executeQuery("SELECT * FROM WORLDS");
            while(res.next()) {
                res.first();
                String id_name = res.getString("WORLD_ID");
                String world_name = res.getString("WORLD_NAME");
                String map_name = res.getString("MAP_NAME");
                SpatialMap world_map = SpatialMap.valueOf(map_name);

                ContextID world_id = new ContextID(id_name);
                World world = new World(world_name, world_map);
                worlds.put(world_id, world);
            }
            res.close();
            st.close();
            con.commit();
            con.close();
        } catch(Exception e){
            System.out.println("Fehler in getWorlds: " + e);

        }
        return worlds;
    }


    //password --> hash; salt?
    // Ja, das muss hier passieren in dieser Klasse. Wir schicken das Passwort ja in Reinform, und sollen es nur als
    // Hash und Salt speichern. Das können wir dann zusammen überlegen, raoul
    @Override
    public User createAccount(String username, String password) {
        try {
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
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
                Timestamp currentTime = new Timestamp(System.currentTimeMillis());
                ps.setString(4, currentTime.toString());
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
        } catch (Exception e) {
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
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
            Connection con = DriverManager.getConnection(dbURL);
            PreparedStatement ps = con.prepareStatement("UPDATE USER_ACCOUNT SET USER_PSW = " + "'" + newPassword + "'" +
                    " WHERE USER_ID = " + "'" + user.getUserId().toString() + "'");
            ps.executeUpdate();
            con.close();
        } catch (Exception e) {
            System.out.print("Fehler in Database-setPassword: " + e);
        }

    }

    @Override
    public void changeAvatar(User user, Avatar avatar) {

        try {
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
            Connection con = DriverManager.getConnection(dbURL);
            PreparedStatement ps = con.prepareStatement("UPDATE USER_ACCOUNT SET AVATAR_NAME = " + "'" + avatar.getName() + "'" +
                    " WHERE USER_ID = " + "'" + user.getUserId().toString() + "'");
            ps.executeUpdate();
            con.close();
        } catch (Exception e) {
            System.out.print("Fehler in Database-changeAvatar: " + e);
        }
    }

    @Override
    public void updateLastOnlineTime(User user) {

        try {
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
            Connection con = DriverManager.getConnection(dbURL);
            Timestamp currentTime = new Timestamp(System.currentTimeMillis());
            PreparedStatement ps = con.prepareStatement("UPDATE USER_ACCOUNT SET LAST_ONLINE_TIME = '" +
                    currentTime.toString() + "' WHERE USER_ID = '" + user.getUserId().toString() + "'");
            ps.executeUpdate();
            con.close();
        } catch (Exception e) {
            System.out.print("Fehler in Database-updateLastOnlineTime: " + e);
        }


    }

    @Override
    public void deleteAccount(User user) {

        try {
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
            Connection con = DriverManager.getConnection(dbURL);
            PreparedStatement ps = con.prepareStatement("DELETE FROM USER_ACCOUNT WHERE USER_ID = '"
                        + user.getUserId().toString() + "'");
            ps.executeUpdate();
            con.close();
        } catch (Exception e) {
            System.out.print("Fehler in Database-deleteAccount: " + e);
        }


    }

    @Override
    public User getUser(UUID userID) {
        try{
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
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
                //Last logout time
                Timestamp last_logout_time = res.getTimestamp("LAST_ONLINE_TIME");
                long time = last_logout_time.getTime();
                LocalDateTime localDateTime = Instant.ofEpochMilli(time).atZone(ZoneOffset.ofHours(8)).toLocalDateTime();
                //Avatar
                Avatar user_avatar = null;
                if (avatar_name != null){
                    user_avatar = Avatar.valueOf(avatar_name);
                }


                //!!! noch nicht bearbeitet!!!
                //TODO
                User user = new User(userID, user_name, user_avatar, localDateTime,
                        null, null, null, null);

                return user;

            } else {
                System.out.println("mehr als 1 or not exist");
            }
            res.close();
            st.close();
            con.commit();
            con.close();
        } catch(Exception e){
            System.out.println("Fehler in Database-getUser(userID): " +e);

        }
        return null;
    }

    @Override
    public User getUser(String username) {
        try{
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
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
                Timestamp last_logout_time = res.getTimestamp("LAST_ONLINE_TIME");
                String avatar_name = res.getString("AVATAR_NAME");
                Avatar user_avatar = null;
                if (avatar_name != null){
                    user_avatar = Avatar.valueOf(avatar_name);
                    //System.out.println(user_avatar.getName());
                }

                //!!! noch nicht bearbeitet!!!
                //TODO
                User user = new User(UUID.fromString(user_id), username, user_avatar, null, null,
                        null, null, null);

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

        } catch(Exception e){
            System.out.println("Fehler in Database-getUser(username): " + e);

        }
        return null;
    }

    //alle
    @Override
    public Map<UUID, User> getUsers() {
        Map<UUID, User> users = new HashMap<UUID, User>();
        try{
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
            Connection con = DriverManager.getConnection(dbURL);
            Statement st = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);

            //Suche alle Benutzer in Datenbank
            ResultSet res = st.executeQuery("SELECT * FROM USER_ACCOUNT");
            while(res.next()) {
                res.first();
                String id = res.getString("USER_ID");
                UUID user_ID = UUID.fromString(id);
                String user_name = res.getString("USER_NAME");
                String avatar_name = res.getString("AVATAR_NAME");
                Timestamp last_logout_time = res.getTimestamp("LAST_ONLINE_TIME");
                Avatar user_avatar = null;
                if (avatar_name != null){
                    user_avatar = Avatar.valueOf(avatar_name);
                }
                //TODO
                User user = new User(user_ID, user_name, user_avatar, null, null,
                        null, null, null);
                users.put(user_ID, user);
            }
            res.close();
            st.close();
            con.commit();
            con.close();
        } catch(Exception e){
            System.out.println("Fehler in getUsers: " + e);

        }
        return users;
    }

    @Override
    public void addFriendship(User first, User second) {

        try {
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
            Connection con = DriverManager.getConnection(dbURL);
            PreparedStatement ps = con.prepareStatement("INSERT INTO FRIENDSHIP(USER_ID1, USER_ID2) values(?,?)");
            ps.setString(1, first.getUserId().toString());
            ps.setString(2, second.getUserId().toString());
            ps.executeUpdate();
            con.close();
        } catch (Exception e) {
            System.out.print("Fehler in Database-addFriendship: " + e);
        }

    }

    @Override
    public void removeFriendship(User first, User second) {

        try {
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
            Connection con = DriverManager.getConnection(dbURL);
            PreparedStatement ps = con.prepareStatement("DELETE FROM FRIENDSHIP WHERE (USER_ID1 = '"
                    + first.getUserId().toString() + "'" + " AND USER_ID2 = '" + second.getUserId().toString()
                    + "') OR (USER_ID1 = '" + second.getUserId().toString() + "'" + " AND USER_ID2 = '" + first.getUserId().toString() + "')");
            ps.executeUpdate();
            con.close();
        } catch (Exception e) {
            System.out.print("Fehler in Database-removeFriendship: " + e);
        }


    }

    @Override
    public void addIgnoredUser(User ignoringUser, User ignoredUser) {

        try {
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
            Connection con = DriverManager.getConnection(dbURL);
            PreparedStatement ps = con.prepareStatement("INSERT INTO IGNORE(USER_ID, IGNORED_ID) values(?,?)");
            ps.setString(1, ignoringUser.getUserId().toString());
            ps.setString(2, ignoredUser.getUserId().toString());
            ps.executeUpdate();
            con.close();
        } catch (Exception e) {
            System.out.print("Fehler in Database-addIgnoredUser: " + e);
        }




    }

    @Override
    public void removeIgnoredUser(User ignoringUser, User ignoredUser) {


        try {
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
            Connection con = DriverManager.getConnection(dbURL);
            PreparedStatement ps = con.prepareStatement("DELETE FROM IGNORE WHERE (USER_ID = '"
                    + ignoringUser.getUserId().toString() + "'" + " AND IGNORED_ID = '" + ignoredUser.getUserId().toString() + "')");
            ps.executeUpdate();
            con.close();
        } catch (Exception e) {
            System.out.print("Fehler in Database-removeIgnoredUser: " + e);
        }




    }

    @Override
    public void addRole(User user, Context context, Role role) {

        try {
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
            Connection con = DriverManager.getConnection(dbURL);
            PreparedStatement ps = con.prepareStatement("INSERT INTO ROLE_WITH_CONTEXT " +
                    "(USER_ID, USER_ROLE, CONTEXT_ID) values(?,?,?)");
            ps.setString(1, user.getUserId().toString());
            ps.setString(2, role.name());
            ps.setString(3, context.getContextId().getId());
            ps.executeUpdate();
            con.close();
        } catch (Exception e) {
            System.out.print("Fehler in Database-addRole: " + e);
        }


    }

    @Override
    public void removeRole(User user, Context context, Role role) {

        try {
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
            Connection con = DriverManager.getConnection(dbURL);
            PreparedStatement ps = con.prepareStatement("DELETE FROM ROLE_WITH_CONTEXT WHERE (USER_ID = '"
                    + user.getUserId().toString() + "' AND USER_ROLE = '" + role.name()
                    + "' AND CONTEXT_ID = '" + context.getContextId().getId() + "')");
            ps.executeUpdate();
            con.close();
        } catch (Exception e) {
            System.out.print("Fehler in Database-removeRole: " + e);
        }


    }

    @Override
    public void addNotification(User user, Notification notification) {
        try {
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
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
        } catch (Exception e) {
            System.out.print("Fehler in Database-addNotification: " + e);
        }

    }

    @Override
    public void removeNotification(User user, Notification notification) {
        try {
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
            Connection con = DriverManager.getConnection(dbURL);
            PreparedStatement ps = con.prepareStatement("DELETE FROM NOTIFICATION WHERE NOTIFICATION_ID = '"
                    + notification.getNotificationId().toString() + "'");
            ps.executeUpdate();
            con.close();
        } catch (Exception e) {
            System.out.print("Fehler in Database-removeNotification: " + e);
        }
    }

    @Override
    public void addBannedUser(User user, Context world) {

        try {
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
            Connection con = DriverManager.getConnection(dbURL);
            PreparedStatement ps = con.prepareStatement("INSERT INTO BAN(USER_ID, WORLD_ID) values(?,?)");
            ps.setString(1, user.getUserId().toString());
            ps.setString(2, world.getContextId().getId());
            ps.executeUpdate();
            con.close();
        } catch (Exception e) {
            System.out.print("Fehler in Database-addBannedUser: " + e);
        }


    }

    @Override
    public void removeBannedUser(User user, Context world) {
        try {
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
            Connection con = DriverManager.getConnection(dbURL);
            PreparedStatement ps = con.prepareStatement("DELETE FROM BAN WHERE (USER_ID = '"
                    + user.getUserId().toString() + "' AND WORLD_ID = '" + world.getContextId().getId() + "')");
            ps.executeUpdate();
            con.close();
        } catch (Exception e) {
            System.out.print("Fehler in Database-addBannedUser: " + e);
        }


    }

    @Override
    public void addAreaReservation(AreaReservation contextReservation) {
        try {
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
            Connection con = DriverManager.getConnection(dbURL);
            PreparedStatement ps = con.prepareStatement("INSERT INTO USER_RESERVATION(USER_ID, START_TIME, END_TIME, CONTEXT_ID) " +
                    "values(?,?,?,?)");
            ps.setString(1, contextReservation.getReserver().getUserId().toString());
            ps.setTimestamp(2, Timestamp.valueOf(contextReservation.getFrom()));
            ps.setTimestamp(3, Timestamp.valueOf(contextReservation.getTo()));
            ps.setString(4, contextReservation.getArea().getContextId().getId());
            ps.executeUpdate();
            con.close();
        } catch (Exception e) {
            System.out.print("Fehler in Database-addAreaReservation: " + e);
        }

    }

    @Override
    public void removeAreaReservation(AreaReservation contextReservation) {
        try {
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
            Connection con = DriverManager.getConnection(dbURL);
            String user_id = contextReservation.getReserver().getUserId().toString();
            String context_id = contextReservation.getArea().getContextId().getId();
            Timestamp from = Timestamp.valueOf(contextReservation.getFrom());
            Timestamp to = Timestamp.valueOf(contextReservation.getTo());

            PreparedStatement ps = con.prepareStatement("DELETE FROM USER_RESERVATION WHERE (USER_ID = '"
                    + user_id + "' AND START_TIME = " + from + " AND END_TIME = " + to +
                    " AND CONTEXT_ID = '" + context_id + "')");
            ps.executeUpdate();
            con.close();
        } catch (Exception e) {
            System.out.print("Fehler in Database-addBannedUser: " + e);
        }
    }

    public static void initialize() {
        try {
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
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
                        "LAST_ONLINE_TIME TIMESTAMP, AVATAR_NAME VARCHAR(16))";
                statement.execute(sql);

            }
            if (!set.contains("WORLDS")) {
                String sql = "CREATE TABLE WORLDS(WORLD_ID VARCHAR(36), WORLD_NAME VARCHAR(16), MAP_NAME VARCHAR(16))";
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
                String sql = "CREATE TABLE ROLE_WITH_CONTEXT(USER_ID VARCHAR(36), USER_ROLE CHAR(10), CONTEXT_ID VARCHAR(36))";
                statement.execute(sql);

            }
            if (!set.contains("NOTIFICATION")) {
                String sql = "CREATE TABLE NOTIFICATION(USER_ID VARCHAR(36), NOTIFICATION_ID VARCHAR(36), OWING_CONTEXT_ID VARCHAR(36), " +
                        "REQUESTER_ID VARCHAR(36), MESSAGE_KEY VARCHAR(16), ARGUMENTS VARCHAR(128), SEND_TIME TIMESTAMP, REQUESTING_CONTEXT_ID VARCHAR(36), " +
                        "REQUEST_TYPE VARCHAR(16))";
                statement.execute(sql);

            }
            statement.close();
            con.close();
            //DriverManager.getConnection("jdbc:derby:E:/DBTest;shutdown=true");
        } catch (Exception e){
            System.out.print("Fehler in Database-init " + e );
            e.printStackTrace();
        }

        // TODO
    }

    private void dropTable(String tableName){
        try {
            Connection con = DriverManager.getConnection(dbURL);
            Statement st = con.createStatement();
            st.executeUpdate("DROP TABLE " + tableName);
            st.close();
            con.close();
        } catch (SQLException e) {
            System.out.print("Fehler in dropTable: " + e);
        }
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