package view2.userInterface.hud;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import controller.network.ServerSender;
import model.MessageBundle;
import model.communication.message.MessageType;
import model.exception.UserNotFoundException;
import model.user.IInternUserView;
import model.user.IUserView;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import view2.Chati;
import view2.ChatiEmojiManager;
import view2.ChatiLocalization.Translatable;
import view2.KeyCommand;
import view2.userInterface.ChatiImageButton;
import view2.userInterface.ChatiTextArea;
import view2.userInterface.ChatiTextButton;
import view2.userInterface.ChatiTooltip;
import view2.world.component.InternUserAvatar;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Eine Klasse, welche das Chatfenster der Anwendung repräsentiert.
 */
public class ChatWindow extends Window implements Translatable {

    private static final int MAX_CHAT_MESSAGES = 256;
    private static final long SHOW_TYPING_DURATION = 1; // in Sekunden

    private static final float DEFAULT_WIDTH = 700;
    private static final float DEFAULT_HEIGHT = 400;
    private static final float MINIMUM_WIDTH = 450;
    private static final float MINIMUM_HEIGHT = 200;
    private static final float SPACE = 5;
    private static final float SEND_BUTTON_WIDTH = 120;
    private static final float SEND_BUTTON_HEIGHT = 60;
    private static final float MAX_IMAGE_SIZE = 180;
    private static final float IMAGE_SCALE_FACTOR = 0.025f;

    private final List<ChatMessage> chatMessages;
    private final Map<IUserView, Long> typingUsers;
    private final Stack chatStack;
    private final Table messageContainer;
    private final ScrollPane historyScrollPane;
    private final ScrollPane emojiScrollPane;
    private final Table emojiScrollPaneContainer;
    private final ChatiTextArea typeMessageArea;
    private final ChatiTextButton sendButton;
    private final Label typingUsersLabel;
    private final Table attachedImageLabelContainer;

    private long lastTimeTypingSent;
    private FileHandle attachedImage;

