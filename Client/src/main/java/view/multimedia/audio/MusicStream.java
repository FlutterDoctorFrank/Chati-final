package view.multimedia.audio;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Eine Klasse, welche den Stream abzuspielender Musikdaten repräsentiert.
 */
public class MusicStream extends AudioProducer {

    /*
     * Beginne erst mit dem Abspielen von AudioDaten, wenn mindestens Daten für eine Abspieldauer von STARTING_DELAY in
     * Sekunden vorhanden sind. Dies führt zu einer kleinen Verzögerung, verhindert aber, dass sich bei einem
     * verspäteten Paket der Puffer direkt leert und das Abspielen der Daten unterbrochen wird.
     */
    private static final float MUSIC_STARTING_DELAY = 1;

    private final Queue<Float> positionQueue;
    private final Queue<Integer> secondsQueue;

    private float currentPosition;
    private int currentSeconds;

    /**
     * Erzeugt eine neue Instanz des MusicStream.
     */
    public MusicStream() {
        super(MUSIC_STARTING_DELAY);
        this.positionQueue = new LinkedBlockingQueue<>();
        this.secondsQueue = new LinkedBlockingQueue<>();
        this.currentPosition = 0;
        this.currentSeconds = 0;
    }

    /**
     * Fügt abzuspielende Daten in die Warteschlange des MusicStream hinzu.
     * @param timestamp Zeitstempel der abzuspielenden Daten.
     * @param musicDataBlock Abzuspielende Daten.
     * @param position Aktuelle Position in zusammenhängenden Daten.
     * @param seconds Aktuelle Sekunde in zusammenhängenden Daten.
     */
    public void addAudioDataBlock(@NotNull final LocalDateTime timestamp, final short[] musicDataBlock,
                                  final float position, final int seconds) {
        int sizeBefore = queueSizeInFrames();
        this.addFrame(timestamp, musicDataBlock);
        int sizeAfter = queueSizeInFrames();
        int difference = sizeAfter - sizeBefore;
        for (int i = 0; i < difference; i++) {
            this.positionQueue.add(position);
            this.secondsQueue.add(seconds);
        }
    }

    @Override
    public void addFrame(@NotNull final LocalDateTime timestamp, final short[] musicDataBlock) {
        /*
         * Der Server liefert die Streamingdaten ggf. mit einer leicht abweichenden Geschwindigkeit, als sie vom Client
         * abgespielt werden. In diesem Fall wird die Geschwindigkeit des Abspielens von Musikdaten leicht angepasst, um
         * einen leeren oder unendlich steigenden Puffer zu vermeiden.
         */
        float slowDownRate = -queueSizeInFrames() / 448f + 1;

        float numPlaybackSample = 0;
        for (short musicData : musicDataBlock) {
            numPlaybackSample += slowDownRate;
            for (int i = (int) numPlaybackSample; i > 0; i--) {
                receivedDataQueue.add(musicData);
                numPlaybackSample--;
            }
        }
        super.addFrame(timestamp, musicDataBlock);
    }

    @Override
    public short[] getNextFrame() {
        this.currentPosition = !positionQueue.isEmpty() ? positionQueue.poll() : (hasData() ? currentPosition : 0);
        this.currentSeconds = !secondsQueue.isEmpty() ? secondsQueue.poll() : (hasData() ? currentSeconds : 0);
        return super.getNextFrame();
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
        receivedDataQueue.clear();
        positionQueue.clear();
        secondsQueue.clear();
        currentPosition = 0;
        currentSeconds = 0;
    }
}
