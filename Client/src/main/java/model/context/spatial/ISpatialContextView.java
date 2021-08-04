package model.context.spatial;

import model.communication.CommunicationMedium;
import model.communication.CommunicationRegion;
import model.context.IContextView;

import java.util.Set;

/**
 * Ein Interface, welche der View Zugriff auf Parameter eines räumlichen Kontexts ermöglicht.
 */
public interface ISpatialContextView extends IContextView {

    /**
     * Gibt die geltende Kommunikationsform innerhalb des Kontexts zurück.
     * @return Die geltende Kommunikationsform.
     */
    CommunicationRegion getCommunicationRegion();

    /**
     * Gibt die Kommunikationsmedien zurück, mit denen im Kontext kommuniziert werden kann.
     * @return Die erlaubten Kommunikationsmedien.
     */
    Set<CommunicationMedium> getCommunicationMedia();

    /**
     * Gibt die Karte des Kontextes zurück.
     * @return Die Karte des Kontextes.
     */
    SpatialMap getMap();
}