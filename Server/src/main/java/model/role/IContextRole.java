package model.role;

import model.context.Context;

import java.util.Set;

/**
 * Eine Schnittstelle, welche dem Controller Methoden zur Verwaltung von Rollen in einem Kontext bereitstellt. Wird von
 * {@link ContextRole} implementiert.
 */
public interface IContextRole {

    /**
     * Gibt den Kontext zurück, in dem die Rolle gilt.
     * @return Kontext der Rolle.
     */
    public Context getContext();

    /**
     * Gibt die Rollen zurück, die in dem Kontext gelten.
     * @return Rollen im Kontext.
     */
    public Set<Role> getRoles();
}