    /**
     * Erzeugt eine neue Instanz des ChatWindow.
     */
    public ChatWindow() {
        super(Chati.CHATI.getLocalization().translate("window.title.chat"), Chati.CHATI.getSkin());
        this.chatMessages = new LinkedList<>();
        this.typingUsers = new HashMap<>();

        messageContainer = new Table();
        historyScrollPane = new ScrollPane(messageContainer, Chati.CHATI.getSkin());
        historyScrollPane.setOverscroll(false, false);
        historyScrollPane.setScrollingDisabled(true, false);

        HorizontalGroup emojiContainer = new HorizontalGroup().wrap().rowAlign(Align.left).padLeft(SPACE);
        Chati.CHATI.getEmojiManager().getEmojis().values().forEach(emoji -> {
            ChatiImageButton emojiButton = new ChatiImageButton(new TextureRegionDrawable(emoji.getRegion()));
            emojiButton.addListener(new ClickListener() {
                @Override
                public void clicked(@NotNull final InputEvent event, final float x, final float y) {
                    typeMessageArea.appendText(emoji.toString());
                    InputEvent typingEvent = new InputEvent();
                    typingEvent.setType(InputEvent.Type.keyTyped);
                    typeMessageArea.fire(typingEvent);
                }
            });
            emojiContainer.addActor(emojiButton);
        });
        emojiScrollPane = new ScrollPane(emojiContainer, Chati.CHATI.getSkin());
        emojiScrollPane.setFadeScrollBars(false);
        emojiScrollPane.setOverscroll(false, false);
        emojiScrollPane.setScrollingDisabled(true, false);

        ChatiImageButton emojiButton = new ChatiImageButton(new TextureRegionDrawable(Chati.CHATI.getRegions("emoji").get(1)));
        emojiButton.getImage().setScale(ChatiEmojiManager.EMOJI_SCALE);
        emojiButton.addListener(new ChatiTooltip("hud.tooltip.emoji"));
        emojiButton.addListener(new ClickListener() {
            @Override
            public void clicked(@NotNull final InputEvent event, final float x, final float y) {
                if (emojiScrollPaneContainer.hasParent()) {
                    closeEmojiMenu();
                } else {
                    openEmojiMenu();
                }
            }
        });

        ChatiImageButton attachmentButton = new ChatiImageButton(Chati.CHATI.getDrawable("attachment"));
        attachmentButton.addListener(new ChatiTooltip("hud.tooltip.attachment"));
        attachmentButton.addListener(new ClickListener() {
            @Override
            public void clicked(@NotNull final InputEvent event, final float x, final float y) {
                new ImageFileChooserWindow().open();
            }
        });

        typingUsersLabel = new Label("", Chati.CHATI.getSkin());
        typingUsersLabel.setFontScale(0.67f);

        attachedImageLabelContainer = new Table();

        typeMessageArea = new ChatiTextArea("menu.text-field.message", false);
        typeMessageArea.addListener(new InputListener() {
            @Override
            public boolean keyDown(@NotNull final InputEvent event, final int keycode) {
                return KeyCommand.SEND_CHAT_MESSAGE.matches(keycode);
            }
            @Override
            public boolean keyUp(@NotNull final InputEvent event, final int keycode) {
                if (KeyCommand.SEND_CHAT_MESSAGE.matches(keycode)) {
                    sendMessage();
                    return true;
                }
                return false;
            }
            @Override
            public boolean keyTyped(@NotNull final InputEvent event, final char c) {
                long now = System.currentTimeMillis();
                if (c != 0 && now - lastTimeTypingSent >= 1000 * SHOW_TYPING_DURATION / 2) {
                    lastTimeTypingSent = now;
                    Chati.CHATI.send(ServerSender.SendAction.TYPING);
                }
                setSendButtonState();
                return true;
            }
        });

        sendButton = new ChatiTextButton("menu.button.send", true);
        sendButton.addListener(new ClickListener() {
            @Override
            public void clicked(@NotNull final InputEvent event, final float x, final float y) {
                sendMessage();
            }
        });

        TextButton closeButton = new TextButton("X", Chati.CHATI.getSkin());
        closeButton.setDisabled(true);
        closeButton.addListener(new ChatiTooltip("hud.tooltip.close"));
        closeButton.addListener(new ClickListener() {
            @Override
            public void clicked(@NotNull final InputEvent event, final float x, final float y) {
                Chati.CHATI.getHeadUpDisplay().hideChatWindow();
            }
        });

        // Layout
        setVisible(false);
        setModal(false);
        setMovable(true);
        setResizable(true);
        setResizeBorder((int) getPadBottom());
        setKeepWithinStage(true);
        setPosition(Gdx.graphics.getWidth(), 0);
        setWidth(DEFAULT_WIDTH);
        setHeight(DEFAULT_HEIGHT);

        messageContainer.top();
        messageContainer.defaults().top().left().padLeft(SPACE).padBottom(SPACE).growX();
        emojiScrollPaneContainer = new Table();
        emojiScrollPaneContainer.add(emojiScrollPane).height(MINIMUM_HEIGHT - 4 * SPACE).bottom().pad(2 * SPACE).growX();
        emojiScrollPaneContainer.bottom();
        chatStack = new Stack();
        chatStack.add(historyScrollPane);
        add(chatStack).top().minWidth(MINIMUM_WIDTH).minHeight(MINIMUM_HEIGHT).grow().row();
        Table labelContainer = new Table();
        typingUsersLabel.setAlignment(Align.left, Align.left);
        labelContainer.add(typingUsersLabel).left().padLeft(SPACE).growX();
        labelContainer.add(attachedImageLabelContainer).right().padRight(SPACE).growX();
        add(labelContainer).padTop(-1.5f * SPACE).growX().row();
        Table sendContainer = new Table();
        sendContainer.defaults().height(SEND_BUTTON_HEIGHT).padLeft(SPACE / 2).padRight(SPACE / 2);
        Table buttonContainer = new Table();
        buttonContainer.defaults().width(SEND_BUTTON_HEIGHT / 2).height(SEND_BUTTON_HEIGHT / 2);
        emojiButton.getImage().setOrigin(SEND_BUTTON_HEIGHT / 4, SEND_BUTTON_HEIGHT / 4);
        buttonContainer.add(emojiButton).row();
        attachmentButton.getImage().setOrigin(SEND_BUTTON_HEIGHT / 4, SEND_BUTTON_HEIGHT / 4);
        buttonContainer.add(attachmentButton);
        sendContainer.add(buttonContainer).width(SEND_BUTTON_HEIGHT / 2).padLeft(SPACE);
        sendContainer.add(typeMessageArea).growX();
        sendContainer.add(sendButton).width(SEND_BUTTON_WIDTH).padRight(SPACE);
        add(sendContainer).growX();

        getTitleTable().add(closeButton).right().width(getPadTop() * (2f/3f)).height(getPadTop() * (2f/3f));
    }

