package model.context;

import org.jetbrains.annotations.NotNull;

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
}
