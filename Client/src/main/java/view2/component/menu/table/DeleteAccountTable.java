package view2.component.menu.table;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import controller.network.ServerSender;
import view2.Assets;
import view2.Chati;
import view2.component.ChatiTextField;
import view2.component.Response;

public class DeleteAccountTable extends MenuTable {

    private ChatiTextField passwordField;
    private ChatiTextField confirmPasswordField;
    private TextButton confirmButton;
    private TextButton cancelButton;

    public DeleteAccountTable() {
        create();
        setLayout();
    }

    @Override
    protected void create() {
        infoLabel.setText("Gib dein aktuelles Passwort ein!");

        passwordField = new ChatiTextField("Passwort", true);
        confirmPasswordField = new ChatiTextField("Passwort bestätigen", true);

        confirmButton = new TextButton("Bestätigen", Assets.SKIN);
        confirmButton.addListener(new ClickListener() {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if (passwordField.getText().isBlank() || confirmPasswordField.getText().isBlank()
                    || passwordField.getStyle().fontColor == Color.GRAY
                    || confirmPasswordField.getStyle().fontColor == Color.GRAY) {
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

        cancelButton = new TextButton("Abbrechen", Assets.SKIN);
        cancelButton.addListener(new ClickListener() {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                Chati.CHATI.getMenuScreen().setMenuTable(new ProfileSettingsTable());
            }
        });
    }

    @Override
    protected void setLayout() {
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

        private TextButton confirmButton;
        private TextButton cancelButton;

        public ConfirmDeletionTable() {
            create();
            setLayout();
        }

        @Override
        protected void create() {
            infoLabel.setText("Bist du sicher, dass du dein Konto löschen möchtest?");

            confirmButton = new TextButton("Bestätigen", Assets.SKIN);
            confirmButton.addListener(new InputListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    return true;
                }
                @Override
                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                    Chati.CHATI.getMenuScreen().setPendingResponse(Response.DELETE_ACCOUNT);
                    Chati.CHATI.getServerSender().send(ServerSender.SendAction.PROFILE_LOGOUT,
                            passwordField.getText(), true);
                }
            });

            cancelButton = new TextButton("Zurück", Assets.SKIN);
            cancelButton.addListener(new InputListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    return true;
                }
                @Override
                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                    Chati.CHATI.getMenuScreen().setMenuTable(new ChangePasswordTable());
                }
            });
        }

        @Override
        protected void setLayout() {
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
