package model.context;

import model.context.spatial.Music;

/**
 * Ein Interface, welche der View Zugriff auf Parameter eines Kontextes ermöglicht.
 */
public interface IContextView {
    /**
     * Gibt die ID des Kontextes zurück.
     * @return ID des Kontextes.
     */
    ContextID getContextId();

    /**
     * Gibt den Namen des Kontextes zurück.
     * @return Name des Kontextes.
     */
    String getContextName();
}