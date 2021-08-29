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
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Align;
import controller.network.ServerSender;
import model.role.Permission;
import model.role.Role;
import model.user.AdministrativeAction;
import model.user.IInternUserView;
import model.user.IUserView;
import org.jetbrains.annotations.NotNull;
import view2.Chati;
import view2.Assets;
import view2.component.ChatiTextArea;
import view2.component.InformationToolTip;
import view2.component.AbstractWindow;
import view2.component.UserInfoContainer;

import java.util.Objects;

public class UserListEntry extends Table implements Comparable<UserListEntry> {

    private static final float BUTTON_SIZE = 30;
    private static final float ICON_SIZE = 22.5f;
    private static final float VERTICAL_SPACING = 5;
    private static final float HORIZONTAL_SPACING = 10;
    private static final float BUTTON_SCALE_FACTOR = 0.1f;

    private UserInfoContainer userInfoContainer;
    private Image statusImage;
    private Image currentWorldImage;
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
        IInternUserView internUser = Chati.CHATI.getUserManager().getInternUserView();
        userInfoContainer = new UserInfoContainer(user);

        statusImage = new Image();
        switch (user.getStatus()) {
            case ONLINE:
                statusImage.setDrawable(Assets.ONLINE_ICON);
                statusImage.addListener(new InformationToolTip("Online"));
                break;
            case AWAY:
                statusImage.setDrawable(Assets.AWAY_ICON);
                statusImage.addListener(new InformationToolTip("Abwesend"));
                break;
            case OFFLINE:
                statusImage.setDrawable(Assets.OFFLINE_ICON);
                statusImage.addListener(new InformationToolTip("Offline"));
                break;
            default:
                throw new IllegalArgumentException("No valid user status.");
        }

        currentWorldImage = new Image();
        if (user.isInCurrentWorld()) {
            currentWorldImage.setDrawable(Assets.CURRENT_WORLD_ICON);
            currentWorldImage.addListener(new InformationToolTip("In deiner Welt!"));
        }

