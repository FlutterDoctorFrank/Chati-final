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
 * Repräsentiert das Ereignis, einem Benutzer nach Eintreffen des Endzeitpunkts einer {@link AreaReservation} die Rolle
 * des Bereichsberechtigten zu entziehen, ihn über den Verlust der Rolle zu informieren und die Reservierung zu
 * entfernen.
 */
public class AreaManagerWithdrawal extends TimedEvent {

    /** Reservierung des Bereichs. */
    private final AreaReservation reservation;

    /**
     * Erzeugt eine neue Instanz des TimedEvent.
     * @param reservation Reservierung des Bereichs.
     */
    public AreaManagerWithdrawal(@NotNull final AreaReservation reservation) {
        super(reservation.getTo());
        this.reservation = reservation;
    }

    @Override
    public void execute() {
        User reserver = reservation.getReserver();
        Area reservedContext = reservation.getArea();

        reservedContext.getWorld();
        reserver.removeRole(reservedContext, Role.AREA_MANAGER);
        Notification roleLoseNotification = new Notification(reserver, reservedContext.getWorld(),
                new MessageBundle("role.area-manage.withdrawal", reservedContext.getContextName()));
        reserver.addNotification(roleLoseNotification);
        reservedContext.removeReservation(reservation);
    }

    @Override
    public boolean isValid() {
        User reserver = reservation.getReserver();
        Area reservedContext = reservation.getArea();
        LocalDateTime from = reservation.getFrom();
        LocalDateTime to = reservation.getTo();
        return reservedContext.isReservedAtBy(reserver, from, to) && reserver.hasRole(reservedContext, Role.AREA_MANAGER);
    }
}