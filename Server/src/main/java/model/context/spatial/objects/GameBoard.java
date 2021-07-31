package model.context.spatial.objects;

import controller.network.ClientSender;
import model.context.spatial.Menu;
import model.context.spatial.SpatialContext;
import model.user.User;

import java.util.HashSet;

/**
 * Eine Klasse, welche ein Objekt repräsentiert, durch welches ein Benutzer ein Minispiel spielen kann. Ist immer vom
 * Typ {@link model.context.spatial.SpatialContextType#OBJECT}.
 */
public class GameBoard extends SpatialContext {

    /**
     * Erzeugt eines neue Instanz des Gameboard.
     * @param objectName Name des Objekts.
     * @param parent Übergeordneter Kontext.
     */
    public GameBoard(String objectName, SpatialContext parent) {
        super(objectName, parent, Menu.GAME_BOARD_MENU, null, new HashSet<>());
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
