package view2.audio;

import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.GdxRuntimeException;
import model.exception.UserNotFoundException;
import model.user.IInternUserView;
import org.jetbrains.annotations.NotNull;
import view2.Chati;
import view2.KeyCommand;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Eine Klasse, welche den VoiceChat sowie das Abspielen von Soundeffekten koordiniert.
 */
public class AudioManager implements Disposable {

    public static final int SAMPLE_RATE = 44100;
    public static final int SEND_RATE = 30;
    public static final int BLOCK_SIZE = SAMPLE_RATE / SEND_RATE;
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
                        || KeyCommand.PUSH_TO_TALK.isPressed()) && internUser != null && internUser.canTalk()) {
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
     * Gibt zurück, ob gerade Daten eines Musikstreams abgespielt werden.
     * @return true, wenn Musikdaten abgespielt werden, sonst false.
     */
    public boolean isPlayingMusic() {
        return audioConsumer.isPlayingMusic();
    }

    /**
     * Kopiert Daten aus einem Short-Array in einen Byte-Array doppelter Größe.
     * @param shorts Short-Array der zu kopierenden Daten.
     * @param bigEndian Endianität der Daten.
     * @return Byte-Array mit den kopierten Daten.
     */
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

    /**
     * Kopiert Daten aus einem Byte-Array in einen Short-Array halber Größe.
     * @param bytes Byte-Array der zu kopierenden Daten.
     * @param bigEndian Endianität der Daten.
     * @return Short-Array mit den kopierten Daten.
     */
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
