package view2.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import model.MessageBundle;
import model.context.ContextID;
import model.context.spatial.ContextMenu;
import model.user.IInternUserView;
import model.user.IUserView;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import view2.Chati;
import view2.ChatiScreen;
import view2.userInterface.interactableMenu.AreaPlannerWindow;
import view2.userInterface.interactableMenu.GameBoardWindow;
import view2.userInterface.interactableMenu.InteractableWindow;
import view2.userInterface.interactableMenu.MusicStreamerWindow;
import view2.userInterface.interactableMenu.PortalWindow;
import view2.userInterface.interactableMenu.RoomReceptionWindow;
import view2.userInterface.interactableMenu.SeatWindow;
import view2.world.component.CollisionArea;
import view2.world.component.InteractionArea;
import view2.world.component.InternUserAvatar;
import view2.world.component.UserAvatar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Eine Klasse, welche den Bildschirm innerhalb der Welt in der Anwendung repräsentiert.
 */
public class WorldScreen extends ChatiScreen {

    public static final short COLLISION_AREA_BIT = 1;
    public static final short USER_BIT = 2;
    public static final short INTERN_USER_BIT = 4;

    public static final float WORLD_STEP = 30;
    private static final int VELOCITY_ITERATIONS = 6;
    private static final int POSITION_ITERATIONS = 2;
    private static final int[] LAYERS_RENDER_BEFORE_AVATAR = new int[]{0, 1, 2};
    private static final int[] LAYERS_RENDER_AFTER_AVATAR = new int[]{3};

    private final WorldInputProcessor worldInputProcessor;
    private final WorldCamera camera;
    private final FitViewport viewport;
    private final OrthogonalTiledMapRenderer tiledMapRenderer;
    // private final Box2DDebugRenderer debugRenderer;
    private final World world;

    private final Map<IUserView, UserAvatar> externUserAvatars;
    private InternUserAvatar internUserAvatar;
    private InteractableWindow currentInteractableWindow;

    /**
     * Erzeugt eine neue Instanz des WorldScreen.
     */
    public WorldScreen() {
        this.worldInputProcessor = new WorldInputProcessor();
        this.camera = new WorldCamera();
        this.viewport = new FitViewport(WorldCamera.scaleToUnit(Gdx.graphics.getWidth()),
                WorldCamera.scaleToUnit(Gdx.graphics.getHeight()), camera);
        this.tiledMapRenderer = new OrthogonalTiledMapRenderer(null, WorldCamera.scaleToUnit(1));
        // this.debugRenderer = new Box2DDebugRenderer();
        this.world = new World(new Vector2(0, 0), true);
        this.world.setContactListener(new WorldContactListener());
        this.externUserAvatars = new HashMap<>();
        this.internUserAvatar = null;
        this.currentInteractableWindow = null;
    }

    @Override
    public void render(final float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (Chati.CHATI.isRoomChanged()) {
            destroy();
            createMap();
            initialize();
        }
        if (tiledMapRenderer.getMap() != null) {
            if (Chati.CHATI.isUserInfoChanged()) {
                loadExternUserAvatars();
            }

            tiledMapRenderer.render(LAYERS_RENDER_BEFORE_AVATAR);

            Chati.CHATI.getSpriteBatch().setProjectionMatrix(camera.combined);
            Chati.CHATI.getSpriteBatch().begin();
            externUserAvatars.values().forEach(avatar -> avatar.draw(Chati.CHATI.getSpriteBatch(), delta));
            internUserAvatar.draw(Chati.CHATI.getSpriteBatch(), delta);
            Chati.CHATI.getSpriteBatch().end();

            tiledMapRenderer.render(LAYERS_RENDER_AFTER_AVATAR);

            Chati.CHATI.getSpriteBatch().begin();
            externUserAvatars.values().forEach(avatar -> avatar.drawHead(Chati.CHATI.getSpriteBatch(), delta));
            internUserAvatar.drawHead(Chati.CHATI.getSpriteBatch(), delta);
            Chati.CHATI.getSpriteBatch().end();

            //debugRenderer.render(world, camera.combined);

            world.step(1 / WORLD_STEP, VELOCITY_ITERATIONS, POSITION_ITERATIONS);

            internUserAvatar.sendPosition();
            camera.update();
            tiledMapRenderer.setView(camera);
        }
        super.render(delta);
    }

    @Override
    public void show() {
        super.show();
        initialize();
    }

    @Override
    public void hide() {
        destroy();
        super.hide();
    }

    @Override
    public void dispose() {
        tiledMapRenderer.dispose();
        // debugRenderer.dispose();
        world.dispose();
        super.dispose();
    }

    @Override
    public void resize(final int width, final int height) {
        viewport.update(width, height);
    }

    @Override
    public @NotNull InputProcessor getInputProcessor() {
        return new InputMultiplexer(stage, worldInputProcessor);
    }

    /**
     * Gibt den WorldInputProcessor zurück.
     * @return WorldInputProcessor.
     */
    public @NotNull WorldInputProcessor getWorldInputProcessor() {
        return worldInputProcessor;
    }

    /**
     * Gibt die aktuelle Karte zurück.
     * @return Aktuelle Karte.
     */
    public @Nullable TiledMap getTiledMap() {
        return tiledMapRenderer.getMap();
    }

