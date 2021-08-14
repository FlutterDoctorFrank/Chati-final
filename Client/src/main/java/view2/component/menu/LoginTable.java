package view2.component.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener;
import controller.network.ServerSender;
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
        Skin usernameFieldSkin = new Skin(Gdx.files.internal("shadeui/uiskin.json"));
        usernameFieldSkin.get(TextField.TextFieldStyle.class).font.getData().setScale(TEXTFIELD_FONT_SCALE_FACTOR);
        usernameField = new TextField("Benutzername", usernameFieldSkin);
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
        Skin passwordFieldSkin = new Skin(Gdx.files.internal("shadeui/uiskin.json"));
        passwordFieldSkin.get(TextField.TextFieldStyle.class).font.getData().setScale(TEXTFIELD_FONT_SCALE_FACTOR);
        passwordField = new TextField("Passwort", passwordFieldSkin);
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
        loginButton = new TextButton("Anmelden", Chati.SKIN);
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
                Chati.getInstance().getServerSender().send(ServerSender.SendAction.PROFILE_LOGIN,
                        usernameField.getText(), passwordField.getText(), false);
                Chati.getInstance().getMenuScreen().setPendingResponse(Response.LOGIN);
            }
        });
        // Füge Register-Button hinzu.
        registerButton = new TextButton("Registrieren", Chati.SKIN);
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
                Chati.getInstance().getServerSender().send(ServerSender.SendAction.PROFILE_LOGIN,
                        usernameField.getText(), passwordField.getText(), true);
                Chati.getInstance().getMenuScreen().setPendingResponse(Response.REGISTRATION);
            }
        });
        // Füge Exit-Button hinzu.
        exitButton = new TextButton("Beenden", Chati.SKIN);
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
        Table infoContainer = new Table();
        infoContainer.add(infoLabel);
        add(infoContainer).width(ROW_WIDTH).height(ROW_HEIGHT).center().spaceBottom(HORIZONTAL_SPACING).row();

        add(usernameField).width(ROW_WIDTH).height(ROW_HEIGHT).center().spaceBottom(HORIZONTAL_SPACING).row();
        add(passwordField).width(ROW_WIDTH).height(ROW_HEIGHT).center().spaceBottom(HORIZONTAL_SPACING).row();

        Table buttonContainer = new Table();
        buttonContainer.setWidth(ROW_WIDTH);
        buttonContainer.defaults().space(VERTICAL_SPACING);
        add(buttonContainer).spaceBottom(HORIZONTAL_SPACING).row();

        buttonContainer.add(loginButton).width(ROW_WIDTH / 2f - (VERTICAL_SPACING / 2f)).height(ROW_HEIGHT);
        buttonContainer.add(registerButton).width(ROW_WIDTH / 2f - (VERTICAL_SPACING / 2f)).height(ROW_HEIGHT);
        add(exitButton).width(ROW_WIDTH).center().height(ROW_HEIGHT);
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