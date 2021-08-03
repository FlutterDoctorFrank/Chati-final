package model.context.spatial.objects;

import model.context.spatial.IArea;
import model.context.spatial.Menu;

public interface IInteractable extends IArea {

    /**
     * Gibt das Menü dieses Interaktionsobjekts zurück.
     * @return Menü dieses Interaktionsobjekts.
     */
    Menu getMenu();
}