package view2.component.world.interactableMenu;

import model.context.ContextID;
import model.context.spatial.Menu;
import view2.component.ChatiWindow;

public abstract class InteractableWindow extends ChatiWindow {

    protected final ContextID interactableId;
    protected final Menu interactableMenu;

    protected InteractableWindow(String title, ContextID interactableId, Menu interactableMenu) {
        super(title);
        this.interactableId = interactableId;
        this.interactableMenu = interactableMenu;
    }

    public ContextID getInteractableId() {
        return interactableId;
    }

    public Menu getInteractableMenu() {
        return interactableMenu;
    }
}
