package model.timedEvents;

import model.MessageBundle;
import model.context.spatial.AreaReservation;
import model.context.spatial.SpatialContext;
import model.notification.Notification;
import model.role.Role;
import model.user.User;

import java.time.LocalDateTime;

public class AreaManagerAssignment extends TimedEvent {

    private final SpatialContext context;
    private final AreaReservation reservation;

    public AreaManagerAssignment(SpatialContext context, AreaReservation reservation) {
        super(reservation.getFrom());
        this.context = context;
        this.reservation = reservation;
    }

    @Override
    public void execute() {
        User reserver = reservation.getReserver();
        reserver.addRole(context, Role.AREA_MANAGER);
        Notification roleReceiveNotification = new Notification(reserver, context.getWorld(), new MessageBundle("key"));
        reserver.addNotification(roleReceiveNotification);
    }

    @Override
    public boolean isValid() {
        User reserver = reservation.getReserver();
        LocalDateTime from = reservation.getFrom();
        LocalDateTime to = reservation.getTo();
        return context.isReservedAtBy(reserver, from, to) && !reserver.hasRole(context, Role.AREA_MANAGER);
    }
}
