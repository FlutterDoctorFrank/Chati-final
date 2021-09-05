package view2.audio;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.GdxRuntimeException;
import model.context.spatial.ContextMusic;
import model.exception.UserNotFoundException;
import model.user.IInternUserView;
import view2.Chati;
import view2.component.KeyAction;
import java.time.LocalDateTime;
import java.util.UUID;

public class AudioManager {

    public static final int SAMPLE_RATE = 44100;
    public static final int SEND_RATE = 30;
    public static final int PACKET_SIZE = SAMPLE_RATE / SEND_RATE;
    public static final boolean MONO = true;

    private static final String CHAT_MESSAGE_SOUND_PATH = ""; // TODO
    private static final String NOTIFICATION_SOUND_PATH = ""; // TODO
    private static final String ROOM_ENTER_SOUND_PATH = ""; // TODO

    private VoiceRecorder voiceRecorder;
    private AudioConsumer audioConsumer;
    private Sound chatMessageSound;
    private Sound notificationSound;
    private Sound roomEnterSound;
    private Music currentMusic;

    public AudioManager() {
        try {
            this.voiceRecorder = new VoiceRecorder();
            this.audioConsumer = new AudioConsumer();
        } catch (GdxRuntimeException e) {
            e.printStackTrace(); // Line is not available
        }
        //this.chatMessageSound = Gdx.audio.newSound(Gdx.files.internal(CHAT_MESSAGE_SOUND_PATH));
        //this.notificationSound = Gdx.audio.newSound(Gdx.files.internal(NOTIFICATION_SOUND_PATH));
        //this.roomEnterSound = Gdx.audio.newSound(Gdx.files.internal(ROOM_ENTER_SOUND_PATH));
        this.currentMusic = null;
    }

    public void update() {
        IInternUserView internUser = Chati.CHATI.getUserManager().getInternUserView();

        if (voiceRecorder != null && audioConsumer != null) {
            if (Chati.CHATI.isUserInfoChanged() || Chati.CHATI.isWorldChanged()) {
                if ((!voiceRecorder.isRunning() || !audioConsumer.isRunning())
                        && internUser != null && internUser.isInCurrentWorld()) {
                    setVoiceVolume();
                    voiceRecorder.start();
                    audioConsumer.start();
                } else if ((voiceRecorder.isRunning() || audioConsumer.isRunning())
                        && (internUser == null || !internUser.isInCurrentWorld())) {
                    voiceRecorder.stop();
                    audioConsumer.stop();
                }
            }

            if (voiceRecorder.isRunning()) {
                if (Chati.CHATI.getPreferences().isMicrophoneOn() && (!Chati.CHATI.getPreferences().getPushToTalk()
                        || KeyAction.PUSH_TO_TALK.isPressed()) && internUser != null && internUser.canTalk()) {
                    if (!voiceRecorder.isRecording()) {
                        voiceRecorder.startRecording();
                    }
                } else if (voiceRecorder.isRecording()) {
                    voiceRecorder.stopRecording();
                }
            }
        }

        if (Chati.CHATI.isMusicChanged() && internUser != null) {
            playMusic(internUser.getMusic());
        }
    }

    public void setVoiceVolume() {
        // TODO
        /*
        if (audioConsumer != null) {
            float volume = (float) Math.sqrt(Settings.getTotalVolume() * Settings.getVoiceVolume());
            audioConsumer.setVolume(volume);
        }
         */
    }

    public void setMusicVolume() {
        // TODO
        /*
        if (currentMusic != null) {
            float volume = (float) Math.sqrt(Settings.getTotalVolume() * Settings.getMusicVolume());
            currentMusic.setVolume(volume);
        }
         */
    }

    public void playAudioData(UUID userId, LocalDateTime timestamp, byte[] audioData) throws UserNotFoundException {
        if (audioConsumer != null && audioConsumer.isRunning()) {
            if (userId == null) {
                audioConsumer.receiveMusicStream(timestamp, audioData);
            } else {
                audioConsumer.receiveVoiceData(userId, timestamp, audioData);
            }
        }
    }

    public void playChatMessageSound() {
        // TODO
        /*
        if (chatMessageSound != null) {
            float volume = (float) Math.sqrt(Settings.getTotalVolume() * Settings.getSoundVolume());
            chatMessageSound.play(volume);
        }
         */
    }

    public void playNotificationSound() {
        // TODO
        /*
        if (notificationSound != null) {
            float volume = (float) Math.sqrt(Settings.getTotalVolume() * Settings.getSoundVolume());
            notificationSound.play(volume);
        }
         */
    }

    public void playRoomEnterSound() {
        // TODO
        /*
        if (roomEnterSound != null) {
            float volume = (float) Math.sqrt(Settings.getTotalVolume() * Settings.getSoundVolume());
            roomEnterSound.play(volume);
        }
         */
    }

    public boolean isPlayingMusic() {
        return audioConsumer.isPlayingMusic();
    }

    private void playMusic(ContextMusic contextMusic) {
        /*
        if (contextMusic != null) {
            currentMusic = Gdx.audio.newMusic(Gdx.files.internal(contextMusic.getPath()));
            currentMusic.setLooping(false);
            currentMusic.setOnCompletionListener(music ->
                    playMusic(ContextMusic.values()[((contextMusic.ordinal() + 1) % ContextMusic.values().length)]));
            setMusicVolume();
            currentMusic.play();
        } else {
            if (currentMusic != null) {
                currentMusic.dispose();
                currentMusic = null;
            }
        }
         */
    }

    public static byte[] toByte(short[] shorts, boolean bigEndian) {
        byte[] bytes = new byte[shorts.length * 2];
        if (bigEndian) {
            for (int i = 0; i < shorts.length; i++) {
                bytes[2 * i] = (byte) (shorts[i] >> 8);
                bytes[2 * i + 1] = (byte) shorts[i];
            }
        } else {
            for (int i = 0; i < shorts.length; i++) {
                bytes[2 * i] = (byte) shorts[i];
                bytes[2 * i + 1] = (byte) (shorts[i] >> 8);
            }
        }
        return bytes;
    }

    public static short[] toShort(byte[] bytes, boolean bigEndian) {
        short[] shorts = new short[bytes.length / 2];
        if (bigEndian) {
            for (int i = 0; i < shorts.length; i++) {
                shorts[i] = (short) ((bytes[2 * i] << 8) | (bytes[2 * i + 1] & 0xff));
            }
        } else {
            for (int i = 0; i < shorts.length; i++) {
                shorts[i] = (short) ((bytes[2 * i] & 0xff) | (bytes[2 * i + 1] << 8));
            }
        }
        return shorts;
    }
}
