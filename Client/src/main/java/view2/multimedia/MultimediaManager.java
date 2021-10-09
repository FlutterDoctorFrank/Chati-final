package view2.multimedia;

import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.github.sarxos.webcam.WebcamException;
import com.github.sarxos.webcam.WebcamLockException;
import model.exception.UserNotFoundException;
import model.user.IInternUserView;
import model.user.IUserView;
import org.jetbrains.annotations.NotNull;
import view2.Chati;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.logging.Level;

/**
 * Eine Klasse, welche die in der Anwendung verwendeten Medien verwaltet.
 */
public class MultimediaManager implements Disposable {

    public static final int SAMPLING_RATE = 44100;
    public static final int SEND_RATE = 30;
    public static final int BLOCK_SIZE = SAMPLING_RATE / SEND_RATE;
    public static final boolean MONO = true;

    private VoiceRecorder voiceRecorder;
    private AudioConsumer audioConsumer;
    private VideoRecorder videoRecorder;
    private final VideoReceiver videoReceiver;

    /**
     * Erzeugt eine neue Instanz des MultimediaManager.
     */
    public MultimediaManager() {
        try {
            this.audioConsumer = new AudioConsumer();
            setVolume();

            try {
                this.voiceRecorder = new VoiceRecorder();
                setMicrophoneSensitivity();
            } catch (GdxRuntimeException e) {
               Chati.LOGGER.log(Level.WARNING, "Microphone not available", e);
               this.voiceRecorder = null;
            }
        } catch (GdxRuntimeException e) {
            Chati.LOGGER.log(Level.WARNING, "Speaker not available", e);
            this.audioConsumer = null;
            this.voiceRecorder = null;
        }

        try {
            this.videoRecorder = new VideoRecorder();
        } catch (WebcamException e) {
            Chati.LOGGER.log(Level.WARNING, "Webcam not available", e);
            this.videoRecorder = null;
        }

        this.videoReceiver = new VideoReceiver();
    }

