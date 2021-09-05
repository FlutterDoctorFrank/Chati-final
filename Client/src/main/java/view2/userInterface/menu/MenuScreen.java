package view2.userInterface.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import view2.Chati;
import view2.ChatiScreen;
import view2.userInterface.Response;
import view2.userInterface.menu.table.LoginTable;
import view2.userInterface.menu.table.MenuTable;
import view2.userInterface.menu.table.StartTable;

public class MenuScreen extends ChatiScreen {

    private MenuTable currentMenuTable;

    public MenuScreen() {
        setMenuTable(new LoginTable());
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        super.render(delta);
    }

    public void registrationResponse(boolean success, String messageKey) {
        if (pendingResponse != Response.REGISTRATION) {
            return;
        }
        setPendingResponse(Response.NONE);
        if (success) {
            currentMenuTable.showMessage("Die Registrierung war erfolgreich!");
        } else {
            currentMenuTable.showMessage(messageKey);
            currentMenuTable.resetTextFields();
        }
    }

    public void loginResponse(boolean success, String messageKey) {
        if (pendingResponse != Response.LOGIN) {
            return;
        }
        setPendingResponse(Response.NONE);
        if (success) {
            setMenuTable(new StartTable());
        } else {
            currentMenuTable.resetTextFields();
        }
    }

    public void passwordChangeResponse(boolean success, String messageKey) {
        if (pendingResponse != Response.PASSWORD_CHANGE) {
            return;
        }
        setPendingResponse(Response.NONE);
        if (success) {
            setMenuTable(new StartTable());
            currentMenuTable.showMessage("Dein Passwort wurde geändert!");
        } else {
            currentMenuTable.showMessage(messageKey);
            currentMenuTable.resetTextFields();
        }
    }

    public void deleteAccountResponse(boolean success, String messageKey) {
        if (pendingResponse != Response.DELETE_ACCOUNT) {
            return;
        }
        setPendingResponse(Response.NONE);
        if (success) {
            setMenuTable(new LoginTable());
            currentMenuTable.showMessage("Dein Konto wurde gelöscht.");
        } else {
            currentMenuTable.showMessage(messageKey);
            currentMenuTable.resetTextFields();
        }
    }

    public void avatarChangeResponse(boolean success, String messageKey) {
        if (pendingResponse != Response.AVATAR_CHANGE) {
            return;
        }
        setPendingResponse(Response.NONE);
        if (success) {
            setMenuTable(new StartTable());
            currentMenuTable.showMessage("Dein Avatar wurde geändert!");
        } else {
            currentMenuTable.showMessage(messageKey);
        }
    }

    public void createWorldResponse(boolean success, String messageKey) {
        if (pendingResponse != Response.CREATE_WORLD) {
            return;
        }
        setPendingResponse(Response.NONE);
        if (success) {
            setMenuTable(new StartTable());
            currentMenuTable.showMessage("Die Welt wurde erfolgreich erstellt!");
        } else {
            currentMenuTable.showMessage(messageKey);
            currentMenuTable.resetTextFields();
        }
    }

    public void deleteWorldResponse(boolean success, String messageKey) {
        if (pendingResponse != Response.DELETE_WORLD) {
            return;
        }
        setPendingResponse(Response.NONE);
        if (success) {
            currentMenuTable.showMessage("Die Welt wurde erfolgreich gelöscht.");
        } else {
            currentMenuTable.showMessage(messageKey);
        }
    }

    public void joinWorldResponse(boolean success, String messageKey) {
        if (pendingResponse != Response.JOIN_WORLD) {
            return;
        }
        setPendingResponse(Response.NONE);
        if (success) {
            Chati.CHATI.setScreen(Chati.CHATI.getWorldScreen());
        } else {
            currentMenuTable.showMessage(messageKey);
        }
    }

    public void setMenuTable(MenuTable table) {
        if (currentMenuTable != null) {
            currentMenuTable.remove();
        }
        stage.addActor(currentMenuTable = table);
    }

    @Override
    public InputProcessor getInputProcessor() {
        return stage;
    }
}
