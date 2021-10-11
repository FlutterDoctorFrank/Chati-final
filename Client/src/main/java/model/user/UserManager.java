package model.user;

import model.context.Context;
import model.context.ContextID;
import model.context.spatial.ISpatialContextView;
import model.context.spatial.SpatialContext;
import model.exception.ContextNotFoundException;
import model.exception.UserNotFoundException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import view.IModelObserver;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Eine Klasse, über welche der intern angemeldete Benutzer sowie aller bekannten externen Benutzer verwaltet werden
 * können.
 */
public class UserManager implements IUserManagerController, IUserManagerView {

    /** Singleton-Instanz der Klasse. */
    private static UserManager userManager;

    /** Der auf diesem Client angemeldete Benutzer. */
    private InternUser internUser;

    /** Bekannte externe Benutzer. */
    private final Map<UUID, User> externUsers;

    /** Wird verwendet, um die View über Änderungen im Modell zu informieren. */
    private IModelObserver modelObserver;

    /**
     * Erzeugt eine neue Instanz des UserManager.
     */
    private UserManager() {
        this.internUser = null;
        this.externUsers = new HashMap<>();
        this.modelObserver = null;
    }

    @Override
    public void login(@NotNull final UUID userId, @NotNull final String username,
                      @NotNull final Status status, @NotNull final Avatar avatar) {
        if (internUser != null) {
            throw new IllegalStateException("There is already a user logged in on this client.");
        }
        this.internUser = new InternUser(userId, username, status, avatar);
        UserManager.getInstance().getModelObserver().setUserInfoChanged();
    }

    @Override
    public void logout() {
        throwIfNotLoggedIn();
        UserManager.getInstance().getModelObserver().setUserInfoChanged();
        if (internUser.getCurrentWorld() != null) {
            UserManager.getInstance().getModelObserver().setWorldChanged();
        }
        internUser = null;
        externUsers.clear();
    }

    @Override
    public void updateWorlds(@NotNull final Map<ContextID, String> worlds) {
        Context.getGlobal().getChildren().values().removeIf(world -> !worlds.containsKey(world.getContextId()));
        worlds.forEach((worldId, worldName) -> {
            if (!Context.getGlobal().hasChild(worldId)) {
                Context.getGlobal().addChild(new SpatialContext(worldName, Context.getGlobal()));
            }
        });
        modelObserver.setWorldListChanged();
    }

    @Override
    public void updatePublicRoom(@NotNull final ContextID worldId, @NotNull final ContextID roomId,
                                 @NotNull final String roomName) throws ContextNotFoundException {
        throwIfNotInWorld();
        SpatialContext world = Context.getGlobal().getChildren().get(worldId);
        if (world == null) {
            throw new ContextNotFoundException("Tried to update public room in an unknown world.", worldId);
        }
        world.getChildren().values().forEach(room -> {
            if (!room.isPrivate() && !room.getContextId().equals(roomId)) {
                throw new IllegalStateException("World has already a public room");
            }
        });

        if (!world.hasChild(roomId)) {
            world.addChild(new SpatialContext(roomName, world));
        }
    }

    @Override
    public void updatePrivateRooms(@NotNull final ContextID worldId, @NotNull final Map<ContextID, String> privateRooms) throws ContextNotFoundException {
        throwIfNotInWorld();
        SpatialContext world = Context.getGlobal().getChildren().get(worldId);
        if (world == null) {
            throw new ContextNotFoundException("Tried to update private rooms in an unknown world.", worldId);
        }
        world.getChildren().values().removeIf(room -> room.isPrivate() && !privateRooms.containsKey(room.getContextId()));
        privateRooms.forEach((roomId, roomName) -> {
            if (!world.hasChild(roomId)) {
                world.addChild(new SpatialContext(roomName, world, true));
            }
        });
        modelObserver.setRoomListChanged();
    }

    @Override
    public void addExternUser(@NotNull final UUID userId, @NotNull final String username,
                              @NotNull final Status status, @Nullable final Avatar avatar) {
        this.externUsers.put(userId, new User(userId, username, status, avatar));
        UserManager.getInstance().getModelObserver().setUserInfoChanged();
    }

    @Override
    public void removeExternUser(@NotNull final UUID userId) throws UserNotFoundException {
        throwIfNotExists(userId);
        externUsers.remove(userId);
        UserManager.getInstance().getModelObserver().setUserInfoChanged();
    }

    @Override
    public @NotNull InternUser getInternUserController() {
        throwIfNotLoggedIn();
        return internUser;
    }

    @Override
    public @NotNull User getExternUserController(@NotNull final UUID userId) throws UserNotFoundException {
        throwIfNotExists(userId);
        return externUsers.get(userId);
    }

    @Override
    public @NotNull Map<UUID, IUserController> getExternUsers() {
        return externUsers.values().stream().collect(Collectors.toUnmodifiableMap(User::getUserId, Function.identity()));
    }

    @Override
    public @Nullable InternUser getInternUserView() {
        return internUser;
    }

    @Override
    public @NotNull User getExternUserView(@NotNull final UUID userId) throws UserNotFoundException {
        throwIfNotExists(userId);
        return externUsers.get(userId);
    }

    @Override
    public @NotNull IUserView getExternUserView(@NotNull final String username) throws UserNotFoundException {
        try {
            return externUsers.values().stream().filter(user -> user.getUsername().equals(username)).findFirst().orElseThrow();
        } catch (NoSuchElementException e) {
            throw new UserNotFoundException("No User with this name was found", username, e);
        }
    }

