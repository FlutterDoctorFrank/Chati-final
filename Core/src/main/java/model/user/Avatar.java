package model.user;

import model.Resource;
import org.jetbrains.annotations.NotNull;

/**
 * Eine Enumeration, welche den Avatar eines Benutzers repräsentiert.
 */
public enum Avatar implements Resource {

    ADAM("Adam", "adam"),
    ALEX("Alex", "alex"),
    AMELIA("Amelia", "amelia"),
    ASH("Ash", "ash"),
    BOB("Bob", "bob"),
    BRUCE("Bruce", "bruce"),
    DAN("Dan", "dan"),
    EDWARD("Edward", "edward"),
    LUCY("Lucy", "lucy"),
    RICK("Rick", "rick"),
    WHITEGHOST("", "whiteghost"),
    BLUEGHOST(" ", "blueghost"),
    BLACKGHOST("  ", "blackghost");

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
        return this.path.toLowerCase();
    }

    /**
     * Gibt zurück, ob dieser Avatar ein Geist ist.
     * @return true, wenn der Avatar ein Geist ist, sonst false.
     */
    public boolean isGhost() {
        return this == WHITEGHOST || this == BLUEGHOST || this == BLACKGHOST;
    }
}
