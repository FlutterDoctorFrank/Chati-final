package model.user;

import model.Resource;
import org.jetbrains.annotations.NotNull;

/**
 * Eine Enumeration, welche den Avatar eines Benutzers repräsentiert.
 */
public enum Avatar implements Resource {

    WHITEGHOST("Henry", "whiteghost"),
    BLUEGHOST("Frederik", "blueghost"),
    BAT("Gustav", "bat") /*,
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
    private final String path;

    Avatar(@NotNull final String name, @NotNull final String path) {
        this.name = name;
        this.path = path;
    }

    public @NotNull String getName() {
        return this.name;
    }

    @Override
    public @NotNull String getPath() {
        return "avatar/" + this.path.toLowerCase();
    }
}
