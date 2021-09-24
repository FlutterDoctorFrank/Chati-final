package model.context.spatial;

import model.context.IContext;
import org.jetbrains.annotations.Nullable;

public interface IArea extends IContext {

    /**
     * Gibt die Musik, die innerhalb des Kontextes läuft, zurück.
     * @return Musik des Kontexts, wenn dieser eine hat, sonst null.
     */
    @Nullable ContextMusic getMusic();

    /**
     * Gibt die Information zurück, ob eine laufende Musik wiederholt abgespielt wird.
     * @return Information, ob eine laufende Musik wiederholt abgespielt wird.
     */
    boolean isLooping();

    /**
     * Gibt die Information zurück, ob nach Abschluss eines Musikstücks ein zufälliges nächstes abgespielt wird.
     * @return Information, ob nach Abschluss eines Musikstücks ein zufälliges nächstes abgespielt wird.
     */
    boolean isRandom();
}