    @Override
    public void act(final float delta) {
        long now = System.currentTimeMillis();
        typingUsers.values().removeIf(lastTypingTime -> now - lastTypingTime >= 1000 * SHOW_TYPING_DURATION);
        showTypingUsers();
        super.act(delta);
    }

    /**
     * Fügt einen Benutzer zu der Menge an Benutzern hinzu, die gerade als tippend angezeigt werden sollen.
     * @param userId ID des hinzuzufügenden Benutzers.
     */
    public void addTypingUser(@NotNull final UUID userId) {
        IUserView user;
        try {
            user = Chati.CHATI.getUserManager().getExternUserView(userId);
        } catch (UserNotFoundException e) {
            throw new IllegalArgumentException("Received typing information from an unknown user", e);
        }
        typingUsers.put(user, System.currentTimeMillis());
        showTypingUsers();
    }

    /**
     * Fügt einen Bildanhang hinzu, der mit der nächsten Nachricht gesendet wird.
     * @param image Angehangenes Bild.
     */
    public void attachImage(@NotNull final FileHandle image) {
        Label attachedImageLabel = new Label(image.name(), Chati.CHATI.getSkin());
        attachedImageLabel.setAlignment(Align.right, Align.right);
        attachedImageLabel.setFontScale(0.67f);
        TextButton removeAttachedImageButton = new TextButton("X", Chati.CHATI.getSkin());
        removeAttachedImageButton.addListener(new ClickListener() {
            @Override
            public void clicked(@NotNull final InputEvent event, final float x, final float y) {
                removeAttachedImage();
            }
        });
        removeAttachedImageButton.getLabel().setFontScale(0.67f);
        attachedImageLabelContainer.clear();
        attachedImageLabelContainer.add(attachedImageLabel).right().padRight(SPACE).growX();
        attachedImageLabelContainer.add(removeAttachedImageButton).size(4 * SPACE).right();
        this.attachedImage = image;
        setSendButtonState();
    }

    /**
     * Zeigt eine von einem anderen Benutzer erhalten Nachricht an.
     * @param senderId ID des Senders.
     * @param timestamp Zeitstempel der Nachricht.
     * @param messageType Typ der Nachricht.
     * @param userMessage Anzuzeigende Nachricht.
     * @param imageData Daten des Bildanhangs.
     * @param imageName Name des Bildanhangs.
     * @throws UserNotFoundException falls kein Benutzer mit der ID gefunden wurde.
     */
    public void showUserMessage(@NotNull final UUID senderId, @NotNull final LocalDateTime timestamp,
                                @NotNull final MessageType messageType, @NotNull final String userMessage,
                                final byte[] imageData, @Nullable final String imageName) throws UserNotFoundException {
        String username;
        IInternUserView internUser = Chati.CHATI.getInternUser();
        if (internUser != null && internUser.getUserId().equals(senderId)) {
            username = internUser.getUsername();
        } else {
            username = Chati.CHATI.getUserManager().getExternUserView(senderId).getUsername();
        }
        Color messageColor;
        switch (messageType) {
            case STANDARD:
                messageColor = Color.WHITE;
                break;
            case WHISPER:
                messageColor = Color.GRAY;
                break;
            case ROOM:
                messageColor = Color.ORANGE;
                break;
            case WORLD:
                messageColor = Color.SKY;
                break;
            case INFO:
                throw new IllegalArgumentException("Users cannot send info messages.");
            default:
                throw new IllegalArgumentException("No valid message type.");
        }
        ChatMessage chatMessage = new ChatMessage(timestamp, Chati.CHATI.getLocalization().format("pattern.chat.message",
                username, userMessage), messageColor);
        if (imageData.length != 0 && imageName != null) {
            chatMessage.setImage(imageName, imageData);
        }
        showChatMessage(chatMessage);
    }

