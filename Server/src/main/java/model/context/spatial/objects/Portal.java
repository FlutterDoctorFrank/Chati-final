package model.context.spatial.objects;

import controller.network.ClientSender;
import model.context.spatial.Location;
import model.context.spatial.Menu;
import model.context.spatial.SpatialContext;
import model.exception.IllegalInteractionException;
import model.user.User;

public class Portal extends SpatialContext {
    private Location destination;

    protected Portal(String contextName, SpatialContext parent, Menu menu, Location interactionLocation) {
        super(contextName, parent, menu, interactionLocation);
    }

    @Override
    public void interact(User user) {
        //user.setCurrentInteractable(this);
        user.getClientSender().send(ClientSender.SendAction.OPEN_MENU, this);
    }

    @Override
    public void executeMenuOption(User user, int menuOption, String[] args) throws IllegalInteractionException {
        switch (menuOption) {
            case 0:
                //user.setCurrentInteractable(null);
                user.getClientSender().send(ClientSender.SendAction.CLOSE_MENU, this);
                break;
            case 1:
                //user.setCurrentInteractable(null);
                user.getClientSender().send(ClientSender.SendAction.CLOSE_MENU, this);
                user.teleport(destination);
                break;
            default:
                throw new IllegalInteractionException("No valid menu option", user);
        }
    }
}