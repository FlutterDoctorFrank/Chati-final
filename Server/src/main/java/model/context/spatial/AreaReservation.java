package model.context.spatial;

import model.user.User;

import java.time.LocalDateTime;

/**
 * Repräsentiert die Reservierung eines Bereichs zum Erhalt der Rolle des Bereichsberechtigten.
 */
public class AreaReservation {

    private User reserver;
    private LocalDateTime from;
    private LocalDateTime to;

    public AreaReservation(User reserver, LocalDateTime from, LocalDateTime to) {
        this.reserver = reserver;
        this.from = from;
        this.to = to;
    }

    public User getReserver() {
        return reserver;
    }

    public LocalDateTime getFrom() {
        return from;
    }

    public LocalDateTime getTo() {
        return to;
    }
}
