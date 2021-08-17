package view2.component.hud.userList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
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
import view2.Icon;
import view2.component.ChatiToolTip;
import view2.component.ChatiWindow;

import java.util.ArrayList;
import java.util.List;

public class UserListEntry extends Table implements Comparable<UserListEntry> {

    private static final float BUTTON_SIZE = 30;
    private static final float USER_INFO_ICON_SIZE = 22.5f;
    private static final float VERTICAL_SPACING = 5;
    private static final float HORIZONTAL_SPACING = 10;
    private static final float LABEL_FONT_SCALE_FACTOR = 1.5f;
    private static final float BUTTON_SCALE_FACTOR = 0.1f;

    private final List<Image> roleIcons;
    private Image statusImage;
    private Label usernameLabel;
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
        this.roleIcons = new ArrayList<>();
        create();
        setLayout();
    }

    protected void create() {
        IInternUserView internUser = Chati.getInstance().getUserManager().getInternUserView();

        usernameLabel = new Label(user.getUsername(), Chati.SKIN);
        usernameLabel.setFontScale(LABEL_FONT_SCALE_FACTOR);

        if (user.hasRole(Role.OWNER)) {
            usernameLabel.setColor(Color.GOLD);
            Image ownerImage = new Image(Icon.OWNER_ICON);
            ownerImage.addListener(new ChatiToolTip("Besitzer"));
            roleIcons.add(ownerImage);
        } else if (user.hasRole(Role.ADMINISTRATOR)) {
            usernameLabel.setColor(Color.SKY);
            Image administratorImage = new Image(Icon.ADMINISTRATOR_ICON);
            administratorImage.addListener(new ChatiToolTip("Administrator"));
            roleIcons.add(administratorImage);
        } else if (user.hasRole(Role.MODERATOR)) {
            usernameLabel.setColor(Color.ORANGE);
            Image moderatorImage = new Image(Icon.MODERATOR_ICON);
            moderatorImage.addListener(new ChatiToolTip("Moderator"));
            roleIcons.add(moderatorImage);
        }
        if (user.hasRole(Role.ROOM_OWNER)) {
            Image roomOwnerImage = new Image(Icon.ROOM_OWNER_ICON);
            roomOwnerImage.addListener(new ChatiToolTip("Raumbesitzer"));
            roleIcons.add(roomOwnerImage);
        }
        if (user.hasRole(Role.AREA_MANAGER)) {
            Image areaManagerImage = new Image(Icon.AREA_MANAGER_ICON);
            areaManagerImage.addListener(new ChatiToolTip("Bereichsberechtigter"));
            roleIcons.add(areaManagerImage);
        }
        if ((internUser.hasPermission(Permission.BAN_MODERATOR) || internUser.hasPermission(Permission.BAN_USER)
                && !(user.hasPermission(Permission.BAN_USER) || user.hasPermission(Permission.BAN_MODERATOR)))
                && user.isReported()) {
            usernameLabel.setColor(Color.RED);
        }

        statusImage = new Image();
        switch (user.getStatus()) {
            case ONLINE:
                statusImage.setDrawable(Icon.ONLINE_ICON);
                statusImage.addListener(new ChatiToolTip("Online"));
                break;
            case AWAY:
                statusImage.setDrawable(Icon.AWAY_ICON);
                statusImage.addListener(new ChatiToolTip("Abwesend"));
                break;
            case OFFLINE:
                statusImage.setDrawable(Icon.OFFLINE_ICON);
                statusImage.addListener(new ChatiToolTip("Offline"));
                usernameLabel.setColor(Color.GRAY);
                break;
            default:
                throw new IllegalArgumentException("No valid user status.");
        }

        if (!user.isFriend()) {
            friendButton = new ImageButton(Icon.ADD_FRIEND_ICON);
            friendButton.addListener(new ChatiToolTip("Freund hinzufügen"));
        } else {
            friendButton = new ImageButton(Icon.REMOVE_FRIEND_ICON);
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
                if (friendButton.getImage().getDrawable().equals(Icon.ADD_FRIEND_ICON)) {
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
            ignoreButton = new ImageButton(Icon.IGNORE_ICON);
            ignoreButton.addListener(new ChatiToolTip("Ignorieren"));
        } else {
            ignoreButton = new ImageButton(Icon.UNIGNORE_ICON);
            ignoreButton.addListener(new ChatiToolTip("Nicht mehr ignorieren"));
        }
        ignoreButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                ignoreButton.getImage().scaleBy(-BUTTON_SCALE_FACTOR);
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                ignoreButton.getImage().scaleBy(BUTTON_SCALE_FACTOR);
                if (ignoreButton.getImage().getDrawable().equals(Icon.IGNORE_ICON)) {
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

        if (internUser.isInPrivateRoom()
                && internUser.hasPermission(Permission.MANAGE_PRIVATE_ROOM)) {
            if (!user.isInCurrentWorld()) {
                roomButton = new ImageButton(Icon.ROOM_INVITE_ICON);
                roomButton.addListener(new ChatiToolTip("In den Raum einladen"));
            } else {
                roomButton = new ImageButton(Icon.ROOM_KICK_ICON);
                roomButton.addListener(new ChatiToolTip("Aus dem Raum entfernen"));
            }
        } else {
            roomButton = new ImageButton(Icon.DISABLED_ROOM_INVITE_ICON);
            roomButton.setDisabled(true);
            roomButton.setTouchable(Touchable.disabled);
        }
        roomButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                roomButton.getImage().scaleBy(-BUTTON_SCALE_FACTOR);
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                roomButton.getImage().scaleBy(BUTTON_SCALE_FACTOR);
                if (roomButton.getImage().getDrawable().equals(Icon.ROOM_INVITE_ICON)) {
                    getStage().addActor(new MessageWindow(AdministrativeAction.ROOM_INVITE));
                } else if (roomButton.getImage().getDrawable().equals(Icon.ROOM_KICK_ICON)) {
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

        if (internUser.isInCurrentWorld() && user.isInCurrentWorld() && user.canTeleportTo()) {
            teleportButton = new ImageButton(Icon.TELEPORT_ICON);
            teleportButton.addListener(new ChatiToolTip("Zu Benutzer teleportieren"));
        } else {
            teleportButton = new ImageButton(Icon.DISABLED_TELEPORT_ICON);
            teleportButton.setDisabled(true);
            teleportButton.setTouchable(Touchable.disabled);
        }
        teleportButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                teleportButton.getImage().scaleBy(-BUTTON_SCALE_FACTOR);
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                teleportButton.getImage().scaleBy(BUTTON_SCALE_FACTOR);
                if (teleportButton.getImage().getDrawable().equals(Icon.TELEPORT_ICON)) {
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

        if (internUser.isInCurrentWorld() && user.isInCurrentWorld()
                && !internUser.hasPermission(Permission.BAN_MODERATOR) && !user.hasPermission(Permission.BAN_MODERATOR)
                && (!internUser.hasPermission(Permission.BAN_USER) || user.hasPermission(Permission.BAN_USER))) {
            reportButton = new ImageButton(Icon.REPORT_ICON);
            reportButton.addListener(new ChatiToolTip("Melden"));
        } else {
            reportButton = new ImageButton(Icon.DISABLED_REPORT_ICON);
            reportButton.setDisabled(true);
            reportButton.setTouchable(Touchable.disabled);
        }
        reportButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                reportButton.getImage().scaleBy(-BUTTON_SCALE_FACTOR);
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                reportButton.getImage().scaleBy(BUTTON_SCALE_FACTOR);
                if (reportButton.getImage().getDrawable().equals(Icon.REPORT_ICON)) {
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

        if (internUser.isInCurrentWorld() && user.isInCurrentWorld()
                && internUser.hasPermission(Permission.MUTE) && !user.hasPermission(Permission.MUTE)) {
            if (!user.isMuted()) {
                muteButton = new ImageButton(Icon.MUTE_ICON);
                muteButton.addListener(new ChatiToolTip("Stummschalten"));
            } else {
                muteButton = new ImageButton(Icon.UNMUTE_ICON);
                muteButton.addListener(new ChatiToolTip("Stummschalten aufheben"));
            }
        } else {
            muteButton = new ImageButton(Icon.DISABLED_MUTE_ICON);
            muteButton.setDisabled(true);
            muteButton.setTouchable(Touchable.disabled);
        }
        muteButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                muteButton.getImage().scaleBy(-BUTTON_SCALE_FACTOR);
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                muteButton.getImage().scaleBy(BUTTON_SCALE_FACTOR);
                if (muteButton.getImage().getDrawable().equals(Icon.MUTE_ICON)) {
                    Chati.getInstance().getServerSender().send(ServerSender.SendAction.USER_MANAGE, user.getUserId(),
                            AdministrativeAction.MUTE_USER, "");
                } else if (muteButton.getImage().getDrawable().equals(Icon.UNMUTE_ICON)) {
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

        if (internUser.isInCurrentWorld() && internUser.hasPermission(Permission.BAN_MODERATOR)
                && !user.hasPermission(Permission.BAN_MODERATOR) || internUser.hasPermission(Permission.BAN_USER)
                && !user.hasPermission(Permission.BAN_MODERATOR) && !user.hasPermission(Permission.BAN_USER)) {
            if (!user.isBanned()) {
                banButton = new ImageButton(Icon.BAN_ICON);
                banButton.addListener(new ChatiToolTip("Sperren"));
            } else {
                banButton = new ImageButton(Icon.UNBAN_ICON);
                banButton.addListener(new ChatiToolTip("Entperren"));
            }
        } else {
            banButton = new ImageButton(Icon.DISABLED_BAN_ICON);
            banButton.setDisabled(true);
            banButton.setTouchable(Touchable.disabled);
        }
        banButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                banButton.getImage().scaleBy(-BUTTON_SCALE_FACTOR);
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                banButton.getImage().scaleBy(BUTTON_SCALE_FACTOR);
                if (banButton.getImage().getDrawable().equals(Icon.BAN_ICON)) {
                    getStage().addActor(new MessageWindow(AdministrativeAction.BAN_USER));
                } else if (banButton.getImage().getDrawable().equals(Icon.UNBAN_ICON)) {
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

        if (internUser.isInCurrentWorld() && user.isInCurrentWorld()
                && internUser.hasPermission(Permission.ASSIGN_MODERATOR)) {
            if (!user.hasRole(Role.MODERATOR)) {
                moderatorButton = new ImageButton(Icon.ASSIGN_MODERATOR_ICON);
                moderatorButton.addListener(new ChatiToolTip("Die Rolle des Moderators vergeben"));
            } else {
                moderatorButton = new ImageButton(Icon.WITHDRAW_MODERATOR_ICON);
                moderatorButton.addListener(new ChatiToolTip("Die Rolle des Moderators entziehen"));
            }
        } else {
            moderatorButton = new ImageButton(Icon.DISABLED_ASSIGN_MODERATOR_ICON);
            moderatorButton.setDisabled(true);
            moderatorButton.setTouchable(Touchable.disabled);
        }
        moderatorButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                moderatorButton.getImage().scaleBy(-BUTTON_SCALE_FACTOR);
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                moderatorButton.getImage().scaleBy(BUTTON_SCALE_FACTOR);
                if (moderatorButton.getImage().getDrawable().equals(Icon.ASSIGN_MODERATOR_ICON)) {
                    Chati.getInstance().getServerSender().send(ServerSender.SendAction.USER_MANAGE, user.getUserId(),
                            AdministrativeAction.ASSIGN_MODERATOR, "");
                } else if (moderatorButton.getImage().getDrawable().equals(Icon.WITHDRAW_MODERATOR_ICON)) {
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

        Table userInfoContainer = new Table();
        userInfoContainer.add(statusImage).width(USER_INFO_ICON_SIZE).height(USER_INFO_ICON_SIZE).space(HORIZONTAL_SPACING);
        userInfoContainer.add(usernameLabel).space(HORIZONTAL_SPACING);
        roleIcons.forEach(roleIcon -> userInfoContainer.add(roleIcon).width(USER_INFO_ICON_SIZE).height(USER_INFO_ICON_SIZE)
            .space(HORIZONTAL_SPACING / 2));

        add(userInfoContainer).left().padLeft(HORIZONTAL_SPACING).spaceBottom(VERTICAL_SPACING).height(BUTTON_SIZE).row();

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

        Role thisHighestRole = this.user.getHighestRole();
        Role otherHighestRole = other.user.getHighestRole();
        int roleComp = thisHighestRole == null ? 1
                : (otherHighestRole == null ? -1
                : thisHighestRole.compareTo(otherHighestRole));

        return statusComp != 0 ? statusComp
                : (roleComp != 0 ? roleComp
                : (this.user.getUsername().compareTo(other.user.getUsername())));
    }

    private class MessageWindow extends ChatiWindow {

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
            super("");
            this.action = action;
            create();
            setLayout();
        }

        @Override
        protected void create() {
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
            confirmButton.addListener(new ClickListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    return true;
                }
                @Override
                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                    Chati.getInstance().getServerSender()
                            .send(ServerSender.SendAction.USER_MANAGE, user.getUserId(), action, userMessageArea.getText());
                    MessageWindow.this.remove();
                }
            });

            cancelButton = new TextButton("Abbrechen", Chati.SKIN);
            cancelButton.addListener(new ClickListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    return true;
                }
                @Override
                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                    MessageWindow.this.remove();
                }
            });
        }

        @Override
        protected void setLayout() {
            setModal(true);
            setMovable(false);
            setPosition((Gdx.graphics.getWidth() - WINDOW_WIDTH) / 2f, (Gdx.graphics.getHeight() - WINDOW_HEIGHT) / 2f);
            setWidth(WINDOW_WIDTH);
            setHeight(WINDOW_HEIGHT);

            Table labelContainer = new Table(Chati.SKIN);
            labelContainer.add(infoLabel).center();

            add(labelContainer).width(ROW_WIDTH).height(ROW_HEIGHT).spaceTop(VERTICAL_SPACING).spaceBottom(VERTICAL_SPACING).row();
            add(userMessageArea).width(ROW_WIDTH).height(2 * ROW_HEIGHT).spaceBottom(VERTICAL_SPACING).row();

            Table buttonContainer = new Table(Chati.SKIN);
            buttonContainer.add(confirmButton).width((ROW_WIDTH - VERTICAL_SPACING) / 2).height(BUTTON_SIZE).space(HORIZONTAL_SPACING);
            buttonContainer.add(cancelButton).width((ROW_WIDTH - VERTICAL_SPACING) / 2).height(BUTTON_SIZE);
            add(buttonContainer).width(ROW_WIDTH).height(ROW_HEIGHT);
        }
    }
}
