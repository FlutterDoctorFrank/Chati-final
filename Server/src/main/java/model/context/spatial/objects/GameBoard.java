package model.context.spatial.objects;

import controller.network.ClientSender;
import model.context.spatial.Location;
import model.context.spatial.Menu;
import model.context.spatial.SpatialContext;
import model.user.User;

public class GameBoard extends SpatialContext {

    protected GameBoard(String contextName, SpatialContext parent, Menu menu, Location interactionLocation) {
        super(contextName, parent, menu, interactionLocation);
    }

    @Override
    public void interact(User user) {
        user.setCurrentInteractable(this);
        user.getClientSender().send(ClientSender.SendAction.OPEN_MENU, this);
    }

    @Override
    public void executeMenuOption(User user, int menuOption, String[] args) {
        // TODO
    }
}
