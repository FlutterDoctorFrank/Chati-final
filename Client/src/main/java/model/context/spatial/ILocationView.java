package model.context.spatial;

import org.jetbrains.annotations.NotNull;

/**
 * Ein Interface, welches der View Zugriff auf die Koordinaten einer Position ermöglicht.
 */
public interface ILocationView {

    /**
     * Gibt die X-Koordinate dieser Position zurück.
     * @return X-Koordinate.
     */
    float getPosX();

    /**
     * Gibt die Y-Koordinate dieser Position zurück.
     * @return Y-Koordinate.
     */
    float getPosY();

    /**
     * Gibt die Richtung dieser Position zurück.
     * @return Richtung dieser Position.
     */
    @NotNull Direction getDirection();

    /**
     * Gibt den innersten räumlichen Kontext der Position im aktuellen Raum zurück.
     * @return Innerster räumlicher Kontext der Position.
     * @see SpatialContext
     */
    @NotNull ISpatialContextView getArea();
}