package view2;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import controller.network.ServerSender;
import model.MessageBundle;
import model.communication.message.MessageType;
import model.context.ContextID;
import model.context.spatial.ContextMenu;
import model.exception.UserNotFoundException;
import model.user.IInternUserView;
import model.user.IUserManagerView;
import org.jetbrains.annotations.Nullable;
import view2.audio.AudioManager;
import view2.userInterface.hud.HeadUpDisplay;
import view2.userInterface.menu.MenuScreen;
import view2.userInterface.menu.table.LoginTable;
import view2.userInterface.menu.table.StartTable;
import view2.world.WorldScreen;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

/**
 * Eine Klasse, welche die für die gesamte View relevanten Komponenten beinhaltet und deren Zusammenspiel koordiniert.
 */
public class Chati extends Game implements ViewControllerInterface, IModelObserver {

    public static Chati CHATI;

    private final Set<ContextEntry> worlds;
    private final Set<ContextEntry> privateRooms;
    private final IUserManagerView userManager;

    private ServerSender serverSender;
    private ChatiAssetManager assetManager;
    private ChatiPreferences preferences;
    private AudioManager audioManager;
    private SpriteBatch spriteBatch;
    private MenuScreen menuScreen;
    private WorldScreen worldScreen;

    private boolean loggedIn;

    private boolean userInfoChangeReceived;
    private boolean newNotificationInfoReceived;
    private boolean userNotificationChangeReceived;
    private boolean roomChangeReceived;
    private boolean worldChangeReceived;
    private boolean musicChangeReceived;
    private boolean worldListUpdateReceived;
    private boolean roomListUpdateReceived;

    private boolean changeUserInfo;
    private boolean receivedNewNotification;
    private boolean changeNotificationInfo;
    private boolean changeRoom;
    private boolean changeWorld;
    private boolean changeMusic;
    private boolean changeWorldList;
    private boolean changeRoomList;

    /**
     * Erzeugt eine neue Instanz der View.
     * @param userManager Interface, über welches auf das Model zur Abfrage benötigter Informationen zugegriffen werden
     * kann.
     */
    public Chati(IUserManagerView userManager) {
        CHATI = this;
        this.worlds = new TreeSet<>();
        this.privateRooms = new TreeSet<>();
        this.userManager = userManager;
    }

    /**
     * Startet die View.
     */
    public void start() {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setFullscreenMode(Lwjgl3ApplicationConfiguration.getDisplayMode());
        config.setWindowIcon("logos/logo_big.bmp", "logos/logo_medium.bmp", "logos/logo_small.bmp", "logos/logo_tiny.bmp");
        config.setForegroundFPS(60);
        config.setTitle("Chati");
        new Lwjgl3Application(this, config);
    }

    @Override
    public void create() {
        this.assetManager = new ChatiAssetManager();
        this.preferences = new ChatiPreferences();
        this.audioManager = new AudioManager();
        this.spriteBatch = new SpriteBatch();
        this.menuScreen = new MenuScreen();
        this.worldScreen = new WorldScreen();
        setScreen(menuScreen);
    }

    @Override
    public void render() {
        /* Übertrage alle Flags auf eine andere Menge von Flags, welche im nächsten Render-Aufruf abgefragt wird.
           So werden keine eingehenden Informationen verpasst, wenn diese am Ende eines Render-Durchlaufs eintreffen. */
        transferFlags();
        resetModelChangeReceivedFlags();
        super.render();
        resetModelChangedFlags();
    }

    /**
     * Setzt den momentan anzuzeigenden Bildschirm.
     * @param screen Anzuzeigender Bildschirm.
     */
    public void setScreen(ChatiScreen screen) {
        Gdx.input.setInputProcessor(screen.getInputProcessor());
        super.setScreen(screen);
    }

    @Override
    public ChatiScreen getScreen() {
        if (screen.equals(menuScreen)) {
            return menuScreen;
        }
        if (screen.equals(worldScreen)) {
            return worldScreen;
        }
        return null;
    }

