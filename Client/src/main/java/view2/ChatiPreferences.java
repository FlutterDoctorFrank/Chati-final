package view2;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;

import java.util.List;

/**
 * Eine Klasse, über die Einstellungen dauerhaft gespeichert und abgerufen werden können.
 */
public class ChatiPreferences {

    public static final String DEFAULT_LANGUAGE = "german";
    public static final boolean DEFAULT_ALWAYS_SPRINTING = false;
    public static final boolean DEFAULT_SHOW_NAMES_IN_WORLD = true;
    public static final boolean DEFAULT_MICROPHONE_ON = false;
    public static final boolean DEFAULT_SOUND_ON = true;
    public static final boolean DEFAULT_PUSH_TO_TALK = false;
    public static final float DEFAULT_MICROPHONE_SENSITIVITY = 0.5f;
    public static final float DEFAULT_TOTAL_VOLUME = 0.5f;
    public static final float DEFAULT_VOICE_VOLUME = 0.5f;
    public static final float DEFAULT_MUSIC_VOLUME = 0.5f;
    public static final float DEFAULT_SOUND_VOLUME = 0.5f;

    private final Preferences preferences;

    /**
     * Erzeugt eine neue Instanz der ChatiPreferences.
     */
    public ChatiPreferences() {
        this.preferences = Gdx.app.getPreferences("/chati/preferences");
        addKeyBindings();
    }

    /**
     * Setzt die aktuell ausgewählte Sprache
     * @param language Ausgewählte Sprache.
     */
    public void setLanguage(String language) {
        preferences.putString("language", language);
        preferences.flush();
    }

    /**
     * Setzt die Einstellung, ob sich der Avatar des angemeldeten Benutzers dauerhaft schnell fortbewegen soll.
     * @param alwaysSprinting true, wenn sich der Avatar dauerhaft schnell fortbewegen soll, sonst false.
     */
    public void setAlwaysSprinting(boolean alwaysSprinting) {
        preferences.putBoolean("always_sprinting", alwaysSprinting);
        preferences.flush();
    }

    /**
     * Setzt die Einstellung, ob innerhalb der Welt dauerhaft die Namen über den Avataren aller Benutzer angezeigt
     * werden sollen.
     * @param showNamesInWorld true, wenn die Namen über allen Avataren angezeigt werden sollen, sonst false.
     */
    public void setShowNamesInWorld(boolean showNamesInWorld) {
        preferences.putBoolean("show_names", showNamesInWorld);
        preferences.flush();
    }

    /**
     * Setzt die Einstellung, ob Töne abgespielt werden sollen.
     * @param soundOn true, wenn Töne abgespielt werden sollen, sonst false.
     */
    public void setSoundOn(boolean soundOn) {
        preferences.putBoolean("sound_on", soundOn);
        preferences.flush();
    }

    /**
     * Setzt die Einstellung, ob das Mikrofon aufzeichnen soll.
     * @param microphoneOn true, wenn das Mikrofon aufzeichnen soll, sonst false.
     */
    public void setMicrophoneOn(boolean microphoneOn) {
        preferences.putBoolean("microphone_on", microphoneOn);
        preferences.flush();
    }

    /**
     * Setzt die Einstellung, ob zum Aufzeichnen mit dem Mikrofon das Drücken einer festgelegten Taste notwendig ist.
     * @param pushToTalk true, wenn das Drücken einer Taste notwendig ist, sonst false.
     */
    public void setPushToTalk(boolean pushToTalk) {
        preferences.putBoolean("push_to_talk", pushToTalk);
        preferences.flush();
    }

    /**
     * Setzt die Mikrofonempfindlichkeit. Durch einen höheren Wert werden leisere Geräusche aufgezeichnet.
     * @param microphoneSensitivity Die Mikrofonempfindlichkeit zwischen 0 und 1.
     */
    public void setMicrophoneSensitivity(float microphoneSensitivity) {
        if (microphoneSensitivity < 0 || microphoneSensitivity > 1) {
            throw new IllegalArgumentException("Expected value between 0 and 1.");
        }
        preferences.putFloat("microphone_sensitivity", microphoneSensitivity);
        preferences.flush();
    }

