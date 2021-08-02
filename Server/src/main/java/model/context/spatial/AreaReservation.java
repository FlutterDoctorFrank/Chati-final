package model.context.spatial;

import model.user.User;

import java.time.LocalDateTime;

/**
 * Repräsentiert die Reservierung eines Bereichs zum Erhalt der Rolle des Bereichsberechtigten.
 */
public class AreaReservation {

    private final User reserver;
    private final Area area;
    private final LocalDateTime from;
    private final LocalDateTime to;

    public AreaReservation(User reserver, Area area, LocalDateTime from, LocalDateTime to) {
        this.reserver = reserver;
        this.area = area;
        this.from = from;
        this.to = to;
    }

    public User getReserver() {
        return reserver;
    }

    public Area getArea() {
        return area;
    }

    public LocalDateTime getFrom() {
        return from;
    }

    public LocalDateTime getTo() {
        return to;
    }
}
