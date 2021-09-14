package view2.userInterface.interactableMenu;

import controller.network.ServerSender;
import model.context.ContextID;
import model.context.spatial.ContextMenu;
import view2.Chati;
import view2.userInterface.ChatiWindow;
import view2.Response;

/**
 * Eine abstrakte Klasse, welche das Menü eines Interaktionsobjekts repräsentiert.
 */
public abstract class InteractableWindow extends ChatiWindow {

    protected static final int MENU_OPTION_CLOSE = 0;

    protected final ContextID interactableId;
    protected final ContextMenu interactableMenu;

    /**
     * Erzeugt eine neue Instanz des InteractableWindow.
     * @param title Anzuzeigender Titel.
     * @param interactableId ID des zugehörigen Interaktionsobjekts.
     * @param interactableMenu Typ des anzuzeigenden Menüs.
     */
    protected InteractableWindow(String title, ContextID interactableId, ContextMenu interactableMenu) {
        super(title);
        this.interactableId = interactableId;
        this.interactableMenu = interactableMenu;
    }

    /**
     * Gibt die ID des zugehörigen Interaktionsobjekts zurück.
     * @return ID des zugehörigen Interaktionsobjekts.
     */
    public ContextID getInteractableId() {
        return interactableId;
    }

    /**
     * Gibt den Typ des Menüs zurück.
     * @return Typ des Menüs.
     */
    public ContextMenu getInteractableMenu() {
        return interactableMenu;
    }

    /**
     * Verarbeitet eine Antwort auf eine durchgeführte Menüaktion.
     * @param success Information, ob die durchgeführte Aktion erfolgreich war.
     * @param messageKey Kennung der anzuzeigenden Nachricht.
     */
    public abstract void receiveResponse(boolean success, String messageKey);

    @Override
    public void close() {
        Chati.CHATI.getWorldScreen().setPendingResponse(Response.CLOSE_MENU);
        Chati.CHATI.send(ServerSender.SendAction.MENU_OPTION, interactableId, new String[0], MENU_OPTION_CLOSE);
    }
}
