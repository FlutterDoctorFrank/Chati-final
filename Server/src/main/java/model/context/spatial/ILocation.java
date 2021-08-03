package model.context.spatial;

/**
 * Ein Interface, welches dem Controller Methoden zur Verwaltung von Position bereitstellt. Wird von {@link Location}
 * implementiert.
 */
public interface ILocation {

    /**
     * Gibt die X-Koordinate der Position im räumlichen Kontext zurück.
     * @return X-Koordinate der Position.
     */
    int getPosX();

    /**
     * Gibt die Y-Koordinate der Position im räumlichen Kontext zurück.
     * @return Y-Koordinate der Position.
     */
    int getPosY();

    /**
     * Gibt den räumlichen Kontext dieser Position zurück.
     * @return Raum der Position.
     */
    IRoom getRoom();

    /**
     * Gibt den innersten Bereich der Position im räumlichen Kontext zurück.
     * @return Innerster Bereich der Position.
     */
    IArea getArea();
}