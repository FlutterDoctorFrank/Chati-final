package model.database;

import model.context.Context;
import model.context.ContextID;
import model.context.spatial.AreaReservation;
import model.context.spatial.World;
import model.user.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.Map;

public interface IContextDatabase {

    void addWorld(@NotNull final World world);

    void removeWorld(@NotNull final World world);

    @Nullable World getWorld(@NotNull final ContextID worldID);

    @NotNull Map<ContextID, World> getWorlds();

    void getBannedUsers(@NotNull final World world);

    void addBannedUser(@NotNull final User user, @NotNull final Context world);

    void removeBannedUser(@NotNull final User user, @NotNull final Context world);

    void getAreaReservations(@NotNull final World world);

    void addAreaReservation(@NotNull final AreaReservation areaReservation);

    void removeAreaReservation(@NotNull final AreaReservation areaReservation);
}
