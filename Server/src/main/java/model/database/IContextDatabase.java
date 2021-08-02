package model.database;

import model.context.Context;
import model.context.ContextID;
import model.context.spatial.AreaReservation;
import model.context.spatial.World;
import model.user.User;

import java.util.Map;

public interface IContextDatabase {
    void addWorld(World world);
    void removeWorld(World world);
    World getWorld(ContextID worldID);
    Map<ContextID, World> getWorlds();

    void addBannedUser(User user, Context world);
    void removeBannedUser(User user, Context world);
    void addAreaReservation(AreaReservation areaReservation);
    void removeAreaReservation(AreaReservation areaReservation);
}
