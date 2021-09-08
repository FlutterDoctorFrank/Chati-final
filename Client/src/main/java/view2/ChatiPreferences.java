package view2;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;

import java.util.List;

public class ChatiPreferences {

    public static final String DEFAULT_LANGUAGE = "german";
    public static final boolean DEFAULT_SHOW_NAMES_IN_WORLD = true;
    public static final boolean DEFAULT_MICROPHONE_ON = true;
    public static final boolean DEFAULT_SOUND_ON = true;
    public static final boolean DEFAULT_PUSH_TO_TALK = false;
    public static final float DEFAULT_TOTAL_VOLUME = 0.5f;
    public static final float DEFAULT_VOICE_VOLUME = 0.5f;
    public static final float DEFAULT_MUSIC_VOLUME = 0.5f;
    public static final float DEFAULT_SOUND_VOLUME = 0.5f;

    private final Preferences preferences;

    public ChatiPreferences() {
        this.preferences = Gdx.app.getPreferences("/chati/preferences");
        addKeyBindings();
    }

    public void setLanguage(String language) {
        preferences.putString("language", language);
        preferences.flush();
    }

    public void setShowNamesInWorld(boolean showNamesInWorld) {
        preferences.putBoolean("show_names", showNamesInWorld);
        preferences.flush();
    }

    public void setSoundOn(boolean soundOn) {
        preferences.putBoolean("sound_on", soundOn);
        preferences.flush();
    }

    public void setMicrophoneOn(boolean microphoneOn) {
        preferences.putBoolean("microphone_on", microphoneOn);
        preferences.flush();
    }

    public void setPushToTalk(boolean pushToTalk) {
        preferences.putBoolean("push_to_talk", pushToTalk);
        preferences.flush();
    }

    public void setTotalVolume(float totalVolume) {
        preferences.putFloat("total_volume", totalVolume);
        preferences.flush();
    }

    public void setVoiceVolume(float voiceVolume) {
        preferences.putFloat("voice_volume", voiceVolume);
        preferences.flush();
    }

    public void setMusicVolume(float musicVolume) {
        preferences.putFloat("music_volume", musicVolume);
        preferences.flush();
    }

    public void setSoundVolume(float soundVolume) {
        preferences.putFloat("sound_volume", soundVolume);
        preferences.flush();
    }

    public String getLanguage() {
        return preferences.getString("language", DEFAULT_LANGUAGE);
    }

    public boolean getShowNamesInWorld() {
        return preferences.getBoolean("show_names", DEFAULT_SHOW_NAMES_IN_WORLD);
    }

    public boolean isSoundOn() {
        return preferences.getBoolean("sound_on", DEFAULT_SOUND_ON);
    }

    public boolean isMicrophoneOn() {
        return preferences.getBoolean("microphone_on", DEFAULT_MICROPHONE_ON);
    }

    public boolean getPushToTalk() {
        return preferences.getBoolean("push_to_talk", DEFAULT_PUSH_TO_TALK);
    }

    public float getTotalVolume() {
        return preferences.getFloat("total_volume", DEFAULT_TOTAL_VOLUME);
    }

    public float getVoiceVolume() {
        return preferences.getFloat("voice_volume", DEFAULT_VOICE_VOLUME);
    }

    public float getMusicVolume() {
        return preferences.getFloat("music_volume", DEFAULT_MUSIC_VOLUME);
    }

    public float getSoundVolume() {
        return preferences.getFloat("sound_volume", DEFAULT_SOUND_VOLUME);
    }

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
