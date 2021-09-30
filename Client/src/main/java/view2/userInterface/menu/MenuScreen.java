package view2.userInterface.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import model.MessageBundle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import view2.Chati;
import view2.ChatiScreen;
import view2.Response;
import view2.userInterface.ChatiTable;

/**
 * Eine Klasse, welche den Menübildschirm der Anwendung repräsentiert.
 */
public class MenuScreen extends ChatiScreen {

    private ChatiTable currentMenuTable;

    /**
     * Erzeugt eine neue Instanz des MenuScreen.
     */
    public MenuScreen() {
        setMenuTable(new LoginTable());
    }

    @Override
    public void render(final float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        super.render(delta);
    }

    /**
     * Verarbeitet die Antwort auf eine durchgeführte Registrierung.
     * @param success Information, ob die Registrierung erfolgreich war.
     * @param messageBundle Kennung der anzuzeigenden Nachricht.
     */
    public void registrationResponse(final boolean success, @Nullable final MessageBundle messageBundle) {
        if (pendingResponse != Response.REGISTRATION) {
            return;
        }
        setPendingResponse(Response.NONE);
        if (success) {
            currentMenuTable.showMessage("account.register.success");
        } else {
            if (messageBundle != null) {
                currentMenuTable.showMessage(messageBundle);
            } else {
                currentMenuTable.showMessage("account.register.failed");
            }
            currentMenuTable.resetTextFields();
        }
    }

    /**
     * Verarbeitet die Antwort auf eine durchgeführte Anmeldung.
     * @param success Information, ob die Anmeldung erfolgreich war.
     * @param messageBundle Kennung der anzuzeigenden Nachricht.
     */
    public void loginResponse(final boolean success, @Nullable final MessageBundle messageBundle) {
        if (pendingResponse != Response.LOGIN) {
            return;
        }
        setPendingResponse(Response.NONE);
        if (success) {
            setMenuTable(new StartTable());
        } else {
            if (messageBundle != null) {
                currentMenuTable.showMessage(messageBundle);
            } else {
                currentMenuTable.showMessage("account.login.failed");
            }
            currentMenuTable.resetTextFields();
        }
    }

    /**
     * Verarbeitet die Antwort auf eine durchgeführte Passwortänderung.
     * @param success Information, ob die Passwortänderung erfolgreich war.
     * @param messageBundle Kennung der anzuzeigenden Nachricht.
     */
    public void passwordChangeResponse(final boolean success, @Nullable final MessageBundle messageBundle) {
        if (pendingResponse != Response.PASSWORD_CHANGE) {
            return;
        }
        setPendingResponse(Response.NONE);
        if (success) {
            setMenuTable(new StartTable());
            currentMenuTable.showMessage("account.change-password.success");
        } else {
            if (messageBundle != null) {
                currentMenuTable.showMessage(messageBundle);
            } else {
                currentMenuTable.showMessage("account.change-password.failed");
            }
            currentMenuTable.resetTextFields();
        }
    }

    /**
     * Verarbeitet die Antwort auf eine durchgeführte Kontolöschung.
     * @param success Information, ob die Kontolöschung erfolgreich war.
     * @param messageBundle Kennung der anzuzeigenden Nachricht.
     */
    public void deleteAccountResponse(final boolean success, @Nullable final MessageBundle messageBundle) {
        if (pendingResponse != Response.DELETE_ACCOUNT) {
            return;
        }
        setPendingResponse(Response.NONE);
        if (success) {
            setMenuTable(new LoginTable());
            currentMenuTable.showMessage("account.delete.success");
        } else {
            if (messageBundle != null) {
                currentMenuTable.showMessage(messageBundle);
            } else {
                currentMenuTable.showMessage("account.delete.failed");
            }
            currentMenuTable.resetTextFields();
        }
    }

    /**
     * Verarbeitet die Antwort auf eine durchgeführte Avataränderung.
     * @param success Information, ob die Avataränderung erfolgreich war.
     * @param messageBundle Kennung der anzuzeigenden Nachricht.
     */
    public void avatarChangeResponse(final boolean success, @Nullable final MessageBundle messageBundle) {
        if (pendingResponse != Response.AVATAR_CHANGE) {
            return;
        }
        setPendingResponse(Response.NONE);
        if (success) {
            setMenuTable(new StartTable());
            currentMenuTable.showMessage("account.change-avatar.success");
        } else {
            if (messageBundle != null) {
                currentMenuTable.showMessage(messageBundle);
            } else {
                currentMenuTable.showMessage("account.change-avatar.failed");
            }
        }
    }

    /**
     * Verarbeitet die Antwort auf eine durchgeführte Welterstellung.
     * @param success Information, ob die Welterstellung erfolgreich war.
     * @param messageBundle Kennung der anzuzeigenden Nachricht.
     */
    public void createWorldResponse(final boolean success, @Nullable final MessageBundle messageBundle) {
        if (pendingResponse != Response.CREATE_WORLD) {
            return;
        }
        setPendingResponse(Response.NONE);
        if (success) {
            setMenuTable(new StartTable());
            currentMenuTable.showMessage("action.world-create.success");
        } else {
            if (messageBundle != null) {
                currentMenuTable.showMessage(messageBundle);
            } else {
                currentMenuTable.showMessage("action.world-create.failed");
            }
            currentMenuTable.resetTextFields();
        }
    }

    /**
     * Verarbeitet die Antwort auf eine durchgeführte Weltlöschung.
     * @param success Information, ob die Weltlöschung erfolgreich war.
     * @param messageBundle Kennung der anzuzeigenden Nachricht.
     */
    public void deleteWorldResponse(final boolean success, @Nullable final MessageBundle messageBundle) {
        if (pendingResponse != Response.DELETE_WORLD) {
            return;
        }
        setPendingResponse(Response.NONE);
        if (success) {
            currentMenuTable.showMessage("action.world-delete.success");
        } else {
            if (messageBundle != null) {
                currentMenuTable.showMessage(messageBundle);
            } else {
                currentMenuTable.showMessage("action.world-delete.failed");
            }
        }
    }

    /**
     * verarbeitet die Antwort auf einen durchgeführten Weltbeitritt.
     * @param success Information, ob der Weltbeitritt erfolgreich war.
     * @param messageBundle Kennung der anzuzeigenden Nachricht.
     */
    public void joinWorldResponse(final boolean success, @Nullable final MessageBundle messageBundle) {
        if (pendingResponse != Response.JOIN_WORLD) {
            return;
        }
        setPendingResponse(Response.NONE);
        if (success) {
            Chati.CHATI.setScreen(Chati.CHATI.getWorldScreen());
        } else {
            if (messageBundle != null) {
                currentMenuTable.showMessage(messageBundle);
            } else {
                currentMenuTable.showMessage("action.world-join.failed");
            }
        }
    }

    /**
     * Setzt den momentan anzuzeigenden Inhalt.
     * @param table Container des anzuzeigenden Inhalts.
     */
    public void setMenuTable(@NotNull final ChatiTable table) {
        if (currentMenuTable != null) {
            currentMenuTable.remove();
        }
        currentMenuTable = table;
        stage.addActor(currentMenuTable);
    }

    @Override
    public @NotNull InputProcessor getInputProcessor() {
        return stage;
    }

    @Override
    public void translate() {
        currentMenuTable.translate();
    }
}
