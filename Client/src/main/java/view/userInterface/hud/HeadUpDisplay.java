package view.userInterface.hud;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import model.MessageBundle;
import model.communication.message.MessageType;
import model.exception.UserNotFoundException;
import model.user.IInternUserView;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import view.Chati;
import view.ChatiLocalization.Translatable;
import view.userInterface.ChatiImageButton;
import view.userInterface.hud.notificationList.NotificationListWindow;
import view.userInterface.hud.settings.SettingsWindow;
import view.userInterface.hud.userList.UserListWindow;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Eine Klasse, welche das HeadUpDisplay der Anwendung repräsentiert.
 */
public class HeadUpDisplay extends Table implements Translatable {

    public static final float BUTTON_SCALE_FACTOR = 0.1f;
    public static final float BUTTON_SIZE = 75;
    public static final float BUTTON_SPACING = 10;

    private final ChatWindow chatWindow;
    private final VideoChatWindow videoChatWindow;
    private final ImageButton userListButton;
    private final NotificationButton notificationListButton;
    private final ImageButton settingsButton;
    private final ChatButton chatButton;
    private final ChatButton videoChatButton;
    private final ImageButton communicationButton;
    private final ImageButton soundButton;
    private final ImageButton microphoneButton;
    private final ImageButton cameraButton;

    private HudMenuWindow currentMenuWindow;
    private CommunicationWindow communicationWindow;
    private Table internUserDisplay;

    private boolean restoreChat;
    private boolean restoreVideoChat;

