package model.database;

import com.badlogic.gdx.utils.SortedIntList;
import model.MessageBundle;
import model.context.Context;
import model.context.ContextID;
import model.context.global.GlobalContext;
import model.context.spatial.AreaReservation;
import model.context.spatial.SpatialMap;
import model.context.spatial.World;
import model.notification.AreaManagingRequest;
import model.notification.Notification;
import model.notification.NotificationType;
import model.notification.RoomRequest;
import model.role.ContextRole;
import model.role.Role;
import model.user.Avatar;
import model.user.User;

import java.sql.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;


public class Database implements IUserAccountManagerDatabase, IUserDatabase, IContextDatabase {
    private static final String dbURL = "jdbc:derby:ChatiDB;create=true";
    private static Database database;

    private Database() {

/*
        dropTable("USER_ACCOUNT");
        dropTable("WORLDS");
        dropTable("BAN");
        dropTable("IGNORE");
        dropTable("FRIENDSHIP");
        dropTable("USER_RESERVATION");
        dropTable("ROLE_WITH_CONTEXT");
        dropTable("NOTIFICATION");

 */



        initialize();
    }



    @Override
    public void addWorld(World world) {

        try {
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
            Connection con = DriverManager.getConnection(dbURL);
            PreparedStatement ps = con.prepareStatement("INSERT INTO WORLDS(WORLD_ID, WORLD_NAME, MAP_NAME) values(?,?,?)");
            ps.setString(2, world.getContextName());
            ps.setString(1, world.getContextId().getId());
            ps.setString(3, world.getMap().name());
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
            PreparedStatement ps = con.prepareStatement("DELETE FROM WORLDS WHERE WORLD_ID = " + "'" + worldId + "'");
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
                System.out.println("world mehr als 1 or not exist");
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
                ps.setString(5, user.getAvatar().name());
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

                //noch ohne ContextRole, Freundesliste, ignoredUser, Notifikationen
                User user = new User(userID, user_name, user_avatar, localDateTime);

                //Context_Role
                ResultSet res_cr = st.executeQuery("SELECT * FROM ROLE_WITH_CONTEXT WHERE USER_ID = " + "'" +
                        userID.toString()+ "'");
                Map<Context, ContextRole> contextRoles = new HashMap<>();
                while (res_cr.next()) {
                    Role role = Role.valueOf(res_cr.getString("USER_ROLE"));
                    ContextID context_id = new ContextID(res_cr.getString("CONTEXT_ID"));
                    Context context = GlobalContext.getInstance().getContext(context_id);

                    if (contextRoles.containsKey(context)) {
                        ContextRole already_add = contextRoles.get(context);
                        already_add.addRole(role);
                    } else {
                        ContextRole context_role = new ContextRole(user, context, role);
                        contextRoles.put(context, context_role);
                    }
                }
                user.addRoles(contextRoles);


                //Freundesliste
                ResultSet res_fr = st.executeQuery("SELECT * FROM FRIENDSHIP WHERE USER_ID1 = " + "'" +
                        userID.toString()+ "' OR USER_ID2 = '" + userID.toString() + "'");
                Map<UUID, User> friends = new HashMap<>();

                Set<UUID> friends_id = new HashSet<>();
                while (res_fr.next()) {
                    UUID friend_id = null;
                    if (res_fr.getString("USER_ID1").equals(userID.toString())) {
                        friend_id = UUID.fromString(res_fr.getString("USER_ID2"));
                    } else if (res_fr.getString("USER_ID2").equals(userID.toString())) {
                        friend_id = UUID.fromString(res_fr.getString("USER_ID1"));
                    }
                    if (friends_id != null && !friends_id.contains(friend_id)) {
                        friends_id.add(friend_id);
                    }
                }
                //erzeuge friend
                if (friends_id.size() != 0) {
                    for (UUID id : friends_id) {
                        User friend = null;
                        ResultSet friend_res = st.executeQuery("SELECT * FROM USER_ACCOUNT WHERE USER_ID = " + "'" +
                                id.toString()+ "'");
                        int f = 0;
                        while (friend_res.next()){
                            f ++;
                        }
                        if (f == 1) {
                            friend_res.first();

                            String friend_name = friend_res.getString("USER_NAME");
                            String friend_avatar_name = friend_res.getString("AVATAR_NAME");
                            //Last logout time
                            Timestamp friend_last_logout_time = friend_res.getTimestamp("LAST_ONLINE_TIME");
                            long friend_time = friend_last_logout_time.getTime();
                            LocalDateTime friend_localDateTime = Instant.ofEpochMilli(friend_time).atZone(ZoneOffset.ofHours(8)).toLocalDateTime();
                            //Avatar
                            Avatar friend_avatar = null;
                            if (avatar_name != null) {
                                friend_avatar = Avatar.valueOf(friend_avatar_name);
                            }

                            friend = new User(id, friend_name, friend_avatar, friend_localDateTime);
                        }
                        if (!friends.containsKey(id)) {
                            friends.put(id, friend);
                        }
                    }
                }
                user.addFriends(friends);

                //IgnoredUser
                ResultSet res_ig = st.executeQuery("SELECT * FROM IGNORE WHERE USER_ID = " + "'" +
                        userID.toString() + "'");
                Map<UUID, User> ignores = new HashMap<>();

                Set<UUID> ignores_id = new HashSet<>();
                while (res_ig.next()) {
                    UUID ignore_id = UUID.fromString(res_ig.getString("IGNORED_ID"));
                    if (!ignores_id.contains(ignore_id)) {
                        ignores_id.add(ignore_id);
                    }
                }
                //erzeuge ignoredUser
                if (ignores_id.size() != 0) {
                    for (UUID id : ignores_id) {
                        User ignore_user = null;
                        ResultSet ignore_res = st.executeQuery("SELECT * FROM USER_ACCOUNT WHERE USER_ID = " + "'" +
                                id.toString()+ "'");
                        int f = 0;
                        while (ignore_res.next()){
                            f ++;
                        }
                        if (f == 1) {
                            ignore_res.first();
                            String ignore_name = ignore_res.getString("USER_NAME");
                            String ignore_avatar_name = ignore_res.getString("AVATAR_NAME");
                            //Last logout time
                            Timestamp ignore_last_logout_time = ignore_res.getTimestamp("LAST_ONLINE_TIME");
                            long ignore_time = ignore_last_logout_time.getTime();
                            LocalDateTime ignore_localDateTime = Instant.ofEpochMilli(ignore_time).atZone(ZoneOffset.ofHours(8)).toLocalDateTime();
                            //Avatar
                            Avatar ignore_avatar = null;

                            if (ignore_avatar_name != null) {
                                ignore_avatar = Avatar.valueOf(ignore_avatar_name);
                            }
                            ignore_user = new User(id, ignore_name, ignore_avatar, ignore_localDateTime);
                        }
                        if (!ignores.containsKey(id)) {
                            ignores.put(id, ignore_user);
                        }
                    }
                }
                user.addIgnoredUsers(ignores);

                //Notification
                ResultSet res_notif = st.executeQuery("SELECT * FROM NOTIFICATION WHERE USER_ID = " + "'" +
                        userID.toString()+ "'");
                Map<UUID, Notification> notifications = new HashMap<>();
                while (res_notif.next()) {
                    UUID notif_id = UUID.fromString(res.getString("NOTIFICATION_ID"));
                    ContextID owing_context_id = new ContextID(res_notif.getString("OWING_CONTEXT_ID"));
                    LocalDateTime send_time = res.getTimestamp("SEND_TIME").toLocalDateTime();
                    String message_key = res.getString("MESSAGE_KEY");
                    String notif_type = res.getString("NOTIFICATION_TYPE");

                    //Arguments
                    int count = 0;
                    while (res_notif.getString(7 + count) != null) {
                        count ++;
                    }
                    Object[] argus = new Object[count];
                    /*
                    //AreaManagingRequest
                    if (count == 4) {
                        String requester_name = res.getString("AEGUMENT1");
                        String area_name = res.getString("ARGUMENT2");
                        String from_string = res.getString("ARGUMENT3");
                        LocalDateTime from = LocalDateTime.parse(from_string);
                        String to_string = res.getString("ARGUMENT4");
                        LocalDateTime to = LocalDateTime.parse(to_string);

                        //Notification notif = new AreaManagingRequest(user, )
                    }

                     */
                    if (count == 0) {
                        MessageBundle notif = new MessageBundle(message_key);
                        String argument_i;
                    }
                }
                user.addNotifications(notifications);

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
                //Last logout time
                Timestamp last_logout_time = res.getTimestamp("LAST_ONLINE_TIME");
                long time = last_logout_time.getTime();
                LocalDateTime localDateTime = Instant.ofEpochMilli(time).atZone(ZoneOffset.ofHours(8)).toLocalDateTime();
                //Avatar
                String avatar_name = res.getString("AVATAR_NAME");
                Avatar user_avatar = null;
                if (avatar_name != null){
                    user_avatar = Avatar.valueOf(avatar_name);
                    //System.out.println(user_avatar.getName());
                }


                //noch ohne ContextRole, Freundesliste, ignoredUser, Notifikationen
                User user = new User(UUID.fromString(user_id), username, user_avatar, localDateTime);

                //Context_Role
                ResultSet res_cr = st.executeQuery("SELECT * FROM ROLE_WITH_CONTEXT WHERE USER_ID = " + "'" +
                        user_id.toString()+ "'");
                Map<Context, ContextRole> contextRoles = new HashMap<>();
                while (res_cr.next()) {
                    Role role = Role.valueOf(res_cr.getString("USER_ROLE"));
                    ContextID context_id = new ContextID(res_cr.getString("CONTEXT_ID"));
                    Context context = GlobalContext.getInstance().getContext(context_id);

                    if (contextRoles.containsKey(context)) {
                        ContextRole already_add = contextRoles.get(context);
                        already_add.addRole(role);
                    } else {
                        ContextRole context_role = new ContextRole(user, context, role);
                        contextRoles.put(context, context_role);
                    }
                }
                user.addRoles(contextRoles);


                //Freundesliste
                ResultSet res_fr = st.executeQuery("SELECT * FROM FRIENDSHIP WHERE USER_ID1 = " + "'" +
                        user_id.toString()+ "' OR USER_ID2 = '" + user_id.toString() + "'");
                Map<UUID, User> friends = new HashMap<>();

                Set<UUID> friends_id = new HashSet<>();
                while (res_fr.next()) {
                    UUID friend_id = null;
                    if (res_fr.getString("USER_ID1").equals(user_id.toString())) {
                        friend_id = UUID.fromString(res_fr.getString("USER_ID2"));
                    } else if (res_fr.getString("USER_ID2").equals(user_id.toString())) {
                        friend_id = UUID.fromString(res_fr.getString("USER_ID1"));
                    }
                    if (friends_id != null && !friends_id.contains(friend_id)) {
                        friends_id.add(friend_id);
                    }
                }
                //erzeuge friend
                if (friends_id.size() != 0) {
                    for (UUID id : friends_id) {
                        User friend = null;
                        ResultSet friend_res = st.executeQuery("SELECT * FROM USER_ACCOUNT WHERE USER_ID = " + "'" +
                                id.toString()+ "'");
                        int f = 0;
                        while (friend_res.next()){
                            f ++;
                        }
                        if (f == 1) {
                            friend_res.first();

                            String friend_name = friend_res.getString("USER_NAME");
                            String friend_avatar_name = friend_res.getString("AVATAR_NAME");
                            //Last logout time
                            Timestamp friend_last_logout_time = friend_res.getTimestamp("LAST_ONLINE_TIME");
                            long friend_time = friend_last_logout_time.getTime();
                            LocalDateTime friend_localDateTime = Instant.ofEpochMilli(friend_time).atZone(ZoneOffset.ofHours(8)).toLocalDateTime();
                            //Avatar
                            Avatar friend_avatar = null;
                            if (avatar_name != null) {
                                friend_avatar = Avatar.valueOf(friend_avatar_name);
                            }

                            friend = new User(id, friend_name, friend_avatar, friend_localDateTime);
                        }
                        if (!friends.containsKey(id)) {
                            friends.put(id, friend);
                        }
                    }
                }
                user.addFriends(friends);

                //IgnoredUser
                ResultSet res_ig = st.executeQuery("SELECT * FROM IGNORE WHERE USER_ID = " + "'" +
                        user_id.toString() + "'");
                Map<UUID, User> ignores = new HashMap<>();

                Set<UUID> ignores_id = new HashSet<>();
                while (res_ig.next()) {
                    UUID ignore_id = UUID.fromString(res_ig.getString("IGNORED_ID"));
                    if (!ignores_id.contains(ignore_id)) {
                        ignores_id.add(ignore_id);
                    }
                }
                //erzeuge ignoredUser
                if (ignores_id.size() != 0) {
                    for (UUID id : ignores_id) {
                        User ignore_user = null;
                        ResultSet ignore_res = st.executeQuery("SELECT * FROM USER_ACCOUNT WHERE USER_ID = " + "'" +
                                id.toString()+ "'");
                        int f = 0;
                        while (ignore_res.next()){
                            f ++;
                        }
                        if (f == 1) {
                            ignore_res.first();
                            String ignore_name = ignore_res.getString("USER_NAME");
                            String ignore_avatar_name = ignore_res.getString("AVATAR_NAME");
                            //Last logout time
                            Timestamp ignore_last_logout_time = ignore_res.getTimestamp("LAST_ONLINE_TIME");
                            long ignore_time = ignore_last_logout_time.getTime();
                            LocalDateTime ignore_localDateTime = Instant.ofEpochMilli(ignore_time).atZone(ZoneOffset.ofHours(8)).toLocalDateTime();
                            //Avatar
                            Avatar ignore_avatar = null;

                            if (ignore_avatar_name != null) {
                                ignore_avatar = Avatar.valueOf(ignore_avatar_name);
                            }
                            ignore_user = new User(id, ignore_name, ignore_avatar, ignore_localDateTime);
                        }
                        if (!ignores.containsKey(id)) {
                            ignores.put(id, ignore_user);
                        }
                    }
                }
                user.addIgnoredUsers(ignores);

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
                String id = res.getString("USER_ID");
                UUID user_ID = UUID.fromString(id);
                String user_name = res.getString("USER_NAME");

                //Last logout time
                Timestamp last_logout_time = res.getTimestamp("LAST_ONLINE_TIME");
                long time = last_logout_time.getTime();
                LocalDateTime localDateTime = Instant.ofEpochMilli(time).atZone(ZoneOffset.ofHours(8)).toLocalDateTime();
                //Avatar
                String avatar_name = res.getString("AVATAR_NAME");
                Avatar user_avatar = null;
                if (avatar_name != null){
                    user_avatar = Avatar.valueOf(avatar_name);
                }
                //TODO
                User user = new User(user_ID, user_name, user_avatar,
                        localDateTime);
                users.put(user_ID, user);
            }

            //Role, Freundesliste, IgnoredUser
            Iterator<Map.Entry<UUID, User>> entries = users.entrySet().iterator();
            while (entries.hasNext()) {
                Map.Entry<UUID, User> user = entries.next();
                UUID user_id = user.getKey();
                System.out.println(user_id);
                //Context_Role
                ResultSet res_cr = st.executeQuery("SELECT * FROM ROLE_WITH_CONTEXT WHERE USER_ID = " + "'" +
                        user_id.toString()+ "'");
                Map<Context, ContextRole> contextRoles = new HashMap<>();
                while (res_cr.next()) {
                    Role role = Role.valueOf(res_cr.getString("USER_ROLE"));
                    ContextID context_id = new ContextID(res_cr.getString("CONTEXT_ID"));
                    Context context = GlobalContext.getInstance().getContext(context_id);

                    if (contextRoles.containsKey(context)) {
                        ContextRole already_add = contextRoles.get(context);
                        already_add.addRole(role);
                    } else {
                        ContextRole context_role = new ContextRole(user.getValue(), context, role);
                        contextRoles.put(context, context_role);
                    }
                }
                user.getValue().addRoles(contextRoles);


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
            PreparedStatement ps = con.prepareStatement("INSERT INTO NOTIFICATION" +
                    "(USER_ID, NOTIFICATION_ID, OWING_CONTEXT_ID, " +
                    "SEND_TIME, MESSAGE_KEY, NOTIFICATION_TYPE, ARGUMENT1, ARGUMENT2, ARGUMENT3, ARGUMENT4) " +
                    "values(?,?,?,?,?,?,?,?,?,?)");
            ps.setString(1, user.getUserId().toString());
            ps.setString(2, notification.getNotificationId().toString());
            ps.setString(3, notification.getContext().getContextId().getId());
            long timestamp = notification.getTimestamp().toInstant(ZoneOffset.of("+8")).toEpochMilli();
            ps.setTimestamp(4, new Timestamp(timestamp));
            //Timestamp time = Timestamp.valueOf(notification.getTimestamp());
            //ps.setTimestamp(4, time);

            MessageBundle messageBundle = notification.getMessageBundle();
            //Speichern Message_key
            ps.setString(5, messageBundle.getMessageKey());
            ps.setString(6, notification.getNotificationType().name());
            //Anzahl der Arguments
            int argu_count = messageBundle.getArguments().length;
            if (argu_count > 4) {
                //wegen bei Notification-Table nur 4 Plaetze fuer Arguments
                System.out.println("Notification Argument mehr als 4");
            }

            //speichern andere Arguments
            int i = 0;
            while(i < argu_count) {
                ps.setString((7 + i), messageBundle.getArguments()[i].toString());
                i++;
            }
            //Stelle in anderen unbenoetigen Plaetze null
            while(i < 4) {
                ps.setString(7 + i, null);
                i++;
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
                    String sql = "CREATE TABLE USER_ACCOUNT(USER_ID VARCHAR(36), USER_NAME VARCHAR(16) not null, " +
                            "USER_PSW VARCHAR(128) not null, LAST_ONLINE_TIME TIMESTAMP, AVATAR_NAME VARCHAR(16))";
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
                String sql = "CREATE TABLE USER_RESERVATION(USER_ID VARCHAR(36), START_TIME TIMESTAMP, " +
                        "END_TIME TIMESTAMP, CONTEXT_ID VARCHAR(36))";
                statement.execute(sql);

            }
            if (!set.contains("ROLE_WITH_CONTEXT")) {
                String sql = "CREATE TABLE ROLE_WITH_CONTEXT(USER_ID VARCHAR(36), USER_ROLE VARCHAR(16), " +
                        "CONTEXT_ID VARCHAR(36))";
                statement.execute(sql);

            }
            if (!set.contains("NOTIFICATION")) {
                String sql = "CREATE TABLE NOTIFICATION(USER_ID VARCHAR(36), NOTIFICATION_ID VARCHAR(36), " +
                        "OWING_CONTEXT_ID VARCHAR(36), SEND_TIME TIMESTAMP, MESSAGE_KEY VARCHAR(16), " +
                        "NOTIFICATION_TYPE VARCHAR(16), ARGUMENT1 VARCHAR(128), ARGUMENT2 VARCHAR(128), ARGUMENT3 VARCHAR(128), " +
                        "ARGUMENT4 VARCHAR(128))";
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