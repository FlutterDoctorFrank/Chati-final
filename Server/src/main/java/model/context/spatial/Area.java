package model.context.spatial;

import controller.network.ClientSender.SendAction;
import model.communication.CommunicationMedium;
import model.communication.CommunicationRegion;
import model.context.Context;
import model.context.ContextID;
import model.context.spatial.objects.Interactable;
import model.exception.ContextNotFoundException;
import model.timedEvents.AreaManagerAssignment;
import model.timedEvents.AreaManagerWithdrawal;
import model.timedEvents.TimedEventScheduler;
import model.user.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Predicate;

public class Area extends Context implements IArea {

    /** Die diesem Kontext übergeordnete Welt. */
    protected final World world;

    /** Menge aller enthaltenen Interaktionsobjekte. */
    protected final Map<ContextID, Interactable> interactables;

    /** Räumliche Ausdehnung des Kontextes. */
    protected Expanse expanse;

    /** Die in diesem Kontext geltende Kommunikationsform. */
    protected CommunicationRegion communicationRegion;

    /** Die in diesem Kontext verwendbaren Kommunikationsmedien. */
    protected Set<CommunicationMedium> communicationMedia;

    /** Reservierungen zum Erhalt der Rolle des Bereichsberechtigten in diesem Kontext. */
    private final Set<AreaReservation> areaReservations;

    /** Die in diesem Kontext laufende Musik.*/
    private ContextMusic music;

    /**
     * Erzeugt eine Instanz eines Bereichs.
     * @param areaName Name des Bereichs..
     * @param parent Übergeordneter Kontext.
     * @param world Übergeordnete Welt.
     * @param communicationRegion Kommunikationsform des Bereichs.
     * @param communicationMedia Benutzbare Kommunikationsmedien des Bereichs.
     * @param expanse Räumliche Ausdehnung des Bereichs.
     */
    protected Area(@NotNull final String areaName, @NotNull final Context parent, @Nullable final World world,
                   @Nullable final CommunicationRegion communicationRegion, @Nullable final Set<CommunicationMedium> communicationMedia,
                   @Nullable final Expanse expanse) {
        super(areaName, parent);
        this.world = world;
        this.expanse = expanse;
        this.communicationRegion = communicationRegion;
        this.communicationMedia = communicationMedia;
        this.areaReservations = new HashSet<>();
        this.interactables = new HashMap<>();
        this.music = null;
        if (communicationRegion != null) {
            communicationRegion.setArea(this);
        }
    }

    @Override
    public @Nullable ContextMusic getMusic() {
        return music;
    }

    /**
     * Setzt die momentan abzuspielende Musik in diesem Kontext.
     * @param music Abzuspielende Musik.
     */
    public void setMusic(@Nullable final ContextMusic music) {
        this.music = music;
        // Sende Information über geänderte Musik an alle Benutzer im Kontext.
        this.containedUsers.values().forEach(user -> user.send(SendAction.CONTEXT_INFO, this));
    }

    @Override
    public @NotNull Map<UUID, User> getCommunicableUsers(@NotNull final User communicatingUser) {
        if (this.communicationRegion == null) {
            throw new IllegalStateException("No communication region available in: " + contextId);
        }

        return new HashMap<>(communicationRegion.getCommunicableUsers(communicatingUser));
    }

    /**
     * Gibt die Benutzer in diesem Kontext zurück, die sich nicht in einem inneren Bereich mit einer exklusiven
     * Kommunikationsform befinden.
     * @return Menge der nicht exklusiv kommunizierbaren Benutzer in diesem Bereich.
     * @see Area#isCommunicateExclusive()
     */
    public @NotNull Map<UUID, User> getCommunicableUsers() {
        Map<UUID, User> communicableUsers = this.getExclusiveUsers();
        children.values().stream()
                .filter(Predicate.not(Area::isCommunicateExclusive))
                .forEach(child -> communicableUsers.putAll(child.getCommunicableUsers()));
        return communicableUsers;
    }

    @Override
    public boolean canCommunicateWith(@NotNull final CommunicationMedium medium) {
        if (this.communicationMedia == null) {
            return false;
        }

        return communicationMedia.contains(medium);
    }

    /**
     * Gibt zurück, ob in diesem Bereich eine exklusive Kommunikationsform gilt.
     * @return true, wenn in diesem Bereich eine exklusive Kommunikationsform gilt, sonst false.
     * @see CommunicationRegion#isExclusive()
     */
    public boolean isCommunicateExclusive() {
        return communicationRegion.isExclusive();
    }

    /**
     * Fügt eine Reservierung zum Erhalt der Rolle des Bereichsberechtigten zu diesem Kontext hinzu.
     * @param user Reservierter Benutzer.
     * @param from Reservierter Anfangszeitpunkt.
     * @param to Reservierter Endzeitpunkt.
     */
    public void addReservation(@NotNull final User user, @NotNull final LocalDateTime from, @NotNull final LocalDateTime to) {
        AreaReservation reservation = new AreaReservation(user, this, from, to);
        areaReservations.add(reservation);
        TimedEventScheduler.getInstance().put(new AreaManagerAssignment(reservation));
    }

