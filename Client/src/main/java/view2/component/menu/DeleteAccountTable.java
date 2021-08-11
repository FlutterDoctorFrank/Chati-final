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
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener;
import controller.network.ServerSender;
import view2.Chati;

public class DeleteAccountTable extends MenuTable {

    private TextField passwordField;
    private TextField confirmPasswordField;
    private TextButton confirmButton;
    private TextButton cancelButton;

    @Override
    protected void create() {
        super.create();
        infoLabel.setText("Gib dein aktuelles Passwort ein!");

        Skin passwordFieldSkin = new Skin(Gdx.files.internal("shadeui/uiskin.json"));
        passwordFieldSkin.get(TextField.TextFieldStyle.class).font.getData().setScale(TEXTFIELD_FONT_SCALE_FACTOR);
        passwordField = new TextField("Passwort", passwordFieldSkin);
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

        Skin confirmPasswordFieldSkin = new Skin(Gdx.files.internal("shadeui/uiskin.json"));
        confirmPasswordFieldSkin.get(TextField.TextFieldStyle.class).font.getData().setScale(TEXTFIELD_FONT_SCALE_FACTOR);
        confirmPasswordField = new TextField("Neues Passwort", confirmPasswordFieldSkin);
        confirmPasswordField.getStyle().fontColor = Color.GRAY;
        confirmPasswordField.setPasswordCharacter('*');
        confirmPasswordField.addListener(new FocusListener() {
            @Override
            public void keyboardFocusChanged(FocusListener.FocusEvent event, Actor actor, boolean focused) {
                if(focused && confirmPasswordField.getStyle().fontColor == Color.GRAY) {
                    confirmPasswordField.getStyle().fontColor = Color.BLACK;
                    confirmPasswordField.setText("");
                    confirmPasswordField.setPasswordMode(true);
                }
                else if (confirmPasswordField.getText().isEmpty()) {
                    resetPasswordField();
                }
            }
        });

        confirmButton = new TextButton("Bestätigen", Chati.SKIN);
        confirmButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if (passwordField.getText().isEmpty() || confirmPasswordField.getText().isEmpty()
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
                Chati.getInstance().getMenuScreen().setTable(new ConfirmDeletionTable());
            }
        });

        cancelButton = new TextButton("Abbrechen", Chati.SKIN);
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
        Table infoContainer = new Table();
        infoContainer.add(infoLabel);
        add(infoContainer).width(ROW_WIDTH).height(ROW_HEIGHT).center().spaceBottom(HORIZONTAL_SPACING).row();

        add(passwordField).width(ROW_WIDTH).height(ROW_HEIGHT).center().spaceBottom(HORIZONTAL_SPACING).row();
        add(confirmPasswordField).width(ROW_WIDTH).height(ROW_HEIGHT).center().spaceBottom(HORIZONTAL_SPACING).row();

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
        resetConfirmPasswordField();
    }

    private void resetPasswordField() {
        passwordField.getStyle().fontColor = Color.GRAY;
        passwordField.setText("Passwort");
        passwordField.setPasswordMode(false);
        getStage().unfocus(passwordField);
    }

    private void resetConfirmPasswordField() {
        confirmPasswordField.getStyle().fontColor = Color.GRAY;
        confirmPasswordField.setText("Passwort bestätigen");
        confirmPasswordField.setPasswordMode(false);
        getStage().unfocus(confirmPasswordField);
    }

    private class ConfirmDeletionTable extends MenuTable {

        private TextButton confirmButton;
        private TextButton cancelButton;

        @Override
        protected void create() {
            infoLabel.setText("Bist du sicher, dass du dein Konto löschen möchtest?");

            confirmButton = new TextButton("Bestätigen", Chati.SKIN);
            confirmButton.addListener(new InputListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    return true;
                }
                @Override
                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                    Chati.getInstance().getServerSender().send(ServerSender.SendAction.PROFILE_LOGOUT,
                            passwordField.getText(), true);
                    Chati.getInstance().getMenuScreen().setPendingResponse(Response.DELETE_ACCOUNT);
                }
            });

            cancelButton = new TextButton("Zurück", Chati.SKIN);
            cancelButton.addListener(new InputListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    return true;
                }
                @Override
                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                    Chati.getInstance().getMenuScreen().setTable(new ChangePasswordTable());
                }
            });
        }

        @Override
        protected void setLayout() {
            Table infoContainer = new Table();
            infoContainer.add(infoLabel);
            add(infoContainer).width(ROW_WIDTH).height(ROW_HEIGHT).center().spaceBottom(HORIZONTAL_SPACING).row();

            Table buttonContainer = new Table();
            buttonContainer.setWidth(ROW_WIDTH);
            buttonContainer.defaults().space(VERTICAL_SPACING);
            add(buttonContainer).spaceBottom(HORIZONTAL_SPACING).row();
            buttonContainer.add(confirmButton).width(ROW_WIDTH / 2f - (VERTICAL_SPACING / 2f)).height(ROW_HEIGHT);
            buttonContainer.add(cancelButton).width(ROW_WIDTH / 2f - (VERTICAL_SPACING / 2f)).height(ROW_HEIGHT);
        }

        @Override
        public void resetTextFields() {
        }
    }
}