    /**
     * Wird periodisch aufgerufen, um den das Aufnehmen von Sprachdaten und Abspielen von erhaltenen Audiopaketen nach
     * Bedarf ein- oder auszuschalten.
     */
    public void update() {
        IInternUserView internUser = Chati.CHATI.getInternUser();

        if (audioConsumer != null) {
            if (Chati.CHATI.isUserInfoChanged() || Chati.CHATI.isWorldChanged()) {
                if (internUser != null && internUser.isInCurrentWorld()) {
                    if (!audioConsumer.isRunning()) {
                        audioConsumer.start();
                    }
                    if (voiceRecorder != null && !voiceRecorder.isRunning()) {
                        voiceRecorder.start();
                    }
                } else if (internUser == null || !internUser.isInCurrentWorld()) {
                    if (audioConsumer.isRunning()) {
                        audioConsumer.stop();
                    }
                    if (voiceRecorder != null && voiceRecorder.isRunning()) {
                        voiceRecorder.stop();
                    }
                }
            }

            if (voiceRecorder != null && voiceRecorder.isRunning()) {
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

            if (Chati.CHATI.isMusicChanged() && (internUser == null || internUser.getMusic() == null)) {
                audioConsumer.stopMusic();
            }
        }

        if (videoRecorder != null) {
            if (Chati.CHATI.isUserInfoChanged() || Chati.CHATI.isWorldChanged()) {
                if (internUser != null && internUser.isInCurrentWorld()) {
                    if (!videoRecorder.isRunning()) {
                        try {
                            videoRecorder.start();
                        } catch (WebcamLockException e) {
                            Chati.LOGGER.log(Level.WARNING, "Webcam is already in use", e);
                        }
                    }
                } else if (internUser == null || !internUser.isInCurrentWorld()) {
                    if (videoRecorder.isRunning()) {
                        videoRecorder.stop();
                    }
                }
            }

            if (videoRecorder.isRunning()) {
                if (Chati.CHATI.getPreferences().isCameraOn() && internUser != null && internUser.canShow()
                        && !videoRecorder.isRecording()) {
                    videoRecorder.startRecording();
                } else if (videoRecorder.isRecording()) {
                    videoRecorder.stopRecording();
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
        if (audioConsumer != null) {
            audioConsumer.setTotalVolume(Chati.CHATI.getPreferences().getTotalVolume());
            audioConsumer.setVoiceVolume(Chati.CHATI.getPreferences().getVoiceVolume());
            audioConsumer.setMusicVolume(Chati.CHATI.getPreferences().getMusicVolume());
        }
    }

    /**
     * Setzt, ob Musik zu hören ist.
     * @param on true, wenn Musik zu hören ist, sonst false.
     */
    public void toggleMusic(boolean on) {
        if (on) {
            setVolume();
        } else if (audioConsumer != null) {
            audioConsumer.setMusicVolume(0);
        }
    }

    /**
     * Setzt die in den Einstellungen hinterlegte Mikrofonempfindlichkeit.
     */
    public void setMicrophoneSensitivity() {
        if (voiceRecorder != null) {
            voiceRecorder.setMicrophoneSensitivity(Chati.CHATI.getPreferences().getMicrophoneSensitivity());
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
     * @param position Aktuelle Position im Musikstück.
     * @param seconds Aktuelle Sekunde im Musikstück.
     */
    public void playMusicData(@NotNull final LocalDateTime timestamp, final byte[] musicData,
                              final float position, final int seconds) {
        if (audioConsumer != null && audioConsumer.isRunning()) {
            audioConsumer.receiveMusicStream(timestamp, musicData, position, seconds);
        }
    }

    /**
     * Spielt einen Ton ab.
     * @param soundName Name des abzuspielenden Tons.
     */
    public void playSound(@NotNull final String soundName) {
        if (audioConsumer != null) {
            audioConsumer.playSound(Chati.CHATI.getSound(soundName), Chati.CHATI.getPreferences().getTotalVolume(),
                    Chati.CHATI.getPreferences().getSoundVolume());
        }
    }

    /**
     * Hinterlegt ein erhaltenes VideoFrame.
     * @param userId ID des Benutzers, dessen Frame erhalten wurde.
     * @param timestamp Zeitstempel des Frames.
     * @param frameData Daten des Frames.
     * @param number Nummer des Frames.
     * @throws UserNotFoundException falls kein Benutzer mit der ID gefunden wurde.
     */
    public void receiveVideoFrame(@NotNull final UUID userId, @NotNull final LocalDateTime timestamp,
                               final byte[] frameData, final int number) throws UserNotFoundException {
        videoReceiver.receiveVideoFrame(userId, timestamp, frameData, number);
    }

    /**
     * Gibt zurück, ob ein Benutzer gerade spricht.
     * @param user Zu überprüfender Benutzer.
     * @return true, wenn der Benutzer gerade spricht, sonst false.
     */
    public boolean isTalking(@NotNull final IUserView user) {
        IInternUserView internUser = Chati.CHATI.getInternUser();
        if (internUser != null && internUser.equals(user)) {
            return voiceRecorder != null && voiceRecorder.isSending();
        } else {
            return audioConsumer != null && audioConsumer.isTalking(user);
        }
    }

    /**
     * Gibt zurück, ob gerade Daten eines Musikstreams abgespielt werden.
     * @return true, wenn Musikdaten abgespielt werden, sonst false.
     */
    public boolean isPlayingMusic() {
        return audioConsumer != null && audioConsumer.isPlayingMusic();
    }

    /**
     * Gibt zurück, ob Frames vorhanden sind.
     * @return true, wenn Frames vorhanden sind, sonst false.
     */
    public boolean hasFrame() {
        return videoReceiver.hasFrame();
    }

    /**
     * Gibt den nächsten anzuzeigenden Frame zurück.
     * @return Nächster anzuzeigender Frame.
     */
    public VideoFrame getNextFrame() {
        return videoReceiver.getNextFrame();
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

    /**
     * Gibt die aktuelle Position im laufenden Musikstück zurück.
     * @return Aktuelle Position im Musikstück.
     */
    public float getCurrentPosition() {
        return audioConsumer != null ? audioConsumer.getCurrentPosition() : 0;
    }

    /**
     * Gibt die aktuelle Sekunde im laufenden Musikstück zurück.
     * @return Aktuelle Sekunde im Musikstück.
     */
    public int getCurrentSeconds() {
        return audioConsumer != null ? audioConsumer.getCurrentSeconds() : 0;
    }
}
