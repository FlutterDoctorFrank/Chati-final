package view2.component.hud;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Align;
import controller.network.ServerSender;
import model.user.AdministrativeAction;
import model.user.IUserView;
import view2.Chati;
import view2.component.ChatiTable;

public class UserListEntry extends ChatiTable {

    private Label usernameLabel;
    private TextButton friendButton;
    private TextButton ignoreButton;
    private TextButton roomButton;
    private TextButton teleportButton;
    private TextButton reportButton;
    private TextButton muteButton;
    private TextButton banButton;
    private TextButton moderatorButton;

    private final IUserView user;

    protected UserListEntry(IUserView user) {
        this.user = user;
    }

    @Override
    protected void create() {
        final UserListEntry entry = this;

        usernameLabel = new Label(user.getUsername(), Chati.SKIN);

        friendButton = new TextButton("f", Chati.SKIN);
        if (user.isFriend()) {
            friendButton.setChecked(true);
        }
        friendButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if (!friendButton.isChecked()) {
                    MessageWindow messageWindow =
                            new MessageWindow(entry, "Füge deiner Anfrage eine Nachricht hinzu!", AdministrativeAction.INVITE_FRIEND);
                    add(messageWindow);
                } else {
                    Chati.getInstance().getServerSender().send(ServerSender.SendAction.USER_MANAGE, user.getUserId(),
                            AdministrativeAction.REMOVE_FRIEND);
                }
            }
        });
    }

    @Override
    protected void setLayout() {

    }

    private class MessageWindow extends Window {

        private final UserListEntry entry;
        private final AdministrativeAction action;

        private Label infoLabel;
        private TextField userMessageField;
        private TextButton confirmButton;
        private TextButton cancelButton;

        protected MessageWindow(UserListEntry entry, String showMessage, AdministrativeAction action) {
            super("Freund hinzufügen", Chati.SKIN);
            this.entry = entry;
            this.action = action;
            create();
            setLayout();
        }

        protected void create() {
            final MessageWindow table = this;

            infoLabel = new Label("Füge zur Anfrage eine Nachricht hinzu!", Chati.SKIN);

            userMessageField = new TextField("", Chati.SKIN);

            confirmButton = new TextButton("Bestätigen", Chati.SKIN);
            confirmButton.addListener(new InputListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    return true;
                }
                @Override
                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                    Chati.getInstance().getServerSender()
                            .send(ServerSender.SendAction.USER_MANAGE, user.getUserId(), action, userMessageField.getText());
                    entry.removeActor(table);
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
                    entry.removeActor(table);
                }
            });
        }

        protected void setLayout() {
            int width = 300;
            int height = 200;

            setSize(width, height);
            align(Align.center);

        }

        public String getMessage() {
            return userMessageField.getText();
        }
    }
}
