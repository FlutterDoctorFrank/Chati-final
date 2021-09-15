package view2.userInterface;

import model.context.ContextID;
import org.jetbrains.annotations.NotNull;

/**
 * Eine Klasse, die die Einträge von in einer Liste anzuzeigenden Kontexten repräsentiert.
 */
public class ContextEntry implements Comparable<ContextEntry> {

    private final ContextID contextId;
    private final String contextName;

    /**
     * Erzeugt eine neue Instanz eines ContextEntry.
     * @param contextId ID des Kontextes.
     * @param contextName Name des Kontextes.
     */
    public ContextEntry(@NotNull final ContextID contextId, @NotNull final String contextName) {
        this.contextId = contextId;
        this.contextName = contextName;
    }

    /**
     * Gibt die ID des Kontextes zurück.
     * @return ID des Kontextes.
     */
    public @NotNull ContextID getContextId() {
        return contextId;
    }

    /**
     * Gibt den Namen des Kontextes zurück.
     * @return Name des Kontextes.
     */
    public @NotNull String getName() {
        return contextName;
    }

    @Override
    public @NotNull String toString() {
        return contextName;
    }

    @Override
    public int compareTo(@NotNull final ContextEntry other) {
        return this.getName().compareTo(other.getName());
    }
}
