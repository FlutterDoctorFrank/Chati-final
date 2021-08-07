package view2.component.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.FitViewport;
import model.context.ContextID;
import view.Screens.IModelObserver;

import java.util.*;

public class MenuScreen extends ScreenAdapter implements IMenuScreen, IModelObserver {

    private final Stage menuTableStage;

    private final Set<ContextEntry> worlds;

    public MenuScreen() {
        this.menuTableStage = new Stage(new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), new OrthographicCamera()));
        Gdx.input.setInputProcessor(menuTableStage);
        this.menuTableStage.addActor(new LoginTable(this));
        this.worlds = new HashSet<>();
    }

    @Override
    public void render(float delta) {
        menuTableStage.act(delta);
        menuTableStage.draw();
    }

    @Override
    public void registrationResponse(boolean success, String messageKey) {

    }

    @Override
    public void loginResponse(boolean success, String messageKey) {

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
    public void updateWorlds(Map<ContextID, String> worlds) {
        worlds.forEach((key, value) -> this.worlds.add(new ContextEntry(key, value)));
    }

    @Override
    public void setUserInfoChanged() {

    }

    @Override
    public void setUserNotificationChanged() {

    }

    @Override
    public void setUserPositionChanged() {

    }

    @Override
    public void setWorldChanged() {

    }

    @Override
    public void setRoomChanged() {

    }

    @Override
    public void setMusicChanged() {

    }

    protected void setTable(Table table) {
        menuTableStage.getActors().forEach(Actor::remove);
        menuTableStage.addActor(table);
    }

    protected Set<ContextEntry> getWorlds() {
        return Collections.unmodifiableSet(worlds);
    }
}
