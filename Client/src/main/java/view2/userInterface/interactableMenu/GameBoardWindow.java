package view2.userInterface.interactableMenu;

import model.context.ContextID;
import model.context.spatial.ContextMenu;

public class GameBoardWindow extends InteractableWindow {

    public GameBoardWindow(ContextID gameBoardId) {
        super("Minispiel", gameBoardId, ContextMenu.GAME_BOARD_MENU);
    }

    @Override
    public void receiveResponse(boolean success, String messageKey) {
    }
}
