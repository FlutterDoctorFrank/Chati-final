package view2;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import controller.network.ServerSender;
import model.communication.message.MessageType;
import model.context.ContextID;
import model.context.spatial.Menu;
import model.user.IUserManagerView;
import org.jetbrains.annotations.Nullable;
import view2.component.game.WorldScreen;
import view2.component.menu.LoginTable;
import view2.component.menu.MenuScreen;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

public class Chati extends Game implements view.Screens.ViewControllerInterface, view.Screens.IModelObserver {

    private static Chati CHATI;

    private final IUserManagerView userManager;
    private ServerSender serverSender;
    private MenuScreen menuScreen;
    private WorldScreen worldScreen;
    private SpriteBatch spriteBatch;

    private boolean userInfoChanged;
    private boolean userNotificationChanged;
    private boolean userPositionChanged;
    private boolean roomChanged;
    private boolean worldChanged;
    private boolean musicChanged;

    public Chati(IUserManagerView userManager) {
        CHATI = this;
        this.userManager = userManager;
    }

    public static Chati getInstance() {
        return CHATI;
    }

    @Override
    public void create() {
        this.menuScreen = new MenuScreen();
        this.worldScreen = new WorldScreen();
        this.spriteBatch = new SpriteBatch();

        menuScreen.setTable(new LoginTable());
        setScreen(menuScreen);
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

    public MenuScreen getMenuScreen() {
        return menuScreen;
    }

    public WorldScreen getWorldScreen() {
        return worldScreen;
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
        if (this.screen.equals(menuScreen)) {
            menuScreen.updateWorlds(worlds);
        }
    }

    @Override
    public void updateRooms(ContextID worldId, Map<ContextID, String> privateRooms) {

    }

    @Override
    public void registrationResponse(boolean success, String messageKey) {
        System.out.println("Da is was.");
        if (this.screen.equals(menuScreen)) {
            menuScreen.registrationResponse(success, messageKey);
        }
    }

    @Override
    public void loginResponse(boolean success, String messageKey) {
        if (this.screen.equals(menuScreen)) {
            menuScreen.loginResponse(success, messageKey);
        }
    }

    @Override
    public void passwordChangeResponse(boolean success, String messageKey) {
        if (this.screen.equals(menuScreen)) {
            menuScreen.passwordChangeResponse(success, messageKey);
        }
    }

    @Override
    public void deleteAccountResponse(boolean success, String messageKey) {
        if (this.screen.equals(menuScreen)) {
            menuScreen.deleteAccountResponse(success, messageKey);
        }
    }

    @Override
    public void avatarChangeResponse(boolean success, String messageKey) {
        if (this.screen.equals(menuScreen)) {
            menuScreen.avatarChangeResponse(success, messageKey);
        }
    }

    @Override
    public void createWorldResponse(boolean success, String messageKey) {
        if (this.screen.equals(menuScreen)) {
            menuScreen.createWorldResponse(success, messageKey);
        }
    }

    @Override
    public void deleteWorldResponse(boolean success, String messageKey) {
        if (this.screen.equals(menuScreen)) {
            menuScreen.deleteWorldResponse(success, messageKey);
        }
    }

    @Override
    public void joinWorldResponse(boolean success, String messageKey) {
        if (this.screen.equals(menuScreen)) {
            menuScreen.joinWorldResponse(success, messageKey);
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
        menuScreen.setTable(new LoginTable());
        setScreen(menuScreen);
    }
}