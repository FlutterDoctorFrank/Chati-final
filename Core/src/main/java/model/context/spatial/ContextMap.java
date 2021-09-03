package model.context.spatial;

import model.Resource;
import org.jetbrains.annotations.NotNull;

/**
 * Eine Enumeration, welche die Karten repräsentiert, die ein räumlicher Kontext haben kann.
 */
public enum ContextMap implements Resource {

    PUBLIC_ROOM_MAP("Public Room", "public_room", true),
    PRIVATE_ROOM_MAP("Private Room", "private_room", false);

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

    /**
     * Gibt zurück, ob es sich um eine Karte für einen öffentlichen Raum handelt.
     * @return true, wenn es eine Karte für den öffentlichen Raum ist, ansonsten false.
     */
    public boolean isPublicRoomMap() {
        return this.publicRoomMap;
    }
}