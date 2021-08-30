package model.context.spatial.objects;

import controller.network.ClientSender.SendAction;
import model.communication.CommunicationMedium;
import model.communication.CommunicationRegion;
import model.context.spatial.*;
import model.exception.IllegalInteractionException;
import model.exception.IllegalMenuActionException;
import model.user.User;
import org.jetbrains.annotations.NotNull;
import java.util.Set;

/**
 * Eine Klasse, welche ein Objekt repräsentiert, durch welches ein Benutzer ein Musikstück in einem räumlichen Kontext
 * auswählen und abspielen lassen kann, oder das Abspielen beenden kann.
 */
public class MusicPlayer extends Interactable {

    /** Menü-Option zum Abspielen eines Musikstücks. */
    private static final int MENU_OPTION_PLAY = 1;

    /** Menü-Option zum Stoppen des gerade abgespielten Musikstücks. */
    private static final int MENU_OPTION_STOP = 2;

    /**
     * Erzeugt eine neue Instanz des MusicPlayer.
     * @param objectName Name des Objekts.
     * @param parent Übergeordneter Kontext.
     * @param expanse Räumliche Ausdehnung des Kontexts.
     * @param communicationRegion Geltende Kommunikationsform.
     * @param communicationMedia Benutzbare Kommunikationsmedien.
     */
    public MusicPlayer(@NotNull final String objectName, @NotNull final Area parent,
                       @NotNull final CommunicationRegion communicationRegion,
                       @NotNull final Set<CommunicationMedium> communicationMedia, @NotNull final Expanse expanse) {
        super(objectName, parent, communicationRegion, communicationMedia, expanse, Menu.MUSIC_PLAYER_MENU);
    }

    @Override
    public void interact(@NotNull final User user) {
        throwIfUserNotAvailable(user);

        // Öffne das Menü beim Benutzer.
        user.setCurrentInteractable(this);
        user.setMovable(false);
        user.send(SendAction.OPEN_MENU, this);
    }

    @Override
    public void executeMenuOption(@NotNull final User user, final int menuOption,
                                  @NotNull final String[] args) throws IllegalInteractionException, IllegalMenuActionException {
        super.executeMenuOption(user, menuOption, args);

        switch (menuOption) {
            case MENU_OPTION_PLAY: // Spiele ein Musikstück ab.
                if (args.length < 1) {
                    throw new IllegalMenuActionException("", "object.arguments.to-few");
                }

                Music music;
                try {
                    music = Music.valueOf(args[0].toUpperCase());
                } catch (IllegalArgumentException ex) {
                    throw new IllegalMenuActionException("", ex, "object.music-player.music-not-found", args[0]);
                }
                getParent().playMusic(music);
                break;
            case MENU_OPTION_STOP: // Stoppe das Abspielen eines Musikstücks.
                getParent().stopMusic();
                break;
            default:
                throw new IllegalInteractionException("No valid menu option", user);
        }
    }
}