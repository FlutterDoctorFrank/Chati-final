package model.context.spatial;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import model.communication.CommunicationMedium;
import model.communication.CommunicationRegion;
import model.context.Context;
import model.context.ContextID;
import model.exception.ContextNotFoundException;
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

    /** Musik dieses Kontextes. */
    private ContextMusic music;

    /** Information, ob Musik in diesem Kontext wiederholt abgespielt wird. */
    private boolean looping;

    /** Information, ob nach Abschluss eines Musikstücks in diesem Kontext ein zufälliges nächstes abgespielt wird. */
    private boolean random;

    /** Die Information, ob mit diesem Kontext interagiert werden darf. */
    private final boolean interactable;

    /** Die Information, ob dieser Kontext ein privater Raum ist. */
    private final boolean isPrivate;

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
        this.isPrivate = false;
    }

    /**
     * Erzeugt eine neue Instanz eines räumlichen Kontextes.
     * @param contextName Name des Kontextes.
     * @param parent Übergeordneter Kontext.
     * @param isPrivate true, wenn der Kontext einen privaten Raum repräsentiert.
     */
    public SpatialContext(@NotNull final String contextName, @NotNull final Context parent, final boolean isPrivate) {
        super(contextName, parent);
        this.communicationRegion = null;
        this.communicationMedia = null;
        this.expanse = null;
        this.map = null;
        this.interactable = false;
        this.isPrivate = isPrivate;
    }

    /**
     * Erzeugt eine neue Instanz eines öffentlichen Kontextes.
     * @param contextName Name des Kontextes.
     * @param parent Übergeordneter Kontext.
     */
    public SpatialContext(@NotNull final String contextName, @NotNull final Context parent) {
        this(contextName, parent, false);
    }

    /**
     * Baut die Raumstruktur auf, in dem der LibGDX Umgebung ein Task übergeben wird.
     * @param map Aufzubauende Karte.
     */
    public void build(@NotNull final ContextMap map) {
        if (Gdx.app == null) {
            throw new IllegalStateException("LibGDX environment is not available");
        }

        if (this.map != null) {
            return; // Der Raum wurde bereits mit einer Karte initialisiert.
        }

        FutureTask<?> buildTask = new FutureTask<>(() -> {
            TiledMap tiledMap = new TmxMapLoader().load(map.getPath());

            this.map = map;
            this.expanse = MapUtils.createMapExpanse(tiledMap);
            this.communicationRegion = MapUtils.parseCommunicationRegion(tiledMap.getProperties());
            this.communicationMedia = MapUtils.parseCommunicationMedia(tiledMap.getProperties());
            MapUtils.createContexts(this, tiledMap.getLayers().get("Contexts"));
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

    @Override
    public boolean isPrivate() {
        return isPrivate;
    }

    @Override
    public void removeChildren() {
        this.map = null;
        super.removeChildren();
    }

    /**
     * Gibt alle untergeordneten Kontexte dieses Kontextes zurück.
     * @return Alle untergeordneten Kontexte.
     */
    public @NotNull Map<ContextID, SpatialContext> getDescendants() {
        Map<ContextID, SpatialContext> descendants = new HashMap<>(children);
        children.values().forEach(child -> descendants.putAll(child.getDescendants()));
        return descendants;
    }

    @Override
    public @NotNull SpatialContext getContext(@NotNull final ContextID contextId) throws ContextNotFoundException {
        if (contextId.equals(this.contextId)) {
            return this;
        }
        SpatialContext found = null;
        for (SpatialContext child : children.values()) {
            try {
                found = child.getContext(contextId);
                break;
            } catch (ContextNotFoundException ignored) {
            }
        }
        if (found == null) {
            throw new ContextNotFoundException("Context with this ID not found.", contextId);
        }
        return found;
    }

    @Override
    public @NotNull SpatialContext getArea(float posX, float posY) {
        try {
            return children.values().stream()
                    .filter(child -> child.expanse.isIn(posX, posY))
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
    public @NotNull Location getCenter() {
        return expanse.getCenter();
    }

    /**
     * Setzt die Musik in diesem Kontext.
     * @param music Musik in dem Kontext.
     */
    public void setMusic(@Nullable final ContextMusic music) {
        this.music = music;
    }

    /**
     * Setzt die Information, ob die Musik in diesem Kontext wiederholt abgespielt wird.
     * @param looping Information, ob die Musik wiederholt abgespielt wird.
     */
    public void setLooping(final boolean looping) {
        this.looping = looping;
    }

    /**
     * Setzt die Information, ob nach Abschluss eines Musikstücks in diesem Kontext ein zufälliges nächstes abgespielt
     * wird.
     * @param random Information, ob ein zufälliges nächstes Musikstück abgespielt wird.
     */
    public void setRandom(final boolean random) {
        this.random = random;
    }

    /**
     * Gibt die Musik in diesem Kontext zurück.
     * @return Musik in dem Kontext.
     */
    public @Nullable ContextMusic getMusic() {
        return music;
    }

    /**
     * Gibt die Information zurück, ob Musik in diesem Kontext wiederholt abgespielt wird.
     * @return Information, ob Musik wiederholt abgespielt wird.
     */
    public boolean isLooping() {
        return looping;
    }

    /**
     * Gibt die Information zurück, ob nach Ablauf eines Musikstücks in diesem Kontext ein zufälliges nächstes
     * abgespielt wird.
     * @return Information, ob ein zufälliges nächstes Musikstück abgespielt wird.
     */
    public boolean isRandom() {
        return random;
    }
}