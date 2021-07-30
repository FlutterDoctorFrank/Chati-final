package model.context.spatial;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.utils.Array;
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

    /** Die maximale Distanz, über die eine Interaktion erfolgen darf. */
    protected static final int INTERACTION_DISTANCE = 1;

    /*
        General Context parameters
     */

    /** Typ des räumlichen Kontextes. */
    private final SpatialContextType spatialContextType;

    /*
        Parameters only relevant for type 'AREA' and 'OBJECT'
     */

    /** Die Information, ob eine Bewegung in diesem räumlichen Kontext erlaubt ist. */
    private boolean isMoveable;

    /** Die Information, ob eine Interaktion mit diesem räumlichen Kontext möglich ist. */
    private boolean isInteractable;

    /** Das Menü, dass beim Benutzer bei einer Interaktion mit diesem Kontext geöffnet werden soll. */
    private Menu menu;

    /** Die Po*/
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
    public SpatialContext(String worldName, SpatialMap map, CommunicationRegion communicationRegion,
                          Set<CommunicationMedium> communicationMedia) {
        super(worldName, GlobalContext.getInstance(), communicationRegion, communicationMedia);
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
    public SpatialContext(String roomName, SpatialContext world, SpatialMap map, String password,
                          CommunicationRegion communicationRegion, Set<CommunicationMedium> communicationMedia) {
        super(roomName, world, communicationRegion, communicationMedia);
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
    public SpatialContext(String areaName, SpatialContext parent, CommunicationRegion communicationRegion,
                          Set<CommunicationMedium> communicationMedia) {
        super(areaName, parent, communicationRegion, communicationMedia);
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
    protected SpatialContext(String objectName, SpatialContext parent, Menu menu, Location interactionLocation,
                             CommunicationRegion communicationRegion, Set<CommunicationMedium> communicationMedia) {
        super(objectName, parent, communicationRegion, communicationMedia);
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
    public SpatialContextType getSpatialContextType() {
        return null;
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
        privateRooms.put(privateRoom.getContextId(), privateRoom);
    }

    public void removePrivateRoom(SpatialContext privateRoom) {
        privateRooms.remove(privateRoom.getContextId());
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
        return privateRooms.containsKey(privateRoom.getContextId());
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