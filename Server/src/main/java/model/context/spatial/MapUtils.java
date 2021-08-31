package model.context.spatial;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.utils.Array;
import model.communication.*;
import model.context.spatial.objects.*;
import org.jetbrains.annotations.NotNull;
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
    public static @NotNull CommunicationRegion getCommunicationRegion(@NotNull final MapProperties properties) {
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
    public static @NotNull Set<CommunicationMedium> getCommunicationMedia(@NotNull final MapProperties properties) {
        Set<CommunicationMedium> communicationMedia = new HashSet<>();
        if (properties.get("text", Boolean.class)) {
            communicationMedia.add(CommunicationMedium.TEXT);
        }
        if (properties.get("voice", Boolean.class)) {
            communicationMedia.add(CommunicationMedium.VOICE);
        }
        return communicationMedia;
    }

    public static float getWidth(@NotNull final TiledMap tiledMap) {
        TiledMapTileLayer layer = (TiledMapTileLayer) tiledMap.getLayers().get(0);
        return layer.getWidth() * layer.getTileWidth();
    }

    public static float getHeight(@NotNull final TiledMap tiledMap) {
        TiledMapTileLayer layer = (TiledMapTileLayer) tiledMap.getLayers().get(0);
        return layer.getHeight() * layer.getTileHeight();
    }

    /*
    public static boolean[][] getCollisionMap(@NotNull final TiledMap tiledMap) {
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
     */

    public static float getSpawnPosX(@NotNull final TiledMap tiledMap) {
        return tiledMap.getProperties().get("spawnPosX", Float.class);
    }

    public static float getSpawnPosY(@NotNull final TiledMap tiledMap) {
        return tiledMap.getProperties().get("spawnPosY", Float.class);
    }

    /**
     * Methode, mit der alle untergeordneten Kontexte eines Raums erzeugt werden.
     * @param room Raum, dessen untergeordnete Kontexte erzeugt werden sollen.
     * @param tiledMap TiledMap, in der die nötigen Daten enthalten sind.
     */
    public static void buildChildTree(@NotNull final Room room, @NotNull final TiledMap tiledMap) {
        // Hole die Quadrate der Kontexte aus der TiledMap.
        MapLayer contextLayer = tiledMap.getLayers().get("contextLayer");
        Array<RectangleMapObject> contextRectangles = contextLayer.getObjects().getByType(RectangleMapObject.class);
        // Sortiere die Quadrate ihrer Größe nach absteigend. Dadurch wird garantiert, dass die Kontexte immer an die
        // richtige Stelle in der Kontexthierarchie eingefügt werden.
        contextRectangles.sort((o1, o2) -> Float.compare(o2.getRectangle().area(), o1.getRectangle().area()));
        // Erzeuge alle Kontexte nacheinander.
        contextRectangles.forEach(contextRectangle -> createByType(room, contextRectangle, tiledMap));
    }

    /**
     * Ermittelt die Klasse und die Daten von zu erzeugenden Kontexten anhand von Quadraten einer TiledMap und erzeugt
     * diese.
     * @param room Übergeordneter Raum der zu erzeugenden Kontexte.
     * @param contextRectangle Quadrat der TiledMap, das die Informationen über den Kontext enthält.
     * @param tiledMap TiledMap, die weitere notwendige Parameter enthält.
     */
    private static void createByType(@NotNull final Room room, @NotNull final RectangleMapObject contextRectangle, @NotNull TiledMap tiledMap) {
        // Ermittle alle Parameter zur Erzeugung des Kontextes.
        String areaName = contextRectangle.getName();
        CommunicationRegion communicationRegion = getCommunicationRegion(contextRectangle.getProperties());
        Set<CommunicationMedium> communicationMedia = getCommunicationMedia(contextRectangle.getProperties());
        float posX = contextRectangle.getRectangle().getX();
        float posY = contextRectangle.getRectangle().getY();
        float width = contextRectangle.getRectangle().getWidth();
        float height = contextRectangle.getRectangle().getHeight();

        Expanse expanse = new Expanse(new Location(room, posX, posY), width, height);
        // Ermittle den übergeordneten Kontext. Da die Quadrate absteigend ihrer Grö0e sortiert eingefügt werden,
        // befindet sich der übergeordnete Kontext immer bereits in der Kontexthierarchie.
        Area parent = room.getArea(posX + width / 2, posY + height / 2);
        String className = contextRectangle.getProperties().get("contextType", String.class);
        switch (className) {
            case "area": // Erzeuge einfachen Bereich.
                Area area = new Area(areaName, parent, room.getWorld(), communicationRegion, communicationMedia, expanse);
                parent.addChild(area);
                break;
            case "areaPlanner": // Erzeuge AreaPlanner.
                AreaPlanner areaPlanner = new AreaPlanner(areaName, parent, communicationRegion, communicationMedia, expanse);
                parent.addChild(areaPlanner);
                parent.addInteractable(areaPlanner);
                break;
            case "gameBoard": // Erzeuge GameBoard.
                GameBoard gameBoard = new GameBoard(areaName, parent, communicationRegion, communicationMedia, expanse);
                parent.addChild(gameBoard);
                parent.addInteractable(gameBoard);
                break;
            case "musicPlayer": // Erzeuge MusicPlayer.
                MusicPlayer musicPlayer = new MusicPlayer(areaName, parent, communicationRegion, communicationMedia, expanse);
                parent.addChild(musicPlayer);
                parent.addInteractable(musicPlayer);
                break;
            case "portal": // Erzeuge Portal.
                Room publicRoom = room.getWorld().getPublicRoom();
                Location destination = new Location(publicRoom, publicRoom.getSpawnLocation().getPosX(), publicRoom.getSpawnLocation().getPosX());
                Portal portal = new Portal(areaName, parent, communicationRegion, communicationMedia, expanse, destination);
                parent.addChild(portal);
                parent.addInteractable(portal);
                break;
            case "roomReception": // Erzeuge RoomReception.
                RoomReception roomReception = new RoomReception(areaName, parent, communicationRegion, communicationMedia, expanse);
                parent.addChild(roomReception);
                parent.addInteractable(roomReception);
                break;
            case "seat": // Erzeuge Seat.
                Seat seat = new Seat(areaName, parent, communicationRegion, communicationMedia, expanse);
                parent.addChild(seat);
                parent.addInteractable(seat);
                break;
            default: // Zu erzeugender Kontext existiert nicht.
                throw new IllegalArgumentException();
        }
    }
}
