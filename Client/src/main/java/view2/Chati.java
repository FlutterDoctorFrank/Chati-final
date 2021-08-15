package view2;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import controller.network.ServerSender;
import model.communication.message.MessageType;
import model.context.ContextID;
import model.context.spatial.Menu;
import model.user.IUserManagerView;
import org.jetbrains.annotations.Nullable;
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
    private SpriteBatch spriteBatch;

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

    @Override
    public void setScreen(Screen screen) {
        if (screen.equals(MenuScreen.getInstance())) {
            Gdx.input.setInputProcessor(MenuScreen.getInstance().getStage());
        } else if (screen.equals(WorldScreen.getInstance())) {
            Gdx.input.setInputProcessor(WorldScreen.getInstance().getStage());
        } else {
            return;
        }
        super.setScreen(screen);
    }

    @Override
    public void create() {
        SKIN = new Skin(Gdx.files.internal("shadeui/uiskin.json"));
        this.spriteBatch = new SpriteBatch();

        setScreen(MenuScreen.getInstance());
    }

    public SpriteBatch getSpriteBatch() {
        return spriteBatch;
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
    public void showChatMessage(UUID userID, LocalDateTime timestamp, MessageType messageType, String message) {

    }

    @Override
    public void playVoiceData(UUID userID, LocalDateTime timestamp, byte[] voiceData) {

    }

    @Override
    public void openMenu(Menu menu) {

    }

    @Override
    public void closeMenu(Menu menu) {

    }

    @Override
    public void menuActionResponse(boolean success, String messageKey) {

    }

    @Override
    public void logout() {
        Gdx.app.postRunnable(() -> {
            MenuScreen.getInstance().setMenuTable(new LoginTable());
            setScreen(MenuScreen.getInstance());
        });
    }

    public boolean isUserInfoChanged() {
        if (userInfoChanged) {
            userInfoChanged = false;
            return true;
        } else {
            return false;
        }
    }

    public boolean isUserNotificationChanged() {
        if (userNotificationChanged) {
            userNotificationChanged = false;
            return true;
        } else {
            return false;
        }
    }

    public boolean isWorldListUpdated() {
        if (worldListUpdated) {
            worldListUpdated = false;
            return true;
        } else {
            return false;
        }
    }
}