package view2.audio;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;

/**
 * Eine Klasse, welche den Stream abzuspielender Musikdaten repräsentiert.
 */
public class MusicStream extends AudioProducer {

    /*
     * Der Server liefert die Streamingdaten ggf. mit einer leicht abweichenden Geschwindigkeit, als sie vom Client
     * abgespielt werden. In diesem Fall wird die Geschwindigkeit des Abspielens von Musikdaten leicht angepasst, um
     * einen leeren oder unendlich steigenden Puffer zu vermeiden.
     */
    private static final float MAX_SLOW_DOWN_RATE = 1.05f;
    private static final float MIN_SLOW_DOWN_RATE = 0.95f;
    private static final float SLOW_DOWN_STEP = 0.001f;

    private float slowDownRate;
    private int lastQueueSize;

    /**
     * Erzeugt eine neue Instanz des MusicStream.
     */
    public MusicStream() {
        this.slowDownRate = 1;
        this.lastQueueSize = 0;
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
            for (int i = 0; i < Math.floor(numPlaybackSample); i++) {
                audioDataQueue.add(musicData);
                numPlaybackSample--;
            }
        }

        this.lastTimeReceived = timestamp;
        if (!ready && queueSizeInBlocks() > MIN_BLOCKS) {
            ready = true;
        }
    }

    /**
     * Leert den Queue der Musikdaten.
     */
    public void clear() {
        audioDataQueue.clear();
    }
}
