package view2.audio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.AudioDevice;
import com.badlogic.gdx.audio.Sound;
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
public class AudioConsumer implements Runnable, Disposable {

    /*
     * Musik und Sounds sind lauter als der VoiceChat und müssen heruntergeregelt werden.
     */
    private static final float MIX_DOWN_FACTOR = 1 / 30f;

    private final AudioDevice player;
    private final Map<IUserView, VoiceChatUser> voiceDataBuffer;
    private final MusicStream musicStream;

    private float musicVolume;
    private float voiceVolume;
    private boolean isRunning;

    /**
     * Erzeugt eine neue Instanz des AudioConsumer.
     */
    public AudioConsumer() {
        this.player = Gdx.audio.newAudioDevice(AudioManager.SAMPLING_RATE, AudioManager.MONO);
        this.voiceDataBuffer = new ConcurrentHashMap<>();
        this.musicStream = new MusicStream();
    }

    @Override
    public void run() {
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
            }

            int[] temp = new int[AudioManager.BLOCK_SIZE];
            for (int i = 0; i < AudioManager.BLOCK_SIZE; i++) {
                if (Chati.CHATI.getPreferences().isSoundOn()) {
                    // Mische Daten aller Teilnehmer des Voicechats und setze die entsprechende Lautstärke.
                    for (short[] block : blocks) {
                        temp[i] += block[i];
                    }
                    temp[i] *= voiceVolume;

                    // Mische Daten des Musikstreams dazu und setze die entsprechende Lautstärke.
                    musicBlock[i] *= musicVolume;
                    temp[i] += musicBlock[i];
                }

                // Verhindere einen Overflow der gemischten Daten.
                mixedData[i] = (short) (temp[i] > Short.MAX_VALUE ? Short.MAX_VALUE
                        : (temp[i] < Short.MIN_VALUE ? Short.MIN_VALUE : temp[i]));
            }

            // Spiele die gemischten Daten ab.
            player.writeSamples(mixedData, 0, mixedData.length);
        }
        this.voiceDataBuffer.clear();
        this.musicStream.stop();
    }

    /**
     * Startet einen Thread zum Mischen und Abspielen empfangener Audiodaten, sofern nicht bereits einer läuft.
     */
    public void start() {
        if (isRunning) {
            return;
        }
        isRunning = true;

        Thread mixAndPlaybackThread = new Thread(this);
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
    public boolean isTalking(@NotNull final IUserView user) {
        VoiceChatUser voiceChatUser = voiceDataBuffer.get(user);
        return voiceChatUser != null && voiceChatUser.isReady();
    }

    /**
     * Gibt zurück, ob gerade Daten eines Musikstreams abgespielt werden.
     * @return true, wenn Musikdaten abgespielt werden, sonst false.
     */
    public boolean isPlayingMusic() {
        return musicStream.isReady();
    }

    /**
     * Stoppt das Abspielen von Musik.
     */
    public void stopMusic() {
        musicStream.stop();
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
     * @param position Aktuelle Position im Musikstück.
     * @param seconds Aktuelle Sekunde im Musikstück.
     */
    public void receiveMusicStream(@NotNull final LocalDateTime timestamp, final byte[] musicData,
                                   final float position, final int seconds) {
        if (!isRunning) {
            return;
        }
        short[] receivedData = AudioManager.toShort(musicData, false);

        synchronized (this) {
            musicStream.addAudioDataBlock(timestamp, receivedData, position, seconds);
            notifyAll();
        }
    }

    @Override
    public void dispose() {
        player.dispose();
    }

    /**
     * Spielt einen Sound ab.
     * @param sound Abzuspielender Sound.
     * @param totalVolume Position des Lautstärkereglers für Gesamtlautstärke zwischen 0 und 1.
     * @param soundVolume Position des Lautstärkereglers für Soundlautstärke zwischen 0 und 1.
     */
    public void playSound(@NotNull final Sound sound, final float totalVolume, final float soundVolume) {
        if (totalVolume < 0 || totalVolume > 1 || soundVolume < 0 || soundVolume > 1) {
            return;
        }
        sound.play(MIX_DOWN_FACTOR * (float) Math.sqrt(totalVolume * soundVolume));
    }

    /**
     * Setzt die Gesamtlautstärke.
     * @param volume Position des Lautstärkereglers für Gesamtlautstärke zwischen 0 und 1.
     */
    public void setTotalVolume(final float volume) {
        if (volume < 0 || volume > 1) {
            return;
        }
        player.setVolume(volume);
    }

    /**
     * Setzt die Lautstärke des Sprachchats.
     * @param volume Position des Lautstärkereglers für Sprachlautstärke zwischen 0 und 1.
     */
    public void setVoiceVolume(final float volume) {
        if (volume < 0 || volume > 1) {
            return;
        }
        this.voiceVolume = getVolume(volume);
    }

    /**
     * Setzt die Lautstärke der Musik.
     * @param volume Position des Lautstärkereglers für Musiklautstärke zwischen 0 und 1.
     */
    public void setMusicVolume(final float volume) {
        if (volume < 0 || volume > 1) {
            return;
        }
        this.musicVolume = getVolume(MIX_DOWN_FACTOR * volume);
    }

    /**
     * Gibt die aktuelle Position im laufenden Musikstück zurück.
     * @return Aktuelle Position im Musikstück.
     */
    public float getCurrentPosition() {
        return musicStream.getCurrentPosition();
    }

    /**
     * Gibt die aktuelle Sekunde im laufenden Musikstück.
     * @return Aktuelle Sekunde im Musikstück.
     */
    public int getCurrentSeconds() {
        return musicStream.getCurrentSeconds();
    }

    /**
     * Berechnet zu gegebener Größe des Lautstärkereglers auf Basis einer Exponentialfunktion den Faktor, der mit den
     * PCM-Samples multipliziert werden muss um das Sample in gewünschter Lautstärke wiederzugeben.
     * @param value Wert des Lautstärkereglers zwischen 0 und 1.
     * @return Faktor zur Multiplikation mit den PCM-Daten zwischen 0 und 1.
     */
    private float getVolume(final float value) {
        float midValue = 0.9f;
        double a = Math.pow(1 / midValue - 1, 2);
        return (float) ((Math.pow(a, value) - 1) / (a - 1));
    }
}