    /**
     * Zeigt eine Info-Nachricht an.
     * @param timestamp Zeitstempel der Nachricht.
     * @param messageBundle Übersetzbare Nachricht zusammen mit ihren benötigten Argumenten.
     */
    public void showInfoMessage(@NotNull final LocalDateTime timestamp, @NotNull final MessageBundle messageBundle) {
        String message = Chati.CHATI.getLocalization().format(messageBundle.getMessageKey(), messageBundle.getArguments());
        ChatMessage chatMessage = new ChatMessage(timestamp, Chati.CHATI.getLocalization().format("pattern.chat.info",
                message), Color.RED);
        showChatMessage(chatMessage);
    }

    /**
     * Leert den Nachrichtenverlauf und das Nachrichtenfeld.
     */
    public void clearChat() {
        chatMessages.clear();
        typingUsers.clear();
        typeMessageArea.reset();
        messageContainer.clearChildren();
        removeAttachedImage();
        setSendButtonState();
    }

    /**
     * Zeige das Chatfenster an und fokussiere es.
     */
    public void show() {
        setVisible(true);
        historyScrollPane.layout();
        historyScrollPane.scrollTo(0, 0, 0, 0);

        InternUserAvatar internUserAvatar = Chati.CHATI.getWorldScreen().getInternUserAvatar();
        if (internUserAvatar != null && !internUserAvatar.isMoving()) {
            Chati.CHATI.getScreen().getStage().setKeyboardFocus(typeMessageArea);
            Chati.CHATI.getScreen().getStage().setScrollFocus(historyScrollPane);
        }
    }

    /**
     * Zeige das Chatfenster nicht an und entferne den Fokus.
     */
    public void hide() {
        closeEmojiMenu();
        setVisible(false);
        if (getStage() != null) {
            getStage().unfocus(this);
        }
    }

    /**
     * Setzt den Text im Textfeld.
     * @param text Anzuzeigender Text.
     */
    public void setText(String text) {
        typeMessageArea.setText("");
        typeMessageArea.setText(text);
        if (text.length() > 0 && Character.isWhitespace(text.charAt(text.length() - 1))) {
            typeMessageArea.appendText(" ");
        }
        typeMessageArea.setCursorPosition(typeMessageArea.getText().length());
        if (getStage() != null) {
            getStage().setKeyboardFocus(typeMessageArea);
        }
    }

    /**
     * Sendet eine eingegebene Nachricht.
     */
    private void sendMessage() {
        if (typeMessageArea.isBlank() && attachedImage == null) {
            return;
        }
        if (attachedImage != null) {
            Chati.CHATI.send(ServerSender.SendAction.MESSAGE, typeMessageArea.getText().trim(),
                    attachedImage.name(), attachedImage.readBytes());
            removeAttachedImage();
        } else {
            Chati.CHATI.send(ServerSender.SendAction.MESSAGE, typeMessageArea.getText().trim());
        }
        typeMessageArea.setText("");
        typeMessageArea.translate();
        closeEmojiMenu();
        setSendButtonState();
    }

    /**
     * Zeigt eine erhaltene Chatnachricht an.
     * @param chatMessage Anzuzeigende Chatnachricht.
     */
    private void showChatMessage(@NotNull final ChatMessage chatMessage) {
        boolean isAtBottomBefore = historyScrollPane.isBottomEdge();
        if (chatMessages.size() >= MAX_CHAT_MESSAGES) {
            chatMessages.remove(0);
            messageContainer.removeActorAt(0, false);
        }
        chatMessages.add(chatMessage);
        messageContainer.add(chatMessage).row();
        // Scrolle beim Erhalten einer neuen Nachricht nur bis nach unten, wenn die Scrollleiste bereits ganz unten war.
        // Ansonsten wird ein Durchscrollen des Verlaufs durch das Erhalten neuer Nachrichten gestört.
        if (isAtBottomBefore) {
            historyScrollPane.layout();
            historyScrollPane.scrollTo(0, 0, 0, 0);
        }
    }

