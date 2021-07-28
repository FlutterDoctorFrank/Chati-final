package model.context.spatial;

import model.context.ContextID;
import model.context.IContext;

import java.util.Map;

public interface ISpatialContext extends IContext {
    public Map<ContextID, SpatialContext> getPrivateRooms();
    public SpatialMap getMap();
}
