package model.context.spatial;

import model.communication.CommunicationMedium;
import model.communication.CommunicationRegion;
import model.exception.IllegalInteractionException;
import model.exception.IllegalMenuActionException;
import model.user.User;

import java.util.Set;

public abstract class Interactable extends Area implements IInteractable {

    /** Die maximale Distanz, über die eine Interaktion erfolgen darf. */
    protected static final int INTERACTION_DISTANCE = 1;

    protected final Area parent;

    /** Das Menü, dass beim Benutzer bei einer Interaktion mit diesem Kontext geöffnet werden soll. */
    private Menu menu;

    /**
     * Erzeugt eine Instanz eines Kontextes.
     *
     * @param contextName            Name des Kontextes.
     * @param parent              Übergeordneter Kontext.
     * @param communicationRegion
     * @param communicationMedia
     * @param expanse
     */
    protected Interactable(String contextName, Area parent, CommunicationRegion communicationRegion,
                           Set<CommunicationMedium> communicationMedia, Expanse expanse, Menu menu) {
        super(contextName, parent, parent.getWorld(), communicationRegion, communicationMedia, expanse);
        this.parent = parent;
        this.menu = menu;
    }

    /**
     * Lässt einen Benutzer mit dem Kontext interagieren. Es kann nur mit Kontexten des Typs
     * {@link SpatialContextType#OBJECT} interagiert werden.
     * @param user Interagierender Benutzer.
     * @throws IllegalInteractionException wenn mit diesem Kontext keine Interaktion möglich ist.
     */
    public abstract void interact(User user) throws IllegalInteractionException;

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
    public abstract void executeMenuOption(User user, int menuOption, String[] args) throws IllegalInteractionException, IllegalMenuActionException;

    /**
     * Überprüft, ob ein Benutzer mit diesem Kontext interagieren darf.
     * @param user Zu überprüfender Benutzer.
     * @return true, wenn der Benutzer mit dem Kontext interagieren darf, sonst false.
     */
    public boolean canInteract(User user) {
        Location userLocation = user.getLocation();
        return (user.isInteractingWith(null) || user.isInteractingWith(this))
                && expanse.isAround(userLocation.getPosX(), userLocation.getPosY(), INTERACTION_DISTANCE);
    }

    @Override
    public Menu getMenu() {
        return menu;
    }

    @Override
    public Area getParent() {
        return parent;
    }
}
