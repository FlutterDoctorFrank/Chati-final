package model.context;

import model.context.spatial.SpatialContext;
import model.exception.ContextNotFoundException;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Eine Klasse, welche einen Kontext der Anwendung repräsentiert. Ein Kontext legt den Umfang fest, in welchem Benutzer
 * Benachrichtigungen empfangen können, Rollen haben können, kommunizieren können und sonstige Benutzeraktionen
 * durchführen können.
 */
public class Context implements IContextView {

    /** Der Name des übergeordnetsten Kontextes. */
    private static final String ROOT_NAME = "Global";

    /** Der übergeordnetste Kontext. */
    private static Context global;

    /** Die eindeutige ID dieses Kontextes. */
    protected final ContextID contextId;

    /** Name des Kontextes. */
    protected final String contextName;

    /** Übergeordneter Kontext dieses Kontextes. */
    protected final Context parent;

    /** Untergeordnete (räumliche) Kontexte dieses Kontextes. */
    protected final Map<ContextID, SpatialContext> children;

    /**
     * Erzeugt eine neue Instanz eines Kontextes.
     * @param contextName Name des Kontextes.
     * @param parent Übergeordneter Kontext.
     */
    protected Context(String contextName, Context parent) {
        if (parent == null) {
            this.contextId = new ContextID(contextName);
        } else {
            this.contextId = new ContextID(parent.getContextId().getId().concat(".").concat(contextName));
        }
        this.contextName = contextName;
        this.parent = parent;
        this.children = new HashMap<>();
    }

    /**
     * Erzeugt eine neue Instanz eines Kontextes.
     * @param contextId ID des Kontextes.
     * @param contextName Name des Kontextes.
     * @param parent Übergeordneter Kontext.
     */
    public Context(ContextID contextId, String contextName, Context parent) {
        this.contextId = contextId;
        this.contextName = contextName;
        this.parent = parent;
        this.children = new HashMap<>();
    }

    @Override
    public ContextID getContextId() {
        return contextId;
    }

    @Override
    public String getContextName() {
        return contextName;
    }

    /**
     * Fügt einen untergeordneten Kontext hinzu.
     * @param context Hinzuzufügender Kontext.
     */
    public void addChild(SpatialContext context) {
        this.children.put(context.getContextId(), context);
    }

    /**
     * Entfernt einen untergeordneten Kontext.
     * @param context Zu entfernender Kontext.
     */
    public void removeChild(SpatialContext context) {
        this.children.remove(context.getContextId());
    }

    /**
     * Gibt den übergeordneten Kontext zurück.
     * @return Übergeordneter Kontext.
     */
    public Context getParent() {
        return parent;
    }

    /**
     * Gibt einen untergeordneten Kontext zu gegebener ID zurück.
     * @param contextId ID des gesuchten Kontextes.
     * @return Kontext mit der übergebenen ID.
     * @throws ContextNotFoundException wenn kein untergeordneter Kontext mit der ID gefunden wurde.
     */
    public Context getContext(ContextID contextId) throws ContextNotFoundException {
        Context found = null;
        if (this.contextId.equals(contextId)) {
            return this;
        } else {
            for (SpatialContext child : children.values()) {
                found = child.getContext(contextId);
                if (found != null) {
                    break;
                }
            }
        }
        if (parent == null && found == null) {
            throw new ContextNotFoundException("Context with this ID not found.", contextId);
        }
        return found;
    }

    /**
     * Gibt den übergeordnetsten Kontext zurück.
     * @return Übergeordnetster Kontext.
     */
    public static Context getGlobal() {
        if (global == null) {
            global = new Context(ROOT_NAME, null);
        }
        return global;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Context context = (Context) object;
        return Objects.equals(contextId, context.contextId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(contextId);
    }
}