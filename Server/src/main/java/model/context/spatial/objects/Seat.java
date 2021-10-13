package model.context.spatial.objects;

import controller.network.ClientSender.SendAction;
import model.communication.CommunicationMedium;
import model.communication.CommunicationRegion;
import model.communication.message.TextMessage;
import model.context.spatial.Area;
import model.context.spatial.ContextMenu;
import model.context.spatial.Expanse;
import model.context.spatial.Location;
import model.exception.IllegalInteractionException;
import model.exception.IllegalMenuActionException;
import model.user.User;
import org.jetbrains.annotations.NotNull;
import java.util.Objects;
import java.util.Set;

/**
 * Eine Klasse, welche ein Objekt repräsentiert, durch welches der Benutzer an eine Stelle bewegt wird und mit anderen
 * Benutzern kommunizieren kann.
 */
public class Seat extends Interactable {

    /** Menü-Option, um den Benutzer auf den Platz zu setzen. */
    private static final int MENU_OPTION_SIT = 1;

    private final Location place;

    /**
     * Erzeugt eine neue Instanz des Seat.
     * @param objectName Name des Objekts.
     * @param parent Übergeordneter Kontext.
     * @param expanse Räumliche Ausdehnung des Kontexts.
     * @param communicationRegion Geltende Kommunikationsform.
     * @param communicationMedia Benutzbare Kommunikationsmedien.
     */
    public Seat(@NotNull final String objectName, @NotNull final Area parent,
                @NotNull final CommunicationRegion communicationRegion,
                @NotNull final Set<CommunicationMedium> communicationMedia, @NotNull final Expanse expanse,
                @NotNull final Location place) {
        super(objectName, parent, communicationRegion, communicationMedia, expanse, ContextMenu.SEAT_MENU);
        this.place = place;
    }

    @Override
    public void interact(@NotNull final User user) {
        throwIfUserNotAvailable(user);

        // Überprüfe, ob der Platz belegt ist.
        if (!containedUsers.isEmpty()) {
            // Überprüfe, ob der interagierende Benutzer den Platz belegt.
            if (contains(user)) {
                // Erlaube dem Benutzer, sich von dem Platz wegzubewegen.
                user.setCurrentInteractable(null);
                user.setMovable(true);
                user.teleport(getLegalPosition(Objects.requireNonNull(user.getLocation())));
            } else {
                // Teile dem Benutzer mit, dass der Platz bereits von einem anderen Benutzer belegt ist.
                TextMessage infoMessage = new TextMessage("object.seat.already-occupied");
                user.send(SendAction.MESSAGE, infoMessage);
            }
        } else {
            // Öffne das Menü beim Benutzer.
            user.setMovable(false);
            user.setCurrentInteractable(this);
            user.send(SendAction.OPEN_MENU, this);
        }
    }

    @Override
    public void executeMenuOption(@NotNull final User user, final int menuOption,
                                  @NotNull final String[] args) throws IllegalInteractionException, IllegalMenuActionException {
        if (executeCloseOption(user, menuOption)) {
            return;
        }

        if (menuOption == MENU_OPTION_SIT) { // Bewege den Benutzer auf den Platz.
            user.send(SendAction.CLOSE_MENU, this);

            if (!containedUsers.isEmpty()) {
                // Teile dem Benutzer mit, dass der Platz bereits von einem anderen Benutzer belegt ist.
                TextMessage infoMessage = new TextMessage("object.seat.already-occupied");
                user.send(SendAction.MESSAGE, infoMessage);
                return;
            }

            try {
                user.setMovable(false);
                user.teleport(place);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            throw new IllegalInteractionException("No valid menu option", user);
        }
    }
}