package model.context.spatial;

import model.user.User;

import java.time.LocalDateTime;

public class ContextReservation {
    private User reserver;
    private LocalDateTime from;
    private LocalDateTime to;

    public ContextReservation(User reserver, LocalDateTime from, LocalDateTime to) {
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