    /**
     * Zeige die momentan tippenden Benutzer an.
     */
    private void showTypingUsers() {
        if (typingUsers.isEmpty()) {
            typingUsersLabel.setText("");
        } else if (typingUsers.size() == 1) {
            try {
                typingUsersLabel.setText(Chati.CHATI.getLocalization().format("window.chat.typing-single",
                        typingUsers.keySet().iterator().next().getUsername()));
            } catch (NoSuchElementException e) {
                // Sollte niemals eintreffen.
                e.printStackTrace();
            }
        } else {
            typingUsersLabel.setText(Chati.CHATI.getLocalization().format("window.chat.typing-multiple", typingUsers.size()));
        }
    }

    /**
     * Entfernt den Anhang.
     */
    private void removeAttachedImage() {
        attachedImageLabelContainer.clear();
        if (attachedImage == null) {
            return;
        }
        attachedImage = null;
        setSendButtonState();
    }

    /**
     * Legt fest, ob die Sendetaste als aktiviert oder deaktiviert angezeigt werden soll.
     */
    private void setSendButtonState() {
        if (typeMessageArea.isBlank() && attachedImage == null && sendButton.isTouchable()) {
            sendButton.getLabel().setColor(Color.GRAY);
            sendButton.setTouchable(Touchable.disabled);
        } else if ((!typeMessageArea.isBlank() || attachedImage != null) && !sendButton.isTouchable()) {
            sendButton.getLabel().setColor(Color.WHITE);
            sendButton.setTouchable(Touchable.enabled);
        }
    }

    /**
     * Öffnet das Menü zur Auswahl eines Emojis.
     */
    private void openEmojiMenu() {
        chatStack.add(emojiScrollPaneContainer);
        if (emojiScrollPane.getStage() != null) {
            emojiScrollPane.getStage().setScrollFocus(emojiScrollPane);
        }
        emojiScrollPane.setScrollPercentY(0);
    }

    /**
     * Schließt das Menü zur Auswahl eines Emojis.
     */
    private void closeEmojiMenu() {
        emojiScrollPaneContainer.remove();
        if (historyScrollPane.getStage() != null) {
            historyScrollPane.getStage().setScrollFocus(historyScrollPane);
        }
    }

    @Override
    public void translate() {
        getTitleLabel().setText(Chati.CHATI.getLocalization().translate("window.title.chat"));
        chatMessages.forEach(ChatMessage::translate);
        typeMessageArea.translate();
        sendButton.translate();
    }

    /**
     * Eine Klasse, welche erhaltene Chatnachrichten repräsentiert.
     */
    private static class ChatMessage extends Table implements Translatable {

        private static final String URL_REGEX = "(((http|https)://)?(www.)|((http|https)://)((www.)?)|((http|https)://www.))" +
                "[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
        private static final Color WEB_LINK_COLOR = Color.ROYAL;

        private final LocalDateTime timestamp;
        private final String message;
        private final Color messageColor;
        private final Label messageLabel;

        /**
         * Erzeugt eine neue Instanz einer ChatMessage.
         * @param timestamp Zeitstempel der Nachricht.
         * @param message Anzuzeigende Textnachricht.
         * @param messageColor Farbe, in die der Text angezeigt werden soll.
         */
        public ChatMessage(@NotNull final LocalDateTime timestamp, @NotNull final String message,
                           @NotNull final Color messageColor) {
            this.timestamp = timestamp;
            this.message = message;
            this.messageColor = messageColor;

            defaults().left().growX();

            String datedMessage = Chati.CHATI.getLocalization().format("pattern.chat.time",
                    Timestamp.valueOf(timestamp), message);
            String markedUpMessage = getMarkedUpMessage(datedMessage, messageColor);
            messageLabel = new Label(markedUpMessage, Chati.CHATI.getSkin());
            messageLabel.setWrap(true);
            messageLabel.addListener(new WeblinkClickListener(messageLabel, WEB_LINK_COLOR));

            add(messageLabel).row();
        }

