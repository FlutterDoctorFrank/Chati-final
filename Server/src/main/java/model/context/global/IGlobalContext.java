package model.context.global;

import model.context.ContextID;
import model.context.IContext;
import model.context.spatial.SpatialMap;
import model.context.spatial.SpatialContext;
import model.exception.ContextNotFoundException;
import model.exception.IllegalWorldActionException;
import model.exception.NoPermissionException;
import model.exception.UserNotFoundException;

import java.util.UUID;

public interface IGlobalContext extends IContext {
    public void createWorld(UUID performerID, String worldname, SpatialMap map) throws UserNotFoundException, NoPermissionException, IllegalWorldActionException;
    public void removeWorld(UUID performerID, ContextID worldID) throws UserNotFoundException, NoPermissionException, ContextNotFoundException;
    public java.util.Map<ContextID, SpatialContext> getWorlds();
}