    /**
     * Entfernt eine Reservierung zum Erhalt der Rolle des Bereichsberechtigten.
     * @param reservation Zu entfernende Reservierung.
     */
    public void removeReservation(@NotNull final AreaReservation reservation) {
        areaReservations.remove(reservation);
        TimedEventScheduler.getInstance().put(new AreaManagerWithdrawal(reservation));
    }

    /**
     * Fügt ein Interaktionsobjekt zu diesem Bereich hinzu.
     * @param interactable Hinzuzufügendes Interaktionsobjekt.
     */
    public void addInteractable(@NotNull final Interactable interactable) {
        interactables.put(interactable.getContextId(), interactable);
    }

    /**
     * Überprüft, ob dieser Kontext von einem Benutzer reserviert ist.
     * @param user Zu überprüfender Benutzer.
     * @return true, wenn der Kontext von dem Benutzer reserviert ist, sonst false.
     */
    public boolean isReservedBy(@NotNull final User user) {
        return areaReservations.stream().anyMatch(reservation -> reservation.getReserver().equals(user));
    }

    /**
     * Überprüft, ob dieser Kontext in einem bestimmten Zeitraum reserviert ist.
     * @param from Zu überprüfender Anfangszeitpunkt.
     * @param to Zu überprüfender Endzeitpunkt.
     * @return true, wenn der Kontext in dem Zeitraum reserviert ist, sonst false.
     */
    public boolean isReservedAt(@NotNull final LocalDateTime from, @NotNull final LocalDateTime to) {
        return areaReservations.stream().anyMatch(reservation -> reservation.getFrom().equals(from)
                && reservation.getTo().equals(to));
    }

    /**
     * Überprüft, ob dieser Kontext von einem Benutzer in einem bestimmten Zeitraum reserviert ist.
     * @param user Zu überprüfender Benutzer.
     * @param from Zu überprüfender Anfangszeitpunkt.
     * @param to Zu überprüfender Endzeitpunkt.
     * @return true, wenn der Kontext von dem Benutzer in dem Zeitraum reserviert ist, sonst false.
     */
    public boolean isReservedAtBy(@NotNull final User user, @NotNull final LocalDateTime from, @NotNull final LocalDateTime to) {
        return areaReservations.stream().anyMatch(reservation -> reservation.getReserver().equals(user)
                && reservation.getFrom().equals(from) && reservation.getTo().equals(to));
    }

    /**
     * Gibt das Interaktionsobjekt mit der gegebenen ID zurück.
     * @param interactableId ID des Interaktionsobjekts.
     * @return Interaktionsobjekt.
     * @throws ContextNotFoundException wenn es in diesem Bereich kein Interaktionsobjekt mit dieser ID gibt.
     */
    public @NotNull Interactable getInteractable(@NotNull final ContextID interactableId) throws ContextNotFoundException {
        Interactable interactable = interactables.get(interactableId);
        if (interactable == null) {
            for (Area child : children.values()) {
                try {
                    interactable = child.getInteractable(interactableId);
                } catch (ContextNotFoundException e) {
                    // Nothing to do here
                }
                if (interactable != null) {
                    return interactable;
                }
            }
        }
        if (interactable == null) {
            throw new ContextNotFoundException("No interactable with this ID found.", interactableId);
        }
        return interactable;
    }

    /**
     * Gibt den untergeordnetsten Kontext zurück, auf dem sich die übergebenen Koordinaten befinden.
     * @param posX X-Koordinate.
     * @param posY Y-Koordinate.
     * @return Untergeordnetster Kontext, auf dem sich die Koordinaten befinden.
     */
    public @NotNull Area getArea(final float posX, final float posY) {
        try {
            // Suche untergeordneten Kontext, auf dem sich übergebene Koordinaten befinden.
            return children.values().stream()
                    .filter(child -> child.expanse != null && child.expanse.isIn(posX, posY))
                    .findFirst().orElseThrow().getArea(posX, posY);
        } catch (NoSuchElementException e) {
            // Dieser Kontext ist der untergeordnetste, auf dem sich die übergebenen Koordinaten befinden.
            return this;
        }
    }

    /**
     * Gibt die diesem räumlichen Kontext übergeordnete Welt zurück.
     * @return Diesem räumlichen Kontext übergeordnete Welt.
     */
    public @NotNull World getWorld() {
        if (this.world == null) {
            throw new IllegalStateException("World of area " + this.contextName + " is not available");
        }

        return world;
    }

    @Override
    public void addUser(@NotNull final User user) {
        if (!this.contains(user)) {
            super.addUser(user);

            user.send(SendAction.CONTEXT_INFO, this);

            /*
             * Ist der Benutzer im Kontext stumm geschaltet, so erhalten alle anderen Benutzer in diesem Kontext die
             * Information der Stummschaltung.
             */
            if (this.mutedUsers.containsKey(user.getUserId())) {
                this.containedUsers.values().stream()
                        .filter(Predicate.not(user::equals))
                        .forEach(receiver -> receiver.send(SendAction.CONTEXT_INFO, this));
            }
        }
    }
}