    /**
     * Gibt den Menübildschirm zurück.
     * @return Menübildschirm
     */
    public MenuScreen getMenuScreen() {
        return menuScreen;
    }

    /**
     * Gibt den Weltbildschirm zurück.
     * @return Weltbildschirm
     */
    public WorldScreen getWorldScreen() {
        return worldScreen;
    }

    /**
     * Gibt den UserManager zurück.
     * @return UserManager
     */
    public IUserManagerView getUserManager() {
        return userManager;
    }

    /**
     * Gibt den intern angemeldeten Benutzer zurück.
     * @return Interner Benutzer.
     */
    public IInternUserView getInternUser() {
        return userManager.getInternUserView();
    }

    /**
     * Veranlasst das Senden eines Pakets an den Server.
     * @param action Information darüber, welches Paket an den Server gesendet werden soll.
     * @param objects Zu sendende Informationen.
     */
    public void send(ServerSender.SendAction action, Object... objects) {
        serverSender.send(action, objects);
    }

    /**
     * Gibt den Skin der Anwendung zurück.
     * @return Verwendeter Skin.
     */
    public Skin getSkin() {
        return assetManager.getSkin();
    }

    /**
     * Gibt eine Textur zurück.
     * @param name Name der Textur.
     * @return Angeforderte Textur.
     */
    public TextureRegionDrawable getDrawable(String name) {
        return assetManager.getDrawable(name);
    }

    /**
     * Gibt eine Menge von zusammengehöriger Texturen zurück.
     * @param name Name der Menge der Texturen.
     * @return Angeforderte Menge an Texturen.
     */
    public Array<TextureAtlas.AtlasRegion> getRegions(String name) {
        return assetManager.getRegions(name);
    }

    /**
     * Gibt die Instanz der Präferenzen zurück.
     * @return Präferenzen
     */
    public ChatiPreferences getPreferences() {
        return preferences;
    }

    /**
     * Gibt die Instanz des AudioManager zurück.
     * @return AudioManager
     */
    public AudioManager getAudioManager() {
        return audioManager;
    }

    /**
     * Gibt die Instanz des SpriteBatch zurück.
     * @return SpriteBatch
     */
    public SpriteBatch getSpriteBatch() {
        return spriteBatch;
    }

    @Override
    public void setSender(@Nullable ServerSender sender) {
        this.serverSender = sender;
    }

    @Override
    public void setUserInfoChanged() {
        this.userInfoChangeReceived = true;
    }

    @Override
    public void setNewNotificationReceived() {
        this.newNotificationInfoReceived = true;
    }

    @Override
    public void setUserNotificationChanged() {
        this.userNotificationChangeReceived = true;
    }

    @Override
    public void setWorldChanged() {
        this.worldChangeReceived = true;
    }

    @Override
    public void setRoomChanged() {
        this.roomChangeReceived = true;
    }

    @Override
    public void setMusicChanged() {
        this.musicChangeReceived = true;
    }

    @Override
    public void updateWorlds(Map<ContextID, String> worlds) {
        this.worlds.clear();
        worlds.forEach((key, value) -> this.worlds.add(new ContextEntry(key, value)));
        worldListUpdateReceived = true;
    }

    @Override
    public void updateRooms(ContextID worldId, Map<ContextID, String> privateRooms) {
        this.privateRooms.clear();
        privateRooms.forEach((key, value) -> this.privateRooms.add(new ContextEntry(key, value)));
        roomListUpdateReceived = true;
    }

    @Override
    public void registrationResponse(boolean success, String messageKey) {
        Gdx.app.postRunnable(() -> {
            if (this.screen.equals(menuScreen)) {
                menuScreen.registrationResponse(success, messageKey);
            }
        });
    }

    @Override
    public void loginResponse(boolean success, String messageKey) {
        loggedIn = success;
        Gdx.app.postRunnable(() -> {
            if (this.screen.equals(menuScreen)) {
                menuScreen.loginResponse(success, messageKey);
            }
        });
    }

