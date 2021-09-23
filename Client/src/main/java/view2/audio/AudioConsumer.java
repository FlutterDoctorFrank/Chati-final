package view2.audio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.AudioDevice;
import com.badlogic.gdx.utils.Disposable;
import model.exception.UserNotFoundException;
import model.user.IUserView;
import org.jetbrains.annotations.NotNull;
import view2.Chati;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Eine Klasse, durch welche das Mischen und Abspielen empfangener Audiodaten realisiert wird.
 */
public class AudioConsumer implements Disposable {

    private final AudioDevice player;
    private final Map<IUserView, VoiceChatUser> voiceDataBuffer;
    private final MusicStream musicStream;

    private float musicVolume;
    private float voiceVolume;
    private boolean isRunning;
    private volatile boolean isPlayingMusic;

    /**
     * Erzeugt eine neue Instanz des AudioConsumer.
     */
    public AudioConsumer() {
        this.player = Gdx.audio.newAudioDevice(AudioManager.SAMPLING_RATE, AudioManager.MONO);
        this.voiceDataBuffer = new ConcurrentHashMap<>();
        this.musicStream = new MusicStream();
    }

    /**
     * Startet einen Thread zum Mischen und Abspielen empfangener Audiodaten, sofern nicht bereits einer läuft.
     */
    public void start() {
        if (isRunning) {
            return;
        }
        isRunning = true;

        Thread mixAndPlaybackThread = new Thread(() -> {
            short[] mixedData = new short[AudioManager.BLOCK_SIZE];

            outer:
            while (isRunning) {
                synchronized (this) {
                    // Warte, solange keine Daten vorhanden sind.
                    while (voiceDataBuffer.isEmpty() && !musicStream.isReady()) {
                        if (!isRunning) {
                            break outer;
                        }
                        try {
                            wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }

                // Entferne die obersten Elemente aus den Puffern des Voicechats und des Musikstreams.
                Set<short[]> blocks = voiceDataBuffer.values().stream().filter(VoiceChatUser::isReady)
                        .map(VoiceChatUser::getAudioDataBlock).collect(Collectors.toSet());
                voiceDataBuffer.values().removeIf(Predicate.not(VoiceChatUser::hasData));
                short[] musicBlock = new short[AudioManager.BLOCK_SIZE];
                if (musicStream.isReady()) {
                    musicBlock = musicStream.getAudioDataBlock();
                    isPlayingMusic = true;
                } else {
                    isPlayingMusic = false;
                }

                // Ton ist aus, beende Iteration ohne Verarbeitung der empfangenen Daten.
                if (!Chati.CHATI.getPreferences().isSoundOn()) {
                    continue;
                }

                int[] temp = new int[AudioManager.BLOCK_SIZE];
                for (int i = 0; i < AudioManager.BLOCK_SIZE; i++) {
                    // Mische Daten aller Teilnehmer des Voicechats und setze die entsprechende Lautstärke.
                    for (short[] block : blocks) {
                        temp[i] += block[i];
                    }
                    temp[i] *= voiceVolume;

                    // Mische Daten des Musikstreams dazu und setze die entsprechende Lautstärke.
                    musicBlock[i] *= musicVolume;
                    temp[i] += musicBlock[i];

                    // Verhindere einen Overflow der gemischten Daten.
                    mixedData[i] = (short) (temp[i] > Short.MAX_VALUE ? Short.MAX_VALUE :
                            (temp[i] < Short.MIN_VALUE ? Short.MIN_VALUE : temp[i]));
                }

                // Spiele die gemischten Daten ab.
                player.writeSamples(mixedData, 0, mixedData.length);
            }

            this.voiceDataBuffer.clear();
            this.musicStream.clear();
        });
        mixAndPlaybackThread.setDaemon(true);
        mixAndPlaybackThread.start();
    }

    /**
     * Stoppt den gerade laufenden Thread zum Mischen und Abspielen von Audiodaten.
     */
    public synchronized void stop() {
        isRunning = false;
        notifyAll();
    }

    /**
     * Gibt zurück, ob gerade ein Thread zum Mischen und Abspielen von Audiodaten aktiv ist.
     * @return true, wenn ein Thread aktiv ist, sonst false.
     */
    public boolean isRunning() {
        return isRunning;
    }

    /**
     * Gibt zurück, ob gerade Sprachdaten von einem Benutzer abgespielt werden.
     * @param user Zu überprüfender Benutzer.
     * @return true, wenn Sprachdaten von diesem Benutzer abgespielt werden, sonst false.
     */
    public boolean isTalking(IUserView user) {
        VoiceChatUser voiceChatUser = voiceDataBuffer.get(user);
        return voiceChatUser != null && voiceChatUser.isReady();
    }

    /**
     * Gibt zurück, ob gerade Daten eines Musikstreams abgespielt werden.
     * @return true, wenn Musikdaten abgespielt werden, sonst false.
     */
    public boolean isPlayingMusic() {
        return isPlayingMusic;
    }

    /**
     * Stoppt das Abspielen von Musik.
     */
    public void stopMusic() {
        musicStream.clear();
    }

    /**
     * Reiht empfangene Sprachdaten in die Warteschlange der abzuspielenden Daten ein.
     * @param senderId ID des sendenden Benutzers.
     * @param timestamp Zeitstempel der Sprachdaten.
     * @param voiceData Abzuspielende Sprachdaten.
     * @throws UserNotFoundException falls kein Benutzer mit der ID gefunden wurde.
     */
    public void receiveVoiceData(@NotNull final UUID senderId, @NotNull final LocalDateTime timestamp, final byte[] voiceData)
            throws UserNotFoundException {
        if (!isRunning) {
            return;
        }
        IUserView sender = Chati.CHATI.getUserManager().getExternUserView(senderId);
        short[] receivedData = AudioManager.toShort(voiceData, true);

        synchronized (this) {
            if (!voiceDataBuffer.containsKey(sender)) {
                voiceDataBuffer.put(sender, new VoiceChatUser(sender, timestamp, receivedData));
            } else {
                voiceDataBuffer.get(sender).addAudioDataBlock(timestamp, receivedData);
            }
            notifyAll();
        }
    }

    /**
     * Reiht empfangene Daten eines Musikstreams in die Warteschlange der abzuspielenden Daten ein.
     * @param timestamp Zeitstempel der Musikstreamdaten.
     * @param musicData Abzuspielende Musikdaten.
     */
    public void receiveMusicStream(@NotNull final LocalDateTime timestamp, final byte[] musicData) {
        if (!isRunning) {
            return;
        }
        short[] receivedData = AudioManager.toShort(musicData, false);

        synchronized (this) {
            musicStream.addAudioDataBlock(timestamp, receivedData);
            notifyAll();
        }
    }

    @Override
    public void dispose() {
        player.dispose();
    }

    /**
     * Setzt die Gesamtlautstärke.
     * @param totalVolume Gesamtlautstärke
     */
    public void setTotalVolume(final float totalVolume) {
        player.setVolume(totalVolume);
    }

    /**
     * Setzt die Lautstärke des Sprachchats.
     * @param voiceVolume Lautstärke des Sprachchats.
     */
    public void setVoiceVolume(final float voiceVolume) {
        this.voiceVolume = voiceVolume;
    }

    /**
     * Setzt die Lautstärke der Musik.
     * @param musicVolume Lautstärke der Musik.
     */
    public void setMusicVolume(final float musicVolume) {
        this.musicVolume = musicVolume;
    }
}
