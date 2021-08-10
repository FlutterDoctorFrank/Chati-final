package model.context.global;

import controller.network.ClientSender;
import model.communication.CommunicationMedium;
import model.context.Context;
import model.context.ContextID;
import model.context.spatial.*;
import model.exception.*;
import model.role.Permission;
import model.user.User;
import model.user.account.UserAccountManager;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

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
        worlds = database.getWorlds();
    }

    @Override
    public void createWorld(UUID performerId, String worldname, SpatialMap map) throws UserNotFoundException, NoPermissionException, IllegalWorldActionException {
        User performer = UserAccountManager.getInstance().getUser(performerId);
        performer.updateLastActivity();
        // Überprüfe, ob der ausführende Benutzer die nötige Berechtigung besitzt.
        if (!performer.hasPermission(GlobalContext.getInstance(), Permission.MANAGE_WORLDS)) {
            throw new NoPermissionException("no permission", performer, Permission.MANAGE_WORLDS);
        }
        // Überprüfe, ob bereits eine Welt mit diesem Namen existiert.
        if (worlds.values().stream().anyMatch(world -> world.getContextName().equals(worldname))) {
            throw new IllegalWorldActionException("", "Eine Welt mit diesem Namen existiert bereits");
        }
        // Erzeuge die Welt und füge sie
        World createdWorld = new World(worldname, map);
        worlds.put(createdWorld.getContextId(), createdWorld);
        addChild(createdWorld);
        // Sende neue Liste der Welten an alle Benutzer im Startbildschirm.
        sendWorldList();
    }

    @Override
    public void removeWorld(UUID performerId, ContextID worldId) throws UserNotFoundException, NoPermissionException, ContextNotFoundException {
        User performer = UserAccountManager.getInstance().getUser(performerId);
        performer.updateLastActivity();
        // Überprüfe, ob der ausführende Benutzer die nötige Berechtigung besitzt.
        if (!performer.hasPermission(GlobalContext.getInstance(), Permission.MANAGE_WORLDS)) {
            throw new NoPermissionException("no permission", performer, Permission.MANAGE_WORLDS);
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
        // Sende neue Liste der Welten an alle Benutzer im Startbildschirm.
        sendWorldList();
    }

    @Override
    public Map<ContextID, IWorld> getWorlds() {
        return Collections.unmodifiableMap(worlds);
    }

    /**
     * Gibt die Welt mit der übergebenen ID zurück.
     * @param worldId: ID der zurückzugebenden Welt.
     * @return Welt mit der übergebenen ID.
     * @throws ContextNotFoundException: wenn keine Welt mit der übergebenen ID existiert.
     */
    public World getWorld(ContextID worldId) throws ContextNotFoundException {
        World world = worlds.get(worldId);
        if (world == null) {
            throw new ContextNotFoundException("Context does not exist.", worldId);
        }
        return world;
    }

    @Override
    public Context getContext(ContextID contextId) throws ContextNotFoundException {
        Context found = super.getContext(contextId);
        if (found == null) {
            throw new ContextNotFoundException("There is no context with this ID.", contextId);
        }
        return found;
    }

    /**
     * Gibt die Singleton-Instanz der Klasse zurück.
     * @return Die Instanz von GlobalContext.
     */
    public static GlobalContext getInstance() {
        if (globalContext == null) {
            globalContext = new GlobalContext();
        }
        return globalContext;
    }

    @Override
    public Map<UUID, User> getCommunicableUsers(User communicatingUser) {
        return new HashMap<>();
    }

    @Override
    public boolean canCommunicateWith(CommunicationMedium medium) {
        return false;
    }

    /**
     * Sendet die Liste aller existierenden Welten an alle Benutzer im Startbildschirm.
     */
    private void sendWorldList() {
        containedUsers.values().stream().filter(Predicate.not(User::isInWorld))
                .forEach(receiver -> receiver.getClientSender().send(ClientSender.SendAction.CONTEXT_LIST, this));
    }
}
