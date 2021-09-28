package model.context.spatial;

import model.Resource;
import org.jetbrains.annotations.NotNull;

/**
 * Eine Enumeration, welche die Musikstücke repräsentiert, die in einem räumlichen Kontext abgespielt werden können.
 */
public enum ContextMusic implements Resource {

    ALLTHAT("Allthat", "bensound-allthat"),
    COUNTRYBODY("Countryboy", "bensound-countryboy"),
    CREATIVEMINDS("Creativeminds", "bensound-creativeminds"),
    CREEPY("Creepy", "bensound-creepy"),
    DREAMS("Dreams", "bensound-dreams"),
    ENDLESSMOTION("Endlessmotion", "bensound-endlessmotion"),
    EPIC("Epic", "bensound-epic"),
    EVOLUTION("Evolution", "bensound-evolution"),
    FUNNYSONG("Funnysong", "bensound-funnysong"),
    GROOVYHIPHOP("Groovyhiphop", "bensound-groovyhiphop"),
    HAPPYROCK("Happyrock", "bensound-happyrock"),
    HIGHOCTANE("Highoctane", "bensound-highoctane"),
    HIPJAZZ("Hipjazz", "bensound-hipjazz"),
    PUNKY("Punky", "bensound-punky"),
    RETROSOUL("Retrosoul", "bensound-retrosoul"),
    SEXY("Sexy", "bensound-sexy");

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
