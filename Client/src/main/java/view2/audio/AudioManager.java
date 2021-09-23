package view2.audio;

import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.GdxRuntimeException;
import model.exception.UserNotFoundException;
import model.user.IInternUserView;
import model.user.IUserView;
import org.jetbrains.annotations.NotNull;
import view2.Chati;
import view2.KeyCommand;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Eine Klasse, welche den VoiceChat sowie das Abspielen von Soundeffekten koordiniert.
 */
public class AudioManager implements Disposable {

    public static final int SAMPLING_RATE = 44100;
    public static final int SEND_RATE = 30;
    public static final int BLOCK_SIZE = SAMPLING_RATE / SEND_RATE;
    public static final boolean MONO = true;

    private VoiceRecorder voiceRecorder;
    private AudioConsumer audioConsumer;

    /**
     * Erzeugt eine neue Instanz des AudioManager.
     */
    public AudioManager() {
        try {
            this.voiceRecorder = new VoiceRecorder();
            this.audioConsumer = new AudioConsumer();
            setMicrophoneSensitivity();
            setVolume();
        } catch (GdxRuntimeException e) {
            e.printStackTrace(); // Line is not available
        }
        //this.chatMessageSound = Gdx.audio.newSound(Gdx.files.internal(CHAT_MESSAGE_SOUND_PATH));
        //this.notificationSound = Gdx.audio.newSound(Gdx.files.internal(NOTIFICATION_SOUND_PATH));
        //this.roomEnterSound = Gdx.audio.newSound(Gdx.files.internal(ROOM_ENTER_SOUND_PATH));
    }

    /**
     * Wird periodisch aufgerufen, um den das Aufnehmen von Sprachdaten und Abspielen von erhaltenen Audiopaketen nach
     * Bedarf ein- oder auszuschalten.
     */
    public void update() {
        IInternUserView internUser = Chati.CHATI.getInternUser();

        if (voiceRecorder != null && audioConsumer != null) {
            if (Chati.CHATI.isUserInfoChanged() || Chati.CHATI.isWorldChanged()) {
                if ((!voiceRecorder.isRunning() || !audioConsumer.isRunning())
                        && internUser != null && internUser.isInCurrentWorld()) {
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
                        || Chati.CHATI.getWorldScreen().getWorldInputProcessor().isPushToTalkPressed())
                        && internUser != null && internUser.canTalk()) {
                    if (!voiceRecorder.isRecording()) {
                        voiceRecorder.startRecording();
                    }
                } else if (voiceRecorder.isRecording()) {
                    voiceRecorder.stopRecording();
                }
            }
        }
    }

    @Override
    public void dispose() {
        if (voiceRecorder != null) {
            voiceRecorder.dispose();
        }
        if (audioConsumer != null) {
            audioConsumer.dispose();
        }
    }

    /**
     * Setzt die in den Einstellungen hinterlegte Lautstärke.
     */
    public void setVolume() {
        float totalVolume = (float) (-Math.pow(Chati.CHATI.getPreferences().getTotalVolume() - 1, 2) + 1);
        float voiceVolume = (float) (-Math.pow(Chati.CHATI.getPreferences().getVoiceVolume() - 1, 2) + 1);
        float musicVolume = 0.1f * (float) (-Math.pow(Chati.CHATI.getPreferences().getMusicVolume() - 1, 2) + 1);
        float soundVolume = 0.1f * (float) (-Math.pow(Chati.CHATI.getPreferences().getSoundVolume() - 1, 2) + 1);

        audioConsumer.setTotalVolume(totalVolume);
        audioConsumer.setVoiceVolume(voiceVolume);
        audioConsumer.setMusicVolume(musicVolume);
    }

    /**
     * Setzt die in den Einstellungen hinterlegte Mikrofonempfindlichkeit.
     */
    public void setMicrophoneSensitivity() {
        voiceRecorder.setMicrophoneSensitivity(Chati.CHATI.getPreferences().getMicrophoneSensitivity());
    }

    /**
     * Veranlasst das Abspielen erhaltener Sprachdaten.
     * @param senderId ID des sendenden Benutzers.
     * @param timestamp Zeitstempel der Sprachdaten.
     * @param voiceData Abzuspielende Sprachdaten.
     * @throws UserNotFoundException falls kein Benutzer mit der ID gefunden wurde.
     */
    public void playVoiceData(@NotNull final UUID senderId, @NotNull final LocalDateTime timestamp, final byte[] voiceData)
            throws UserNotFoundException {
        if (audioConsumer != null && audioConsumer.isRunning()) {
            audioConsumer.receiveVoiceData(senderId, timestamp, voiceData);
        }
    }

    /**
     * Veranlasst das Abspielen erhaltener Musikdaten.
     * @param timestamp Zeitstempel der Sprachdaten.
     * @param musicData Abzuspielende Musikdaten.
     */
    public void playMusicData(@NotNull final LocalDateTime timestamp, final byte[] musicData) {
        if (audioConsumer != null && audioConsumer.isRunning()) {
            audioConsumer.receiveMusicStream(timestamp, musicData);
        }
    }

    /**
     * Gibt zurück, ob im gerade Sprachdaten von einem Benutzer abgespielt werden.
     * @param user Zu überprüfender Benutzer.
     * @return true, wenn Sprachdaten von diesem Benutzer abgespielt werden, sonst false.
     */
    public boolean isTalking(IUserView user) {
        return audioConsumer.isTalking(user);
    }

    /**
     * Gibt zurück, ob gerade Daten eines Musikstreams abgespielt werden.
     * @return true, wenn Musikdaten abgespielt werden, sonst false.
     */
    public boolean isPlayingMusic() {
        return audioConsumer.isPlayingMusic();
    }

    /**
     * Stoppt das Abspielen von Musik.
     */
    public void stopMusic() {
        audioConsumer.stopMusic();
    }

    /**
     * Kopiert Daten aus einem Short-Array in einen Byte-Array doppelter Größe.
     * @param shorts Short-Array der zu kopierenden Daten.
     * @param bigEndian Endianität der Daten.
     * @return Byte-Array mit den kopierten Daten.
     */
    public static byte[] toByte(final short[] shorts, final boolean bigEndian) {
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

    /**
     * Kopiert Daten aus einem Byte-Array in einen Short-Array halber Größe.
     * @param bytes Byte-Array der zu kopierenden Daten.
     * @param bigEndian Endianität der Daten.
     * @return Short-Array mit den kopierten Daten.
     */
    public static short[] toShort(final byte[] bytes, final boolean bigEndian) {
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
