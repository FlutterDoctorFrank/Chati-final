package view2.userInterface.hud.userList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Align;
import controller.network.ServerSender;
import model.role.Role;
import model.user.AdministrativeAction;
import model.user.IUserView;
import org.jetbrains.annotations.NotNull;
import view2.Chati;
import view2.userInterface.*;

public class UserListEntry extends Table implements Comparable<UserListEntry> {

    private static final float BUTTON_SIZE = 30;
    private static final float ICON_SIZE = 22.5f;
    private static final float VERTICAL_SPACING = 5;
    private static final float HORIZONTAL_SPACING = 10;

    private final IUserView user;

    public UserListEntry(IUserView user) {
        this.user = user;

        UserInfoContainer userInfoContainer = new UserInfoContainer(user);

        Image statusImage = new Image();
        switch (user.getStatus()) {
            case ONLINE:
                statusImage.setDrawable(Chati.CHATI.getDrawable("status_online"));
                statusImage.addListener(new ChatiToolTip("Online"));
                break;
            case AWAY:
                statusImage.setDrawable(Chati.CHATI.getDrawable("status_away"));
                statusImage.addListener(new ChatiToolTip("Abwesend"));
                break;
            case BUSY:
                statusImage.setDrawable(Chati.CHATI.getDrawable("status_busy"));
                statusImage.addListener(new ChatiToolTip("Beschäftigt"));
                break;
            case INVISIBLE:
                statusImage.setDrawable(Chati.CHATI.getDrawable("status_invisible"));
                statusImage.addListener(new ChatiToolTip("Unsichtbar"));
                break;
            case OFFLINE:
                statusImage.setDrawable(Chati.CHATI.getDrawable("status_offline"));
                statusImage.addListener(new ChatiToolTip("Offline"));
                break;
            default:
                throw new IllegalArgumentException("Invalid user status");
        }

        Image currentRoomImage = new Image();
        if (user.isOnline() && user.isInCurrentRoom()) {
            currentRoomImage.setDrawable(Chati.CHATI.getDrawable("current_room"));
            currentRoomImage.addListener(new ChatiToolTip("Der Benutzer ist in deinem Raum."));
        }

        Image currentWorldImage = new Image();
        if (user.isOnline() && user.isInCurrentWorld()) {
            currentWorldImage.setDrawable(Chati.CHATI.getDrawable("current_world"));
            currentWorldImage.addListener(new ChatiToolTip("Der Benutzer ist in deiner Welt."));
        }

        ChatiImageButton friendButton;
        if (!user.isFriend()) {
            friendButton = new ChatiImageButton(Chati.CHATI.getDrawable("action_add_friend"));
            friendButton.addListener(new ChatiToolTip("Freund hinzufügen"));
        } else {
            friendButton = new ChatiImageButton(Chati.CHATI.getDrawable("action_remove_friend"));
            friendButton.addListener(new ChatiToolTip("Freund entfernen"));
        }
        friendButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!user.isFriend()) {
                    new MessageWindow(AdministrativeAction.INVITE_FRIEND).open();
                } else {
                    Chati.CHATI.send(ServerSender.SendAction.USER_MANAGE, user.getUserId(),
                            AdministrativeAction.REMOVE_FRIEND, "");
                }
            }
        });

        ChatiImageButton ignoreButton;
        if (!user.isIgnored()) {
            ignoreButton = new ChatiImageButton(Chati.CHATI.getDrawable("action_ignore_user"));
            ignoreButton.addListener(new ChatiToolTip("Ignorieren"));
        } else {
            ignoreButton = new ChatiImageButton(Chati.CHATI.getDrawable("action_unignore_user"));
            ignoreButton.addListener(new ChatiToolTip("Nicht mehr ignorieren"));
        }
        ignoreButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!user.isIgnored()) {
                    Chati.CHATI.send(ServerSender.SendAction.USER_MANAGE, user.getUserId(),
                            AdministrativeAction.IGNORE_USER, "");
                } else {
                    Chati.CHATI.send(ServerSender.SendAction.USER_MANAGE, user.getUserId(),
                            AdministrativeAction.UNIGNORE_USER, "");
                }
            }
        });

        ChatiImageButton roomButton;
        if (user.canBeInvited()) {
            roomButton = new ChatiImageButton(Chati.CHATI.getDrawable("action_invite_user"));
            roomButton.addListener(new ChatiToolTip("In den Raum einladen"));
        } else if (user.canBeKicked()) {
            roomButton = new ChatiImageButton(Chati.CHATI.getDrawable("action_kick_user"));
            roomButton.addListener(new ChatiToolTip("Aus dem Raum entfernen"));
        } else {
            roomButton = new ChatiImageButton(Chati.CHATI.getDrawable("action_invite_user_disabled"));
            roomButton.setDisabled(true);
            roomButton.setTouchable(Touchable.disabled);
        }
        roomButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (user.canBeInvited()) {
                    new MessageWindow(AdministrativeAction.ROOM_INVITE).open();
                } else if (user.canBeKicked()) {
                    Chati.CHATI.send(ServerSender.SendAction.USER_MANAGE, user.getUserId(),
                            AdministrativeAction.ROOM_KICK, "");
                }
            }
        });

        ChatiImageButton teleportButton;
        if (user.canTeleportTo()) {
            teleportButton = new ChatiImageButton(Chati.CHATI.getDrawable("action_teleport_to_user"));
            teleportButton.addListener(new ChatiToolTip("Zu Benutzer teleportieren"));
        } else {
            teleportButton = new ChatiImageButton(Chati.CHATI.getDrawable("action_teleport_to_user_disabled"));
            teleportButton.setDisabled(true);
            teleportButton.setTouchable(Touchable.disabled);
        }
        teleportButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (user.canTeleportTo()) {
                    Chati.CHATI.send(ServerSender.SendAction.USER_MANAGE, user.getUserId(),
                            AdministrativeAction.TELEPORT_TO_USER, "");
                }
            }
        });

        ChatiImageButton reportButton;
        if (user.canBeReported()) {
            reportButton = new ChatiImageButton(Chati.CHATI.getDrawable("action_report_user"));
            reportButton.addListener(new ChatiToolTip("Melden"));
        } else {
            reportButton = new ChatiImageButton(Chati.CHATI.getDrawable("action_report_user_disabled"));
            reportButton.setDisabled(true);
            reportButton.setTouchable(Touchable.disabled);
        }
        reportButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (user.canBeReported()) {
                    new MessageWindow(AdministrativeAction.REPORT_USER).open();
                }
            }
        });

        ChatiImageButton muteButton;
        if (user.canBeMuted()) {
            if (!user.isMuted()) {
                muteButton = new ChatiImageButton(Chati.CHATI.getDrawable("action_mute_user"));
                muteButton.addListener(new ChatiToolTip("Stummschalten"));
            } else {
                muteButton = new ChatiImageButton(Chati.CHATI.getDrawable("action_unmute_user"));
                muteButton.addListener(new ChatiToolTip("Stummschalten aufheben"));
            }
        } else {
            muteButton = new ChatiImageButton(Chati.CHATI.getDrawable("action_mute_user_disabled"));
            muteButton.setDisabled(true);
            muteButton.setTouchable(Touchable.disabled);
        }
        muteButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (user.canBeMuted()) {
                    if (!user.isMuted()) {
                        Chati.CHATI.send(ServerSender.SendAction.USER_MANAGE, user.getUserId(),
                                AdministrativeAction.MUTE_USER, "");
                    } else {
                        Chati.CHATI.send(ServerSender.SendAction.USER_MANAGE, user.getUserId(),
                                AdministrativeAction.UNMUTE_USER, "");
                    }
                }
            }
        });

        ChatiImageButton banButton;
        if (user.canBeBanned()) {
            if (!user.isBanned()) {
                banButton = new ChatiImageButton(Chati.CHATI.getDrawable("action_ban_user"));
                banButton.addListener(new ChatiToolTip("Sperren"));
            } else {
                banButton = new ChatiImageButton(Chati.CHATI.getDrawable("action_unban_user"));
                banButton.addListener(new ChatiToolTip("Entperren"));
            }
        } else {
            banButton = new ChatiImageButton(Chati.CHATI.getDrawable("action_ban_user_disabled"));
            banButton.setDisabled(true);
            banButton.setTouchable(Touchable.disabled);
        }
        banButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (user.canBeBanned()) {
                    if (!user.isBanned()) {
                        new MessageWindow(AdministrativeAction.BAN_USER).open();
                    } else {
                        new MessageWindow(AdministrativeAction.UNBAN_USER).open();
                    }
                }
            }
        });

        ChatiImageButton moderatorButton;
        if (user.canAssignModerator()) {
            if (!user.hasRole(Role.MODERATOR)) {
                moderatorButton = new ChatiImageButton(Chati.CHATI.getDrawable("action_assign_moderator"));
                moderatorButton.addListener(new ChatiToolTip("Die Rolle des Moderators vergeben"));
            } else {
                moderatorButton = new ChatiImageButton(Chati.CHATI.getDrawable("action_withdraw_moderator"));
                moderatorButton.addListener(new ChatiToolTip("Die Rolle des Moderators entziehen"));
            }
        } else {
            moderatorButton = new ChatiImageButton(Chati.CHATI.getDrawable("action_assign_moderator_disabled"));
            moderatorButton.setDisabled(true);
            moderatorButton.setTouchable(Touchable.disabled);
        }
        moderatorButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (user.canAssignModerator()) {
                    if (!user.hasRole(Role.MODERATOR)) {
                        Chati.CHATI.send(ServerSender.SendAction.USER_MANAGE, user.getUserId(),
                                AdministrativeAction.ASSIGN_MODERATOR, "");
                    } else {
                        Chati.CHATI.send(ServerSender.SendAction.USER_MANAGE, user.getUserId(),
                                AdministrativeAction.WITHDRAW_MODERATOR, "");
                    }
                }
            }
        });

        // Layout
        NinePatchDrawable controlsBackground =
                new NinePatchDrawable(new NinePatch(Chati.CHATI.getSkin().getRegion("panel1"), 10, 10, 10, 10));
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

    private class MessageWindow extends ChatiWindow {

        private static final float WINDOW_WIDTH = 750;
        private static final float WINDOW_HEIGHT = 350;

        private final ChatiTextArea userMessageArea;

        protected MessageWindow(AdministrativeAction action) {
            super("");

            Label infoLabel = new Label("", Chati.CHATI.getSkin());

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

            ChatiTextButton confirmButton = new ChatiTextButton("Bestätigen", true);
            confirmButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    String message;
                    if (userMessageArea.isBlank()) {
                        message = "";
                    } else {
                        message = userMessageArea.getText();
                    }
                    Chati.CHATI.send(ServerSender.SendAction.USER_MANAGE, user.getUserId(), action, message);
                    close();
                }
            });

            ChatiTextButton cancelButton = new ChatiTextButton("Abbrechen", true);
            cancelButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    close();
                }
            });

            // Layout
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
            buttonContainer.defaults().colspan(2).height(ROW_HEIGHT).growX();
            buttonContainer.add(confirmButton).padRight(SPACING / 2);
            buttonContainer.add(cancelButton).padLeft(SPACING / 2);
            container.add(buttonContainer);
            add(container).padLeft(SPACING).padRight(SPACING).grow();
        }

        public void resetTextFields() {
            userMessageArea.reset();
        }
    }
}
