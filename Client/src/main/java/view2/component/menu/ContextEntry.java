package view2.component.menu;

import model.context.ContextID;

public class ContextEntry {

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
}
