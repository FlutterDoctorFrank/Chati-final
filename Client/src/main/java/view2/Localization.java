package view2;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.I18NBundle;
import org.jetbrains.annotations.NotNull;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Eine Klasse, die die Sprache der Anwendung verwaltet und für die Übersetzung und Formatierung
 * von Nachrichten zuständig ist.
 */
public class Localization {

    public static final Locale[] AVAILABLE_LOCALES = new Locale[]{Locale.GERMAN, Locale.ENGLISH};

    private static final Logger LOGGER = Logger.getLogger("chati.localization");
    private static final String BASE = "localizations/localization";
    private static Localization instance;

    private final FileHandle handle;
    private I18NBundle bundle;

    private Localization() {
        this.handle = Gdx.files.internal(BASE);

        I18NBundle.setExceptionOnMissingKey(true);
        I18NBundle.setSimpleFormatter(false);

        instance = this;
    }

    /**
     * Lädt die Nachrichten, die in die angegebene Sprache übersetzt sind.
     * Falls keine Übersetzung in die angegebene Sprache gefunden wird, so wird die Standardsprache der Anwendung
     * geladen.
     * @param locale Die Sprache die geladen werden soll.
     */
    public void load(@NotNull final Locale locale) {
        try {
            this.bundle = I18NBundle.createBundle(this.handle, locale);

            if (this.bundle.getLocale().equals(locale)) {
                LOGGER.info("Loaded resource bundle for language: " + locale);
            } else {
                LOGGER.warning("Unable to find resource bundle for language: " + locale);
                LOGGER.info("Loaded fallback resource bundle for language: " + this.bundle.getLocale());
            }
        } catch (MissingResourceException ex) {
            throw new IllegalStateException("Resource bundle " + this.handle.name() + " not available", ex);
        }

        LOGGER.info(String.format("Locale has been changed. Using locale %s", this.bundle.getLocale()));
    }

    /**
     * Gibt die aktuelle Sprache der Anwendung zurück.
     * @return die aktuell ausgewählte Sprache.
     */
    public static @NotNull Locale locale() {
        if (instance == null) {
            throw new IllegalStateException("Localization is not initialized");
        }

        return instance.bundle.getLocale();
    }

    /**
     * Lädt die Nachricht mit dem angegebenen Nachrichten-Schlüssel.
     * @param key Der Nachrichten-Schlüssel.
     * @return Die übersetzte Nachricht.
     */
    public static @NotNull String translate(@NotNull final String key) {
        if (instance == null) {
            throw new IllegalStateException("Localization is not initialized");
        }

        try {
            return instance.bundle.get(key);
        } catch (MissingResourceException ex) {
            LOGGER.log(Level.WARNING, String.format("Missing translation key '%s' in resource bundles", ex.getKey()), ex);

            return "???" + ex.getKey() + "???";
        }
    }

    /**
     * Lädt die Nachricht mit dem angegebenen Nachrichten-Schlüssel und formatiert diese mit den angegebenen Argumenten.
     * @param key Der Nachrichten-Schlüssel.
     * @param arguments Die Argumente zum Formatieren der Nachricht.
     * @return Die übersetzte und formatierte Nachricht.
     */
    public static @NotNull String format(@NotNull final String key, @NotNull final Object... arguments) {
        if (instance == null) {
            throw new IllegalStateException("Localization is not initialized");
        }

        if (arguments.length > 0) {
            try {
                return instance.bundle.format(key, arguments);
            } catch (MissingResourceException ex) {
                LOGGER.log(Level.WARNING, String.format("Missing translation key '%s' in resource bundles", ex.getKey()), ex);

                return "???" + ex.getKey() + "???";
            }
        }

        return translate(key);
    }

    /**
     * Gibt die Instanz der Localization Klasse zurück.
     * @return die Localization Instanz.
     */
    public static @NotNull Localization getInstance() {
        if (instance == null) {
            return instance = new Localization();
        }

        return instance;
    }

    /**
     * Eine Schnittstelle, welche von all den Menüs und Fenster implementiert wird, welche übersetzbar sind.
     */
    public interface Translatable {

        /**
         * Ruft das Objekt dazu auf die übersetzbaren Nachrichten neu zu laden.
         */
        void translate();
    }
}
