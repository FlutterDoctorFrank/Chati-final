package view2.audio;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Eine Klasse, welche einen Sender von abzuspielenden Audiodaten repräsentiert.
 */
public abstract class AudioProducer {

    protected final Queue<Short> audioDataQueue;
    protected final int minBlocks;
    protected LocalDateTime lastTimeReceived;
    protected boolean ready;

    /**
     * Erzeugt eine neue Instanz des AudioProducer.
     * @param startingDelay Länge der Daten in Sekunden, die vorhanden sein muss, bevor Daten aus diesem AudioProducer
     * entnommmen werden können.
     */
    protected AudioProducer(final float startingDelay) {
        this.audioDataQueue = new LinkedBlockingQueue<>();
        this.minBlocks = (int) (startingDelay * AudioManager.SEND_RATE);
    }

    /**
     * Fügt abzuspielende Daten in die Warteschlange des AudioProducer hinzu.
     * @param timestamp Zeitstempel der abzuspielenden Daten.
     * @param audioBlock Abzuspielende Daten.
     */
    public void addAudioDataBlock(@NotNull LocalDateTime timestamp, final short[] audioBlock) {
        this.lastTimeReceived = timestamp;
        if (!ready && queueSizeInBlocks() > minBlocks) {
            ready = true;
        }
    }

    /**
     * Entfernt den aktuell abzuspielenden Block mit Audiodaten aus der Warteschlange und gibt diesen zurück.
     * @return Block mit aktuell abzuspielenden Audiodaten.
     */
    public short[] getAudioDataBlock() {
        short[] block = new short[AudioManager.BLOCK_SIZE];
        for (int i = 0; i < block.length; i++) {
            Short data = audioDataQueue.poll();
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
        return queueSizeInBlocks() >= 1;
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
    protected int queueSizeInBlocks() {
        return audioDataQueue.size() / AudioManager.BLOCK_SIZE;
    }
}
