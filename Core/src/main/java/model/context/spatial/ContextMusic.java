package model.context.spatial;

import model.Resource;
import org.jetbrains.annotations.NotNull;

/**
 * Eine Enumeration, welche die Musikstücke repräsentiert, die in einem räumlichen Kontext abgespielt werden können.
 */
public enum ContextMusic implements Resource {

    PLACEHOLDER("placeholder");

    private static final String PATH = "musics/";

    private final String name;
    private final String path;

    ContextMusic(@NotNull final String name) {
        this.name = name;
        this.path = PATH + this.name.toLowerCase();
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
