package view2.userInterface.interactableMenu;

import model.MessageBundle;
import model.context.ContextID;
import model.context.spatial.ContextMenu;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Eine Klasse, welches das Menü des GameBoard repräsentiert.
 */
public class GameBoardWindow extends InteractableWindow {

    /**
     * Erzeugt eine neue Instanz des GameBoardWindow.
     * @param gameBoardId ID des zugehörigen GameBoard.
     */
    public GameBoardWindow(@NotNull final ContextID gameBoardId) {
        super("window.title.game-board", gameBoardId, ContextMenu.GAME_BOARD_MENU);
    }

    @Override
    public void receiveResponse(final boolean success, @Nullable final MessageBundle messageBundle) {

    }
}
