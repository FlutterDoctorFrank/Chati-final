package model.context.spatial;

import model.Resource;
import org.jetbrains.annotations.NotNull;

/**
 * Eine Enumeration, welche die Karten repräsentiert, die ein räumlicher Kontext haben kann.
 */
public enum SpatialMap implements Resource {

    //MAP("map"),
    PUBLIC_ROOM_MAP("Public Room", "publicRoomMap/publicRoom"),
    PRIVATE_ROOM_MAP("Private Room", "privateRoomMap/privateRoom");

    private static final String MAPS_PATH = "maps/";

    private final String name;
    private final String path;

    SpatialMap(@NotNull final String name, @NotNull final String path) {
        this.name = name;
        this.path = MAPS_PATH + path + ".tmx";
    }

    @Override
    public @NotNull String getName() {
        return this.name;
    }

    @Override
    public @NotNull String getPath() {
        return this.path;
    }
}