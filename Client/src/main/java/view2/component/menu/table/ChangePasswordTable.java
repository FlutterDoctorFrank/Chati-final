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
import view2.component.menu.MenuResponse;
import view2.component.menu.MenuScreen;

public class ChangePasswordTable extends MenuTable {

    private TextField passwordField;
    private TextField newPasswordField;
    private TextField confirmNewPasswordField;
    private TextButton confirmButton;
    private TextButton cancelButton;

    @Override
    protected void create() {
        infoLabel.setText("Gib dein aktuelles und ein neues Passwort ein!");

        passwordField = new TextField("Aktuelles Passwort", Assets.getNewSkin());
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

        newPasswordField = new TextField("Neues Passwort", Assets.getNewSkin());
        newPasswordField.getStyle().fontColor = Color.GRAY;
        newPasswordField.setPasswordCharacter('*');
        newPasswordField.addListener(new FocusListener() {
            @Override
            public void keyboardFocusChanged(FocusListener.FocusEvent event, Actor actor, boolean focused) {
                if(focused && newPasswordField.getStyle().fontColor == Color.GRAY) {
                    newPasswordField.getStyle().fontColor = Color.BLACK;
                    newPasswordField.setText("");
                    newPasswordField.setPasswordMode(true);
                }
                else if (newPasswordField.getText().isEmpty()) {
                    resetNewPasswordField();
                }
            }
        });

        confirmNewPasswordField = new TextField("Neues Passwort bestätigen", Assets.getNewSkin());
        confirmNewPasswordField.getStyle().fontColor = Color.GRAY;
        confirmNewPasswordField.setPasswordCharacter('*');
        confirmNewPasswordField.addListener(new FocusListener() {
            @Override
            public void keyboardFocusChanged(FocusListener.FocusEvent event, Actor actor, boolean focused) {
                if(focused && confirmNewPasswordField.getStyle().fontColor == Color.GRAY) {
                    confirmNewPasswordField.getStyle().fontColor = Color.BLACK;
                    confirmNewPasswordField.setText("");
                    confirmNewPasswordField.setPasswordMode(true);
                }
                else if (confirmNewPasswordField.getText().isEmpty()) {
                    resetConfirmNewPasswordField();
                }
            }
        });

        confirmButton = new TextButton("Bestätigen", Assets.SKIN);
        confirmButton.addListener(new ClickListener() {
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
                    infoLabel.setText("Die neuen Passwörter stimmen nicht überein.");
                    resetNewPasswordField();
                    resetConfirmNewPasswordField();
                    return;
                }
                if (passwordField.getText().equals(newPasswordField.getText())) {
                    infoLabel.setText("Bitte gib ein neues Passwort ein!");
                    resetNewPasswordField();
                    resetConfirmNewPasswordField();
                    return;
                }
                MenuScreen.getInstance().setPendingResponse(MenuResponse.PASSWORD_CHANGE);
                Chati.getInstance().getServerSender().send(ServerSender.SendAction.PROFILE_CHANGE,
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
                MenuScreen.getInstance().setMenuTable(new ProfileSettingsTable());
            }
        });
    }

    @Override
    protected void setLayout() {
        Table container = new Table();
        container.defaults().height(ROW_HEIGHT).spaceBottom(SPACING).center().fillX().expandX();
        container.add(infoLabel).row();
        container.add(passwordField).row();
        container.add(newPasswordField).row();
        container.add(confirmNewPasswordField).row();
        Table buttonContainer = new Table();
        buttonContainer.defaults().colspan(2).height(ROW_HEIGHT).fillX().expandX();
        buttonContainer.add(confirmButton).spaceRight(SPACING);
        buttonContainer.add(cancelButton);
        container.add(buttonContainer);
        add(container).width(ROW_WIDTH);
    }

    @Override
    public void resetTextFields() {
        resetPasswordField();
        resetNewPasswordField();
        resetConfirmNewPasswordField();
    }

    private void resetPasswordField() {
        passwordField.getStyle().fontColor = Color.GRAY;
        passwordField.setText("Passwort");
        passwordField.setPasswordMode(false);
        getStage().unfocus(passwordField);
    }

    private void resetNewPasswordField() {
        newPasswordField.getStyle().fontColor = Color.GRAY;
        newPasswordField.setText("Neues Passwort");
        newPasswordField.setPasswordMode(false);
        getStage().unfocus(newPasswordField);
    }

    private void resetConfirmNewPasswordField() {
        confirmNewPasswordField.getStyle().fontColor = Color.GRAY;
        confirmNewPasswordField.setText("Neues Passwort bestätigen");
        confirmNewPasswordField.setPasswordMode(false);
        getStage().unfocus(confirmNewPasswordField);
    }
}
