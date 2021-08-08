package view2.component.menu;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import controller.network.ServerSender;
import view2.Chati;

public class DeleteAccountTable extends MenuTable {

    private static final String NAME = "delete-account-table";
    private TextField passwordField;
    private TextField confirmPasswordField;
    private TextButton confirmButton;
    private TextButton cancelButton;

    protected DeleteAccountTable() {
        super(NAME);
    }

    @Override
    protected void create() {
        infoLabel = new Label("Gib dein Passwort ein.", SKIN);

        passwordField = new TextField("Password", SKIN);
        passwordField.setPasswordMode(true);
        passwordField.setPasswordCharacter('*');
        passwordField.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                passwordField.setText("");
            }
        });

        confirmPasswordField = new TextField("Passwort bestätigen", SKIN);
        confirmPasswordField.setPasswordMode(true);
        confirmPasswordField.setPasswordCharacter('*');
        confirmPasswordField.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                confirmPasswordField.setText("");
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
                if (passwordField.getText().isEmpty() || confirmPasswordField.getText().isEmpty()) {
                    infoLabel.setText("Bitte gib dein Passwort in beide Felder ein.");
                    return;
                }
                if (!passwordField.getText().equals(confirmPasswordField.getText())) {
                    infoLabel.setText("Die Passwörter stimmen nicht überein.");
                    return;
                }
                Chati.getInstance().getServerSender().send(ServerSender.SendAction.PROFILE_CHANGE,
                        passwordField.getText(), false /* ? */);
                Chati.getInstance().getMenuScreen().setPendingResponse(Response.DELETE_ACCOUNT);
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
