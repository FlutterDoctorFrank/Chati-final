package view2.userInterface.hud;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Action;
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
import view2.userInterface.ChatiImageButton;
import view2.userInterface.hud.notificationList.NotificationListWindow;
import view2.userInterface.hud.settings.SettingsWindow;
import view2.userInterface.hud.userList.UserListWindow;

import java.time.LocalDateTime;
import java.util.UUID;

public class HeadUpDisplay extends Table {

    public static final float BUTTON_SCALE_FACTOR = 0.1f;
    public static final float BUTTON_SIZE = 75;
    public static final float BUTTON_SPACING = 10;

    private static HeadUpDisplay headUpDisplay;

    private final ChatWindow chatWindow;
    private final ImageButton userListButton;
    private final NotificationButton notificationListButton;
    private final ImageButton settingsButton;
    private final ChatButton chatButton;
    private final ImageButton communicationButton;
    private final ImageButton microphoneButton;
    private final ImageButton soundButton;

    private HudMenuWindow currentMenuWindow;
    private CommunicationWindow communicationWindow;
    private Table internUserDisplay;

    private HeadUpDisplay() {
        this.chatWindow = new ChatWindow();

        userListButton = new ChatiImageButton(Chati.CHATI.getDrawable("user_menu_closed"), Chati.CHATI.getDrawable("user_menu_open"));
        userListButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (userListButton.isChecked()) {
                    openUserMenu();
                } else {
                    closeCurrentMenu();
                }
            }
        });

        notificationListButton = new NotificationButton(Chati.CHATI.getDrawable("notification_menu_closed"),
                Chati.CHATI.getDrawable("notification_menu_open"));
        notificationListButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (notificationListButton.isChecked()) {
                    openNotificationMenu();
                } else {
                    closeCurrentMenu();
                }
            }
        });

        settingsButton = new ChatiImageButton(Chati.CHATI.getDrawable("settings_menu_closed"),
                Chati.CHATI.getDrawable("settings_menu_open"));
        settingsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (settingsButton.isChecked()) {
                    openSettingsMenu();
                } else {
                    closeCurrentMenu();
                }
            }
        });

        chatButton = new ChatButton(Chati.CHATI.getDrawable("chat_closed"), Chati.CHATI.getDrawable("chat_open"));
        chatButton.setVisible(false);
        chatButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (chatButton.isChecked()) {
                    showChatWindow();
                } else {
                    hideChatWindow();
                }
            }
        });

        communicationButton = new ChatiImageButton(Chati.CHATI.getDrawable("communicable_list_closed"),
                Chati.CHATI.getDrawable("communicable_list_open"));
        communicationButton.setVisible(false);
        communicationButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (communicationButton.isChecked()) {
                    openCommunicationWindow();
                } else {
                    closeCommunicationWindow();
                }
            }
        });

        microphoneButton = new ChatiImageButton(Chati.CHATI.getDrawable("microphone_off"),
                Chati.CHATI.getDrawable("microphone_on"));
        microphoneButton.setVisible(false);
        microphoneButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Chati.CHATI.getPreferences().setMicrophoneOn(microphoneButton.isChecked());
            }
        });

        soundButton = new ChatiImageButton(Chati.CHATI.getDrawable("sound_off"), Chati.CHATI.getDrawable("sound_on"));
        soundButton.setVisible(false);
        soundButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Chati.CHATI.getPreferences().setSoundOn(soundButton.isChecked());
            }
        });

        ButtonGroup<ImageButton> hudMenuButtonGroup = new ButtonGroup<>();
        hudMenuButtonGroup.setMinCheckCount(0);
        hudMenuButtonGroup.setMaxCheckCount(1);
        hudMenuButtonGroup.setUncheckLast(true);
        hudMenuButtonGroup.add(userListButton);
        hudMenuButtonGroup.add(notificationListButton);
        hudMenuButtonGroup.add(settingsButton);

        // Layout
        setFillParent(true);

        Table topRightButtonContainer = new Table();
        topRightButtonContainer.setFillParent(true);
        topRightButtonContainer.top().right().defaults().size(BUTTON_SIZE).space(BUTTON_SPACING);
        userListButton.getImage().setOrigin(BUTTON_SIZE / 2, BUTTON_SIZE / 2);
        notificationListButton.getImage().setOrigin(BUTTON_SIZE/ 2, BUTTON_SIZE / 2);
        settingsButton.getImage().setOrigin(BUTTON_SIZE / 2, BUTTON_SIZE / 2);
        topRightButtonContainer.add(userListButton, notificationListButton, settingsButton);
        addActor(topRightButtonContainer);

        Table bottomLeftButtonContainer = new Table();
        bottomLeftButtonContainer.setFillParent(true);
        bottomLeftButtonContainer.bottom().left().defaults().size(BUTTON_SIZE).spaceBottom(BUTTON_SPACING);
        communicationButton.getImage().setOrigin(BUTTON_SIZE / 2, BUTTON_SIZE / 2);
        microphoneButton.getImage().setOrigin(BUTTON_SIZE / 2, BUTTON_SIZE / 2);
        soundButton.getImage().setOrigin(BUTTON_SIZE / 2, BUTTON_SIZE / 2);
        bottomLeftButtonContainer.add(communicationButton, microphoneButton, soundButton);
        addActor(bottomLeftButtonContainer);

        Table chatButtonContainer = new Table();
        chatButtonContainer.setFillParent(true);
        chatButtonContainer.bottom().right().defaults().size(BUTTON_SIZE);
        chatButton.getImage().setOrigin(BUTTON_SIZE/ 2, BUTTON_SIZE / 2);
        chatButtonContainer.add(chatButton);
        addActor(chatButtonContainer);
    }

    @Override
    public void act(float delta) {
        IInternUserView internUser = Chati.CHATI.getInternUser();

        if (Chati.CHATI.isUserInfoChanged() || Chati.CHATI.isWorldChanged() || Chati.CHATI.isRoomChanged()) {
            if (internUser != null && internUserDisplay == null) {
                internUserDisplay = new InternUserDisplay();
                addActor(internUserDisplay);
            } else if (internUser == null && internUserDisplay != null) {
                internUserDisplay.remove();
                internUserDisplay = null;
            }

            if (internUser != null && internUser.isInCurrentWorld()) {
                if (!chatButton.isVisible() || !communicationButton.isVisible() || !microphoneButton.isVisible()
                    || !soundButton.isVisible()) {
                    chatButton.setVisible(true);
                    communicationButton.setVisible(true);
                    microphoneButton.setVisible(true);
                    soundButton.setVisible(true);
                }
            } else if ((internUser == null || !internUser.isInCurrentWorld())) {
                if (chatButton.isVisible() || communicationButton.isVisible() || microphoneButton.isVisible()
                    || soundButton.isVisible()) {
                    chatWindow.clearChat();
                    hideChatWindow();
                    chatButton.setVisible(false);
                    communicationButton.setVisible(false);
                    microphoneButton.setVisible(false);
                    soundButton.setVisible(false);
                }
            }
        }

        if (Chati.CHATI.isNewNotificationReceived()) {
            notificationListButton.startAnimation();
        }
        super.act(delta);
    }

    public void openUserMenu() {
        closeCurrentMenu();
        userListButton.setChecked(true);
        currentMenuWindow = new UserListWindow();
        currentMenuWindow.open();
    }

    public void openNotificationMenu() {
        closeCurrentMenu();
        notificationListButton.setChecked(true);
        currentMenuWindow = new NotificationListWindow();
        currentMenuWindow.open();
    }

    public void openSettingsMenu() {
        closeCurrentMenu();
        settingsButton.setChecked(true);
        currentMenuWindow = new SettingsWindow();
        currentMenuWindow.open();
    }

    public void closeCurrentMenu() {
        if (currentMenuWindow != null) {
            currentMenuWindow.close();
            removeCurrentMenu();
        }
    }

    public void removeCurrentMenu() {
        userListButton.setChecked(false);
        notificationListButton.setChecked(false);
        settingsButton.setChecked(false);
        currentMenuWindow = null;
    }

    public void openCommunicationWindow() {
        communicationButton.setChecked(true);
        (communicationWindow = new CommunicationWindow()).open();
    }

    public void closeCommunicationWindow() {
        if (communicationWindow != null) {
            communicationWindow.close();
            removeCommunicationWindow();
        }
    }

    public void removeCommunicationWindow() {
        communicationButton.setChecked(false);
        communicationWindow = null;
    }

    public void toggleMicrophone() {
        microphoneButton.setChecked(!microphoneButton.isChecked());
        Chati.CHATI.getPreferences().setMicrophoneOn(microphoneButton.isChecked());
    }

    public void toggleSound() {
        soundButton.setChecked(!soundButton.isChecked());
        Chati.CHATI.getPreferences().setSoundOn(soundButton.isChecked());
    }

    public void showChatWindow() {
        if (isChatOpen()) {
            return;
        }
        chatButton.stopBlinking();
        chatButton.setChecked(true);
        chatWindow.show();
    }

    public void hideChatWindow() {
        chatButton.setChecked(false);
        chatWindow.hide();
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

    public boolean isMenuOpen() {
        return currentMenuWindow != null;
    }

    public boolean isUserMenuOpen() {
        return isMenuOpen() && userListButton.isChecked();
    }

    public boolean isNotificationMenuOpen() {
        return isMenuOpen() && notificationListButton.isChecked();
    }

    public boolean isSettingsMenuOpen() {
        return isMenuOpen() && settingsButton.isChecked();
    }

    public boolean isCommunicationWindowOpen() {
        return communicationWindow != null && communicationButton.isChecked();
    }

    public boolean isChatOpen() {
        return chatButton.isVisible() && chatWindow.isVisible();
    }

    public ChatWindow getChatWindow() {
        return chatWindow;
    }

    public static HeadUpDisplay getInstance() {
        if (headUpDisplay == null) {
            headUpDisplay = new HeadUpDisplay();
        }
        return headUpDisplay;
    }

    private static class ChatButton extends ChatiImageButton {

        private static final float BLINKING_FREQUENCY = 2; // Pro Sekunde

        private boolean blinking;
        private boolean visible;
        private float delta;

        public ChatButton(Drawable imageUnchecked, Drawable imageChecked) {
            super(imageUnchecked, imageChecked);
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

    private static class NotificationButton extends ChatiImageButton {

        private static final float DURATION = 2; // In Sekunden
        private static final float ANGLE = 15;
        private boolean isAnimating;

        public NotificationButton(Drawable imageUnchecked, Drawable imageChecked) {
            super(imageUnchecked, imageChecked);
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