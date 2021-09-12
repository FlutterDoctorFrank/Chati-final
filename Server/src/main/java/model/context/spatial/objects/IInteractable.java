package model.context.spatial.objects;

import model.context.spatial.ContextMenu;
import model.context.spatial.IArea;
import org.jetbrains.annotations.NotNull;

/**
 * Eine Schnittstelle, welche dem Controller Methoden zum Zugriff auf Objekte bereitstellt.
 */
public interface IInteractable extends IArea {

    /**
     * Gibt das Menü dieses Interaktionsobjekts zurück.
     * @return Menü dieses Interaktionsobjekts.
     */
    @NotNull ContextMenu getMenu();
}