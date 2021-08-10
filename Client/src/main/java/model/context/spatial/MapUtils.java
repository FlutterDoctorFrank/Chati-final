package model.context.spatial;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.utils.Array;
import model.communication.CommunicationMedium;
import model.communication.CommunicationRegion;

import java.util.HashSet;
import java.util.Set;

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
                return CommunicationRegion.AREA;
            case "radiusCommunication":
                return CommunicationRegion.RADIAL;
            case "parentCommunication":
                return CommunicationRegion.PARENT;
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

    /**
     * Methode, mit der alle untergeordneten Kontexte eines Raums erzeugt werden.
     * @param room Raum, dessen untergeordnete Kontexte erzeugt werden sollen.
     * @param tiledMap TiledMap, in der die nötigen Daten enthalten sind.
     */
    public static void buildChildTree(SpatialContext room, TiledMap tiledMap) {
        // Hole die Quadrate der Kontexte aus der TiledMap.
        MapLayer contextLayer = tiledMap.getLayers().get("contextLayer");
        Array<RectangleMapObject> contextRectangles = contextLayer.getObjects().getByType(RectangleMapObject.class);
        // Sortiere die Quadrate ihrer Größe nach absteigend. Dadurch wird garantiert, dass die Kontexte immer an die
        // richtige Stelle in der Kontexthierarchie eingefügt werden.
        contextRectangles.sort((o1, o2) -> {
            float size1 = o1.getRectangle().getHeight() * o1.getRectangle().getWidth();
            float size2 = o2.getRectangle().getHeight() * o2.getRectangle().getWidth();
            return Float.compare(size2, size1);
        });
        contextRectangles.forEach(contextRectangle -> System.out.println(contextRectangle.getName()));
        // Erzeuge alle Kontexte nacheinander.
        contextRectangles.forEach(contextRectangle -> {
            create(room, contextRectangle);
        });
    }

    /**
     * Ermittelt die Klasse und die Daten von zu erzeugenden Kontexten anhand von Quadraten einer TiledMap und erzeugt
     * diese.
     * @param room Übergeordneter Raum der zu erzeugenden Kontexte.
     * @param contextRectangle Quadrat der TiledMap, das die Informationen über den Kontext enthält.
     */
    private static void create(SpatialContext room, RectangleMapObject contextRectangle) {
        // Ermittle alle Parameter zur Erzeugung des Kontextes.
        String contextName = contextRectangle.getName();
        CommunicationRegion communicationRegion = getCommunicationRegion(contextRectangle.getProperties());
        Set<CommunicationMedium> communicationMedia = getCommunicationMedia(contextRectangle.getProperties());
        int posX = (int) contextRectangle.getRectangle().getX();
        int posY = (int) contextRectangle.getRectangle().getY();
        int width = (int) contextRectangle.getRectangle().getWidth();
        int height = (int) contextRectangle.getRectangle().getHeight();
        Expanse expanse = new Expanse(new Location(posX, posY), width, height);
        // Ermittle den übergeordneten Kontext. Da die Quadrate absteigend ihrer Grö0e sortiert eingefügt werden,
        // befindet sich der übergeordnete Kontext immer bereits in der Kontexthierarchie.
        SpatialContext parent = room.getArea(posX, posY);
        SpatialContext child = new SpatialContext(contextName, parent, communicationRegion, communicationMedia, expanse);
        parent.addChild(child);

        System.out.println(child.getContextId().getId() + "  parent:" + parent.getContextId().getId());
    }
}