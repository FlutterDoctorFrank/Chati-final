package model.context.spatial.objects;

import model.context.Context;
import model.context.ContextID;
import model.context.spatial.Location;
import model.context.spatial.SpatialContext;
import model.exception.IllegalInteractionException;
import model.user.User;

import java.util.Map;

public class Portal extends SpatialContext {
    private Location destination;

    protected Portal(String contextName, Context parent, Map<ContextID, SpatialContext> children) {
        super(contextName, parent, children);
    }

    @Override
    public void interact(User user) {
        user.setCurrentInteractable(this);
        // send menu open packet
    }

    @Override
    public void executeMenuOption(User user, int menuOption, String[] args) throws IllegalInteractionException {
        switch (menuOption) {
            case 0:
                user.setCurrentInteractable(null);
                // Send packet for menu close
                break;
            case 1:
                user.setCurrentInteractable(null);
                user.teleport(destination);
                break;
            default:
                throw new IllegalInteractionException("No valid menu option", user);
        }
    }
}