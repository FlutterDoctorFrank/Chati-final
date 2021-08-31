package model.context.spatial;

import model.Resource;
import org.jetbrains.annotations.NotNull;

/**
 * Eine Enumeration, welche die Karten repräsentiert, die ein räumlicher Kontext haben kann.
 */
public enum ContextMap implements Resource {

    //DEFAULT_WORLD("Standardwelt", "worlds/default-world"),
    PUBLIC_ROOM_MAP("Public Room", "publicRoomMap/publicRoom", true),
    PRIVATE_ROOM_MAP("Private Room", "privateRoomMap/privateRoom", false);

    private static final String MAPS_PATH = "maps/";

    private final String name;
    private final String path;
    private final boolean publicRoomMap;

    ContextMap(@NotNull final String name, @NotNull final String path, final boolean publicRoomMap) {
        this.name = name;
        this.path = MAPS_PATH + path + ".tmx";
        this.publicRoomMap = publicRoomMap;
    }

    @Override
    public @NotNull String getName() {
        return this.name;
    }

    @Override
    public @NotNull String getPath() {
        return this.path;
    }

    public boolean isPublicRoomMap() {
        return publicRoomMap;
    }
}