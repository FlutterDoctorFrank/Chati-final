package view2.component.hud;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import model.MessageBundle;
import model.communication.message.MessageType;
import model.user.IInternUserView;
import view2.Chati;
import view2.Assets;
import view2.component.hud.notificationList.NotificationListWindow;
import view2.component.hud.settings.SettingsWindow;
import view2.component.hud.userList.UserListWindow;

import java.time.LocalDateTime;
import java.util.UUID;

public class HeadUpDisplay extends Table {

    public static final float BUTTON_SCALE_FACTOR = 0.1f;
    public static final float BUTTON_SIZE = 75;
    public static final float BUTTON_SPACING = 10f;
    public static final float HUD_MENU_TABLE_WIDTH = 450;
    public static final float HUD_MENU_TABLE_HEIGHT = 600;

    private static HeadUpDisplay headUpDisplay;

    private HudMenuWindow currentMenuWindow;
    private ChatWindow chatWindow;

    private Table internUserDisplay;

    private ImageButton userListButton;
    private ImageButton notificationListButton;
    private ImageButton settingsButton;
    private ImageButton chatButton;

    private HeadUpDisplay() {
        create();
        setLayout();
    }

    @Override
    public void act(float delta) {
        if (Chati.CHATI.isUserInfoChanged() || Chati.CHATI.isWorldChanged()
                || Chati.CHATI.isRoomChanged()) {
            IInternUserView internUser = Chati.CHATI.getUserManager().getInternUserView();
            if (internUser != null && internUserDisplay == null) {
                internUserDisplay = new InternUserDisplay();
                add(internUserDisplay).top().left().width(HUD_MENU_TABLE_WIDTH).grow();
            } else if (internUser == null && internUserDisplay != null) {
                internUserDisplay.remove();
            }
            if (internUser != null && internUser.isInCurrentWorld() && !chatButton.isVisible()) {
                chatButton.setVisible(true);
            } else if ((internUser == null || !internUser.isInCurrentWorld()) && chatButton.isVisible()) {
                chatWindow.clearChat();
                hideChatWindow();
                chatButton.setVisible(false);
            }
        }
        super.act(delta);
    }