        /**
         * Fügt der Textnachricht ein anzuzeigendes Bild hinzu.
         * @param imageName Name des anzuzeigenden Bildes.
         * @param imageData Daten des anzuzeigenden Bildes.
         */
        public void setImage(@NotNull final String imageName, final byte[] imageData) {
            Pixmap image;
            try {
                image = new Pixmap(imageData, 0, imageData.length);
            } catch (GdxRuntimeException e) {
                Logger.getLogger("chati.view").log(Level.WARNING, "Could not show received image", e);
                return;
            }

            Texture imageTexture = new Texture(image);
            Drawable imageDrawable = new TextureRegionDrawable(imageTexture);
            image.dispose();

            Label imageNameLabel = new Label(imageName, Chati.CHATI.getSkin());
            imageNameLabel.setAlignment(Align.center, Align.center);
            imageNameLabel.setFontScale(0.67f);

            ChatiImageButton imageButton = new ChatiImageButton(imageDrawable, imageDrawable, imageDrawable, IMAGE_SCALE_FACTOR);
            imageButton.addListener(new ClickListener() {
                @Override
                public void enter(@NotNull final InputEvent event, final float x, final float y, final int pointer,
                                  @Nullable final Actor fromActor) {
                    if (pointer == -1) {
                        imageNameLabel.setColor(Color.MAGENTA);
                    }
                }
                @Override
                public void exit(@NotNull final InputEvent event, final float x, final float y, final int pointer,
                                 @Nullable final Actor fromActor) {
                    if (pointer == -1) {
                        imageNameLabel.setColor(Color.WHITE);
                    }
                }
                @Override
                public void clicked(@NotNull final InputEvent event, final float x, final float y) {
                    new ImageWindow(imageName, imageData).open();
                }
            });

            Table imageContainer = new Table();
            imageContainer.left();
            float ratio = 1;
            float largerSide = Math.max(image.getWidth(), image.getHeight());
            if (largerSide > MAX_IMAGE_SIZE) {
                ratio = MAX_IMAGE_SIZE / largerSide;
            }
            float imageWidth = ratio * image.getWidth();
            float imageHeight = ratio * image.getHeight();
            imageButton.getImage().setOrigin(imageWidth / 2f, imageHeight / 2f);
            imageContainer.add(imageButton).width(imageWidth).height(imageHeight).row();
            imageContainer.add(imageNameLabel);

            add(imageContainer);
        }

        @Override
        public void translate() {
            String datedMessage = Chati.CHATI.getLocalization().format("pattern.chat.time", Timestamp.valueOf(timestamp), message);
            String colorizedMessage = getMarkedUpMessage(datedMessage, messageColor);
            messageLabel.setText(colorizedMessage);
        }

