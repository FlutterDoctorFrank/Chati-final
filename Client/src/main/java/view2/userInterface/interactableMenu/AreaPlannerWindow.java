package view2.userInterface.interactableMenu;

import model.context.ContextID;
import model.context.spatial.ContextMenu;

/**
 * Eine Klasse, welche das Menü des AreaPlanner repräsentiert.
 */
public class AreaPlannerWindow extends InteractableWindow {

    /**
     * Erzeugt eine neue Instanz des AreaPlannerWindow.
     * @param interactableId ID des zugehörigen AreaPlanner.
     */
    public AreaPlannerWindow(ContextID interactableId) {
        super("Bereichsplaner", interactableId, ContextMenu.AREA_PLANNER_MENU);
    }

    @Override
    public void receiveResponse(boolean success, String messageKey) {
    }
}
