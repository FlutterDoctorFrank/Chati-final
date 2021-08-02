package model.Context.Global;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.utils.Array;
import model.Communication.CommunicationRegion;
import model.Context.Context;
import model.Context.Spatial.Expanse;
import model.Context.Spatial.ISpatialContextView;
import model.Context.Spatial.Location;
import model.Context.Spatial.SpatialContext;
import model.Exceptions.ContextNotFoundException;
import model.communication.CommunicationMedium;
import model.context.ContextID;
import model.context.spatial.Music;
import model.context.spatial.SpatialMap;
import view.Screens.IModelObserver;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Eine Klasse, welche den globalen Kontext der Anwendung repräsentiert. Kann nur einmal
 * instanziiert werden.
 */
public class GlobalContext extends Context implements IGlobalContextView, IGlobalContextController {

    private static GlobalContext global;
    private static final String GLOBALNAME = "global";
    private SpatialContext currentWorld;
    private SpatialContext currentRoom;
    private IModelObserver iModelObserver;

    private GlobalContext() {
        super(GLOBALNAME, null, null, null);
    }

    @Override
    public void setWorld(String worldName) {
        currentWorld = new SpatialContext(worldName, global, null,
                null, null);
    }

    @Override
    public void setRoom(String roomName, SpatialMap map) {
        TiledMap tiledMap = new TmxMapLoader().load(map.getPath());
        MapLayer objectLayer = tiledMap.getLayers().get("contextLayer");
        Array<RectangleMapObject> contextRectangles = sortByAreaSize(objectLayer.getObjects().
                getByType(RectangleMapObject.class));
        currentRoom = createRoom(tiledMap.getProperties(), roomName, global);
        if (currentRoom.equals(currentWorld)){
            currentWorld = currentRoom;
        } else{
            currentRoom = createRoom(tiledMap.getProperties(), roomName, currentWorld);
        }
        createContexts(contextRectangles);
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
     * Wirft eine ContextNotFoundException mit der Nachricht message, wenn contextId nicht in contextMap enthalten ist
     * @param contextId ID des Contexts
     * @param contextMap Map von Contexten
     * @param message Nachricht der Exception
     * @throws ContextNotFoundException wenn contectId nicht in contextMap enthalten
     */
    private void checkContextExistIn(ContextID contextId, Map<ContextID, SpatialContext> contextMap, String message)
            throws ContextNotFoundException{
        if(!contextMap.containsKey(contextId)){
            throw  new ContextNotFoundException(message, contextId);
        }
    }

    /**
     * sortiert ein Array von Map-Objekten absteigend nach deren Flächeninhalt
     * @param contextRectangles unsortiertes Array
     * @return sortiertes Array
     */
    public Array<RectangleMapObject> sortByAreaSize(Array<RectangleMapObject> contextRectangles){
        contextRectangles.sort((object1, object2) -> {
            int size1 = (int) (object1.getRectangle().getX() * object1.getRectangle().getY());
            int size2 = (int) (object2.getRectangle().getX() * object2.getRectangle().getY());
            return Integer.compare(size2, size1);
        });
        return contextRectangles;
    }

    public IModelObserver getIModelObserver() {
        return iModelObserver;
    }

    /**
     * initialisiert einen Raum anhand der Map-Properties und einem Raumnamen
     * @param roomProperties Map-Properties
     * @param roomName Raumname
     * @param parent Eltern-Kontext
     * @return initialisierter Raum
     */
    private SpatialContext createRoom(MapProperties roomProperties, String roomName, Context parent){
        Expanse roomExpanse = new Expanse(new Location(0, 0), roomProperties.get("width", Integer.class),
                roomProperties.get("height", Integer.class));
        return new SpatialContext(roomName, parent, getRegion(roomProperties),
                getMedia(roomProperties), roomExpanse);
    }

    /**
     * initialisiert den Kontext-Teilbaum unter dem privaten Raum anhand der Map-Struktur
     * @param contextRectangles Map-Struktur
     */
    private void createContexts(Array<RectangleMapObject> contextRectangles){
        contextRectangles.forEach(contextRectangle -> {
            int posX = (int) contextRectangle.getRectangle().getX();
            int posY = (int) contextRectangle.getRectangle().getY();
            Expanse expanse = new Expanse(new Location(posX, posY), (int) contextRectangle.getRectangle().getWidth()
                    ,(int) contextRectangle.getRectangle().getHeight());

            new SpatialContext(contextRectangle.getName(), currentRoom.getArea(posX, posY),
                    getRegion(contextRectangle.getProperties()), getMedia(contextRectangle.getProperties()), expanse);
        });
    }

    /**
     * liest aus den Properties der Map das entsprechende Kommunikations-Medium aus
     * @param properties Propertie
     * @return Kommunikations-Medium
     */
    private Set<CommunicationMedium> getMedia(MapProperties properties) {
        Set<CommunicationMedium> communicationMedia = new HashSet<>();
        if (properties.get("text", Boolean.class)) {
            communicationMedia.add(CommunicationMedium.TEXT);
        }
        if (properties.get("voice", Boolean.class)) {
            communicationMedia.add(CommunicationMedium.VOICE);
        }
        return communicationMedia;
    }

    /**
     * liest aus den Properties der Map die entsprechende Kommunikations-Region aus
     * @param properties Properties der Map
     * @return Kommunikations-Region
     */
    private CommunicationRegion getRegion(MapProperties properties) {
        String className = properties.get("communicationRegion", String.class);
        switch (className) {
            case "areaCommunication":
                return CommunicationRegion.AREA;
            case "radiusCommunication":
                return CommunicationRegion.RADIAL;
            case "parentCommunication":
                return CommunicationRegion.PARENT;
            default:
                throw new IllegalArgumentException();
        }
    }
}
