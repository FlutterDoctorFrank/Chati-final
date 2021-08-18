package model.role;

import model.context.Context;
import model.user.User;
import org.jetbrains.annotations.NotNull;
import java.util.EnumSet;
import java.util.Set;

/**
 * Eine Klasse, welche die Zusammengehörigkeit von Rollen mit Kontexten repräsentiert.
 */
public class ContextRole implements IContextRole {

    /** Benutzer, der diese Rollen besitzt. */
    private final User user;

    /** Der Kontext, in dem ein Benutzer die Rollen besitzt. */
    private final Context context;

    /** Die Rollen, die ein Benutzer in dem entsprechenden Kontext besitzt. */
    private final Set<Role> roles;

    /**
     * Erzeugt eine Instanz der ContextRole mit einer Rolle.
     * @param context Kontext, in dem der Benutzer die Rolle besitzen soll.
     * @param role Rolle, die der Benutzer besitzen soll.
     */
    public ContextRole(@NotNull final User user, @NotNull final Context context, @NotNull final Role role) {
        this.user = user;
        this.context = context;
        this.roles = EnumSet.of(role);
    }

    /**
     * Erzeugt eine Instanz der ContextRole mit einer Menge von Rollen.
     * @param context Kontext, in dem der Benutzer die Rollen besitzen soll.
     * @param roles Rollen, die der Benutzer besitzen soll.
     */
    public ContextRole(@NotNull final User user, @NotNull final Context context, @NotNull final Set<Role> roles) {
        this.user = user;
        this.context = context;
        this.roles = roles;
    }

    @Override
    public @NotNull User getUser() {
        return user;
    }

    @Override
    public @NotNull Context getContext() {
        return context;
    }

    @Override
    public @NotNull Set<Role> getRoles() {
        return roles;
    }

    /**
     * Fügt eine Rolle hinzu.
     * @param role Hinzuzufügende Rolle.
     */
    public void addRole(@NotNull final Role role) {
        roles.add(role);
    }

    /**
     * Entfernt eine Rolle.
     * @param role Zu entfernende Rolle.
     */
    public void removeRole(@NotNull final Role role) {
        roles.remove(role);
    }

    /**
     * Überprüft, ob eine Rolle vorhanden ist.
     * @param role Zu überprüfende Rolle.
     * @return true, wenn die Rolle vorhanden ist, sonst false.
     */
    public boolean hasRole(@NotNull final Role role) {
        return roles.contains(role);
    }

    /**
     * Überprüft, ob eine Berechtigung vorhanden ist.
     * @param permission Zu überprüfende Berechtigung.
     * @return true, wenn die Berechtigung vorhanden ist, sonst false.
     */
    public boolean hasPermission(@NotNull final Permission permission) {
        return roles.stream().anyMatch(role -> role.hasPermission(permission));
    }
}