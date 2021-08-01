package model.context.spatial;

import model.user.User;

import java.time.LocalDateTime;

/**
 * Repräsentiert die Reservierung eines Bereichs zum Erhalt der Rolle des Bereichsberechtigten.
 */
public class AreaReservation {

    private final User reserver;
    private final SpatialContext context;
    private final LocalDateTime from;
    private final LocalDateTime to;

    public AreaReservation(User reserver, SpatialContext context, LocalDateTime from, LocalDateTime to) {
        this.reserver = reserver;
        this.context = context;
        this.from = from;
        this.to = to;
    }

    public User getReserver() {
        return reserver;
    }

    public SpatialContext getContext() {
        return context;
    }

    public LocalDateTime getFrom() {
        return from;
    }

    public LocalDateTime getTo() {
        return to;
    }
}