    @Override
    public void passwordChangeResponse(boolean success, String messageKey) {
        Gdx.app.postRunnable(() -> {
            if (this.screen.equals(menuScreen)) {
                menuScreen.passwordChangeResponse(success, messageKey);
            }
        });
    }

    @Override
    public void deleteAccountResponse(boolean success, String messageKey) {
        Gdx.app.postRunnable(() -> {
            if (this.screen.equals(menuScreen)) {
                menuScreen.deleteAccountResponse(success, messageKey);
            }
        });
    }

    @Override
    public void avatarChangeResponse(boolean success, String messageKey) {
        Gdx.app.postRunnable(() -> {
            if (this.screen.equals(menuScreen)) {
                menuScreen.avatarChangeResponse(success, messageKey);
            }
        });
    }

    @Override
    public void createWorldResponse(boolean success, String messageKey) {
        Gdx.app.postRunnable(() -> {
            if (this.screen.equals(menuScreen)) {
                menuScreen.createWorldResponse(success, messageKey);
            }
        });
    }

    @Override
    public void deleteWorldResponse(boolean success, String messageKey) {
        Gdx.app.postRunnable(() -> {
            if (this.screen.equals(menuScreen)) {
                menuScreen.deleteWorldResponse(success, messageKey);
            }
        });
    }

    @Override
    public void joinWorldResponse(boolean success, String messageKey) {
        Gdx.app.postRunnable(() -> {
            if (this.screen.equals(menuScreen)) {
                menuScreen.joinWorldResponse(success, messageKey);
            }
        });
    }

    @Override
    public void showTypingUser(UUID userId) {
        Gdx.app.postRunnable(() -> {
            if (this.screen.equals(worldScreen)) {
                HeadUpDisplay.getInstance().showTypingUser(userId);
            }
        });
    }

