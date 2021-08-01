package model.timedEvents;

import java.time.LocalDateTime;

/**
 * Eine Klasse, deren Objekte ein in der Zukunft auszuführendes Ereignis repräsentiert.
 */
public abstract class TimedEvent implements Comparable<TimedEvent> {

    /** Zeitpunkt, an dem das Ereignis ausgeführt werden soll. */
    protected final LocalDateTime time;

    /**
     * Erzeugt eine neue Instanz eines TimedEvent.
     * @param time Zeitpunkt, an dem das Ereignis ausgeführt werden soll.
     */
    protected TimedEvent(LocalDateTime time) {
        this.time = time;
    }

    /**
     * Führt das Ereignis aus.
     */
    public abstract void execute();

    /**
     * Überprüft, ob das auszuführende Ereignis noch gültig ist.
     * @return true, wenn es gültig ist, sonst false.
     */
    public abstract boolean isValid();

    /**
     * Gibt den Zeitpunkt zurück, an dem das Ereignis ausgeführt werden soll.
     * @return Zeitpunkt, an dem das Ereigniss ausgeführt werden soll.
     */
    public LocalDateTime getTime() {
        return time;
    }

    @Override
    public int compareTo(TimedEvent event) {
        return time.compareTo(event.getTime());
    }
}
