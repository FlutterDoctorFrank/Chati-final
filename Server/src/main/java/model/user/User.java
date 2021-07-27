package model.user;

import controller.network.ClientSender;
import model.MessageBundle;
import model.communication.CommunicationHandler;
import model.communication.message.TextMessage;
import model.context.Context;
import model.context.ContextID;
import model.context.IContext;
import model.context.global.GlobalContext;
import model.context.spatial.Location;
import model.context.spatial.SpatialContext;
import model.database.Database;
import model.database.IUserDatabase;
import model.exception.*;
import model.notification.INotification;
import model.notification.Notification;
import model.role.ContextRole;
import model.role.IContextRole;
import model.role.Permission;
import model.role.Role;
import model.user.account.UserAccountManager;

import java.util.*;
import java.util.stream.Collectors;

public class User implements IUser {

    private final UUID userID;
    private String username;
    private Status status;
    private Avatar avatar;
    private SpatialContext currentWorld;
    private Location currentLocation;
    private SpatialContext currentInteractable;
    private final CommunicationHandler communicationHandler;
    private final Map<UUID, User> friends;
    private final Map<UUID, User> ignoredUsers;
    private final Map<Context, ContextRole> contextRoles;
    private final Map<UUID, Notification> notifications;
    private final IUserDatabase database;
    private ClientSender clientSender;

    public User(UUID userID, String username, Avatar avatar, Map<UUID, User> friends, Map<UUID, User> ignoredUsers,
                Map<Context, ContextRole> contextRoles, Map<UUID, Notification> notifications) {
        this.userID = userID;
        this.username = username;
        this.status = Status.OFFLINE;
        this.avatar = avatar;
        this.communicationHandler = new CommunicationHandler(this);
        this.friends = friends;
        this.ignoredUsers = ignoredUsers;
        this.contextRoles = contextRoles;
        this.notifications = notifications;
        this.database = Database.getUserDatabase();
    }

    @Override
    public void joinWorld(ContextID worldID) throws IllegalWorldActionException, IllegalActionException, ContextNotFoundException {
        if (currentWorld != null) {
            throw new IllegalActionException("User is already in a world.");
        }
        SpatialContext world = GlobalContext.getInstance().getWorld(worldID);
        if (world.isBanned(this)) {
            throw new IllegalWorldActionException("", "Du bist in dieser Welt gesperrt.");
        }
        currentWorld = world;
        currentWorld.addUser(this);

        // Send packets related to world join
        clientSender.send(ClientSender.SendAction.WORLD_ACTION, currentWorld);
        clientSender.send(ClientSender.SendAction.CONTEXT_JOIN, currentWorld);
        clientSender.send(ClientSender.SendAction.CONTEXT_ROLE, contextRoles);
        clientSender.send(ClientSender.SendAction.NOTIFICATION, getWorldNotifications());
        currentWorld.getUsers().forEach((userID, user) -> {
            user.getClientSender().send(ClientSender.SendAction.USER_INFO, this);
        });

        // Spawn User & send packets related to location
        teleport(currentWorld.getSpawnLocation());
    }

    @Override
    public void leaveWorld() throws IllegalActionException {
        if (currentWorld == null) {
            throw new IllegalActionException("User is not in a world.");
        }
        currentLocation = null;
        currentWorld.removeUser(this);

        // Send packets to other users to remove avatar & update user info
        currentWorld.getUsers().forEach((userID, user) -> {
            user.getClientSender().send(ClientSender.SendAction.AVATAR_REMOVE, this);
            user.getClientSender().send(ClientSender.SendAction.USER_INFO, this);
        });

        currentWorld = null;

        // Send packet for leaving world
        clientSender.send(ClientSender.SendAction.WORLD_ACTION, null); // Hier is noch unklar wie das gehen soll?
    }

    @Override
    public void chat(String message) {
        communicationHandler.handleTextMessage(message);
    }

    @Override
    public void talk(byte[] voicedata) {
        communicationHandler.handleVoiceMessage(voicedata);
    }

    @Override
    public void move(int posX, int posY) throws IllegalPositionException, IllegalActionException {
        SpatialContext currentRoom = getRoom();
        SpatialContext currentArea = getArea();
        if (!currentRoom.isLegal(posX, posY)) {
            throw new IllegalPositionException("Position is illegal.", this, posX, posY);
        }
        if (!currentArea.isMoveable()) {
            throw new IllegalActionException("Movement is not allowed.");
        }
        currentLocation.setPosition(posX, posY);

        currentRoom.getUsers().forEach((userID, user) -> {
            user.getClientSender().send(ClientSender.SendAction.AVATAR_MOVE, this);
        });

        SpatialContext newArea = getArea();
        if (!currentArea.equals(newArea)) {
            Context lastCommonAncestor = currentArea.lastCommonAncestor(newArea);
            lastCommonAncestor.removeUser(this);
            newArea.addUser(this);
            // Send music info of new context
            clientSender.send(ClientSender.SendAction.CONTEXT_MUSIC, newArea);
        }
    }

