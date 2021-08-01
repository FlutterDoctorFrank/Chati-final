package model.context.spatial;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.utils.Array;
import model.communication.*;
import model.context.spatial.objects.*;

import java.util.*;

public class MapInitializer {

    private MapInitializer() {
    }

    public static SpatialContext buildMap(String roomName, SpatialMap map) {
        TmxMapLoader mapLoader = new TmxMapLoader();
        TiledMap tiledMap = mapLoader.load(map.getPath());
        MapProperties roomProperties = tiledMap.getProperties();

        // Initialisiere den Raum.
        CommunicationRegion roomCommunicationRegion = getRegion(roomProperties);
        Set<CommunicationMedium> roomCommunicationMedia = getMedia(roomProperties);
        int roomWidth = roomProperties.get("width", Integer.class);
        int roomHeight = roomProperties.get("height", Integer.class);
        Expanse roomExpanse = new Expanse(new Location(null, 0, 0), roomWidth, roomHeight);
        SpatialContext room = new SpatialContext(roomName, map, roomExpanse, roomCommunicationRegion, roomCommunicationMedia);

        MapLayer objectLayer = tiledMap.getLayers().get("contextLayer");
        Array<RectangleMapObject> contextRectangles = objectLayer.getObjects().getByType(RectangleMapObject.class);
        contextRectangles.sort(new Comparator<RectangleMapObject>() {
            @Override
            public int compare(RectangleMapObject o1, RectangleMapObject o2) {
                int size1 = (int) (o1.getRectangle().getX() * o1.getRectangle().getY());
                int size2 = (int) (o2.getRectangle().getX() * o2.getRectangle().getY());
                return Integer.compare(size2, size1);
            }
        });
        contextRectangles.forEach(contextRectangle -> {
            createByType(room, contextRectangle);
        });
        return room;
    }

    private static void createByType(SpatialContext room, RectangleMapObject contextRectangle) {
        String contextName = contextRectangle.getName();
        CommunicationRegion communicationRegion = getRegion(contextRectangle.getProperties());
        Set<CommunicationMedium> communicationMedia = getMedia(contextRectangle.getProperties());
        String className = contextRectangle.getProperties().get("contextType", String.class);
        int posX = (int) contextRectangle.getRectangle().getX();
        int posY = (int) contextRectangle.getRectangle().getY();
        int width = (int) contextRectangle.getRectangle().getWidth();
        int height = (int) contextRectangle.getRectangle().getHeight();
        Expanse expanse = new Expanse(new Location(room, posX, posY), width, height);
        SpatialContext parent = room.getArea(posX, posY);

        switch (className) {
            case "area":
                new SpatialContext(contextName, parent, expanse, communicationRegion, communicationMedia);
                break;
            case "areaPlanner":
                new AreaPlanner(contextName, parent, expanse, communicationRegion, communicationMedia);
                break;
            case "gameBoard":
                new GameBoard(contextName, parent, expanse, communicationRegion, communicationMedia);
                break;
            case "musicPlayer":
                new MusicPlayer(contextName, parent, expanse, communicationRegion, communicationMedia);
                break;
            case "portal":
                new Portal(contextName, parent, expanse, communicationRegion, communicationMedia);
                break;
            case "roomReception":
                new RoomReception(contextName, parent, expanse, communicationRegion, communicationMedia);
                break;
            case "seat":
                new Seat(contextName, parent, expanse, communicationRegion, communicationMedia);
                break;
            default:
                throw new IllegalArgumentException();
        }
    }

    private static CommunicationRegion getRegion(MapProperties properties) {
        String className = properties.get("communicationRegion", String.class);
        switch (className) {
            case "areaCommunication":
                return new AreaCommunication();
            case "radiusCommunication":
                return new RadiusCommunication();
            case "parentCommunication":
                return new ParentCommunication();
            default:
                throw new IllegalArgumentException();
        }
    }

    private static Set<CommunicationMedium> getMedia(MapProperties properties) {
        Set<CommunicationMedium> communicationMedia = new HashSet<>();
        if (properties.get("text", Boolean.class)) {
            communicationMedia.add(CommunicationMedium.TEXT);
        }
        if (properties.get("voice", Boolean.class)) {
            communicationMedia.add(CommunicationMedium.VOICE);
        }
        return communicationMedia;
    }
}
