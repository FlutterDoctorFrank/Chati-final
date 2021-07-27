package model.Context;

import model.Context.Spatial.CommunicationMedium;
import model.Context.Spatial.CommunicationRegion;
import model.context.ContextID;

import java.util.Set;

/**
 * Eine Schnittstelle, welche der View Zugriff auf Parameter eines Kontextes ermöglicht.
 */
public interface IContextView {
    /**
     * Gibt die ID des Kontextes zurück.
     * @return ID des Kontextes.
     */
    public ContextID getContextId();

    /**
     * Gibt den Namen des Kontextes zurück.
     * @return Name des Kontextes.
     */
    public String getContextName();

    /**
     * Gibt die im räumlichen Kontext abzuspielende Musik zurück.
     * @return Die abzuspielende Musik.
     */
    public Music getMusic();

    /**
     * Gibt die geltende Kommunikationsform innerhalb des räumlichen Kontexts zurück.
     * @return Die geltende Kommunikationsform.
     */
    public CommunicationRegion getCommunicationRegion();

    /**
     * Gibt die Kommunikationsmedien zurück, mit denen im räumlichen Kontext kommuniziert werden kann.
     * @return Die erlaubten Kommunikationsmedien.
     */
    public Set<CommunicationMedium> getCommunicationMedia();
}