    /**
     * Setzt die Gesamtlautstärke.
     * @param totalVolume Die Gesamtlautstärke zwischen 0 und 1.
     */
    public void setTotalVolume(float totalVolume) {
        if (totalVolume < 0 || totalVolume > 1) {
            throw new IllegalArgumentException("Expected value between 0 and 1.");
        }
        preferences.putFloat("total_volume", totalVolume);
        preferences.flush();
    }

    /**
     * Setzt die Lautstärke des Sprachchats.
     * @param voiceVolume Die Lautstärke des Sprachchats zwischen 0 und 1.
     */
    public void setVoiceVolume(float voiceVolume) {
        if (voiceVolume < 0 || voiceVolume > 1) {
            throw new IllegalArgumentException("Expected value between 0 and 1.");
        }
        preferences.putFloat("voice_volume", voiceVolume);
        preferences.flush();
    }

    /**
     * Setzt die Lautstärke der Musik.
     * @param musicVolume Die Lautstärke der Musik zwischen 0 und 1.
     */
    public void setMusicVolume(float musicVolume) {
        if (musicVolume < 0 || musicVolume > 1) {
            throw new IllegalArgumentException("Expected value between 0 and 1.");
        }
        preferences.putFloat("music_volume", musicVolume);
        preferences.flush();
    }

    /**
     * Setzt die Lautstärke von Hintergrundgeräuschen.
     * @param soundVolume Die Lautstärke von Hintergrundgeräuschen zwischen 0 und 1.
     */
    public void setSoundVolume(float soundVolume) {
        if (soundVolume < 0 || soundVolume > 1) {
            throw new IllegalArgumentException("Expected value between 0 and 1.");
        }
        preferences.putFloat("sound_volume", soundVolume);
        preferences.flush();
    }

    /**
     * Gibt die momentan ausgewählte Sprache zurück.
     * @return Ausgewählte Sprache.
     */
    public String getLanguage() {
        return preferences.getString("language", DEFAULT_LANGUAGE);
    }

    /**
     * Gibt zurück, ob sich der Avatar momentan dauerhaft schnell fortbewegt.
     * @return true, falls sich der Avatar dauerhaft schnell fortbewegt, sonst false.
     */
    public boolean isAlwaysSprinting() {
        return preferences.getBoolean("always_sprinting", DEFAULT_ALWAYS_SPRINTING);
    }

    /**
     * Gibt zurück, ob innerhalb der Welt momentan dauerhaft die Namen aller Benutzer über den Avataren angezeigt werden.
     * @return true, wenn momentan die Namen dauerhaft angezeigt werden, sonst false.
     */
    public boolean getShowNamesInWorld() {
        return preferences.getBoolean("show_names", DEFAULT_SHOW_NAMES_IN_WORLD);
    }

    /**
     * Gibt zurück, ob der Ton eingeschaltet ist.
     * @return true, wenn der Ton eingeschaltet ist, sonst false.
     */
    public boolean isSoundOn() {
        return preferences.getBoolean("sound_on", DEFAULT_SOUND_ON);
    }

    /**
     * Gibt zurück, ob das Mikrofon eingeschaltet ist.
     * @return true, wenn das Mikrofon eingeschaltet ist, sonst false.
     */
    public boolean isMicrophoneOn() {
        return preferences.getBoolean("microphone_on", DEFAULT_MICROPHONE_ON);
    }

    /**
     * Gibt zurück, ob zum Aufzeichnen mit dem Mikrofon momentan das Drücken einer Taste notwendig ist.
     * @return true, wenn das Drücken, einer Taste zum Aufzeichnen notwendig ist, sonst false.
     */
    public boolean getPushToTalk() {
        return preferences.getBoolean("push_to_talk", DEFAULT_PUSH_TO_TALK);
    }

