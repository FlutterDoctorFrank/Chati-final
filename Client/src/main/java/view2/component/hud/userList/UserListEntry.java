package view2.component.hud.userList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import controller.network.ServerSender;
import model.user.AdministrativeAction;
import model.user.IUserView;
import view2.Chati;

public class UserListEntry extends Table {

    private static final Drawable ONLINE_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("icons/online.png")));
    private static final Drawable AWAY_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("icons/away.png")));
    private static final Drawable OFFLINE_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("icons/offline.png")));
    private static final Drawable ADD_FRIEND_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("icons/freund_hinzufugen.png")));
    private static final Drawable REMOVE_FRIEND_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("icons/freund_entfernen.png")));
    private static final Drawable IGNORE_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("icons/ignorieren.png")));
    private static final Drawable UNIGNORE_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("icons/ignorieren_aufheben.png")));
    private static final Drawable ROOM_INVITE_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("icons/raum_anfragen.png")));
    private static final Drawable ROOM_KICK_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("icons/aus_raum_entfernen.png")));
    private static final Drawable TELEPORT_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("icons/teleportieren.png")));
    private static final Drawable REPORT_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("icons/melden.png")));
    private static final Drawable MUTE_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("icons/stummschalten.png")));
    private static final Drawable UNMUTE_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("icons/stummschalten_aufheben.png")));
    private static final Drawable BAN_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("icons/sperren.png")));
    private static final Drawable UNBAN_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("icons/entsperren.png")));
    private static final Drawable ASSIGN_MODERATOR_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("icons/moderator_ernennen.png")));
    private static final Drawable WITHDRAW_MODERATOR_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("icons/moderator_entfernen.png")));

    private static final float BUTTON_SIZE = 30;
    private static final float STATUS_ICON_SIZE = 22f;
    private static final float VERTICAL_SPACING = 5;
    private static final float HORIZONTAL_SPACING = 7.5f;
    private static final float BUTTON_SPACING = 8f;
    private static final float LABEL_FONT_SCALE_FACTOR = 1.5f;

    private Label usernameLabel;
    private Image statusImage;
    private ImageButton friendButton;
    private ImageButton ignoreButton;
    private ImageButton roomButton;
    private ImageButton teleportButton;
    private ImageButton reportButton;
    private ImageButton muteButton;
    private ImageButton banButton;
    private ImageButton moderatorButton;

    private final IUserView user;

    protected UserListEntry(IUserView user) {
        this.user = user;
        create();
        setLayout();
    }

    protected void create() {
        usernameLabel = new Label(user.getUsername(), Chati.SKIN);
        usernameLabel.setFontScale(LABEL_FONT_SCALE_FACTOR);

        statusImage = new Image();
        switch (user.getStatus()) {
            case ONLINE:
                statusImage.setDrawable(ONLINE_ICON);
                break;
            case AWAY:
                statusImage.setDrawable(AWAY_ICON);
                break;
            case OFFLINE:
                statusImage.setDrawable(OFFLINE_ICON);
                break;
            default:
                throw new IllegalArgumentException("There is no icon for this user status.");
        }

        if (!user.isFriend()) {
            friendButton = new ImageButton(ADD_FRIEND_ICON);
        } else {
            friendButton = new ImageButton(REMOVE_FRIEND_ICON);
        }
        friendButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if (!friendButton.isChecked()) {
                    getStage().addActor(new MessageWindow("Freund hinzufügen", "Füge deiner Anfrage eine Nachricht hinzu!", AdministrativeAction.INVITE_FRIEND));
                } else {
                    Chati.getInstance().getServerSender().send(ServerSender.SendAction.USER_MANAGE, user.getUserId(),
                            AdministrativeAction.REMOVE_FRIEND, null);
                }
            }
        });

        /*
        if (!user.isIgnored()) {
            ignoreButton = new ImageButton(IGNORE_ICON);
        } else {
            ignoreButton = new ImageButton(UNIGNORE_ICON);
        }
         */

        ignoreButton = new ImageButton(IGNORE_ICON);

        // Im Modell muss es eine Abfrage geben, ob der momentane Raum ein privater Raum ist!
        if (user.isInCurrentWorld() && !user.isInCurrentRoom()) {
            roomButton = new ImageButton(ROOM_INVITE_ICON);
        } else {
            roomButton = new ImageButton(ROOM_KICK_ICON);
        }

        // Hier brauch ich noch einen ausgegrauten Button, wenn man sich nicht teleportieren kann!
        // Ich brauch allgemein ausgegraute buttons...
        teleportButton = new ImageButton(TELEPORT_ICON);

        reportButton = new ImageButton(REPORT_ICON);

        /*
        if (!user.isMuted()) {
            muteButton = new ImageButton(MUTE_ICON);
        } else {
            muteButton = new ImageButton(UNMUTE_ICON);
        }
         */
        muteButton = new ImageButton(MUTE_ICON);

        /*
        if (!user.isBanned()) {
            banButton = new ImageButton(BAN_ICON);
        } else {
            banButton = new ImageButton(UNBAN_ICON);
        }
         */
        banButton = new ImageButton(BAN_ICON);

        // Das Modell braucht eine Abfrage der Rolle!
        moderatorButton = new ImageButton(ASSIGN_MODERATOR_ICON);
    }

    protected void setLayout() {
        NinePatchDrawable controlsBackground =
                new NinePatchDrawable(new NinePatch(Chati.SKIN.getRegion("panel1"), 10, 10, 10, 10));
        setBackground(controlsBackground);

        left().defaults().padTop(VERTICAL_SPACING);

        Table nameStatusContainer = new Table();
        nameStatusContainer.add(statusImage).width(STATUS_ICON_SIZE).height(STATUS_ICON_SIZE).space(1.6f * HORIZONTAL_SPACING);
        nameStatusContainer.add(usernameLabel);

        add(nameStatusContainer).left().padLeft(1.6f * HORIZONTAL_SPACING).spaceBottom(VERTICAL_SPACING).height(BUTTON_SIZE).row();

        Table buttonContainer = new Table();
        buttonContainer.add(friendButton).width(BUTTON_SIZE).height(BUTTON_SIZE).padBottom(VERTICAL_SPACING).space(HORIZONTAL_SPACING);
        buttonContainer.add(ignoreButton).width(BUTTON_SIZE).height(BUTTON_SIZE).padBottom(VERTICAL_SPACING).space(HORIZONTAL_SPACING);
        buttonContainer.add(roomButton).width(BUTTON_SIZE).height(BUTTON_SIZE).padBottom(VERTICAL_SPACING).space(HORIZONTAL_SPACING);
        buttonContainer.add(teleportButton).width(BUTTON_SIZE).height(BUTTON_SIZE).padBottom(VERTICAL_SPACING).space(HORIZONTAL_SPACING);
        buttonContainer.add(reportButton).width(BUTTON_SIZE).height(BUTTON_SIZE).padBottom(VERTICAL_SPACING).space(HORIZONTAL_SPACING);
        buttonContainer.add(muteButton).width(BUTTON_SIZE).height(BUTTON_SIZE).padBottom(VERTICAL_SPACING).space(HORIZONTAL_SPACING);
        buttonContainer.add(banButton).width(BUTTON_SIZE).height(BUTTON_SIZE).padBottom(VERTICAL_SPACING).space(HORIZONTAL_SPACING);
        buttonContainer.add(moderatorButton).width(BUTTON_SIZE).height(BUTTON_SIZE).padBottom(VERTICAL_SPACING);
        add(buttonContainer).center().fillX().expandX();
    }

    private class MessageWindow extends Window {

        private static final float WINDOW_WIDTH = 550;
        private static final float WINDOW_HEIGHT = 350;
        private static final float ROW_WIDTH = 450;
        private static final float ROW_HEIGHT = 60;
        private static final float VERTICAL_SPACING = 15;
        private static final float HORIZONTAL_SPACING = 15;
        private static final float LABEL_FONT_SCALE_FACTOR = 0.5f;
        private static final float TEXTFIELD_FONT_SCALE_FACTOR = 1.6f;

        private Label infoLabel;
        private TextArea userMessageArea;
        private TextButton confirmButton;
        private TextButton cancelButton;

        private final String showMessage;
        private final AdministrativeAction action;

        protected MessageWindow(String title, String showMessage, AdministrativeAction action) {
            super(title, Chati.SKIN);
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

            Skin userMessageAreaSkin = new Skin(Gdx.files.internal("shadeui/uiskin.json"));
            userMessageAreaSkin.get(TextField.TextFieldStyle.class).font.getData().setScale(TEXTFIELD_FONT_SCALE_FACTOR);
            userMessageArea = new TextArea("", userMessageAreaSkin);

            confirmButton = new TextButton("Bestätigen", Chati.SKIN);
            confirmButton.addListener(new InputListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    return true;
                }
                @Override
                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                    Chati.getInstance().getServerSender()
                            .send(ServerSender.SendAction.USER_MANAGE, user.getUserId(), action, userMessageArea.getText());
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
            setMovable(false);
            setPosition((Gdx.graphics.getWidth() - WINDOW_WIDTH) / 2f, (Gdx.graphics.getHeight() - WINDOW_HEIGHT) / 2f);
            setWidth(WINDOW_WIDTH);
            setHeight(WINDOW_HEIGHT);

            Table labelContainer = new Table(Chati.SKIN);
            labelContainer.add(infoLabel).center();

            add(labelContainer).width(ROW_WIDTH).height(ROW_HEIGHT).spaceTop(HORIZONTAL_SPACING).spaceBottom(VERTICAL_SPACING).row();
            add(userMessageArea).width(ROW_WIDTH).height(2 * ROW_HEIGHT).spaceBottom(VERTICAL_SPACING).row();

            Table buttonContainer = new Table(Chati.SKIN);
            buttonContainer.add(confirmButton).width((ROW_WIDTH - VERTICAL_SPACING) / 2).height(ROW_HEIGHT).space(HORIZONTAL_SPACING);
            buttonContainer.add(cancelButton).width((ROW_WIDTH - VERTICAL_SPACING) / 2).height(ROW_HEIGHT);
            add(buttonContainer).width(ROW_WIDTH).height(ROW_HEIGHT);
        }
    }
}
