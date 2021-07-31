package model.context.spatial.objects;

import controller.network.ClientSender;
import model.MessageBundle;
import model.communication.message.TextMessage;
import model.context.spatial.Menu;
import model.context.spatial.SpatialContext;
import model.exception.IllegalInteractionException;
import model.user.User;

import java.util.HashSet;

/**
 * Eine Klasse, welche ein Objekt repräsentiert, durch welches der Benutzer an eine Stelle bewegt wird und mit anderen
 * Benutzern kommunizieren kann. Hat immer den Typ {@link model.context.spatial.SpatialContextType#OBJECT}.
 */
public class Seat extends SpatialContext {

    /**
     * Erzeugt eines neue Instanz des Seat.
     * @param objectName Name des Objekts.
     * @param parent Übergeordneter Kontext.
     */
    public Seat(String objectName, SpatialContext parent) {
        super(objectName, parent, Menu.SEAT_MENU, null, new HashSet<>());
    }

    @Override
    public void interact(User user) {
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
                user.getClientSender().send(ClientSender.SendAction.MESSAGE, infoMessage);
            }
        } else {
            // Öffne das Menü beim Benutzer.
            user.setCurrentInteractable(this);
            user.setMoveable(false);
            user.getClientSender().send(ClientSender.SendAction.OPEN_MENU, this);
        }
    }

    @Override
    public void executeMenuOption(User user, int menuOption, String[] args) throws IllegalInteractionException {
        switch (menuOption) {
            case 0: // Schließe das Menü beim Benutzer.
                user.setCurrentInteractable(null);
                user.setMoveable(true);
                user.getClientSender().send(ClientSender.SendAction.CLOSE_MENU, this);
                break;
            case 1: // Bewege den Benutzer auf den Platz.
                user.getClientSender().send(ClientSender.SendAction.CLOSE_MENU, this);
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
