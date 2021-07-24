package model.context.global;

import model.context.ContextID;
import model.context.IContext;
import model.context.spatial.Map;
import model.context.spatial.SpatialContext;

import java.util.UUID;

public interface IGlobalContext extends IContext {
    public void createWorld(UUID performerID, Map map, String worldname);
    public void removeWorld(UUID performerID, ContextID worldID);
    public java.util.Map<ContextID, SpatialContext> getWorlds();
}