    /**
     * Gibt die Instanz der World (LibGDX-Klasse) zurück.
     * @return Instanz der World.
     */
    public @NotNull World getWorld() {
        return world;
    }

    /**
     * Gibt die Kamera zurück.
     * @return Kamera.
     */
    public @NotNull WorldCamera getCamera() {
        return camera;
    }

    /**
     * Gibt den Avatar des intern angemeldeten Benutzers zurück.
     * @return Avatar des internen Benutzers.
     */
    public @Nullable InternUserAvatar getInternUserAvatar() {
        return internUserAvatar;
    }

    /**
     * Öffnet das Menü eines Interaktionsobjekts.
     * @param contextId ID des Interaktionsobjekts.
     * @param menu Zu öffnendes Menü.
     */
    public void openMenu(@NotNull final ContextID contextId, @NotNull final ContextMenu menu) {
        switch (menu) {
            case ROOM_RECEPTION_MENU:
                currentInteractableWindow = new RoomReceptionWindow(contextId);
                break;
            case MUSIC_STREAMER_MENU:
                currentInteractableWindow = new MusicStreamerWindow(contextId);
                break;
            case AREA_PLANNER_MENU:
                currentInteractableWindow = new AreaPlannerWindow(contextId);
                break;
            case GAME_BOARD_MENU:
                currentInteractableWindow = new GameBoardWindow(contextId);
                break;
            case SEAT_MENU:
                currentInteractableWindow = new SeatWindow(contextId);
                break;
            case PORTAL_MENU:
                currentInteractableWindow = new PortalWindow(contextId);
                break;
            default:
                throw new IllegalArgumentException("Tried to open an unsupported menu.");
        }
        currentInteractableWindow.open();
    }

    /**
     * Schließt das Menü eines Interaktionsobjekts.
     * @param contextId ID des Interaktionsobjekts.
     * @param menu Zu schließendes Menü.
     */
    public void closeMenu(@NotNull final ContextID contextId, @NotNull final ContextMenu menu) {
        if (currentInteractableWindow == null || !currentInteractableWindow.getInteractableId().equals(contextId)
            || currentInteractableWindow.getInteractableMenu() != menu) {
            throw new IllegalArgumentException("Tried to close a menu that is not open.");
        }
        stage.closeWindow(currentInteractableWindow);
        currentInteractableWindow = null;
    }

    /**
     * Veranlasst die Verarbeitung einer Antwort auf eine durchgeführte Menüaktion im aktuell geöffneten Menü.
     * @param success Information, ob die durchgeführte Aktion erfolgreich war.
     * @param messageBundle die Nachricht mit ihren Argumenten, die angezeigt werden soll.
     */
    public void menuActionResponse(final boolean success, @Nullable final MessageBundle messageBundle) {
        if (currentInteractableWindow == null) {
            return;
        }
        currentInteractableWindow.receiveResponse(success, messageBundle);
    }

    /**
     * Lädt die Avatare aller Benutzer.
     */
    private void initialize() {
        this.internUserAvatar = new InternUserAvatar();
        loadExternUserAvatars();
    }

    /**
     * Entfernt die Avatare aller Benutzer und die Karte.
     */
    private void destroy() {
        Array<Body> bodies = new Array<>();
        world.getBodies(bodies);
        bodies.forEach(world::destroyBody);
        internUserAvatar = null;
        externUserAvatars.clear();
        tiledMapRenderer.setMap(null);
    }

    /**
     * Erzeugt eine neue Karte.
     */
    private void createMap() {
        IInternUserView internUser = Chati.CHATI.getUserManager().getInternUserView();
        if (internUser == null || internUser.getCurrentRoom() == null || internUser.getCurrentRoom().getMap() == null) {
            return;
        }
        TiledMap tiledMap = new TmxMapLoader().load(internUser.getCurrentRoom().getMap().getPath());
        tiledMapRenderer.setMap(tiledMap);
        tiledMapRenderer.getMap().getLayers().get("Collisions").getObjects().getByType(RectangleMapObject.class)
                .forEach(border -> new CollisionArea(border.getRectangle()));
        tiledMapRenderer.getMap().getLayers().get("Interactions").getObjects().getByType(RectangleMapObject.class)
                .forEach(interactiveObject -> new InteractionArea(interactiveObject.getRectangle()));
    }

    /**
     * Lädt die Avatare aller externen Benutzer.
     */
    private void loadExternUserAvatars() {
        Chati.CHATI.getUserManager().getUsersInRoom().values().forEach(externUser -> {
            if (!externUserAvatars.containsKey(externUser)) {
                UserAvatar newUserAvatar = new UserAvatar(externUser);
                externUserAvatars.put(externUser, newUserAvatar);
                newUserAvatar.teleport();
            }
        });
        Iterator<Map.Entry<IUserView, UserAvatar>> iterator = externUserAvatars.entrySet().iterator();
        while (iterator.hasNext()) {
            UserAvatar userAvatar = iterator.next().getValue();
            if (!Chati.CHATI.getUserManager().getUsersInRoom().containsValue(userAvatar.getUser())) {
                world.destroyBody(userAvatar.getBody());
                iterator.remove();
            }
        }
    }

    @Override
    public void translate() {

    }
}
