package view2.userInterface.menu.table;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import controller.network.ServerSender;
import view2.Chati;
import view2.userInterface.ChatiTextButton;
import view2.userInterface.ChatiTextField;
import view2.userInterface.Response;

public class DeleteAccountTable extends MenuTable {

    private final ChatiTextField passwordField;
    private final ChatiTextField confirmPasswordField;

    public DeleteAccountTable() {
        infoLabel.setText("Gib dein aktuelles Passwort ein!");

        passwordField = new ChatiTextField("Passwort", true);
        confirmPasswordField = new ChatiTextField("Passwort bestätigen", true);

        TextButton confirmButton = new ChatiTextButton("Bestätigen", true);
        confirmButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (passwordField.isBlank() || confirmPasswordField.isBlank()) {
                    infoLabel.setText("Bitte fülle alle Felder aus.");
                    return;
                }
                if (!passwordField.getText().equals(confirmPasswordField.getText())) {
                    infoLabel.setText("Die Passwörter stimmen nicht überein.");
                    resetTextFields();
                    return;
                }
                Chati.CHATI.getMenuScreen().setMenuTable(new ConfirmDeletionTable());
            }
        });

        TextButton cancelButton = new ChatiTextButton("Abbrechen", true);
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
        container.add(confirmPasswordField).row();
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
        confirmPasswordField.reset();
    }

    private class ConfirmDeletionTable extends MenuTable {

        public ConfirmDeletionTable() {
            infoLabel.setText("Bist du sicher, dass du dein Konto löschen möchtest?");

            ChatiTextButton confirmButton = new ChatiTextButton("Bestätigen", true);
            confirmButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    Chati.CHATI.getMenuScreen().setPendingResponse(Response.DELETE_ACCOUNT);
                    Chati.CHATI.send(ServerSender.SendAction.PROFILE_LOGOUT, passwordField.getText(), true);
                }
            });

            ChatiTextButton cancelButton = new ChatiTextButton("Zurück", true);
            cancelButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    Chati.CHATI.getMenuScreen().setMenuTable(new ChangePasswordTable());
                }
            });

            // Layout
            Table container = new Table();
            container.defaults().height(ROW_HEIGHT).spaceBottom(SPACING).center().growX();
            container.add(infoLabel).row();
            Table buttonContainer = new Table();
            buttonContainer.defaults().colspan(2).height(ROW_HEIGHT).growX();
            buttonContainer.add(confirmButton).padRight(SPACING / 2);
            buttonContainer.add(cancelButton).padLeft(SPACING / 2);
            container.add(buttonContainer);
            add(container).width(ROW_WIDTH);
        }

        @Override
        public void resetTextFields() {
        }
    }
}