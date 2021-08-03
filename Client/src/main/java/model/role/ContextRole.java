package model.role;

import model.exception.IllegalActionException;

import java.util.Set;

/**
 * Eine Klasse, welche die Zusammengehörigkeit von Rollen mit Kontexten repräsentiert.
 */
public class ContextRole {
    /**
     * enthaltene Rollen
     */
    private final Set<Role> roles;


    public ContextRole(Set<Role> roles) {
        this.roles = roles;
    }


    /**
     * Fügt eine Rolle hinzu.
     * @param role: Hinzuzufügende Rolle.
     * @throws IllegalActionException : wenn die Rolle bereits enthalten ist.
     */
    public void addRole(Role role) throws IllegalActionException{
        if(!roles.add(role)){
            throw new IllegalActionException("This Role is already added!");
        }
    }

    /**
     * Entfernt eine Rolle.
     * @param role: Zu entfernende Rolle.
     * @throws IllegalActionException: wenn die Rolle nicht enthalten ist.
     */
    public void removeRole(Role role) throws IllegalActionException {
        if(!roles.remove(role)){
            throw new IllegalActionException("This Role isn't in roles-set!");
        }
    }

    /**
     * Gibt zurück, ob die Rolle mit Kontext eine Berechtigung enthält.
     * @param permission: Berechtigung, nach der gesucht wird.
     * @return Gibt true zurück, wenn die Berechtigung gefunden wird, sonst false
     */
    public boolean hasPermission(Permission permission){
        return roles.stream().anyMatch(role -> role.hasPermission(permission));
    }

    public Set<Role> getRoles() {
        return roles;
    }
}
