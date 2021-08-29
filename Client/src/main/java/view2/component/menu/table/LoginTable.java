package view2.component.menu.table;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener;
import controller.network.ServerSender;
import view2.Assets;
import view2.Chati;
import view2.component.ChatiTextField;
import view2.component.Response;

public class LoginTable extends MenuTable {

    private ChatiTextField usernameField;
    private ChatiTextField passwordField;
    private TextButton registerButton;
    private TextButton loginButton;
    private TextButton exitButton;

    @Override
    protected void create() {
        infoLabel.setText("Bitte gib dein Benutzername und dein Passwort ein!");

        usernameField = new ChatiTextField("Benutzername", false);
        passwordField = new ChatiTextField("Passwort", true);

        loginButton = new TextButton("Anmelden", Assets.SKIN);
        loginButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if (usernameField.getText().isBlank() || passwordField.getText().isBlank()
                        || usernameField.getStyle().fontColor == Color.GRAY
                        || passwordField.getStyle().fontColor == Color.GRAY) {
                    infoLabel.setText("Bitte fülle alle Felder aus.");
                    return;
                }
                Chati.CHATI.getMenuScreen().setPendingResponse(Response.LOGIN);
                Chati.CHATI.getServerSender().send(ServerSender.SendAction.PROFILE_LOGIN,
                        usernameField.getText(), passwordField.getText(), false);
            }
        });

        registerButton = new TextButton("Registrieren", Assets.SKIN);
        registerButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if (usernameField.getText().isBlank() || passwordField.getText().isBlank()
                        || usernameField.getStyle().fontColor == Color.GRAY
                        || passwordField.getStyle().fontColor == Color.GRAY) {
                    infoLabel.setText("Bitte fülle alle Felder aus.");
                    return;
                }
                Chati.CHATI.getMenuScreen().setPendingResponse(Response.REGISTRATION);
                Chati.CHATI.getServerSender().send(ServerSender.SendAction.PROFILE_LOGIN,
                        usernameField.getText(), passwordField.getText(), true);
            }
        });

        exitButton = new TextButton("Beenden", Assets.SKIN);
        exitButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                Gdx.app.exit();
                System.exit(1);
            }
        });
    }

    @Override
    protected void setLayout() {
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