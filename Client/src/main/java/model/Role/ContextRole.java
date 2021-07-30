package model.Role;

import model.Context.Context;
import model.Exceptions.IllegalActionException;
import model.role.Permission;
import model.role.Role;
import org.lwjgl.system.CallbackI;
import view.Screens.IModelObserver;

import java.util.Set;

/**
 * Eine Klasse, welche die Zusammengehörigkeit von Rollen mit Kontexten repräsentiert.
 */
public class ContextRole {
    private Context context;
    private Set<Role> roles;
    private IModelObserver iModelObserver;


    public ContextRole(Context context, Set<Role> roles) {
        this.context = context;
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
        };
    }

    /**
     * Entfernt eine Rolle.
     * @param role: Zu entfernende Rolle.
     * @throws IllegalActionException: wenn die Rolle nicht enthalten ist.
     */
    public void removeRole(Role role) throws IllegalActionException {
        if(!roles.remove(role)){
            throw new IllegalActionException("This Role isn't in roles-set!");
        };
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
