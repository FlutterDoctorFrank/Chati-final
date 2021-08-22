package view2.component.menu;

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

public class LoginTable extends MenuTable {

    private TextField usernameField;
    private TextField passwordField;
    private TextButton registerButton;
    private TextButton loginButton;
    private TextButton exitButton;

    @Override
    protected void create() {
        infoLabel.setText("Bitte gib dein Benutzername und dein Passwort ein!");
        // Füge Benutzernamen-Feld hinzu.
        usernameField = new TextField("Benutzername", Assets.getNewSkin());
        usernameField.getStyle().fontColor = Color.GRAY;
        usernameField.addListener(new FocusListener() {
            @Override
            public void keyboardFocusChanged(FocusListener.FocusEvent event, Actor actor, boolean focused) {
                if (focused && usernameField.getStyle().fontColor == Color.GRAY) {
                    usernameField.setText("");
                    usernameField.getStyle().fontColor = Color.BLACK;
                }
                else if (usernameField.getText().isEmpty()) {
                    resetUsernameField();
                }
            }
        });
        // Füge Passwort-Feld hinzu.
        passwordField = new TextField("Passwort", Assets.getNewSkin());
        passwordField.getStyle().fontColor = Color.GRAY;
        passwordField.setPasswordCharacter('*');
        passwordField.addListener(new FocusListener() {
            @Override
            public void keyboardFocusChanged(FocusListener.FocusEvent event, Actor actor, boolean focused) {
                if(focused && passwordField.getStyle().fontColor == Color.GRAY) {
                    passwordField.getStyle().fontColor = Color.BLACK;
                    passwordField.setText("");
                    passwordField.setPasswordMode(true);
                }
                else if (passwordField.getText().isEmpty()) {
                    resetPasswordField();
                }
            }
        });
        // Füge Login-Button hinzu.
        loginButton = new TextButton("Anmelden", Assets.SKIN);
        loginButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if (usernameField.getText().isEmpty() || passwordField.getText().isEmpty()
                        || usernameField.getStyle().fontColor == Color.GRAY
                        || passwordField.getStyle().fontColor == Color.GRAY) {
                    infoLabel.setText("Bitte fülle alle Felder aus.");
                    return;
                }
                MenuScreen.getInstance().setPendingResponse(MenuResponse.LOGIN);
                Chati.getInstance().getServerSender().send(ServerSender.SendAction.PROFILE_LOGIN,
                        usernameField.getText(), passwordField.getText(), false);
            }
        });
        // Füge Register-Button hinzu.
        registerButton = new TextButton("Registrieren", Assets.SKIN);
        registerButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if (usernameField.getText().isEmpty() || passwordField.getText().isEmpty()
                        || usernameField.getStyle().fontColor == Color.GRAY
                        || passwordField.getStyle().fontColor == Color.GRAY) {
                    infoLabel.setText("Bitte fülle alle Felder aus.");
                    return;
                }
                MenuScreen.getInstance().setPendingResponse(MenuResponse.REGISTRATION);
                Chati.getInstance().getServerSender().send(ServerSender.SendAction.PROFILE_LOGIN,
                        usernameField.getText(), passwordField.getText(), true);
            }
        });
        // Füge Exit-Button hinzu.
        exitButton = new TextButton("Beenden", Assets.SKIN);
        exitButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                Gdx.app.exit();
            }
        });
    }

    @Override
    protected void setLayout() {
        Table container = new Table();
        container.defaults().height(ROW_HEIGHT).spaceBottom(SPACING).center().fillX().expandX();
        container.add(infoLabel).row();
        container.add(usernameField).row();
        container.add(passwordField).row();
        Table buttonContainer = new Table();
        buttonContainer.defaults().height(ROW_HEIGHT).fillX().expandX();
        buttonContainer.add(loginButton).spaceRight(SPACING);
        buttonContainer.add(registerButton);
        container.add(buttonContainer).row();
        container.add(exitButton);
        add(container).width(ROW_WIDTH);
    }

    @Override
    public void resetTextFields() {
        resetUsernameField();
        resetPasswordField();
    }

    private void resetUsernameField() {
        usernameField.getStyle().fontColor = Color.GRAY;
        usernameField.setText("Benutzername");
        getStage().unfocus(usernameField);
    }

    private void resetPasswordField() {
        passwordField.getStyle().fontColor = Color.GRAY;
        passwordField.setText("Passwort");
        passwordField.setPasswordMode(false);
        getStage().unfocus(passwordField);
    }
}