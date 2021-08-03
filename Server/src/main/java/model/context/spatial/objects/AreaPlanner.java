package model.context.spatial.objects;

import controller.network.ClientSender;
import model.communication.CommunicationMedium;
import model.communication.CommunicationRegion;
import model.context.spatial.Area;
import model.context.spatial.Expanse;
import model.context.spatial.Menu;
import model.exception.IllegalInteractionException;
import model.exception.IllegalMenuActionException;
import model.notification.AreaManagingRequest;
import model.role.Permission;
import model.user.User;
import model.user.account.UserAccountManager;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Eine Klasse, welche ein Objekt repräsentiert, durch welches ein Benutzer die Rolle des Bereichsberechtigten für einen
 * zukünftigen Zeitpunkt für einen festgelegten räumlichen Kontext beantragen kann. Ist immer vom Typ
 * {@link model.context.spatial.SpatialContextType#OBJECT}.
 */
public class AreaPlanner extends Interactable {

    /**
     * Erzeugt eines neue Instanz des AreaPlanner.
     * @param objectName Name des Objekts.
     * @param parent Übergeordneter Kontext.
     * @param expanse Räumliche Ausdehnung des Kontexts.
     * @param communicationRegion Geltende Kommunikationsform.
     * @param communicationMedia Benutzbare Kommunikationsmedien.
     */
    public AreaPlanner(String objectName, Area parent, CommunicationRegion communicationRegion,
                       Set<CommunicationMedium> communicationMedia, Expanse expanse) {
        super(objectName, parent, communicationRegion, communicationMedia, expanse, Menu.AREA_PLANNER_MENU);
    }

    @Override
    public void interact(User user) {
        // Öffne das Menü beim Benutzer.
        user.setCurrentInteractable(this);
        user.setMoveable(false);
        user.getClientSender().send(ClientSender.SendAction.OPEN_MENU, this);
    }

    @Override
    public void executeMenuOption(User user, int menuOption, String[] args) throws IllegalInteractionException, IllegalMenuActionException {
        switch (menuOption) {
            case 0: // Schließt das Menü.
                user.setCurrentInteractable(null);
                user.setMoveable(true);
                user.getClientSender().send(ClientSender.SendAction.CLOSE_MENU, this);
                break;
            case 1: // Stellt eine Anfrage zur Reservierung zum Erhalt der Rolle des Bereichsberechtigten im übergeordneten
                    // Kontext.
                LocalDateTime from;
                LocalDateTime to;
                try {
                    from = LocalDateTime.parse(args[0]);
                    to = LocalDateTime.parse(args[1]);
                } catch (DateTimeParseException e) {
                    throw new IllegalMenuActionException("", "Ungültige Zeit.");
                }
                if (from.isAfter(to) || from.getMinute() != 0 || to.getMinute() != 0 || to.getHour() - from.getHour() != 1) {
                    throw new IllegalMenuActionException("", "Ungültige Zeit.");
                }
                if (getParent().isReservedBy(user)) {
                   throw new IllegalMenuActionException("", "Du hast diesen Bereich bereits einmal reserviert.");
                }
                if (getParent().isReservedAt(from, to)) {
                    throw new IllegalMenuActionException("", "Dieser Zeitraum ist bereits belegt. Bitte probiere einen anderen.");
                }

                Map<UUID, User> receivers = UserAccountManager.getInstance().getUsersWithPermission(user.getWorld(), Permission.ASSIGN_AREA_MANAGER);
                receivers.forEach((receiverID, receiver) -> {
                    AreaManagingRequest areaManagingRequest = new AreaManagingRequest(receiver, user, getParent(), from, to);
                    receiver.addNotification(areaManagingRequest);
                });
                break;
            default:
                throw new IllegalInteractionException("No valid menu option", user);
        }
    }
}