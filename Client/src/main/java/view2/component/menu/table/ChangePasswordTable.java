package view2.component.menu.table;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener;
import controller.network.ServerSender;
import view2.Assets;
import view2.Chati;
import view2.component.ChatiTextField;
import view2.component.Response;

public class ChangePasswordTable extends MenuTable {

    private ChatiTextField passwordField;
    private ChatiTextField newPasswordField;
    private ChatiTextField confirmNewPasswordField;
    private TextButton confirmButton;
    private TextButton cancelButton;

    public ChangePasswordTable() {
        create();
        setLayout();
    }

    @Override
    protected void create() {
        infoLabel.setText("Gib dein aktuelles und ein neues Passwort ein!");

        passwordField = new ChatiTextField("Aktuelles Passwort", true);
        newPasswordField = new ChatiTextField("Neues Passwort", true);
        confirmNewPasswordField = new ChatiTextField("Neues Passwort bestätigen", true);

        confirmButton = new TextButton("Bestätigen", Assets.SKIN);
        confirmButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if (passwordField.getText().isBlank() || newPasswordField.getText().isBlank()
                    || confirmNewPasswordField.getText().isBlank()
                    || passwordField.getStyle().fontColor == Color.GRAY
                    || newPasswordField.getStyle().fontColor == Color.GRAY
                    || confirmNewPasswordField.getStyle().fontColor == Color.GRAY) {
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
                Chati.CHATI.getServerSender().send(ServerSender.SendAction.PROFILE_CHANGE,
                    passwordField.getText(), newPasswordField.getText());
            }
        });

        cancelButton = new TextButton("Zurück", Assets.SKIN);
        cancelButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
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
        container.add(newPasswordField).row();
        container.add(confirmNewPasswordField).row();
        Table buttonContainer = new Table();
        buttonContainer.defaults().colspan(2).height(ROW_HEIGHT).growX();
        buttonContainer.add(confirmButton).spaceRight(SPACING);
        buttonContainer.add(cancelButton);
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
