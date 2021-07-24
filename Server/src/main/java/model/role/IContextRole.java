package model.role;

import model.context.Context;

import java.util.Set;

public interface IContextRole {
    public Context getContext();
    public Set<Role> getRoles();
}
