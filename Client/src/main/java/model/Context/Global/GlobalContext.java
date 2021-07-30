package model.Context.Global;

import model.Context.Context;
import model.Context.Spatial.ISpatialContextView;
import model.Context.Spatial.SpatialContext;
import model.Exceptions.ContextNotFoundException;
import model.context.ContextID;
import model.context.spatial.Music;
import model.context.spatial.SpatialMap;
import view.Screens.IModelObserver;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Eine Klasse, welche den globalen Kontext der Anwendung repräsentiert. Kann nur einmal
 * instanziiert werden.
 */
public class GlobalContext extends Context implements IGlobalContextView, IGlobalContextController {

    static private GlobalContext global;
    private Map<ContextID, SpatialContext> worlds;
    private SpatialContext currentWorld;
    private SpatialContext currentRoom;
    private IModelObserver iModelObserver;


    private GlobalContext() {
        super("global", null, new ContextID("global"));
        this.worlds = new HashMap<>();
    }


    @Override
    public void updateWorlds(Map<ContextID, String> worlds) {
        this.worlds.clear();
        worlds.forEach((worldId, world) -> this.worlds.put(worldId, new SpatialContext(world,
                GlobalContext.getInstance(), worldId)));

    }

    @Override
    public void updateRooms(ContextID worldId, Map<ContextID,String> privateRooms) throws ContextNotFoundException {
        checkContextExistIn(worldId, worlds, "world with this id doesn't exist!");
        Map<ContextID, SpatialContext> rooms = new HashMap<>();
        privateRooms.forEach((roomId, room) -> rooms.put(worldId, new SpatialContext(room,
                worlds.get(worldId), roomId)));
        worlds.get(worldId).setRooms(rooms);
        iModelObserver.setRoomInfoChanged();
    }

    @Override
    public void setWorld(ContextID worldId) throws ContextNotFoundException {
        checkContextExistIn(worldId, worlds, "world with this id doesn't exist!");
        currentWorld = worlds.get(worldId);
        iModelObserver.setMapChanged();
    }

    @Override
    public void setRoom(ContextID roomId, SpatialMap map) throws ContextNotFoundException {
        checkContextExistIn(roomId, currentWorld.getRooms(), "room with this id doesn't exist!");
        currentRoom = currentWorld.getRooms().get(roomId);
        currentRoom.setMap(map);
        iModelObserver.setMapChanged();
    }

    @Override
    public void setMusic(ContextID spatialId, Music music) throws ContextNotFoundException {
        Context context = currentWorld.getContext(spatialId);
        if(context == null){
            throw new ContextNotFoundException("This Id doesn't map to any context!", spatialId);
        }
        context.setMusic(music);
    }

    @Override
    public Map<ContextID, ISpatialContextView> getWorlds() {
        return worlds.entrySet().stream().collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Override
    public ISpatialContextView getCurrentWold() {
        return currentWorld;
    }

    @Override
    public ISpatialContextView getCurrentRoom() {
        return currentRoom;
    }


    /**
     * Gibt die Singleton-Instanz der Klasse zurück.
     * @return Die Instanz von GlobalContext.
     */
    public static GlobalContext getInstance(){
        if (global == null){
            global = new GlobalContext();
        }
        return global;
    }


    public SpatialContext getWorld() {
        return currentWorld;
    }

    public SpatialContext getRoom() { return currentRoom; }

    public void setIModelObserver(IModelObserver iModelObserver) {
        this.iModelObserver = iModelObserver;
    }

    /**
     * Wirft eine ContextNotFoundException mit der Nachricht message, wenn contectId nicht in contextMap enthalten
     * @param contextId ID des Contexts
     * @param contextMap Map von Contexten
     * @param message Nachricht der Exception
     * @throws ContextNotFoundException wenn contectId nicht in contextMap enthalten
     */
    private void checkContextExistIn(ContextID contextId, Map contextMap, String message) throws ContextNotFoundException{
        if(!contextMap.containsKey(contextId)){
            throw  new ContextNotFoundException(message, contextId);
        }
    }
}
