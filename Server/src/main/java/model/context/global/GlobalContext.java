package model.context.global;

import model.context.Context;
import model.context.ContextID;
import model.context.spatial.Map;
import model.context.spatial.SpatialContext;
import model.database.Database;
import model.database.IGlobalContextDatabase;
import model.exception.ContextNotFoundException;

import java.util.Collections;
import java.util.UUID;

public class GlobalContext extends Context implements IGlobalContext {
    private static GlobalContext globalContext;
    private java.util.Map<ContextID, SpatialContext> worlds;
    private IGlobalContextDatabase database;

    private GlobalContext() {
        super();
        database = Database.getGlobalContextDatabase();
    }

    @Override
    public void createWorld(UUID performerID, Map map, String worldname) {
        // TODO
    }

    @Override
    public void removeWorld(UUID performerID, ContextID worldID) {
        // TODO
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
