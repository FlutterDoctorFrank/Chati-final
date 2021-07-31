package model.context.spatial;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.utils.Array;
import controller.network.ClientSender;
import model.MessageBundle;
import model.communication.CommunicationMedium;
import model.communication.CommunicationRegion;
import model.communication.message.TextMessage;
import model.context.Context;
import model.context.ContextID;
import model.context.global.GlobalContext;
import model.exception.IllegalInteractionException;
import model.exception.IllegalMenuActionException;
import model.role.Role;
import model.timedEvents.AreaManagerAssignment;
import model.timedEvents.AreaManagerWithdrawal;
import model.timedEvents.TimedEventScheduler;
import model.user.User;

import java.time.LocalDateTime;
import java.util.*;

public class SpatialContext extends Context implements ISpatialContext {

    /** Die maximale Distanz, über die eine Interaktion erfolgen darf. */
    protected static final int INTERACTION_DISTANCE = 1;

    /*
     * General Context parameters
     */

    /** Typ des räumlichen Kontextes. */
    private final SpatialContextType spatialContextType;

    /** Die diesem räumlichen Kontext übergeordnete Welt. */
    private final SpatialContext world;

    /*
     * Parameters only relevant for type 'AREA' and 'OBJECT'
     */

    /** Reservierungen zum Erhalt der Rolle des Bereichsberechtigten in diesem Kontext. */
    protected final Set<AreaReservation> areaReservations;

    /** Die Information, ob eine Interaktion mit diesem räumlichen Kontext möglich ist. */
    private final boolean isInteractable;

    /** Das Menü, dass beim Benutzer bei einer Interaktion mit diesem Kontext geöffnet werden soll. */
    private Menu menu;

    /*
     * Parameters relevant for type 'ROOM' and 'WORLD'
     */

    /** Karte dieses räumlichen Kontextes. */
    private final SpatialMap map;

    /** Enthält Information über erlaubte Positionen auf der Karte. */
    private boolean[][] collisionMap;

    /** Räumliche Ausdehnung des Kontextes. */
    protected Expanse expanse;

    /** Information, ob dieser Kontext privat, oder öffentlich zugänglich ist. */
    private final boolean isPrivate;

    /** Passwort des Kontextes, im Falle eines privaten Kontextes. */
    private final String password;

    /** Anfangsposition auf der Karte. */
    private Location spawnLocation;

    /** Untergeordnete private Räume. */
    private final Map<ContextID, SpatialContext> privateRooms;

    /**
     * Erzeugt eine neue Instanz eines räumlichen Kontextes. Wird zum Erzeugen von Kontexten des Typs
     * {@link SpatialContextType#WORLD} verwendet.
     * @param worldName Name der Welt.
     * @param map Karte der Welt.
     * @param communicationRegion Kommunikationsform der Welt.
     * @param communicationMedia Benutzbare Kommunikationsmedien der Welt.
     */
    public SpatialContext(String worldName, SpatialMap map, CommunicationRegion communicationRegion,
                          Set<CommunicationMedium> communicationMedia) {
        super(worldName, GlobalContext.getInstance(), communicationRegion, communicationMedia);
        GlobalContext.getInstance().addChild(this);
        this.spatialContextType = SpatialContextType.WORLD;
        this.world = this;
        this.areaReservations = new HashSet<>();
        this.isInteractable = false;
        this.menu = null;
        this.map = map;
        this.isPrivate = false;
        this.password = null;
        this.privateRooms = new HashMap<>();
        initializeMap();
    }

    /**
     * Erzeugt eine neue Instanz eines räumlichen Kontextes. Wird zum Erzeugen von Kontexten des Typs
     * {@link SpatialContextType#ROOM} verwendet.
     * @param roomName Name des Raums.
     * @param world Übergeordnete Welt des Raums.
     * @param map Karte des Raums.
     * @param password Passwort des Raums.
     * @param communicationRegion Kommunikationsform des Raums.
     * @param communicationMedia Benutzbare Kommunikationsmedien des Raums.
     */
    public SpatialContext(String roomName, SpatialContext world, SpatialMap map, String password,
                          CommunicationRegion communicationRegion, Set<CommunicationMedium> communicationMedia) {
        super(roomName, world, communicationRegion, communicationMedia);
        world.addChild(this);
        this.spatialContextType = SpatialContextType.ROOM;
        this.world = world;
        this.areaReservations = new HashSet<>();
        this.isInteractable = false;
        this.menu = null;
        this.map = map;
        this.isPrivate = true;
        this.password = password;
        this.privateRooms = null;
        initializeMap();
    }

