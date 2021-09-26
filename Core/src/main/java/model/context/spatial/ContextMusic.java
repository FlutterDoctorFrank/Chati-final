package model.context.spatial;

import model.Resource;
import org.jetbrains.annotations.NotNull;

/**
 * Eine Enumeration, welche die Musikstücke repräsentiert, die in einem räumlichen Kontext abgespielt werden können.
 */
public enum ContextMusic implements Resource {

    COUNTRYBODY("Countrybody", "bensound-countryboy"),
    CREATIVEMINDS("Creativeminds", "bensound-creativeminds"),
    DREAMS("Dreams", "bensound-dreams"),
    EPIC("Epic", "bensound-epic"),
    FUNNYSONG("Funnysong", "bensound-funnysong"),
    HAPPYROCK("Happyrock", "bensound-happyrock"),
    HIGHOCTANE("Highoctane", "bensound-highoctane"),
    HIPJAZZ("Hipjazz", "bensound-hipjazz"),
    PUNKY("Punky", "bensound-punky"),
    RETROSOUL("Retrosoul", "bensound-retrosoul");

    private static final String MUSIC_PATH = "music/";

    private final String name;
    private final String path;

    /**
     * Erzeugt eine neue Instanz der ContextMusic.
     * @param name Name des Musikstücks.
     * @param path Pfad des Musikstücks.
     */
    ContextMusic(@NotNull final String name, @NotNull final String path) {
        this.name = name;
        this.path = MUSIC_PATH + path + ".wav";
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
