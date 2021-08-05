package view.UIComponents;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.utils.viewport.FitViewport;
import controller.network.ServerSender;
import model.exception.ContextNotFoundException;
import model.communication.message.MessageType;
import model.context.ContextID;
import model.context.spatial.Menu;
import model.exception.UserNotFoundException;
import view.Chati;
import view.Screens.*;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class Hud extends Stage implements IModelObserver, ViewControllerInterface {
    private ApplicationScreen applicationScreen;
    private LinkedList<Table> waitingResponse;
    private WidgetGroup group;
    Map<ContextID, String> worlds;
    private ContextID contextID;

    private boolean isPlaying = false;


    private boolean waitingLoginResponse = false;
    private boolean waitingRegistrationResponse = false;
    private boolean waitingJoinWorldResponse = false;

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
            playScreenLayout();
        } else if (Objects.isNull(currentMenuTable) || table.getName().equals("login-table")) {
            if (Objects.isNull(currentMenuTable) && isPlaying) {
                applicationScreen.getGame().setScreen(new ApplicationScreen(applicationScreen.getHud()));
                sendLeaveWorld();
            }
            group.clear();
            group.addActor(table);
            currentMenuTable = table.getName();
            if (!table.getName().equals("login-table")) {
                group.addActor(new DropDownMenu(this));

            }
        } else if (currentMenuTable.equals("login-table")) {
            group.removeActor(group.findActor(currentMenuTable));
            group.addActor(table);
            group.addActor(new DropDownMenu(this));
            currentMenuTable = table.getName();
        } else {
            group.removeActor(group.findActor(currentMenuTable));
            group.addActor(table);
            currentMenuTable = table.getName();
        }
    }

    private void loginLayout() {

    }

    private void playScreenLayout() {
        group.clear();
        applicationScreen.getGame().setScreen(new ApplicationScreen(applicationScreen.getHud(), true));
        group.addActor(new DropDownMenu(this));
        group.addActor(new ChatWindow(this));

        currentMenuTable = null;
        isPlaying = true;
    }

    /**
     * Fügt eine Popup Tabelle zum Bildschirm hinzu oder löscht diese vom Bildschirm
     *
     * @param menu Das Menü, das hinzugefügt oder gelöscht werden muss
     * @param add  true falls das Menu hinzugefügt werden muss, false zum Löschen
     */
    public void addPopupMenu(PopupMenu menu, boolean add) {
        if (add) {
            group.addActor(menu);
        } else {
            group.removeActor(group.findActor(menu.getName()));
        }
    }

    @Override
    public void setUserInfoChanged() {

    }

    @Override
    public void setUserNotificationChanged() {

    }

    @Override
    public void setUserPositionChanged() {
        if(isPlaying) {
            applicationScreen.updateAvatarsPositions();
        }
    }

    /**
     * Benachrichtigt die View, dass der Benutzer ein Spatial Context erfolgreich beigetreten ist.
     */
    @Override
    public void setWorldChanged() {
        /*
        if (waitingJoinWorldResponse) {
            String mapPath = applicationScreen.getGame().getContext().getCurrentWold().getMap().getPath();
            Map<UUID, IUserView> users = applicationScreen.getGame().getUserManager().getActiveUsers();

            applicationScreen.getGame().setScreen(new ApplicationScreen(applicationScreen.getGame(), this, mapPath, users));
            addMenuTable(null);
        }
        */
    }

    @Override
    public void setRoomChanged() {
    }

    public void setMusicChanged() {

    }

    @Override
    public void updateWorlds(Map<ContextID, String> worlds) {
        this.worlds = worlds;

    }

    @Override
    public void updateRooms(ContextID worldId, Map<ContextID, String> privateRooms) throws
            ContextNotFoundException {
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
            waitingLoginResponse = true;

             */
            addMenuTable(new StartScreenTable(this));
        }
    }

    public void sendTextMessage(String[] messageToSend) {
        Object[] message = {messageToSend};
        ServerSender serverSender = applicationScreen.getGame().getServerSender();
        serverSender.send(ServerSender.SendAction.MESSAGE, message);
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

    public void sendJoinWorldRequest(ContextID id) {
        contextID = id;
        waitingJoinWorldResponse = true;
        Object[] data = {id, true};
        ServerSender serverSender = applicationScreen.getGame().getServerSender();
        serverSender.send(ServerSender.SendAction.WORLD_ACTION, data);
    }

    public void sendLeaveWorld() {
        Object[] data = {contextID, false};
        contextID = null;
        ServerSender serverSender = applicationScreen.getGame().getServerSender();
        serverSender.send(ServerSender.SendAction.WORLD_ACTION, data);
    }


    @Override
    public void joinWorldResponse(boolean success, String messageKey) {
        StartScreenTable table = (StartScreenTable) group.findActor("start-screen-table");
        if (success && !Objects.isNull(contextID)) {
            //applicationScreen.getGame().setScreen(new PlayScreen(applicationScreen.getGame(), this));
        } else {
            table.displayMessage(messageKey);
        }
    }


    @Override
    public void showChatMessage(UUID userID, LocalDateTime timestamp, MessageType messageType, String message) throws UserNotFoundException {
        ChatWindow chat = (ChatWindow) group.findActor("chat-window");
        if (!Objects.isNull(chat)) {
            String name = applicationScreen.getGame().getUserManager().getExternUserView(userID).getUsername();
            chat.receiveMessage(timestamp + " " + name +  ": " + message);
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

    public WidgetGroup getGroup() {
        return group;
    }

    public Map<ContextID, String> getWorlds() {
        return worlds;
    }

    public ApplicationScreen getApplicationScreen() {
        return applicationScreen;
    }
}