    @Override
    public void showChatMessage(UUID userId, LocalDateTime timestamp, MessageType messageType, String message,
            MessageBundle messageBundle) {
        Gdx.app.postRunnable(() -> {
            if (this.screen.equals(worldScreen)) {
                try {
                    HeadUpDisplay.getInstance()
                            .showChatMessage(userId, timestamp, messageType, message, messageBundle);
                } catch (UserNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void playAudioData(UUID userId, LocalDateTime timestamp, byte[] audioData) throws UserNotFoundException {
        if (this.screen.equals(worldScreen)) {
            audioManager.playAudioData(userId, timestamp, audioData);
        }
    }

    @Override
    public void openMenu(ContextID contextId, ContextMenu contextMenu) {
        Gdx.app.postRunnable(() -> {
            if (this.screen.equals(worldScreen)) {
                worldScreen.openMenu(contextId, contextMenu);
            }
        });
    }

    @Override
    public void closeMenu(ContextID contextId, ContextMenu contextMenu) {
        Gdx.app.postRunnable(() -> {
            if (this.screen.equals(worldScreen)) {
                worldScreen.closeMenu(contextId, contextMenu);
            }
        });
    }

    @Override
    public void menuActionResponse(boolean success, String messageKey) {
        Gdx.app.postRunnable(() -> {
            if (this.screen.equals(worldScreen)) {
                worldScreen.menuActionResponse(success, messageKey);
            }
        });
    }

    @Override
    public void logout() {
        if (loggedIn) {
            Gdx.app.postRunnable(() -> {
                menuScreen.setMenuTable(new LoginTable());
                if (!screen.equals(menuScreen)) {
                    setScreen(menuScreen);
                }
            });
        }
    }

    @Override
    public void leaveWorld() {
        Gdx.app.postRunnable(() -> {
            if (this.screen.equals(worldScreen)) {
                menuScreen.setMenuTable(new StartTable());
                setScreen(menuScreen);
            }
        });
    }

    /**
     * Gibt die Information zurück, ob sich Benutzerinformationen seit dem letzten Rendern geändert haben.
     * @return true, wenn sich Benutzerinformationen geändert haben, sonst false.
     */
    public boolean isUserInfoChanged() {
        return changeUserInfo;
    }

    /**
     * Gibt die Information zurück, ob eine neue Benachrichtigung seit dem letzten Rendern erhalten wurde.
     * @return true, wenn eine neue Benachrichtigung erhalten wurde, sonst false.
     */
    public boolean isNewNotificationReceived() {
        return receivedNewNotification;
    }

    /**
     * Gibt die Information zurück, ob sich vorhandene Benachrichtigungen seit dem letzten Rendern geändert haben.
     * @return true, wenn sich Benachrichtigungen geändert haben, sonst false.
     */
    public boolean isUserNotificationChanged() {
        return changeNotificationInfo;
    }

    /**
     * Gibt die Information zurück, ob sich die Welt seit dem letzten Rendern geändert hat.
     * @return true, wenn sich die Welt geändert hat, sonst false.
     */
    public boolean isWorldChanged() {
        return changeWorld;
    }

    /**
     * Gibt die Information zurück, ob sich der Raum seit dem letzten Rendern geändert hat.
     * @return true, wenn sich der Raum geändert hat, sonst false.
     */
    public boolean isRoomChanged() {
        return changeRoom;
    }

    /**
     * Gibt die Information zurück, ob sich die abgespielte Musik seit dem letzten Rendern geändert hat.
     * @return true, wenn sich die Musik geändert hat, sonst false.
     */
    public boolean isMusicChanged() {
        return changeMusic;
    }

    /**
     * Gibt die Information zurück, ob sich die Liste aller verfügbaren Welten seit dem letzten Rendern geändert hat.
     * @return true, wenn sich die Liste der Welten geändert hat, sonst false.
     */
    public boolean isWorldListChanged() {
        return changeWorldList;
    }

    /**
     * Gibt die Information zurück, ob sich die Liste aller verfügbaren Räume seit dem letzten Rendern geändert hat.
     * @return true, wenn sich die Liste der Räume geändert hat, sonst false.
     */
    public boolean isRoomListChanged() {
        return changeRoomList;
    }

    /**
     * Gibt die Menge aller verfügbaren Welten zurück.
     * @return Menge aller verfügbaren Welten.
     */
    public Set<ContextEntry> getWorlds() {
        return Set.copyOf(worlds);
    }

    /**
     * Gibt die Menge aller verfügbaren privaten Räume zurück.
     * @return Menge aller verfügbaren privaten Räume.
     */
    public Set<ContextEntry> getPrivateRooms() {
        return Set.copyOf(privateRooms);
    }

    /**
     * Transferiert die Informationen der Flags.
     */
    private void transferFlags() {
        changeUserInfo = userInfoChangeReceived;
        receivedNewNotification = newNotificationInfoReceived;
        changeNotificationInfo = userNotificationChangeReceived;
        changeRoom = roomChangeReceived;
        changeWorld = worldChangeReceived;
        changeMusic = musicChangeReceived;
        changeWorldList = worldListUpdateReceived;
        changeRoomList = roomListUpdateReceived;
    }

    /**
     * Setzt die Flags über neu erhaltene Änderungen zurück.
     */
    private void resetModelChangeReceivedFlags() {
        userInfoChangeReceived = false;
        newNotificationInfoReceived = false;
        userNotificationChangeReceived = false;
        roomChangeReceived = false;
        worldChangeReceived = false;
        musicChangeReceived = false;
        worldListUpdateReceived = false;
        roomListUpdateReceived = false;
    }

    /**
     * Setzt die Flags zurück, ob die Änderungen abgefragt werden können.
     */
    private void resetModelChangedFlags() {
        changeUserInfo = false;
        receivedNewNotification = false;
        changeNotificationInfo = false;
        changeRoom = false;
        changeWorld = false;
        changeMusic = false;
        changeWorldList = false;
        changeRoomList = false;
    }
}