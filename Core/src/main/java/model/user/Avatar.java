package model.user;

import model.Resource;
import org.jetbrains.annotations.NotNull;

/**
 * Eine Enumeration, welche den Avatar eines Benutzers repräsentiert.
 */
public enum Avatar implements Resource {


    BAT("bat") /*,
    ALEX("Alex"),
    AMELIA("Amelia"),
    ASH("Ash"),
    BOB("Bob"),
    BRUCE("Bruce"),
    DAN("Dan"),
    EDWARD("Edward"),
    LUCY("Lucy"),
    RICK("Rick");
    */;

    private final String name;

    Avatar(@NotNull final String name) {
        this.name = name;
    }

    public @NotNull String getName() {
        return this.name;
    }

    @Override
    public @NotNull String getPath() {
        return "avatar/" + this.name.toLowerCase();
    }
}
