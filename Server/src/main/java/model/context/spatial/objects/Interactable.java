package model.context.spatial.objects;

import controller.network.ClientSender;
import model.communication.CommunicationMedium;
import model.communication.CommunicationRegion;
import model.context.ContextID;
import model.context.spatial.Area;
import model.context.spatial.ContextMenu;
import model.context.spatial.Direction;
import model.context.spatial.Expanse;
import model.context.spatial.Location;
import model.context.spatial.Room;
import model.exception.ContextNotFoundException;
import model.exception.IllegalInteractionException;
import model.exception.IllegalMenuActionException;
import model.user.User;
import org.jetbrains.annotations.NotNull;
import java.util.Set;

/**
 * Eine Abstrakte Klasse, welche von den Objekten erweitert wird.
 */
public abstract class Interactable extends Area implements IInteractable {

    /** Die maximale Distanz, über die eine Interaktion erfolgen darf. */
    protected static final int INTERACTION_DISTANCE = 1 * 32;

    /** Menü-Option zum Schließen des Menüs. */
    protected static final int MENU_OPTION_CLOSE = 0;

    protected final Area parent;

    /** Das Menü das beim Benutzer bei einer Interaktion mit diesem Kontext geöffnet werden soll. */
    private final ContextMenu menu;

    /**
     * Erzeugt eine Instanz eines Interaktionsobjekts.
     *
     * @param interactableName Name des Interaktionsobjekts.
     * @param parent Übergeordneter Bereich.
     * @param communicationRegion Kommunikationsform des Objekts.
     * @param communicationMedia Benutzbare Kommunikationsmedien des Objekts.
     * @param expanse Räumliche Ausdehnung des Objekts.
     * @param menu Menü des Objekts.
     */
    protected Interactable(@NotNull final String interactableName, @NotNull final Area parent,
                           @NotNull final CommunicationRegion communicationRegion, @NotNull final Set<CommunicationMedium> communicationMedia,
                           @NotNull final Expanse expanse, @NotNull final ContextMenu menu) {
        super(interactableName, parent, parent.getWorld(), communicationRegion, communicationMedia, expanse);
        this.parent = parent;
        this.menu = menu;
    }

    /**
     * Lässt einen Benutzer mit dem Kontext interagieren. Es kann nur mit {@link Interactable} interagiert werden.
     * @param user Interagierender Benutzer.
     * @throws IllegalInteractionException wenn mit diesem Kontext keine Interaktion möglich ist.
     */
    public abstract void interact(@NotNull final User user) throws IllegalInteractionException;

    /**
     * Lässt einen Benutzer eine Menü-Option dieses Kontextes ausführen. Es können nur Menü-Optionen von
     * {@link Interactable} ausgeführt werden.
     * @param user Ausführender Benutzer.
     * @param menuOption Auszuführende Menü-Option.
     * @param args Argumente, mit denen die Menü-Option ausgeführt werden sollen.
     * @throws IllegalInteractionException wenn mit diesem Kontext keine Interaktion möglich ist.
     * @throws IllegalMenuActionException wenn die Menü-Option nicht unterstützt wird oder die übergebenen Argumente
     * ungültig sind.
     */
    public abstract void executeMenuOption(@NotNull final User user, final int menuOption,
                                           @NotNull final String[] args) throws IllegalInteractionException, IllegalMenuActionException;

    @Override
    public @NotNull Interactable getInteractable(@NotNull final ContextID interactableId) throws ContextNotFoundException {
        if (interactableId.equals(this.contextId)) {
            return this;
        }
        return super.getInteractable(interactableId);
    }

    /**
     * Überprüft, ob ein Benutzer mit diesem Kontext interagieren darf.
     * @param user Zu überprüfender Benutzer.
     * @return true, wenn der Benutzer mit dem Kontext interagieren darf, sonst false.
     */
    public boolean canInteract(@NotNull final User user) {
        Location location = user.getLocation();
        return (location != null && (!user.isInteracting() || user.isInteractingWith(this))
                && expanse.isAround(location.getPosX(), location.getPosY(), INTERACTION_DISTANCE));
    }

    protected void throwIfUserNotAvailable(@NotNull final User user) {
        if (!user.isOnline() || !user.isInWorld()) {
            throw new IllegalStateException("User is not logged in or in a world.");
        }
    }

    protected void throwIfInteractNotAllowed(@NotNull final User user) throws IllegalInteractionException {
        if (this.equals(user.getCurrentInteractable())) {
            throw new IllegalInteractionException("User is already interacting with a context.", user);
        }
    }

    protected boolean executeCloseOption(@NotNull final User user, final int menuOption) {
        throwIfUserNotAvailable(user);

        if (menuOption == MENU_OPTION_CLOSE) {
            user.setCurrentInteractable(null);
            user.setMovable(true);
            user.send(ClientSender.SendAction.CLOSE_MENU, this);
            return true;
        }

        return false;
    }

    @Override
    public @NotNull ContextMenu getMenu() {
        return menu;
    }

    @Override
    public @NotNull Area getParent() {
        return parent;
    }

    public static @NotNull Location getLegalPosition(@NotNull final Location location) {
        int searchedDirections = 0;
        int ordinal = location.getDirection().ordinal();
        float posX = location.getPosX();
        float posY = location.getPosY();
        Room room = location.getRoom();

        while (searchedDirections < Direction.values().length) {
            Direction direction = Direction.values()[ordinal];

            switch (direction) {
                case UP:
                    for (int range = 0; range < INTERACTION_DISTANCE; range++) {
                        if (room.isLegal(posX, posY + range)) {
                            return new Location(room, direction, posX, posY + range);
                        }
                    }
                    break;

                case RIGHT:
                    for (int range = 0; range < INTERACTION_DISTANCE; range++) {
                        if (room.isLegal(posX + range, posY)) {
                            return new Location(room, direction, posX + range, posY);
                        }
                    }
                    break;

                case DOWN:
                    for (int range = 0; range < INTERACTION_DISTANCE; range++) {
                        if (room.isLegal(posX, posY - range)) {
                            return new Location(room, direction, posX, posY - range);
                        }
                    }
                    break;

                case LEFT:
                    for (int range = 0; range < INTERACTION_DISTANCE; range++) {
                        if (room.isLegal(posX - range, posY)) {
                            return new Location(room, direction, posX - range, posY);
                        }
                    }
                    break;
            }

            ordinal = (ordinal + 1) % Direction.values().length;
            searchedDirections++;
        }

        throw new IllegalStateException("No space in all directions available");
    }
}