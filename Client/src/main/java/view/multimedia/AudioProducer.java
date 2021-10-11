package view.multimedia;

import controller.AudioUtils;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Eine Klasse, welche einen Sender von abzuspielenden Audiodaten repräsentiert.
 */
public abstract class AudioProducer {

    protected final Queue<Short> receivedDataQueue;
    protected final int minBlocks;
    protected LocalDateTime lastTimeReceived;
    protected boolean ready;

    /**
     * Erzeugt eine neue Instanz des AudioProducer.
     * @param startingDelay Länge der Daten in Sekunden, die vorhanden sein muss, bevor Daten aus diesem AudioProducer
     * entnommmen werden können.
     */
    protected AudioProducer(final float startingDelay) {
        this.receivedDataQueue = new LinkedBlockingQueue<>();
        this.minBlocks = (int) (startingDelay * AudioUtils.FRAME_RATE);
    }

    /**
     * Fügt abzuspielende Daten in die Warteschlange des AudioProducer hinzu.
     * @param timestamp Zeitstempel der abzuspielenden Daten.
     * @param audioBlock Abzuspielende Daten.
     */
    public void addFrame(@NotNull LocalDateTime timestamp, final short[] audioBlock) {
        this.lastTimeReceived = timestamp;
        if (!ready && queueSizeInFrames() > minBlocks) {
            ready = true;
        }
    }

    /**
     * Entfernt den aktuell abzuspielenden Block mit Audiodaten aus der Warteschlange und gibt diesen zurück.
     * @return Block mit aktuell abzuspielenden Audiodaten.
     */
    public short[] getNextFrame() {
        short[] block = new short[AudioUtils.FRAME_SIZE];
        for (int i = 0; i < block.length; i++) {
            Short data = receivedDataQueue.poll();
            block[i] = data != null ? data : 0;
        }
        if (!hasData()) {
            ready = false;
        }
        return block;
    }

    /**
     * Gibt zurück, ob die Warteschlange genug Daten zum Abspielen eines Blocks enthält.
     * @return true, wenn sie genug Daten enthält, sonst false.
     */
    public boolean hasData() {
        return queueSizeInFrames() >= 1;
    }

    /**
     * Gibt zurück, ob die Warteschlange bereit zum Abspielen von Daten ist.
     * @return true, wenn sie bereit ist, sonst false.
     */
    public boolean isReady() {
        return hasData() && ready;
    }

    /**
     * Gibt den Zeitstempel zurück, an dem in dieser Warteschlange das letzte mal Daten empfangen wurde.
     * @return Zeitstempel der letzten empfangenen Daten.
     */
    public @NotNull LocalDateTime getLastTimeReceived() {
        return lastTimeReceived;
    }

    /**
     * Gibt die Größe der Warteschlange in der Anzahl abspielbarer Blöcke wieder.
     * @return Größe der Warteschlange in der Anzahl abspielbarer Blöcke.
     */
    protected int queueSizeInFrames() {
        return receivedDataQueue.size() / AudioUtils.FRAME_SIZE;
    }
}
