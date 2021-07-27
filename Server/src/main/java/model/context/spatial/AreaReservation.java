package model.context.spatial;

import model.user.User;

import java.time.LocalDateTime;

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
