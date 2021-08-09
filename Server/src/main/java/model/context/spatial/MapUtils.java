package model.context.spatial;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.utils.Array;
import model.communication.*;
import model.context.spatial.objects.*;

import java.util.*;

/**
 * Eine Hilfsklasse, die Methoden bereitstellt, die mit Hilfe von Informationen einer TiledMap einen Raum initialisiert
 * und die Kontexthierarchie aufbaut.
 */
public class MapUtils {

    private MapUtils() {
    }

    /**
     * Ermittelt die Kommunikationsform anhand einer TiledMap
     * @param properties TiledMap, die die Informationen über die Kommunikationsform enthält.
     * @return Kommunikationsform, die in der TiledMap enthalten ist.
     */
    public static CommunicationRegion getCommunicationRegion(MapProperties properties) {
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

    /**
     * Ermittelt die Menge der verfügbaren Kommunikationsmedien anhand einer TiledMap.
     * @param properties TiledMap, die die Informationen über die Menge der verfügbaren Kommunikationsmedien enthält.
     * @return Menge der verfügbaren Kommunikationsmedien, die in der TiledMap enthalten sind.
     */
    public static Set<CommunicationMedium> getCommunicationMedia(MapProperties properties) {
        Set<CommunicationMedium> communicationMedia = new HashSet<>();
        if (properties.get("text", Boolean.class)) {
            communicationMedia.add(CommunicationMedium.TEXT);
        }
        if (properties.get("voice", Boolean.class)) {
            communicationMedia.add(CommunicationMedium.VOICE);
        }
        return communicationMedia;
    }

    public static int getWidth(TiledMap tiledMap) {
        TiledMapTileLayer layer = (TiledMapTileLayer) tiledMap.getLayers().get(0);
        return layer.getWidth();
    }

    public static int getHeight(TiledMap tiledMap) {
        TiledMapTileLayer layer = (TiledMapTileLayer) tiledMap.getLayers().get(0);
        return layer.getHeight();
    }

    public static boolean[][] getCollisionMap(TiledMap tiledMap) {
        TiledMapTileLayer collisionLayer = (TiledMapTileLayer) tiledMap.getLayers().get("collision");
        int width = collisionLayer.getWidth();
        int height = collisionLayer.getHeight();
        boolean[][] collisionMap = new boolean[width][height];
        Array<RectangleMapObject> collisionRectangles = collisionLayer.getObjects().getByType(RectangleMapObject.class);
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

    public static int getSpawnPosX(TiledMap tiledMap) {
        return tiledMap.getProperties().get("spawnPosX", Integer.class);
    }

    public static int getSpawnPosY(TiledMap tiledMap) {
        return tiledMap.getProperties().get("spawnPosY", Integer.class);
    }

    /**
     * Methode, mit der alle untergeordneten Kontexte eines Raums erzeugt werden.
     * @param room Raum, dessen untergeordnete Kontexte erzeugt werden sollen.
     * @param tiledMap TiledMap, in der die nötigen Daten enthalten sind.
     */
    public static void buildChildTree(Room room, TiledMap tiledMap) {
        // Hole die Quadrate der Kontexte aus der TiledMap.
        MapLayer contextLayer = tiledMap.getLayers().get("contextLayer");
        Array<RectangleMapObject> contextRectangles = contextLayer.getObjects().getByType(RectangleMapObject.class);
        // Sortiere die Quadrate ihrer Größe nach absteigend. Dadurch wird garantiert, dass die Kontexte immer an die
        // richtige Stelle in der Kontexthierarchie eingefügt werden.
        contextRectangles.sort((o1, o2) -> {
            float size1 = o1.getRectangle().getX() * o1.getRectangle().getY();
            float size2 = o2.getRectangle().getX() * o2.getRectangle().getY();
            return Float.compare(size2, size1);
        });
        // Erzeuge alle Kontexte nacheinander.
        contextRectangles.forEach(contextRectangle -> {
            createByType(room, contextRectangle);
        });
    }

    /**
     * Ermittelt die Klasse und die Daten von zu erzeugenden Kontexten anhand von Quadraten einer TiledMap und erzeugt
     * diese.
     * @param room Übergeordneter Raum der zu erzeugenden Kontexte.
     * @param contextRectangle Quadrat der TiledMap, das die Informationen über den Kontext enthält.
     */
    private static void createByType(Room room, RectangleMapObject contextRectangle) {
        // Ermittle alle Parameter zur Erzeugung des Kontextes.
        String areaName = contextRectangle.getName();
        CommunicationRegion communicationRegion = getCommunicationRegion(contextRectangle.getProperties());
        Set<CommunicationMedium> communicationMedia = getCommunicationMedia(contextRectangle.getProperties());
        int posX = (int) contextRectangle.getRectangle().getX();
        int posY = (int) contextRectangle.getRectangle().getY();
        int width = (int) contextRectangle.getRectangle().getWidth();
        int height = (int) contextRectangle.getRectangle().getHeight();
        Expanse expanse = new Expanse(new Location(room, posX, posY), width, height);
        // Ermittle den übergeordneten Kontext. Da die Quadrate absteigend ihrer Grö0e sortiert eingefügt werden,
        // befindet sich der übergeordnete Kontext immer bereits in der Kontexthierarchie.
        Area parent = room.getArea(posX, posY);
        String className = contextRectangle.getProperties().get("contextType", String.class);
        switch (className) {
            case "area": // Erzeuge einfachen Bereich.
                Area area = new Area(areaName, parent, room.getWorld(), communicationRegion, communicationMedia, expanse);
                parent.addChild(area);
                break;
            case "areaPlanner": // Erzeuge AreaPlanner.
                AreaPlanner areaPlanner = new AreaPlanner(areaName, parent, communicationRegion, communicationMedia, expanse);
                parent.addInteractable(areaPlanner);
                break;
            case "gameBoard": // Erzeuge GameBoard.
                GameBoard gameBoard = new GameBoard(areaName, parent, communicationRegion, communicationMedia, expanse);
                parent.addInteractable(gameBoard);
                break;
            case "musicPlayer": // Erzeuge MusicPlayer.
                MusicPlayer musicPlayer = new MusicPlayer(areaName, parent, communicationRegion, communicationMedia, expanse);
                parent.addInteractable(musicPlayer);
                break;
            case "portal": // Erzeuge Portal.
                Portal portal = new Portal(areaName, parent, communicationRegion, communicationMedia, expanse);
                parent.addInteractable(portal);
                break;
            case "roomReception": // Erzeuge RoomReception.
                RoomReception roomReception = new RoomReception(areaName, parent, communicationRegion, communicationMedia, expanse);
                parent.addInteractable(roomReception);
                break;
            case "seat": // Erzeuge Seat.
                Seat seat = new Seat(areaName, parent, communicationRegion, communicationMedia, expanse);
                parent.addInteractable(seat);
                break;
            default: // Zu erzeugender Kontext existiert nicht.
                throw new IllegalArgumentException();
        }
    }
}
