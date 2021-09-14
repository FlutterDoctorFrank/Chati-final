package view2.userInterface.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import controller.network.ServerSender;
import view2.Chati;
import view2.userInterface.ChatiTextButton;
import view2.userInterface.ChatiTextField;
import view2.Response;

/**
 * Eine Klasse, welche das Menü zum Anmelden repräsentiert.
 */
public class LoginTable extends MenuTable {

    private final ChatiTextField usernameField;
    private final ChatiTextField passwordField;

    /**
     * Erzeugt eine neue Instanz des LoginTable.
     */
    public LoginTable() {
        infoLabel.setText("Bitte gib dein Benutzername und dein Passwort ein!");

        usernameField = new ChatiTextField("Benutzername", false);
        passwordField = new ChatiTextField("Passwort", true);

        ChatiTextButton loginButton = new ChatiTextButton("Anmelden", true);
        loginButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (usernameField.isBlank() || passwordField.isBlank()) {
                    infoLabel.setText("Bitte fülle alle Felder aus.");
                    return;
                }
                Chati.CHATI.getMenuScreen().setPendingResponse(Response.LOGIN);
                Chati.CHATI.send(ServerSender.SendAction.PROFILE_LOGIN, usernameField.getText(), passwordField.getText(), false);
            }
        });

        ChatiTextButton registerButton = new ChatiTextButton("Registrieren", true);
        registerButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (usernameField.isBlank() || passwordField.isBlank()) {
                    infoLabel.setText("Bitte fülle alle Felder aus.");
                    return;
                }
                Chati.CHATI.getMenuScreen().setPendingResponse(Response.REGISTRATION);
                Chati.CHATI.send(ServerSender.SendAction.PROFILE_LOGIN, usernameField.getText(), passwordField.getText(), true);
            }
        });

        ChatiTextButton exitButton = new ChatiTextButton("Beenden", true);
        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });

        // Layout
        Table container = new Table();
        container.defaults().height(ROW_HEIGHT).spaceBottom(SPACING).center().growX();
        container.add(infoLabel).row();
        container.add(usernameField).row();
        container.add(passwordField).row();
        Table buttonContainer = new Table();
        buttonContainer.defaults().colspan(2).height(ROW_HEIGHT).growX();
        buttonContainer.add(loginButton).padRight(SPACING / 2);
        buttonContainer.add(registerButton).padLeft(SPACING / 2);
        container.add(buttonContainer).row();
        container.add(exitButton);
        add(container).width(ROW_WIDTH);
    }

    @Override
    public void resetTextFields() {
        usernameField.reset();
        passwordField.reset();
    }
}