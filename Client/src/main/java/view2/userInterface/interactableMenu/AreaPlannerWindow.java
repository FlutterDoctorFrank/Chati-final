package view2.userInterface.interactableMenu;

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
 * Eine Klasse, welche das Menü des AreaPlanner repräsentiert.
 */
public class AreaPlannerWindow extends InteractableWindow {

    private static final float WINDOW_WIDTH = 600;
    private static final float WINDOW_HEIGHT = 200;

    /**
     * Erzeugt eine neue Instanz des AreaPlannerWindow.
     * @param interactableId ID des zugehörigen AreaPlanner.
     */
    public AreaPlannerWindow(@NotNull final ContextID interactableId) {
        super("window.title.area-planner", interactableId, ContextMenu.AREA_PLANNER_MENU, WINDOW_WIDTH, WINDOW_HEIGHT);

        infoLabel = new ChatiLabel("window.entry.not-implemented");

        ChatiTextButton confirmButton = new ChatiTextButton("menu.button.okay", true);
        confirmButton.addListener(new ClickListener() {
            @Override
            public void clicked(@NotNull final InputEvent event, final float x, final float y) {
                close();
            }
        });

        setModal(true);
        setMovable(false);

        Table container = new Table();
        container.defaults().height(ROW_HEIGHT).spaceBottom(SPACE).center().growX();
        infoLabel.setAlignment(Align.center, Align.center);
        container.add(infoLabel).row();
        Table buttonContainer = new Table();
        buttonContainer.defaults().colspan(2).height(ROW_HEIGHT).growX();
        buttonContainer.add(confirmButton);
        container.add(buttonContainer);
        add(container).padLeft(SPACE).padRight(SPACE).grow();

        // Translatable register
        translates.add(infoLabel);
        translates.add(confirmButton);
        translates.trimToSize();
    }

    @Override
    public void receiveResponse(final boolean success, @Nullable final MessageBundle messageBundle) {
    }
}
