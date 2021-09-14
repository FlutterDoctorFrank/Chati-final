package view2.userInterface.interactableMenu;

import model.context.ContextID;
import model.context.spatial.ContextMenu;

/**
 * Ein Fenster, welches das Menü des GameBoard repräsentiert.
 */
public class GameBoardWindow extends InteractableWindow {

    /**
     * Erzeugt eine neue Instanz des GameBoardWindow.
     * @param gameBoardId ID des zugehörigen GameBoard.
     */
    public GameBoardWindow(ContextID gameBoardId) {
        super("Minispiel", gameBoardId, ContextMenu.GAME_BOARD_MENU);
    }

    @Override
    public void receiveResponse(boolean success, String messageKey) {
    }
}
