package model.database;

import model.MessageBundle;
import model.context.Context;
import model.context.ContextID;
import model.context.global.GlobalContext;
import model.context.spatial.Area;
import model.context.spatial.AreaReservation;
import model.context.spatial.ContextMap;
import model.context.spatial.World;
import model.exception.ContextNotFoundException;
import model.exception.EncryptionException;
import model.exception.UserNotFoundException;
import model.notification.AreaManagingRequest;
import model.notification.FriendRequest;
import model.notification.Notification;
import model.notification.NotificationType;
import model.role.ContextRole;
import model.role.Role;
import model.user.Avatar;
import model.user.User;
import model.user.account.UserAccountManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Database implements IUserAccountManagerDatabase, IUserDatabase, IContextDatabase {

    private static final Logger LOGGER = Logger.getLogger("chati.database");
    private static final String dbURL = "jdbc:derby:ChatiDB;create=true";
    private static Database database;

    private Database() {
        initialize();
    }

    private static final String SQL_INSERT_WORLD = "INSERT INTO WORLDS(WORLD_ID, WORLD_NAME, MAP_NAME) values(?,?,?)";

    @Override
    public void addWorld(@NotNull final World world) {
        try {
            Connection con = this.getConnection();
            PreparedStatement ps = con.prepareStatement(SQL_INSERT_WORLD);

            ps.setString(1, world.getContextId().getId());
            ps.setString(2, world.getContextName());
            ps.setString(3, world.getPublicRoom().getMap().name());
            ps.executeUpdate();
            ps.close();
            con.close();
        } catch (SQLException ex) {
            LOGGER.log(Level.WARNING, "Failed to insert world " + world.getContextName() + " into the database", ex);
        }
    }

    private static final String SQL_DELETE_WORLD = "DELETE FROM WORLDS WHERE WORLD_ID = ?";

    @Override
    public void removeWorld(@NotNull final World world) {
        try {
            Connection con = this.getConnection();
            PreparedStatement ps = con.prepareStatement(SQL_DELETE_WORLD);

            ps.setString(1, world.getContextId().getId());
            ps.executeUpdate();
            ps.close();
            con.close();
        } catch (SQLException ex) {
            LOGGER.log(Level.WARNING, "Failed to delete world " + world.getContextName() + " from the database", ex);
        }
    }

    private static final String SQL_QUERY_WORLD = "SELECT WORLD_NAME, MAP_NAME FROM WORLDS WHERE WORLD_ID = ?";

    @Override
    public @Nullable World getWorld(@NotNull final ContextID worldId) {
        World world = null;

        try {
            Connection con = this.getConnection();
            PreparedStatement ps = con.prepareStatement(SQL_QUERY_WORLD, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
            ps.setString(1, worldId.getId());
            ResultSet res = ps.executeQuery();

            if (res.next()) {
                try {
                    String name = res.getString("WORLD_NAME");
                    ContextMap map = ContextMap.valueOf(res.getString("MAP_NAME"));

                    world = new World(name, map);
                } catch (IllegalArgumentException ex) {
                    LOGGER.log(Level.WARNING, "Found invalid row in table worlds", ex);
                    res.deleteRow();
                }
            } else {
                LOGGER.warning("Unable to query non-existing world: " + worldId.getId());
            }

            res.close();
            ps.close();
            con.close();
        } catch (SQLException ex) {
            LOGGER.log(Level.WARNING, "Failed to query world " + worldId.getId() + " from the database", ex);
        }

        return world;
    }

    @Override
    public @NotNull Map<ContextID, World> getWorlds() {
        Map<ContextID, World> worlds = new HashMap<>();

        try {
            Connection con = this.getConnection();
            Statement ps = con.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            ResultSet res = ps.executeQuery("SELECT WORLD_ID FROM WORLDS");

            while (res.next()) {
                World world = this.getWorld(new ContextID(res.getString("WORLD_ID")));

                if (world != null) {
                    worlds.put(world.getContextId(), world);
                }
            }

            res.close();
            ps.close();
            con.close();
        } catch (SQLException ex) {
            LOGGER.log(Level.WARNING, "Failed to query worlds from the database", ex);
        }

        return worlds;
    }

    private static final String SQL_INSERT_USER = "INSERT INTO USER_ACCOUNT(USER_ID, USER_NAME, PSW_SALT, PSW_HASH, LAST_ONLINE_TIME, AVATAR_NAME) values(?,?,?,?,?,?)";

    @Override
    public @Nullable User createAccount(@NotNull final String username, @NotNull final String password) {
        try {
            Connection con = this.getConnection();
            PreparedStatement fetch = con.prepareStatement(SQL_QUERY_USER_ID);
            fetch.setString(1, username);

            if (fetch.executeQuery().next()) {
                LOGGER.warning("Unable to create user account: Account for " + username + " already exists");

                fetch.close();
                con.close();

                return null;
            } else {
                fetch.close();

                User user = new User(username);
                PreparedStatement ps = con.prepareStatement(SQL_INSERT_USER);
                ps.setString(1, user.getUserId().toString());
                ps.setString(2, user.getUsername());

                try {
                    final String salt = PasswordEncryption.salt();
                    final String hash = PasswordEncryption.hash(password, salt);

                    ps.setString(3, salt);
                    ps.setString(4, hash);
                } catch (EncryptionException ex) {
                    LOGGER.log(Level.WARNING, "Failed to hash password to due an encryption error", ex);

                    // Failed to hash password, clear salt and write raw password into the database
                    ps.setString(3, null);
                    ps.setString(4, password);
                }

                ps.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
                ps.setString(6, user.getAvatar().toString());
                ps.executeUpdate();
                ps.close();
                con.close();

                return user;
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.WARNING, "Failed to insert user account " + username + " into the database", ex);
        }

        return null;
    }

    private static final String SQL_QUERY_PASSWORD = "SELECT PSW_SALT, PSW_HASH FROM USER_ACCOUNT WHERE USER_NAME = ?";

    @Override
    public boolean checkPassword(@NotNull final String username, @NotNull final String password) {
        try {
            Connection con = this.getConnection();
            PreparedStatement ps = con.prepareStatement(SQL_QUERY_PASSWORD,
                    ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ps.setString(1, username);
            ResultSet res = ps.executeQuery();
            boolean result;

            if (res.next()) {
                final String salt = res.getString("PSW_SALT");
                final String hash = res.getString("PSW_HASH");

                // When salt is null, then the password was not hashed, because of an error while encrypting
                result = salt == null ? password.equals(hash) : PasswordEncryption.verify(password, salt, hash);
            } else {
                LOGGER.warning("Unable to verify password of non-existing user: " + username);
                result = false;
            }

            res.close();
            ps.close();
            con.close();

            return result;
        } catch (EncryptionException ex) {
            LOGGER.log(Level.WARNING, "Failed to verify password to due an encryption error", ex);
        } catch (SQLException ex) {
            LOGGER.log(Level.WARNING, "Failed to verify password for user " + username, ex);
        }

        return false;
    }

    private static final String SQL_UPDATE_PASSWORD = "UPDATE USER_ACCOUNT SET PSW_SALT = ?, PSW_HASH = ? WHERE USER_ID = ?";

    @Override
    public void setPassword(@NotNull final User user, @NotNull final String newPassword) {
        try {
            Connection con = this.getConnection();
            PreparedStatement ps = con.prepareStatement(SQL_UPDATE_PASSWORD);

            try {
                final String salt = PasswordEncryption.salt();
                final String hash = PasswordEncryption.hash(newPassword, salt);

                ps.setString(1, salt);
                ps.setString(2, hash);
            } catch (EncryptionException ex) {
                LOGGER.log(Level.WARNING, "Failed to hash password to due an encryption error", ex);

                // Failed to hash password, clear salt and write raw password into the database
                ps.setString(1, null);
                ps.setString(2, newPassword);
            }

            ps.setString(3, user.getUserId().toString());
            ps.executeUpdate();
            ps.close();
            con.close();
        } catch (SQLException ex) {
            LOGGER.log(Level.WARNING, "Failed to update password for user " + user.getUsername(), ex);
        }
    }

    private static final String SQL_UPDATE_AVATAR = "UPDATE USER_ACCOUNT SET AVATAR_NAME = ? WHERE USER_ID = ?";

    @Override
    public void changeAvatar(@NotNull final User user, @NotNull final Avatar avatar) {
        try {
            Connection con = this.getConnection();
            PreparedStatement ps = con.prepareStatement(SQL_UPDATE_AVATAR);

            ps.setString(1, avatar.name());
            ps.setString(2, user.getUserId().toString());
            ps.executeUpdate();
            ps.close();
            con.close();
        } catch (SQLException ex) {
            LOGGER.log(Level.WARNING, "Failed to update avatar for user " + user.getUsername(), ex);
        }
    }

    private static final String SQL_UPDATE_ONLINE_TIME = "UPDATE USER_ACCOUNT SET LAST_ONLINE_TIME = ? WHERE USER_ID = ?";

    @Override
    public void updateLastOnlineTime(@NotNull final User user) {
        try {
            Connection con = this.getConnection();
            PreparedStatement ps = con.prepareStatement(SQL_UPDATE_ONLINE_TIME);

            ps.setTimestamp(1, Timestamp.valueOf(user.getLastLogout()));
            ps.setString(2, user.getUserId().toString());
            ps.executeUpdate();
            ps.close();
            con.close();
        } catch (SQLException ex) {
            LOGGER.log(Level.WARNING, "Failed to update last online time for user " + user.getUsername(), ex);
        }
    }

    private static final String SQL_DELETE_USER = "DELETE FROM USER_ACCOUNT WHERE USER_ID = ?";

    @Override
    public void deleteAccount(@NotNull final User user) {
        try {
            Connection con = this.getConnection();
            PreparedStatement ps = con.prepareStatement(SQL_DELETE_USER);

            ps.setString(1, user.getUserId().toString());
            ps.executeUpdate();
            ps.close();
            con.close();
        } catch (SQLException ex) {
            LOGGER.log(Level.WARNING, "Failed to delete user " + user.getUsername() + " from the database", ex);
        }
    }

    private static final String SQL_QUERY_USER = "SELECT USER_ID, USER_NAME, LAST_ONLINE_TIME, AVATAR_NAME FROM USER_ACCOUNT WHERE USER_ID = ?";

    @Override
    public @Nullable User getUser(@NotNull final UUID userId) {
        User user = null;

        try {
            Connection con = this.getConnection();
            PreparedStatement ps = con.prepareStatement(SQL_QUERY_USER);
            ps.setString(1, userId.toString());
            ResultSet res = ps.executeQuery();

            if (res.next()) {
                String name = res.getString("USER_NAME");
                LocalDateTime lastOnline = res.getTimestamp("LAST_ONLINE_TIME").toLocalDateTime();
                Avatar avatar;

                try {
                    avatar = Avatar.valueOf(res.getString("AVATAR_NAME"));
                } catch (IllegalArgumentException ex) {
                    LOGGER.log(Level.WARNING, "Unable to find avatar for user with id " + userId, ex);
                    avatar = Avatar.values()[new Random().nextInt(Avatar.values().length)];
                }

                user = new User(userId, name, avatar, lastOnline, true);
            } else {
                LOGGER.warning("Unable to query non-existing user with id " + userId);
            }

            res.close();
            ps.close();
            con.close();
        } catch (SQLException ex) {
            LOGGER.log(Level.WARNING, "Failed to query user with id " + userId + " from the database", ex);
        }

        return user;
    }

    private static final String SQL_QUERY_USER_ID = "SELECT USER_ID FROM USER_ACCOUNT WHERE USER_NAME = ?";

    @Override
    public @Nullable User getUser(@NotNull final String username) {
        try {
            UUID userId = null;
            Connection con = this.getConnection();
            PreparedStatement ps = con.prepareStatement(SQL_QUERY_USER_ID, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
            ps.setString(1, username);
            ResultSet res = ps.executeQuery();

            if (res.next()) {
                try {
                    userId = UUID.fromString(res.getString("USER_ID"));
                } catch (IllegalArgumentException ex) {
                    LOGGER.log(Level.WARNING, "Unable to parse user id for user " + username);
                    res.deleteRow();
                }
            } else {
                LOGGER.warning("Unable to query non-existing user: " + username);
            }

            res.close();
            ps.close();
            con.close();

            return userId != null ? this.getUser(userId) : null;
        } catch (SQLException ex) {
            LOGGER.log(Level.WARNING, "Failed to query user " + username + " from the database", ex);
        }

        return null;
    }

    @Override
    public @NotNull Map<UUID, User> getUsers() {
        Map<UUID, User> users = new HashMap<>();

        try {
            Connection con = this.getConnection();
            Statement ps = con.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            ResultSet res = ps.executeQuery("SELECT USER_ID FROM USER_ACCOUNT");

            while (res.next()) {
                try {
                    UUID userId = UUID.fromString(res.getString("USER_ID"));
                    User user = this.getUser(userId);

                    if (user != null) {
                        users.put(user.getUserId(), user);
                    }
                } catch (IllegalArgumentException ex) {
                    LOGGER.log(Level.WARNING, "Found invalid row in table user_account", ex);
                }
            }

            res.close();
            ps.close();
            con.close();

            // Load users roles, friend, ignored users and notifications...
            for (final User user : users.values()) {
                user.addRoles(this.getRoles(user));
                user.addFriends(this.getFriendship(users, user));
                user.addIgnoredUsers(this.getIgnoredUser(users, user));
                user.addNotifications(this.getNotifications(users, user));
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.WARNING, "Failed to query users from the database", ex);
            users.clear();
        }

        return users;
    }

    private static final String SQL_QUERY_FRIENDSHIP = "SELECT * FROM FRIENDSHIP WHERE USER_ID1 = ? OR USER_ID2 = ?";

    public @NotNull Map<UUID, User> getFriendship(@NotNull final Map<UUID, User> users, @NotNull final User user) {
        final Map<UUID, User> friendships = new HashMap<>();

        try {
            Connection con = this.getConnection();
            PreparedStatement ps = con.prepareStatement(SQL_QUERY_FRIENDSHIP, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
            ps.setString(1, user.getUserId().toString());
            ps.setString(2, user.getUserId().toString());
            ResultSet res = ps.executeQuery();

            while (res.next()) {
                try {
                    UUID friendId = UUID.fromString(res.getString("USER_ID1"));
                    if (friendId.equals(user.getUserId())) {
                        friendId = UUID.fromString(res.getString("USER_ID2"));
                    }
                    if (!users.containsKey(friendId)) {
                        throw new UserNotFoundException("Friend user not found", friendId);
                    }

                    friendships.put(users.get(friendId).getUserId(), users.get(friendId));
                } catch (UserNotFoundException | IllegalArgumentException ex) {
                    LOGGER.log(Level.WARNING, "Found invalid row in table friendship", ex);
                    res.deleteRow();
                }
            }

            res.close();
            ps.close();
            con.close();
        } catch (SQLException ex) {
            LOGGER.log(Level.WARNING, "Failed to query friendships for user " + user.getUsername(), ex);
            friendships.clear();
        }

        return friendships;
    }

    private static final String SQL_INSERT_FRIENDSHIP = "INSERT INTO FRIENDSHIP(USER_ID1, USER_ID2) values (?,?)";

    @Override
    public void addFriendship(@NotNull final User first, @NotNull final User second) {
        try {
            Connection con = this.getConnection();
            PreparedStatement ps = con.prepareStatement(SQL_INSERT_FRIENDSHIP);

            ps.setString(1, first.getUserId().toString());
            ps.setString(2, second.getUserId().toString());
            ps.executeUpdate();
            ps.close();
            con.close();
        } catch (SQLException ex) {
            LOGGER.log(Level.WARNING, "Failed to insert friendship into the database", ex);
        }
    }

    private static final String SQL_DELETE_FRIENDSHIP = "DELETE FROM FRIENDSHIP WHERE (USER_ID1 = ? AND USER_ID2 = ?) OR (USER_ID1 = ? AND USER_ID2 = ?)";

    @Override
    public void removeFriendship(@NotNull final User first, @NotNull final User second) {
        try {
            Connection con = this.getConnection();
            PreparedStatement ps = con.prepareStatement(SQL_DELETE_FRIENDSHIP);

            ps.setString(1, first.getUserId().toString());
            ps.setString(4, first.getUserId().toString());
            ps.setString(2, second.getUserId().toString());
            ps.setString(3, second.getUserId().toString());
            ps.executeUpdate();
            ps.close();
            con.close();
        } catch (SQLException ex) {
            LOGGER.log(Level.WARNING, "Failed to delete friendship from the database", ex);
        }
    }

    private static final String SQL_QUERY_IGNORE = "SELECT IGNORED_ID FROM IGNORE WHERE USER_ID = ?";

    public @NotNull Map<UUID, User> getIgnoredUser(@NotNull final Map<UUID, User> users, @NotNull final User user) {
        final Map<UUID, User> ignores = new HashMap<>();

        try {
            Connection con = this.getConnection();
            PreparedStatement ps = con.prepareStatement(SQL_QUERY_IGNORE, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
            ps.setString(1, user.getUserId().toString());
            ResultSet res = ps.executeQuery();

            while (res.next()) {
                try {
                    UUID ignoredId = UUID.fromString(res.getString("IGNORED_ID"));
                    if (!users.containsKey(ignoredId)) {
                        throw new UserNotFoundException("Ignored user not found", ignoredId);
                    }

                    ignores.put(users.get(ignoredId).getUserId(), users.get(ignoredId));
                } catch (UserNotFoundException | IllegalArgumentException ex) {
                    LOGGER.log(Level.WARNING, "Found invalid row in table ignore", ex);
                    res.deleteRow();
                }
            }

            res.close();
            ps.close();
            con.close();
        } catch (SQLException ex) {
            LOGGER.log(Level.WARNING, "Failed to query ignored users for " + user.getUsername(), ex);
            ignores.clear();
        }

        return ignores;
    }

    private static final String SQL_INSERT_IGNORE = "INSERT INTO IGNORE(USER_ID, IGNORED_ID) values(?,?)";

    @Override
    public void addIgnoredUser(@NotNull final User ignoringUser, @NotNull final User ignoredUser) {
        try {
            Connection con = this.getConnection();
            PreparedStatement ps = con.prepareStatement(SQL_INSERT_IGNORE);

            ps.setString(1, ignoringUser.getUserId().toString());
            ps.setString(2, ignoredUser.getUserId().toString());
            ps.executeUpdate();
            ps.close();
            con.close();
        } catch (SQLException ex) {
            LOGGER.log(Level.WARNING, "Failed to insert ignored user for " + ignoringUser.getUsername(), ex);
        }
    }

    private static final String SQL_DELETE_IGNORE = "DELETE FROM IGNORE WHERE USER_ID = ? AND IGNORED_ID = ?";

    @Override
    public void removeIgnoredUser(@NotNull final User ignoringUser, @NotNull final User ignoredUser) {
        try {
            Connection con = this.getConnection();
            PreparedStatement ps = con.prepareStatement(SQL_DELETE_IGNORE);

            ps.setString(1, ignoringUser.getUserId().toString());
            ps.setString(2, ignoredUser.getUserId().toString());
            ps.executeUpdate();
            ps.close();
            con.close();
        } catch (SQLException ex) {
            LOGGER.log(Level.WARNING, "Failed to delete ignored user for " + ignoringUser.getUsername(), ex);
        }
    }

    private static final String SQL_QUERY_ROLE = "SELECT USER_ROLE, CONTEXT_ID FROM ROLE_WITH_CONTEXT WHERE USER_ID = ?";

    public @NotNull Map<Context, ContextRole> getRoles(@NotNull final User user) {
        Map<Context, ContextRole> roles = new HashMap<>();

        try {
            Connection con = this.getConnection();
            PreparedStatement ps = con.prepareStatement(SQL_QUERY_ROLE, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
            ps.setString(1, user.getUserId().toString());
            ResultSet res = ps.executeQuery();

            while (res.next()) {
                try {
                    Role role = Role.valueOf(res.getString("USER_ROLE"));
                    ContextID contextId = new ContextID(res.getString("CONTEXT_ID"));
                    Context context = GlobalContext.getInstance().getContext(contextId);

                    if (!roles.containsKey(context)) {
                        ContextRole contextRole = new ContextRole(user, context, role);

                        roles.put(context, contextRole);
                    } else {
                        roles.get(context).addRole(role);
                    }
                } catch (ContextNotFoundException | IllegalArgumentException ex) {
                    res.deleteRow();
                }
            }

            res.close();
            ps.close();
            con.close();
        } catch (SQLException ex) {
            LOGGER.log(Level.WARNING, "Failed to query roles of user " + user.getUsername() + " from the database", ex);
            roles.clear();
        }

        return roles;
    }

    private static final String SQL_INSERT_ROLE = "INSERT INTO ROLE_WITH_CONTEXT(USER_ID, USER_ROLE, CONTEXT_ID) values(?,?,?)";

    @Override
    public void addRole(@NotNull final User user, @NotNull final Context context, @NotNull final Role role) {
        try {
            Connection con = this.getConnection();
            PreparedStatement ps = con.prepareStatement(SQL_INSERT_ROLE);

            ps.setString(1, user.getUserId().toString());
            ps.setString(2, role.name());
            ps.setString(3, context.getContextId().getId());
            ps.executeUpdate();
            ps.close();
            con.close();
        } catch (SQLException ex) {
            LOGGER.log(Level.WARNING, "Failed to insert role " + role + " for user " + user.getUsername() + " into the database", ex);
        }
    }

    private static final String SQL_DELETE_ROLE = "DELETE FROM ROLE_WITH_CONTEXT WHERE USER_ID = ? AND USER_ROLE = ? AND CONTEXT_ID = ?";

    @Override
    public void removeRole(@NotNull final User user, @NotNull final Context context, @NotNull final Role role) {
        try {
            Connection con = this.getConnection();
            PreparedStatement ps = con.prepareStatement(SQL_DELETE_ROLE);

            ps.setString(1, user.getUserId().toString());
            ps.setString(2, role.name());
            ps.setString(3, context.getContextId().toString());
            ps.executeUpdate();
            ps.close();
            con.close();
        } catch (SQLException ex) {
            LOGGER.log(Level.WARNING, "Failed to delete role " + role + " of user " + user.getUsername() + " from the database", ex);
        }
    }

    private static final String SQL_QUERY_NOTIFICATION = "SELECT * FROM NOTIFICATION WHERE USER_ID = ?";

    public @NotNull Map<UUID, Notification> getNotifications(@NotNull final Map<UUID, User> users, @NotNull final User user) {
        final Map<UUID, Notification> notifications = new HashMap<>();

        try {
            Connection con = this.getConnection();
            PreparedStatement ps = con.prepareStatement(SQL_QUERY_NOTIFICATION, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
            ps.setString(1, user.getUserId().toString());
            ResultSet res = ps.executeQuery();

            while (res.next()) {
                try {
                    UUID notificationId = UUID.fromString(res.getString("NOTIFICATION_ID"));
                    NotificationType type = NotificationType.valueOf(res.getString("NOTIFICATION_TYPE"));
                    ContextID contextId = new ContextID(res.getString("CONTEXT_ID"));
                    Context context = GlobalContext.getInstance().getContext(contextId);
                    LocalDateTime timestamp = res.getTimestamp("TIMESTAMP").toLocalDateTime();
                    boolean read = res.getBoolean("IS_READ");
                    boolean accepted = res.getBoolean("IS_ACCEPTED");
                    boolean declined = res.getBoolean("IS_DECLINED");

                    Notification notification;
                    UUID requesterId;

                    switch (type) {
                        case AREA_MANAGING_REQUEST:
                            requesterId = UUID.fromString(res.getString("ARGUMENT_1"));
                            if (!users.containsKey(requesterId)) {
                                throw new UserNotFoundException("Requesting user not found", requesterId);
                            }

                            ContextID requestingId = new ContextID(res.getString("ARGUMENT_2"));
                            Context area = context.getContext(requestingId);
                            if (!(area instanceof Area)) {
                                throw new ContextNotFoundException("Requesting area not found", requestingId);
                            }

                            LocalDateTime from = LocalDateTime.parse(res.getString("ARGUMENT_3"));
                            LocalDateTime to = LocalDateTime.parse(res.getString("ARGUMENT_4"));

                            notification = new AreaManagingRequest(notificationId, timestamp, context, user,
                                    users.get(requesterId), (Area) area, from, to, read, accepted, declined);
                            break;

                        case FRIEND_REQUEST:
                            requesterId = UUID.fromString(res.getString("ARGUMENT_1"));
                            if (!users.containsKey(requesterId)) {
                                throw new UserNotFoundException("Requesting user not found", requesterId);
                            }

                            notification = new FriendRequest(notificationId, timestamp, user, users.get(requesterId),
                                    res.getString("ARGUMENT_2"), read, accepted, declined);
                            break;

                        case INFORMATION:
                            String key = res.getString("ARGUMENT_1");
                            List<Object> arguments = new ArrayList<>();

                            for (int index = 2; index <= 4; index++) {
                                Object argument = res.getString(String.format("ARGUMENT_%d", index));

                                if (argument != null) {
                                    arguments.add(argument);
                                }
                            }

                            notification = new Notification(notificationId, type, context, user, timestamp,
                                    new MessageBundle(key, arguments.toArray()), read, accepted, declined);
                            break;

                        default:
                            throw new IllegalStateException("Notification type should not be saved");
                    }

                    notifications.put(notification.getNotificationId(), notification);
                } catch (ContextNotFoundException | UserNotFoundException | RuntimeException ex) {
                    LOGGER.log(Level.WARNING, "Found invalid row in table notification", ex);
                    res.deleteRow();
                }
            }

            res.close();
            ps.close();
            con.close();
        } catch (SQLException ex) {
            LOGGER.log(Level.WARNING, "Failed to load notifications for " + user.getUsername(), ex);
            notifications.clear();
        }

        return notifications;
    }

    private static final String SQL_INSERT_NOTIFICATION = "INSERT INTO NOTIFICATION(NOTIFICATION_ID,"
            + "NOTIFICATION_TYPE, CONTEXT_ID, USER_ID, TIMESTAMP, IS_READ, IS_ACCEPTED, IS_DECLINED,"
            + "ARGUMENT_1, ARGUMENT_2, ARGUMENT_3, ARGUMENT_4) values(?,?,?,?,?,?,?,?,?,?,?,?)";

    @Override
    public void addNotification(@NotNull final User user, @NotNull final Notification notification) {
        try {
            final Connection con = this.getConnection();
            final PreparedStatement ps = con.prepareStatement(SQL_INSERT_NOTIFICATION);

            ps.setString(1, notification.getNotificationId().toString());
            ps.setString(2, notification.getNotificationType().name());
            ps.setString(3, notification.getContext().getContextId().getId());
            ps.setString(4, user.getUserId().toString());
            ps.setTimestamp(5, Timestamp.valueOf(notification.getTimestamp()));
            ps.setBoolean(6, notification.isRead());
            ps.setBoolean(7, notification.isAccepted());
            ps.setBoolean(8, notification.isDeclined());

            switch (notification.getNotificationType()) {
                case AREA_MANAGING_REQUEST:
                    final AreaManagingRequest areaRequest = (AreaManagingRequest) notification;
                    ps.setString(9, areaRequest.getRequestingUser().getUserId().toString());
                    ps.setString(10, areaRequest.getRequestedArea().getContextId().getId());
                    ps.setString(11, areaRequest.getFrom().toString());
                    ps.setString(12, areaRequest.getTo().toString());
                    break;

                case FRIEND_REQUEST:
                    final FriendRequest friendRequest = (FriendRequest) notification;
                    ps.setString(9, friendRequest.getRequestingUser().getUserId().toString());
                    ps.setString(10, friendRequest.getUserMessage());
                    ps.setString(11, null);
                    ps.setString(12, null);
                    break;

                case INFORMATION:
                    final MessageBundle bundle = notification.getMessageBundle();
                    ps.setString(9, bundle.getMessageKey());

                    for (int index = 0; index < 3; index++) {
                        if (bundle.getArguments().length > index) {
                            ps.setString(10 + index, bundle.getArguments()[index].toString());
                        } else {
                            ps.setString(10 + index, null);
                        }
                    }
                    break;

                default:
                    ps.close();
                    con.close();
                    return;
            }

            ps.executeUpdate();
            ps.close();
            con.close();
        } catch (SQLException ex) {
            LOGGER.log(Level.WARNING, "Failed to insert notification for " + user.getUsername() + " into the database", ex);
        }
    }

    private static final String SQL_UPDATE_NOTIFICATION = "UPDATE NOTIFICATION SET IS_READ = ?, IS_ACCEPTED = ?, IS_DECLINED = ? WHERE USER_ID = ? AND NOTIFICATION_ID = ?";

    @Override
    public void updateNotification(@NotNull final User user, @NotNull final Notification notification) {
        try {
            final Connection con = this.getConnection();
            final PreparedStatement ps = con.prepareStatement(SQL_UPDATE_NOTIFICATION);

            ps.setBoolean(1, notification.isRead());
            ps.setBoolean(2, notification.isAccepted());
            ps.setBoolean(3, notification.isDeclined());
            ps.setString(4, user.getUserId().toString());
            ps.setString(5, notification.getNotificationId().toString());
            ps.executeUpdate();
            ps.close();
            con.close();
        } catch (SQLException ex) {
            LOGGER.log(Level.WARNING, "Failed to update notification of " + user.getUsername(), ex);
        }
    }

    private static final String SQL_DELETE_NOTIFICATION = "DELETE FROM NOTIFICATION WHERE USER_ID = ? AND NOTIFICATION_ID = ?";

    @Override
    public void removeNotification(@NotNull final User user, @NotNull final Notification notification) {
        try {
            final Connection con = this.getConnection();
            final PreparedStatement ps = con.prepareStatement(SQL_DELETE_NOTIFICATION);

            ps.setString(1, user.getUserId().toString());
            ps.setString(2, notification.getNotificationId().toString());
            ps.executeUpdate();
            ps.close();
            con.close();
        } catch (SQLException ex) {
            LOGGER.log(Level.WARNING, "Failed to delete notification of " + user.getUsername() + " from the database", ex);
        }
    }

    private static final String SQL_QUERY_BAN = "SELECT USER_ID FROM BAN WHERE WORLD_ID = ?";

    @Override
    public void getBannedUsers(@NotNull final World world) {
        try {
            Connection con = this.getConnection();
            PreparedStatement ps = con.prepareStatement(SQL_QUERY_BAN, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
            ps.setString(1, world.getContextId().getId());
            ResultSet res = ps.executeQuery();
            Map<UUID, User> bans = new HashMap<>();

            while (res.next()) {
                try {
                    UUID bannedId = UUID.fromString(res.getString("USER_ID"));
                    User banned = UserAccountManager.getInstance().getUser(bannedId);

                    bans.put(banned.getUserId(), banned);
                } catch (UserNotFoundException | IllegalArgumentException ex) {
                    LOGGER.log(Level.WARNING, "Found invalid row in table ban", ex);
                    res.deleteRow();
                }
            }

            world.addBannedUsers(bans);
            res.close();
            ps.close();
            con.close();
        } catch (SQLException ex) {
            LOGGER.log(Level.WARNING, "Failed to query bans of world " + world.getContextName() + " from the database", ex);
        }
    }
    
    private static final String SQL_INSERT_BAN = "INSERT INTO BAN(USER_ID, WORLD_ID) values(?,?)";

    @Override
    public void addBannedUser(@NotNull final User user, @NotNull final Context world) {
        try {
            Connection con = this.getConnection();
            PreparedStatement ps = con.prepareStatement(SQL_INSERT_BAN);
            
            ps.setString(1, user.getUserId().toString());
            ps.setString(2, world.getContextId().getId());
            ps.executeUpdate();
            ps.close();
            con.close();
        } catch (SQLException ex) {
            LOGGER.log(Level.WARNING, "Failed to insert banned user into the database", ex);
        }
    }

    private static final String SQL_DELETE_BAN = "DELETE FROM BAN WHERE USER_ID = ? AND WORLD_ID = ?";

    @Override
    public void removeBannedUser(@NotNull final User user, @NotNull final Context world) {
        try {
            Connection con = this.getConnection();
            PreparedStatement ps = con.prepareStatement(SQL_DELETE_BAN);

            ps.setString(1, user.getUserId().toString());
            ps.setString(2, world.getContextId().getId());
            ps.executeUpdate();
            ps.close();
            con.close();
        } catch (SQLException ex) {
            LOGGER.log(Level.WARNING, "Failed to delete banned user from the database", ex);
        }
    }

    private static final String SQL_QUERY_RESERVATION = "SELECT USER_ID, CONTEXT_ID, START_TIME, END_TIME FROM USER_RESERVATION WHERE WORLD_ID = ?";

    @Override
    public void getAreaReservations(@NotNull final World world) {
        try {
            Connection con = this.getConnection();
            PreparedStatement ps = con.prepareStatement(SQL_QUERY_RESERVATION, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
            ps.setString(1, world.getContextId().getId());
            ResultSet res = ps.executeQuery();

            while (res.next()) {
                try {
                    Context context = world.getContext(new ContextID(res.getString("CONTEXT_ID")));
                    if (!(context instanceof Area)) {
                        throw new ContextNotFoundException("", context.getContextId());
                    }

                    UUID userId = UUID.fromString(res.getString("USER_ID"));
                    User user = UserAccountManager.getInstance().getUser(userId);
                    LocalDateTime from = res.getTimestamp("START_TIME").toLocalDateTime();
                    LocalDateTime to = res.getTimestamp("END_TIME").toLocalDateTime();

                    ((Area) context).addReservation(user, from, to);
                } catch (ContextNotFoundException | UserNotFoundException | IllegalArgumentException ex) {
                    LOGGER.log(Level.WARNING, "Found invalid row in table user_reservation", ex);
                    res.deleteRow();
                }
            }

            res.close();
            ps.close();
            con.close();
        } catch (SQLException ex) {
            LOGGER.log(Level.WARNING, "Failed to query area reservations of world " + world.getContextName() + " from the database", ex);
        }
    }

    private static final String SQL_INSERT_RESERVATION = "INSERT INTO USER_RESERVATION(WORLD_ID, USER_ID, CONTEXT_ID, START_TIME, END_TIME) values (?,?,?,?,?)";

    @Override
    public void addAreaReservation(@NotNull final AreaReservation contextReservation) {
        try {
            Connection con = this.getConnection();
            PreparedStatement ps = con.prepareStatement(SQL_INSERT_RESERVATION);

            ps.setString(1, contextReservation.getArea().getWorld().getContextId().getId());
            ps.setString(2, contextReservation.getReserver().getUserId().toString());
            ps.setString(3, contextReservation.getArea().getContextId().getId());
            ps.setTimestamp(4, Timestamp.valueOf(contextReservation.getFrom()));
            ps.setTimestamp(5, Timestamp.valueOf(contextReservation.getTo()));
            ps.executeUpdate();
            ps.close();
            con.close();
        } catch (SQLException ex) {
            LOGGER.log(Level.WARNING, "Failed to insert area reservation into the database", ex);
        }
    }

    private static final String SQL_DELETE_RESERVATION = "DELETE FROM USER_RESERVATION WHERE "
            + "WORLD_ID = ? AND USER_ID = ? AND CONTEXT_ID = ? AND START_TIME = ? AND END_TIME = ?";

    @Override
    public void removeAreaReservation(@NotNull final AreaReservation contextReservation) {
        try {
            Connection con = this.getConnection();
            PreparedStatement ps = con.prepareStatement(SQL_DELETE_RESERVATION);

            ps.setString(1, contextReservation.getArea().getWorld().getContextId().getId());
            ps.setString(2, contextReservation.getReserver().getUserId().toString());
            ps.setString(3, contextReservation.getArea().getContextId().getId());
            ps.setTimestamp(4, Timestamp.valueOf(contextReservation.getFrom()));
            ps.setTimestamp(5, Timestamp.valueOf(contextReservation.getTo()));
            ps.executeUpdate();
            ps.close();
            con.close();
        } catch (SQLException ex) {
            LOGGER.log(Level.WARNING, "Failed to delete area reservation from the database", ex);
        }
    }

    public @NotNull Connection getConnection() throws SQLException {
        try {
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver");

            return DriverManager.getConnection(dbURL);
        } catch (ClassNotFoundException ex) {
            throw new SQLException("Failed to find apache derby driver", ex);
        }
    }

    public void initialize() {
        try {
            Connection con = this.getConnection();
            Statement statement = con.createStatement();

            //Suche alle Namen von schon existierten tables
            DatabaseMetaData meta = con.getMetaData();
            ResultSet res = meta.getTables(null, null, null, new String[]{"TABLE"});
            HashSet<String> set = new HashSet<>();
            while (res.next()) {
                set.add(res.getString("TABLE_NAME"));
            }

            // Prüfe ob alle tables existieren, falls nicht dann create
            if (!set.contains("USER_ACCOUNT")) {
                String sql = "CREATE TABLE USER_ACCOUNT("
                        + "USER_ID CHAR(36) not null unique,"
                        + "USER_NAME VARCHAR(16) not null unique,"
                        + "PSW_SALT CHAR(32),"
                        + "PSW_HASH CHAR(32) not null,"
                        + "LAST_ONLINE_TIME TIMESTAMP not null,"
                        + "AVATAR_NAME VARCHAR(16) not null)";
                statement.execute(sql);
            }
            if (!set.contains("WORLDS")) {
                String sql = "CREATE TABLE WORLDS("
                        + "WORLD_ID VARCHAR(32) not null unique,"
                        + "WORLD_NAME VARCHAR(16) not null unique,"
                        + "MAP_NAME VARCHAR(16) not null)";
                statement.execute(sql);
            }
            if (!set.contains("BAN")) {
                String sql = "CREATE TABLE BAN("
                        + "USER_ID CHAR(36) not null,"
                        + "WORLD_ID VARCHAR(32) not null)";
                statement.execute(sql);
            }
            if (!set.contains("IGNORE")) {
                String sql = "CREATE TABLE IGNORE("
                        + "USER_ID CHAR(36) not null, "
                        + "IGNORED_ID CHAR(36) not null)";
                statement.execute(sql);
            }
            if (!set.contains("FRIENDSHIP")) {
                String sql = "CREATE TABLE FRIENDSHIP("
                        + "USER_ID1 CHAR(36) not null,"
                        + "USER_ID2 CHAR(36) not null)";
                statement.execute(sql);
            }
            if (!set.contains("USER_RESERVATION")) {
                String sql = "CREATE TABLE USER_RESERVATION("
                        + "WORLD_ID VARCHAR(32) not null,"
                        + "USER_ID CHAR(36) not null,"
                        + "CONTEXT_ID VARCHAR(48) not null,"
                        + "START_TIME TIMESTAMP not null,"
                        + "END_TIME TIMESTAMP not null)";
                statement.execute(sql);
            }
            if (!set.contains("ROLE_WITH_CONTEXT")) {
                String sql = "CREATE TABLE ROLE_WITH_CONTEXT("
                        + "USER_ID CHAR(36) not null,"
                        + "USER_ROLE VARCHAR(16) not null,"
                        + "CONTEXT_ID VARCHAR(48) not null)";
                statement.execute(sql);
            }
            if (!set.contains("NOTIFICATION")) {
                String sql = "CREATE TABLE NOTIFICATION("
                        + "NOTIFICATION_ID CHAR(36) not null unique,"
                        + "NOTIFICATION_TYPE VARCHAR(36) not null,"
                        + "CONTEXT_ID VARCHAR(128) not null,"
                        + "USER_ID CHAR(36) not null,"
                        + "TIMESTAMP TIMESTAMP not null,"
                        + "IS_READ BOOLEAN, IS_ACCEPTED BOOLEAN, IS_DECLINED BOOLEAN,"
                        + "ARGUMENT_1 VARCHAR(128), ARGUMENT_2 VARCHAR(128), ARGUMENT_3 VARCHAR(128), ARGUMENT_4 VARCHAR(128))";
                statement.execute(sql);
            }
            statement.close();
            con.close();
        } catch (SQLException ex){
            LOGGER.log(Level.WARNING, "Failed to initialize database", ex);
        }
    }

    private static @NotNull Database getInstance() {
        if (database == null) {
            database = new Database();
        }
        return database;
    }

    public static @NotNull IUserAccountManagerDatabase getUserAccountManagerDatabase() {
        return getInstance();
    }

    public static @NotNull IUserDatabase getUserDatabase() {
        return getInstance();
    }

    public static @NotNull IContextDatabase getContextDatabase() {
        return getInstance();
    }
}