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
    public boolean equals(@Nullable final Object object) {
        if (this == object) {
            return true;
        }

        if (object == null || this.getClass() != object.getClass()) {
            return false;
        }

        return this.contextId.equals(((ContextID) object).contextId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.contextId);
    }
}
