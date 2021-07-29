package model.context.spatial;

import model.communication.CommunicationMedium;
import model.communication.CommunicationRegion;
import model.context.Context;
import model.context.ContextID;
import model.context.global.GlobalContext;
import model.exception.IllegalInteractionException;
import model.exception.IllegalMenuActionException;
import model.user.User;

import java.time.LocalDateTime;
import java.util.*;

public class SpatialContext extends Context implements ISpatialContext {
    protected static final int INTERACTION_DISTANCE = 1;
    /*
        General Context parameters
     */
    private final SpatialContextType spatialContextType;
    /*
        Parameters only relevant for type 'AREA' and 'OBJECT'
     */
    private boolean isMoveable;
    private boolean isInteractable;
    private Menu menu;
    protected Location interactionLocation;
    private Set<AreaReservation> areaReservations;
    /*
        Parameters relevant for type 'ROOM' and 'WORLD'
     */
    private SpatialMap map;
    private boolean[][] collisionMap;
    private Expanse expanse;
    private boolean isPrivate;
    private String password;
    private Location spawnLocation;
    private java.util.Map<ContextID, SpatialContext> privateRooms;

    // World constructor
    public SpatialContext(String worldName, SpatialMap map) {
        super(worldName, GlobalContext.getInstance());
        GlobalContext.getInstance().addChild(this);
        this.spatialContextType = SpatialContextType.WORLD;
        this.isMoveable = true;
        this.isInteractable = false;
        this.menu = null;
        this.interactionLocation = null;
        this.areaReservations = null;
        this.map = map;
        this.isPrivate = false;
        this.password = null;
        this.privateRooms = new HashMap<>();
        initializeMap();
    }

    // Private Room constructor
    public SpatialContext(String roomName, SpatialContext world, SpatialMap map, String password) {
        super(roomName, world);
        world.addChild(this);
        this.spatialContextType = SpatialContextType.ROOM;
        this.isMoveable = true;
        this.isInteractable = false;
        this.menu = null;
        this.interactionLocation = null;
        this.areaReservations = null;
        this.map = map;
        this.isPrivate = true;
        this.password = password;
        this.privateRooms = null;
        initializeMap();
    }

    // Area constructor
    public SpatialContext(String areaName, SpatialContext parent) {
        super(areaName, parent);
        parent.addChild(this);
        this.spatialContextType = SpatialContextType.AREA;
        this.isMoveable = true;
        this.isInteractable = false;
        this.menu = null;
        this.interactionLocation = null;
        this.areaReservations = new HashSet<>();
        this.map = null;
        this.isPrivate = false;
        this.password = null;
        this.privateRooms = null;
    }

    // Object constructor
    protected SpatialContext(String objectName, SpatialContext parent, Menu menu, Location interactionLocation) {
        super(objectName, parent);
        parent.addChild(this);
        this.spatialContextType = SpatialContextType.OBJECT;
        this.isMoveable = false;
        this.isInteractable = true;
        this.menu = menu;
        this.interactionLocation = interactionLocation;
        this.areaReservations = null;
        this.map = null;
        this.isPrivate = false;
        this.password = null;
        this.privateRooms = null;
    }

    @Override
    public java.util.Map<ContextID, SpatialContext> getPrivateRooms() {
        return Collections.unmodifiableMap(privateRooms);
    }

    @Override
    public SpatialMap getMap() {
        return map;
    }

    public void interact(User user) throws IllegalInteractionException {
        throw new IllegalInteractionException("Interaction with this context is not possible.", user, this);
    }

    public void executeMenuOption(User user, int menuOption, String[] args) throws IllegalInteractionException, IllegalMenuActionException {
        throw new IllegalInteractionException("Interaction with this context is not possible.", user);
    }

    public void addPrivateRoom(SpatialContext privateRoom) {
        privateRooms.put(privateRoom.getContextID(), privateRoom);
    }

    public void removePrivateRoom(SpatialContext privateRoom) {
        privateRooms.remove(privateRoom.getContextID());
    }

    public SpatialContext getArea(int posX, int posY) {
        try {
            return children.entrySet().stream().filter(entry -> entry.getValue().expanse.isIn(posX, posY))
                    .findFirst().orElseThrow().getValue().getArea(posX, posY);
        } catch (NoSuchElementException e) {
            return this;
        }
    }

    public boolean isLegal(int posX, int posY) {
        return collisionMap[posX][posY];
    }

    public Expanse getExpanse() {
        return expanse;
    }

    public Location getSpawnLocation() {
        return spawnLocation;
    }

    public boolean isMoveable() {
        return isMoveable;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public boolean containsPrivateRoom(SpatialContext privateRoom) {
        return privateRooms.containsKey(privateRoom.getContextID());
    }

    public SpatialContext getPrivateRoom(ContextID roomID) {
        return privateRooms.get(roomID);
    }

    public boolean checkPassword(String password) {
        return this.password.equals(password);
    }

    public boolean canInteract(User user) {
        return isInteractable && user.getLocation().distance(interactionLocation) <= INTERACTION_DISTANCE;
    }

    public void initializeMap() {
        // TODO
    }

    public void addReservation(User user, LocalDateTime from, LocalDateTime to) {
        areaReservations.add(new AreaReservation(user, from, to));
    }

    public boolean isReservedBy(User user) {
        return areaReservations.stream().anyMatch(reservation -> reservation.getReserver().equals(user));
    }

    public boolean isReservedAt(LocalDateTime from, LocalDateTime to) {
        return areaReservations.stream().anyMatch(reservation -> reservation.getFrom().equals(from)
            && reservation.getTo().equals(to));
    }

    public boolean isReservedAtBy(User user, LocalDateTime from, LocalDateTime to) {
        return areaReservations.stream().anyMatch(reservation -> reservation.getReserver().equals(user)
                && reservation.getFrom().equals(from) && reservation.getTo().equals(to));
    }
}