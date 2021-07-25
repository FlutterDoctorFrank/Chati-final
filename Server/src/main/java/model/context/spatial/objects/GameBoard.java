package model.context.spatial.objects;

import model.context.Context;
import model.context.ContextID;
import model.context.spatial.SpatialContext;
import model.user.User;

import java.util.Map;

public class GameBoard extends SpatialContext {

    protected GameBoard(String contextName, Context parent, Map<ContextID, SpatialContext> children) {
        super(contextName, parent, children);
    }

    @Override
    public void interact(User user) {
        user.setCurrentInteractable(this);
        // send menu open packet
    }

    @Override
    public void executeMenuOption(User user, int menuOption, String[] args) {
        // TODO
    }
}
