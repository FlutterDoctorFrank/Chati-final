package model.database;

import model.context.ContextID;
import model.context.spatial.SpatialContext;

import java.util.Map;

public interface IGlobalContextDatabase {
    public void addWorld(SpatialContext world);
    public void removeWorld(SpatialContext world);
    public void getWorld(ContextID worldID);
    public Map<ContextID, SpatialContext> getWorlds();
}
