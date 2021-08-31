package model.context.spatial;

import org.jetbrains.annotations.NotNull;

/**
 * Ein Interface, welches dem Controller Methoden zur Verwaltung von Position bereitstellt. Wird von {@link Location}
 * implementiert.
 */
public interface ILocation {

    /**
     * Gibt die X-Koordinate der Position im räumlichen Kontext zurück.
     * @return X-Koordinate der Position.
     */
    float getPosX();

    /**
     * Gibt die Y-Koordinate der Position im räumlichen Kontext zurück.
     * @return Y-Koordinate der Position.
     */
    float getPosY();

    /**
     * Gibt die Richtung der Position im räumlichen Kontext zurück.
     * @return Richtung der Position.
     */
    @NotNull Direction getDirection();

    /**
     * Gibt den räumlichen Kontext dieser Position zurück.
     * @return Raum der Position.
     */
    @NotNull IRoom getRoom();

    /**
     * Gibt den innersten Bereich der Position im räumlichen Kontext zurück.
     * @return Innerster Bereich der Position.
     */
    @NotNull IArea getArea();
}