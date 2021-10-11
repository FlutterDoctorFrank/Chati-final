package view;

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
public class ChatiLocalization {

    public static final Locale[] AVAILABLE_LOCALES = new Locale[]{Locale.GERMAN, Locale.ENGLISH};

    private static final Logger LOGGER = Logger.getLogger("chati.localization");
    private static final String BASE = "localizations/localization";

    private final FileHandle handle;
    private I18NBundle bundle;

    public ChatiLocalization() {
        this.handle = Gdx.files.internal(BASE);

        I18NBundle.setExceptionOnMissingKey(true);
        I18NBundle.setSimpleFormatter(false);

        this.load();
    }

    /**
     * Lädt die Nachrichten, die in die angegebene Sprache übersetzt sind.
     * Falls keine Übersetzung in die angegebene Sprache gefunden wird, so wird die Standardsprache der Anwendung
     * geladen.
     */
    public void load() {
        Locale locale = Chati.CHATI.getPreferences().getLanguage();
        try {
            this.bundle = I18NBundle.createBundle(this.handle, locale);

            if (this.bundle.getLocale().equals(locale)) {
                LOGGER.info("Loaded resource bundle for language: " + locale);
            } else {
                LOGGER.warning("Unable to find resource bundle for language: " + locale);
                LOGGER.info("Loaded fallback resource bundle for language: " + this.bundle.getLocale());
            }
        } catch (MissingResourceException e) {
            throw new IllegalStateException("Resource bundle " + this.handle.name() + " not available", e);
        }

        LOGGER.info(String.format("Locale has been changed. Using locale %s", this.bundle.getLocale()));
    }

    /**
     * Gibt die aktuelle Sprache der Anwendung zurück.
     * @return die aktuell ausgewählte Sprache.
     */
    public @NotNull Locale getLocale() {
        return bundle.getLocale();
    }

    /**
     * Lädt die Nachricht mit dem angegebenen Nachrichten-Schlüssel.
     * @param key Der Nachrichten-Schlüssel.
     * @return Die übersetzte Nachricht.
     */
    public @NotNull String translate(@NotNull final String key) {
        try {
            return bundle.get(key);
        } catch (MissingResourceException e) {
            LOGGER.log(Level.WARNING, String.format("Missing translation key '%s' in resource bundles", e.getKey()), e);

            return "???" + e.getKey() + "???";
        }
    }

    /**
     * Lädt die Nachricht mit dem angegebenen Nachrichten-Schlüssel und formatiert diese mit den angegebenen Argumenten.
     * @param key Der Nachrichten-Schlüssel.
     * @param arguments Die Argumente zum Formatieren der Nachricht.
     * @return Die übersetzte und formatierte Nachricht.
     */
    public @NotNull String format(@NotNull final String key, @NotNull final Object... arguments) {
        if (arguments.length > 0) {
            try {
                return bundle.format(key, arguments);
            } catch (MissingResourceException e) {
                LOGGER.log(Level.WARNING, String.format("Missing translation key '%s' in resource bundles", e.getKey()), e);

                return "???" + e.getKey() + "???";
            }
        }

        return translate(key);
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
