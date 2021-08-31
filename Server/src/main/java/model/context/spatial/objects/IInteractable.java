package model.context.spatial.objects;

import model.context.spatial.IArea;
import model.context.spatial.ContextMenu;
import org.jetbrains.annotations.NotNull;

public interface IInteractable extends IArea {

    /**
     * Gibt das Menü dieses Interaktionsobjekts zurück.
     * @return Menü dieses Interaktionsobjekts.
     */
    @NotNull ContextMenu getMenu();
}