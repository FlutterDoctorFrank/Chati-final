package model.context.spatial.objects;

import controller.network.ClientSender;
import model.communication.CommunicationMedium;
import model.communication.CommunicationRegion;
import model.context.spatial.Expanse;
import model.context.spatial.Menu;
import model.context.spatial.Music;
import model.context.spatial.SpatialContext;
import model.exception.IllegalInteractionException;
import model.exception.IllegalMenuActionException;
import model.user.User;

import java.util.HashSet;
import java.util.Set;

/**
 * Eine Klasse, welche ein Objekt repräsentiert, durch welches ein Benutzer ein Musikstück in einem räumlichen Kontext
 * auswählen und abspielen lassen kann, oder das Abspielen beenden kann. Ist immer vom Typ
 * {@link model.context.spatial.SpatialContextType#OBJECT}.
 */
public class MusicPlayer extends SpatialContext {

    /**
     * Erzeugt eines neue Instanz des MusicPlayer.
     * @param objectName Name des Objekts.
     * @param parent Übergeordneter Kontext.
     * @param expanse Räumliche Ausdehnung des Kontexts.
     * @param communicationRegion Geltende Kommunikationsform.
     * @param communicationMedia Benutzbare Kommunikationsmedien.
     */
    public MusicPlayer(String objectName, SpatialContext parent, Expanse expanse,
                       CommunicationRegion communicationRegion, Set<CommunicationMedium> communicationMedia) {
        super(objectName, parent, Menu.MUSIC_PLAYER_MENU, expanse, communicationRegion, communicationMedia);
    }

    @Override
    public void interact(User user) {
        // Öffne das Menü beim Benutzer.
        user.setCurrentInteractable(this);
        user.setMoveable(false);
        user.getClientSender().send(ClientSender.SendAction.OPEN_MENU, this);
    }

    @Override
    public void executeMenuOption(User user, int menuOption, String[] args) throws IllegalInteractionException, IllegalMenuActionException {
        switch (menuOption) {
            case 0: // Schließe das Menü beim Benutzer.
                user.setCurrentInteractable(null);
                user.setMoveable(true);
                user.getClientSender().send(ClientSender.SendAction.CLOSE_MENU, this);
                break;
            case 1: // Spiele ein Musikstück ab.
                Music music;
                try {
                    music = Music.valueOf(args[0]);
                } catch (IllegalArgumentException e) {
                    throw new IllegalMenuActionException("", "Das abzuspielende Musikstück existiert nicht.", e);
                }
                getParent().playMusic(music);
                break;
            case 2: // Stoppe das Abspielen eines Musikstücks.
                getParent().stopMusic();
            default:
                throw new IllegalInteractionException("No valid menu option", user);
        }
    }
}
