package model.context.spatial;

import model.context.IContext;

public interface IArea extends IContext {

    /**
     * Gibt die Musik, die innerhalb des Kontextes läuft, zurück.
     * @return Musik des Kontexts, wenn dieser eine hat, sonst null.
     */
    Music getMusic();
}