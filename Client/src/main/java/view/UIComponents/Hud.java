package view.UIComponents;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import controller.network.ClientNetworkManager;
import controller.network.ServerConnection;
import controller.network.ServerSender;
import model.communication.message.MessageType;
import model.context.spatial.Menu;
import view.Chati;
import view.Screens.ApplicationScreen;
import view.Screens.IModelObserver;
import view.Screens.ViewControllerInterface;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.UUID;

public class Hud implements IModelObserver, ViewControllerInterface {
    private Stage stage;
    private Viewport viewport;
    private OrthographicCamera camera;
    private ApplicationScreen applicationScreen;
    private LinkedList<Table> waitingResponse;

    private boolean userInfoChanged = false;
    private boolean userNotificationChanged = false;
    private boolean userPositionChanged = false;
    private boolean worldInfoChanged = false;
    private boolean roomInfoChanged = false;
    private boolean mapChanged = false;

    public Hud(SpriteBatch spriteBatch, ApplicationScreen applicationScreen) {
        camera = new OrthographicCamera();
        viewport = new FitViewport(Chati.V_WIDTH, Chati.V_HEIGHT, camera);
        stage = new Stage(viewport, spriteBatch);
        Gdx.input.setInputProcessor(stage);
        this.applicationScreen = applicationScreen;

        waitingResponse = new LinkedList<>();
    }

    public Stage getStage() {
        return stage;
    }

    public void addTable(Table table) {
        stage.clear();
        stage.addActor(table);
    }

    public ApplicationScreen getApplicationScreen() {
        return applicationScreen;
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
    public void setWorldInfoChanged() {
        worldInfoChanged = true;
    }

    @Override
    public void setRoomInfoChanged() {
        roomInfoChanged = true;
    }

    @Override
    public void setMapChanged() {
        mapChanged = true;
    }

    @Override
    public void registrationResponse(boolean success, String messageKey) {
        for (Actor actor : stage.getActors()) {
            if(actor.getClass().equals(LoginTable.class)) {
                ((LoginTable) actor).receiveLoginResponse(success, messageKey);
            }
        }
    }

    @Override
    public void loginResponse(boolean success, String messageKey) {
        for (Actor actor : stage.getActors()) {
            if(actor.getClass().equals(LoginTable.class)) {
                ((LoginTable) actor).receiveLoginResponse(success, messageKey);
            }
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

    @Override
    public void joinWorldResponse(boolean success, String messageKey) {

    }

    @Override
    public void showChatMessage(UUID userID, LocalDateTime timestamp, MessageType messageType) {

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

    public void sendRequest(Object[] credentials) {
        ServerSender serverSender = applicationScreen.getGame().getServerSender();
        serverSender.send(ServerSender.SendAction.PROFILE_LOGIN, credentials);
    }
}
