package model.context.spatial;

/**
 * Ein Interface, welches der View Zugriff auf die Koordinaten einer Position ermöglicht.
 */
public interface ILocationView {

    /**
     * Gibt die X-Koordinate dieser Position zurück.
     * @return X-Koordinate.
     */
    int getPosX();

    /**
     * Gibt die Y-Koordinate dieser Position zurück.
     * @return Y-Koordinate.
     */
    int getPosY();

    /**
     * Gibt den innersten räumlichen Kontext der Position im aktuellen Raum zurück.
     * @return Innerster räumlicher Kontext der Position.
     * @see SpatialContext
     */
    ISpatialContextView getArea();
}