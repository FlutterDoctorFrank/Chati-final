package view2;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
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
import view2.component.ChatiScreen;
import view2.component.hud.HeadUpDisplay;
import view2.component.menu.ContextEntry;
import view2.component.menu.table.LoginTable;
import view2.component.menu.MenuScreen;
import view2.component.menu.table.StartTable;
import view2.component.world.WorldScreen;

import java.time.LocalDateTime;
import java.util.*;

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
    private boolean userNotificationChangeReceived;
    private boolean newNotificationInfoReceived;
    private boolean roomChangeReceived;
    private boolean worldChangeReceived;
    private boolean musicChangeReceived;
    private boolean worldListUpdateReceived;
    private boolean roomListUpdateReceived;

    private boolean changeUserInfo;
    private boolean changeNotificationInfo;
    private boolean receivedNewNotification;
    private boolean changeRoom;
    private boolean changeWorld;
    private boolean changeMusic;
    private boolean changeWorldList;
    private boolean changeRoomList;

    public Chati(IUserManagerView userManager) {
        CHATI = this;
        this.worlds = new HashSet<>();
        this.privateRooms = new HashSet<>();
        this.userManager = userManager;
    }

    public void start() {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setFullscreenMode(Lwjgl3ApplicationConfiguration.getDisplayMode());
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
           So werden keine eingehenden Informationen verpasst, wenn diese am Ende eines Render-Aufrufs eintreffen. */
        transferFlags();
        resetModelChangeReceivedFlags();
        super.render();
        resetModelChangedFlags();
    }

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

    public ServerSender getServerSender() {
        return serverSender;
    }

    public SpriteBatch getSpriteBatch() {
        return spriteBatch;
    }

    public Skin getSkin() {
        return assetManager.getSkin();
    }

    public TextureRegion getTextureRegion(String name) {
        return assetManager.getTextureRegion(name);
    }

    public TextureRegionDrawable getDrawable(String name) {
        return new TextureRegionDrawable(getTextureRegion(name));
    }

    public ChatiPreferences getPreferences() {
        return preferences;
    }

    public AudioManager getAudioManager() {
        return audioManager;
    }

    public IUserManagerView getUserManager() {
        return userManager;
    }

    public IInternUserView getInternUser() {
        return userManager.getInternUserView();
    }

    public void send(ServerSender.SendAction action, Object... objects) {
        serverSender.send(action, objects);
    }

    @Override
    public void setUserInfoChanged() {
        this.userInfoChangeReceived = true;
    }

    @Override
    public void setUserNotificationChanged() {
        this.userNotificationChangeReceived = true;
    }

    @Override
    public void setNewNotificationReceived() {

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
    public void setSender(@Nullable ServerSender sender) {
        this.serverSender = sender;
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
        if (this.screen.equals(menuScreen)) {
            Gdx.app.postRunnable(() -> menuScreen.registrationResponse(success, messageKey));
        }
    }

    @Override
    public void loginResponse(boolean success, String messageKey) {
        loggedIn = success;
        if (this.screen.equals(menuScreen)) {
            Gdx.app.postRunnable(() -> menuScreen.loginResponse(success, messageKey));
        }
    }

    @Override
    public void passwordChangeResponse(boolean success, String messageKey) {
        if (this.screen.equals(menuScreen)) {
            Gdx.app.postRunnable(() -> menuScreen.passwordChangeResponse(success, messageKey));
        }
    }

    @Override
    public void deleteAccountResponse(boolean success, String messageKey) {
        if (this.screen.equals(menuScreen)) {
            Gdx.app.postRunnable(() -> menuScreen.deleteAccountResponse(success, messageKey));
        }
    }

    @Override
    public void avatarChangeResponse(boolean success, String messageKey) {
        if (this.screen.equals(menuScreen)) {
            Gdx.app.postRunnable(() -> menuScreen.avatarChangeResponse(success, messageKey));
        }
    }

    @Override
    public void createWorldResponse(boolean success, String messageKey) {
        if (this.screen.equals(menuScreen)) {
            Gdx.app.postRunnable(() -> menuScreen.createWorldResponse(success, messageKey));
        }
    }

    @Override
    public void deleteWorldResponse(boolean success, String messageKey) {
        if (this.screen.equals(menuScreen)) {
            Gdx.app.postRunnable(() -> menuScreen.deleteWorldResponse(success, messageKey));
        }
    }

    @Override
    public void joinWorldResponse(boolean success, String messageKey) {
        if (this.screen.equals(menuScreen)) {
            Gdx.app.postRunnable(() -> menuScreen.joinWorldResponse(success, messageKey));
        }
    }

    @Override
    public void showTypingUser(UUID userId) {
        if (this.screen.equals(worldScreen)) {
            Gdx.app.postRunnable(() -> HeadUpDisplay.getInstance().showTypingUser(userId));
        }
    }

    @Override
    public void showChatMessage(UUID userId, LocalDateTime timestamp, MessageType messageType, String message,
            MessageBundle messageBundle) {
        if (this.screen.equals(worldScreen)) {
            Gdx.app.postRunnable(() -> {
                try {
                    HeadUpDisplay.getInstance()
                            .showChatMessage(userId, timestamp, messageType, message, messageBundle);
                } catch (UserNotFoundException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    @Override
    public void playAudioData(UUID userId, LocalDateTime timestamp, byte[] audioData) throws UserNotFoundException {
        if (this.screen.equals(worldScreen)) {
            audioManager.playAudioData(userId, timestamp, audioData);
        }
    }

    @Override
    public void openMenu(ContextID contextId, ContextMenu contextMenu) {
        if (this.screen.equals(worldScreen)) {
            Gdx.app.postRunnable(() -> worldScreen.openMenu(contextId, contextMenu));
        }
    }

    @Override
    public void closeMenu(ContextID contextId, ContextMenu contextMenu) {
        if (this.screen.equals(worldScreen)) {
            Gdx.app.postRunnable(() -> worldScreen.closeMenu(contextId, contextMenu));
        }
    }

    @Override
    public void menuActionResponse(boolean success, String messageKey) {
        if (this.screen.equals(worldScreen)) {
            Gdx.app.postRunnable(() -> worldScreen.menuActionResponse(success, messageKey));
        }
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
        if (screen.equals(worldScreen)) {
            Gdx.app.postRunnable(() -> {
                menuScreen.setMenuTable(new StartTable());
                setScreen(menuScreen);
            });
        }
    }

    public boolean isUserInfoChanged() {
        return changeUserInfo;
    }

    public boolean isUserNotificationChanged() {
        return changeNotificationInfo;
    }

    public boolean isNewNotificationReceived() {
        return receivedNewNotification;
    }

    public boolean isWorldChanged() {
        return changeWorld;
    }

    public boolean isRoomChanged() {
        return changeRoom;
    }

    public boolean isMusicChanged() {
        return changeMusic;
    }

    public boolean isWorldListChanged() {
        return changeWorldList;
    }

    public boolean isRoomListChanged() {
        return changeRoomList;
    }

    public MenuScreen getMenuScreen() {
        return menuScreen;
    }

    public WorldScreen getWorldScreen() {
        return worldScreen;
    }

    private void resetModelChangeReceivedFlags() {
        userInfoChangeReceived = false;
        userNotificationChangeReceived = false;
        newNotificationInfoReceived = false;
        roomChangeReceived = false;
        worldChangeReceived = false;
        musicChangeReceived = false;
        worldListUpdateReceived = false;
        roomListUpdateReceived = false;
    }

    private void transferFlags() {
        changeUserInfo = userInfoChangeReceived;
        changeNotificationInfo = userNotificationChangeReceived;
        receivedNewNotification = newNotificationInfoReceived;
        changeRoom = roomChangeReceived;
        changeWorld = worldChangeReceived;
        changeMusic = musicChangeReceived;
        changeWorldList = worldListUpdateReceived;
        changeRoomList = roomListUpdateReceived;
    }

    private void resetModelChangedFlags() {
        changeUserInfo = false;
        changeNotificationInfo = false;
        receivedNewNotification = false;
        changeRoom = false;
        changeWorld = false;
        changeMusic = false;
        changeWorldList = false;
        changeRoomList = false;
    }

    public Set<ContextEntry> getWorlds() {
        return Set.copyOf(worlds);
    }

    public Set<ContextEntry> getPrivateRooms() {
        return Set.copyOf(privateRooms);
    }
}