        if (!user.isFriend()) {
            friendButton = new ImageButton(Assets.ADD_FRIEND_ICON);
            friendButton.addListener(new InformationToolTip("Freund hinzufügen"));
        } else {
            friendButton = new ImageButton(Assets.REMOVE_FRIEND_ICON);
            friendButton.addListener(new InformationToolTip("Freund entfernen"));
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
                if (friendButton.getImage().getDrawable().equals(Assets.ADD_FRIEND_ICON)) {
                    new MessageWindow(AdministrativeAction.INVITE_FRIEND).open();
                } else {
                    Chati.CHATI.getServerSender().send(ServerSender.SendAction.USER_MANAGE, user.getUserId(),
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
            ignoreButton = new ImageButton(Assets.IGNORE_ICON);
            ignoreButton.addListener(new InformationToolTip("Ignorieren"));
        } else {
            ignoreButton = new ImageButton(Assets.UNIGNORE_ICON);
            ignoreButton.addListener(new InformationToolTip("Nicht mehr ignorieren"));
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
                if (ignoreButton.getImage().getDrawable().equals(Assets.IGNORE_ICON)) {
                    Chati.CHATI.getServerSender().send(ServerSender.SendAction.USER_MANAGE, user.getUserId(),
                            AdministrativeAction.IGNORE_USER, "");
                } else {
                    Chati.CHATI.getServerSender().send(ServerSender.SendAction.USER_MANAGE, user.getUserId(),
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
            if (!user.isInCurrentRoom()) {
                roomButton = new ImageButton(Assets.ROOM_INVITE_ICON);
                roomButton.addListener(new InformationToolTip("In den Raum einladen"));
            } else {
                roomButton = new ImageButton(Assets.ROOM_KICK_ICON);
                roomButton.addListener(new InformationToolTip("Aus dem Raum entfernen"));
            }
        } else {
            roomButton = new ImageButton(Assets.DISABLED_ROOM_INVITE_ICON);
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
                if (roomButton.getImage().getDrawable().equals(Assets.ROOM_INVITE_ICON)) {
                    new MessageWindow(AdministrativeAction.ROOM_INVITE).open();
                } else if (roomButton.getImage().getDrawable().equals(Assets.ROOM_KICK_ICON)) {
                    Chati.CHATI.getServerSender().send(ServerSender.SendAction.USER_MANAGE, user.getUserId(),
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
            teleportButton = new ImageButton(Assets.TELEPORT_ICON);
            teleportButton.addListener(new InformationToolTip("Zu Benutzer teleportieren"));
        } else {
            teleportButton = new ImageButton(Assets.DISABLED_TELEPORT_ICON);
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
                if (teleportButton.getImage().getDrawable().equals(Assets.TELEPORT_ICON)) {
                    Chati.CHATI.getServerSender().send(ServerSender.SendAction.USER_MANAGE, user.getUserId(),
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
            reportButton = new ImageButton(Assets.REPORT_ICON);
            reportButton.addListener(new InformationToolTip("Melden"));
        } else {
            reportButton = new ImageButton(Assets.DISABLED_REPORT_ICON);
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
                if (reportButton.getImage().getDrawable().equals(Assets.REPORT_ICON)) {
                    new MessageWindow(AdministrativeAction.REPORT_USER).open();
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
                muteButton = new ImageButton(Assets.MUTE_ICON);
                muteButton.addListener(new InformationToolTip("Stummschalten"));
            } else {
                muteButton = new ImageButton(Assets.UNMUTE_ICON);
                muteButton.addListener(new InformationToolTip("Stummschalten aufheben"));
            }
        } else {
            muteButton = new ImageButton(Assets.DISABLED_MUTE_ICON);
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
                if (muteButton.getImage().getDrawable().equals(Assets.MUTE_ICON)) {
                    Chati.CHATI.getServerSender().send(ServerSender.SendAction.USER_MANAGE, user.getUserId(),
                            AdministrativeAction.MUTE_USER, "");
                } else if (muteButton.getImage().getDrawable().equals(Assets.UNMUTE_ICON)) {
                    Chati.CHATI.getServerSender().send(ServerSender.SendAction.USER_MANAGE, user.getUserId(),
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
                banButton = new ImageButton(Assets.BAN_ICON);
                banButton.addListener(new InformationToolTip("Sperren"));
            } else {
                banButton = new ImageButton(Assets.UNBAN_ICON);
                banButton.addListener(new InformationToolTip("Entperren"));
            }
        } else {
            banButton = new ImageButton(Assets.DISABLED_BAN_ICON);
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
                if (banButton.getImage().getDrawable().equals(Assets.BAN_ICON)) {
                    new MessageWindow(AdministrativeAction.BAN_USER).open();
                } else if (banButton.getImage().getDrawable().equals(Assets.UNBAN_ICON)) {
                    new MessageWindow(AdministrativeAction.UNBAN_USER).open();
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
                && internUser.hasPermission(Permission.ASSIGN_MODERATOR)
                && !user.hasRole(Role.ADMINISTRATOR) && !user.hasRole(Role.OWNER)) {
            if (!user.hasRole(Role.MODERATOR)) {
                moderatorButton = new ImageButton(Assets.ASSIGN_MODERATOR_ICON);
                moderatorButton.addListener(new InformationToolTip("Die Rolle des Moderators vergeben"));
            } else {
                moderatorButton = new ImageButton(Assets.WITHDRAW_MODERATOR_ICON);
                moderatorButton.addListener(new InformationToolTip("Die Rolle des Moderators entziehen"));
            }
        } else {
            moderatorButton = new ImageButton(Assets.DISABLED_ASSIGN_MODERATOR_ICON);
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
                if (moderatorButton.getImage().getDrawable().equals(Assets.ASSIGN_MODERATOR_ICON)) {
                    Chati.CHATI.getServerSender().send(ServerSender.SendAction.USER_MANAGE, user.getUserId(),
                            AdministrativeAction.ASSIGN_MODERATOR, "");
                } else if (moderatorButton.getImage().getDrawable().equals(Assets.WITHDRAW_MODERATOR_ICON)) {
                    Chati.CHATI.getServerSender().send(ServerSender.SendAction.USER_MANAGE, user.getUserId(),
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
                new NinePatchDrawable(new NinePatch(Assets.SKIN.getRegion("panel1"), 10, 10, 10, 10));
        setBackground(controlsBackground);
        left().defaults().padTop(VERTICAL_SPACING);

        Table container = new Table();
        container.add(statusImage).left().size(ICON_SIZE).padRight(HORIZONTAL_SPACING);
        container.add(userInfoContainer).left();
        container.add(currentWorldImage).right().size(ICON_SIZE).padLeft(HORIZONTAL_SPACING).padRight(HORIZONTAL_SPACING).growX();
        add(container).left().padLeft(HORIZONTAL_SPACING).spaceBottom(VERTICAL_SPACING).height(BUTTON_SIZE).growX().row();

        Table buttonContainer = new Table();
        buttonContainer.defaults().size(BUTTON_SIZE).padBottom(VERTICAL_SPACING).growX();
        buttonContainer.add(friendButton, ignoreButton, roomButton, teleportButton, reportButton, muteButton,
                banButton, moderatorButton);
        add(buttonContainer).center().growX();
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

    private class MessageWindow extends AbstractWindow {

        private static final float WINDOW_WIDTH = 750;
        private static final float WINDOW_HEIGHT = 350;
        private static final float ROW_HEIGHT = 60;
        private static final float SPACING = 15;

        private Label infoLabel;
        private ChatiTextArea userMessageArea;
        private TextButton confirmButton;
        private TextButton cancelButton;
        private TextButton closeButton;

        private final AdministrativeAction action;

        protected MessageWindow(AdministrativeAction action) {
            super("");
            this.action = action;
            create();
            setLayout();
        }

        @Override
        protected void create() {
            this.infoLabel = new Label("", Assets.SKIN);

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

            userMessageArea = new ChatiTextArea("Nachricht", true);

            confirmButton = new TextButton("Bestätigen", Assets.SKIN);
            confirmButton.addListener(new ClickListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    return true;
                }
                @Override
                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                    String message;
                    if (userMessageArea.getText().isBlank() || userMessageArea.getStyle().fontColor == Color.GRAY) {
                        message = "";
                    } else {
                        message = userMessageArea.getText();
                    }

                    Chati.CHATI.getServerSender()
                            .send(ServerSender.SendAction.USER_MANAGE, user.getUserId(), action, message);
                    close();
                }
            });

            cancelButton = new TextButton("Abbrechen", Assets.SKIN);
            cancelButton.addListener(new ClickListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    return true;
                }
                @Override
                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                    close();
                }
            });

            closeButton = new TextButton("X", Assets.SKIN);
            closeButton.addListener(new ClickListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    return true;
                }
                @Override
                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                    close();
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

            Table container = new Table();
            container.defaults().height(ROW_HEIGHT).spaceBottom(SPACING).center().growX();
            infoLabel.setAlignment(Align.center, Align.center);
            infoLabel.setWrap(true);
            container.add(infoLabel).row();
            container.add(userMessageArea).height(2 * ROW_HEIGHT).row();
            Table buttonContainer = new Table();
            buttonContainer.defaults().colspan(2).height(ROW_HEIGHT).space(SPACING).growX();
            buttonContainer.add(confirmButton, cancelButton);
            container.add(buttonContainer);
            add(container).padLeft(SPACING).padRight(SPACING).grow();

            getTitleTable().add(closeButton).right().width(getPadTop() * (2f/3f)).height(getPadTop() * (2f/3f));
        }

        public void resetTextFields() {
            userMessageArea.reset();
        }
    }
}
