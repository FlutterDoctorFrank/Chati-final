package view2.component.hud.userList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import controller.network.ServerSender;
import model.role.Permission;
import model.role.Role;
import model.user.AdministrativeAction;
import model.user.IInternUserView;
import model.user.IUserView;
import org.jetbrains.annotations.NotNull;
import view2.Chati;
import view2.Texture;
import view2.component.ChatiToolTip;

import java.util.ArrayList;
import java.util.List;

public class UserListEntry extends Table implements Comparable<UserListEntry> {

    private static final float BUTTON_SIZE = 30;
    private static final float STATUS_ICON_SIZE = 22f;
    private static final float VERTICAL_SPACING = 5;
    private static final float HORIZONTAL_SPACING = 7.5f;
    private static final float LABEL_FONT_SCALE_FACTOR = 1.5f;
    private static final float BUTTON_SCALE_FACTOR = 0.1f;

    private Image statusImage;
    private Label usernameLabel;
    private List<Image> roleImages;
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
        this.roleImages = new ArrayList<>();
        create();
        setLayout();
    }

    protected void create() {
        usernameLabel = new Label(user.getUsername(), Chati.SKIN);
        usernameLabel.setFontScale(LABEL_FONT_SCALE_FACTOR);

        statusImage = new Image();
        switch (user.getStatus()) {
            case ONLINE:
                statusImage.setDrawable(Texture.ONLINE_ICON);
                statusImage.addListener(new ChatiToolTip("Online"));
                break;
            case AWAY:
                statusImage.setDrawable(Texture.AWAY_ICON);
                statusImage.addListener(new ChatiToolTip("Abwesend"));
                break;
            case OFFLINE:
                statusImage.setDrawable(Texture.OFFLINE_ICON);
                statusImage.addListener(new ChatiToolTip("Offline"));
                usernameLabel.setColor(Color.GRAY);
                break;
            default:
                throw new IllegalArgumentException("There is no icon for this user status.");
        }

        if (user.hasRole(Role.OWNER)) {
            usernameLabel.setColor(Color.GOLD);
        } else if (user.hasRole(Role.ADMINISTRATOR)) {
            usernameLabel.setColor(Color.SLATE);
        } else if (user.hasRole(Role.MODERATOR)) {
            usernameLabel.setColor(Color.ORANGE);
        } else if (user.isReported()) {
            usernameLabel.setColor(Color.RED);
        }

        IInternUserView internUser = Chati.getInstance().getUserManager().getInternUserView();

        if (!user.isFriend()) {
            friendButton = new ImageButton(Texture.ADD_FRIEND_ICON);
            friendButton.addListener(new ChatiToolTip("Freund hinzufügen"));
        } else {
            friendButton = new ImageButton(Texture.REMOVE_FRIEND_ICON);
            friendButton.addListener(new ChatiToolTip("Freund entfernen"));
        }
        friendButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                friendButton.getImage().scaleBy(-BUTTON_SCALE_FACTOR);
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                friendButton.getImage().scaleBy(BUTTON_SCALE_FACTOR);
                if (friendButton.getImage().getDrawable().equals(Texture.ADD_FRIEND_ICON)) {
                    getStage().addActor(new MessageWindow(AdministrativeAction.INVITE_FRIEND));
                } else {
                    Chati.getInstance().getServerSender().send(ServerSender.SendAction.USER_MANAGE, user.getUserId(),
                            AdministrativeAction.REMOVE_FRIEND, "");
                }
            }
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if (pointer == -1) {
                    friendButton.getImage().scaleBy(BUTTON_SCALE_FACTOR);
                }
            }
            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if (pointer == -1) {
                    friendButton.getImage().scaleBy(-BUTTON_SCALE_FACTOR);
                }
            }
        });

        if (!user.isIgnored()) {
            ignoreButton = new ImageButton(Texture.IGNORE_ICON);
            ignoreButton.addListener(new ChatiToolTip("Ignorieren"));
        } else {
            ignoreButton = new ImageButton(Texture.UNIGNORE_ICON);
            ignoreButton.addListener(new ChatiToolTip("Nicht mehr ignorieren"));
        }
        ignoreButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                ignoreButton.getImage().scaleBy(-BUTTON_SCALE_FACTOR);
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                ignoreButton.getImage().scaleBy(BUTTON_SCALE_FACTOR);
                if (ignoreButton.getImage().getDrawable().equals(Texture.IGNORE_ICON)) {
                    Chati.getInstance().getServerSender().send(ServerSender.SendAction.USER_MANAGE, user.getUserId(),
                            AdministrativeAction.IGNORE_USER, "");
                } else {
                    Chati.getInstance().getServerSender().send(ServerSender.SendAction.USER_MANAGE, user.getUserId(),
                            AdministrativeAction.UNIGNORE_USER, "");
                }
            }
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if (pointer == -1) {
                    ignoreButton.getImage().scaleBy(BUTTON_SCALE_FACTOR);
                }
            }
            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if (pointer == -1) {
                    ignoreButton.getImage().scaleBy(-BUTTON_SCALE_FACTOR);
                }
            }
        });

        if (internUser != null && internUser.isInPrivateRoom()
                && internUser.hasPermission(Permission.MANAGE_PRIVATE_ROOM)) {
            if (!user.isInCurrentWorld()) {
                roomButton = new ImageButton(Texture.ROOM_INVITE_ICON);
                roomButton.addListener(new ChatiToolTip("In den Raum einladen"));
            } else {
                roomButton = new ImageButton(Texture.ROOM_KICK_ICON);
                roomButton.addListener(new ChatiToolTip("Aus dem Raum entfernen"));
            }
        } else {
            roomButton = new ImageButton(Texture.DISABLED_ROOM_INVITE_ICON);
            roomButton.setDisabled(true);
            roomButton.setTouchable(Touchable.disabled);
        }
        roomButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                roomButton.getImage().scaleBy(-BUTTON_SCALE_FACTOR);
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                roomButton.getImage().scaleBy(BUTTON_SCALE_FACTOR);
                if (roomButton.getImage().getDrawable().equals(Texture.ROOM_INVITE_ICON)) {
                    getStage().addActor(new MessageWindow(AdministrativeAction.ROOM_INVITE));
                } else if (roomButton.getImage().getDrawable().equals(Texture.ROOM_KICK_ICON)) {
                    Chati.getInstance().getServerSender().send(ServerSender.SendAction.USER_MANAGE, user.getUserId(),
                            AdministrativeAction.ROOM_KICK, "");
                }
            }
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if (pointer == -1) {
                    roomButton.getImage().scaleBy(BUTTON_SCALE_FACTOR);
                }
            }
            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if (pointer == -1) {
                    roomButton.getImage().scaleBy(-BUTTON_SCALE_FACTOR);
                }
            }
        });

        if (internUser != null && internUser.isInCurrentWorld() && user.isInCurrentWorld() && user.canTeleportTo()) {
            teleportButton = new ImageButton(Texture.TELEPORT_ICON);
            teleportButton.addListener(new ChatiToolTip("Zu Benutzer teleportieren"));
        } else {
            teleportButton = new ImageButton(Texture.DISABLED_TELEPORT_ICON);
            teleportButton.setDisabled(true);
            teleportButton.setTouchable(Touchable.disabled);
        }
        teleportButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                teleportButton.getImage().scaleBy(-BUTTON_SCALE_FACTOR);
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                teleportButton.getImage().scaleBy(BUTTON_SCALE_FACTOR);
                if (teleportButton.getImage().getDrawable().equals(Texture.TELEPORT_ICON)) {
                    Chati.getInstance().getServerSender().send(ServerSender.SendAction.USER_MANAGE, user.getUserId(),
                            AdministrativeAction.TELEPORT_TO_USER, "");
                }
            }
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if (pointer == -1) {
                    teleportButton.getImage().scaleBy(BUTTON_SCALE_FACTOR);
                }
            }
            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if (pointer == -1) {
                    teleportButton.getImage().scaleBy(-BUTTON_SCALE_FACTOR);
                }
            }
        });

        if (internUser != null && internUser.isInCurrentWorld() && user.isInCurrentWorld()
                && !internUser.hasPermission(Permission.BAN_MODERATOR) && !user.hasPermission(Permission.BAN_MODERATOR)
                && (!internUser.hasPermission(Permission.BAN_USER) || user.hasPermission(Permission.BAN_USER))) {
            reportButton = new ImageButton(Texture.REPORT_ICON);
            reportButton.addListener(new ChatiToolTip("Melden"));
        } else {
            reportButton = new ImageButton(Texture.DISABLED_REPORT_ICON);
            reportButton.setDisabled(true);
            reportButton.setTouchable(Touchable.disabled);
        }
        reportButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                reportButton.getImage().scaleBy(-BUTTON_SCALE_FACTOR);
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                reportButton.getImage().scaleBy(BUTTON_SCALE_FACTOR);
                if (reportButton.getImage().getDrawable().equals(Texture.REPORT_ICON)) {
                    getStage().addActor(new MessageWindow(AdministrativeAction.REPORT_USER));
                }
            }
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if (pointer == -1) {
                    reportButton.getImage().scaleBy(BUTTON_SCALE_FACTOR);
                }
            }
            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if (pointer == -1) {
                    reportButton.getImage().scaleBy(-BUTTON_SCALE_FACTOR);
                }
            }
        });

        if (internUser != null && internUser.isInCurrentWorld() && user.isInCurrentWorld()
                && internUser.hasPermission(Permission.MUTE) && !user.hasPermission(Permission.MUTE)) {
            if (!user.isMuted()) {
                muteButton = new ImageButton(Texture.MUTE_ICON);
                muteButton.addListener(new ChatiToolTip("Stummschalten"));
            } else {
                muteButton = new ImageButton(Texture.UNMUTE_ICON);
                muteButton.addListener(new ChatiToolTip("Stummschalten aufheben"));
            }
        } else {
            muteButton = new ImageButton(Texture.DISABLED_MUTE_ICON);
            muteButton.setDisabled(true);
            muteButton.setTouchable(Touchable.disabled);
        }
        muteButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                muteButton.getImage().scaleBy(-BUTTON_SCALE_FACTOR);
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                muteButton.getImage().scaleBy(BUTTON_SCALE_FACTOR);
                if (muteButton.getImage().getDrawable().equals(Texture.MUTE_ICON)) {
                    Chati.getInstance().getServerSender().send(ServerSender.SendAction.USER_MANAGE, user.getUserId(),
                            AdministrativeAction.MUTE_USER, "");
                } else if (muteButton.getImage().getDrawable().equals(Texture.UNMUTE_ICON)) {
                    Chati.getInstance().getServerSender().send(ServerSender.SendAction.USER_MANAGE, user.getUserId(),
                            AdministrativeAction.UNMUTE_USER, "");
                }
            }
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if (pointer == -1) {
                    muteButton.getImage().scaleBy(BUTTON_SCALE_FACTOR);
                }
            }
            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if (pointer == -1) {
                    muteButton.getImage().scaleBy(-BUTTON_SCALE_FACTOR);
                }
            }
        });

        if (internUser != null && (internUser.isInCurrentWorld() && internUser.hasPermission(Permission.BAN_MODERATOR)
                && !user.hasPermission(Permission.BAN_MODERATOR) || internUser.hasPermission(Permission.BAN_USER)
                && !user.hasPermission(Permission.BAN_MODERATOR) && !user.hasPermission(Permission.BAN_USER))) {
            if (!user.isBanned()) {
                banButton = new ImageButton(Texture.BAN_ICON);
                banButton.addListener(new ChatiToolTip("Sperren"));
            } else {
                banButton = new ImageButton(Texture.UNBAN_ICON);
                banButton.addListener(new ChatiToolTip("Entperren"));
            }
        } else {
            banButton = new ImageButton(Texture.DISABLED_BAN_ICON);
            banButton.setDisabled(true);
            banButton.setTouchable(Touchable.disabled);
        }
        banButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                banButton.getImage().scaleBy(-BUTTON_SCALE_FACTOR);
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                banButton.getImage().scaleBy(BUTTON_SCALE_FACTOR);
                if (banButton.getImage().getDrawable().equals(Texture.BAN_ICON)) {
                    getStage().addActor(new MessageWindow(AdministrativeAction.BAN_USER));
                } else if (banButton.getImage().getDrawable().equals(Texture.UNBAN_ICON)) {
                    getStage().addActor(new MessageWindow(AdministrativeAction.UNBAN_USER));
                }
            }
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if (pointer == -1) {
                    banButton.getImage().scaleBy(BUTTON_SCALE_FACTOR);
                }
            }
            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if (pointer == -1) {
                    banButton.getImage().scaleBy(-BUTTON_SCALE_FACTOR);
                }
            }
        });

        if (internUser != null && internUser.isInCurrentWorld() && user.isInCurrentWorld()
                && internUser.hasPermission(Permission.ASSIGN_MODERATOR)) {
            if (!user.hasRole(Role.MODERATOR)) {
                moderatorButton = new ImageButton(Texture.ASSIGN_MODERATOR_ICON);
                moderatorButton.addListener(new ChatiToolTip("Die Rolle des Moderators vergeben"));
            } else {
                moderatorButton = new ImageButton(Texture.WITHDRAW_MODERATOR_ICON);
                moderatorButton.addListener(new ChatiToolTip("Die Rolle des Moderators entziehen"));
            }
        } else {
            moderatorButton = new ImageButton(Texture.DISABLED_ASSIGN_MODERATOR_ICON);
            moderatorButton.setDisabled(true);
            moderatorButton.setTouchable(Touchable.disabled);
        }
        moderatorButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                moderatorButton.getImage().scaleBy(-BUTTON_SCALE_FACTOR);
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                moderatorButton.getImage().scaleBy(BUTTON_SCALE_FACTOR);
                if (moderatorButton.getImage().getDrawable().equals(Texture.ASSIGN_MODERATOR_ICON)) {
                    Chati.getInstance().getServerSender().send(ServerSender.SendAction.USER_MANAGE, user.getUserId(),
                            AdministrativeAction.ASSIGN_MODERATOR, "");
                } else if (moderatorButton.getImage().getDrawable().equals(Texture.WITHDRAW_MODERATOR_ICON)) {
                    Chati.getInstance().getServerSender().send(ServerSender.SendAction.USER_MANAGE, user.getUserId(),
                            AdministrativeAction.WITHDRAW_MODERATOR, "");
                }
            }
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if (pointer == -1) {
                    moderatorButton.getImage().scaleBy(BUTTON_SCALE_FACTOR);
                }
            }
            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if (pointer == -1) {
                    moderatorButton.getImage().scaleBy(-BUTTON_SCALE_FACTOR);
                }
            }
        });
    }

    protected void setLayout() {
        NinePatchDrawable controlsBackground =
                new NinePatchDrawable(new NinePatch(Chati.SKIN.getRegion("panel1"), 10, 10, 10, 10));
        setBackground(controlsBackground);

        left().defaults().padTop(VERTICAL_SPACING);

        Table nameStatusContainer = new Table();
        nameStatusContainer.add(statusImage).width(STATUS_ICON_SIZE).height(STATUS_ICON_SIZE).space(1.5f * HORIZONTAL_SPACING);
        nameStatusContainer.add(usernameLabel);

        add(nameStatusContainer).left().padLeft(1.5f * HORIZONTAL_SPACING).spaceBottom(VERTICAL_SPACING).height(BUTTON_SIZE).row();

        Table buttonContainer = new Table();
        buttonContainer.add(friendButton).width(BUTTON_SIZE).height(BUTTON_SIZE).padBottom(VERTICAL_SPACING).fillX().expandX();
        buttonContainer.add(ignoreButton).width(BUTTON_SIZE).height(BUTTON_SIZE).padBottom(VERTICAL_SPACING).fillX().expandX();
        buttonContainer.add(roomButton).width(BUTTON_SIZE).height(BUTTON_SIZE).padBottom(VERTICAL_SPACING).fillX().expandX();
        buttonContainer.add(teleportButton).width(BUTTON_SIZE).height(BUTTON_SIZE).padBottom(VERTICAL_SPACING).fillX().expandX();
        buttonContainer.add(reportButton).width(BUTTON_SIZE).height(BUTTON_SIZE).padBottom(VERTICAL_SPACING).fillX().expandX();
        buttonContainer.add(muteButton).width(BUTTON_SIZE).height(BUTTON_SIZE).padBottom(VERTICAL_SPACING).fillX().expandX();
        buttonContainer.add(banButton).width(BUTTON_SIZE).height(BUTTON_SIZE).padBottom(VERTICAL_SPACING).fillX().expandX();
        buttonContainer.add(moderatorButton).width(BUTTON_SIZE).height(BUTTON_SIZE).padBottom(VERTICAL_SPACING).fillX().expandX();
        add(buttonContainer).center().fillX().expandX();
    }

    @Override
    public int compareTo(@NotNull UserListEntry other) {
        int statusComp = this.user.getStatus().compareTo(other.user.getStatus());
        if (statusComp == 0) {
            return this.user.getUsername().compareTo(other.user.getUsername());
        } else {
            return statusComp;
        }
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

        private final AdministrativeAction action;

        protected MessageWindow(AdministrativeAction action) {
            super("", Chati.SKIN);
            this.action = action;
            create();
            setLayout();
        }

        protected void create() {
            final MessageWindow thisWindow = this;

            Label.LabelStyle style = new Label.LabelStyle();
            style.font = new BitmapFont();
            style.font.getData().scale(LABEL_FONT_SCALE_FACTOR);
            this.infoLabel = new Label("", style);

            switch(action) {
                case INVITE_FRIEND:
                    getTitleLabel().setText("Freund hinzufügen");
                    infoLabel.setText("Füge eine Nachricht zu deiner Anfrage hinzu!");
                    break;
                case REPORT_USER:
                    getTitleLabel().setText("Benutzer melden");
                    infoLabel.setText("Nenne einen Grund für das Melden dieses Benutzers!");
                    break;
                case BAN_USER:
                    getTitleLabel().setText("Benutzer sperren");
                    infoLabel.setText("Nenne einen Grund für das Sperren dieses Benutzers!");
                    break;
                case UNBAN_USER:
                    getTitleLabel().setText("Benutzer entsperren");
                    infoLabel.setText("Nennen einen Grund für das Entsperren dieses Benutzers!");
                    break;
                case ROOM_INVITE:
                    getTitleLabel().setText("In den Raum einladen");
                    infoLabel.setText("Füge eine Nachricht zu deiner Einladung hinzu!");
            }

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

    public IUserView getUser() {
        return user;
    }
}
