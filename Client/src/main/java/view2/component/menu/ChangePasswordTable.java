package view2.component.menu;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
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
        infoLabel = new Label("Gib dein aktuelles und ein neues Passwort ein", SKIN);

        passwordField = new TextField("Aktuelles Passwort", SKIN);
        passwordField.setPasswordMode(true);
        passwordField.setPasswordCharacter('*');
        passwordField.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                passwordField.setText("");
            }
        });

        newPasswordField = new TextField("Neues Passwort", SKIN);
        newPasswordField.setPasswordMode(true);
        newPasswordField.setPasswordCharacter('*');
        newPasswordField.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                newPasswordField.setText("");
            }
        });

        confirmNewPasswordField = new TextField("Neues Passwort bestätigen", SKIN);
        confirmNewPasswordField.setPasswordMode(true);
        confirmNewPasswordField.setPasswordCharacter('*');
        confirmNewPasswordField.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                confirmNewPasswordField.setText("");
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
                    || confirmNewPasswordField.getText().isEmpty()) {
                    infoLabel.setText("Bitte fülle alle Felder aus.");
                    return;
                }
                if (!newPasswordField.getText().equals(confirmNewPasswordField.getText())) {
                    infoLabel.setText("Die Passwörter stimmen nicht überein.");
                    return;
                }
                Chati.getInstance().getServerSender().send(ServerSender.SendAction.PROFILE_CHANGE,
                    passwordField.getText(), newPasswordField.getText(), false /* ? */);
                Chati.getInstance().getMenuScreen().setPendingResponse(Response.PASSWORD_CHANGE);
            }
        });

        cancelButton = new TextButton("Abbrechen", SKIN);
        cancelButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                Chati.getInstance().getMenuScreen().setTable(new StartTable());
            }
        });
    }

    @Override
    protected void setLayout() {

    }
}
