package model.context.spatial;

import model.Resource;
import org.jetbrains.annotations.NotNull;

/**
 * Eine Enumeration, welche die Karten repräsentiert, die ein räumlicher Kontext haben kann.
 */
public enum SpatialMap implements Resource {

    MAP("map");

    private static final String PATH = "maps/";

    private final String name;
    private final String path;

    SpatialMap(@NotNull final String name) {
        this.name = name;
        this.path = PATH + this.name().toLowerCase() + ".tmx";
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