    protected void create() {
        this.chatWindow = new ChatWindow();

        userListButton = new ImageButton(Assets.USER_ICON);
        userListButton.getImage().scaleBy(-BUTTON_SCALE_FACTOR);
        userListButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if (userListButton.isChecked()) {
                    openUserMenu();
                } else {
                    closeCurrentMenu();
                }
            }
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if (pointer == -1) {
                    userListButton.getImage().scaleBy(BUTTON_SCALE_FACTOR);
                }
            }
            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if (pointer == -1) {
                    userListButton.getImage().scaleBy(-BUTTON_SCALE_FACTOR);
                }
            }
        });

        notificationListButton = new ImageButton(Assets.NOTIFICATION_ICON);
        notificationListButton.getImage().scaleBy(-BUTTON_SCALE_FACTOR);
        notificationListButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if (notificationListButton.isChecked()) {
                    openNotificationMenu();
                } else {
                    closeCurrentMenu();
                }
            }
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if (pointer == -1) {
                    notificationListButton.getImage().scaleBy(BUTTON_SCALE_FACTOR);
                }
            }
            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if (pointer == -1) {
                    notificationListButton.getImage().scaleBy(-BUTTON_SCALE_FACTOR);
                }
            }
        });

        settingsButton = new ImageButton(Assets.SETTINGS_ICON);
        settingsButton.getImage().scaleBy(-BUTTON_SCALE_FACTOR);
        settingsButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if (settingsButton.isChecked()) {
                    openSettingsMenu();
                } else {
                    closeCurrentMenu();
                }
            }
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if (pointer == -1) {
                    settingsButton.getImage().scaleBy(BUTTON_SCALE_FACTOR);
                }
            }
            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if (pointer == -1) {
                    settingsButton.getImage().scaleBy(-BUTTON_SCALE_FACTOR);
                }
            }
        });

        chatButton = new ImageButton(Assets.CHAT_ICON);
        chatButton.getImage().scaleBy(-BUTTON_SCALE_FACTOR);
        chatButton.setVisible(false);
        chatButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if (chatButton.isChecked()) {
                    showChatWindow();
                } else {
                    hideChatWindow();
                }
            }
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if (pointer == -1) {
                    chatButton.getImage().scaleBy(BUTTON_SCALE_FACTOR);
                }
            }
            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if (pointer == -1) {
                    chatButton.getImage().scaleBy(-BUTTON_SCALE_FACTOR);
                }
            }
        });

        ButtonGroup<ImageButton> hudButtons = new ButtonGroup<>();
        hudButtons.setMinCheckCount(0);
        hudButtons.setMaxCheckCount(1);
        hudButtons.setUncheckLast(true);
        hudButtons.add(userListButton);
        hudButtons.add(notificationListButton);
        hudButtons.add(settingsButton);
    }

    private void setLayout() {
        setFillParent(true);

        Table hudButtonContainer = new Table();
        hudButtonContainer.setFillParent(true);
        hudButtonContainer.top().right().defaults().size(BUTTON_SIZE).space(BUTTON_SPACING);
        hudButtonContainer.add(userListButton, notificationListButton, settingsButton);
        addActor(hudButtonContainer);

        Table chatButtonContainer = new Table();
        chatButtonContainer.setFillParent(true);
        chatButtonContainer.bottom().right().defaults().size(BUTTON_SIZE);
        chatButtonContainer.add(chatButton);
        addActor(chatButtonContainer);
    }

    public void showChatMessage(UUID userId, LocalDateTime timestamp, MessageType messageType, String message,
                                MessageBundle messageBundle) {
        if (messageType == MessageType.INFO) {
            chatWindow.showInfoMessage(timestamp, messageBundle);
        } else {
            chatWindow.showUserMessage(userId, timestamp, messageType, message);
        }
    }

    public void showChatWindow() {
        if (!chatButton.isVisible()) {
            return;
        }
        chatButton.setChecked(true);
        chatButton.getStyle().imageUp = Assets.CHECKED_CHAT_ICON;
        chatWindow.setVisible(true);
        chatWindow.focus();
    }

    public void hideChatWindow() {
        chatButton.setChecked(false);
        chatButton.getStyle().imageUp = Assets.CHAT_ICON;
        chatWindow.setVisible(false);
        getStage().unfocus(chatWindow);
    }

    public boolean isChatOpen() {
        return chatButton.isVisible() && chatWindow.isVisible();
    }

    public boolean isMenuOpen() {
        return currentMenuWindow != null;
    }

    public static HeadUpDisplay getInstance() {
        if (headUpDisplay == null) {
            headUpDisplay = new HeadUpDisplay();
        }
        return headUpDisplay;
    }

    public boolean isUserMenuOpen() {
        return userListButton.isChecked();
    }

    public boolean isNotificationMenuOpen() {
        return notificationListButton.isChecked();
    }

    public boolean isSettingsMenuOpen() {
        return settingsButton.isChecked();
    }

    public void openUserMenu() {
        closeCurrentMenu();
        userListButton.setChecked(true);
        userListButton.getStyle().imageUp = Assets.CHECKED_USER_ICON;
        notificationListButton.getStyle().imageUp = Assets.NOTIFICATION_ICON;
        settingsButton.getStyle().imageUp = Assets.SETTINGS_ICON;
        currentMenuWindow = new UserListWindow();
        Chati.CHATI.getScreen().getStage().addActor(currentMenuWindow);
    }

    public void openNotificationMenu() {
        closeCurrentMenu();
        notificationListButton.setChecked(true);
        notificationListButton.getStyle().imageUp = Assets.CHECKED_NOTIFICATION_ICON;
        userListButton.getStyle().imageUp = Assets.USER_ICON;
        settingsButton.getStyle().imageUp = Assets.SETTINGS_ICON;
        currentMenuWindow = new NotificationListWindow();
        Chati.CHATI.getScreen().getStage().addActor(currentMenuWindow);
    }

    public void openSettingsMenu() {
        closeCurrentMenu();
        settingsButton.setChecked(true);
        settingsButton.getStyle().imageUp = Assets.CHECKED_SETTINGS_ICON;
        userListButton.getStyle().imageUp = Assets.USER_ICON;
        notificationListButton.getStyle().imageUp = Assets.NOTIFICATION_ICON;
        currentMenuWindow = new SettingsWindow();
        Chati.CHATI.getScreen().getStage().addActor(currentMenuWindow);
    }

    public void closeCurrentMenu() {
        if (currentMenuWindow == null) {
            return;
        }
        userListButton.setChecked(false);
        notificationListButton.setChecked(false);
        settingsButton.setChecked(false);
        userListButton.getStyle().imageUp = Assets.USER_ICON;
        notificationListButton.getStyle().imageUp = Assets.NOTIFICATION_ICON;
        settingsButton.getStyle().imageUp = Assets.SETTINGS_ICON;
        currentMenuWindow.remove();
        currentMenuWindow = null;
    }

    public ChatWindow getChatWindow() {
        return chatWindow;
    }


}
