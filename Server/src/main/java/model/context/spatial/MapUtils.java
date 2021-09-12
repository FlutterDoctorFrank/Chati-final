package model.context.spatial;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.utils.Array;
import model.communication.AreaCommunication;
import model.communication.CommunicationMedium;
import model.communication.CommunicationRegion;
import model.communication.ParentCommunication;
import model.communication.RadiusCommunication;
import model.context.spatial.objects.AreaPlanner;
import model.context.spatial.objects.GameBoard;
import model.context.spatial.objects.MusicPlayer;
import model.context.spatial.objects.MusicStreamer;
import model.context.spatial.objects.Portal;
import model.context.spatial.objects.RoomReception;
import model.context.spatial.objects.Seat;
import org.jetbrains.annotations.NotNull;
import java.util.BitSet;
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
     * @param room Übergeordneter Raum der zu der die Karte besitzt.
     * @param map Die geladene TiledMap.
     * @return die räumliche Ausdehnung der Karte.
     */
    public static @NotNull Expanse createMapExpanse(@NotNull final Room room, @NotNull final TiledMap map) {
        final TiledMapTileLayer baseLayer = (TiledMapTileLayer) map.getLayers().get(0);
        final float width = baseLayer.getWidth() * baseLayer.getTileWidth();
        final float height = baseLayer.getHeight() * baseLayer.getTileHeight();

        return new Expanse(room, 0, 0, width, height);
    }

    /**
     * Ermittelt die Kollisionen anhand von Quadraten des Collisions MapLayers und erzeugt ein BitSet daraus.
     * @param room Übergeordneter Raum der zu der die Karte besitzt.
     * @param layer MapLayer, in dem die Kollisionen enthalten sind.
     * @return BitSet mit den Kollisionen der Karte.
     */
    public static @NotNull BitSet createCollisionMap(@NotNull final Room room, @NotNull final MapLayer layer) {
        final Array<RectangleMapObject> rectangles = layer.getObjects().getByType(RectangleMapObject.class);
        final BitSet collisions = new BitSet(room.expanse.getWidth() * room.expanse.getHeight());

        for (final RectangleMapObject object : rectangles) {
            final int posX = Math.round(object.getRectangle().getX());
            final int posY = Math.round(object.getRectangle().getY());
            final int width = Math.round(object.getRectangle().getWidth());
            final int height = Math.round(object.getRectangle().getHeight());

            for (int row = posY; row < posY + height; row++) {
                final int base = row * room.expanse.getWidth() + posX;

                collisions.set(base, base + width);
            }
        }

        return collisions;
    }

    /**
     * Ermittelt die zu erzeugenden Kontexte anhand von Quadraten des Contexts MapLayers und erzeugt diese.
     * @param room Übergeordneter Raum der zu erzeugenden Kontexte.
     * @param layer MapLayer, in dem die Kontexte enthalten sind.
     */
    public static void createContexts(@NotNull final Room room, @NotNull final MapLayer layer) {
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
            Area parent = room.getArea(posX + width / 2, posY + height / 2);
            Expanse expanse = new Expanse(room, posX, posY, width, height);

            try {
                if (!object.getProperties().containsKey("type")) {
                    throw new IllegalArgumentException("Properties does not contain type");
                }

                Set<CommunicationMedium> media = parseMedia(object.getProperties());
                CommunicationRegion communication = parseCommunication(object.getProperties());
                Area context;

                switch (object.getProperties().get("type", String.class).toLowerCase()) {
                    case "area":
                        context = new Area(name, parent, room.getWorld(), communication, media, expanse);
                        break;

                    case "area_planner":
                        context = new AreaPlanner(name, parent, communication, media, expanse);
                        parent.addInteractable((AreaPlanner) context);
                        break;

                    case "game_board":
                        context = new GameBoard(name, parent, communication, media, expanse);
                        parent.addInteractable((GameBoard) context);
                        break;

                    case "music_player":
                        context = new MusicPlayer(name, parent, communication, media, expanse);
                        parent.addInteractable((MusicPlayer) context);
                        break;

                    case "music_streamer":
                        context = new MusicStreamer(name, parent, communication, media, expanse);
                        parent.addInteractable((MusicStreamer) context);
                        break;

                    case "portal":
                        Location destination = parseLocation(room.getWorld().getPublicRoom(), object.getProperties());
                        context = new Portal(name, parent, communication, media, expanse, destination);
                        parent.addInteractable((Portal) context);
                        break;

                    case "room_reception":
                        context = new RoomReception(name, parent, communication, media, expanse);
                        parent.addInteractable((RoomReception) context);
                        break;

                    case "seat":
                        Location place = parseLocation(room, object.getProperties());
                        context = new Seat(name, parent, communication, media, expanse, place);
                        parent.addInteractable((Seat) context);
                        break;

                    default:
                        throw new IllegalArgumentException("Properties type contains invalid value");
                }

                parent.addChild(context);
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
    public static @NotNull Set<CommunicationMedium> parseMedia(@NotNull final MapProperties properties) {
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
    public static @NotNull CommunicationRegion parseCommunication(@NotNull final MapProperties properties) {
        if (!properties.containsKey("communication")) {
            throw new IllegalArgumentException("Properties does not contain communication region");
        }

        try {
            switch (properties.get("communication", String.class)) {
                case "area":
                    return new AreaCommunication();

                case "parent":
                    return new ParentCommunication();

                case "radius":
                    if (properties.containsKey("radius")) {
                        try {
                            return new RadiusCommunication(properties.get("radius", Integer.class));
                        } catch (ClassCastException ex) {
                            throw new IllegalArgumentException("Properties communication radius of not of type Integer");
                        }
                    }
                    return new RadiusCommunication();

                default:
                    throw new IllegalArgumentException("Properties communication region contains invalid value");
            }
        } catch (ClassCastException ex) {
            throw new IllegalArgumentException("Properties communication region is not of type String");
        }
    }

    /**
     * Ermittelt eine Location anhand einer MapProperties.
     * @param room Übergeordneter Raum der zu erzeugenden Location.
     * @param properties Die Properties eines Tiled Map Objects.
     * @return Location, die in den Properties enthalten ist.
     */
    public static @NotNull Location parseLocation(@NotNull final Room room, @NotNull final MapProperties properties) throws IllegalArgumentException {
        if (!properties.containsKey("location_x") || !properties.containsKey("location_y")) {
            throw new IllegalArgumentException("Properties does not contain coordinates");
        }

        try {
            Direction direction = Direction.values()[properties.get("location_direction", 0, Integer.class)];
            float posX = properties.get("location_x", Float.class);
            float posY = room.expanse.getHeight() - properties.get("location_y", Float.class);

            return new Location(room, direction, posX, posY);
        } catch (ClassCastException ex) {
            throw new IllegalArgumentException("Properties coordinates/direction are not of type float/int");
        } catch (ArrayIndexOutOfBoundsException ex) {
            throw new IllegalArgumentException("Properties direction contains invalid number", ex);
        }
    }
}
