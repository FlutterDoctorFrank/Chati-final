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
 * Eine Klasse, welche ein Objekt repräsentiert, durch welches ein Benutzer zu einer festgelegten Position teleportiert
 * wird.
 */
public class Portal extends Interactable {

    /** Menü-Option, um den Benutzer zur Zielposition zu teleportieren. */
    private static final int MENU_OPTION_TELEPORT = 1;

    /** Position, an die man teleportiert wird. */
    private final Location destination;

    /**
     * Erzeugt eines neue Instanz des MusicPlayer.
     * @param objectName Name des Objekts.
     * @param parent Übergeordneter Kontext.
     * @param expanse Räumliche Ausdehnung des Kontexts.
     * @param communicationRegion Geltende Kommunikationsform.
     * @param communicationMedia Benutzbare Kommunikationsmedien.
     * @param destination Position, an die teleportiert werden soll.
     */
    public Portal(@NotNull final String objectName, @NotNull final Area parent,
                  @NotNull final CommunicationRegion communicationRegion, @NotNull final Set<CommunicationMedium> communicationMedia,
                  @NotNull final Expanse expanse, @NotNull final Location destination) {
        super(objectName, parent, communicationRegion, communicationMedia, expanse, ContextMenu.PORTAL_MENU);
        this.destination = destination;
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

        if (menuOption == MENU_OPTION_TELEPORT) { // Teleportiere den Benutzer zur festgelegten Position.
            user.setCurrentInteractable(null);
            user.setMovable(true);
            user.send(SendAction.CLOSE_MENU, this);
            user.teleport(destination);
        } else {
            throw new IllegalInteractionException("No valid menu option", user);
        }
    }
}