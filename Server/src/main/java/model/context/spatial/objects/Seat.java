package model.context.spatial.objects;

import controller.network.ClientSender;
import model.MessageBundle;
import model.communication.message.TextMessage;
import model.context.Context;
import model.context.ContextID;
import model.context.spatial.Location;
import model.context.spatial.Menu;
import model.context.spatial.SpatialContext;
import model.exception.IllegalInteractionException;
import model.user.User;

import java.util.Map;

public class Seat extends SpatialContext {

    private User sittingUser;
    private Location sittingLocation;

    protected Seat(String contextName, SpatialContext parent, Menu menu, Location interactionLocation) {
        super(contextName, parent, menu, interactionLocation);
    }

    @Override
    public void interact(User user) {
        if (sittingUser != null) {
            if (sittingUser.equals(user)) {
                try {
                    user.move(interactionLocation.getPosX(), interactionLocation.getPosY());
                    user.setCurrentInteractable(null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            MessageBundle messageBundle = new MessageBundle("Dieser Platz ist bereits belegt.");
            TextMessage infoMessage = new TextMessage(messageBundle);
            user.getClientSender().send(ClientSender.SendAction.MESSAGE, infoMessage);
        }
        user.setCurrentInteractable(this);
        user.getClientSender().send(ClientSender.SendAction.OPEN_MENU, this);
    }

    @Override
    public void executeMenuOption(User user, int menuOption, String[] args) throws IllegalInteractionException {
        switch (menuOption) {
            case 0:
                user.setCurrentInteractable(null);
                user.getClientSender().send(ClientSender.SendAction.CLOSE_MENU, this);
                break;
            case 1:
                user.setCurrentInteractable(null);
                user.getClientSender().send(ClientSender.SendAction.CLOSE_MENU, this);
                try {
                    user.move(sittingLocation.getPosX(), sittingLocation.getPosY());
                    sittingUser = user;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            default:
                throw new IllegalInteractionException("No valid menu option", user);
        }
    }
}
