package view.UIComponents;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import controller.network.ServerSender;
import model.exception.ContextNotFoundException;
import model.communication.message.MessageType;
import model.context.ContextID;
import model.context.spatial.Menu;
import org.jetbrains.annotations.NotNull;
import view.Chati;
import view.Screens.*;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class Hud extends Stage implements IModelObserver, ViewControllerInterface {
    private Viewport viewport;
    private OrthographicCamera camera;
    private ApplicationScreen applicationScreen;
    private LinkedList<Table> waitingResponse;
    private WidgetGroup group;
    private ContextID contextID;

    private boolean userInfoChanged = false;
    private boolean userNotificationChanged = false;
    private boolean userPositionChanged = false;
    private boolean worldChanged = false;
    private boolean roomChanged = false;

    private boolean waitingLoginResponse = false;
    private boolean waitingRegistrationResponse = false;
    private boolean waitingJoinWorldResponde = false;

    private String currentMenuTable = null;

    public Hud(SpriteBatch spriteBatch, ApplicationScreen applicationScreen) {
        super(new FitViewport(Chati.V_WIDTH, Chati.V_HEIGHT, new OrthographicCamera()), spriteBatch);
        Gdx.input.setInputProcessor(this);
        this.applicationScreen = applicationScreen;

        group = new WidgetGroup();
        group.setFillParent(true);
        group.setHeight(getHeight());
        group.setWidth(getWidth());
        addActor(group);

        waitingResponse = new LinkedList<>();
    }


    public void addMenuTable(UIComponentTable table) {
        if (Objects.isNull(table)) {
            group.clear();
            applicationScreen.getGame().setScreen(new PlayScreen(applicationScreen.getGame(), this));
            group.addActor(new DropDownMenus(this));
            group.addActor(new ChatWindow(this));
            currentMenuTable = null;
        } else if (Objects.isNull(currentMenuTable) || table.getName().equals("login-table")) {
            if (Objects.isNull(currentMenuTable) && !Objects.isNull(group.findActor("chat-window"))) {
                applicationScreen.getGame().setScreen(new MenuScreen(applicationScreen.getGame(), this));
                leaveWorld();
            }
            group.clear();
            group.addActor(table);
            currentMenuTable = table.getName();
            if (!table.getName().equals("login-table")) {
                group.addActor(new DropDownMenus(this));

            }
        } else if (currentMenuTable.equals("login-table")) {
            group.removeActor(group.findActor(currentMenuTable));
            group.addActor(table);
            group.addActor(new DropDownMenus(this));
            currentMenuTable = table.getName();
        } else {
            group.removeActor(group.findActor(currentMenuTable));
            group.addActor(table);
            currentMenuTable = table.getName();
        }
    }

    @Override
    public void setUserInfoChanged() {
        userInfoChanged = true;
    }

    @Override
    public void setUserNotificationChanged() {
        userNotificationChanged = true;
    }

    @Override
    public void setUserPositionChanged() {
        userPositionChanged = true;
    }

    @Override
    public void setWorldChanged() {
        worldChanged = true;
    }

    @Override
    public void setRoomChanged() {
        roomChanged = true;
    }

    @Override
    public void updateWorlds(Map<ContextID, String> worlds) {

    }

    @Override
    public void updateRooms(ContextID worldId, Map<ContextID, String> privateRooms) throws ContextNotFoundException {

    }

    @Override
    public void registrationResponse(boolean success, String messageKey) {
        LoginTable table = (LoginTable) group.findActor("login-table");
        if (!Objects.isNull(table) && waitingRegistrationResponse) {
            if (success) {
                addMenuTable(new StartScreenTable(this));
            } else {
                table.displayMessage(messageKey);
            }
        }

        waitingRegistrationResponse = false;
    }

    public void sendRegistrationRequest(String username, String password) {
        LoginTable table = (LoginTable) group.findActor("login-table");
        if (waitingRegistrationResponse) {
            table.displayMessage("Auf Antwort warten");
        } else {
            Object[] credentials = {username, password, true};
            ServerSender serverSender = applicationScreen.getGame().getServerSender();
            serverSender.send(ServerSender.SendAction.PROFILE_LOGIN, credentials);
            waitingRegistrationResponse = true;
        }
    }


    @Override
    public void loginResponse(boolean success, String messageKey) {
        LoginTable table = (LoginTable) group.findActor("login-table");
        if (!Objects.isNull(table) && waitingLoginResponse) {
            if (success) {
                addMenuTable(new StartScreenTable(this));
            } else {
                table.displayMessage(messageKey);
            }
        }

        waitingLoginResponse = false;
    }

    public void sendLoginRequest(String username, String password) {
        LoginTable table = (LoginTable) group.findActor("login-table");
        if (waitingLoginResponse) {
            table.displayMessage("Auf Antwort warten");
        } else {
            /*
            Object[] credentials = {username, password, false};
            ServerSender serverSender = applicationScreen.getGame().getServerSender();
            serverSender.send(ServerSender.SendAction.PROFILE_LOGIN, credentials);
            waitingLoginResponse = true; */
            addMenuTable(new StartScreenTable(this));
        }
    }

    @Override
    public void passwordChangeResponse(boolean success, String messageKey) {

    }

    @Override
    public void deleteAccountResponse(boolean success, String messageKey) {

    }

    @Override
    public void avatarChangeResponse(boolean success, String messageKey) {

    }

    @Override
    public void createWorldResponse(boolean success, String messageKey) {

    }

    @Override
    public void deleteWorldResponse(boolean success, String messageKey) {

    }

    public void joinWorldRequest (ContextID id) {
        contextID = id;
        waitingJoinWorldResponde = true;
        Object[] data = {id, true};
        ServerSender serverSender = applicationScreen.getGame().getServerSender();
        serverSender.send(ServerSender.SendAction.WORLD_ACTION, data);
    }

    public void leaveWorld () {
        Object[] data = {contextID, false};
        contextID = null;
        ServerSender serverSender = applicationScreen.getGame().getServerSender();
        serverSender.send(ServerSender.SendAction.WORLD_ACTION, data);
    }

    @Override
    public void joinWorldResponse(boolean success, String messageKey) {
        StartScreenTable table = (StartScreenTable) group.findActor("start-screen-table");
        if (success && !Objects.isNull(contextID)) {
            applicationScreen.getGame().setScreen(new PlayScreen(applicationScreen.getGame(), this));
        } else {
            table.displayMessage(messageKey);
        }
    }

    @Override
    public void showChatMessage(UUID userID, LocalDateTime timestamp, MessageType messageType) {
        ChatWindow chat = (ChatWindow)group.findActor("chat-window");
        if(!Objects.isNull(chat)) {
            chat.receiveMessage(timestamp + " " + userID + ": " + messageType);
        }
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


    public ApplicationScreen getApplicationScreen() {
        return applicationScreen;
    }

    public WidgetGroup getGroup() {
        return group;
    }
}