    @Override
    public void executeAdministrativeAction(UUID targetID, AdministrativeAction administrativeAction, String[] args) throws UserNotFoundException, IllegalActionException, NoPermissionException {
        User target = UserAccountManager.getInstance().getUser(targetID);
        administrativeAction.execute(this, target, args);
    }

    @Override
    public void interact(ContextID spatialID) throws IllegalInteractionException {
        SpatialContext currentArea = currentLocation.getArea();
        SpatialContext interactable = currentArea.getChildren().get(spatialID);
        if (currentInteractable != null) {
            throw new IllegalInteractionException("User is already interacting with a context.", this);
        }
        if (interactable == null || !interactable.canInteract(this)) {
            throw new IllegalInteractionException("There is no interactable context with this ID near the user.", this);
        }
        interactable.interact(this);
    }

    @Override
    public void executeOption(ContextID spatialID, int menuOption, String[] args) throws IllegalInteractionException, IllegalMenuActionException {
        SpatialContext currentArea = currentLocation.getArea();
        SpatialContext interactable = currentArea.getChildren().get(spatialID);
        if (interactable == null || !interactable.canInteract(this)) {
            throw new IllegalInteractionException("There is no interactable context with this ID near the user.", this);
        }
        if (!currentInteractable.equals(interactable)) {
            throw new IllegalInteractionException("The user has not opened the menu of this context.", this, interactable);
        }
        currentInteractable.executeMenuOption(this, menuOption, args);
    }

    @Override
    public void deleteNotification(UUID notificationID) throws NotificationNotFoundException {
        if (notifications.remove(notificationID) == null) {
            throw new NotificationNotFoundException("This user has no notification with this ID.", this, notificationID);
        }
    }

    @Override
    public void manageNotification(UUID notificationID, boolean accept) throws NotificationNotFoundException, IllegalNotificationActionException {
        Notification notification = notifications.get(notificationID);
        if (notification == null) {
            throw new NotificationNotFoundException("This user has no notification with this ID.", this, notificationID);
        }
        if (accept) {
            notification.accept();
        } else {
            notification.decline();
        }
    }

    @Override
    public void setStatus(Status status) {
        this.status = status;
        updateUserInfo();
    }

    @Override
    public void setAvatar(Avatar avatar) {
        this.avatar = avatar;
        updateUserInfo();
    }

