package view2.component.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener;
import controller.network.ServerSender;
import view2.Chati;

public class LoginTable extends MenuTable {

    private static final String NAME = "login-table";

    private TextField usernameField;
    private TextField passwordField;
    private TextButton registerButton;
    private TextButton loginButton;
    private TextButton exitButton;

    public LoginTable() {
        super(NAME);
    }

    @Override
    protected void create() {
        // Füge Info-Label hinzu
        infoLabel.setText("Bitte gib dein Benutzername und dein Passwort ein!");
        // Füge Benutzernamen-Feld hinzu.
        Skin usernameFieldSkin = new Skin(Gdx.files.internal("shadeui/uiskin.json"));
        usernameFieldSkin.get(TextField.TextFieldStyle.class).font.getData().setScale(1.6f);
        usernameField = new TextField("Benutzername", usernameFieldSkin);
        usernameField.getStyle().fontColor = Color.GRAY;
        usernameField.addListener(new FocusListener() {
            @Override
            public boolean handle(Event event){
                if (event.toString().equals("mouseMoved")
                        || event.toString().equals("exit")
                        || event.toString().equals("enter")
                        || event.toString().equals("keyDown")
                        || event.toString().equals("touchUp")) {
                    return false;
                }
                if (usernameField.getStyle().fontColor == Color.GRAY) {
                    usernameField.setText("");
                    usernameField.getStyle().fontColor = Color.BLACK;
                }
                return true;
            }
        });
        // Füge Passwort-Feld hinzu.
        Skin passwordFieldSkin = new Skin(Gdx.files.internal("shadeui/uiskin.json"));
        passwordFieldSkin.get(TextField.TextFieldStyle.class).font.getData().setScale(1.6f);
        passwordField = new TextField("Passwort", passwordFieldSkin);
        passwordField.getStyle().fontColor = Color.GRAY;
        passwordField.setPasswordCharacter('*');
        passwordField.addListener(new FocusListener() {
            @Override
            public boolean handle(Event event){
                if (event.toString().equals("mouseMoved")
                        || event.toString().equals("exit")
                        || event.toString().equals("enter")
                        || event.toString().equals("keyDown")
                        || event.toString().equals("touchUp")) {
                    return false;
                }
                if (passwordField.getStyle().fontColor == Color.GRAY) {
                    passwordField.setText("");
                    passwordField.getStyle().fontColor = Color.BLACK;
                    passwordField.setPasswordMode(true);
                }
                return true;
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
                if (usernameField.getText().isEmpty() || passwordField.getText().isEmpty()
                        || usernameField.getStyle().fontColor == Color.GRAY
                        || passwordField.getStyle().fontColor == Color.GRAY) {
                    infoLabel.setText("Bitte fülle alle Felder aus.");
                    return;
                }
                Chati.getInstance().getServerSender().send(ServerSender.SendAction.PROFILE_LOGIN,
                        usernameField.getText(), passwordField.getText(), false);
                Chati.getInstance().getMenuScreen().setPendingResponse(Response.LOGIN);
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
                if (usernameField.getText().isEmpty() || passwordField.getText().isEmpty()
                        || usernameField.getStyle().fontColor == Color.GRAY
                        || passwordField.getStyle().fontColor == Color.GRAY) {
                    infoLabel.setText("Bitte fülle alle Felder aus.");
                    return;
                }
                Chati.getInstance().getServerSender().send(ServerSender.SendAction.PROFILE_LOGIN,
                        usernameField.getText(), passwordField.getText(), true);
                Chati.getInstance().getMenuScreen().setPendingResponse(Response.REGISTRATION);
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
        int width = 600;
        int height = 60;
        int verticalSpacing = 15;
        int horizontalSpacing = 15;

        Table infoContainer = new Table();
        infoContainer.add(infoLabel);
        add(infoContainer).width(width).height(height).center().spaceBottom(horizontalSpacing).row();

        add(usernameField).width(width).height(height).center().spaceBottom(horizontalSpacing).row();
        add(passwordField).width(width).height(height).center().spaceBottom(horizontalSpacing).row();

        Table buttonContainer = new Table();
        buttonContainer.setWidth(width);
        buttonContainer.defaults().space(verticalSpacing);
        add(buttonContainer).spaceBottom(horizontalSpacing).row();

        buttonContainer.add(loginButton).width(width / 2f - (verticalSpacing / 2f)).height(height);
        buttonContainer.add(registerButton).width(width / 2f - (verticalSpacing / 2f)).height(height);
        add(exitButton).width(width).center().height(height);
    }

    @Override
    public void clearTextFields() {
        usernameField.setText("");
        passwordField.setText("");
    }
}