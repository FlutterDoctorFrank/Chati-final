package model.context.spatial.objects;

import controller.network.ClientSender;
import model.communication.CommunicationMedium;
import model.communication.CommunicationRegion;
import model.context.spatial.Location;
import model.context.spatial.Menu;
import model.context.spatial.SpatialContext;
import model.user.User;

import java.util.Set;

public class GameBoard extends SpatialContext {

    protected GameBoard(String contextName, SpatialContext parent, Menu menu, Location interactionLocation,
                        CommunicationRegion region, Set<CommunicationMedium> communicationMedia) {
        super(contextName, parent, menu, interactionLocation, region, communicationMedia);
    }

    @Override
    public void interact(User user) {
        //user.setCurrentInteractable(this);
        user.getClientSender().send(ClientSender.SendAction.OPEN_MENU, this);
    }

    @Override
    public void executeMenuOption(User user, int menuOption, String[] args) {
        // TODO
    }
}
