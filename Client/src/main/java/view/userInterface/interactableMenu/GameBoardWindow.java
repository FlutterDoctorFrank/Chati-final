package view.userInterface.interactableMenu;

import model.MessageBundle;
import model.context.ContextID;
import model.context.spatial.ContextMenu;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import view.userInterface.actor.ChatiTable;
import view.userInterface.interactableMenu.game.TicTacToeTable;

/**
 * Eine Klasse, welches das Menü des GameBoard repräsentiert.
 */
public class GameBoardWindow extends InteractableWindow {

    private static final float WINDOW_WIDTH = 750;
    private static final float WINDOW_HEIGHT = 400;

    private ChatiTable currentTable;

    /**
     * Erzeugt eine neue Instanz des GameBoardWindow.
     * @param gameBoardId ID des zugehörigen GameBoard.
     */
    public GameBoardWindow(@NotNull final ContextID gameBoardId) {
        super("window.title.game-board", gameBoardId, ContextMenu.GAME_BOARD_MENU, WINDOW_WIDTH, WINDOW_HEIGHT);

        setCurrentTable(new TicTacToeTable(this));

        // Layout
        setModal(true);
        setMovable(false);
    }

    /**
     * Setzt den momentan anzuzeigenden Inhalt.
     * @param table Container des anzuzeigenden Inhalts.
     */
    public void setCurrentTable(@NotNull final ChatiTable table) {
        removeActor(currentTable);
        currentTable = table;
        addActor(table);
    }

    @Override
    public void receiveResponse(final boolean success, @Nullable final MessageBundle messageBundle) {
    }

    @Override
    public void focus() {
    }
}
