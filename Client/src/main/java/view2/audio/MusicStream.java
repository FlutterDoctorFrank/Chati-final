package view2.audio;

import model.context.spatial.ContextMusic;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Eine Klasse, welche den Stream abzuspielender Musikdaten repräsentiert.
 */
public class MusicStream extends AudioProducer {

    /*
     * Der Server liefert die Streamingdaten ggf. mit einer leicht abweichenden Geschwindigkeit, als sie vom Client
     * abgespielt werden. In diesem Fall wird die Geschwindigkeit des Abspielens von Musikdaten leicht angepasst, um
     * einen leeren oder unendlich steigenden Puffer zu vermeiden.
     */
    private static final float MAX_SLOW_DOWN_RATE = 1.1f;
    private static final float MIN_SLOW_DOWN_RATE = 0.9f;
    private static final float SLOW_DOWN_STEP = 0.001f;

    private final Queue<Float> positionQueue;
    private final Queue<Integer> secondsQueue;

    private float slowDownRate;
    private int lastQueueSize;

    private float currentPosition;
    private int currentSeconds;

    /**
     * Erzeugt eine neue Instanz des MusicStream.
     */
    public MusicStream() {
        this.positionQueue = new LinkedBlockingQueue<>();
        this.secondsQueue = new LinkedBlockingQueue<>();
        this.slowDownRate = 1;
        this.lastQueueSize = 0;
        this.currentPosition = 0;
        this.currentSeconds = 0;
    }

    /**
     * Fügt abzuspielende Daten in die Warteschlange des AudioProducer hinzu.
     * @param timestamp Zeitstempel der abzuspielenden Daten.
     * @param musicDataBlock Abzuspielende Daten.
     * @param position Aktuelle Position in zusammenhängenden Daten.
     * @param seconds Aktuelle Sekunde in zusammenhängenden Daten.
     */
    public void addAudioDataBlock(@NotNull final LocalDateTime timestamp, final short[] musicDataBlock,
                                  final float position, final int seconds) {
        int sizeBefore = queueSizeInBlocks();
        this.addAudioDataBlock(timestamp, musicDataBlock);
        int sizeAfter = queueSizeInBlocks();
        int difference = sizeAfter - sizeBefore;
        for (int i = 0; i < difference; i++) {
            this.positionQueue.add(position);
            this.secondsQueue.add(seconds);
        }
    }

    @Override
    public void addAudioDataBlock(@NotNull final LocalDateTime timestamp, final short[] musicDataBlock) {
        int currentQueueSize = queueSizeInBlocks();
        if (ready && (currentQueueSize < MIN_BLOCKS || currentQueueSize < lastQueueSize) && slowDownRate < MAX_SLOW_DOWN_RATE) {
            // Puffer zu klein oder wird kleiner. Sample runter.
            slowDownRate += SLOW_DOWN_STEP;
        } else if (ready && currentQueueSize >= MIN_BLOCKS && currentQueueSize > lastQueueSize && slowDownRate > MIN_SLOW_DOWN_RATE) {
            // Puffer nicht zu klein und wird größer. Sample hoch.
            slowDownRate -= SLOW_DOWN_STEP;
        }
        lastQueueSize = currentQueueSize;

        float numPlaybackSample = 0;
        for (short musicData : musicDataBlock) {
            numPlaybackSample += slowDownRate;
            for (int i = (int) numPlaybackSample; i > 0; i--) {
                audioDataQueue.add(musicData);
                numPlaybackSample--;
            }
        }
        super.addAudioDataBlock(timestamp, musicDataBlock);
    }

    @Override
    public short[] getAudioDataBlock() {
        this.currentPosition = !positionQueue.isEmpty() ? positionQueue.poll() : (hasData() ? currentPosition : 0);
        this.currentSeconds = !secondsQueue.isEmpty() ? secondsQueue.poll() : (hasData() ? currentSeconds : 0);
        return super.getAudioDataBlock();
    }

    /**
     * Gibt die aktuelle Position im laufenden Musikstück zurück.
     * @return Aktuelle Position im Musikstück.
     */
    public float getCurrentPosition() {
        return currentPosition;
    }

    /**
     * Gibt die aktuelle Sekunde im laufenden Musikstück.
     * @return Aktuelle Sekunde im Musikstück.
     */
    public int getCurrentSeconds() {
        return currentSeconds;
    }

    /**
     * Leert den Queue der Musikdaten.
     */
    public void stop() {
        ready = false;
        audioDataQueue.clear();
        positionQueue.clear();
        secondsQueue.clear();
        currentPosition = 0;
        currentSeconds = 0;
    }
}
