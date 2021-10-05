package view2;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.graphics.Pixmap;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import view2.audio.AudioManager;
import view2.userInterface.hud.HeadUpDisplay;
import view2.userInterface.menu.ConnectionTable;
import view2.userInterface.menu.LoginTable;
import view2.userInterface.menu.MenuScreen;
import view2.userInterface.menu.StartTable;
import view2.world.WorldScreen;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * Eine Klasse, welche die für die gesamte View relevanten Komponenten beinhaltet und deren Zusammenspiel koordiniert.
 */
public class Chati extends Game implements ViewControllerInterface, IModelObserver, ChatiLocalization.Translatable {

    public static final int MINIMUM_HEIGHT = 720;
    public static final int MINIMUM_WIDTH = 1280;
    public static Chati CHATI;

    private final IUserManagerView userManager;

    private ServerSender serverSender;
    private ChatiAssetManager assetManager;
    private ChatiEmojiManager emojiManager;
    private ChatiPreferences preferences;
    private ChatiLocalization localization;
    private AudioManager audioManager;
    private SpriteBatch spriteBatch;
    private HeadUpDisplay headUpDisplay;
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
    public Chati(@NotNull final IUserManagerView userManager) {
        CHATI = this;
        this.userManager = userManager;
    }

    /**
     * Startet die View.
     */
    public void start() {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setWindowIcon("logos/logo_big.bmp", "logos/logo_medium.bmp", "logos/logo_small.bmp", "logos/logo_tiny.bmp");
        config.setPreferencesConfig("Documents/Chati", FileType.External);
        config.setWindowSizeLimits(MINIMUM_WIDTH, MINIMUM_HEIGHT, -1, -1);
        config.setForegroundFPS(60);
        config.setTitle("Chati");
        new Lwjgl3Application(this, config);
    }

    @Override
    public void create() {
        this.assetManager = new ChatiAssetManager();
        this.preferences = new ChatiPreferences();
        this.localization = new ChatiLocalization();
        this.emojiManager = new ChatiEmojiManager();
        this.audioManager = new AudioManager();
        this.spriteBatch = new SpriteBatch();
        this.headUpDisplay = new HeadUpDisplay();
        this.menuScreen = new MenuScreen();
        this.worldScreen = new WorldScreen();
        if (serverSender == null) {
            menuScreen.setMenuTable(new ConnectionTable("table.connection.connect"));
        }
        setScreen(menuScreen);
        updateDisplay();
    }

    @Override
    public void render() {
        /* Übertrage alle Flags auf eine andere Menge von Flags, welche im nächsten Render-Aufruf abgefragt wird.
           So werden keine eingehenden Informationen verpasst, wenn diese am Ende eines Render-Durchlaufs eintreffen. */
        transferFlags();
        resetModelChangeReceivedFlags();
        audioManager.update();
        super.render();
        resetModelChangedFlags();
    }

    @Override
    public void resize(final int width, final int height) {
        menuScreen.resize(width, height);
        worldScreen.resize(width, height);
        Gdx.graphics.setCursor(ChatiCursor.ARROW.getCursor());
    }

    @Override
    public void dispose() {
        assetManager.dispose();
        audioManager.dispose();
        spriteBatch.dispose();
        menuScreen.dispose();
        worldScreen.dispose();
        super.dispose();
    }

    /**
     * Setzt den momentan anzuzeigenden Bildschirm.
     * @param screen Anzuzeigender Bildschirm.
     */
    public void setScreen(@NotNull final ChatiScreen screen) {
        Gdx.input.setInputProcessor(screen.getInputProcessor());
        super.setScreen(screen);
    }

    @Override
    public @NotNull ChatiScreen getScreen() {
        if (screen.equals(menuScreen)) {
            return menuScreen;
        }
        if (screen.equals(worldScreen)) {
            return worldScreen;
        }
        throw new IllegalStateException("Screen not available");
    }

    /**
     * Gibt den Menübildschirm zurück.
     * @return Menübildschirm
     */
    public @NotNull MenuScreen getMenuScreen() {
        return menuScreen;
    }

    /**
     * Gibt den Weltbildschirm zurück.
     * @return Weltbildschirm
     */
    public @NotNull WorldScreen getWorldScreen() {
        return worldScreen;
    }

    /**
     * Gibt den HeadUpDisplay zurück.
     * @return HeadUpDisplay
     */
    public @NotNull HeadUpDisplay getHeadUpDisplay() {
        return headUpDisplay;
    }

    /**
     * Gibt den UserManager zurück.
     * @return UserManager
     */
    public @NotNull IUserManagerView getUserManager() {
        return userManager;
    }