    /**
     * Erzeugt eine neue Instanz des HeadUpDisplay.
     */
    public HeadUpDisplay() {
        this.chatWindow = new ChatWindow();
        this.videoChatWindow = new VideoChatWindow();

        userListButton = new ChatiImageButton(Chati.CHATI.getDrawable("user_menu_closed"),
                Chati.CHATI.getDrawable("user_menu_open"));
        userListButton.addListener(new ClickListener() {
            @Override
            public void clicked(@NotNull final InputEvent event, final float x, final float y) {
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
            public void clicked(@NotNull final InputEvent event, final float x, final float y) {
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
            public void clicked(@NotNull final InputEvent event, final float x, final float y) {
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
            public void clicked(@NotNull final InputEvent event, final float x, final float y) {
                if (chatButton.isChecked()) {
                    showChatWindow();
                } else {
                    hideChatWindow();
                }
            }
        });

        videoChatButton = new ChatButton(Chati.CHATI.getDrawable("videochat_closed"),
                Chati.CHATI.getDrawable("videochat_open"));
        videoChatButton.setVisible(false);
        videoChatButton.addListener(new ClickListener() {
            @Override
            public void clicked(@NotNull final InputEvent event, final float x, final float y) {
                if (videoChatButton.isChecked()) {
                    showVideoChatWindow();
                } else {
                    hideVideoChatWindow();
                }
            }
        });

        communicationButton = new ChatiImageButton(Chati.CHATI.getDrawable("communicable_list_closed"),
                Chati.CHATI.getDrawable("communicable_list_open"));
        communicationButton.setVisible(false);
        communicationButton.addListener(new ClickListener() {
            @Override
            public void clicked(@NotNull final InputEvent event, final float x, final float y) {
                if (communicationButton.isChecked()) {
                    openCommunicationWindow();
                } else {
                    closeCommunicationWindow();
                }
            }
        });

        soundButton = new ChatiImageButton(Chati.CHATI.getDrawable("sound_off"), Chati.CHATI.getDrawable("sound_on"));
        soundButton.setChecked(Chati.CHATI.getPreferences().isSoundOn());
        soundButton.setVisible(false);
        soundButton.addListener(new ClickListener() {
            @Override
            public void clicked(@NotNull final InputEvent event, final float x, final float y) {
                Chati.CHATI.getPreferences().setSoundOn(soundButton.isChecked());
            }
        });

        microphoneButton = new ChatiImageButton(Chati.CHATI.getDrawable("microphone_off"),
                Chati.CHATI.getDrawable("microphone_on"));
        microphoneButton.setChecked(Chati.CHATI.getPreferences().isMicrophoneOn());
        microphoneButton.setVisible(false);
        microphoneButton.addListener(new ClickListener() {
            @Override
            public void clicked(@NotNull final InputEvent event, final float x, final float y) {
                Chati.CHATI.getPreferences().setMicrophoneOn(microphoneButton.isChecked());
            }
        });

        cameraButton = new ChatiImageButton(Chati.CHATI.getDrawable("camera_off"), Chati.CHATI.getDrawable("camera_on"));
        cameraButton.setChecked(Chati.CHATI.getPreferences().isCameraOn());
        cameraButton.setVisible(false);
        cameraButton.addListener(new ClickListener() {
            @Override
            public void clicked(@NotNull final InputEvent event, final float x, final float y) {
                Chati.CHATI.getPreferences().setCameraOn(cameraButton.isChecked());
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

        // Oben rechts: Benutzerliste, Benachrichtigungen, Einstellungen
        Table topRightButtonContainer = new Table();
        topRightButtonContainer.setFillParent(true);
        topRightButtonContainer.top().right().defaults().size(BUTTON_SIZE).space(BUTTON_SPACING);
        userListButton.getImage().setOrigin(BUTTON_SIZE / 2, BUTTON_SIZE / 2);
        notificationListButton.getImage().setOrigin(BUTTON_SIZE/ 2, BUTTON_SIZE / 2);
        settingsButton.getImage().setOrigin(BUTTON_SIZE / 2, BUTTON_SIZE / 2);
        topRightButtonContainer.add(userListButton, notificationListButton, settingsButton);
        addActor(topRightButtonContainer);

        // Unten links: Kommunikation, Ton, Mikrophon, Kamera
        Table bottomLeftButtonContainer = new Table();
        bottomLeftButtonContainer.setFillParent(true);
        bottomLeftButtonContainer.bottom().left().defaults().size(BUTTON_SIZE).spaceBottom(BUTTON_SPACING);
        communicationButton.getImage().setOrigin(BUTTON_SIZE / 2, BUTTON_SIZE / 2);
        soundButton.getImage().setOrigin(BUTTON_SIZE / 2, BUTTON_SIZE / 2);
        microphoneButton.getImage().setOrigin(BUTTON_SIZE / 2, BUTTON_SIZE / 2);
        cameraButton.getImage().setOrigin(BUTTON_SIZE / 2, BUTTON_SIZE / 2);
        bottomLeftButtonContainer.add(communicationButton, soundButton, microphoneButton, cameraButton);
        addActor(bottomLeftButtonContainer);

        // Unten rechts: Chat, Videochat
        Table bottomRightButtonContainer = new Table();
        bottomRightButtonContainer.setFillParent(true);
        bottomRightButtonContainer.bottom().right().defaults().size(BUTTON_SIZE);
        chatButton.getImage().setOrigin(BUTTON_SIZE / 2, BUTTON_SIZE / 2);
        videoChatButton.getImage().setOrigin(BUTTON_SIZE / 2, BUTTON_SIZE / 2);
        bottomRightButtonContainer.add(videoChatButton, chatButton);
        addActor(bottomRightButtonContainer);
    }

    @Override
    public void act(final float delta) {
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
                if (!chatButton.isVisible() || !videoChatButton.isVisible() || !communicationButton.isVisible()
                        || !soundButton.isVisible() || !microphoneButton.isVisible() || !cameraButton.isVisible()) {
                    chatButton.setVisible(true);
                    videoChatButton.setVisible(true);
                    communicationButton.setVisible(true);
                    soundButton.setVisible(true);
                    microphoneButton.setVisible(true);
                    cameraButton.setVisible(true);
                }
            } else if ((internUser == null || !internUser.isInCurrentWorld())) {
                if (chatButton.isVisible() || videoChatButton.isVisible() || communicationButton.isVisible()
                        || soundButton.isVisible() || microphoneButton.isVisible() || cameraButton.isVisible()) {
                    chatWindow.clearChat();
                    videoChatWindow.clearVideochat();
                    hideChatWindow();
                    hideVideoChatWindow();
                    chatButton.setVisible(false);
                    videoChatButton.setVisible(false);
                    communicationButton.setVisible(false);
                    soundButton.setVisible(false);
                    microphoneButton.setVisible(false);
                    cameraButton.setVisible(false);
                }
            }
        }

        if (Chati.CHATI.isNewNotificationReceived()) {
            notificationListButton.startAnimation();
            Chati.CHATI.getMultimediaManager().playSound("notification_sound");
        }
        super.act(delta);
    }

    public void resize(final int width, final int height) {
        if (width <= 0 && height <= 0) {
            if (chatWindow.isVisible()) {
                chatWindow.hide();
                restoreChat = true;
            }

            if (videoChatWindow.isVisible()) {
                videoChatWindow.hide();
                restoreVideoChat = true;
            }
        } else {
            if (restoreChat) {
                restoreChat = false;

                if (Chati.CHATI.getScreen().equals(Chati.CHATI.getWorldScreen())) {
                    chatWindow.show();
                }
            }

            if (restoreVideoChat) {
                restoreVideoChat = false;

                if (Chati.CHATI.getScreen().equals(Chati.CHATI.getWorldScreen())) {
                    videoChatWindow.show();
                }
            }
        }

        invalidate();
    }

    /**
     * Öffnet das Benutzermenü.
     */
    public void openUserMenu() {
        closeCurrentMenu();
        userListButton.setChecked(true);
        currentMenuWindow = new UserListWindow();
        currentMenuWindow.open();
    }

    /**
     * Öffnet das Benachrichtigungsmenü.
     */
    public void openNotificationMenu() {
        closeCurrentMenu();
        notificationListButton.setChecked(true);
        currentMenuWindow = new NotificationListWindow();
        currentMenuWindow.open();
    }

    /**
     * Öffnet das Einstellungsmenü.
     */
    public void openSettingsMenu() {
        closeCurrentMenu();
        settingsButton.setChecked(true);
        currentMenuWindow = new SettingsWindow();
        currentMenuWindow.open();
    }

    /**
     * Schließt das Benutzer-, Benachrichtigungs- oder Einstellungsmenü, sofern eines von diesen geöffnet ist.
     */
    public void closeCurrentMenu() {
        if (currentMenuWindow != null) {
            currentMenuWindow.close();
            removeCurrentMenu();
        }
    }

    /**
     * Setzt die entsprechenden Icons zur Anzeige der geschlossenen Menüs.
     */
    public void removeCurrentMenu() {
        userListButton.setChecked(false);
        notificationListButton.setChecked(false);
        settingsButton.setChecked(false);
        currentMenuWindow = null;
    }

    /**
     * Öffnet das Kommunikationsfenster.
     */
    public void openCommunicationWindow() {
        communicationButton.setChecked(true);
        communicationWindow = new CommunicationWindow();
        communicationWindow.open();
    }

    /**
     * Schließt das Kommunikationsfenster, sofern es geöffnet ist.
     */
    public void closeCommunicationWindow() {
        if (communicationWindow != null) {
            communicationWindow.close();
            removeCommunicationWindow();
        }
    }

    /**
     * Setzt das entsprechende Icon zur Anzeige eines geschlossenen Kommunikationsfensters.
     */
    public void removeCommunicationWindow() {
        communicationButton.setChecked(false);
        communicationWindow = null;
    }

    /**
     * Schaltet den Ton ein oder aus.
     */
    public void toggleSound() {
        soundButton.setChecked(!soundButton.isChecked());
        Chati.CHATI.getPreferences().setSoundOn(soundButton.isChecked());
    }

    /**
     * Schaltet das Mikrofon ein oder aus.
     */
    public void toggleMicrophone() {
        microphoneButton.setChecked(!microphoneButton.isChecked());
        Chati.CHATI.getPreferences().setMicrophoneOn(microphoneButton.isChecked());
    }

    /**
     * Schaltet die Kamera ein oder aus.
     */
    public void toggleCamera() {
        cameraButton.setChecked(!cameraButton.isChecked());
        Chati.CHATI.getPreferences().setCameraOn(cameraButton.isChecked());
    }

    /**
     * Zeigt das Chatfenster an.
     */
    public void showChatWindow() {
        if (isChatOpen()) {
            return;
        }
        chatButton.stopBlinking();
        chatButton.setChecked(true);
        chatWindow.show();
    }

    /**
     * Zeigt das Chatfenster mit einem Text im Nachrichtenfeld an.
     * @param text Anzuzeigender Text im Nachrichtenfeld.
     */
    public void showChatWindow(String text) {
        showChatWindow();
        chatWindow.setText(text);
    }

    /**
     * Zeigt das Chatfenster nicht mehr an.
     */
    public void hideChatWindow() {
        if (!isChatOpen()) {
            return;
        }
        chatButton.stopBlinking();
        chatButton.setChecked(false);
        chatWindow.hide();
    }

    /**
     * Zeigt das Videochat-Fenster an.
     */
    public void showVideoChatWindow() {
        if (isVideoChatOpen()) {
            return;
        }
        videoChatButton.stopBlinking();
        videoChatButton.setChecked(true);
        videoChatWindow.show();
    }

    /**
     * Zeigt das Videochat-Fenster nichtmehr an.
     */
    public void hideVideoChatWindow() {
        if (!isVideoChatOpen()) {
            return;
        }
        videoChatButton.stopBlinking();
        videoChatButton.setChecked(false);
        videoChatWindow.hide();
    }

    /**
     * Zeigt einen tippenden Benutzer im Chatfenster an.
     * @param userId ID des tippenden Benutzers.
     */
    public void showTypingUser(@NotNull final UUID userId) {
        chatWindow.addTypingUser(userId);
    }

    /**
     * Zeigt eine Nachricht im Chatfenster an.
     * @param senderId ID des Senders.
     * @param timestamp Zeitstempel der Nachricht.
     * @param messageType Typ der Nachricht.
     * @param message Anzuzeigende Nachricht.
     * @param imageData Daten des Bildanhangs.
     * @param imageName Name des Bildanhangs.
     * @throws UserNotFoundException falls kein Benutzer mit der ID gefunden wurde.
     */
    public void showChatMessage(@NotNull final UUID senderId, @NotNull final LocalDateTime timestamp,
                                @NotNull final MessageType messageType, @NotNull final String message,
                                final byte[] imageData, @Nullable final String imageName) throws UserNotFoundException {
        chatWindow.showUserMessage(senderId, timestamp, messageType, message, imageData, imageName);
        if (!isChatOpen()) {
            chatButton.startBlinking();
        }
        IInternUserView internUser = Chati.CHATI.getInternUser();
        if (internUser != null && !internUser.getUserId().equals(senderId)) {
            Chati.CHATI.getMultimediaManager().playSound("chat_message_sound");
        }
    }

    /**
     * Zeigt eine Informationsnachricht im Chatfenster an.
     * @param timestamp Zeitstempel der Nachricht.
     * @param messageBundle Übersetzbare Nachricht mit deren benötigten Argumenten.
     */
    public void showInfoMessage(@NotNull final LocalDateTime timestamp, @NotNull final MessageBundle messageBundle) {
        chatWindow.showInfoMessage(timestamp, messageBundle);
        if (!isChatOpen()) {
            chatButton.startBlinking();
        }
        Chati.CHATI.getMultimediaManager().playSound("chat_message_sound");
    }

    /**
     * Gibt zurück, ob das Benutzer-, Benachrichtigungs- oder Einstellungsmenü geöffnet ist.
     * @return true, wenn das Benutzer-, Benachrichtigungs- oder Einstellungsmenü geöffnet ist, sonst false.
     */
    public boolean isMenuOpen() {
        return currentMenuWindow != null;
    }

    /**
     * Gibt zurück, ob das Benutzermenü geöffnet ist.
     * @return true, wenn das Benutzermenü geöffnet ist, sonst false.
     */
    public boolean isUserMenuOpen() {
        return isMenuOpen() && userListButton.isChecked();
    }

    /**
     * Gibt zurück, ob das Benachrichtigungsmenü geöffnet ist.
     * @return true, wenn das Benachrichtigungsmenü geöffnet ist, sonst false.
     */
    public boolean isNotificationMenuOpen() {
        return isMenuOpen() && notificationListButton.isChecked();
    }

    /**
     * Gibt zurück, ob das Einstellungsmenü geöffnet ist.
     * @return true, wenn das Einstellungsmenü geöffnet ist, sonst false.
     */
    public boolean isSettingsMenuOpen() {
        return isMenuOpen() && settingsButton.isChecked();
    }

    /**
     * Gibt zurück, ob das Kommunikationsmenü geöffnet ist.
     * @return true, wenn das Kommunikationsmenü geöffnet ist, sonst false.
     */
    public boolean isCommunicationWindowOpen() {
        return communicationWindow != null && communicationButton.isChecked();
    }

    /**
     * Gibt zurück, ob das Chatfenster sichtbar ist.
     * @return true, wenn das Chatfenster sichtbar ist, sonst false.
     */
    public boolean isChatOpen() {
        return chatButton.isVisible() && chatWindow.isVisible() || restoreChat;
    }

    /**
     * Gibt zurück, ob das Videochat-Fenster sichtbar ist.
     * @return true, wenn das Videochat-Fenster sichtbar ist, sonst false.
     */
    public boolean isVideoChatOpen() {
        return videoChatButton.isVisible() && videoChatWindow.isVisible() || restoreVideoChat;
    }

    /**
     * Gibt das Chatfenster zurück.
     * @return Chatfenster.
     */
    public @NotNull ChatWindow getChatWindow() {
        return chatWindow;
    }

    /**
     * Gibt das Videochat-Fenster zurück.
     * @return Videochat-Fenster.
     */
    public @NotNull VideoChatWindow getVideoChatWindow() {
        return videoChatWindow;
    }

    @Override
    public void translate() {
        chatWindow.translate();
        if (communicationWindow != null) {
            communicationWindow.translate();
        }
        if (currentMenuWindow != null) {
            currentMenuWindow.translate();
        }
    }

    /**
     * Eine Klasse, welche den Button zum Öffnen des Chats repräsentiert.
     */
    private static class ChatButton extends ChatiImageButton {

        private static final float BLINKING_FREQUENCY = 2; // Pro Sekunde

        private boolean blinking;
        private boolean visible;
        private float delta;

        /**
         * Erzeugt eine neue Instanz des ChatButton.
         * @param imageUnchecked Im nicht ausgewählten und deaktivierten Zustand anzuzeigendes Bild.
         * @param imageChecked Im ausgewählten Zustand anzuzeigendes Bild.
         */
        public ChatButton(@NotNull final Drawable imageUnchecked, @NotNull final Drawable imageChecked) {
            super(imageUnchecked, imageChecked);
        }

        @Override
        public void draw(@NotNull final Batch batch, final float parentAlpha) {
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

        /**
         * Starte das Blinken des Icons.
         */
        public void startBlinking() {
            blinking = true;
        }

        /**
         * Stoppe das Blinken des Icons.
         */
        public void stopBlinking() {
            blinking = false;
        }
    }

    /**
     * Eine Klasse, welche den Button zum Öffnen des Benachrichtigungsmenüs repräsentiert.
     */
    private static class NotificationButton extends ChatiImageButton {

        private static final float DURATION = 2; // In Sekunden
        private static final float ANGLE = 15; // In Grad
        private boolean isAnimating;

        /**
         * Erzeugt eine neue Instanz des NotificationButton.
         * @param imageUnchecked Im nicht ausgewählten und deaktivierten Zustand anzuzeigendes Bild.
         * @param imageChecked Im ausgewählten Zustand anzuzeigendes Bild.
         */
        public NotificationButton(@NotNull final Drawable imageUnchecked, @NotNull final Drawable imageChecked) {
            super(imageUnchecked, imageChecked);
        }

        /**
         * Beginne die Animation zum Signalisieren des Erhalts einer neuen Benachrichtigung.
         */
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
                        public boolean act(final float delta) {
                            isAnimating = false;
                            return true;
                        }
                    }));
        }
    }
}
