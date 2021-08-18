package model.context.spatial.objects;

import controller.network.ClientSender;
import model.MessageBundle;
import model.communication.CommunicationMedium;
import model.communication.CommunicationRegion;
import model.communication.message.TextMessage;
import model.context.spatial.Area;
import model.context.spatial.Expanse;
import model.context.spatial.Menu;
import model.exception.IllegalInteractionException;
import model.user.User;
import org.jetbrains.annotations.NotNull;
import java.util.Set;

/**
 * Eine Klasse, welche ein Objekt repräsentiert, durch welches der Benutzer an eine Stelle bewegt wird und mit anderen
 * Benutzern kommunizieren kann. Hat immer den Typ {@link model.context.spatial.SpatialContextType#OBJECT}.
 */
public class Seat extends Interactable {

    /**
     * Erzeugt eines neue Instanz des Seat.
     * @param objectName Name des Objekts.
     * @param parent Übergeordneter Kontext.
     * @param expanse Räumliche Ausdehnung des Kontexts.
     * @param communicationRegion Geltende Kommunikationsform.
     * @param communicationMedia Benutzbare Kommunikationsmedien.
     */
    public Seat(@NotNull final String objectName, @NotNull final Area parent,
                @NotNull final CommunicationRegion communicationRegion,
                @NotNull final Set<CommunicationMedium> communicationMedia, @NotNull final Expanse expanse) {
        super(objectName, parent, communicationRegion, communicationMedia, expanse, Menu.SEAT_MENU);
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
                user.setMoveable(true);
            } else {
                // Teile dem Benutzer mit, dass der Platz bereits von einem anderen Benutzer belegt ist.
                MessageBundle messageBundle = new MessageBundle("Dieser Platz ist bereits belegt.");
                TextMessage infoMessage = new TextMessage(messageBundle);
                user.send(ClientSender.SendAction.MESSAGE, infoMessage);
            }
        } else {
            // Öffne das Menü beim Benutzer.
            user.setCurrentInteractable(this);
            user.setMoveable(false);
            user.send(ClientSender.SendAction.OPEN_MENU, this);
        }
    }

    @Override
    public void executeMenuOption(@NotNull final User user, final int menuOption,
                                  @NotNull final String[] args) throws IllegalInteractionException {
        throwIfUserNotAvailable(user);

        switch (menuOption) {
            case 0: // Schließe das Menü beim Benutzer.
                user.setCurrentInteractable(null);
                user.setMoveable(true);
                user.send(ClientSender.SendAction.CLOSE_MENU, this);
                break;
            case 1: // Bewege den Benutzer auf den Platz.
                user.send(ClientSender.SendAction.CLOSE_MENU, this);
                try {
                    user.setMoveable(true);
                    user.move(expanse.getBottomLeft().getPosX(), expanse.getBottomLeft().getPosY());
                    user.setMoveable(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            default:
                throw new IllegalInteractionException("No valid menu option", user);
        }
    }
}