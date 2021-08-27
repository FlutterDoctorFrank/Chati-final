package view2.component.world.interactableMenu;

import controller.network.ServerSender;
import model.context.ContextID;
import model.context.spatial.Menu;
import view2.Chati;
import view2.component.AbstractWindow;

public abstract class InteractableWindow extends AbstractWindow {

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

    @Override
    public void close() {
        Chati.CHATI.getServerSender().send(ServerSender.SendAction.MENU_OPTION, interactableId, new String[0], 0);
    }
}
