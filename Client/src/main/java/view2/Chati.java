package view2;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import controller.network.ServerSender;
import model.communication.message.MessageType;
import model.context.ContextID;
import model.context.spatial.Menu;
import model.user.IUserManagerView;
import org.jetbrains.annotations.Nullable;
import view2.component.AbstractScreen;
import view2.component.hud.ChatWindow;
import view2.component.hud.HeadUpDisplay;
import view2.component.menu.LoginTable;
import view2.component.menu.MenuScreen;
import view2.component.world.WorldScreen;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

public class Chati extends Game implements view.Screens.ViewControllerInterface, view.Screens.IModelObserver {

    public static Skin SKIN;

    private static Chati CHATI;

    private final IUserManagerView userManager;
    private ServerSender serverSender;

    private boolean userInfoChanged;
    private boolean userNotificationChanged;
    private boolean userPositionChanged;
    private boolean roomChanged;
    private boolean worldChanged;
    private boolean musicChanged;
    private boolean worldListUpdated;
    private boolean roomListUpdated;

    public Chati(IUserManagerView userManager) {
        CHATI = this;
        this.userManager = userManager;
    }

    public static Chati getInstance() {
        return CHATI;
    }

    public void setScreen(AbstractScreen screen) {
        Gdx.input.setInputProcessor(screen.getInputProcessor());
        screen.getStage().addActor(HeadUpDisplay.getInstance());
        super.setScreen(screen);
    }

    @Override
    public void create() {
        SKIN = new Skin(Gdx.files.internal("shadeui/uiskin.json"));
        setScreen(MenuScreen.getInstance());
    }

    @Override
    public void render() {
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
        this.userInfoChanged = true;
    }

    @Override
    public void setUserNotificationChanged() {
        this.userNotificationChanged = true;
    }

    @Override
    public void setUserPositionChanged() {
        this.userPositionChanged = true;
    }

    @Override
    public void setWorldChanged() {
        this.worldChanged = true;
    }

    @Override
    public void setRoomChanged() {
        this.roomChanged = true;
    }

    @Override
    public void setMusicChanged() {
        this.musicChanged = true;
    }

    @Override
    public void setSender(@Nullable ServerSender sender) {
        this.serverSender = sender;
    }

    @Override
    public void updateWorlds(Map<ContextID, String> worlds) {
        if (this.screen.equals(MenuScreen.getInstance())) {
            MenuScreen.getInstance().updateWorlds(worlds);
        }
        worldListUpdated = true;
    }

    @Override
    public void updateRooms(ContextID worldId, Map<ContextID, String> privateRooms) {
        if (this.screen.equals(WorldScreen.getInstance())) {
            // TODO
        }
        roomListUpdated = true;
    }

    @Override
    public void registrationResponse(boolean success, String messageKey) {
        if (this.screen.equals(MenuScreen.getInstance())) {
            MenuScreen.getInstance().registrationResponse(success, messageKey);
        }
    }

    @Override
    public void loginResponse(boolean success, String messageKey) {
        if (this.screen.equals(MenuScreen.getInstance())) {
            MenuScreen.getInstance().loginResponse(success, messageKey);
        }
    }

    @Override
    public void passwordChangeResponse(boolean success, String messageKey) {
        if (this.screen.equals(MenuScreen.getInstance())) {
            MenuScreen.getInstance().passwordChangeResponse(success, messageKey);
        }
    }

    @Override
    public void deleteAccountResponse(boolean success, String messageKey) {
        if (this.screen.equals(MenuScreen.getInstance())) {
            MenuScreen.getInstance().deleteAccountResponse(success, messageKey);
        }
    }

    @Override
    public void avatarChangeResponse(boolean success, String messageKey) {
        if (this.screen.equals(MenuScreen.getInstance())) {
            MenuScreen.getInstance().avatarChangeResponse(success, messageKey);
        }
    }

    @Override
    public void createWorldResponse(boolean success, String messageKey) {
        if (this.screen.equals(MenuScreen.getInstance())) {
            MenuScreen.getInstance().createWorldResponse(success, messageKey);
        }
    }

    @Override
    public void deleteWorldResponse(boolean success, String messageKey) {
        if (this.screen.equals(MenuScreen.getInstance())) {
            MenuScreen.getInstance().deleteWorldResponse(success, messageKey);
        }
    }

    @Override
    public void joinWorldResponse(boolean success, String messageKey) {
        if (this.screen.equals(MenuScreen.getInstance())) {
            MenuScreen.getInstance().joinWorldResponse(success, messageKey);
        }
    }

    @Override
    public void showChatMessage(UUID userId, LocalDateTime timestamp, MessageType messageType, String message) {
        if (this.screen.equals(WorldScreen.getInstance())) {
            ChatWindow.getInstance().showMessage(userId, message, messageType, timestamp);
        }
    }

    @Override
    public void playVoiceData(UUID userID, LocalDateTime timestamp, byte[] voiceData) {
        if (this.screen.equals(WorldScreen.getInstance())) {
            // TODO
        }
    }

    @Override
    public void openMenu(Menu menu) {
        if (this.screen.equals(WorldScreen.getInstance())) {
            // TODO
        }
    }

    @Override
    public void closeMenu(Menu menu) {
        if (this.screen.equals(WorldScreen.getInstance())) {
            // TODO
        }
    }

    @Override
    public void menuActionResponse(boolean success, String messageKey) {
        if (this.screen.equals(WorldScreen.getInstance())) {
            // TODO
        }
    }

    @Override
    public void logout() {
        Gdx.app.postRunnable(() -> {
            MenuScreen.getInstance().setMenuTable(new LoginTable());
            setScreen(MenuScreen.getInstance());
        });
    }

    public boolean isUserInfoChanged() {
        return userInfoChanged;
    }

    public boolean isUserNotificationChanged() {
        return userNotificationChanged;
    }

    public boolean isWorldListUpdated() {
        return worldListUpdated;
    }

    public boolean isWorldChanged() {
        return worldChanged;
    }

    public boolean isRoomChanged() {
        return roomChanged;
    }

    public boolean isUserPositionChanged() {
        return userPositionChanged;
    }

    private void resetModelChangedFlags() {
        userInfoChanged = false;
        userNotificationChanged = false;
        userPositionChanged = false;
        roomChanged = false;
        worldChanged = false;
        musicChanged = false;
        worldListUpdated = false;
        roomListUpdated = false;
    }
}