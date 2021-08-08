package view2.component.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.FitViewport;
import model.context.ContextID;

import java.util.*;

public class MenuScreen extends ScreenAdapter {

    private final Stage menuTableStage;

    private final Set<ContextEntry> worlds;

    private Response pendingResponse;

    public MenuScreen() {
        this.menuTableStage = new Stage(new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), new OrthographicCamera()));
        Gdx.input.setInputProcessor(menuTableStage);
        this.worlds = new HashSet<>();
        this.pendingResponse = Response.NONE;
    }

    @Override
    public void render(float delta) {
        menuTableStage.act(delta);
        menuTableStage.draw();
    }

    public void registrationResponse(boolean success, String messageKey) {
        if (pendingResponse != Response.REGISTRATION) {
            return;
        }
        // Code
        setPendingResponse(Response.NONE);
    }

    public void loginResponse(boolean success, String messageKey) {
        if (pendingResponse != Response.LOGIN) {
            return;
        }
        // Code
        setPendingResponse(Response.NONE);
    }

    public void passwordChangeResponse(boolean success, String messageKey) {
        if (pendingResponse != Response.PASSWORD_CHANGE) {
            return;
        }
        // Code
        setPendingResponse(Response.NONE);
    }

    public void deleteAccountResponse(boolean success, String messageKey) {
        if (pendingResponse != Response.DELETE_ACCOUNT) {
            return;
        }
        // Code
        setPendingResponse(Response.NONE);
    }

    public void avatarChangeResponse(boolean success, String messageKey) {
        if (pendingResponse != Response.AVATAR_CHANGE) {
            return;
        }
        // Code
        setPendingResponse(Response.NONE);
    }

    public void createWorldResponse(boolean success, String messageKey) {
        if (pendingResponse != Response.CREATE_WORLD) {
            return;
        }
        // Code
        setPendingResponse(Response.NONE);
    }

    public void deleteWorldResponse(boolean success, String messageKey) {
        if (pendingResponse != Response.DELETE_WORLD) {
            return;
        }
        // Code
        setPendingResponse(Response.NONE);
    }

    public void joinWorldResponse(boolean success, String messageKey) {
        if (pendingResponse != Response.JOIN_WORLD) {
            return;
        }
        // Code
        setPendingResponse(Response.NONE);
    }

    public void updateWorlds(Map<ContextID, String> worlds) {
        worlds.forEach((key, value) -> this.worlds.add(new ContextEntry(key, value)));
    }

    public void setTable(Table table) {
        menuTableStage.getActors().forEach(Actor::remove);
        menuTableStage.addActor(table);
    }

    protected void setPendingResponse(Response pendingResponse) {
        this.pendingResponse = pendingResponse;
    }

    protected Set<ContextEntry> getWorlds() {
        return Collections.unmodifiableSet(worlds);
    }
}
