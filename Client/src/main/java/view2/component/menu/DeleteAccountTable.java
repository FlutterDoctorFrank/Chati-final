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

    protected DeleteAccountTable(MenuScreen menuScreen) {
        super(NAME, menuScreen);
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
                String password = passwordField.getText();
                String confirmPassword = confirmPasswordField.getText();
                if (password.equals(confirmPassword)) {
                    Chati.getInstance().getServerSender().send(ServerSender.SendAction.PROFILE_CHANGE,
                            password, false /* ? */);
                } else {
                    infoLabel.setText("Die Passwörter stimmen nicht überein.");
                }
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
                menuScreen.setTable(new StartTable(menuScreen));
            }
        });
    }

    @Override
    protected void setLayout() {

    }
}
