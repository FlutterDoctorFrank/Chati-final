package model.timedEvents;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.function.Predicate;

/**
 * Eine Klasse die benutzt wird um Ereignisse auszuführen, die in der Zukunft stattfinden sollen.
 */
public class TimedEventScheduler extends Thread {

    /** Singleton-Instanz der Klasse. */
    private static TimedEventScheduler scheduler;

    /** Queue der auszuführenden Ereignisse. */
    private final PriorityBlockingQueue<TimedEvent> timedEvents;

    /**
     * Erzeugt eine neue Instanz des TimedEventScheduler.
     */
    private TimedEventScheduler() {
        this.timedEvents = new PriorityBlockingQueue<>();
    }

    @Override
    public void run() {
        for (;;) { // ever
            // Warte, solange es keine abzuarbeitenden Ereignisse gibt.
            while (timedEvents.isEmpty()) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            LocalDateTime now = LocalDateTime.now();
            TimedEvent first = timedEvents.peek();
            // Prüfe, ob das erste Ereignis jetzt abzuarbeiten ist.
            if (!now.isBefore(first.getTime())) {
                // Prüfe, ob das Ereignis noch gültig ist.
                if (first.isValid()) {
                    first.execute();
                }
                timedEvents.poll();
            } else {
                // Wenn die Zeit nicht erreicht ist, warte, bis die Zeit zum Abarbeiten des ersten Ereignisses
                // eingetroffen ist.
                try {
                    wait(now.until(first.getTime(), ChronoUnit.MILLIS));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            // Entferne alle ungültigen Ereignisse.
            timedEvents.removeIf(Predicate.not(TimedEvent::isValid));
        }
    }

    /**
     * Füge ein Ereignis hinzu.
     * @param event Hinzuzufügendes Ereignis.
     */
    public void put(TimedEvent event) {
        timedEvents.put(event);
        notifyAll();
    }

    /**
     * Gibt die Singleton-Instanz des TimedEventScheduler zurück
     * @return Singleton-Instanz des TimedEventScheduler.
     */
    public static TimedEventScheduler getInstance() {
        if (scheduler == null) {
            scheduler = new TimedEventScheduler();
        }
        return scheduler;
    }
}
