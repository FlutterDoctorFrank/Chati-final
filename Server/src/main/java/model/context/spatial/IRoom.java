package model.context.spatial;

import org.jetbrains.annotations.NotNull;

/**
 * Ein Interface, welche dem Controller Methoden zur Verwaltung von Räumen bereitstellt. Wird von
 * {@link Room} implementiert.
 */
public interface IRoom extends IArea {

    /**
     * Gibt die Karte des Raumes zurück.
     * @return Karte des Raumes.
     */
    @NotNull ContextMap getMap();

    /**
     * Gibt zurück, ob dieser Raum ein privater Raum ist.
     * @return true, wenn der Raum privat ist, sonst false.
     */
    boolean isPrivate();
}