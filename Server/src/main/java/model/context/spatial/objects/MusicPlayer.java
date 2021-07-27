package model.context.spatial.objects;

import controller.network.ClientSender;
import model.context.spatial.Location;
import model.context.spatial.Menu;
import model.context.spatial.Music;
import model.context.spatial.SpatialContext;
import model.exception.IllegalInteractionException;
import model.exception.IllegalMenuActionException;
import model.user.User;

public class MusicPlayer extends SpatialContext {

    protected MusicPlayer(String contextName, SpatialContext parent, Menu menu, Location interactionLocation) {
        super(contextName, parent, menu, interactionLocation);
    }

    @Override
    public void interact(User user) {
        user.setCurrentInteractable(this);
        user.getClientSender().send(ClientSender.SendAction.OPEN_MENU, this);
    }

    @Override
    public void executeMenuOption(User user, int menuOption, String[] args) throws IllegalInteractionException, IllegalMenuActionException {
        switch (menuOption) {
            case 0:
                user.setCurrentInteractable(null);
                user.getClientSender().send(ClientSender.SendAction.CLOSE_MENU, this);
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
            case 2:
                getParent().stopMusic();
                user.getClientSender().send(ClientSender.SendAction.CONTEXT_MUSIC, getParent()); // ???
            default:
                throw new IllegalInteractionException("No valid menu option", user);
        }
    }
}
