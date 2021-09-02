package model.context.spatial;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import model.communication.CommunicationRegion;
import model.context.Context;
import model.communication.CommunicationMedium;
import model.context.ContextID;
import model.user.UserManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * Eine Klasse, welche einen räumlichen Kontext der Anwendung repräsentiert.
 */
public class SpatialContext extends Context implements ISpatialContextView {

    /** Der Abstand, den der Benutzer maximal von einem Objekt entfernt sein darf, um mit diesem zu interagieren. */
    public static final float INTERACTION_DISTANCE = 32;

    /** Die räumliche Ausdehnung dieses Kontextes. */
    private Expanse expanse;

    /** Die in diesem Kontext geltende Kommunikationsform. */
    private CommunicationRegion communicationRegion;

    /** Die in diesem Kontext benutzbaren Kommunikationsmedien. */
    private Set<CommunicationMedium> communicationMedia;

    /** Karte dieses Kontextes. */
    private ContextMap map;

    /** Die Information, ob mit diesem Kontext interagiert werden darf. */
    private final boolean interactable;

    /**
     * Erzeugt eine neue Instanz eines räumlichen Kontextes.
     * @param contextName Name des Kontextes.
     * @param parent Übergeordneter Kontext.
     * @param communicationRegion Kommunikationsform des Kontextes.
     * @param communicationMedia Benutzbare Kommunikationsmedien des Kontextes.
     * @param expanse Räumliche Ausdehnung des Kontextes.
     */
    public SpatialContext(@NotNull final String contextName, @NotNull final Context parent,
                          @NotNull final CommunicationRegion communicationRegion,
                          @NotNull final Set<CommunicationMedium> communicationMedia,
                          @NotNull final Expanse expanse, final boolean interactable) {
        super(contextName, parent);
        this.communicationRegion = communicationRegion;
        this.communicationMedia = communicationMedia;
        this.expanse = expanse;
        this.map = null;
        this.interactable = interactable;
        parent.addChild(this);
    }

    /**
     * Erzeugt eine neue Instanz eines räumlichen Kontextes.
     * @param contextName Name des Kontextes.
     * @param parent Übergeordneter Kontext.
     */
    public SpatialContext(@NotNull final String contextName, @NotNull final Context parent) {
        super(contextName, parent);
        this.communicationRegion = null;
        this.communicationMedia = null;
        this.expanse = null;
        this.map = null;
        this.interactable = false;
        parent.addChild(this);
    }

    public void build(ContextMap map) {
        FutureTask<?> buildTask = new FutureTask<>(() -> {
            // Raum wurde bereits initialisiert.
            if (this.map != null) {
                return;
            }
            this.map = map;
            TiledMap tiledMap = new TmxMapLoader().load(map.getPath());
            this.communicationRegion = MapUtils.getCommunicationRegion(tiledMap.getProperties());
            this.communicationMedia = MapUtils.getCommunicationMedia(tiledMap.getProperties());
            this.expanse = new Expanse(new Location(0, 0), MapUtils.getWidth(tiledMap), MapUtils.getHeight(tiledMap));
            MapUtils.buildChildTree(this, tiledMap);
            UserManager.getInstance().getModelObserver().setRoomChanged();
        }, null);
        Gdx.app.postRunnable(buildTask);
        try {
            buildTask.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gibt die räumliche Ausdehnung dieses Kontextes zurück.
     * @return Räumliche Ausdehnung dieses Kontextes.
     */
    public @NotNull Expanse getExpanse() {
        return expanse;
    }

    /**
     * Gibt die Information zurück, ob mit diesem Kontext interagiert werden kann.
     * @return true, wenn mit dem Kontext interagiert werden kann, sonst false.
     */
    public boolean isInteractable() {
        return interactable;
    }

    /**
     * Gibt alle untergeordneten Kontexte dieses Kontextes zurück.
     * @return Alle untergeordneten Kontexte.
     */
    public @NotNull Map<ContextID, SpatialContext> getDescendants() {
        Map<ContextID, SpatialContext> descentants = new HashMap<>(children);
        children.values().forEach(child -> descentants.putAll(child.getDescendants()));
        return descentants;
    }

    @Override
    public @NotNull SpatialContext getArea(float posX, float posY) {
        try {
            return children.values().stream().filter(child -> child.getExpanse().isIn(posX, posY))
                    .findFirst().orElseThrow().getArea(posX, posY);
        } catch (NoSuchElementException e) {
            return this;
        }
    }

    @Override
    public @NotNull CommunicationRegion getCommunicationRegion() {
        return communicationRegion;
    }

    @Override
    public @NotNull Set<CommunicationMedium> getCommunicationMedia() {
        return communicationMedia;
    }

    @Override
    public @Nullable ContextMap getMap() {
        return map;
    }

    @Override
    public @NotNull ILocationView getCenter() {
        return expanse.getCenter();
    }
}