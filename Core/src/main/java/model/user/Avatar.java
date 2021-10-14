package model.user;

import model.Resource;
import org.jetbrains.annotations.NotNull;

/**
 * Eine Enumeration, welche den Avatar eines Benutzers repräsentiert.
 */
public enum Avatar implements Resource {

    ADAM("Adam", "adam", true, false),
    ALEX("Alex", "alex", true, false),
    AMELIA("Amelia", "amelia", false, false),
    ASH("Ash", "ash", true, false),
    BOB("Bob", "bob", true, false),
    BRUCE("Bruce", "bruce", true, false),
    DAN("Dan", "dan", true, false),
    EDWARD("Edward", "edward", true, false),
    LUCY("Lucy", "lucy", false, false),
    RICK("Rick", "rick", true, false),
    WHITEGHOST("", "whiteghost", false, true),
    BLUEGHOST(" ", "blueghost", false, true),
    BLACKGHOST("  ", "blackghost", true, true);

    private final String name;
    private final String path;
    private final boolean male;
    private final boolean transparent;

    Avatar(@NotNull final String name, @NotNull final String path, final boolean male, final boolean transparent) {
        this.name = name;
        this.path = path;
        this.male = male;
        this.transparent = transparent;
    }

    public @NotNull String getName() {
        return this.name;
    }

    @Override
    public @NotNull String getPath() {
        return this.path.toLowerCase();
    }

    /**
     * Gibt zurück, ob dieser Avatar männlich ist.
     * @return true, wenn der Avatar männlich ist, false wenn er weiblich ist.
     */
    public boolean isMale() {
        return this.male;
    }

    /**
     * Gibt zurück, ob dieser Avatar ein Geist ist.
     * @return true, wenn der Avatar ein Geist ist, sonst false.
     */
    public boolean isTransparent() {
        return this.transparent;
    }
}
