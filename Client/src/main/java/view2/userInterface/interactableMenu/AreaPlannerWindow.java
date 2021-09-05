package view2.userInterface.interactableMenu;

import model.context.ContextID;
import model.context.spatial.ContextMenu;

public class AreaPlannerWindow extends InteractableWindow {

    public AreaPlannerWindow(ContextID interactableId) {
        super("Bereichsplaner", interactableId, ContextMenu.AREA_PLANNER_MENU);
    }

    @Override
    public void receiveResponse(boolean success, String messageKey) {
    }
}
