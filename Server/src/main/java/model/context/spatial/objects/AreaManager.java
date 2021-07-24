package model.context.spatial.objects;

import model.context.Context;
import model.context.ContextID;
import model.context.spatial.SpatialContext;
import model.user.User;

import java.util.Map;

public class AreaManager extends SpatialContext {

    protected AreaManager(String contextName, Context parent, Map<ContextID, SpatialContext> children) {
        super(contextName, parent, children);
    }

    @Override
    public void interact(User user) {
        // TODO
    }

    @Override
    public void executeMenuOption(User user, int menuOption, String[] args) {
        // TODO
    }
}
