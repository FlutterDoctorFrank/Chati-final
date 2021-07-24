package model.context.spatial;

import model.communication.CommunicationMedium;
import model.communication.CommunicationRegion;
import model.context.Context;
import model.context.ContextID;
import model.exception.IllegalInteractionException;
import model.exception.IllegalMenuActionException;
import model.user.User;

import java.util.Set;
import java.util.UUID;

public class SpatialContext extends Context implements ISpatialContext {
    private static final int INTERACTION_DISTANCE = 1;
    /*
        General Context parameters
     */
    private SpatialContextType spatialContextType;
    private int maxUserCount;
    private CommunicationRegion communicationRegion;
    private Set<CommunicationMedium> communicationMedia;
    /*
        Parameters only relevant for type 'AREA' and 'OBJECT'
     */
    private boolean isMoveable;
    private boolean isManageable;
    private boolean isInteractable;
    private Menu menu;
    private Location interactionLocation;
    /*
        Parameters relevant for type 'ROOM' and 'WORLD'
     */
    private Map map;
    private boolean[][] collisionMap;
    private SpatialContext[][] areaMap;
    private boolean isPrivate;
    private String password;
    private Location spawnLocation;
    private java.util.Map<ContextID, SpatialContext> privateRooms;

    protected SpatialContext(String contextName, Context parent, java.util.Map<ContextID, SpatialContext> children) {
        super(contextName, parent, children);
    }

    @Override
    public Map getMap() {
        return map;
    }

    public void interact(User user) throws IllegalInteractionException {
        throw new IllegalInteractionException("Interaction not possible.", user);
    }

    public void executeMenuOption(User user, int menuOption, String[] args) throws IllegalInteractionException, IllegalMenuActionException {
        throw new IllegalInteractionException("Interaction not possible.", user);
    }

    public void addPrivateRoom(SpatialContext privateRoom) {
        privateRooms.put(privateRoom.getContextID(), privateRoom);
    }

    public void removePrivateRoom(SpatialContext privateRoom) {
        privateRooms.remove(privateRoom.getContextID());
    }

    public SpatialContext getArea(int posX, int posY) {
        return null; // TODO
    }

    public SpatialContext getArea(Location location) {
        return null; // TODO
    }

    public SpatialContext getInteractable(int posX, int posY, ContextID spatialID) {
        return null; // TODO
    }

    public SpatialContext getInteractable(Location location, ContextID spatialID) {
        return null; // TODO
    }

    public boolean isLegal(int posX, int posY) {
        return false; // TODO
    }

    public boolean isLegal(Location location) {
        return false; // TODO
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

    public java.util.Map<UUID, User> getCommunicableUsers(User communicatingUser) {
        return communicationRegion.getCommunicableUsers(communicatingUser);
    }

    public boolean canCommunicateWith(CommunicationMedium medium) {
        return communicationMedia.contains(medium);
    }

    public Location getInteractionLocation() {
        return interactionLocation;
    }

    public boolean canInteract(User user) {
        return isInteractable && user.getLocation().distance(interactionLocation) <= INTERACTION_DISTANCE;
    }
}