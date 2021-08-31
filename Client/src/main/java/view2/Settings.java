package view2;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public class Settings {

    private static final String PREFERENCES_PATH = "/chati/preferences";

    public static final String DEFAULT_LANGUAGE = "German";
    public static final boolean DEFAULT_SHOW_NAMES_IN_WORLD = true;
    public static final boolean DEFAULT_MICROPHONE_ON = true;
    public static final boolean DEFAULT_PUSH_TO_TALK = false;
    public static final float DEFAULT_TOTAL_VOLUME = 0.5f;
    public static final float DEFAULT_VOICE_VOLUME = 0.5f;
    public static final float DEFAULT_MUSIC_VOLUME = 0.5f;
    public static final float DEFAULT_SOUND_VOLUME = 0.5f;

    private static String LANGUAGE;
    private static boolean SHOW_NAMES_IN_WORLD;
    private static boolean MICROPHONE_ON;
    private static boolean PUSH_TO_TALK;
    private static float TOTAL_VOLUME;
    private static float VOICE_VOLUME;
    private static float MUSIC_VOLUME;
    private static float SOUND_VOLUME;

    private static Preferences PREFERENCES;

    private Settings() {
    }

    public static void initialize() {
        PREFERENCES = Gdx.app.getPreferences(PREFERENCES_PATH);

        LANGUAGE = PREFERENCES.getString("language", DEFAULT_LANGUAGE);
        SHOW_NAMES_IN_WORLD = PREFERENCES.getBoolean("show_names", DEFAULT_SHOW_NAMES_IN_WORLD);
        MICROPHONE_ON = PREFERENCES.getBoolean("microphone_on", DEFAULT_MICROPHONE_ON);
        PUSH_TO_TALK = PREFERENCES.getBoolean("push_to_talk", DEFAULT_PUSH_TO_TALK);
        TOTAL_VOLUME = PREFERENCES.getFloat("total_volume", DEFAULT_TOTAL_VOLUME);
        VOICE_VOLUME = PREFERENCES.getFloat("voice_volume", DEFAULT_VOICE_VOLUME);
        MUSIC_VOLUME = PREFERENCES.getFloat("music_volume", DEFAULT_MUSIC_VOLUME);
        SOUND_VOLUME = PREFERENCES.getFloat("sound_volume", DEFAULT_SOUND_VOLUME);
    }

    public static void setLanguage(String language) {
        LANGUAGE = language;
        PREFERENCES.putString("language", LANGUAGE);
        PREFERENCES.flush();
    }

    public static void setShowNamesInWorld(boolean showNamesInWorld) {
        SHOW_NAMES_IN_WORLD = showNamesInWorld;
        PREFERENCES.putBoolean("show_names", SHOW_NAMES_IN_WORLD);
        PREFERENCES.flush();
    }

    public static void setMicrophoneOn(boolean microphoneOn) {
        MICROPHONE_ON = microphoneOn;
        PREFERENCES.putBoolean("microphone_on", MICROPHONE_ON);
        PREFERENCES.flush();
    }

    public static void setPushToTalk(boolean pushToTalk) {
        PUSH_TO_TALK = pushToTalk;
        PREFERENCES.putBoolean("push_to_talk", pushToTalk);
        PREFERENCES.flush();
    }

    public static void setTotalVolume(float totalVolume) {
        TOTAL_VOLUME = totalVolume;
        PREFERENCES.putFloat("total_volume", TOTAL_VOLUME);
        PREFERENCES.flush();
        Chati.CHATI.getAudioManager().setMusicVolume();
        Chati.CHATI.getAudioManager().setVoiceVolume();
    }

    public static void setVoiceVolume(float voiceVolume) {
        VOICE_VOLUME = voiceVolume;
        PREFERENCES.putFloat("voice_volume", VOICE_VOLUME);
        PREFERENCES.flush();
        Chati.CHATI.getAudioManager().setVoiceVolume();
    }

    public static void setMusicVolume(float musicVolume) {
        MUSIC_VOLUME = musicVolume;
        PREFERENCES.putFloat("music_volume", MUSIC_VOLUME);
        PREFERENCES.flush();
        Chati.CHATI.getAudioManager().setMusicVolume();
    }

    public static void setSoundVolume(float soundVolume) {
        SOUND_VOLUME = soundVolume;
        PREFERENCES.putFloat("sound_volume", SOUND_VOLUME);
        PREFERENCES.flush();
    }

    public static String getLanguage() {
        return LANGUAGE;
    }

    public static boolean getShowNamesInWorld() {
        return SHOW_NAMES_IN_WORLD;
    }

    public static boolean isMicrophoneOn() {
        return MICROPHONE_ON;
    }

    public static boolean getPushToTalk() {
        return PUSH_TO_TALK;
    }

    public static float getTotalVolume() {
        return TOTAL_VOLUME;
    }

    public static float getVoiceVolume() {
        return VOICE_VOLUME;
    }

    public static float getMusicVolume() {
        return MUSIC_VOLUME;
    }

    public static float getSoundVolume() {
        return SOUND_VOLUME;
    }
}
