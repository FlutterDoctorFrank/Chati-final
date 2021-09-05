package view2.userInterface.menu;

import model.context.ContextID;
import org.jetbrains.annotations.NotNull;

public class ContextEntry implements Comparable<ContextEntry> {

    private final ContextID contextId;
    private final String contextName;

    public ContextEntry(ContextID contextId, String contextName) {
        this.contextId = contextId;
        this.contextName = contextName;
    }

    public ContextID getContextId() {
        return contextId;
    }

    public String getName() {
        return contextName;
    }

    @Override
    public String toString() {
        return contextName;
    }

    @Override
    public int compareTo(@NotNull ContextEntry other) {
        return this.getName().compareTo(other.getName());
    }
}
