package model.context;

import model.context.spatial.SpatialContext;
import model.exception.ContextNotFoundException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Eine Klasse, welche einen Kontext der Anwendung repräsentiert. Ein Kontext legt den Umfang fest, in welchem Benutzer
 * Benachrichtigungen empfangen können, Rollen haben können, kommunizieren können und sonstige Benutzeraktionen
 * durchführen können.
 */
public class Context implements IContextView {

    /** Der Name des global Kontextes. */
    private static final String ROOT_NAME = "Global";

    /** Der globale Kontext. */
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
    public Context(@NotNull final String contextName, @Nullable final Context parent) {
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
    public Context(@NotNull final ContextID contextId, @NotNull final String contextName, @NotNull final Context parent) {
        this.contextId = contextId;
        this.contextName = contextName;
        this.parent = parent;
        this.children = new HashMap<>();
    }

    @Override
    public @NotNull ContextID getContextId() {
        return contextId;
    }

    @Override
    public @NotNull String getContextName() {
        return contextName;
    }

    /**
     * Fügt einen untergeordneten Kontext hinzu.
     * @param context Hinzuzufügender Kontext.
     */
    public void addChild(@NotNull final SpatialContext context) {
        if (!children.containsKey(context.getContextId())) {
            children.put(context.getContextId(), context);
        }
    }

    /**
     * Entfernt einen untergeordneten Kontext.
     * @param context Zu entfernender Kontext.
     */
    public void removeChild(@NotNull final SpatialContext context) {
        children.remove(context.getContextId());
    }

    /**
     * Gibt zurück, ob der Kontext einen untergeordneten Kontext mit der gegebenen ID besitzt.
     * @param contextId Zu prüfende Kontext-ID
     * @return true, wenn ein untergeordneter Kontext mit der ID existiert.
     */
    public boolean hasChild(@NotNull final ContextID contextId) {
        return children.containsKey(contextId);
    }

    /**
     * Entfernt alle untergeordneten Kontexte.
     */
    public void removeChildren() {
        children.clear();
    }

    /**
     * Gibt den übergeordneten Kontext zurück.
     * @return Übergeordneter Kontext.
     */
    public @Nullable Context getParent() {
        return parent;
    }

    /**
     * Gibt einen untergeordneten Kontext zu gegebener ID zurück.
     * @param contextId ID des gesuchten Kontextes.
     * @return Kontext mit der übergebenen ID.
     * @throws ContextNotFoundException wenn kein untergeordneter Kontext mit der ID gefunden wurde.
     */
    public @NotNull Context getContext(@NotNull final ContextID contextId) throws ContextNotFoundException {
        Context found = null;
        if (this.contextId.equals(contextId)) {
            return this;
        } else {
            for (SpatialContext child : children.values()) {
                try {
                    found = child.getContext(contextId);
                    break;
                } catch (ContextNotFoundException ignored) {
                }
            }
        }
        if (found == null) {
            throw new ContextNotFoundException("Context with this ID not found.", contextId);
        }
        return found;
    }

    /**
     * Gibt den globalen (übergeordnetsten) Kontext zurück.
     * @return Globaler Kontext.
     */
    public static @NotNull Context getGlobal() {
        if (global == null) {
            global = new Context(ROOT_NAME, null);
        }
        return global;
    }

    /**
     * Gibt alle untergeordneten Kontexte zurück.
     * @return Untergeordnete Kontexte.
     */
    public @NotNull Map<ContextID, SpatialContext> getChildren() {
        return children;
    }

    /**
     * Gibt zurück, ob dieser Kontext ein privater Raum ist.
     * @return true, wenn er ein privater Raum ist, sonst false.
     */
    public boolean isPrivate() {
        return false;
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