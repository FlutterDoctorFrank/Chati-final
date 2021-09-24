package view2.audio;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Eine Klasse, welche einen Sender von abzuspielenden Audiodaten repräsentiert.
 */
public abstract class AudioProducer {

    /*
     * Beginne erst mit dem Abspielen von AudioDaten, wenn mindestens Daten für eine Abspieldauer von STARTING_DELAY in
     * Sekunden vorhanden sind. Dies führt zu einer kleinen Verzögerung, verhindert aber, dass sich bei einem
     * verspäteten Paket der Puffer direkt leert und das Abspielen der Daten unterbrochen wird.
     */
    protected static final float STARTING_DELAY = 0.2f;
    protected static final int MIN_BLOCKS = (int) (STARTING_DELAY * AudioManager.SEND_RATE);

    protected final Queue<Short> audioDataQueue;
    protected LocalDateTime lastTimeReceived;
    protected boolean ready;

    /**
     * Erzeugt eine neue Instanz des AudioProducer.
     */
    protected AudioProducer() {
        this.audioDataQueue = new LinkedBlockingQueue<>();
    }

    /**
     * Fügt abzuspielende Daten in die Warteschlange des AudioProducer hinzu.
     * @param timestamp Zeitstempel der abzuspielenden Daten.
     * @param audioBlock Abzuspielende Daten.
     */
    public void addAudioDataBlock(@NotNull LocalDateTime timestamp, final short[] audioBlock) {
        this.lastTimeReceived = timestamp;
        if (!ready && queueSizeInBlocks() > MIN_BLOCKS) {
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
