package model.role;

import model.context.Context;
import model.user.User;

import java.util.EnumSet;
import java.util.Set;

/**
 * Eine Klasse, welche die Zusammengehörigkeit von Rollen mit Kontexten repräsentiert.
 */
public class ContextRole implements IContextRole {

    /** Benutzer, der diese Rollen besitzt. */
    private User user;

    /** Der Kontext, in dem ein Benutzer die Rollen besitzt. */
    private final Context context;

    /** Die Rollen, die ein Benutzer in dem entsprechenden Kontext besitzt. */
    private final Set<Role> roles;

    /**
     * Erzeugt eine Instanz der ContextRole mit einer Rolle.
     * @param context Kontext, in dem der Benutzer die Rolle besitzen soll.
     * @param role Rolle, die der Benutzer besitzen soll.
     */
    public ContextRole(User user, Context context, Role role) {
        this.user = user;
        this.context = context;
        this.roles = EnumSet.of(role);
    }

    /**
     * Erzeugt eine Instanz der ContextRole mit einer Menge von Rollen.
     * @param context Kontext, in dem der Benutzer die Rollen besitzen soll.
     * @param roles Rollen, die der Benutzer besitzen soll.
     */
    public ContextRole(Context context, Set<Role> roles) {
        this.context = context;
        this.roles = roles;
    }

    @Override
    public User getUser() {
        return user;
    }

    @Override
    public Context getContext() {
        return context;
    }

    @Override
    public Set<Role> getRoles() {
        return roles;
    }

    /**
     * Fügt eine Rolle hinzu.
     * @param role Hinzuzufügende Rolle.
     */
    public void addRole(Role role) {
        roles.add(role);
    }

    /**
     * Entfernt eine Rolle.
     * @param role Zu entfernende Rolle.
     */
    public void removeRole(Role role) {
        roles.remove(role);
    }

    /**
     * Überprüft, ob eine Rolle vorhanden ist.
     * @param role Zu überprüfende Rolle.
     * @return true, wenn die Rolle vorhanden ist, sonst false.
     */
    public boolean hasRole(Role role) {
        return roles.contains(role);
    }

    /**
     * Überprüft, ob eine Berechtigung vorhanden ist.
     * @param permission Zu überprüfende Berechtigung.
     * @return true, wenn die Berechtigung vorhanden ist, sonst false.
     */
    public boolean hasPermission(Permission permission) {
        return roles.stream().anyMatch(role -> role.hasPermission(permission));
    }
}
