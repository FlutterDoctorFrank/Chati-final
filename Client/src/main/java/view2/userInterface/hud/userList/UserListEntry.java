package view2.userInterface.hud.userList;

import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Align;
import controller.network.ServerSender;
import model.role.Role;
import model.user.AdministrativeAction;
import model.user.IUserView;
import org.jetbrains.annotations.NotNull;
import view2.Chati;
import view2.Response;
import view2.userInterface.ChatiImageButton;
import view2.userInterface.ChatiLabel;
import view2.userInterface.ChatiTextArea;
import view2.userInterface.ChatiTextButton;
import view2.userInterface.ChatiTooltip;
import view2.userInterface.ChatiWindow;
import view2.userInterface.UserInfoContainer;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Eine Klasse, welche einen Eintrag in der Benutzerliste des HeadUpDisplay repräsentiert.
 */
public class UserListEntry extends Table implements Comparable<UserListEntry> {

    private static final float BUTTON_SIZE = 30;
    private static final float ICON_SIZE = 22.5f;
    private static final float VERTICAL_SPACING = 5;
    private static final float HORIZONTAL_SPACING = 10;

    private final IUserView user;

    /**
     * Erzeugt eine neue Instanz des UserListEntry.
     * @param user Zum Eintrag zugehöriger Benutzer.
     */
    public UserListEntry(@NotNull final IUserView user) {
        this.user = user;

        UserInfoContainer userInfoContainer = new UserInfoContainer(user);

        Image statusImage = new Image();
        switch (user.getStatus()) {
            case ONLINE:
                statusImage.setDrawable(Chati.CHATI.getDrawable("status_online"));
                statusImage.addListener(new ChatiTooltip("hud.tooltip.online"));
                break;
            case AWAY:
                statusImage.setDrawable(Chati.CHATI.getDrawable("status_away"));
                statusImage.addListener(new ChatiTooltip("hud.tooltip.away"));
                break;
            case BUSY:
                statusImage.setDrawable(Chati.CHATI.getDrawable("status_busy"));
                statusImage.addListener(new ChatiTooltip("hud.tooltip.busy"));
                break;
            case INVISIBLE:
                statusImage.setDrawable(Chati.CHATI.getDrawable("status_invisible"));
                statusImage.addListener(new ChatiTooltip("hud.tooltip.invisible"));
                break;
            case OFFLINE:
                statusImage.setDrawable(Chati.CHATI.getDrawable("status_offline"));
                statusImage.addListener(new ChatiTooltip("hud.tooltip.offline"));
                break;
            default:
                Logger.getLogger("chati.view").log(Level.WARNING, "Tried to set an invalid user status " + user.getStatus());
        }

        Image currentRoomImage = new Image();
        if (user.isOnline() && user.getCurrentRoom() != null) {
            currentRoomImage.setDrawable(Chati.CHATI.getDrawable("current_room"));
            currentRoomImage.addListener(new ChatiTooltip("hud.tooltip.current-room", user.getCurrentRoom().getContextName()));
        }

        Image currentWorldImage = new Image();
        if (user.isOnline() && user.getCurrentWorld() != null) {
            currentWorldImage.setDrawable(Chati.CHATI.getDrawable("current_world"));
            currentWorldImage.addListener(new ChatiTooltip("hud.tooltip.current-world", user.getCurrentWorld().getContextName()));
        }

        ChatiImageButton whisperButton = new ChatiImageButton(Chati.CHATI.getDrawable("action_whisper_message"),
                Chati.CHATI.getDrawable("action_whisper_message"),
                Chati.CHATI.getDrawable("action_whisper_message_disabled"));
        whisperButton.addListener(new ChatiTooltip("hud.tooltip.whisper"));
        whisperButton.addListener(new ClickListener() {
            @Override
            public void clicked(@NotNull final InputEvent event, final float x, final float y) {
                if (user.canWhisper()) {
                    Chati.CHATI.getHeadUpDisplay().showChatWindow("\\\\" + user.getUsername() + " ");
                }
            }
        });
        if (!user.canWhisper()) {
            whisperButton.setDisabled(true);
            whisperButton.setTouchable(Touchable.disabled);
        }

        ChatiImageButton friendButton;
        if (!user.isFriend()) {
            friendButton = new ChatiImageButton(Chati.CHATI.getDrawable("action_add_friend"));
            friendButton.addListener(new ChatiTooltip("hud.tooltip.friend-add"));
        } else {
            friendButton = new ChatiImageButton(Chati.CHATI.getDrawable("action_remove_friend"));
            friendButton.addListener(new ChatiTooltip("hud.tooltip.friend-remove"));
        }
        friendButton.addListener(new ClickListener() {
            @Override
            public void clicked(@NotNull final InputEvent event, final float x, final float y) {
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
            ignoreButton.addListener(new ChatiTooltip("hud.tooltip.ignore"));
        } else {
            ignoreButton = new ChatiImageButton(Chati.CHATI.getDrawable("action_unignore_user"));
            ignoreButton.addListener(new ChatiTooltip("hud.tooltip.unignore"));
        }
        ignoreButton.addListener(new ClickListener() {
            @Override
            public void clicked(@NotNull final InputEvent event, final float x, final float y) {
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
            roomButton.addListener(new ChatiTooltip("hud.tooltip.room-invite"));
        } else if (user.canBeKicked()) {
            roomButton = new ChatiImageButton(Chati.CHATI.getDrawable("action_kick_user"));
            roomButton.addListener(new ChatiTooltip("hud.tooltip.room-kick"));
        } else {
            roomButton = new ChatiImageButton(Chati.CHATI.getDrawable("action_invite_user_disabled"));
            roomButton.setDisabled(true);
            roomButton.setTouchable(Touchable.disabled);
        }
        roomButton.addListener(new ClickListener() {
            @Override
            public void clicked(@NotNull final InputEvent event, final float x, final float y) {
                if (user.canBeInvited()) {
                    new MessageWindow(AdministrativeAction.ROOM_INVITE).open();
                } else if (user.canBeKicked()) {
                    Chati.CHATI.send(ServerSender.SendAction.USER_MANAGE, user.getUserId(),
                            AdministrativeAction.ROOM_KICK, "");
                }
            }
        });

        ChatiImageButton teleportButton = new ChatiImageButton(Chati.CHATI.getDrawable("action_teleport_to_user"),
                Chati.CHATI.getDrawable("action_teleport_to_user"),
                Chati.CHATI.getDrawable("action_teleport_to_user_disabled"));
        teleportButton.addListener(new ChatiTooltip("hud.tooltip.teleport-to"));
        teleportButton.addListener(new ClickListener() {
            @Override
            public void clicked(@NotNull final InputEvent event, final float x, final float y) {
                if (user.canTeleportTo()) {
                    if (!user.isInCurrentWorld()) {
                        Chati.CHATI.getMenuScreen().setPendingResponse(Response.JOIN_WORLD);
                    }
                    Chati.CHATI.send(ServerSender.SendAction.USER_MANAGE, user.getUserId(),
                            AdministrativeAction.TELEPORT_TO_USER, "");
                }
            }
        });
        if (!user.canTeleportTo()) {
            teleportButton.setDisabled(true);
            teleportButton.setTouchable(Touchable.disabled);
        }

        ChatiImageButton reportButton;
        if (user.canBeReported()) {
            reportButton = new ChatiImageButton(Chati.CHATI.getDrawable("action_report_user"));
            reportButton.addListener(new ChatiTooltip("hud.tooltip.report"));
        } else if (user.canBeBanned()) {
            reportButton = new ChatiImageButton(Chati.CHATI.getDrawable("action_report_user"));
            reportButton.addListener(new ChatiTooltip("hud.tooltip.warn"));
        } else {
            reportButton = new ChatiImageButton(Chati.CHATI.getDrawable("action_report_user_disabled"));
            reportButton.setDisabled(true);
            reportButton.setTouchable(Touchable.disabled);
        }
        reportButton.addListener(new ClickListener() {
            @Override
            public void clicked(@NotNull final InputEvent event, final float x, final float y) {
                if (user.canBeReported()) {
                    new MessageWindow(AdministrativeAction.REPORT_USER).open();
                } else if (user.canBeBanned()) {
                    new MessageWindow(AdministrativeAction.WARN_USER).open();
                }
            }
        });

        ChatiImageButton muteButton;
        if (user.canBeMuted()) {
            if (!user.isMuted()) {
                muteButton = new ChatiImageButton(Chati.CHATI.getDrawable("action_mute_user"));
                muteButton.addListener(new ChatiTooltip("hud.tooltip.mute"));
            } else {
                muteButton = new ChatiImageButton(Chati.CHATI.getDrawable("action_unmute_user"));
                muteButton.addListener(new ChatiTooltip("hud.tooltip.unmute"));
            }
        } else {
            muteButton = new ChatiImageButton(Chati.CHATI.getDrawable("action_mute_user_disabled"));
            muteButton.setDisabled(true);
            muteButton.setTouchable(Touchable.disabled);
        }
        muteButton.addListener(new ClickListener() {
            @Override
            public void clicked(@NotNull final InputEvent event, final float x, final float y) {
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
                banButton.addListener(new ChatiTooltip("hud.tooltip.ban"));
            } else {
                banButton = new ChatiImageButton(Chati.CHATI.getDrawable("action_unban_user"));
                banButton.addListener(new ChatiTooltip("hud.tooltip.unban"));
            }
        } else {
            banButton = new ChatiImageButton(Chati.CHATI.getDrawable("action_ban_user_disabled"));
            banButton.setDisabled(true);
            banButton.setTouchable(Touchable.disabled);
        }
        banButton.addListener(new ClickListener() {
            @Override
            public void clicked(@NotNull final InputEvent event, final float x, final float y) {
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
                moderatorButton.addListener(new ChatiTooltip("hud.tooltip.moderator-assign"));
            } else {
                moderatorButton = new ChatiImageButton(Chati.CHATI.getDrawable("action_withdraw_moderator"));
                moderatorButton.addListener(new ChatiTooltip("hud.tooltip.moderator-withdraw"));
            }
        } else {
            moderatorButton = new ChatiImageButton(Chati.CHATI.getDrawable("action_assign_moderator_disabled"));
            moderatorButton.setDisabled(true);
            moderatorButton.setTouchable(Touchable.disabled);
        }
        moderatorButton.addListener(new ClickListener() {
            @Override
            public void clicked(@NotNull final InputEvent event, final float x, final float y) {
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
        NinePatchDrawable background =
                new NinePatchDrawable(new NinePatch(Chati.CHATI.getSkin().getRegion("panel1"), 10, 10, 10, 10));
        setBackground(background);
        left().defaults().padTop(VERTICAL_SPACING);

        Table container = new Table();
        container.add(statusImage).left().size(ICON_SIZE).padRight(HORIZONTAL_SPACING);
        container.add(userInfoContainer).left();
        container.add(currentRoomImage).right().size(ICON_SIZE).padLeft(HORIZONTAL_SPACING).padRight(HORIZONTAL_SPACING).growX();
        container.add(currentWorldImage).right().size(ICON_SIZE).padLeft(HORIZONTAL_SPACING).padRight(HORIZONTAL_SPACING);
        add(container).left().padLeft(HORIZONTAL_SPACING).spaceBottom(VERTICAL_SPACING).height(BUTTON_SIZE).growX().row();

        Table buttonContainer = new Table();
        buttonContainer.defaults().size(BUTTON_SIZE).padBottom(VERTICAL_SPACING).growX();
        buttonContainer.add(whisperButton, friendButton, ignoreButton, roomButton, teleportButton, reportButton,
                muteButton, banButton, moderatorButton);
        add(buttonContainer).center().growX();
    }

    /**
     * Gibt den Benutzer dieses Eintrags zurück.
     * @return Benutzer dieses Eintrags.
     */
    public @NotNull IUserView getUser() {
        return user;
    }

    @Override
    public int compareTo(@NotNull final UserListEntry other) {
        int statusComp = this.user.getStatus().compareTo(other.user.getStatus());
        Role thisHighestRole = this.user.getHighestRole();
        Role otherHighestRole = other.user.getHighestRole();
        int roleComp = thisHighestRole == otherHighestRole ? 0
                : (thisHighestRole == null ? 1
                : (otherHighestRole == null ? -1
                : thisHighestRole.compareTo(otherHighestRole)));
        int nameComp = this.user.getUsername().compareTo(other.user.getUsername());

        return statusComp != 0 ? statusComp
                : (roleComp != 0 ? roleComp
                : nameComp);
    }

    /**
     * Eine Klasse, welches ein Bestätigungsmenü zur Durchführung einer administrativen Benutzeraktion repräsentiert.
     */
    private class MessageWindow extends ChatiWindow {

        private static final float WINDOW_WIDTH = 750;
        private static final float WINDOW_HEIGHT = 350;

        private final ChatiTextArea userMessageArea;

        /**
         * Erzeugt eine neue Instanz des MessageWindow.
         * @param action Durchzuführende administrative Aktion.
         */
        protected MessageWindow(@NotNull final AdministrativeAction action) {
            super("", WINDOW_WIDTH, WINDOW_HEIGHT);

            switch(action) {
                case INVITE_FRIEND:
                    titleKey = "window.title.add-friend";
                    infoLabel = new ChatiLabel("window.entry.add-friend");
                    break;
                case REPORT_USER:
                    titleKey = "window.title.report";
                    infoLabel = new ChatiLabel("window.entry.report");
                    break;
                case WARN_USER:
                    titleKey = "window.title.warn";
                    infoLabel = new ChatiLabel("window.entry.warn");
                    break;
                case BAN_USER:
                    titleKey = "window.title.ban";
                    infoLabel = new ChatiLabel("window.entry.ban");
                    break;
                case UNBAN_USER:
                    titleKey = "window.title.unban";
                    infoLabel = new ChatiLabel("window.entry.unban");
                    break;
                case ROOM_INVITE:
                    titleKey = "window.title.invite";
                    infoLabel = new ChatiLabel("window.entry.invite");
            }

            if (!titleKey.isBlank()) {
                getTitleLabel().setText(Chati.CHATI.getLocalization().translate(titleKey));
            }

            userMessageArea = new ChatiTextArea("menu.text-field.message", true);

            ChatiTextButton confirmButton = new ChatiTextButton("menu.button.confirm", true);
            confirmButton.addListener(new ClickListener() {
                @Override
                public void clicked(@NotNull final InputEvent event, final float x, final float y) {
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

            ChatiTextButton cancelButton = new ChatiTextButton("menu.button.cancel", true);
            cancelButton.addListener(new ClickListener() {
                @Override
                public void clicked(@NotNull final InputEvent event, final float x, final float y) {
                    close();
                }
            });

            // Layout
            setModal(true);
            setMovable(false);

            Table container = new Table();
            container.defaults().height(ROW_HEIGHT).spaceBottom(SPACE).center().growX();
            infoLabel.setAlignment(Align.center, Align.center);
            infoLabel.setWrap(true);
            container.add(infoLabel).row();
            container.add(userMessageArea).height(2 * ROW_HEIGHT).row();
            Table buttonContainer = new Table();
            buttonContainer.defaults().colspan(2).height(ROW_HEIGHT).growX();
            buttonContainer.add(confirmButton).padRight(SPACE / 2);
            buttonContainer.add(cancelButton).padLeft(SPACE / 2);
            container.add(buttonContainer);
            add(container).padLeft(SPACE).padRight(SPACE).grow();

            // Translatable register
            translatables.add(infoLabel);
            translatables.add(userMessageArea);
            translatables.add(confirmButton);
            translatables.add(cancelButton);
            translatables.trimToSize();
        }

        public void resetTextFields() {
            userMessageArea.reset();
        }

        @Override
        public void focus() {
        }
    }
}
