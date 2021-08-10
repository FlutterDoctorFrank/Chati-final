package view2.component.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener;
import controller.network.ServerSender;
import view2.Chati;

public class ChangePasswordTable extends MenuTable {

    private static final String NAME = "change-password-table";
    private TextField passwordField;
    private TextField newPasswordField;
    private TextField confirmNewPasswordField;
    private TextButton confirmButton;
    private TextButton cancelButton;

    protected ChangePasswordTable() {
        super(NAME);
    }

    @Override
    protected void create() {
        infoLabel.setText("Gib dein aktuelles und ein neues Passwort ein!");

        Skin passwordFieldSkin = new Skin(Gdx.files.internal("shadeui/uiskin.json"));
        passwordFieldSkin.get(TextField.TextFieldStyle.class).font.getData().setScale(1.6f);
        passwordField = new TextField("Aktuelles Passwort", passwordFieldSkin);
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

        Skin newPasswordFieldSkin = new Skin(Gdx.files.internal("shadeui/uiskin.json"));
        newPasswordFieldSkin.get(TextField.TextFieldStyle.class).font.getData().setScale(1.6f);
        newPasswordField = new TextField("Neues Passwort", newPasswordFieldSkin);
        newPasswordField.getStyle().fontColor = Color.GRAY;
        newPasswordField.setPasswordCharacter('*');
        newPasswordField.addListener(new FocusListener() {
            @Override
            public boolean handle(Event event){
                if (event.toString().equals("mouseMoved")
                        || event.toString().equals("exit")
                        || event.toString().equals("enter")
                        || event.toString().equals("keyDown")
                        || event.toString().equals("touchUp")) {
                    return false;
                }
                if (newPasswordField.getStyle().fontColor == Color.GRAY) {
                    newPasswordField.setText("");
                    newPasswordField.getStyle().fontColor = Color.BLACK;
                    newPasswordField.setPasswordMode(true);
                }
                return true;
            }
        });

        Skin confirmNewPasswordFieldSkin = new Skin(Gdx.files.internal("shadeui/uiskin.json"));
        confirmNewPasswordFieldSkin.get(TextField.TextFieldStyle.class).font.getData().setScale(1.6f);
        confirmNewPasswordField = new TextField("Neues Passwort bestätigen", confirmNewPasswordFieldSkin);
        confirmNewPasswordField.getStyle().fontColor = Color.GRAY;
        confirmNewPasswordField.setPasswordCharacter('*');
        confirmNewPasswordField.addListener(new FocusListener() {
            @Override
            public boolean handle(Event event){
                if (event.toString().equals("mouseMoved")
                        || event.toString().equals("exit")
                        || event.toString().equals("enter")
                        || event.toString().equals("keyDown")
                        || event.toString().equals("touchUp")) {
                    return false;
                }
                if (confirmNewPasswordField.getStyle().fontColor == Color.GRAY) {
                    confirmNewPasswordField.setText("");
                    confirmNewPasswordField.getStyle().fontColor = Color.BLACK;
                    confirmNewPasswordField.setPasswordMode(true);
                }
                return true;
            }
        });

        confirmButton = new TextButton("Bestätigen", SKIN);
        confirmButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if (passwordField.getText().isEmpty() || newPasswordField.getText().isEmpty()
                    || confirmNewPasswordField.getText().isEmpty()
                    || passwordField.getStyle().fontColor == Color.GRAY
                    || newPasswordField.getStyle().fontColor == Color.GRAY
                    || confirmNewPasswordField.getStyle().fontColor == Color.GRAY) {
                    infoLabel.setText("Bitte fülle alle Felder aus.");
                    return;
                }
                if (!newPasswordField.getText().equals(confirmNewPasswordField.getText())) {
                    infoLabel.setText("Die Passwörter stimmen nicht überein.");
                    return;
                }
                if (passwordField.getText().equals(newPasswordField.getText())) {
                    infoLabel.setText("Bitte gib ein neues Passwort ein!");
                    return;
                }
                Chati.getInstance().getServerSender().send(ServerSender.SendAction.PROFILE_CHANGE,
                    passwordField.getText(), newPasswordField.getText());
                Chati.getInstance().getMenuScreen().setPendingResponse(Response.PASSWORD_CHANGE);
            }
        });

        cancelButton = new TextButton("Zurück", SKIN);
        cancelButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                Chati.getInstance().getMenuScreen().setTable(new ProfileSettingsTable());
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

        add(passwordField).width(width).height(height).center().spaceBottom(horizontalSpacing).row();
        add(newPasswordField).width(width).height(height).center().spaceBottom(horizontalSpacing).row();
        add(confirmNewPasswordField).width(width).height(height).center().spaceBottom(horizontalSpacing).row();

        Table buttonContainer = new Table();
        buttonContainer.setWidth(width);
        buttonContainer.defaults().space(verticalSpacing);
        add(buttonContainer).spaceBottom(horizontalSpacing).row();
        buttonContainer.add(confirmButton).width(width / 2f - (verticalSpacing / 2f)).height(height);
        buttonContainer.add(cancelButton).width(width / 2f - (verticalSpacing / 2f)).height(height);
    }

    @Override
    public void clearTextFields() {
        passwordField.setText("");
        newPasswordField.setText("");
        confirmNewPasswordField.setText("");
    }
}
