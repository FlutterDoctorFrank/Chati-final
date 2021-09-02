package model.context;

import org.jetbrains.annotations.NotNull;

/**
 * Ein Interface, welche der View Zugriff auf Parameter eines Kontextes ermöglicht.
 */
public interface IContextView {

    /**
     * Gibt die ID des Kontextes zurück.
     * @return ID des Kontextes.
     */
    @NotNull ContextID getContextId();

    /**
     * Gibt den Namen des Kontextes zurück.
     * @return Name des Kontextes.
     */
    @NotNull String getContextName();
}