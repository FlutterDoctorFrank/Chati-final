package model.context.spatial.objects;

import model.MessageBundle;
import model.communication.message.TextMessage;
import model.context.Context;
import model.context.ContextID;
import model.context.spatial.Location;
import model.context.spatial.SpatialContext;
import model.exception.IllegalInteractionException;
import model.user.User;

import java.util.Map;

public class Seat extends SpatialContext {

    private User sittingUser;
    private Location leaveLocation;

    protected Seat(String contextName, Context parent, Map<ContextID, SpatialContext> children) {
        super(contextName, parent, children);
    }

    @Override
    public void interact(User user) {
        if (sittingUser != null) {
            if (sittingUser.equals(user)) {
                try {
                    user.move(leaveLocation.getPosX(), leaveLocation.getPosY());
                    user.setCurrentInteractable(null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            MessageBundle messageBundle = new MessageBundle("Dieser Platz ist bereits belegt.");
            TextMessage info = new TextMessage(messageBundle);
            // send message
        }
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
                try {
                    user.move(interactionLocation.getPosX(), interactionLocation.getPosY());
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