    /**
     * Gibt den intern angemeldeten Benutzer zurück.
     * @return Interner Benutzer.
     */
    public @Nullable IInternUserView getInternUser() {
        return userManager.getInternUserView();
    }

    /**
     * Veranlasst das Senden eines Pakets an den Server.
     * @param action Information darüber, welches Paket an den Server gesendet werden soll.
     * @param objects Zu sendende Informationen.
     */
    public void send(@NotNull final ServerSender.SendAction action, @NotNull final Object... objects) {
        serverSender.send(action, objects);
    }

    /**
     * Gibt den Skin der Anwendung zurück.
     * @return Verwendeter Skin.
     */
    public @NotNull Skin getSkin() {
        return assetManager.getSkin();
    }

    /**
     * Gibt den TextureAtlas zurück.
     * @return TextureAtlas
     */
    public @NotNull TextureAtlas getAtlas() {
        return assetManager.getAtlas();
    }

    /**
     * Gibt eine Textur zurück.
     * @param name Name der Textur.
     * @return Angeforderte Textur.
     */
    public @NotNull TextureRegionDrawable getDrawable(@NotNull final String name) {
        return assetManager.getDrawable(name);
    }

    /**
     * Gibt eine Menge von zusammengehöriger Texturen zurück.
     * @param name Name der Menge der Texturen.
     * @return Angeforderte Menge an Texturen.
     */
    public @NotNull Array<TextureAtlas.AtlasRegion> getRegions(@NotNull final String name) {
        return assetManager.getRegions(name);
    }

    /**
     * Gibt eine Pixmap zurück.
     * @param name Name der Pixmap.
     * @return Angeforderte Pixmap.
     */
    public @NotNull Pixmap getPixmap(@NotNull final String name) {
        return assetManager.getPixmap(name);
    }

    /**
     * Gibt einen Sound zurück.
     * @param name Name des Sounds.
     * @return Angeforderter Sound.
     */
    public @NotNull Sound getSound(@NotNull final String name) {
        return assetManager.getSound(name);
    }

    /**
     * Gibt die Instanz des EmojiSupport zurück.
     * @return EmojiSupport
     */
    public @NotNull ChatiEmojiManager getEmojiManager() {
        return emojiManager;
    }

    /**
     * Gibt die Instanz der Präferenzen zurück.
     * @return Präferenzen
     */
    public @NotNull ChatiPreferences getPreferences() {
        return preferences;
    }

    /**
     * Gibt die Instanz der Lokalisierung zurück.
     * @return Lokalisierung
     */
    public @NotNull ChatiLocalization getLocalization() {
        return localization;
    }

    /**
     * Gibt die Instanz des AudioManager zurück.
     * @return AudioManager
     */
    public @NotNull AudioManager getAudioManager() {
        return audioManager;
    }

    /**
     * Gibt die Instanz des SpriteBatch zurück.
     * @return SpriteBatch
     */
    public @NotNull SpriteBatch getSpriteBatch() {
        return spriteBatch;
    }