        /**
         * Markiere die Nachricht mithilfe der Color-Markup-Language. Glyphen von Emojis bleiben hierbei weiß, um korrekt
         * dargestellt zu werden. Weblinks werden unabhängig von der verwendeten Farbe in die dafür vorgesehene Farbe
         * eingefärbt.
         * @param message Nachricht, die eingefärbt werden soll.
         * @param messageColor Farbe, in die die Nachricht eingefärbt werden soll.
         * @return Eingefärbte Nachricht.
         */
        private String getMarkedUpMessage(@NotNull final String message, @NotNull final Color messageColor) {
            Matcher urlMatcher = Pattern.compile(URL_REGEX).matcher(message);
            boolean hasWeblink = urlMatcher.find();

            if (messageColor == Color.WHITE && !hasWeblink) {
                return message;
            }

            StringBuilder stringBuilder = new StringBuilder();
            Color currentColor = messageColor == Color.WHITE ? messageColor : null;
            for (int i = 0; i < message.length(); i++) {
                char c = message.charAt(i);
                boolean isEmoji = Chati.CHATI.getEmojiManager().isEmoji(c);
                if (hasWeblink && i > urlMatcher.end()) {
                    hasWeblink = urlMatcher.find();
                }
                boolean isWeblink = hasWeblink && i >= urlMatcher.start() && i < urlMatcher.end();

                if (isEmoji && currentColor != Color.WHITE) {
                    currentColor = Color.WHITE;
                    stringBuilder.append("[#").append(currentColor).append("]");
                } else if (!isEmoji && isWeblink && currentColor != WEB_LINK_COLOR) {
                    currentColor = WEB_LINK_COLOR;
                    stringBuilder.append("[#").append(currentColor).append("]");
                } else if (!isEmoji && !isWeblink && hasWeblink && i == urlMatcher.end()) {
                    if (i + 1 < message.length() && Chati.CHATI.getEmojiManager().isEmoji(message.charAt(i + 1))) {
                        currentColor = Color.WHITE;
                    } else {
                        currentColor = messageColor;
                    }
                    stringBuilder.append("[#").append(currentColor).append("]");
                } else if (!isEmoji && !isWeblink && !Character.isWhitespace(c) && currentColor != messageColor) {
                    currentColor = messageColor;
                    stringBuilder.append("[#").append(currentColor).append("]");
                }
                if (c == '[') {
                    stringBuilder.append("[[");
                } else {
                    stringBuilder.append(c);
                }
            }
            return stringBuilder.toString();
        }
    }

    /**
     * Eine Klasse, welche einen ClickListener repräsentiert, der einen in einem Label enthaltenen Weblink durch einen
     * Klick öffnet.
     */
    private static class WeblinkClickListener extends ClickListener {

        private final Label label;
        private final Color webLinkColor;

        /**
         * Erzeugt eine neue Instanz des WeblinkClickListener.
         * @param label Label, in dem Weblinks durch einen Klick geöffnet werden sollen.
         * @param webLinkColor Farbe der Weblinks in dem Label.
         */
        public WeblinkClickListener(@NotNull final Label label, @NotNull final Color webLinkColor) {
            this.label = label;
            this.webLinkColor = webLinkColor;
        }

        @Override
        public void enter(@NotNull final InputEvent event, final float x, final float y, final int pointer,
                          @Nullable final Actor fromActor) {
            if (pointer == -1) {
                label.getGlyphLayout().runs.forEach(run -> {
                    if (run.color.equals(webLinkColor)) {
                        Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Hand);
                    }
                });

            }
        }

        @Override
        public void exit(@NotNull final InputEvent event, final float x, final float y, final int pointer,
                         @Nullable final Actor fromActor) {
            if (pointer == -1) {
                label.getGlyphLayout().runs.forEach(run -> {
                    if (run.color.equals(webLinkColor)) {
                        Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);
                    }
                });

            }
        }

        @Override
        public void clicked(@NotNull final InputEvent event, final float x, final float y) {
            Array<GlyphLayout.GlyphRun> runs = label.getGlyphLayout().runs;
            for (int i = 0; i < runs.size; i++) {
                float textHeight = label.getHeight() - 3;
                float lineHeight = label.getStyle().font.getLineHeight();
                if (x > runs.get(i).x && x < runs.get(i).x + runs.get(i).width && y > runs.get(i).y + textHeight
                        - lineHeight && y < runs.get(i).y + textHeight && runs.get(i).color.equals(webLinkColor)) {
                    StringBuilder urlBuilder = new StringBuilder();
                    urlBuilder.append(runs.get(i).glyphs.toString(""));
                    for (int j = i - 1; j > 0; j--) {
                        if (runs.get(j).color.equals(webLinkColor)) {
                            urlBuilder.insert(0, runs.get(j).glyphs.toString(""));
                        } else {
                            break;
                        }
                    }
                    for (int j = i + 1; j < runs.size; j++) {
                        if (runs.get(j).color.equals(webLinkColor)) {
                            urlBuilder.append(runs.get(j).glyphs.toString(""));
                        } else {
                            break;
                        }
                    }
                    if (!Gdx.net.openURI(urlBuilder.toString())) {
                        Gdx.net.openURI("https://www.google.de/search?q=" + urlBuilder);
                    }
                }
            }
        }
    }
}
