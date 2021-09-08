package view2.userInterface.menu.table;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import controller.network.ServerSender;
import view2.Chati;
import view2.userInterface.ChatiTextButton;
import view2.userInterface.ChatiTextField;
import view2.Response;

public class ChangePasswordTable extends MenuTable {

    private final ChatiTextField passwordField;
    private final ChatiTextField newPasswordField;
    private final ChatiTextField confirmNewPasswordField;

    public ChangePasswordTable() {
        infoLabel.setText("Gib dein aktuelles und ein neues Passwort ein!");

        passwordField = new ChatiTextField("Aktuelles Passwort", true);
        newPasswordField = new ChatiTextField("Neues Passwort", true);
        confirmNewPasswordField = new ChatiTextField("Neues Passwort bestätigen", true);

        ChatiTextButton confirmButton = new ChatiTextButton("Bestätigen", true);
        confirmButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (passwordField.isBlank() || newPasswordField.isBlank()
                        || confirmNewPasswordField.isBlank()) {
                    infoLabel.setText("Bitte fülle alle Felder aus.");
                    return;
                }
                if (!newPasswordField.getText().equals(confirmNewPasswordField.getText())) {
                    infoLabel.setText("Die neuen Passwörter stimmen nicht überein.");
                    newPasswordField.reset();
                    confirmNewPasswordField.reset();
                    return;
                }
                if (passwordField.getText().equals(newPasswordField.getText())) {
                    infoLabel.setText("Bitte gib ein neues Passwort ein!");
                    newPasswordField.reset();
                    confirmNewPasswordField.reset();
                    return;
                }
                Chati.CHATI.getMenuScreen().setPendingResponse(Response.PASSWORD_CHANGE);
                Chati.CHATI.send(ServerSender.SendAction.PROFILE_CHANGE, passwordField.getText(), newPasswordField.getText());
            }
        });

        ChatiTextButton cancelButton = new ChatiTextButton("Zurück", true);
        cancelButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Chati.CHATI.getMenuScreen().setMenuTable(new ProfileSettingsTable());
            }
        });

        // Layout
        Table container = new Table();
        container.defaults().height(ROW_HEIGHT).spaceBottom(SPACING).center().growX();
        container.add(infoLabel).row();
        container.add(passwordField).row();
        container.add(newPasswordField).row();
        container.add(confirmNewPasswordField).row();
        Table buttonContainer = new Table();
        buttonContainer.defaults().colspan(2).height(ROW_HEIGHT).growX();
        buttonContainer.add(confirmButton).padRight(SPACING / 2);
        buttonContainer.add(cancelButton).padLeft(SPACING / 2);
        container.add(buttonContainer);
        add(container).width(ROW_WIDTH);
    }

    @Override
    public void resetTextFields() {
        passwordField.reset();
        newPasswordField.reset();
        confirmNewPasswordField.reset();
    }
}
