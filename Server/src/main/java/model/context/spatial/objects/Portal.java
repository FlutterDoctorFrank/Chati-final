package model.context.spatial.objects;

import controller.network.ClientSender;
import model.context.spatial.Location;
import model.context.spatial.Menu;
import model.context.spatial.SpatialContext;
import model.exception.IllegalInteractionException;
import model.user.User;

import java.util.HashSet;

/**
 * Eine Klasse, welche ein Objekt repräsentiert, durch welches ein Benutzer zu einer festgelegten Position teleportiert
 * wird. Ist immer vom Typ {@link model.context.spatial.SpatialContextType#OBJECT}.
 */
public class Portal extends SpatialContext {

    /** Position, an die man teleportiert wird. */
    private Location destination;

    /**
     * Erzeugt eines neue Instanz des MusicPlayer.
     * @param objectName Name des Objekts.
     * @param parent Übergeordneter Kontext.
     */
    public Portal(String objectName, SpatialContext parent) {
        super(objectName, parent, Menu.PORTAL_MENU, null, new HashSet<>());
    }

    @Override
    public void interact(User user) {
        // Öffne das Menü beim Benutzer.
        user.setCurrentInteractable(this);
        user.setMoveable(false);
        user.getClientSender().send(ClientSender.SendAction.OPEN_MENU, this);
    }

    @Override
    public void executeMenuOption(User user, int menuOption, String[] args) throws IllegalInteractionException {
        switch (menuOption) {
            case 0: // Schließe das Menü beim Benutzer.
                user.setCurrentInteractable(null);
                user.setMoveable(true);
                user.getClientSender().send(ClientSender.SendAction.CLOSE_MENU, this);
                break;
            case 1: // Teleportiere den Benutzer zur festgelegten Position.
                user.setCurrentInteractable(null);
                user.setMoveable(true);
                user.getClientSender().send(ClientSender.SendAction.CLOSE_MENU, this);
                user.teleport(destination);
                break;
            default:
                throw new IllegalInteractionException("No valid menu option", user);
        }
    }
}