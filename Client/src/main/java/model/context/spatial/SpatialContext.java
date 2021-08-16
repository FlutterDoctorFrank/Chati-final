package model.context.spatial;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import model.communication.CommunicationRegion;
import model.context.Context;
import model.communication.CommunicationMedium;
import model.context.ContextID;
import model.user.UserManager;

import java.util.NoSuchElementException;
import java.util.Set;

/**
 * Eine Klasse, welche einen räumlichen Kontext der Anwendung repräsentiert.
 */
public class SpatialContext extends Context implements ISpatialContextView {

    /** Die räumliche Ausdehnung dieses Kontextes. */
    private Expanse expanse;

    /** Die in diesem Kontext geltende Kommunikationsform. */
    private CommunicationRegion communicationRegion;

    /** Die in diesem Kontext benutzbaren Kommunikationsmedien. */
    private Set<CommunicationMedium> communicationMedia;

    /** Karte dieses Kontextes. */
    private SpatialMap map;

    /**
     * Erzeugt eine neue Instanz eines räumlichen Kontextes.
     * @param contextName Name des Kontextes.
     * @param parent Übergeordneter Kontext.
     * @param communicationRegion Kommunikationsform des Kontextes.
     * @param communicationMedia Benutzbare Kommunikationsmedien des Kontextes.
     * @param expanse Räumliche Ausdehnung des Kontextes.
     */
    public SpatialContext(String contextName, Context parent, CommunicationRegion communicationRegion,
                          Set<CommunicationMedium> communicationMedia, Expanse expanse) {
        super(contextName, parent);
        this.communicationRegion = communicationRegion;
        this.communicationMedia = communicationMedia;
        this.expanse = expanse;
        this.map = null;
    }

    /**
     * Erzeugt eine neue Instanz eines räumlichen Kontextes.
     * @param contextId ID des Kontextes.
     * @param contextName Name des Kontextes.
     * @param parent Übergeordneter Kontext.
     */
    public SpatialContext(ContextID contextId, String contextName, Context parent) {
        super(contextId, contextName, parent);
        this.communicationRegion = null;
        this.communicationMedia = null;
        this.expanse = null;
        this.map = null;
    }

    public void build(SpatialMap map) {
        // Raum wurde bereits initialisiert.
        if (this.map != null) {
            return;
        }
        this.map = map;
        System.out.println(map.getPath());
        TiledMap tiledMap = new TmxMapLoader().load(map.getPath());
        this.communicationRegion = MapUtils.getCommunicationRegion(tiledMap.getProperties());
        this.communicationMedia = MapUtils.getCommunicationMedia(tiledMap.getProperties());
        this.expanse = new Expanse(new Location(0, 0), MapUtils.getWidth(tiledMap), MapUtils.getHeight(tiledMap));
        MapUtils.buildChildTree(this, tiledMap);
        UserManager.getInstance().getModelObserver().setRoomChanged();
    }

    /**
     * Gibt den untergeordnetsten Kontext zurück, auf dem sich die übergebenen Koordinaten befinden.
     * @param posX X-Koordinate.
     * @param posY Y-Koordinate.
     * @return Untergeordnetster Kontext, auf dem sich die Koordinaten befinden.
     */
    public SpatialContext getArea(int posX, int posY) {
        try {
            return children.values().stream().filter(child -> child.getExpanse().isIn(posX, posY))
                    .findFirst().orElseThrow().getArea(posX, posY);
        } catch (NoSuchElementException e) {
            return this;
        }
    }

    /**
     * Gibt die räumliche Ausdehnung dieses Kontextes zurück.
     * @return Räumliche Ausdehnung dieses Kontextes.
     */
    public Expanse getExpanse() {
        return expanse;
    }

    @Override
    public CommunicationRegion getCommunicationRegion() {
        return communicationRegion;
    }

    @Override
    public Set<CommunicationMedium> getCommunicationMedia() {
        return communicationMedia;
    }

    @Override
    public SpatialMap getMap() {
        return map;
    }
}