    @Override
    public UUID getUserID() {
        return userID;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public Avatar getAvatar() {
        return avatar;
    }

    @Override
    public SpatialContext getWorld() {
        return currentWorld;
    }

    @Override
    public Location getLocation() {
        return currentLocation;
    }

    @Override
    public Map<UUID, IUser> getFriends() {
        return Collections.unmodifiableMap(friends);
    }

    @Override
    public Map<UUID, IUser> getIgnoredUsers() {
        return Collections.unmodifiableMap(ignoredUsers);
    }

    @Override
    public Map<IContext, IContextRole> getGlobalRoles() {
        return contextRoles.entrySet().stream()
                .filter(entry -> entry.getKey().equals(GlobalContext.getInstance()))
                .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    /*
    @Override
    public Map<IContext, IContextRole> getWorldRoles() {
        return contextRoles.entrySet().stream()
                .filter(entry -> entry.getKey().equals(currentWorld))
                .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue));
    }
     */

    @Override
    public Map<UUID, INotification> getGlobalNotifications() {
        return notifications.entrySet().stream()
                .filter(entry -> entry.getValue().getContext().equals(GlobalContext.getInstance()))
                .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public Map<UUID, INotification> getWorldNotifications() {
        return notifications.entrySet().stream()
                .filter(entry -> entry.getValue().getContext().equals(currentWorld))
                .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public void addFriend(User user) {
        friends.put(user.getUserID(), user);
        database.addFriendship(this, user);
        updateUserInfo();
    }

    public void removeFriend(User user) {
        friends.remove(user.getUserID());
        database.removeFriendship(this, user);
        updateUserInfo();
    }

    public void ignoreUser(User user) {
        ignoredUsers.put(user.getUserID(), user);
        database.addIgnoredUser(this, user);
        updateUserInfo();
    }

    public void unignoreUser(User user) {
        ignoredUsers.remove(user.getUserID());
        database.removeIgnoredUser(this, user);
        updateUserInfo();
    }

    public void addRole(Context context, Role role) {
        ContextRole contextRole = contextRoles.get(context);
        if (contextRole == null) {
            contextRoles.put(context, new ContextRole(context, role));
        } else {
            contextRole.addRole(role);
        }
        database.addRole(this, context, role);
        clientSender.send(ClientSender.SendAction.CONTEXT_ROLE, contextRoles);
    }

    public void removeRole(Context context, Role role) {
        ContextRole contextRole = contextRoles.get(context);
        contextRole.removeRole(role);
        if (contextRole.getRoles().isEmpty()) {
            contextRoles.remove(context);
        }
        database.removeRole(this, context, role);
        clientSender.send(ClientSender.SendAction.CONTEXT_ROLE, contextRoles);
    }

    public void addNotification(Notification notification) {
        notifications.put(notification.getNotificationID(), notification);
        database.addNotification(this, notification);
        clientSender.send(ClientSender.SendAction.NOTIFICATION, Collections.singletonMap(notification.getNotificationID(), notification));
    }

    public void removeNotification(Notification notification) {
        notifications.remove(notification.getNotificationID());
        database.removeNotification(this, notification);
    }

    public boolean hasRole(Context context, Role role) {
        ContextRole contextRole = contextRoles.get(context);
        return contextRole != null && contextRole.hasRole(role);
    }

    public boolean hasPermission(Context context, Permission permission) {
        ContextRole contextRole = contextRoles.get(context);
        if (contextRole != null && contextRole.hasPermission(permission)) {
            return true;
        } else {
            return hasPermission(context.getParent(), permission);
        }
    }

    /*
    public Context getHighestPermittedContext(Context context, Permission permission) {
        Context highestPermittedContext = null;
        Context currentContext = context;
        ContextRole contextRole;
        do {
            contextRole = contextRoles.get(currentContext);
            if (contextRole != null && contextRole.hasPermission(permission)) {
                highestPermittedContext = currentContext;
            }
            currentContext = currentContext.getParent();
        } while (currentContext != null);
        return highestPermittedContext;
    }
    */

    public boolean isFriend(User user) {
        return friends.containsKey(user.getUserID());
    }

    public boolean isIgnoring(User user) {
        return ignoredUsers.containsKey(user.getUserID());
    }

    public void teleport(Location newLocation) {
        SpatialContext currentRoom = getRoom();
        SpatialContext currentArea = getArea();
        currentLocation = newLocation;
        SpatialContext newRoom = getRoom();
        SpatialContext newArea = getArea();
        // check if user entered a new area
        if (!currentArea.equals(newArea)) {
            // check if user entered a new room
            if (!currentRoom.equals(newRoom)) {
                // Remove avatar from all other users in the old room
                currentRoom.getUsers().forEach((userID, user) -> {
                    user.getClientSender().send(ClientSender.SendAction.AVATAR_REMOVE, this);
                });
                // Send room join information to user
                clientSender.send(ClientSender.SendAction.CONTEXT_JOIN, this);
                // Send position of user to other users in new room & position of others in new room to joining user
                newRoom.getUsers().forEach((userID, user) -> {
                    clientSender.send(ClientSender.SendAction.AVATAR_MOVE, user);
                    user.getClientSender().send(ClientSender.SendAction.AVATAR_MOVE, this);
                });
                // check if user entered a private room
                if (newRoom.isPrivate()) {
                    MessageBundle messageBundle = new MessageBundle("PrivateRoomEnterMessageKey", new Object[]{username});
                    TextMessage info = new TextMessage(messageBundle);
                    // send message to users in private room
                    newRoom.getUsers().forEach((userID, user) -> {
                        user.getClientSender().send(ClientSender.SendAction.MESSAGE, info);
                    });
                }
            }
            Context lastCommonAncestor = currentArea.lastCommonAncestor(newArea);
            lastCommonAncestor.removeUser(this);
            newArea.addUser(this);
            // Send music info of new context
            clientSender.send(ClientSender.SendAction.CONTEXT_MUSIC, newArea);
        }
    }

    public boolean isInteractingWith(SpatialContext interactable) {
        return currentInteractable.equals(interactable);
    }

    public void setCurrentInteractable(SpatialContext interactable) {
        this.currentInteractable = interactable;
    }

    public boolean isOnline() {
        return !status.equals(Status.OFFLINE);
    }

    public void setClientSender(ClientSender clientSender) {
        this.clientSender = clientSender;
    }

    public ClientSender getClientSender() {
        return clientSender;
    }

    public SpatialContext getArea() {
        return currentLocation != null ? currentLocation.getArea() : null;
    }

    public SpatialContext getRoom() {
        return currentLocation != null ? currentLocation.getRoom() : null;
    }

    public void updateUserInfo() {
        Map<UUID, User> receivers = getWorld().getUsers();
        receivers.putAll(friends);
        receivers.forEach((userID, user) -> {
            user.getClientSender().send(ClientSender.SendAction.USER_INFO, this);
        });
    }
}