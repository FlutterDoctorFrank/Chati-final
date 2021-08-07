package view2.component.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import controller.network.ServerSender;
import view2.Chati;

public class LoginTable extends MenuTable {

    private static final String NAME = "login-table";

    private TextField usernameField;
    private TextField passwordField;
    private TextButton registerButton;
    private TextButton loginButton;
    private TextButton exitButton;

    public LoginTable(MenuScreen menuScreen) {
        super(NAME, menuScreen);
    }

    @Override
    protected void create() {
        // Füge Info-Label hinzu.
        infoLabel = new Label("Bitte geben Sie ihr Benutzername und Passwort ein!", SKIN);
        // Füge Benutzernamen-Feld hinzu.
        usernameField = new TextField("Benutzername", SKIN);
        usernameField.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                usernameField.setText("");
            }
        });
        // Füge Passwort-Feld hinzu.
        passwordField = new TextField("Passwort", SKIN);
        passwordField.setPasswordCharacter('*');
        passwordField.setPasswordMode(true);
        passwordField.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                passwordField.setText("");
            }
        });
        // Füge Login-Button hinzu.
        loginButton = new TextButton("Anmelden", SKIN);
        loginButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                Chati.getInstance().getServerSender().send(ServerSender.SendAction.PROFILE_LOGIN,
                        usernameField.getText(), passwordField.getText(), false);
            }
        });
        // Füge Register-Button hinzu.
        registerButton = new TextButton("Registrieren", SKIN);
        registerButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if (usernameField.getText().isEmpty() || passwordField.getText().isEmpty()) {
                    return;
                }
                Chati.getInstance().getServerSender().send(ServerSender.SendAction.PROFILE_LOGIN,
                        usernameField.getText(), passwordField.getText(), true);
            }
        });
        // Füge Exit-Button hinzu.
        exitButton = new TextButton("Beenden", SKIN);
        exitButton.addListener(new InputListener() {
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

    }
}