    /**
     * Erzeugt eine neue Instanz eines räumlichen Kontextes. Wird zum Erzeugen von Kontexten des Typs
     * {@link SpatialContextType#AREA} verwendet.
     * @param areaName Name des Bereichs.
     * @param parent Übergeordneter Kontext des Bereichs.
     * @param communicationRegion Kommunikationsform des Bereichs.
     * @param communicationMedia Benutzbare Kommunikationsmedien des Bereichs.
     */
    public SpatialContext(String areaName, SpatialContext parent, CommunicationRegion communicationRegion,
                          Set<CommunicationMedium> communicationMedia) {
        super(areaName, parent, communicationRegion, communicationMedia);
        parent.addChild(this);
        this.spatialContextType = SpatialContextType.AREA;
        this.world = parent.getWorld();
        this.areaReservations = new HashSet<>();
        this.isInteractable = false;
        this.menu = null;
        this.map = null;
        this.isPrivate = false;
        this.password = null;
        this.privateRooms = null;
    }

    /**
     * Erzeugt eine neue Instanz eines räumlichen Kontextes. Wird zum Erzeugen von Kontexten des Typs
     * {@link SpatialContextType#OBJECT} verwendet.
     * @param objectName Name des Objects.
     * @param parent Übergeordneter Kontext des Objekts.
     * @param menu Menü des Objekts.
     * @param communicationRegion Kommunikationsform des Bereichs.
     * @param communicationMedia Benutzbare Kommunikationsmedien des Bereichs.
     */
    protected SpatialContext(String objectName, SpatialContext parent, Menu menu, CommunicationRegion communicationRegion,
                             Set<CommunicationMedium> communicationMedia) {
        super(objectName, parent, communicationRegion, communicationMedia);
        parent.addChild(this);
        this.spatialContextType = SpatialContextType.OBJECT;
        this.world = parent.getWorld();
        this.areaReservations = new HashSet<>();
        this.isInteractable = true;
        this.menu = menu;
        this.map = null;
        this.isPrivate = false;
        this.password = null;
        this.privateRooms = null;
    }

    @Override
    public SpatialContextType getSpatialContextType() {
        return spatialContextType;
    }

    @Override
    public java.util.Map<ContextID, SpatialContext> getPrivateRooms() {
        return Collections.unmodifiableMap(privateRooms);
    }

    @Override
    public SpatialMap getMap() {
        return map;
    }

    /**
     * Lässt einen Benutzer mit dem Kontext interagieren. Es kann nur mit Kontexten des Typs
     * {@link SpatialContextType#OBJECT} interagiert werden.
     * @param user Interagierender Benutzer.
     * @throws IllegalInteractionException wenn mit diesem Kontext keine Interaktion möglich ist.
     */
    public void interact(User user) throws IllegalInteractionException {
        throw new IllegalInteractionException("Interaction with this context is not possible.", user, this);
    }

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
    public void executeMenuOption(User user, int menuOption, String[] args) throws IllegalInteractionException, IllegalMenuActionException {
        throw new IllegalInteractionException("Interaction with this context is not possible.", user);
    }

    /**
     * Fügt dem Kontext einen privaten Raum hinzu.
     * @param privateRoom Hinzuzufügender privater Raum.
     */
    public void addPrivateRoom(SpatialContext privateRoom) {
        privateRooms.put(privateRoom.getContextId(), privateRoom);
        children.put(privateRoom.getContextId(), privateRoom);
        // Hier noch an alle clienten neue Liste senden...
    }

    /**
     * Entfernt einen privaten Raum aus dem Kontext.
     * @param privateRoom Zu entfernender privater Raum.
     */
    public void removePrivateRoom(SpatialContext privateRoom) {
        privateRooms.remove(privateRoom.getContextId());
        children.remove(privateRoom.getContextId());
        // Hier noch alle clienten neue Liste senden...
    }

    /**
     * Fügt eine Reservierung zum Erhalt der Rolle des Bereichsberechtigten zu diesem Kontext hinzu.
     * @param user Reservierter Benutzer.
     * @param from Reservierter Anfangszeitpunkt.
     * @param to Reservierter Endzeitpunkt.
     */
    public void addReservation(User user, LocalDateTime from, LocalDateTime to) {
        AreaReservation reservation = new AreaReservation(user, from, to);
        areaReservations.add(reservation);
        TimedEventScheduler.getInstance().put(new AreaManagerAssignment(this, reservation));
    }

    /**
     * Entfernt eine Reservierung zum Erhalt der Rolle des Bereichsberechtigten.
     * @param reservation
     */
    public void removeReservation(AreaReservation reservation) {
        areaReservations.remove(reservation);
        TimedEventScheduler.getInstance().put(new AreaManagerWithdrawal(this, reservation));
    }

    /**
     * Überprüft, ob ein Benutzer mit diesem Kontext interagieren darf.
     * @param user Zu überprüfender Benutzer.
     * @return true, wenn der Benutzer mit dem Kontext interagieren darf, sonst false.
     */
    public boolean canInteract(User user) {
        Location userLocation = user.getLocation();
        return user.isInteractingWith(null) && isInteractable &&
                expanse.isAround(userLocation.getPosX(), userLocation.getPosY(), INTERACTION_DISTANCE);
    }

