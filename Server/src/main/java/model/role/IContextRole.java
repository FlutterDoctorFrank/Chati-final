package model.role;

import model.context.IContext;
import model.user.IUser;
import java.util.Set;

/**
 * Ein Interface, welche dem Controller Methoden zur Verwaltung von Rollen in einem Kontext bereitstellt. Wird von
 * {@link ContextRole} implementiert.
 */
public interface IContextRole {

    /**
     * Gibt den Benutzer zurück, dem diese Rollen gehören.
     * @return Benutzer, dem diese Rollen gehören.
     */
    IUser getUser();

    /**
     * Gibt den Kontext zurück, in dem die Rolle gilt.
     * @return Kontext der Rolle.
     */
    IContext getContext();

    /**
     * Gibt die Rollen zurück, die in dem Kontext gelten.
     * @return Rollen im Kontext.
     */
    Set<Role> getRoles();
}