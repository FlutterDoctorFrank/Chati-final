package view.userInterface.interactableMenu;

import controller.network.ServerSender;
import model.MessageBundle;
import model.context.ContextID;
import model.context.spatial.ContextMenu;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import view.Chati;
import view.userInterface.actor.ChatiWindow;

/**
 * Eine abstrakte Klasse, welche das Menü eines Interaktionsobjekts repräsentiert.
 */
public abstract class InteractableWindow extends ChatiWindow {

    protected static final int MENU_OPTION_CLOSE = 0;

    protected final ContextID interactableId;
    protected final ContextMenu interactableMenu;

    /**
     * Erzeugt eine neue Instanz des InteractableWindow.
     * @param titleKey Kennung des anzuzeigenden Titels.
     * @param interactableId ID des zugehörigen Interaktionsobjekts.
     * @param interactableMenu Typ des anzuzeigenden Menüs.
     */
    protected InteractableWindow(@NotNull final String titleKey, @NotNull final ContextID interactableId,
                                 @NotNull final ContextMenu interactableMenu) {
        this(titleKey, interactableId, interactableMenu, -1f, -1f);
    }

    /**
     * Erzeugt eine neue Instanz des InteractableWindow.
     * @param titleKey Kennung des anzuzeigenden Titels.
     * @param interactableId ID des zugehörigen Interaktionsobjekts.
     * @param interactableMenu Typ des anzuzeigenden Menüs.
     * @param width Die Breite des InteractableWindows.
     * @param height Die Höhe des InteractableWindows.
     */
    protected InteractableWindow(@NotNull final String titleKey, @NotNull final ContextID interactableId,
                                 @NotNull final ContextMenu interactableMenu, final float width, final float height) {
        super(titleKey, width, height);
        this.interactableId = interactableId;
        this.interactableMenu = interactableMenu;
    }

    /**
     * Gibt die ID des zugehörigen Interaktionsobjekts zurück.
     * @return ID des zugehörigen Interaktionsobjekts.
     */
    public @NotNull ContextID getInteractableId() {
        return interactableId;
    }

    /**
     * Gibt den Typ des Menüs zurück.
     * @return Typ des Menüs.
     */
    public @NotNull ContextMenu getInteractableMenu() {
        return interactableMenu;
    }

    /**
     * Verarbeitet eine Antwort auf eine durchgeführte Menüaktion.
     * @param success Information, ob die durchgeführte Aktion erfolgreich war.
     * @param messageBundle Kennung der anzuzeigenden Nachricht.
     */
    public abstract void receiveResponse(final boolean success, @Nullable final MessageBundle messageBundle);

    @Override
    public void close() {
        Chati.CHATI.send(ServerSender.SendAction.MENU_OPTION, interactableId, new String[0], MENU_OPTION_CLOSE);
    }
}
