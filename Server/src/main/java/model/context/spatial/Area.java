package model.context.spatial;

import controller.network.ClientSender;
import model.communication.CommunicationMedium;
import model.communication.CommunicationRegion;
import model.context.Context;
import model.context.ContextID;
import model.timedEvents.AreaManagerAssignment;
import model.timedEvents.AreaManagerWithdrawal;
import model.timedEvents.TimedEventScheduler;
import model.user.User;

import java.time.LocalDateTime;
import java.util.*;

public class Area extends Context implements IArea {

    /** Die diesem räumlichen Kontext übergeordnete Welt. */
    protected World world;

    /** Menge aller enthaltenen Interaktionsobjekte. */
    private final Map<ContextID, Interactable> interactables;

    /** Räumliche Ausdehnung des Kontextes. */
    protected Expanse expanse;

    /** Die in diesem Kontext geltende Kommunikationsform. */
    protected CommunicationRegion communicationRegion;

    /** Die in diesem Kontext verwendbaren Kommunikationsmedien. */
    protected Set<CommunicationMedium> communicationMedia;

    /** Reservierungen zum Erhalt der Rolle des Bereichsberechtigten in diesem Kontext. */
    private final Set<AreaReservation> areaReservations;

    /** Die in diesem Kontext laufende Musik.*/
    private Music music;

    /**
     * Erzeugt eine Instanz eines Kontextes.
     *
     * @param areaName         Name des Kontextes.
     * @param parent              Übergeordneter Kontext.
     * @param communicationRegion
     * @param communicationMedia
     */
    protected Area(String areaName, Context parent, World world, CommunicationRegion communicationRegion,
                   Set<CommunicationMedium> communicationMedia, Expanse expanse) {
        super(areaName, parent);
        this.world = world;
        this.expanse = expanse;
        this.communicationRegion = communicationRegion;
        this.communicationMedia = communicationMedia;
        this.areaReservations = new HashSet<>();
        this.interactables = new HashMap<>();
        this.music = null;
        parent.addChild(this);
    }

    @Override
    public Music getMusic() {
        return music;
    }

    /**
     * Setzt die momentan abzuspielende Musik in diesem Kontext.
     * @param music Abzuspielende Musik.
     */
    public void playMusic(Music music) {
        this.music = music;
        // Sende Information über geänderte Musik an alle Benutzer im Kontext.
        containedUsers.values().forEach(user -> {
            user.getClientSender().send(ClientSender.SendAction.CONTEXT_MUSIC, this);
        });
    }

    /**
     * Stoppt die momentan abzuspielende Musik in diesem Kontext.
     */
    public void stopMusic() {
        playMusic(null);
    }

    @Override
    public Map<UUID, User> getCommunicableUsers(User communicatingUser) {
        return communicationRegion.getCommunicableUsers(communicatingUser);
    }

    @Override
    public boolean canCommunicateWith(CommunicationMedium medium) {
        return communicationMedia.contains(medium);
    }

    /**
     * Fügt eine Reservierung zum Erhalt der Rolle des Bereichsberechtigten zu diesem Kontext hinzu.
     * @param user Reservierter Benutzer.
     * @param from Reservierter Anfangszeitpunkt.
     * @param to Reservierter Endzeitpunkt.
     */
    public void addReservation(User user, LocalDateTime from, LocalDateTime to) {
        AreaReservation reservation = new AreaReservation(user, this, from, to);
        areaReservations.add(reservation);
        TimedEventScheduler.getInstance().put(new AreaManagerAssignment(reservation));
    }

    /**
     * Entfernt eine Reservierung zum Erhalt der Rolle des Bereichsberechtigten.
     * @param reservation Zu entfernende Reservierung.
     */
    public void removeReservation(AreaReservation reservation) {
        areaReservations.remove(reservation);
        TimedEventScheduler.getInstance().put(new AreaManagerWithdrawal(reservation));
    }

    /**
     *
     * @param interactable
     */
    public void addInteractable(Interactable interactable) {
        interactables.put(interactable.getContextId(), interactable);
    }

    /**
     * Überprüft, ob dieser Kontext von einem Benutzer reserviert ist.
     * @param user Zu überprüfender Benutzer.
     * @return true, wenn der Kontext von dem Benutzer reserviert ist, sonst false.
     */
    public boolean isReservedBy(User user) {
        return areaReservations.stream().anyMatch(reservation -> reservation.getReserver().equals(user));
    }

    /**
     * Überprüft, ob dieser Kontext in einem bestimmten Zeitraum reserviert ist.
     * @param from Zu überprüfender Anfangszeitpunkt.
     * @param to Zu überprüfender Endzeitpunkt.
     * @return true, wenn der Kontext in dem Zeitraum reserviert ist, sonst false.
     */
    public boolean isReservedAt(LocalDateTime from, LocalDateTime to) {
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
    public boolean isReservedAtBy(User user, LocalDateTime from, LocalDateTime to) {
        return areaReservations.stream().anyMatch(reservation -> reservation.getReserver().equals(user)
                && reservation.getFrom().equals(from) && reservation.getTo().equals(to));
    }

    public Interactable getInteractable(ContextID interactableId) {
        return interactables.get(interactableId);
    }

    /**
     * Gibt den untergeordnetsten Kontext zurück, auf dem sich die übergebenen Koordinaten befinden.
     * @param posX X-Koordinate.
     * @param posY Y-Koordinate.
     * @return Untergeordnetster Kontext, auf dem sich die Koordinaten befinden.
     */
    public Area getArea(int posX, int posY) {
        try {
            // Suche untergeordneten Kontext, auf dem sich übergebene Koordinaten befinden.
            return children.values().stream().filter(child -> child.expanse.isIn(posX, posY))
                    .findFirst().orElseThrow().getArea(posX, posY);
        } catch (NoSuchElementException e) {
            // Kein untergeordneter Kontext vorhanden.
            return this;
        }
    }

    /**
     * Gibt die diesem räumlichen Kontext übergeordnete Welt zurück.
     * @return Diesem räumlichen Kontext übergeordnete Welt.
     */
    public World getWorld() {
        return world;
    }

    public void setExpanse(Expanse expanse) {
        this.expanse = expanse;
    }

    public void setCommunicationRegion(CommunicationRegion communicationRegion) {
        this.communicationRegion = communicationRegion;
    }

    public void setCommunicationMedia(Set<CommunicationMedium> communicationMedia) {
        this.communicationMedia = communicationMedia;
    }

    @Override
    public void addUser(User user) {
        super.addUser(user);
        // Sende Musikinformationen an den Benutzer.
        if (music != null) {
            user.getClientSender().send(ClientSender.SendAction.CONTEXT_MUSIC, this);
        }
    }
}