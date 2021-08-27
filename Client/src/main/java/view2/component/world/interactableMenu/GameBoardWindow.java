package view2.component.world.interactableMenu;

import model.context.ContextID;
import model.context.spatial.Menu;

public class GameBoardWindow extends InteractableWindow {

    public GameBoardWindow(ContextID gameBoardId) {
        super("Minispiel", gameBoardId, Menu.GAME_BOARD_MENU);
    }

    @Override
    protected void create() {

    }

    @Override
    protected void setLayout() {

    }

    @Override
    public void showMessage(String messageKey) {

    }
}
