package view2.userInterface.interactableMenu;

import model.MessageBundle;
import model.context.ContextID;
import model.context.spatial.ContextMenu;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Eine Klasse, welche das Menü des AreaPlanner repräsentiert.
 */
public class AreaPlannerWindow extends InteractableWindow {

    /**
     * Erzeugt eine neue Instanz des AreaPlannerWindow.
     * @param interactableId ID des zugehörigen AreaPlanner.
     */
    public AreaPlannerWindow(@NotNull final ContextID interactableId) {
        super("window.title.area-planner", interactableId, ContextMenu.AREA_PLANNER_MENU);
    }

    @Override
    public void receiveResponse(final boolean success, @Nullable final MessageBundle messageBundle) {

    }
}
