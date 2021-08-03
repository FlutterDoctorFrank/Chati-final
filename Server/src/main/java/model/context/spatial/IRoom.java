package model.context.spatial;

/**
 * Ein Interface, welche dem Controller Methoden zur Verwaltung von Räumen bereitstellt. Wird von
 * {@link Room} implementiert.
 */
public interface IRoom extends IArea {

    /**
     * Gibt die Karte des Raumes zurück.
     * @return Karte des Raumes.
     */
    SpatialMap getMap();
}