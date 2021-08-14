package view2.component.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import model.context.ContextID;
import view2.component.hud.HeadUpDisplay;

import java.util.*;

public class MenuScreen extends ScreenAdapter {

    private static int loginFailCounter = 0;

    private final Stage stage;
    private MenuTable currentMenuTable;
    private Response pendingResponse;

    private final Set<ContextEntry> worlds;

    public MenuScreen() {
        this.stage = new Stage();
        stage.addActor(HeadUpDisplay.getInstance());
        Gdx.input.setInputProcessor(stage);
        this.worlds = new HashSet<>();
        this.pendingResponse = Response.NONE;
        setTable(new LoginTable());
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();
    }

    public void registrationResponse(boolean success, String messageKey) {
        if (pendingResponse != Response.REGISTRATION) {
            return;
        }
        if (success) {
            currentMenuTable.showMessage("Die Registrierung war erfolgreich!");
        } else {
            currentMenuTable.showMessage(messageKey);
            currentMenuTable.resetTextFields();
        }
        setPendingResponse(Response.NONE);
    }

    public void loginResponse(boolean success, String messageKey) {
        if (pendingResponse != Response.LOGIN) {
            return;
        }
        if (success) {
            Gdx.app.postRunnable(() -> setTable(new StartTable()));
            loginFailCounter = 0;
        } else {
            if (loginFailCounter == 2) {
                currentMenuTable.showMessage("Bist du zu blöd dir dein Passwort zu merken?");
            } else {
                currentMenuTable.showMessage(messageKey);
            }
            currentMenuTable.resetTextFields();
            loginFailCounter++;
        }
        setPendingResponse(Response.NONE);
    }

    public void passwordChangeResponse(boolean success, String messageKey) {
        if (pendingResponse != Response.PASSWORD_CHANGE) {
            return;
        }
        if (success) {
            Gdx.app.postRunnable(() -> {
                setTable(new StartTable());
                currentMenuTable.showMessage("Dein Passwort wurde geändert!");
            });
        } else {
            currentMenuTable.showMessage(messageKey);
            currentMenuTable.resetTextFields();
        }
        setPendingResponse(Response.NONE);
    }

    public void deleteAccountResponse(boolean success, String messageKey) {
        if (pendingResponse != Response.DELETE_ACCOUNT) {
            return;
        }
        if (success) {
            Gdx.app.postRunnable(() -> {
                setTable(new LoginTable());
                currentMenuTable.showMessage("Dein Konto wurde gelöscht.");
            });
        } else {
            currentMenuTable.showMessage(messageKey);
            currentMenuTable.resetTextFields();
        }
        setPendingResponse(Response.NONE);
    }

    public void avatarChangeResponse(boolean success, String messageKey) {
        if (pendingResponse != Response.AVATAR_CHANGE) {
            return;
        }
        if (success) {
            Gdx.app.postRunnable(() -> {
                setTable(new StartTable());
                currentMenuTable.showMessage("Dein Avatar wurde geändert!");
            });
        } else {
            currentMenuTable.showMessage(messageKey);
        }
        setPendingResponse(Response.NONE);
    }

    public void createWorldResponse(boolean success, String messageKey) {
        if (pendingResponse != Response.CREATE_WORLD) {
            return;
        }
        if (success) {
            Gdx.app.postRunnable(() -> {
                setTable(new StartTable());
                currentMenuTable.showMessage("Die Welt wurde erfolgreich erstellt!");
            });
        } else {
            currentMenuTable.showMessage(messageKey);
            currentMenuTable.resetTextFields();
        }
        setPendingResponse(Response.NONE);
    }

    public void deleteWorldResponse(boolean success, String messageKey) {
        if (pendingResponse != Response.DELETE_WORLD) {
            return;
        }
        if (success) {
            currentMenuTable.showMessage("Die Welt wurde erfolgreich gelöscht.");
        } else {
            currentMenuTable.showMessage(messageKey);
        }
        setPendingResponse(Response.NONE);
    }

    public void joinWorldResponse(boolean success, String messageKey) {
        if (pendingResponse != Response.JOIN_WORLD) {
            return;
        }
        if (success) {
            // Join world...
        } else {
            currentMenuTable.showMessage(messageKey);
        }
        setPendingResponse(Response.NONE);
    }

    public void updateWorlds(Map<ContextID, String> worlds) {
        this.worlds.clear();
        worlds.forEach((key, value) -> this.worlds.add(new ContextEntry(key, value)));
    }

    public void setTable(MenuTable table) {
        if (currentMenuTable != null) {
            currentMenuTable.remove();
        }
        stage.addActor(currentMenuTable = table);
    }

    protected void setPendingResponse(Response pendingResponse) {
        this.pendingResponse = pendingResponse;
    }

    protected Set<ContextEntry> getWorlds() {
        return Collections.unmodifiableSet(worlds);
    }

    public Stage getStage() {
        return stage;
    }
}