    /**
     * Überprüft, ob dieser Kontext einen privaten Raum enthält.
     * @param privateRoom Zu überprüfender privater Raum.
     * @return true, wenn der Kontext den privaten Raum enthält, sonst false.
     */
    public boolean containsPrivateRoom(SpatialContext privateRoom) {
        return privateRooms.containsKey(privateRoom.getContextId());
    }

    /**
     * Überprüft, ob ein übergebenes Passwort mit dem Passwort dieses Kontextes übereinstimmt.
     * @param password Zu überprüfendes Passwort.
     * @return true, wenn das Passwort übereinstimmt oder false, wenn dieser Kontext kein Passwort hat oder es nicht
     * übereinstimmt.
     */
    public boolean checkPassword(String password) {
        if (password == null) {
            return false;
        }
        return this.password.equals(password);
    }

    /**
     * Gibt einen privaten Raum zu übergebener ID zurück.
     * @param roomID ID des privaten Raums.
     * @return Privater Raum.
     */
    public SpatialContext getPrivateRoom(ContextID roomID) {
        return privateRooms.get(roomID);
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

    /**
     * Gibt die diesem räumlichen Kontext übergeordnete Welt zurück.
     * @return Diesem räumlichen Kontext übergeordnete Welt.
     */
    public SpatialContext getWorld() {
        return world;
    }

    /**
     * Gibt den untergeordnetsten Kontext zurück, auf dem sich die übergebenen Koordinaten befinden.
     * @param posX X-Koordinate.
     * @param posY Y-Koordinate.
     * @return Untergeordnetster Kontext, auf dem sich die Koordinaten befinden.
     */
    public SpatialContext getArea(int posX, int posY) {
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
     * Gibt zurück, ob die Position auf der Karte dieses Kontextes an den übergebenen Koordinaten erlaubt ist.
     * @param posX X-Koordinate.
     * @param posY Y-Koordinate.
     * @return true, wenn die Position erlaubt ist, sonst false.
     */
    public boolean isLegal(int posX, int posY) {
        return collisionMap[posX][posY];
    }

    /**
     * Gibt die Anfangsposition auf der Karte dieses Kontextes zurück.
     * @return Anfangsposition auf der Karte dieses Kontextes.
     */
    public Location getSpawnLocation() {
        return spawnLocation;
    }

    /**
     * Gibt zurück, ob dieser Kontext privat ist.
     * @return true, wenn der Kontext privat ist, sonst false.
     */
    public boolean isPrivate() {
        return isPrivate;
    }

    /**
     * Initialisiert die Kontexthierarchie dieses Kontextes anhand der Karte.
     */
    private void initializeMap() {
        // TODO

        TmxMapLoader mapLoader = new TmxMapLoader();
        TiledMap tiledMap = mapLoader.load("maps/map.tmx");
        //Array<RectangleMapObject> contexts = tiledMap.getLayers().get("Borders").getObjects().getByType(RectangleMapObject.class);

        int levels = (int) tiledMap.getProperties().get("levels");
        int offset = (int) tiledMap.getProperties().get("offset");
        for (int i = offset; i < offset + levels; i++) {
            MapLayer layer = tiledMap.getLayers().get(i);
            Array<RectangleMapObject> contexts = layer.getObjects().getByType(RectangleMapObject.class);
            contexts.forEach(context -> {
            });
        }
    }

    @Override
    public void addUser(User user) {
        // Wenn ein neuer Raum betreten wird, sende Positionsinformationen.
        if (spatialContextType.equals(SpatialContextType.WORLD) || spatialContextType.equals(SpatialContextType.ROOM)) {
            containedUsers.values().forEach(containedUser -> {
                containedUser.getClientSender().send(ClientSender.SendAction.AVATAR_MOVE, null);
                user.getClientSender().send(ClientSender.SendAction.AVATAR_MOVE, containedUser);
                });
            if (isPrivate) {
                TextMessage infoMessage = new TextMessage(new MessageBundle("key"));
                containedUsers.values().forEach(containedUser -> {
                    containedUser.getClientSender().send(ClientSender.SendAction.MESSAGE, infoMessage);
                });
            }
        }
        super.addUser(user);
    }

    @Override
    public void removeUser(User user) {
        super.removeUser(user);
        // Sende Pakete zum entfernen des Avatars im alten Raum.7
        if (spatialContextType.equals(SpatialContextType.WORLD) || spatialContextType.equals(SpatialContextType.ROOM)) {
            containedUsers.values().forEach(containedUser -> {
                containedUser.getClientSender().send(ClientSender.SendAction.AVATAR_REMOVE, null);
            });
            if (isPrivate) {
                if (user.hasRole(this, Role.ROOM_OWNER)) {
                    user.removeRole(this, Role.ROOM_OWNER);
                    if (containedUsers.isEmpty()) {
                        world.removePrivateRoom(this);
                    } else {
                        containedUsers.values().stream().findFirst().get().addRole(this, Role.ROOM_OWNER);
                    }
                }
            }
        }
    }
}