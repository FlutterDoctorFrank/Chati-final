package view2.component.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
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

    private TextField passwordField;
    private TextField newPasswordField;
    private TextField confirmNewPasswordField;
    private TextButton confirmButton;
    private TextButton cancelButton;

    @Override
    protected void create() {
        infoLabel.setText("Gib dein aktuelles und ein neues Passwort ein!");

        Skin passwordFieldSkin = new Skin(Gdx.files.internal("shadeui/uiskin.json"));
        passwordFieldSkin.get(TextField.TextFieldStyle.class).font.getData().setScale(TEXTFIELD_FONT_SCALE_FACTOR);
        passwordField = new TextField("Aktuelles Passwort", passwordFieldSkin);
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

        Skin newPasswordFieldSkin = new Skin(Gdx.files.internal("shadeui/uiskin.json"));
        newPasswordFieldSkin.get(TextField.TextFieldStyle.class).font.getData().setScale(TEXTFIELD_FONT_SCALE_FACTOR);
        newPasswordField = new TextField("Neues Passwort", newPasswordFieldSkin);
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

        Skin confirmNewPasswordFieldSkin = new Skin(Gdx.files.internal("shadeui/uiskin.json"));
        confirmNewPasswordFieldSkin.get(TextField.TextFieldStyle.class).font.getData().setScale(TEXTFIELD_FONT_SCALE_FACTOR);
        confirmNewPasswordField = new TextField("Neues Passwort bestätigen", confirmNewPasswordFieldSkin);
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

        confirmButton = new TextButton("Bestätigen", Chati.SKIN);
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
                Chati.getInstance().getServerSender().send(ServerSender.SendAction.PROFILE_CHANGE,
                    passwordField.getText(), newPasswordField.getText());
                Chati.getInstance().getMenuScreen().setPendingResponse(Response.PASSWORD_CHANGE);
            }
        });

        cancelButton = new TextButton("Zurück", Chati.SKIN);
        cancelButton.addListener(new ClickListener() {
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
        Table infoContainer = new Table();
        infoContainer.add(infoLabel);
        add(infoContainer).width(ROW_WIDTH).height(ROW_HEIGHT).center().spaceBottom(HORIZONTAL_SPACING).row();

        add(passwordField).width(ROW_WIDTH).height(ROW_HEIGHT).center().spaceBottom(HORIZONTAL_SPACING).row();
        add(newPasswordField).width(ROW_WIDTH).height(ROW_HEIGHT).center().spaceBottom(HORIZONTAL_SPACING).row();
        add(confirmNewPasswordField).width(ROW_WIDTH).height(ROW_HEIGHT).center().spaceBottom(HORIZONTAL_SPACING).row();

        Table buttonContainer = new Table();
        buttonContainer.setWidth(ROW_WIDTH);
        buttonContainer.defaults().space(VERTICAL_SPACING);
        add(buttonContainer).spaceBottom(HORIZONTAL_SPACING).row();
        buttonContainer.add(confirmButton).width(ROW_WIDTH / 2f - (VERTICAL_SPACING / 2f)).height(ROW_HEIGHT);
        buttonContainer.add(cancelButton).width(ROW_WIDTH / 2f - (VERTICAL_SPACING / 2f)).height(ROW_HEIGHT);
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
