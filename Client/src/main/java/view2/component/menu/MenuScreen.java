package view2.component.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import model.context.ContextID;
import view2.Chati;
import view2.component.AbstractScreen;
import view2.component.hud.HeadUpDisplay;
import view2.component.world.WorldScreen;

import java.util.*;

public class MenuScreen extends AbstractScreen {

    private static int loginFailCounter = 0;

    private static MenuScreen menuScreen;

    private MenuTable currentMenuTable;
    private MenuResponse pendingMenuResponse;

    private final Set<ContextEntry> worlds;

    private MenuScreen() {
        this.worlds = new HashSet<>();
        this.pendingMenuResponse = MenuResponse.NONE;
        setMenuTable(new LoginTable());
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        super.render(delta);
    }

    @Override
    public InputProcessor getInputProcessor() {
        return new InputMultiplexer(stage, new MenuInputProcessor());
    }

    public void registrationResponse(boolean success, String messageKey) {
        if (pendingMenuResponse != MenuResponse.REGISTRATION) {
            return;
        }
        setPendingResponse(MenuResponse.NONE);
        if (success) {
            currentMenuTable.showMessage("Die Registrierung war erfolgreich!");
        } else {
            currentMenuTable.showMessage(messageKey);
            currentMenuTable.resetTextFields();
        }
    }

    public void loginResponse(boolean success, String messageKey) {
        if (pendingMenuResponse != MenuResponse.LOGIN) {
            return;
        }
        setPendingResponse(MenuResponse.NONE);
        if (success) {
            Gdx.app.postRunnable(() -> setMenuTable(new StartTable()));
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
    }

    public void passwordChangeResponse(boolean success, String messageKey) {
        if (pendingMenuResponse != MenuResponse.PASSWORD_CHANGE) {
            return;
        }
        setPendingResponse(MenuResponse.NONE);
        if (success) {
            Gdx.app.postRunnable(() -> {
                setMenuTable(new StartTable());
                currentMenuTable.showMessage("Dein Passwort wurde geändert!");
            });
        } else {
            currentMenuTable.showMessage(messageKey);
            currentMenuTable.resetTextFields();
        }
    }

    public void deleteAccountResponse(boolean success, String messageKey) {
        if (pendingMenuResponse != MenuResponse.DELETE_ACCOUNT) {
            return;
        }
        setPendingResponse(MenuResponse.NONE);
        if (success) {
            Gdx.app.postRunnable(() -> {
                setMenuTable(new LoginTable());
                currentMenuTable.showMessage("Dein Konto wurde gelöscht.");
            });
        } else {
            currentMenuTable.showMessage(messageKey);
            currentMenuTable.resetTextFields();
        }
    }

    public void avatarChangeResponse(boolean success, String messageKey) {
        if (pendingMenuResponse != MenuResponse.AVATAR_CHANGE) {
            return;
        }
        setPendingResponse(MenuResponse.NONE);
        if (success) {
            Gdx.app.postRunnable(() -> {
                setMenuTable(new StartTable());
                currentMenuTable.showMessage("Dein Avatar wurde geändert!");
            });
        } else {
            currentMenuTable.showMessage(messageKey);
        }
    }

    public void createWorldResponse(boolean success, String messageKey) {
        if (pendingMenuResponse != MenuResponse.CREATE_WORLD) {
            return;
        }
        setPendingResponse(MenuResponse.NONE);
        if (success) {
            Gdx.app.postRunnable(() -> {
                setMenuTable(new StartTable());
                currentMenuTable.showMessage("Die Welt wurde erfolgreich erstellt!");
            });
        } else {
            currentMenuTable.showMessage(messageKey);
            currentMenuTable.resetTextFields();
        }
    }

    public void deleteWorldResponse(boolean success, String messageKey) {
        if (pendingMenuResponse != MenuResponse.DELETE_WORLD) {
            return;
        }
        setPendingResponse(MenuResponse.NONE);
        if (success) {
            currentMenuTable.showMessage("Die Welt wurde erfolgreich gelöscht.");
        } else {
            currentMenuTable.showMessage(messageKey);
        }
    }

    public void joinWorldResponse(boolean success, String messageKey) {
        if (pendingMenuResponse != MenuResponse.JOIN_WORLD) {
            return;
        }
        setPendingResponse(MenuResponse.NONE);
        if (success) {
            Gdx.app.postRunnable(() -> {
                Chati.getInstance().setScreen(WorldScreen.getInstance());
            });
        } else {
            currentMenuTable.showMessage(messageKey);
        }
    }

    public void updateWorlds(Map<ContextID, String> worlds) {
        this.worlds.clear();
        worlds.forEach((key, value) -> this.worlds.add(new ContextEntry(key, value)));
    }

    public void setMenuTable(MenuTable table) {
        if (currentMenuTable != null) {
            currentMenuTable.remove();
        }
        stage.addActor(currentMenuTable = table);
    }

    protected void setPendingResponse(MenuResponse pendingMenuResponse) {
        this.pendingMenuResponse = pendingMenuResponse;
    }

    protected Set<ContextEntry> getWorlds() {
        return Collections.unmodifiableSet(worlds);
    }

    public static MenuScreen getInstance() {
        if (menuScreen == null) {
            menuScreen = new MenuScreen();
            menuScreen.getStage().addActor(HeadUpDisplay.getInstance());
        }
        return menuScreen;
    }
}
