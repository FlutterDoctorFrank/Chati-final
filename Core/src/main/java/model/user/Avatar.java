package model.user;

import model.Resource;
import org.jetbrains.annotations.NotNull;

/**
 * Eine Enumeration, welche den Avatar eines Benutzers repräsentiert.
 */
public enum Avatar implements Resource {

    ;

    private static final String PATH = "avatars/";

    private final String name;
    private final String path;

    Avatar(@NotNull final String name) {
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
