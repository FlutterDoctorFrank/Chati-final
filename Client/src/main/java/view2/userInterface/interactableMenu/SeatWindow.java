package view2.userInterface.interactableMenu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import controller.network.ServerSender;
import model.MessageBundle;
import model.context.ContextID;
import model.context.spatial.ContextMenu;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import view2.Chati;
import view2.userInterface.ChatiLabel;
import view2.userInterface.ChatiTextButton;

/**
 * Eine Klasse, welche das Menü des Seat repräsentiert.
 */
public class SeatWindow extends InteractableWindow {

    private static final int MENU_OPTION_SIT = 1;

    private static final float WINDOW_WIDTH = 550;
    private static final float WINDOW_HEIGHT = 275;

    /**
     * Erzeugt eine neue Instanz des SeatWindow.
     * @param seatId ID des zugehörigen Seat.
     */
    public SeatWindow(@NotNull final ContextID seatId) {
        super("window.title.seat", seatId, ContextMenu.SEAT_MENU);

        infoLabel = new ChatiLabel("window.entry.seat");

        ChatiTextButton confirmButton = new ChatiTextButton("menu.button.yes", true);
        confirmButton.addListener(new ClickListener() {
            @Override
            public void clicked(@NotNull final InputEvent event, final float x, final float y) {
                Chati.CHATI.send(ServerSender.SendAction.MENU_OPTION, interactableId, new String[0], MENU_OPTION_SIT);
            }
        });

        ChatiTextButton cancelButton = new ChatiTextButton("menu.button.no", true);
        cancelButton.addListener(new ClickListener() {
            @Override
            public void clicked(@NotNull final InputEvent event, final float x, final float y) {
                close();
            }
        });

        // Layout
        setModal(true);
        setMovable(false);
        setPosition((Gdx.graphics.getWidth() - WINDOW_WIDTH) / 2f, (Gdx.graphics.getHeight() - WINDOW_HEIGHT) / 2f);
        setWidth(WINDOW_WIDTH);
        setHeight(WINDOW_HEIGHT);

        Table container = new Table();
        container.defaults().height(ROW_HEIGHT).spaceBottom(SPACING).center().growX();
        infoLabel.setAlignment(Align.center, Align.center);
        container.add(infoLabel).row();
        Table buttonContainer = new Table();
        buttonContainer.defaults().colspan(2).height(ROW_HEIGHT).growX();
        buttonContainer.add(confirmButton).padRight(SPACING / 2);
        buttonContainer.add(cancelButton).padLeft(SPACING / 2);
        container.add(buttonContainer);
        add(container).padLeft(SPACING).padRight(SPACING).grow();

        // Translatable register
        translates.add(infoLabel);
        translates.add(confirmButton);
        translates.add(cancelButton);
        translates.trimToSize();
    }

    @Override
    public void receiveResponse(final boolean success, @Nullable final MessageBundle messageBundle) {
    }
}
