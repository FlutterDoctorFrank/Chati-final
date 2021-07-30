package model.role;

import model.context.Context;

import java.util.Set;

/**
 * Ein Interface, welche dem Controller Methoden zur Verwaltung von Rollen in einem Kontext bereitstellt. Wird von
 * {@link ContextRole} implementiert.
 */
public interface IContextRole {

    /**
     * Gibt den Kontext zurück, in dem die Rolle gilt.
     * @return Kontext der Rolle.
     */
    Context getContext();

    /**
     * Gibt die Rollen zurück, die in dem Kontext gelten.
     * @return Rollen im Kontext.
     */
    Set<Role> getRoles();
}
