package model.context.spatial.objects;

import controller.network.ClientSender;
import model.communication.CommunicationMedium;
import model.communication.CommunicationRegion;
import model.context.spatial.Area;
import model.context.spatial.Expanse;
import model.context.spatial.Interactable;
import model.context.spatial.Menu;
import model.user.User;

import java.util.Set;

/**
 * Eine Klasse, welche ein Objekt repräsentiert, durch welches ein Benutzer ein Minispiel spielen kann. Ist immer vom
 * Typ {@link model.context.spatial.SpatialContextType#OBJECT}.
 */
public class GameBoard extends Interactable {

    /**
     * Erzeugt eines neue Instanz des Gameboard.
     * @param objectName Name des Objekts.
     * @param parent Übergeordneter Kontext.
     * @param expanse Räumliche Ausdehnung des Kontexts.
     * @param communicationRegion Geltende Kommunikationsform.
     * @param communicationMedia Benutzbare Kommunikationsmedien.
     */
    public GameBoard(String objectName, Area parent, CommunicationRegion communicationRegion,
                     Set<CommunicationMedium> communicationMedia, Expanse expanse) {
        super(objectName, parent, communicationRegion, communicationMedia, expanse, Menu.GAME_BOARD_MENU);
    }

    @Override
    public void interact(User user) {
        // Öffne das Menü beim Benutzer.
        user.setCurrentInteractable(this);
        user.setMoveable(false);
        user.getClientSender().send(ClientSender.SendAction.OPEN_MENU, this);
    }

    @Override
    public void executeMenuOption(User user, int menuOption, String[] args) {
        // TODO
    }
}
