package view2.component.hud;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import model.MessageBundle;
import model.communication.message.MessageType;
import model.exception.UserNotFoundException;
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
    public static final float BUTTON_SPACING = 10;

    private static HeadUpDisplay headUpDisplay;

    private HudMenuWindow currentMenuWindow;
    private ChatWindow chatWindow;

    private Table internUserDisplay;

    private ImageButton userListButton;
    private NotificationButton notificationListButton;
    private ImageButton settingsButton;
    private ChatButton chatButton;

    private HeadUpDisplay() {
        create();
        setLayout();
    }

    @Override
    public void act(float delta) {
        IInternUserView internUser = Chati.CHATI.getUserManager().getInternUserView();
        if (Chati.CHATI.isUserInfoChanged() || Chati.CHATI.isWorldChanged()
                || Chati.CHATI.isRoomChanged()) {
            if (internUser != null && internUserDisplay == null) {
                internUserDisplay = new InternUserDisplay();
                addActor(internUserDisplay);
            } else if (internUser == null && internUserDisplay != null) {
                internUserDisplay.remove();
                internUserDisplay = null;
            }
            if (internUser != null && internUser.isInCurrentWorld() && !chatButton.isVisible()) {
                chatButton.setVisible(true);
            } else if ((internUser == null || !internUser.isInCurrentWorld()) && chatButton.isVisible()) {
                chatWindow.clearChat();
                hideChatWindow();
                chatButton.setVisible(false);
            }
        }
        if (Chati.CHATI.isUserNotificationChanged() && internUser != null && internUser.receivedNewNotification()) {
            notificationListButton.startAnimation();
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

        notificationListButton = new NotificationButton(Assets.NOTIFICATION_ICON);
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

        chatButton = new ChatButton(Assets.CHAT_ICON);
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
        userListButton.getImage().setOrigin(BUTTON_SIZE/ 2, BUTTON_SIZE / 2);
        notificationListButton.getImage().setOrigin(BUTTON_SIZE/ 2, BUTTON_SIZE / 2);
        settingsButton.getImage().setOrigin(BUTTON_SIZE/ 2, BUTTON_SIZE / 2);
        hudButtonContainer.add(userListButton, notificationListButton, settingsButton);
        addActor(hudButtonContainer);

        Table chatButtonContainer = new Table();
        chatButtonContainer.setFillParent(true);
        chatButtonContainer.bottom().right().defaults().size(BUTTON_SIZE);
        chatButton.getImage().setOrigin(BUTTON_SIZE/ 2, BUTTON_SIZE / 2);
        chatButtonContainer.add(chatButton);
        addActor(chatButtonContainer);
    }

    public void showTypingUser(UUID userId) {
        chatWindow.updateTypingUser(userId);
    }

    public void showChatMessage(UUID userId, LocalDateTime timestamp, MessageType messageType, String message,
                                MessageBundle messageBundle) throws UserNotFoundException {
        if (!isChatOpen()) {
            chatButton.startBlinking();
        }
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
        chatButton.stopBlinking();
        chatButton.setChecked(true);
        chatButton.getStyle().imageUp = Assets.CHECKED_CHAT_ICON;
        chatWindow.open();
    }

    public void hideChatWindow() {
        chatButton.setChecked(false);
        chatButton.getStyle().imageUp = Assets.CHAT_ICON;
        chatWindow.close();
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
        (currentMenuWindow = new UserListWindow()).open();
    }

    public void openNotificationMenu() {
        closeCurrentMenu();
        notificationListButton.setChecked(true);
        notificationListButton.getStyle().imageUp = Assets.CHECKED_NOTIFICATION_ICON;
        userListButton.getStyle().imageUp = Assets.USER_ICON;
        settingsButton.getStyle().imageUp = Assets.SETTINGS_ICON;
        (currentMenuWindow = new NotificationListWindow()).open();
    }

    public void openSettingsMenu() {
        closeCurrentMenu();
        settingsButton.setChecked(true);
        settingsButton.getStyle().imageUp = Assets.CHECKED_SETTINGS_ICON;
        userListButton.getStyle().imageUp = Assets.USER_ICON;
        notificationListButton.getStyle().imageUp = Assets.NOTIFICATION_ICON;
        (currentMenuWindow = new SettingsWindow()).open();
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
        currentMenuWindow.close();
        currentMenuWindow = null;
    }

    public ChatWindow getChatWindow() {
        return chatWindow;
    }

    public HudMenuWindow getCurrentMenuWindow() {
        return currentMenuWindow;
    }

    private static class ChatButton extends ImageButton {

        private static final float BLINKING_FREQUENCY = 2; // Pro Sekunde

        private boolean blinking;
        private boolean visible;
        private float delta;

        public ChatButton(Drawable imageUp) {
            super(imageUp);
        }

        @Override
        public void draw(Batch batch, float parentAlpha) {
            delta += Gdx.graphics.getDeltaTime();
            if (blinking) {
                if (delta >= 1 / BLINKING_FREQUENCY) {
                    delta = 0;
                    visible = !visible;
                }
                if (visible) {
                    super.draw(batch, parentAlpha);
                } else {
                    super.draw(batch, 0);
                }
            } else {
                super.draw(batch, parentAlpha);
            }
        }

        public void startBlinking() {
            blinking = true;
        }

        public void stopBlinking() {
            blinking = false;
        }
    }

    private static class NotificationButton extends ImageButton {

        private static final float DURATION = 2; // In Sekunden
        private static final float ANGLE = 15;
        private boolean isAnimating;

        public NotificationButton(Drawable imageUp) {
            super(imageUp);
        }

        public void startAnimation() {
            if (isAnimating) {
                return;
            }
            isAnimating = true;
            getImage().addAction(Actions.sequence(Actions.scaleBy(BUTTON_SCALE_FACTOR, BUTTON_SCALE_FACTOR, DURATION / 8),
                    Actions.rotateBy(ANGLE, DURATION / 8), Actions.rotateBy(-2 * ANGLE, DURATION / 4),
                    Actions.rotateBy(2 * ANGLE, DURATION / 4), Actions.rotateBy(-ANGLE, DURATION / 8),
                    Actions.scaleBy(-BUTTON_SCALE_FACTOR, -BUTTON_SCALE_FACTOR, DURATION / 8),
                    new Action() {
                        @Override
                        public boolean act(float delta) {
                            isAnimating = false;
                            return true;
                        }
                    }));
        }
    }
}
