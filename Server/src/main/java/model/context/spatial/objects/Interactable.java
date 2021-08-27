package model.context.spatial.objects;

import controller.network.ClientSender;
import model.communication.CommunicationMedium;
import model.communication.CommunicationRegion;
import model.context.spatial.*;
import model.exception.IllegalInteractionException;
import model.exception.IllegalMenuActionException;
import model.user.User;
import org.jetbrains.annotations.NotNull;
import java.util.Set;

public abstract class Interactable extends Area implements IInteractable {

    /** Die maximale Distanz, über die eine Interaktion erfolgen darf. */
    protected static final int INTERACTION_DISTANCE = 1 * 32;

    /** Menü-Option zum Schließen des Menüs. */
    protected static final int MENU_OPTION_CLOSE = 0;

    protected final Area parent;

    /** Das Menü, dass beim Benutzer bei einer Interaktion mit diesem Kontext geöffnet werden soll. */
    private final Menu menu;

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
                           @NotNull final Expanse expanse, @NotNull final Menu menu) {
        super(interactableName, parent, parent.getWorld(), communicationRegion, communicationMedia, expanse);
        this.parent = parent;
        this.menu = menu;
    }

    /**
     * Lässt einen Benutzer mit dem Kontext interagieren. Es kann nur mit Kontexten des Typs
     * {@link SpatialContextType#OBJECT} interagiert werden.
     * @param user Interagierender Benutzer.
     * @throws IllegalInteractionException wenn mit diesem Kontext keine Interaktion möglich ist.
     */
    public abstract void interact(@NotNull final User user) throws IllegalInteractionException;

    /**
     * Lässt einen Benutzer eine Menü-Option dieses Kontextes ausführen. Es können nur Menü-Optionen von Kontexten des
     * Typs {@link SpatialContextType#OBJECT} ausgeführt werden.
     * @param user Ausführender Benutzer.
     * @param menuOption Auszuführende Menü-Option.
     * @param args Argumente, mit denen die Menü-Option ausgeführt werden sollen.
     * @throws IllegalInteractionException wenn mit diesem Kontext keine Interaktion möglich ist.
     * @throws IllegalMenuActionException wenn die Menü-Option nicht unterstützt wird oder die übergebenen Argumente
     * ungültig sind.
     */
    public void executeMenuOption(@NotNull final User user, final int menuOption,
                                           @NotNull final String[] args) throws IllegalInteractionException, IllegalMenuActionException {
        throwIfUserNotAvailable(user);

        if (menuOption == MENU_OPTION_CLOSE) {
            user.setCurrentInteractable(null);
            user.setMovable(true);
            user.send(ClientSender.SendAction.CLOSE_MENU, this);
        }
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

    @Override
    public @NotNull Menu getMenu() {
        return menu;
    }

    @Override
    public @NotNull Area getParent() {
        return parent;
    }
}