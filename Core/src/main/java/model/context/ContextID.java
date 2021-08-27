package model.context;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Dient der eindeutigen Identifikation eines Kontextes.
 */
public class ContextID {

    private final String contextId;

    public ContextID(@NotNull final String contextId) {
        this.contextId = contextId;
    }

    public @NotNull String getId() {
        return this.contextId;
    }

    @Override
    public @NotNull String toString() {
        return this.contextId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ContextID contextID = (ContextID) o;
        return Objects.equals(contextId, contextID.contextId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.contextId);
    }
}
