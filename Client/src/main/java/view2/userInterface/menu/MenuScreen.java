package view2.userInterface.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import view2.Chati;
import view2.ChatiScreen;
import view2.Response;
import view2.userInterface.menu.table.LoginTable;
import view2.userInterface.menu.table.MenuTable;
import view2.userInterface.menu.table.StartTable;

/**
 * Eine Klasse, welche den Menübildschirm der Anwendung repräsentiert.
 */
public class MenuScreen extends ChatiScreen {

    private MenuTable currentMenuTable;

    /**
     * Erzeugt eine neue Instanz des MenuScreen.
     */
    public MenuScreen() {
        setMenuTable(new LoginTable());
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        super.render(delta);
    }

    /**
     * Verarbeitet die Antwort auf eine durchgeführte Registrierung.
     * @param success Information, ob die Registrierung erfolgreich war.
     * @param messageKey Kennung der anzuzeigenden Nachricht.
     */
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

    /**
     * Verarbeitet die Antwort auf eine durchgeführte Anmeldung.
     * @param success Information, ob die Anmeldung erfolgreich war.
     * @param messageKey Kennung der anzuzeigenden Nachricht.
     */
    public void loginResponse(boolean success, String messageKey) {
        if (pendingResponse != Response.LOGIN) {
            return;
        }
        setPendingResponse(Response.NONE);
        if (success) {
            setMenuTable(new StartTable());
        } else {
            currentMenuTable.showMessage(messageKey);
            currentMenuTable.resetTextFields();
        }
    }

    /**
     * Verarbeitet die Antwort auf eine durchgeführte Passwortänderung.
     * @param success Information, ob die Passwortänderung erfolgreich war.
     * @param messageKey Kennung der anzuzeigenden Nachricht.
     */
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

    /**
     * Verarbeitet die Antwort auf eine durchgeführte Kontolöschung.
     * @param success Information, ob die Kontolöschung erfolgreich war.
     * @param messageKey Kennung der anzuzeigenden Nachricht.
     */
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

    /**
     * Verarbeitet die Antwort auf eine durchgeführte Avataränderung.
     * @param success Information, ob die Avataränderung erfolgreich war.
     * @param messageKey Kennung der anzuzeigenden Nachricht.
     */
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

    /**
     * Verarbeitet die Antwort auf eine durchgeführte Welterstellung.
     * @param success Information, ob die Welterstellung erfolgreich war.
     * @param messageKey Kennung der anzuzeigenden Nachricht.
     */
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

    /**
     * Verarbeitet die Antwort auf eine durchgeführte Weltlöschung.
     * @param success Information, ob die Weltlöschung erfolgreich war.
     * @param messageKey Kennung der anzuzeigenden Nachricht.
     */
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

    /**
     * verarbeitet die Antwort auf einen durchgeführten Weltbeitritt.
     * @param success Information, ob der Weltbeitritt erfolgreich war.
     * @param messageKey Kennung der anzuzeigenden Nachricht.
     */
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

    /**
     * Setzt den momentan anzuzeigenden Inhalt.
     * @param table Container des anzuzeigenden Inhalts.
     */
    public void setMenuTable(MenuTable table) {
        if (currentMenuTable != null) {
            currentMenuTable.remove();
        }
        currentMenuTable = table;
        stage.addActor(currentMenuTable);
    }

    @Override
    public InputProcessor getInputProcessor() {
        return stage;
    }
}
