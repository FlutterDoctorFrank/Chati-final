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

public class RoomBuilder {

    private RoomBuilder() {
    }

    public static void build(Room room) {
        TmxMapLoader mapLoader = new TmxMapLoader();
        TiledMap tiledMap = mapLoader.load(room.getMap().getPath());
        MapProperties properties = tiledMap.getProperties();

        room.setCommunicationRegion(getCommunicationRegion(properties));
        room.setCommunicationMedia(getCommunicationMedia(properties));
        room.setExpanse(getExpanse(properties));
        room.setSpawnLocation(getSpawnLocation(properties));
        room.setCollisionMap(getCollisionMap(tiledMap));

        buildChildTree(room, tiledMap);
    }

    private static void buildChildTree(Room room, TiledMap tiledMap) {
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
    }

    private static void createByType(Room room, RectangleMapObject contextRectangle) {
        String areaName = contextRectangle.getName();
        CommunicationRegion communicationRegion = getCommunicationRegion(contextRectangle.getProperties());
        Set<CommunicationMedium> communicationMedia = getCommunicationMedia(contextRectangle.getProperties());
        String className = contextRectangle.getProperties().get("contextType", String.class);
        int posX = (int) contextRectangle.getRectangle().getX();
        int posY = (int) contextRectangle.getRectangle().getY();
        int width = (int) contextRectangle.getRectangle().getWidth();
        int height = (int) contextRectangle.getRectangle().getHeight();
        Expanse expanse = new Expanse(new Location(room, posX, posY), width, height);
        Area parent = room.getArea(posX, posY);

        switch (className) {
            case "area":
                new Area(areaName, parent, room.getWorld(), communicationRegion, communicationMedia, expanse);
                break;
            case "areaPlanner":
                AreaPlanner areaPlanner = new AreaPlanner(areaName, parent, communicationRegion, communicationMedia, expanse);
                parent.addInteractable(areaPlanner);
                break;
            case "gameBoard":
                GameBoard gameBoard = new GameBoard(areaName, parent, communicationRegion, communicationMedia, expanse);
                parent.addInteractable(gameBoard);
                break;
            case "musicPlayer":
                MusicPlayer musicPlayer = new MusicPlayer(areaName, parent, communicationRegion, communicationMedia, expanse);
                parent.addInteractable(musicPlayer);
                break;
            case "portal":
                Portal portal = new Portal(areaName, parent, communicationRegion, communicationMedia, expanse);
                parent.addInteractable(portal);
                break;
            case "roomReception":
                RoomReception roomReception = new RoomReception(areaName, parent, communicationRegion, communicationMedia, expanse);
                parent.addInteractable(roomReception);
                break;
            case "seat":
                Seat seat = new Seat(areaName, parent, communicationRegion, communicationMedia, expanse);
                parent.addInteractable(seat);
                break;
            default:
                throw new IllegalArgumentException();
        }

    }

    private static CommunicationRegion getCommunicationRegion(MapProperties properties) {
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

    private static Set<CommunicationMedium> getCommunicationMedia(MapProperties properties) {
        Set<CommunicationMedium> communicationMedia = new HashSet<>();
        if (properties.get("text", Boolean.class)) {
            communicationMedia.add(CommunicationMedium.TEXT);
        }
        if (properties.get("voice", Boolean.class)) {
            communicationMedia.add(CommunicationMedium.VOICE);
        }
        return communicationMedia;
    }

    private static Expanse getExpanse(MapProperties properties) {
        int width = properties.get("width", Integer.class);
        int height = properties.get("height", Integer.class);
        return new Expanse(new Location(null, 0, 0), width, height);
    }

    private static Location getSpawnLocation(MapProperties properties) {
        int posX = properties.get("spawnPosX", Integer.class);
        int posY = properties.get("spawnPosY", Integer.class);
        return new Location(null, posX, posY);
    }

    private static boolean[][] getCollisionMap(TiledMap tiledMap) {
        MapProperties properties = tiledMap.getProperties();
        MapLayer collisionLayer = tiledMap.getLayers().get("collision");
        Array<RectangleMapObject> collisionRectangles = collisionLayer.getObjects().getByType(RectangleMapObject.class);
        int width = properties.get("width", Integer.class);
        int height = properties.get("height", Integer.class);
        boolean[][] collisionMap = new boolean[width][height];
        collisionRectangles.forEach(rectangle -> {
            int startPosX = (int) rectangle.getRectangle().getX();
            int startPosY = (int) rectangle.getRectangle().getY();
            int rectWidth = (int) rectangle.getRectangle().getWidth();
            int rectHeight = (int) rectangle.getRectangle().getHeight();
            for (int i = startPosX; i < startPosX + rectWidth; i++) {
                for (int j = startPosY; j < startPosY + rectHeight; j++) {
                    collisionMap[i][j] = true;
                }
            }
        });
        return collisionMap;
    }
}