    @Override
    public @NotNull Map<UUID, IUserView> getActiveUsers() {
        throwIfNotInWorld();
        return externUsers.values().stream().filter(User::isInCurrentWorld)
                .collect(Collectors.toUnmodifiableMap(User::getUserId, Function.identity()));
    }

    @Override
    public @NotNull Map<UUID, IUserView> getFriends() {
        throwIfNotLoggedIn();
        return externUsers.values().stream().filter(User::isFriend)
                .collect(Collectors.toUnmodifiableMap(User::getUserId, Function.identity()));
    }

    @Override
    public @NotNull Map<UUID, IUserView> getBannedUsers() {
        throwIfNotInWorld();
        return externUsers.values().stream().filter(User::isBanned)
                .collect(Collectors.toUnmodifiableMap(User::getUserId, Function.identity()));
    }

    @Override
    public @NotNull Map<UUID, IUserView> getUsersInRoom() {
        throwIfNotInWorld();
        return externUsers.values().stream().filter(User::isInCurrentRoom)
                .collect(Collectors.toUnmodifiableMap(User::getUserId, Function.identity()));
    }

    @Override
    public @NotNull Map<UUID, IUserView> getCommunicableUsers() {
        throwIfNotInWorld();
        return externUsers.values().stream().filter(User::canCommunicateWith)
                .collect(Collectors.toUnmodifiableMap(User::getUserId, Function.identity()));
    }

    @Override
    public boolean isLoggedIn() {
        return internUser != null;
    }

    @Override
    public @NotNull Map<ContextID, ISpatialContextView> getWorlds() {
        return Collections.unmodifiableMap(Context.getGlobal().getChildren());
    }

    @Override
    public @NotNull Map<ContextID, ISpatialContextView> getPrivateRooms() {
        throwIfNotLoggedIn();
        throwIfNotInWorld();
        SpatialContext world = internUser.getCurrentWorld();
        if (world != null) {
            return internUser.getCurrentWorld().getChildren().values().stream().filter(Context::isPrivate)
                    .collect(Collectors.toUnmodifiableMap(SpatialContext::getContextId, Function.identity()));
        } else {
            return Collections.emptyMap();
        }

    }

    /**
     * Gibt die Singleton-Instanz der Klasse zurück.
     * @return Instanz des UserManager.
     */
    public static @NotNull UserManager getInstance() {
        if (userManager == null) {
            userManager = new UserManager();
        }
        return userManager;
    }

    /**
     * Gibt den an diesem Client angemeldeten Benutzer zurück.
     * @return An diesem Client angemeldeten Benutzer.
     */
    public @NotNull InternUser getInternUser() {
        if (this.internUser == null) {
            throw new IllegalStateException("Intern User has not been set");
        }
        return internUser;
    }

    /**
     * Wird aufgerufen, wenn der intern angemeldete Benutzer die Welt verlässt. Veranlasst, dass die Informationen
     * zu allen Kontexten innerhalb einer Welt für alle Benutzer verworfen werden. Entfernt externe Benutzer, mit denen
     * der intern angemeldete Benutzer nicht befreundet ist oder Benutzer, die nicht gesperrt sind sofern der intern
     * angemeldete Benutzer die Berechtigung zum Sperren besitzt.
     */
    public void discardWorldInfo() {
        Iterator<User> iterator = externUsers.values().iterator();
        while (iterator.hasNext()) {
            User user = iterator.next();
            if (!user.isKnown()) {
                iterator.remove();
            } else {
                user.discardWorldInfo();
            }
        }
        internUser.discardWorldInfo();
    }

    /**
     * Wird aufgerufen, wenn der intern angemeldete Benutzer einen Raum verlässt. Veranlasst, dass die Informationen
     * zu allen Kontexten innerhalb des Raums für alle Benutzer verworfen werden.
     */
    public void discardRoomInfo() {
        externUsers.values().forEach(User::discardRoomInfo);
        internUser.discardRoomInfo();
    }

    /**
     * Wirft eine Exeption, wenn auf diesem Client kein Benutzer angemeldet ist.
     * @throws IllegalStateException wenn auf diesem Client kein Benutzer angemeldet ist.
     */
    private void throwIfNotLoggedIn() {
        if (internUser == null) {
            throw new IllegalStateException("There is no user logged in on this client.");
        }
    }

    /**
     * Wirft eine Exeption, wenn der intern angemeldete Benutzer in keiner Welt ist.
     * @throws IllegalStateException wenn der intern angemeldete Benutzer in keiner Welt ist.
     */
    private void throwIfNotInWorld() {
        if (internUser == null || !internUser.isInCurrentWorld()) {
            throw new IllegalStateException("User is not in world.");
        }
    }

    /**
     * Wirft eine Exeption, wenn dem Client kein externer Benutzer mit der ID bekannt ist.
     * @param userId ID des gesuchten Benutzers.
     * @throws UserNotFoundException wenn dem Client kein externer Benutzer mit der ID bekannt ist.
     */
    private void throwIfNotExists(@NotNull final UUID userId) throws UserNotFoundException {
        if (!externUsers.containsKey(userId)) {
            throw new UserNotFoundException("No user with this ID was found.", userId);
        }
    }

    /**
     * Setzt den Beobachter des Modells, über den die View über Änderungen benachrichtigt werden kann.
     * @param modelObserver Beobachter des Modells.
     */
    public void setModelObserver(@NotNull final IModelObserver modelObserver) {
        this.modelObserver = modelObserver;
    }

    /**
     * Gibt den Beobachter des Modells zurück.
     * @return Beobachter des Modells.
     */
    public @NotNull IModelObserver getModelObserver() {
        if (modelObserver == null) {
            throw new IllegalStateException("Model Observer has not been set");
        }
        return modelObserver;
    }
}
