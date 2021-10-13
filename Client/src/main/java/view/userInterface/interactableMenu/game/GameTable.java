package view.userInterface.interactableMenu.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import org.jetbrains.annotations.NotNull;
import view.userInterface.actor.ChatiTable;
import view.userInterface.interactableMenu.GameBoardWindow;

public abstract class GameTable extends ChatiTable {

    private final GameBoardWindow window;

    public GameTable(@NotNull final GameBoardWindow window, @NotNull final String infoKey) {
        super(infoKey);

        this.window = window;
    }

    public void close() {
        window.close();
    }

    protected void enableButton(@NotNull final TextButton button) {
        button.setTouchable(Touchable.enabled);
        button.getLabel().setColor(Color.WHITE);
    }

    protected void enableSelectBox(@NotNull final SelectBox<?> selectBox) {
        selectBox.setTouchable(Touchable.enabled);
        selectBox.getStyle().fontColor = Color.WHITE;
    }

    protected void disableButton(@NotNull final TextButton button) {
        button.setTouchable(Touchable.disabled);
        button.getLabel().setColor(Color.DARK_GRAY);
    }

    protected void disableSelectBox(@NotNull final SelectBox<?> selectBox) {
        selectBox.setTouchable(Touchable.disabled);
        selectBox.getStyle().fontColor = Color.DARK_GRAY;
    }
}
