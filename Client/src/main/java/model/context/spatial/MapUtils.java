package model.context.spatial;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.utils.Array;
import model.communication.CommunicationMedium;
import model.communication.CommunicationRegion;
import org.jetbrains.annotations.NotNull;
import java.util.EnumSet;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Eine Hilfsklasse, die Methoden bereitstellt, die mithilfe von Informationen einer TiledMap einen Raum initialisiert
 * und die Kontexthierarchie aufbaut.
 */
public class MapUtils {

    private static final Logger LOGGER = Logger.getLogger("chati.map-loader");

    private MapUtils() {
    }

    /**
     * Ermittelt die räumliche Ausdehnung einer Karte.
     * @param map Die geladene TiledMap.
     * @return die räumliche Ausdehnung der Karte.
     */
    public static @NotNull Expanse createMapExpanse(@NotNull final TiledMap map) {
        final TiledMapTileLayer baseLayer = (TiledMapTileLayer) map.getLayers().get(0);
        final float width = baseLayer.getWidth() * baseLayer.getTileWidth();
        final float height = baseLayer.getHeight() * baseLayer.getTileHeight();

        return new Expanse(0, 0, width, height);
    }

    /**
     * Ermittelt die zu erzeugenden Kontexte anhand von Quadraten des Contexts MapLayers und erzeugt diese.
     * @param room Übergeordneter Raum der zu erzeugenden Kontexte.
     * @param layer MapLayer, in dem die Kontexte enthalten sind.
     */
    public static void createContexts(@NotNull final SpatialContext room, @NotNull final MapLayer layer) {
        Array<RectangleMapObject> rectangles = layer.getObjects().getByType(RectangleMapObject.class);

        // Sortiere die Quadrate ihrer Größe nach absteigend. Dadurch wird garantiert, dass die Kontexte immer an die
        // richtige Stelle in der Kontexthierarchie eingefügt werden.
        rectangles.sort((prev, next) -> Float.compare(next.getRectangle().area(), prev.getRectangle().area()));

        for (final RectangleMapObject object : rectangles) {
            String name = object.getName();
            float posX = object.getRectangle().getX();
            float posY = object.getRectangle().getY();
            float width = object.getRectangle().getWidth();
            float height = object.getRectangle().getHeight();

            // Ermittle den übergeordneten Kontext. Da die Quadrate absteigend ihrer Grö0e sortiert eingefügt werden,
            // befindet sich der übergeordnete Kontext immer bereits in der Kontexthierarchie.
            SpatialContext parent = room.getArea(posX + width / 2, posY + height / 2);
            Expanse expanse = new Expanse(posX, posY, width, height);

            try {
                if (!object.getProperties().containsKey("type")) {
                    throw new IllegalArgumentException("Properties does not contain type");
                }

                Set<CommunicationMedium> media = parseCommunicationMedia(object.getProperties());
                CommunicationRegion communication = parseCommunicationRegion(object.getProperties());
                boolean interactable = !object.getProperties().get("type", String.class).equalsIgnoreCase("area");

                parent.addChild(new SpatialContext(name, parent, communication, media, expanse, interactable));
            } catch (ClassCastException ex) {
                LOGGER.warning(String.format("Found invalid object %s in map %s: Properties type is not of type String", name, room.getMap()));
            } catch (IllegalArgumentException ex) {
                LOGGER.warning(String.format("Found invalid object %s in map %s: %s", name, room.getMap(), ex.getMessage()));
            }
        }
    }

    /**
     * Ermittelt die Menge der verfügbaren Kommunikationsmedien anhand einer MapProperties.
     * @param properties Die Properties eines Tiled Map Objects.
     * @return Menge der verfügbaren Kommunikationsmedien, die in den Properties enthalten ist.
     */
    public static @NotNull Set<CommunicationMedium> parseCommunicationMedia(@NotNull final MapProperties properties) {
        final Set<CommunicationMedium> media = EnumSet.noneOf(CommunicationMedium.class);

        for (final String medium : properties.get("media", "", String.class).split(";")) {
            try {
                media.add(CommunicationMedium.valueOf(medium.toUpperCase()));
            } catch (IllegalArgumentException ignored) {
            }
        }

        return media;
    }

    /**
     * Ermittelt die Kommunikationsform anhand einer MapProperties.
     * @param properties Die Properties eines Tiled Map Objects.
     * @return Kommunikationsform, die in den Properties enthalten ist.
     */
    public static @NotNull CommunicationRegion parseCommunicationRegion(@NotNull final MapProperties properties) throws IllegalArgumentException {
        if (!properties.containsKey("communication")) {
            throw new IllegalArgumentException("Properties does not contain communication region");
        }

        try {
            return CommunicationRegion.valueOf(properties.get("communication", String.class).toUpperCase());
        } catch (ClassCastException ex) {
            throw new IllegalArgumentException("Properties communication region is not of type String");
        }
    }
}