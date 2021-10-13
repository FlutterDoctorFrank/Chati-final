package view.multimedia;

import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.GdxRuntimeException;
import utils.AudioUtils;
import model.exception.UserNotFoundException;
import model.user.IInternUserView;
import model.user.IUserView;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import view.Chati;
import view.multimedia.audio.AudioConsumer;
import view.multimedia.audio.VoiceRecorder;
import view.multimedia.video.CameraRecorder;
import view.multimedia.video.ScreenRecorder;
import view.multimedia.video.VideoReceiver;

import java.awt.image.BufferedImage;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.logging.Level;

/**
 * Eine Klasse, welche die in der Anwendung verwendeten Medien verwaltet.
 */
public class MultimediaManager implements Disposable {

    public static final int AUDIO_BLOCK_SIZE = AudioUtils.FRAME_SIZE / AudioUtils.SAMPLE_SIZE_IN_BYTES;

    private VoiceRecorder voiceRecorder;
    private AudioConsumer audioConsumer;
    private final CameraRecorder cameraRecorder;
    private final ScreenRecorder screenRecorder;
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

        this.cameraRecorder = new CameraRecorder();
        this.screenRecorder = new ScreenRecorder();
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

        if (Chati.CHATI.isUserInfoChanged() || Chati.CHATI.isWorldChanged()) {
            if (internUser != null && internUser.isInCurrentWorld()) {
                if (!cameraRecorder.isRunning()) {
                    cameraRecorder.start();
                }
                if (!screenRecorder.isRunning()) {
                    screenRecorder.start();
                }
            } else if (internUser == null || !internUser.isInCurrentWorld()) {
                if (cameraRecorder.isRunning()) {
                    cameraRecorder.stop();
                }
                if (screenRecorder.isRunning()) {
                    screenRecorder.stop();
                }
            }
        }

        if (cameraRecorder.isRunning()) {
            if (Chati.CHATI.getHeadUpDisplay().getVideoChatWindow().isCameraOn() && internUser != null
                    && internUser.canShow() && !cameraRecorder.isRecording()) {
                cameraRecorder.startRecording();
            } else if (cameraRecorder.isRecording()) {
                cameraRecorder.stopRecording();
            }
        }

        if (screenRecorder.isRunning()) {
            if (Chati.CHATI.getHeadUpDisplay().getVideoChatWindow().shareScreen() && internUser != null
                    && internUser.canShare() && !screenRecorder.isRecording()) {
                screenRecorder.startRecording();
            } else if (screenRecorder.isRecording()) {
                screenRecorder.stopRecording();
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
    public void receiveVoiceData(@NotNull final UUID senderId, @NotNull final LocalDateTime timestamp, final byte[] voiceData)
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
    public void receiveMusicData(@NotNull final LocalDateTime timestamp, final byte[] musicData,
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
     * Hinterlegt ein erhaltenes VideoFrame von einem externen Benutzer.
     * @param userId ID des Benutzers, dessen Frame erhalten wurde.
     * @param timestamp Zeitstempel des Frames.
     * @param screen true, wenn dieser Frame Teil einer Bildschirmaufnahme ist, sonst false.
     * @param frameData Daten des Frames.
     * @throws UserNotFoundException falls kein Benutzer mit der ID gefunden wurde.
     */
    public void receiveVideoFrame(@NotNull final UUID userId, @NotNull final LocalDateTime timestamp,
                                  final boolean screen, final byte[] frameData) throws UserNotFoundException {
        videoReceiver.receiveVideoFrame(userId, timestamp, screen, frameData);
    }

    /**
     * Hinterlegt ein erhaltenes VideoFrame vom intern angemeldeten Benutzer.
     * @param frame Erhaltenes Videoframe.
     * @param screen true, wenn dieser Frame Teil einer Bildschirmaufnahme ist, sonst false.
     */
    public void receiveVideoFrame(@NotNull final BufferedImage frame, final boolean screen) {
        videoReceiver.receiveVideoFrame(frame, screen);
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

    /**
     * Gibt zurück, ob die Anwendung gerade eine Kamera verwendet.
     * @return true, wenn eine Kamera verwendet wird, sonst false.
     */
    public boolean hasCamera() {
        return cameraRecorder.hasCamera();
    }

    /**
     * Gibt zurück, ob Frames von Kameraaufnahmen vorhanden sind.
     * @return true, wenn Frames vorhanden sind, sonst false.
     */
    public boolean hasVideoFrame() {
        return videoReceiver.hasReadyFrame();
    }

    /**
     * Gibt den nächsten anzuzeigenden Frame einer Kameraaufnahme zurück.
     * @return Nächster anzuzeigender Frame.
     */
    public @Nullable VideoReceiver.VideoFrame getNextVideoFrame() {
        return videoReceiver.getNextFrame();
    }
}
