package model.context.spatial.objects;

import controller.network.ClientSender.SendAction;
import model.communication.CommunicationMedium;
import model.communication.CommunicationRegion;
import model.context.spatial.Area;
import model.context.spatial.Expanse;
import model.context.spatial.ContextMenu;
import model.exception.IllegalInteractionException;
import model.exception.IllegalMenuActionException;
import model.notification.AreaManagingRequest;
import model.role.Permission;
import model.user.User;
import model.user.account.UserAccountManager;
import org.jetbrains.annotations.NotNull;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Eine Klasse, welche ein Objekt repräsentiert, durch welches ein Benutzer die Rolle des Bereichsberechtigten für einen
 * zukünftigen Zeitpunkt für einen festgelegten räumlichen Kontext beantragen kann.
 */
public class AreaPlanner extends Interactable {

    /** Menü-Option zum Anfragen der Rolle des Bereichsberechtigten in einem Bereich. */
    private static final int MENU_OPTION_REQUEST_ROLE = 1;

    /**
     * Erzeugt eine neue Instanz des AreaPlanner.
     * @param objectName Name des Objekts.
     * @param parent Übergeordneter Kontext.
     * @param expanse Räumliche Ausdehnung des Kontexts.
     * @param communicationRegion Geltende Kommunikationsform.
     * @param communicationMedia Benutzbare Kommunikationsmedien.
     */
    public AreaPlanner(@NotNull final String objectName, @NotNull final Area parent,
                       @NotNull final CommunicationRegion communicationRegion,
                       @NotNull final Set<CommunicationMedium> communicationMedia, @NotNull final Expanse expanse) {
        super(objectName, parent, communicationRegion, communicationMedia, expanse, ContextMenu.AREA_PLANNER_MENU);
    }

    @Override
    public void interact(@NotNull final User user) throws IllegalInteractionException {
        throwIfUserNotAvailable(user);
        throwIfInteractNotAllowed(user);
        // Öffne das Menü beim Benutzer.
        user.setCurrentInteractable(this);
        user.setMovable(false);
        user.send(SendAction.OPEN_MENU, this);
    }

    @Override
    public void executeMenuOption(@NotNull final User user, final int menuOption,
                                  @NotNull final String[] args) throws IllegalInteractionException, IllegalMenuActionException {
        if (executeCloseOption(user, menuOption)) {
            return;
        }

        if (menuOption == MENU_OPTION_REQUEST_ROLE) {
            // Stellt eine Anfrage zur Reservierung zum Erhalt der Rolle des Bereichsberechtigten im übergeordneten
            // Kontext.
            if (args.length < 2) {
                throw new IllegalMenuActionException("", "object.arguments.to-few");
            }
            if (user.getWorld() == null) {
                throw new IllegalStateException("Users world is not available");
            }

            LocalDateTime from;
            LocalDateTime to;
            try {
                from = LocalDateTime.parse(args[0]);
                to = LocalDateTime.parse(args[1]);
            } catch (DateTimeParseException e) {
                throw new IllegalMenuActionException("", "object.area-planner.invalid-time");
            }
            if (from.isAfter(to) || from.getMinute() != 0 || to.getMinute() != 0 || to.getHour() - from.getHour() != 1) {
                throw new IllegalMenuActionException("", "object.area-planner.invalid-time");
            }
            if (getParent().isReservedBy(user)) {
                throw new IllegalMenuActionException("", "object.area-planner.already-reserved", getParent().getContextName());
            }
            if (getParent().isReservedAt(from, to)) {
                throw new IllegalMenuActionException("", "object.area-planner.already-assigned", getParent().getContextName());
            }

            Map<UUID, User> receivers = UserAccountManager.getInstance().getUsersWithPermission(user.getWorld(), Permission.ASSIGN_AREA_MANAGER);

            receivers.values().forEach(receiver -> {
                AreaManagingRequest areaManagingRequest = new AreaManagingRequest(receiver, user, getParent(), from, to);
                receiver.addNotification(areaManagingRequest);
            });
        } else {
            throw new IllegalInteractionException("No valid menu option", user);
        }
    }
}