package model.context.spatial.objects;

import model.context.Context;
import model.context.ContextID;
import model.context.spatial.Music;
import model.context.spatial.SpatialContext;
import model.exception.IllegalInteractionException;
import model.exception.IllegalMenuActionException;
import model.user.User;

import java.util.Map;

public class MusicPlayer extends SpatialContext {

    protected MusicPlayer(String contextName, Context parent, Map<ContextID, SpatialContext> children) {
        super(contextName, parent, children);
    }

    @Override
    public void interact(User user) {
        // Check if user is close to object
        // Check if user currently interacts with another object
        // Send Packet for menu open
    }

    @Override
    public void executeMenuOption(User user, int menuOption, String[] args) throws IllegalInteractionException, IllegalMenuActionException {
        if (!user.isInteractingWith(this)) {
            throw new IllegalInteractionException("User has not opened the menu of this object.", user);
        }
        switch (menuOption) {
            case 0:
                user.setCurrentInteractable(null);
                // Send packet for menu close
                break;
            case 1:
                Music music;
                try {
                    music = Music.valueOf(args[0]);
                } catch (IllegalArgumentException e) {
                    throw new IllegalMenuActionException("", "Das abzuspielende Musikstück existiert nicht.", e);
                }
                getParent().playMusic(music);
                break;
            default:
                throw new IllegalInteractionException("No valid menu option", user);
        }
    }
}
