package model.database;

import model.context.Context;
import model.context.ContextID;
import model.context.spatial.AreaReservation;
import model.context.spatial.SpatialContext;
import model.user.User;

import java.util.Map;

public interface IContextDatabase {
    void addWorld(SpatialContext world);
    void removeWorld(SpatialContext world);
    SpatialContext getWorld(ContextID worldID);
    Map<ContextID, SpatialContext> getWorlds();

    void addBannedUser(User user, Context world);
    void removeBannedUser(User user, Context world);
    void addAreaReservation(AreaReservation areaReservation);
    void removeAreaReservation(AreaReservation areaReservation);
}
