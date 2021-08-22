package view.UIComponents;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.utils.viewport.FitViewport;
import controller.network.ServerSender;
import model.context.spatial.SpatialMap;
import model.exception.ContextNotFoundException;
import model.communication.message.MessageType;
import model.context.ContextID;
import model.context.spatial.Menu;
import model.exception.UserNotFoundException;
import model.user.IUserView;
import org.jetbrains.annotations.Nullable;
import view.Chati;
import view.Screens.*;
import view2.IModelObserver;
import view2.ViewControllerInterface;

import java.time.LocalDateTime;
import java.util.*;

public class Hud extends Stage implements IModelObserver, ViewControllerInterface {
    private static Hud hud;

    private ServerSender sender;

    private ApplicationScreen applicationScreen;
    private LinkedList<Table> waitingResponse;
    private WidgetGroup group;
    private Map<ContextID, String> worlds = new HashMap<>();

    private ContextID contextID;

    private boolean isPlaying = false;

    private boolean waitingLoginResponse = false;
    private boolean waitingRegistrationResponse = false;
    private boolean waitingJoinWorldResponse = false;

    private String currentMenuTable = null;

    public Hud(SpriteBatch spriteBatch, ApplicationScreen applicationScreen) {
        super(new FitViewport(Chati.V_WIDTH, Chati.V_HEIGHT, new OrthographicCamera()), spriteBatch);
        hud = this;
        Gdx.input.setInputProcessor(this);
        this.applicationScreen = applicationScreen;

        group = new WidgetGroup();
        group.setFillParent(true);
        group.setHeight(getHeight());
        group.setWidth(getWidth());
        addActor(group);

        waitingResponse = new LinkedList<>();
    }

    public synchronized ServerSender getSender() {
        return this.sender;
    }

    @Override
    public synchronized void setSender(@Nullable final ServerSender sender) {
        this.sender = sender;
    }


    public void addMenuTable(MenuTable table) {
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

        /*
        if (Objects.isNull(table)) {
            playScreenLayout();
        } else if (table.getName().equals("login-table")) {
            logiLayout(table);
        } else if (Objects.isNull(group.findActor("drop-down-menu"))){
            group.removeActor(group.findActor(currentMenuTable));
            group.addActor(table);
            group.addActor(new DropDownMenu(this));
        }
         */

    }

    private void loginLayout(MenuTable table) {

    }

    private void playScreenLayout() {
        group.clear();
       // applicationScreen.getGame().setScreen(new ApplicationScreen(applicationScreen.getHud(), true)); // Test DELETE
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
    public void setInternUserPositionChanged() {

    }

    @Override
    public void setUserPositionChanged() {
        Gdx.app.postRunnable(() -> {
            if(isPlaying) {
                applicationScreen.updateAvatarsPositions();
            }
        });
    }

    /**
     * Benachrichtigt die View, dass der Benutzer ein Spatial Context erfolgreich beigetreten ist.
     */
    @Override
    public void setWorldChanged() {

    }

    @Override
    public void setRoomChanged() {
        Gdx.app.postRunnable(() -> {
            if (waitingJoinWorldResponse) {
                String mapPath = applicationScreen.getGame().getUserManager().getInternUserView().getCurrentWorld().getMap().getPath();
                Map<UUID, IUserView> users = applicationScreen.getGame().getUserManager().getActiveUsers();

                applicationScreen.getGame().setScreen(new ApplicationScreen(applicationScreen.getGame(), hud, mapPath, users));
                addMenuTable(null);
            }
        });
    }

    public void setMusicChanged() {

    }

    @Override
    public void updateWorlds(Map<ContextID, String> worlds) {
        Gdx.app.postRunnable(() -> hud.worlds = worlds);
    }

    @Override
    public void updateRooms(ContextID worldId, Map<ContextID, String> privateRooms) throws
            ContextNotFoundException {
    }

    @Override
    public void registrationResponse(boolean success, String messageKey) {
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {

            }
        });
        LoginTable table = (LoginTable) group.findActor("login-table");
        if (!Objects.isNull(table) && waitingRegistrationResponse) {
                table.displayMessage(messageKey);
        }

        waitingRegistrationResponse = false;
    }

    public void sendRegistrationRequest(String username, String password) {
        LoginTable table = (LoginTable) group.findActor("login-table");
        if (waitingRegistrationResponse) {
            table.displayMessage("Auf Antwort warten");
        } else {
            Object[] credentials = {username, password, true};
            sender.send(ServerSender.SendAction.PROFILE_LOGIN, credentials);
            waitingRegistrationResponse = true;
        }
    }


    @Override
    public void loginResponse(boolean success, String messageKey) {
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                LoginTable table = (LoginTable) group.findActor("login-table");
                if (!Objects.isNull(table) && waitingLoginResponse) {
                    if (success) {
                        addMenuTable(new StartScreenTable(hud));
                    } else {
                        table.displayMessage(messageKey);
                    }
                }

                waitingLoginResponse = false;
            }
        });
    }

    public void sendLoginRequest(String username, String password) {
        LoginTable table = (LoginTable) group.findActor("login-table");
        if (waitingLoginResponse) {
            table.displayMessage("Auf Antwort warten");
        } else {
            Object[] credentials = {username, password, false};
            sender.send(ServerSender.SendAction.PROFILE_LOGIN, credentials);
            waitingLoginResponse = true;
           // addMenuTable(new StartScreenTable(this));
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

    public void sendCreateWorldRequest(SpatialMap map, String worldName) {
        try {
            Object[] data = {map, worldName};
            sender.send(ServerSender.SendAction.WORLD_CREATE, data);
        } catch (NullPointerException exception) {
            System.out.println("ServerSender not found");
        }
    }

    @Override
    public void createWorldResponse(boolean success, String messageKey) {
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                StartScreenTable table = (StartScreenTable) group.findActor("start-screen-table");
                if (!Objects.isNull(table)) {
                    table.displayMessage(messageKey);
                }
            }
        });
    }

    @Override
    public void deleteWorldResponse(boolean success, String messageKey) {

    }

    public void sendJoinWorldRequest(ContextID id) {
        contextID = id;
        waitingJoinWorldResponse = true;
        Object[] data = {id, true};
        this.getSender().send(ServerSender.SendAction.WORLD_ACTION, data);
    }

    public void sendLeaveWorld() {
        Object[] data = {contextID, false};
        contextID = null;
        this.getSender().send(ServerSender.SendAction.WORLD_ACTION, data);
    }


    @Override
    public void joinWorldResponse(boolean success, String messageKey) {
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                StartScreenTable table = (StartScreenTable) group.findActor("start-screen-table");
                if (success && !Objects.isNull(contextID)) {
                    //applicationScreen.getGame().setScreen(new PlayScreen(applicationScreen.getGame(), this));
                } else {
                    table.displayMessage(messageKey);
                }
            }
        });

    }

    @Override
    public void updatePosition(UUID userID, int posX, int posY, boolean teleport, boolean sprint) {

    }


    @Override
    public void showChatMessage(UUID userID, LocalDateTime timestamp, MessageType messageType, String message) throws UserNotFoundException {
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                ChatWindow chat = (ChatWindow) group.findActor("chat-window");
                if (!Objects.isNull(chat)) {
                    chat.receiveMessage(timestamp +  ": " + message);
                }
            }
        });
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