package model.user;

import model.Resource;
import org.jetbrains.annotations.NotNull;

/**
 * Eine Enumeration, welche den Avatar eines Benutzers repräsentiert.
 */
public enum Avatar implements Resource {

    ADAM("Adam", "adam/adam"),
    ALEX("Alex", "alex/alex"),
    AMELIA("Amelia", "amelia/amelia"),
    ASH("Ash", "ash/ash"),
    BOB("Bob", "bob/bob"),
    BRUCE("Bruce", "bruce/bruce"),
    DAN("Dan", "dan/dan"),
    EDWARD("Edward", "edward/edward"),
    LUCY("Lucy", "lucy/lucy"),
    RICK("Rick", "rick/rick"),
    WHITEGHOST("Henry", "whiteghost/whiteghost"),
    BLUEGHOST("Frederik", "blueghost/blueghost"),
    BLACKGHOST("Martin", "blackghost/blackghost");

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
