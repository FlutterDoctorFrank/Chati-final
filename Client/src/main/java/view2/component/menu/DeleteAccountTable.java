package view2.component.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
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

import java.awt.*;

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
        infoLabel.setText("Gib dein aktuelles Passwort ein!");

        Skin passwordFieldSkin = new Skin(Gdx.files.internal("shadeui/uiskin.json"));
        passwordFieldSkin.get(TextField.TextFieldStyle.class).font.getData().setScale(1.6f);
        passwordField = new TextField("Passwort", passwordFieldSkin);
        passwordField.getStyle().fontColor = Color.GRAY;
        passwordField.setPasswordCharacter('*');
        passwordField.addListener(new FocusListener() {
            @Override
            public boolean handle(Event event){
                if (event.toString().equals("mouseMoved")
                        || event.toString().equals("exit")
                        || event.toString().equals("enter")
                        || event.toString().equals("keyDown")
                        || event.toString().equals("touchUp")) {
                    return false;
                }
                if (passwordField.getStyle().fontColor == Color.GRAY) {
                    passwordField.setText("");
                    passwordField.getStyle().fontColor = Color.BLACK;
                    passwordField.setPasswordMode(true);
                }
                return true;
            }
        });

        Skin confirmPasswordFieldSkin = new Skin(Gdx.files.internal("shadeui/uiskin.json"));
        confirmPasswordFieldSkin.get(TextField.TextFieldStyle.class).font.getData().setScale(1.6f);
        confirmPasswordField = new TextField("Neues Passwort", confirmPasswordFieldSkin);
        confirmPasswordField.getStyle().fontColor = Color.GRAY;
        confirmPasswordField.setPasswordCharacter('*');
        confirmPasswordField.addListener(new FocusListener() {
            @Override
            public boolean handle(Event event){
                if (event.toString().equals("mouseMoved")
                        || event.toString().equals("exit")
                        || event.toString().equals("enter")
                        || event.toString().equals("keyDown")
                        || event.toString().equals("touchUp")) {
                    return false;
                }
                if (confirmPasswordField.getStyle().fontColor == Color.GRAY) {
                    confirmPasswordField.setText("");
                    confirmPasswordField.getStyle().fontColor = Color.BLACK;
                    confirmPasswordField.setPasswordMode(true);
                }
                return true;
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
                if (passwordField.getText().isEmpty() || confirmPasswordField.getText().isEmpty()
                    || passwordField.getStyle().fontColor == Color.GRAY
                    || confirmPasswordField.getStyle().fontColor == Color.GRAY) {
                    infoLabel.setText("Bitte fülle alle Felder aus.");
                    return;
                }
                if (!passwordField.getText().equals(confirmPasswordField.getText())) {
                    infoLabel.setText("Die Passwörter stimmen nicht überein.");
                    return;
                }
                Chati.getInstance().getMenuScreen().setTable(new ConfirmDeletionTable());
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
                Chati.getInstance().getMenuScreen().setTable(new ProfileSettingsTable());
            }
        });
    }

    @Override
    protected void setLayout() {
        int width = 600;
        int height = 60;
        int verticalSpacing = 15;
        int horizontalSpacing = 15;

        Table infoContainer = new Table();
        infoContainer.add(infoLabel);
        add(infoContainer).width(width).height(height).center().spaceBottom(horizontalSpacing).row();

        add(passwordField).width(width).height(height).center().spaceBottom(horizontalSpacing).row();
        add(confirmPasswordField).width(width).height(height).center().spaceBottom(horizontalSpacing).row();

        Table buttonContainer = new Table();
        buttonContainer.setWidth(width);
        buttonContainer.defaults().space(verticalSpacing);
        add(buttonContainer).spaceBottom(horizontalSpacing).row();
        buttonContainer.add(confirmButton).width(width / 2f - (verticalSpacing / 2f)).height(height);
        buttonContainer.add(cancelButton).width(width / 2f - (verticalSpacing / 2f)).height(height);
    }

    @Override
    public void clearTextFields() {
        passwordField.setText("");
        confirmPasswordField.setText("");
    }

    private class ConfirmDeletionTable extends MenuTable {

        private static final String NAME = "confirm-deletion-table";
        private TextButton confirmButton;
        private TextButton cancelButton;

        protected ConfirmDeletionTable() {
            super(NAME);
        }

        @Override
        protected void create() {
            infoLabel.setText("Bist du sicher, dass du dein Konto löschen möchtest?");

            confirmButton = new TextButton("Bestätigen", SKIN);
            confirmButton.addListener(new InputListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    return true;
                }
                @Override
                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                    Chati.getInstance().getServerSender().send(ServerSender.SendAction.PROFILE_CHANGE,
                            passwordField.getText());
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
                    Chati.getInstance().getMenuScreen().setTable(new ChangePasswordTable());
                }
            });
        }

        @Override
        protected void setLayout() {
            int width = 600;
            int height = 60;
            int verticalSpacing = 15;
            int horizontalSpacing = 15;

            Table infoContainer = new Table();
            infoContainer.add(infoLabel);
            add(infoContainer).width(width).height(height).center().spaceBottom(horizontalSpacing).row();

            Table buttonContainer = new Table();
            buttonContainer.setWidth(width);
            buttonContainer.defaults().space(verticalSpacing);
            add(buttonContainer).spaceBottom(horizontalSpacing).row();
            buttonContainer.add(confirmButton).width(width / 2f - (verticalSpacing / 2f)).height(height);
            buttonContainer.add(cancelButton).width(width / 2f - (verticalSpacing / 2f)).height(height);
        }

        @Override
        public void clearTextFields() {

        }
    }
}
