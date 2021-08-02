package model.context.spatial.objects;

import controller.network.ClientSender;
import model.communication.CommunicationMedium;
import model.communication.CommunicationRegion;
import model.context.spatial.*;
import model.exception.IllegalInteractionException;
import model.user.User;

import java.util.Set;

/**
 * Eine Klasse, welche ein Objekt repräsentiert, durch welches ein Benutzer zu einer festgelegten Position teleportiert
 * wird. Ist immer vom Typ {@link model.context.spatial.SpatialContextType#OBJECT}.
 */
public class Portal extends Interactable {

    /** Position, an die man teleportiert wird. */
    private Location destination;

    /**
     * Erzeugt eines neue Instanz des MusicPlayer.
     * @param objectName Name des Objekts.
     * @param parent Übergeordneter Kontext.
     * @param expanse Räumliche Ausdehnung des Kontexts.
     * @param communicationRegion Geltende Kommunikationsform.
     * @param communicationMedia Benutzbare Kommunikationsmedien.
     */
    public Portal(String objectName, Area parent, CommunicationRegion communicationRegion,
                  Set<CommunicationMedium> communicationMedia, Expanse expanse) {
        super(objectName, parent, communicationRegion, communicationMedia, expanse, Menu.PORTAL_MENU);
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