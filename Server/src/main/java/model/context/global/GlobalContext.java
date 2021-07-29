package model.context.global;

import controller.network.ClientSender;
import model.context.Context;
import model.context.ContextID;
import model.context.spatial.SpatialMap;
import model.context.spatial.SpatialContext;
import model.database.Database;
import model.database.IGlobalContextDatabase;
import model.exception.*;
import model.role.Permission;
import model.user.User;
import model.user.account.UserAccountManager;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

public class GlobalContext extends Context implements IGlobalContext {
    private static GlobalContext globalContext;
    private Map<ContextID, SpatialContext> worlds;
    private IGlobalContextDatabase database;

    private GlobalContext() {
        super("Global", null);
        database = Database.getGlobalContextDatabase();
        worlds = database.getWorlds();
    }

    @Override
    public void createWorld(UUID performerID, String worldname, SpatialMap map) throws UserNotFoundException, NoPermissionException, IllegalWorldActionException {
        User performer = UserAccountManager.getInstance().getUser(performerID);
        if (!performer.hasPermission(GlobalContext.getInstance(), Permission.MANAGE_WORLDS)) {
            throw new NoPermissionException("no permission", performer, Permission.MANAGE_WORLDS);
        }
        if (worlds.entrySet().stream().anyMatch(entry -> entry.getValue().getContextName().equals(worldname))) {
            throw new IllegalWorldActionException("", "Eine Welt mit diesem Namen existiert bereits");
        }
        SpatialContext world = new SpatialContext(worldname, map);
        worlds.put(world.getContextID(), world);

        // Send all online users info about new world
        getContainedUsers().forEach((userID, user) -> {
            user.getClientSender().send(ClientSender.SendAction.CONTEXT_INFO, world);
        });
    }

    @Override
    public void removeWorld(UUID performerID, ContextID worldID) throws UserNotFoundException, NoPermissionException, ContextNotFoundException {
        User performer = UserAccountManager.getInstance().getUser(performerID);
        if (!performer.hasPermission(GlobalContext.getInstance(), Permission.MANAGE_WORLDS)) {
            throw new NoPermissionException("no permission", performer, Permission.MANAGE_WORLDS);
        }
        SpatialContext world = worlds.get(worldID);
        if (world == null) {
            throw new ContextNotFoundException("no such context", worldID);
        }
        world.getContainedUsers().forEach((userID, user) -> {
            try {
                user.leaveWorld();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        });
        worlds.remove(worldID);

        // Send all online users info about deleted world
        getContainedUsers().forEach((userID, user) -> {
            user.getClientSender().send(ClientSender.SendAction.CONTEXT_INFO, world);
        });
    }

    @Override
    public java.util.Map<ContextID, SpatialContext> getWorlds() {
        return Collections.unmodifiableMap(worlds);
    }

    public SpatialContext getWorld(ContextID worldID) throws ContextNotFoundException {
        SpatialContext world = worlds.get(worldID);
        if (world == null) {
            throw new ContextNotFoundException("Context does not exist.", worldID);
        }
        return world;
    }

    public static GlobalContext getInstance() {
        if (globalContext == null) {
            globalContext = new GlobalContext();
        }
        return globalContext;
    }
}
