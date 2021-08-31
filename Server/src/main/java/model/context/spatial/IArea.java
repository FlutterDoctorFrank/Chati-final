package model.context.spatial;

import model.context.IContext;
import org.jetbrains.annotations.Nullable;

public interface IArea extends IContext {

    /**
     * Gibt die Musik, die innerhalb des Kontextes läuft, zurück.
     * @return Musik des Kontexts, wenn dieser eine hat, sonst null.
     */
    @Nullable ContextMusic getMusic();
}