    @Override
    public void setSender(@Nullable final ServerSender sender) {
        if (Gdx.app != null) {
            Gdx.app.postRunnable(() -> {
                if (this.menuScreen != null) {
                    this.menuScreen.setMenuTable(sender != null ? new LoginTable() :
                            new ConnectionTable("table.connection.reconnect"));
                }
            });
        }
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
    public void setWorldListChanged() {
        this.worldListUpdateReceived = true;
    }

    @Override
    public void setRoomListChanged() {
        this.roomListUpdateReceived = true;
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
    public void registrationResponse(final boolean success, @Nullable final MessageBundle messageBundle) {
        Gdx.app.postRunnable(() -> {
            if (this.screen.equals(menuScreen)) {
                menuScreen.registrationResponse(success, messageBundle);
            }
        });
    }

    @Override
    public void loginResponse(final boolean success, @Nullable final MessageBundle messageBundle) {
        loggedIn = success;
        Gdx.app.postRunnable(() -> {
            if (this.screen.equals(menuScreen)) {
                menuScreen.loginResponse(success, messageBundle);
            }
        });
    }

    @Override
    public void passwordChangeResponse(final boolean success, @Nullable final MessageBundle messageBundle) {
        Gdx.app.postRunnable(() -> {
            if (this.screen.equals(menuScreen)) {
                menuScreen.passwordChangeResponse(success, messageBundle);
            }
        });
    }

    @Override
    public void deleteAccountResponse(final boolean success, @Nullable final MessageBundle messageBundle) {
        Gdx.app.postRunnable(() -> {
            if (this.screen.equals(menuScreen)) {
                menuScreen.deleteAccountResponse(success, messageBundle);
            }
        });
    }

    @Override
    public void avatarChangeResponse(final boolean success, @Nullable final MessageBundle messageBundle) {
        Gdx.app.postRunnable(() -> {
            if (this.screen.equals(menuScreen)) {
                menuScreen.avatarChangeResponse(success, messageBundle);
            }
        });
    }

    @Override
    public void createWorldResponse(final boolean success, @Nullable final MessageBundle messageBundle) {
        Gdx.app.postRunnable(() -> {
            if (this.screen.equals(menuScreen)) {
                menuScreen.createWorldResponse(success, messageBundle);
            }
        });
    }

    @Override
    public void deleteWorldResponse(final boolean success, @Nullable final MessageBundle messageBundle) {
        Gdx.app.postRunnable(() -> {
            if (this.screen.equals(menuScreen)) {
                menuScreen.deleteWorldResponse(success, messageBundle);
            }
        });
    }

    @Override
    public void joinWorldResponse(final boolean success, @Nullable final MessageBundle messageBundle) {
        Gdx.app.postRunnable(() -> {
            if (this.screen.equals(menuScreen)) {
                menuScreen.joinWorldResponse(success, messageBundle);
            }
        });
    }

    @Override
    public void showTypingUser(@NotNull final UUID userId) {
        Gdx.app.postRunnable(() -> {
            if (this.screen.equals(worldScreen)) {
                headUpDisplay.showTypingUser(userId);
            }
        });
    }

    @Override
    public void showChatMessage(@NotNull final UUID userId, @NotNull final LocalDateTime timestamp,
                                @NotNull final MessageType messageType, @NotNull final String message,
                                byte[] imageData, @Nullable String imageName) {
        Gdx.app.postRunnable(() -> {
            if (this.screen.equals(worldScreen)) {
                try {
                    headUpDisplay.showChatMessage(userId, timestamp, messageType, message, imageData, imageName);
                } catch (UserNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void showInfoMessage(@NotNull final LocalDateTime timestamp, @NotNull final MessageBundle messageBundle) {
        Gdx.app.postRunnable(() -> {
            if (this.screen.equals(worldScreen)) {
                headUpDisplay.showInfoMessage(timestamp, messageBundle);
            }
        });
    }

    @Override
    public void playVoiceData(@NotNull final UUID userId, @NotNull final LocalDateTime timestamp, final byte[] voiceData)
            throws UserNotFoundException {
        if (this.screen.equals(worldScreen)) {
            audioManager.playVoiceData(userId, timestamp, voiceData);
        }
    }

    @Override
    public void playMusicData(@NotNull final LocalDateTime timestamp, final byte[] musicData,
                              final float position, final int seconds) {
        if (this.screen.equals(worldScreen)) {
            audioManager.playMusicData(timestamp, musicData, position, seconds);
        }
    }

    @Override
    public void openMenu(@NotNull final ContextID contextId, @NotNull final ContextMenu contextMenu) {
        Gdx.app.postRunnable(() -> {
            if (this.screen.equals(worldScreen)) {
                worldScreen.openMenu(contextId, contextMenu);
            }
        });
    }

    @Override
    public void closeMenu(@NotNull final ContextID contextId, @NotNull final ContextMenu contextMenu) {
        Gdx.app.postRunnable(() -> {
            if (this.screen.equals(worldScreen)) {
                worldScreen.closeMenu(contextId, contextMenu);
            }
        });
    }

    @Override
    public void menuActionResponse(final boolean success, @Nullable final MessageBundle messageBundle) {
        Gdx.app.postRunnable(() -> {
            if (this.screen.equals(worldScreen)) {
                worldScreen.menuActionResponse(success, messageBundle);
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

    @Override
    public void translate() {
        localization.load();
        menuScreen.translate();
        worldScreen.translate();
        headUpDisplay.translate();
    }

    /**
     * Aktualisiert den Displaymodus der Anwendung.
     * Schaltet zwischen Vollbildmodus und Fenstermodus, abhängig von der Einstellung in den Präferenzen, um.
     */
    public void updateDisplay() {
        if (Gdx.graphics.supportsDisplayModeChange()) {
            if (preferences.isFullscreen() && !Gdx.graphics.isFullscreen()) {
                Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
            } else if (Gdx.graphics.isFullscreen()) {
                Gdx.graphics.setWindowedMode(MINIMUM_WIDTH, MINIMUM_HEIGHT);
            }
        } else {
            Logger.getLogger("chati-view").info("Changing the displaymode is not supported");
        }
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