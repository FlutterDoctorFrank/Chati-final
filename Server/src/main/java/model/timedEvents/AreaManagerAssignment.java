package model.timedEvents;

import model.MessageBundle;
import model.context.spatial.Area;
import model.context.spatial.AreaReservation;
import model.notification.Notification;
import model.role.Role;
import model.user.User;
import org.jetbrains.annotations.NotNull;
import java.time.LocalDateTime;

/**
 * Repräsentiert das Ereignis, einem Benutzer nach Eintreffen des Anfangszeitpunkts einer {@link AreaReservation} die
 * Rolle des Bereichsberechtigten zu übergeben und ihn über den Erhalt der Rolle zu informieren.
 */
public class AreaManagerAssignment extends TimedEvent {

    /** Reservierung eines Bereichs. */
    private final AreaReservation reservation;

    /**
     * Erzeugt eine neue Instanz des TimedEvent.
     * @param reservation Reservierung des Bereichs.
     */
    public AreaManagerAssignment(@NotNull final AreaReservation reservation) {
        super(reservation.getFrom());
        this.reservation = reservation;
    }

    @Override
    public void execute() {
        User reserver = reservation.getReserver();
        Area reservedContext = reservation.getArea();

        if (reservedContext.getWorld() != null) {
            reserver.addRole(reservedContext, Role.AREA_MANAGER);
            Notification roleReceiveNotification = new Notification(reserver, reservedContext.getWorld(), new MessageBundle("key"));
            reserver.addNotification(roleReceiveNotification);
        }
    }

    @Override
    public boolean isValid() {
        User reserver = reservation.getReserver();
        Area reservedContext = reservation.getArea();
        LocalDateTime from = reservation.getFrom();
        LocalDateTime to = reservation.getTo();
        return reservedContext.isReservedAtBy(reserver, from, to) && !reserver.hasRole(reservedContext, Role.AREA_MANAGER);
    }
}