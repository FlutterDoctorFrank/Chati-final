package model.user;

import model.Resource;
import org.jetbrains.annotations.NotNull;

/**
 * Eine Enumeration, welche den Avatar eines Benutzers repräsentiert.
 */
public enum Avatar implements Resource {

    PLACEHOLDER("Adam/Adam.pack", "Adam_idle", "Adam_run"),
    ADAM("Adam/Adam.pack", "Adam_idle", "Adam_run");

    private static final String PATH = "avatars/";

    private final String name;
    private final String path;
    private final String idleRegion;
    private final String runRegion;

    Avatar(@NotNull final String name, @NotNull final String idleRegion, @NotNull final String runRegion) {
        this.name = name;
        this.path = PATH + this.name.toLowerCase();
        this.idleRegion = idleRegion;
        this.runRegion = runRegion;
    }

    @Override
    public @NotNull String getName() {
        return this.name;
    }

    @Override
    public @NotNull String getPath() {
        return this.path;
    }

    public @NotNull String getIdleRegion() {
        return this.idleRegion;
    }

    public @NotNull String getRunRegion() {
        return this.runRegion;
    }
}