    /**
     * Gibt die aktuelle Mikrofonempfindlichkeit zurück.
     * @return Aktuelle Mikrofonempfindlichkeit.
     */
    public float getMicrophoneSensitivity() {
        return preferences.getFloat("microphone_sensitivity", DEFAULT_MICROPHONE_SENSITIVITY);
    }

    /**
     * Gibt die aktuelle Gesamtlautstärke zurück.
     * @return Aktuelle Gesamtlautstärke.
     */
    public float getTotalVolume() {
        return preferences.getFloat("total_volume", DEFAULT_TOTAL_VOLUME);
    }

    /**
     * Gibt die aktuelle Lautstärke des Sprachchats zurück.
     * @return Aktuelle Lautstärke des Sprachchats.
     */
    public float getVoiceVolume() {
        return preferences.getFloat("voice_volume", DEFAULT_VOICE_VOLUME);
    }

    /**
     * Gibt die aktuelle Lautstärke der Musik zurück.
     * @return Aktuelle Lautstärke der Musik.
     */
    public float getMusicVolume() {
        return preferences.getFloat("music_volume", DEFAULT_MUSIC_VOLUME);
    }

    /**
     * Gibt die aktuelle Lautstärke der Hintergrundgeräusche zurück.
     * @return Aktuelle Lautstärke der Hintergrundgeräusche.
     */
    public float getSoundVolume() {
        return preferences.getFloat("sound_volume", DEFAULT_SOUND_VOLUME);
    }

    /**
     * Initialisiert die Tastenbelegung.
     */
    public void addKeyBindings() {
        /*  Anmerkung: Die Tastenbelegung ist hier festgelegt, kann aber in den Präferenzen gespeichert
            und abgerufen werden. Somit besteht prinzipiell die Möglichkeit, die Tastenbelegung zu ändern. */

        KeyCommand.MOVE_UP.addKeyBindings(List.of(Input.Keys.UP, Input.Keys.W));
        KeyCommand.MOVE_LEFT.addKeyBindings(List.of(Input.Keys.LEFT, Input.Keys.A));
        KeyCommand.MOVE_DOWN.addKeyBindings(List.of(Input.Keys.DOWN, Input.Keys.S));
        KeyCommand.MOVE_RIGHT.addKeyBindings(List.of(Input.Keys.RIGHT, Input.Keys.D));
        KeyCommand.SPRINT.addKeyBindings(List.of(Input.Keys.SHIFT_LEFT, Input.Keys.SHIFT_RIGHT));
        KeyCommand.SHOW_NAMES.addKeyBindings(List.of(Input.Keys.TAB));
        KeyCommand.INTERACT.addKeyBindings(List.of(Input.Keys.E));
        KeyCommand.OPEN_CHAT.addKeyBindings(List.of(Input.Keys.SPACE));
        KeyCommand.SEND_CHAT_MESSAGE.addKeyBindings(List.of(Input.Keys.ENTER));
        KeyCommand.PUSH_TO_TALK.addKeyBindings(List.of(Input.Keys.P));
        KeyCommand.OPEN_USER_MENU.addKeyBindings(List.of(Input.Keys.NUM_1, Input.Keys.NUMPAD_1));
        KeyCommand.OPEN_NOTIFICATION_MENU.addKeyBindings(List.of(Input.Keys.NUM_2, Input.Keys.NUMPAD_2));
        KeyCommand.OPEN_SETTINGS_MENU.addKeyBindings(List.of(Input.Keys.NUM_3, Input.Keys.NUMPAD_3));
        KeyCommand.OPEN_COMMUNICATION_MENU.addKeyBindings(List.of(Input.Keys.NUM_4, Input.Keys.NUMPAD_4));
        KeyCommand.TOGGLE_MICROPHONE.addKeyBindings(List.of(Input.Keys.NUM_5, Input.Keys.NUMPAD_5));
        KeyCommand.TOGGLE_SOUND.addKeyBindings(List.of(Input.Keys.NUM_6, Input.Keys.NUMPAD_6));
        KeyCommand.CLOSE.addKeyBindings(List.of(Input.Keys.ESCAPE));
    }
}
