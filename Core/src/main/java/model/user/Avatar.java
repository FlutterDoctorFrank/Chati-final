package model.user;

import model.Resource;
import org.jetbrains.annotations.NotNull;

/**
 * Eine Enumeration, welche den Avatar eines Benutzers repräsentiert.
 */
public enum Avatar implements Resource {

    PLACEHOLDER("Adam", "Adam/Adam", "Adam_idle", "Adam_run"),
    ADAM("Adam", "Adam/Adam", "Adam_idle", "Adam_run"),
    ALEX("Alex", "Alex/Alex", "Alex_idle", "Alex_run"),
    AMELIA("Amelia", "Amelia/Amelia", "Amelia_idle", "Amelia_run"),
    ASH("Ash", "Ash/Ash", "Ash_idle", "Ash_run"),
    BOB("Bob", "Bob/Bob", "Bob_idle", "Bob_run"),
    BRUCE("Bruce", "Bruce/Bruce", "Bruce_idle", "Bruce_run"),
    DAN("Dan", "Dan/Dan", "Dan_idle", "Dan_run"),
    EDWARD("Edward", "Edward/Edward", "Edward_idle", "Edward_run"),
    LUCY("Lucy", "Lucy/Lucy", "Lucy_idle", "Lucy_run"),
    RICK("Rick", "Rick/Rick", "Rick_idle", "Rick_run");

    private static final String AVATARS_PATH = "avatars/";

    private final String name;
    private final String path;
    private final String idleRegionName;
    private final String runRegionName;

    Avatar(@NotNull final String name, @NotNull final String path,
           @NotNull final String idleRegionName, @NotNull final String runRegionName) {
        this.name = name;
        this.path = AVATARS_PATH + path + ".pack";
        this.idleRegionName = idleRegionName;
        this.runRegionName = runRegionName;
    }

    @Override
    public @NotNull String getName() {
        return this.name;
    }

    @Override
    public @NotNull String getPath() {
        return this.path;
    }

    public @NotNull String getIdleRegionName() {
        return this.idleRegionName;
    }

    public @NotNull String getRunRegionName() {
        return this.runRegionName;
    }
}
