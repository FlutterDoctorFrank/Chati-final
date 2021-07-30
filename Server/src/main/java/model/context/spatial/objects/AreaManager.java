package model.context.spatial.objects;

import controller.network.ClientSender;
import model.context.spatial.Location;
import model.context.spatial.Menu;
import model.context.spatial.SpatialContext;
import model.exception.IllegalInteractionException;
import model.exception.IllegalMenuActionException;
import model.notification.AreaManagingRequest;
import model.role.Permission;
import model.user.User;
import model.user.account.UserAccountManager;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Map;
import java.util.UUID;

public class AreaManager extends SpatialContext {

    public AreaManager(String contextName, SpatialContext parent, Menu menu, Location interactionLocation) {
        super(contextName, parent, menu, interactionLocation);
    }

    @Override
    public void interact(User user) {
        //user.setCurrentInteractable(this);
        user.getClientSender().send(ClientSender.SendAction.OPEN_MENU, this);
    }

    @Override
    public void executeMenuOption(User user, int menuOption, String[] args) throws IllegalInteractionException, IllegalMenuActionException {
        switch (menuOption) {
            case 0:
                //user.setCurrentInteractable(null);
                user.getClientSender().send(ClientSender.SendAction.CLOSE_MENU, this);
                break;
            case 1:
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

    @Override
    public SpatialContext getParent() {
        return (SpatialContext) parent;
    }
}
