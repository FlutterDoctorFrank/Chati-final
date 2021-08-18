package model.context.spatial;

import model.user.User;
import org.jetbrains.annotations.NotNull;
import java.time.LocalDateTime;

/**
 * Repräsentiert die Reservierung eines Bereichs zum Erhalt der Rolle des Bereichsberechtigten.
 */
public class AreaReservation {

    /** Reservierender Benutzer. */
    private final User reserver;

    /** Reservierter Bereich. */
    private final Area area;

    /** Anfangszeitpunkt der Reservierung. */
    private final LocalDateTime from;

    /** Endzeitpunkt der Reservierung. */
    private final LocalDateTime to;

    /**
     * Erzeugt eine neue Instanz der Reservierung.
     * @param reserver reservierender Benutzer.
     * @param area reservierter Bereich.
     * @param from Anfangszeitpunkt der Reservierung.
     * @param to Endzeitpunkt der Reservierung.
     */
    public AreaReservation(@NotNull final User reserver, @NotNull final Area area,
                           @NotNull final LocalDateTime from, @NotNull final LocalDateTime to) {
        this.reserver = reserver;
        this.area = area;
        this.from = from;
        this.to = to;
    }

    /**
     * Gibt den reservierenden Benutzer zurück.
     * @return Reservierender Benutzer.
     */
    public @NotNull User getReserver() {
        return reserver;
    }

    /**
     * Gibt den reservierten Bereich zurück.
     * @return Reservierter Bereich.
     */
    public @NotNull Area getArea() {
        return area;
    }

    /**
     * Gibt den Anfangszeitpunkt der Reservierung zurück.
     * @return Anfangszeitpunkt der Reservierung.
     */
    public @NotNull LocalDateTime getFrom() {
        return from;
    }

    /**
     * Gibt den Endzeitpunkt der Reservierung zurück.
     * @return Endzeitpunkt der Reservierung.
     */
    public @NotNull LocalDateTime getTo() {
        return to;
    }
}