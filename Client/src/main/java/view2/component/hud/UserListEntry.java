package view2.component.hud;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
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
        create();
        setLayout();
    }

    @Override
    protected void create() {
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
                            new MessageWindow("Füge deiner Anfrage eine Nachricht hinzu!", AdministrativeAction.INVITE_FRIEND);
                    showMessageWindow(messageWindow);
                } else {
                    Chati.getInstance().getServerSender().send(ServerSender.SendAction.USER_MANAGE, user.getUserId(),
                            AdministrativeAction.REMOVE_FRIEND, null);
                }
            }
        });
    }

    @Override
    protected void setLayout() {
        add(usernameLabel).left().row();
        add(friendButton).left().height(30).width(30);
    }

    private void showMessageWindow(MessageWindow window) {
        getStage().addActor(window);
    }

    private class MessageWindow extends Window {

        private static final float WINDOW_WIDTH = 400;
        private static final float WINDOW_HEIGHT = 300;
        private static final float ROW_WIDTH = 300;
        private static final float ROW_HEIGHT = 60;
        private static final float VERTICAL_SPACING = 15;
        private static final float HORIZONTAL_SPACING = 15;
        private static final float LABEL_FONT_SCALE_FACTOR = 0.5f;
        private static final float TEXTFIELD_FONT_SCALE_FACTOR = 1.6f;

        private Label infoLabel;
        private TextField userMessageField;
        private TextButton confirmButton;
        private TextButton cancelButton;

        private final String showMessage;
        private final AdministrativeAction action;

        protected MessageWindow(String showMessage, AdministrativeAction action) {
            super("Freund hinzufügen", Chati.SKIN);
            this.showMessage = showMessage;
            this.action = action;
            create();
            setLayout();
        }

        protected void create() {
            final MessageWindow thisWindow = this;

            Label.LabelStyle style = new Label.LabelStyle();
            style.font = new BitmapFont();
            style.font.getData().scale(LABEL_FONT_SCALE_FACTOR);
            this.infoLabel = new Label(showMessage, style);

            userMessageField = new TextField("", Chati.SKIN);
            userMessageField.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    getStage().setKeyboardFocus(userMessageField);
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
                    Chati.getInstance().getServerSender()
                            .send(ServerSender.SendAction.USER_MANAGE, user.getUserId(), action, userMessageField.getText());
                    thisWindow.remove();
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
                    thisWindow.remove();
                }
            });
        }

        protected void setLayout() {
            setModal(true);
            setPosition((Gdx.graphics.getWidth() - WINDOW_WIDTH) / 2f, (Gdx.graphics.getHeight() - WINDOW_HEIGHT) / 2f);
            setWidth(WINDOW_WIDTH);
            setHeight(WINDOW_HEIGHT);

            Table labelContainer = new Table(Chati.SKIN);
            labelContainer.add(infoLabel).center();

            add(labelContainer).width(ROW_WIDTH).height(ROW_HEIGHT).spaceBottom(VERTICAL_SPACING).row();
            add(userMessageField).width(ROW_WIDTH).height(ROW_HEIGHT).spaceBottom(VERTICAL_SPACING).row();

            Table buttonContainer = new Table(Chati.SKIN);
            buttonContainer.add(confirmButton).width(ROW_WIDTH / 2 - VERTICAL_SPACING).height(ROW_HEIGHT).space(HORIZONTAL_SPACING);
            buttonContainer.add(cancelButton).width(ROW_WIDTH / 2 - VERTICAL_SPACING).height(ROW_HEIGHT);
            add(buttonContainer).width(ROW_WIDTH).height(ROW_HEIGHT);
        }
    }
}
