package model.context.global;

import controller.network.ClientSender.SendAction;
import model.communication.CommunicationMedium;
import model.context.Context;
import model.context.ContextID;
import model.context.spatial.IWorld;
import model.context.spatial.ContextMap;
import model.context.spatial.World;
import model.exception.ContextNotFoundException;
import model.exception.IllegalWorldActionException;
import model.exception.NoPermissionException;
import model.exception.UserNotFoundException;
import model.role.Permission;
import model.user.User;
import model.user.account.UserAccountManager;
import org.jetbrains.annotations.NotNull;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Predicate;

/**
 * Eine Klasse, welche den globalen Kontext der Anwendung repräsentiert. Kann nur einmal instanziiert werden.
 */
public class GlobalContext extends Context implements IGlobalContext {

    private static final String CONTEXT_NAME = "Global";

    /** Singleton-Instanz der Klasse. */
    private static GlobalContext globalContext;

    /** Menge aller Welten. */
    private final Map<ContextID, World> worlds;

    /**
     * Erzeugt eine neue Instanz des globalen Kontexts.
     */
    private GlobalContext() {
        super(CONTEXT_NAME, null);
        this.worlds = new HashMap<>();
    }

    public void load() {
        this.worlds.clear();
        this.worlds.putAll(this.database.getWorlds());
    }

    @Override
    public void createWorld(@NotNull final UUID performerId, @NotNull final String worldName,
                            @NotNull final ContextMap map) throws UserNotFoundException, NoPermissionException, IllegalWorldActionException {
        User performer = UserAccountManager.getInstance().getUser(performerId);
        performer.updateLastActivity();
        // Überprüfe, ob der ausführende Benutzer die nötige Berechtigung besitzt.
        if (!performer.hasPermission(GlobalContext.getInstance(), Permission.MANAGE_WORLDS)) {
            throw new NoPermissionException("no permission", "action.world-create.not-permitted", performer, Permission.MANAGE_WORLDS);
        }
        // Überprüfe, ob bereits eine Welt mit diesem Namen existiert.
        if (worlds.values().stream().anyMatch(world -> world.getContextName().equals(worldName))) {
            throw new IllegalWorldActionException("", "action.world-create.already-created", worldName);
        }
        // Erzeuge die Welt und füge sie
        World createdWorld = new World(worldName, map);
        worlds.put(createdWorld.getContextId(), createdWorld);
        addChild(createdWorld);
        database.addWorld(createdWorld);
        // Sende neue Liste der Welten an alle Benutzer im Startbildschirm.
        sendWorldList();
    }

    @Override
    public void removeWorld(@NotNull final UUID performerId, @NotNull final ContextID worldId) throws UserNotFoundException, NoPermissionException, ContextNotFoundException {
        User performer = UserAccountManager.getInstance().getUser(performerId);
        performer.updateLastActivity();
        // Überprüfe, ob der ausführende Benutzer die nötige Berechtigung besitzt.
        if (!performer.hasPermission(GlobalContext.getInstance(), Permission.MANAGE_WORLDS)) {
            throw new NoPermissionException("no permission", "action.world-delete.not-permitted", performer, Permission.MANAGE_WORLDS);
        }
        // Überprüfe, ob die zu löschende Welt existiert.
        World deleteWorld = worlds.get(worldId);
        if (deleteWorld == null) {
            throw new ContextNotFoundException("no such context", worldId);
        }
        // Verlasse die Welt mit allen Benutzern, die in dieser enthalten sind.
        deleteWorld.getUsers().values().forEach(user -> {
            try {
                user.leaveWorld();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        });
        // Entferne die Welt.
        worlds.remove(worldId);
        removeChild(deleteWorld);
        database.removeWorld(deleteWorld);
        // Sende neue Liste der Welten an alle Benutzer im Startbildschirm.
        sendWorldList();
    }

    @Override
    public @NotNull Map<ContextID, IWorld> getIWorlds() {
        return Collections.unmodifiableMap(worlds);
    }

    /**
     * Gibt die Menge aller Welten zurück.
     * @return Menge aller Welten.
     */
    public Map<ContextID, World> getWorlds() {
        return worlds;
    }

    /**
     * Gibt die Welt mit der übergebenen ID zurück.
     * @param worldId: ID der zurückzugebenden Welt.
     * @return Welt mit der übergebenen ID.
     * @throws ContextNotFoundException: wenn keine Welt mit der übergebenen ID existiert.
     */
    public @NotNull World getWorld(@NotNull final ContextID worldId) throws ContextNotFoundException {
        World world = worlds.get(worldId);
        if (world == null) {
            throw new ContextNotFoundException("Context does not exist.", worldId);
        }
        return world;
    }

    /**
     * Gibt die Singleton-Instanz der Klasse zurück.
     * @return Die Instanz von GlobalContext.
     */
    public static @NotNull GlobalContext getInstance() {
        if (globalContext == null) {
            globalContext = new GlobalContext();
        }
        return globalContext;
    }

    @Override
    public @NotNull Map<UUID, User> getCommunicableUsers(@NotNull final User communicatingUser) {
        return Collections.emptyMap();
    }

    @Override
    public boolean canCommunicateWith(@NotNull final CommunicationMedium medium) {
        return false;
    }

    /**
     * Sendet die Liste aller existierenden Welten an alle Benutzer im Startbildschirm.
     */
    private void sendWorldList() {
        this.containedUsers.values().stream()
                .filter(Predicate.not(User::isInWorld))
                .forEach(receiver -> receiver.send(SendAction.CONTEXT_LIST, this));
    }
}
