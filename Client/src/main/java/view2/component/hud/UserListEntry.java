package view2.component.hud;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.*;
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
        super(user.getUsername());
        this.user = user;
    }

    @Override
    protected void create() {
        final UserListEntry entry = this;

        usernameLabel = new Label(user.getUsername(), SKIN);

        friendButton = new TextButton("f", SKIN);
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
                    MessageDialog messageDialog =
                            new MessageDialog(entry, "Füge deiner Anfrage eine Nachricht hinzu!", AdministrativeAction.INVITE_FRIEND);
                    add(messageDialog);
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

    private class MessageDialog extends Window {

        private final UserListEntry entry;
        private final AdministrativeAction action;

        private TextField userMessageField;
        private TextButton confirmButton;
        private TextButton cancelButton;

        protected MessageDialog(UserListEntry entry, String showMessage, AdministrativeAction action) {
            super("", ChatiTable.SKIN);
            this.entry = entry;
            infoLabel.setText(showMessage);
            this.action = action;
        }

        protected void create() {
            final MessageDialog table = this;

            userMessageField = new TextField("", SKIN);

            confirmButton = new TextButton("Bestätigen", SKIN);
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

            cancelButton = new TextButton("Abbrechen", SKIN);
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

        }

        public String getMessage() {
            return userMessageField.getText();
        }
    }
}
