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
        // Check if user is close to object
        // Check if user currently interacts with another object
        // Send Packet for menu open
    }

    @Override
    public void executeMenuOption(User user, int menuOption, String[] args) throws IllegalInteractionException {
        if (!user.isInteractingWith(this)) {
            throw new IllegalInteractionException("User has not opened the menu of this object.", user);
        }
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