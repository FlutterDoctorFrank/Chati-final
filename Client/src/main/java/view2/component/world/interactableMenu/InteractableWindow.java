package view2.component.world.interactableMenu;

import controller.network.ServerSender;
import model.context.ContextID;
import model.context.spatial.Menu;
import view2.Chati;
import view2.component.AbstractWindow;
import view2.component.Response;

public abstract class InteractableWindow extends AbstractWindow {

    protected static final int MENU_OPTION_CLOSE = 0;

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

    public abstract void showMessage(String messageKey);

    @Override
    public void close() {
        Chati.CHATI.getWorldScreen().setPendingResponse(Response.CLOSE_MENU);
        Chati.CHATI.getServerSender()
                .send(ServerSender.SendAction.MENU_OPTION, interactableId, new String[0], MENU_OPTION_CLOSE);
    }
}
