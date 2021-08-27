package view2;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.profiling.GLProfiler;
import controller.network.ServerSender;
import model.MessageBundle;
import model.communication.message.MessageType;
import model.context.ContextID;
import model.context.spatial.Menu;
import model.user.IUserManagerView;
import org.jetbrains.annotations.Nullable;
import view2.component.AbstractScreen;
import view2.component.hud.ChatWindow;
import view2.component.hud.HeadUpDisplay;
import view2.component.menu.table.LoginTable;
import view2.component.menu.MenuScreen;
import view2.component.menu.table.StartTable;
import view2.component.world.WorldScreen;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

public class Chati extends Game implements ViewControllerInterface, IModelObserver {

    public static SpriteBatch SPRITE_BATCH;
    public static Chati CHATI;

    private final IUserManagerView userManager;
    private ServerSender serverSender;
    private MenuScreen menuScreen;
    private WorldScreen worldScreen;

    private boolean loggedIn;

    private boolean userInfoChangeReceived;
    private boolean userNotificationChangeReceived;
    private boolean roomChangeReceived;
    private boolean worldChangeReceived;
    private boolean musicChangeReceived;
    private boolean worldListUpdateReceived;
    private boolean roomListUpdateReceived;

    private boolean changeUserInfo;
    private boolean changeNotificationInfo;
    private boolean changeRoom;
    private boolean changeWorld;
    private boolean changeMusic;
    private boolean changeWorldList;
    private boolean changeRoomList;

    private GLProfiler profiler;

    public Chati(IUserManagerView userManager) {
        CHATI = this;
        this.userManager = userManager;
    }

    public void start() {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setFullscreenMode(Lwjgl3ApplicationConfiguration.getDisplayMode());
        config.setForegroundFPS(60);
        config.setTitle("Chati");
        new Lwjgl3Application(this, config);
    }

    public void setScreen(AbstractScreen screen) {
        Gdx.input.setInputProcessor(screen.getInputProcessor());
        super.setScreen(screen);
    }

    @Override
    public AbstractScreen getScreen() {
        if (screen.equals(menuScreen)) {
            return menuScreen;
        }
        if (screen.equals(worldScreen)) {
            return worldScreen;
        }
        return null;
    }

    @Override
    public void create() {
        this.profiler = new GLProfiler(Gdx.graphics);
        profiler.enable();

        SPRITE_BATCH = new SpriteBatch();
        Assets.initialize();
        this.menuScreen = new MenuScreen();
        this.worldScreen = new WorldScreen();
        setScreen(menuScreen);
    }

    @Override
    public void render() {
        transferFlags();
        resetModelChangeReceivedFlags();
        super.render();
        resetModelChangedFlags();
    }

    public ServerSender getServerSender() {
        return serverSender;
    }

    public IUserManagerView getUserManager() {
        return userManager;
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
        if (this.screen.equals(menuScreen)) {
            menuScreen.updateWorlds(worlds);
        }
        worldListUpdateReceived = true;
    }

    @Override
    public void updateRooms(ContextID worldId, Map<ContextID, String> privateRooms) {
        if (this.screen.equals(worldScreen)) {
            // TODO
        }
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
    public void showChatMessage(UUID userId, LocalDateTime timestamp, MessageType messageType, String message,
            MessageBundle messageBundle) {
        if (this.screen.equals(worldScreen)) {
            Gdx.app.postRunnable(() -> HeadUpDisplay.getInstance()
                    .showChatMessage(userId, timestamp, messageType, message, messageBundle));
        }
    }

    @Override
    public void playVoiceData(UUID userID, LocalDateTime timestamp, byte[] voiceData) {
        if (this.screen.equals(worldScreen)) {
            // TODO
        }
    }

    @Override
    public void openMenu(ContextID contextId, Menu menu) {
        if (this.screen.equals(worldScreen)) {
            Gdx.app.postRunnable(() -> worldScreen.openMenu(contextId, menu));
        }
    }

    @Override
    public void closeMenu(ContextID contextId, Menu menu) {
        if (this.screen.equals(worldScreen)) {
            Gdx.app.postRunnable(() -> worldScreen.closeMenu(contextId, menu));
        }
    }

    @Override
    public void menuActionResponse(boolean success, String messageKey) {
        if (this.screen.equals(worldScreen)) {
            // TODO
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

    public boolean isWorldListUpdated() {
        return changeWorldList;
    }

    public boolean isWorldChanged() {
        return changeWorld;
    }

    public boolean isRoomChanged() {
        return changeRoom;
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
        roomChangeReceived = false;
        worldChangeReceived = false;
        musicChangeReceived = false;
        worldListUpdateReceived = false;
        roomListUpdateReceived = false;
    }

    private void transferFlags() {
        changeUserInfo = userInfoChangeReceived;
        changeNotificationInfo = userNotificationChangeReceived;
        changeRoom = roomChangeReceived;
        changeWorld = worldChangeReceived;
        changeMusic = musicChangeReceived;
        changeWorldList = worldListUpdateReceived;
        changeRoomList = roomListUpdateReceived;
    }

    private void resetModelChangedFlags() {
        changeUserInfo = false;
        changeNotificationInfo = false;
        changeRoom = false;
        changeWorld = false;
        changeMusic = false;
        changeWorldList = false;
        changeRoomList = false;
    }
}