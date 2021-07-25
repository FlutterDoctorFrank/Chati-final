package model.role;

import model.context.Context;

import java.util.EnumSet;
import java.util.Set;

public class ContextRole implements IContextRole {
    Context context;
    Set<Role> roles;

    public ContextRole(Context context, Role role) {
        this.context = context;
        this.roles = EnumSet.of(role);
    }

    @Override
    public Context getContext() {
        return context;
    }

    @Override
    public Set<Role> getRoles() {
        return roles;
    }

    public void addRole(Role role) {
        roles.add(role);
    }

    public void removeRole(Role role) {
        roles.remove(role);
    }

    public boolean hasRole(Role role) {
        return roles.contains(role);
    }

    public boolean hasPermission(Permission permission) {
        return roles.stream().anyMatch(role -> role.hasPermission(permission));
    }
}
